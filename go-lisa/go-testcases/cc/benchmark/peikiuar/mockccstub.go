package mocking

import (
	"container/list"
	"errors"
	"fmt"
	"strings"
	"unicode/utf8"

	"github.com/golang/protobuf/ptypes"
	"github.com/golang/protobuf/ptypes/timestamp"
	"github.com/hyperledger/fabric-chaincode-go/shim"
	"github.com/hyperledger/fabric-protos-go/ledger/queryresult"
	pb "github.com/hyperledger/fabric-protos-go/peer"
)

const (
	minUnicodeRuneValue   = 0 //U+0000
	compositeKeyNamespace = "\x00"
)

// MockChaincodeStub implements the ChaincodeStubInterface for unit testing chaincode.
type MockChaincodeStub struct {
	// arguments the stub was called with
	args [][]byte

	// A pointer back to the chaincode that will invoke this, set by constructor.
	// If a peer calls this stub, the chaincode will be invoked from here.
	cc shim.Chaincode

	// A nice name that can be used for logging
	Name string

	// State keeps name value pairs
	State map[string][]byte

	// Keys stores the list of mapped values in lexical order
	Keys *list.List

	// registered list of other MockChaincodeStub chaincodes that can be called from this MockChaincodeStub
	Invokables map[string]*MockChaincodeStub

	// stores a transaction uuid while being Invoked / Deployed
	// TODO if a chaincode uses recursion this may need to be a stack of TxIDs or possibly a reference counting map
	TxID string

	TxTimestamp *timestamp.Timestamp

	// mocked signedProposal
	signedProposal *pb.SignedProposal

	// stores a channel ID of the proposal
	ChannelID string

	PvtState map[string]map[string][]byte

	// stores per-key endorsement policy, first map index is the collection, second map index is the key
	EndorsementPolicies map[string]map[string][]byte

	// channel to store ChaincodeEvents
	ChaincodeEventsChannel chan *pb.ChaincodeEvent

	Creator []byte

	Decorations map[string][]byte

	transient map[string][]byte
}

// NewMockChaincodeStub Constructor to initialise the internal State map
func NewMockChaincodeStub(name string, cc shim.Chaincode, transient map[string][]byte) *MockChaincodeStub {
	m := new(MockChaincodeStub)
	m.Name = name
	m.cc = cc
	m.State = make(map[string][]byte)
	m.PvtState = make(map[string]map[string][]byte)
	m.EndorsementPolicies = make(map[string]map[string][]byte)
	m.Invokables = make(map[string]*MockChaincodeStub)
	m.Keys = list.New()
	m.ChaincodeEventsChannel = make(chan *pb.ChaincodeEvent, 100) //define large capacity for non-blocking setEvent calls.
	m.Decorations = make(map[string][]byte)
	m.transient = transient

	return m
}

// InvokeChaincode locally calls the specified chaincode `Invoke`.
// E.g. stub1.InvokeChaincode("othercc", funcArgs, channel)
// Before calling this make sure to create another MockChaincodeStub stub2, call shim.NewMockChaincodeStub("othercc", Chaincode)
// and register it with stub1 by calling stub1.MockPeerChaincode("othercc", stub2, channel)
func (m *MockChaincodeStub) InvokeChaincode(chaincodeName string, args [][]byte, channel string) pb.Response {
	// Internally we use chaincode name as a composite name
	if channel != "" {
		chaincodeName = chaincodeName + "/" + channel
	}
	// TODO "args" here should possibly be a serialized pb.ChaincodeInput
	otherStub := m.Invokables[chaincodeName]
	//	function, strings := getFuncArgs(args)
	res := otherStub.MockInvoke(m.TxID, args)
	return res
}

// MockInvoke Invoke this chaincode, also starts and ends a transaction.
func (m *MockChaincodeStub) MockInvoke(uuid string, args [][]byte) pb.Response {
	m.args = args
	m.MockTransactionStart(uuid)
	res := m.cc.Invoke(m)
	m.MockTransactionEnd(uuid)
	return res
}

// MockTransactionStart Used to indicate to a chaincode that it is part of a transaction.
// This is important when chaincodes invoke each other.
// MockChaincodeStub doesn't support concurrent transactions at present.
func (m *MockChaincodeStub) MockTransactionStart(txid string) {
	m.TxID = txid
	m.signedProposal = &pb.SignedProposal{}
	m.TxTimestamp = ptypes.TimestampNow()
}

// MockTransactionEnd End a mocked transaction, clearing the UUID.
func (m *MockChaincodeStub) MockTransactionEnd(uuid string) {
	m.signedProposal = nil
	m.TxID = ""
}

// CreateCompositeKey combines the list of attributes
// to form a composite key.
func (m *MockChaincodeStub) CreateCompositeKey(objectType string, attributes []string) (string, error) {
	return shim.CreateCompositeKey(objectType, attributes)
}

// SplitCompositeKey splits the composite key into attributes
// on which the composite key was formed.
func (m *MockChaincodeStub) SplitCompositeKey(compositeKey string) (string, []string, error) {
	return splitCompositeKey(compositeKey)
}

func splitCompositeKey(compositeKey string) (string, []string, error) {
	componentIndex := 1
	components := []string{}
	for i := 1; i < len(compositeKey); i++ {
		if compositeKey[i] == minUnicodeRuneValue {
			components = append(components, compositeKey[componentIndex:i])
			componentIndex = i + 1
		}
	}
	return components[0], components[1:], nil
}

// PutState writes the specified `value` and `key` into the ledger.
func (m *MockChaincodeStub) PutState(key string, value []byte) error {
	if m.TxID == "" {
		err := errors.New("cannot PutState without a transactions - call m.MockTransactionStart()?")
		return err
	}

	// If the value is nil or empty, delete the key
	if len(value) == 0 {
		return m.DelState(key)
	}
	m.State[key] = value

	// insert key into ordered list of keys
	for elem := m.Keys.Front(); elem != nil; elem = elem.Next() {
		elemValue := elem.Value.(string)
		comp := strings.Compare(key, elemValue)
		if comp < 0 {
			// key < elem, insert it before elem
			m.Keys.InsertBefore(key, elem)
			break
		} else if comp == 0 {
			// keys exists, no need to change
			break
		} else { // comp > 0
			// key > elem, keep looking unless this is the end of the list
			if elem.Next() == nil {
				m.Keys.PushBack(key)
				break
			}
		}
	}

	// special case for empty Keys list
	if m.Keys.Len() == 0 {
		m.Keys.PushFront(key)
	}

	return nil
}

// SetArgs is a function to manually set the args of a function call since this is not done automatically in the MockChaincodeStub
func (m *MockChaincodeStub) SetArgs(args [][]byte) {
	m.args = args
}

// SetTransient can be used to change the transient data between transactions while maintaining the world state
func (m *MockChaincodeStub) SetTransient(transient map[string][]byte) {
	m.transient = transient
}

// SetEvent ...
func (m *MockChaincodeStub) SetEvent(name string, payload []byte) error {
	m.ChaincodeEventsChannel <- &pb.ChaincodeEvent{EventName: name, Payload: payload}
	return nil
}

// PutPrivateData ...
func (m *MockChaincodeStub) PutPrivateData(collection string, key string, value []byte) error {
	colMap, ok := m.PvtState[collection]
	if !ok {
		m.PvtState[collection] = make(map[string][]byte)
		colMap = m.PvtState[collection]
	}

	colMap[key] = value

	return nil
}

// DelState removes the specified `key` and its value from the ledger.
func (m *MockChaincodeStub) DelState(key string) error {
	delete(m.State, key)

	for elem := m.Keys.Front(); elem != nil; elem = elem.Next() {
		if strings.Compare(key, elem.Value.(string)) == 0 {
			m.Keys.Remove(elem)
		}
	}

	return nil
}

// SetStateValidationParameter ...
func (m *MockChaincodeStub) SetStateValidationParameter(key string, ep []byte) error {
	return m.SetPrivateDataValidationParameter("", key, ep)
}

// SetPrivateDataValidationParameter ...
func (m *MockChaincodeStub) SetPrivateDataValidationParameter(collection, key string, ep []byte) error {
	colEndorsementMap, ok := m.EndorsementPolicies[collection]
	if !ok {
		m.EndorsementPolicies[collection] = make(map[string][]byte)
		colEndorsementMap = m.EndorsementPolicies[collection]
	}

	colEndorsementMap[key] = ep
	return nil
}

// GetTxID ...
func (m *MockChaincodeStub) GetTxID() string {
	return m.TxID
}

// GetArgs ...
func (m *MockChaincodeStub) GetArgs() [][]byte {
	return m.args
}

// GetTxTimestamp ...
func (m *MockChaincodeStub) GetTxTimestamp() (*timestamp.Timestamp, error) {
	if m.TxTimestamp == nil {
		return nil, errors.New("TxTimestamp not set")
	}
	return m.TxTimestamp, nil
}

// GetChannelID ...
func (m *MockChaincodeStub) GetChannelID() string {
	return m.ChannelID
}

// GetCreator ...
func (m *MockChaincodeStub) GetCreator() ([]byte, error) {
	return m.Creator, nil
}

// GetDecorations ...
func (m *MockChaincodeStub) GetDecorations() map[string][]byte {
	return m.Decorations
}

// GetFunctionAndParameters ...
func (m *MockChaincodeStub) GetFunctionAndParameters() (function string, params []string) {
	allargs := m.GetStringArgs()
	function = ""
	params = []string{}
	if len(allargs) >= 1 {
		function = allargs[0]
		params = allargs[1:]
	}
	return
}

// GetStringArgs ...
func (m *MockChaincodeStub) GetStringArgs() []string {
	args := m.GetArgs()
	strargs := make([]string, 0, len(args))
	for _, barg := range args {
		strargs = append(strargs, string(barg))
	}
	return strargs
}

// GetState retrieves the value for a given key from the ledger
func (m *MockChaincodeStub) GetState(key string) (value []byte, err error) {
	value = m.State[key]
	return
}

// GetStateByPartialCompositeKey function can be invoked by a chaincode to query the
// state based on a given partial composite key. This function returns an
// iterator which can be used to iterate over all composite keys whose prefix
// matches the given partial composite key. This function should be used only for
// a partial composite key. For a full composite key, an iter with empty response
// would be returned.
func (m *MockChaincodeStub) GetStateByPartialCompositeKey(objectType string, attributes []string) (shim.StateQueryIteratorInterface, error) {
	partialCompositeKey, err := m.CreateCompositeKey(objectType, attributes)
	if err != nil {
		return nil, err
	}
	return NewMockStateRangeQueryIterator(m, partialCompositeKey, partialCompositeKey+string(utf8.MaxRune)), nil
}

// GetStateByRange ...
func (m *MockChaincodeStub) GetStateByRange(startKey, endKey string) (shim.StateQueryIteratorInterface, error) {
	if err := validateSimpleKeys(startKey, endKey); err != nil {
		return nil, err
	}
	return NewMockStateRangeQueryIterator(m, startKey, endKey), nil
}

//To ensure that simple keys do not go into composite key namespace,
//we validate simplekey to check whether the key starts with 0x00 (which
//is the namespace for compositeKey). This helps in avoding simple/composite
//key collisions.
func validateSimpleKeys(simpleKeys ...string) error {
	for _, key := range simpleKeys {
		if len(key) > 0 && key[0] == compositeKeyNamespace[0] {
			return fmt.Errorf(`first character of the key [%s] contains a null character which is not allowed`, key)
		}
	}
	return nil
}

// GetStateValidationParameter ...
func (m *MockChaincodeStub) GetStateValidationParameter(key string) ([]byte, error) {
	return m.GetPrivateDataValidationParameter("", key)
}

// GetPrivateData ...
func (m *MockChaincodeStub) GetPrivateData(collection string, key string) ([]byte, error) {
	colMap, ok := m.PvtState[collection]

	if !ok {
		return nil, nil
	}

	return colMap[key], nil
}

// GetPrivateDataValidationParameter ...
func (m *MockChaincodeStub) GetPrivateDataValidationParameter(collection, key string) ([]byte, error) {
	colEndorsementMap, ok := m.EndorsementPolicies[collection]

	if !ok {
		return nil, nil
	}

	return colEndorsementMap[key], nil
}

// GetSignedProposal ...
func (m *MockChaincodeStub) GetSignedProposal() (*pb.SignedProposal, error) {
	return m.signedProposal, nil
}

/* TODO: Not Implemented */

// DelPrivateData ...
func (m *MockChaincodeStub) DelPrivateData(collection string, key string) error {
	return errors.New("Not Implemented")
}

// GetArgsSlice Not implemented ...
func (m *MockChaincodeStub) GetArgsSlice() ([]byte, error) {
	return nil, nil
}

// GetBinding Not implemented ...
func (m *MockChaincodeStub) GetBinding() ([]byte, error) {
	return nil, nil
}

// GetHistoryForKey function can be invoked by a chaincode to return a history of
// key values across time. GetHistoryForKey is intended to be used for read-only queries.
func (m *MockChaincodeStub) GetHistoryForKey(key string) (shim.HistoryQueryIteratorInterface, error) {
	return nil, errors.New("not implemented")
}

// GetPrivateDataByPartialCompositeKey ...
func (m *MockChaincodeStub) GetPrivateDataByPartialCompositeKey(collection, objectType string, attributes []string) (shim.StateQueryIteratorInterface, error) {
	return nil, errors.New("Not Implemented")
}

// GetPrivateDataByRange ...
func (m *MockChaincodeStub) GetPrivateDataByRange(collection, startKey, endKey string) (shim.StateQueryIteratorInterface, error) {
	return nil, errors.New("Not Implemented")
}

// GetPrivateDataHash ...
func (m *MockChaincodeStub) GetPrivateDataHash(collection, key string) ([]byte, error) {
	return nil, errors.New("Not Implemented")
}

// GetPrivateDataQueryResult ...
func (m *MockChaincodeStub) GetPrivateDataQueryResult(collection, query string) (shim.StateQueryIteratorInterface, error) {
	// Not implemented since the mock engine does not have a query engine.
	// However, a very simple query engine that supports string matching
	// could be implemented to test that the framework supports queries
	return nil, errors.New("Not Implemented")
}

// GetQueryResult function can be invoked by a chaincode to perform a
// rich query against state database.  Only supported by state database implementations
// that support rich query.  The query string is in the syntax of the underlying
// state database. An iterator is returned which can be used to iterate (next) over
// the query result set
func (m *MockChaincodeStub) GetQueryResult(query string) (shim.StateQueryIteratorInterface, error) {
	// Not implemented since the mock engine does not have a query engine.
	// However, a very simple query engine that supports string matching
	// could be implemented to test that the framework supports queries
	return nil, errors.New("not implemented")
}

// GetQueryResultWithPagination ...
func (m *MockChaincodeStub) GetQueryResultWithPagination(query string, pageSize int32,
	bookmark string) (shim.StateQueryIteratorInterface, *pb.QueryResponseMetadata, error) {
	return nil, nil, nil
}

// GetStateByPartialCompositeKeyWithPagination ...
func (m *MockChaincodeStub) GetStateByPartialCompositeKeyWithPagination(objectType string, keys []string,
	pageSize int32, bookmark string) (shim.StateQueryIteratorInterface, *pb.QueryResponseMetadata, error) {
	return nil, nil, nil
}

// GetStateByRangeWithPagination ...
func (m *MockChaincodeStub) GetStateByRangeWithPagination(startKey, endKey string, pageSize int32,
	bookmark string) (shim.StateQueryIteratorInterface, *pb.QueryResponseMetadata, error) {
	return nil, nil, nil
}

// GetTransient ...
func (m *MockChaincodeStub) GetTransient() (map[string][]byte, error) {
	return m.transient, nil
}

/*****************************
 Range Query Iterator
*****************************/

// MockStateRangeQueryIterator ...
type MockStateRangeQueryIterator struct {
	Closed   bool
	Stub     *MockChaincodeStub
	StartKey string
	EndKey   string
	Current  *list.Element
}

// HasNext returns true if the range query iterator contains additional keys
// and values.
func (iter *MockStateRangeQueryIterator) HasNext() bool {
	if iter.Closed {
		// previously called Close()
		return false
	}

	if iter.Current == nil {
		return false
	}

	current := iter.Current
	for current != nil {
		// if this is an open-ended query for all keys, return true
		if iter.StartKey == "" && iter.EndKey == "" {
			return true
		}
		comp1 := strings.Compare(current.Value.(string), iter.StartKey)
		comp2 := strings.Compare(current.Value.(string), iter.EndKey)
		if comp1 >= 0 {
			return comp2 < 0
		}
		current = current.Next()
	}
	return false
}

// Next returns the next key and value in the range query iterator.
func (iter *MockStateRangeQueryIterator) Next() (*queryresult.KV, error) {
	if iter.Closed {
		err := errors.New("MockStateRangeQueryIterator.Next() called after Close()")
		return nil, err
	}

	if !iter.HasNext() {
		err := errors.New("MockStateRangeQueryIterator.Next() called when it does not HaveNext()")
		return nil, err
	}

	for iter.Current != nil {
		comp1 := strings.Compare(iter.Current.Value.(string), iter.StartKey)
		comp2 := strings.Compare(iter.Current.Value.(string), iter.EndKey)
		// compare to start and end keys. or, if this is an open-ended query for
		// all keys, it should always return the key and value
		if (comp1 >= 0 && comp2 < 0) || (iter.StartKey == "" && iter.EndKey == "") {
			key := iter.Current.Value.(string)
			value, err := iter.Stub.GetState(key)
			iter.Current = iter.Current.Next()
			return &queryresult.KV{Key: key, Value: value}, err
		}
		iter.Current = iter.Current.Next()
	}
	err := errors.New("MockStateRangeQueryIterator.Next() went past end of range")
	return nil, err
}

// Close closes the range query iterator. This should be called when done
// reading from the iterator to free up resources.
func (iter *MockStateRangeQueryIterator) Close() error {
	if iter.Closed {
		err := errors.New("MockStateRangeQueryIterator.Close() called after Close()")
		return err
	}

	iter.Closed = true
	return nil
}

// NewMockStateRangeQueryIterator ...
func NewMockStateRangeQueryIterator(m *MockChaincodeStub, startKey string, endKey string) *MockStateRangeQueryIterator {
	iter := new(MockStateRangeQueryIterator)
	iter.Closed = false
	iter.Stub = m
	iter.StartKey = startKey
	iter.EndKey = endKey
	iter.Current = m.Keys.Front()
	return iter
}
