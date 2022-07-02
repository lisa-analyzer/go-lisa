package testing

import (
	"container/list"
	"encoding/json"
	"errors"
	"fmt"
	"log"
	"math/rand"
	"strings"
	"time"
	"unicode/utf8"

	convert2 "github.com/KompiTech/fabric-cc-core/v2/internal/testing/convert"
	"github.com/golang/protobuf/proto"
	"github.com/golang/protobuf/ptypes"
	"github.com/golang/protobuf/ptypes/timestamp"
	"github.com/hyperledger/fabric-chaincode-go/shim"
	"github.com/hyperledger/fabric-protos-go/ledger/queryresult"
	"github.com/hyperledger/fabric-protos-go/msp"
	"github.com/hyperledger/fabric-protos-go/peer"
)

var (
	// ErrChaincodeNotExists occurs when attempting to invoke a nonexostent external chaincode
	ErrChaincodeNotExists = errors.New(`chaincode not exists`)
	// ErrUnknownFromArgsType  occurs when attempting to set unknown Args in From func
	ErrUnknownFromArgsType = errors.New(`unknown Args type to cckit.MockStub.From func`)
	ErrPaginatedTmpl       = `%s failed: transaction ID: %s: txid [%s]: Transaction has already performed a paginated query. Writes are not allowed`
)

const (
	minUnicodeRuneValue    = 0 //U+0000
	compositeKeyNamespace  = "\x00"
	EventChannelBufferSize = 100
)

// MockStub is an implementation of ChaincodeStubInterface for unit testing chaincode.
// Use this instead of ChaincodeStub in your chaincode's unit test calls to Init or Invoke.
type MockStub struct {
	// arguments the stub was called with
	Args [][]byte

	// transientMap
	TransientMap map[string][]byte
	// A pointer back to the chaincode that will invoke this, set by constructor.
	// If a peer calls this stub, the chaincode will be invoked from here.
	Cc shim.Chaincode

	// A nice name that can be used for logging
	Name string

	// State keeps name value pairs
	State map[string][]byte

	// Keys stores the list of mapped values in lexical order
	Keys *list.List

	// registered list of other MockStub chaincodes that can be called from this MockStub
	Invokables map[string]*MockStub

	// stores a transaction uuid while being Invoked / Deployed
	// TODO if a chaincode uses recursion this may need to be a stack of TxIDs or possibly a reference counting map
	TxID string

	TxTimestamp *timestamp.Timestamp

	// mocked signedProposal
	signedProposal *peer.SignedProposal

	// stores a channel ID of the proposal
	ChannelID string

	PvtState map[string]map[string][]byte

	// stores per-key endorsement policy, first map index is the collection, second map index is the key
	EndorsementPolicies map[string]map[string][]byte

	// channel to store ChaincodeEvents
	ChaincodeEventsChannel chan *peer.ChaincodeEvent

	Creator []byte

	Decorations map[string][]byte

	// ==== KompiTech, stuff ported from CCKIT
	InvokablesFull              map[string]*MockStub // invokable this version of MockStub
	creatorTransformer          CreatorTransformer   // transformer for tx creator data, used in From func
	mockCreator                 []byte
	ClearCreatorAfterInvoke     bool
	ChaincodeEvent              *peer.ChaincodeEvent // event in last tx
	useSetTime                  bool                 // use time set by TravelInTime method
	isRO                        bool                 // if GetQueryResultWithPagination or GetPrivateDataQueryResult was executed, this is true
	ignoringEvents              bool                 // if true, then events are not saved in channel and it does not need to be emptied
	_args                       [][]byte
	chaincodeEventSubscriptions []chan *peer.ChaincodeEvent // multiple event subscriptions
	CouchDBMock                 *CouchDBMock
	//shimtest.MockStub
}

// NewMockStub Constructor to initialise the internal State map
func NewMockStub(name string, cc shim.Chaincode) *MockStub {
	s := new(MockStub)
	s.Name = name
	s.Cc = cc
	s.State = make(map[string][]byte)
	s.PvtState = make(map[string]map[string][]byte)
	s.EndorsementPolicies = make(map[string]map[string][]byte)
	s.Invokables = make(map[string]*MockStub)
	s.Keys = list.New()
	s.ChaincodeEventsChannel = make(chan *peer.ChaincodeEvent, 100) //define large capacity for non-blocking setEvent calls.
	s.Decorations = make(map[string][]byte)

	// Always set current mocked time to whole second to prevent overflows
	s.TxTimestamp = &timestamp.Timestamp{
		Seconds: time.Now().Unix(),
	}

	return s
}

type CreatorTransformer func(...interface{}) (mspID string, certPEM []byte, err error)

// GetTxID ...
func (stub *MockStub) GetTxID() string {
	return stub.TxID
}

// GetChannelID ...
func (stub *MockStub) GetChannelID() string {
	return stub.ChannelID
}

// GetArgs ...
func (stub *MockStub) GetArgs() [][]byte {
	return stub.Args
}

// GetStringArgs ...
func (stub *MockStub) GetStringArgs() []string {
	args := stub.GetArgs()
	strargs := make([]string, 0, len(args))
	for _, barg := range args {
		strargs = append(strargs, string(barg))
	}
	return strargs
}

// GetFunctionAndParameters ...
func (stub *MockStub) GetFunctionAndParameters() (function string, params []string) {
	allargs := stub.GetStringArgs()
	function = ""
	params = []string{}
	if len(allargs) >= 1 {
		function = allargs[0]
		params = allargs[1:]
	}
	return
}

// MockTransactionStart Used to indicate to a chaincode that it is part of a transaction.
// This is important when chaincodes invoke each other.
// MockStub doesn't support concurrent transactions at present.
func (stub *MockStub) MockTransactionStart(txid string) {
	stub.TxID = txid
	stub.setSignedProposal(&peer.SignedProposal{})

	ts, err := stub.GetTxTimestamp()
	if stub.useSetTime && err == nil {
		stub.TxTimestamp = ts
	} else {
		stub.setTxTimestamp(ptypes.TimestampNow())
	}
}

// MockTransactionEnd End a mocked transaction, clearing the UUID.
func (stub *MockStub) MockTransactionEnd(uuid string) {
	stub.signedProposal = nil
	stub.TxID = ""
}

// MockPeerChaincode Register another MockStub chaincode with this MockStub.
// invokableChaincodeName is the name of a chaincode.
// otherStub is a MockStub of the chaincode, already initialized.
// channel is the name of a channel on which another MockStub is called.
func (stub *MockStub) MockPeerChaincode(invokableChaincodeName string, otherStub *MockStub, channel string) {
	// Internally we use chaincode name as a composite name
	if channel != "" {
		invokableChaincodeName = invokableChaincodeName + "/" + channel
	}
	stub.Invokables[invokableChaincodeName] = otherStub
}

// MockInit Initialise this chaincode,  also starts and ends a transaction.
func (stub *MockStub) MockInit(uuid string, args [][]byte) peer.Response {
	stub.Args = args
	stub.MockTransactionStart(uuid)
	res := stub.Cc.Init(stub)
	stub.MockTransactionEnd(uuid)
	stub.isRO = false
	return res
}

// MockInvoke Invoke this chaincode, also starts and ends a transaction.
func (stub *MockStub) MockInvoke(uuid string, args [][]byte) peer.Response {
	stub.Args = args
	stub.ChaincodeEvent = nil
	stub.MockTransactionStart(uuid)
	res := stub.Cc.Invoke(stub)
	stub.MockTransactionEnd(uuid)
	stub.isRO = false
	return res
}

// GetDecorations ...
func (stub *MockStub) GetDecorations() map[string][]byte {
	return stub.Decorations
}

// MockInvokeWithSignedProposal Invoke this chaincode, also starts and ends a transaction.
func (stub *MockStub) MockInvokeWithSignedProposal(uuid string, args [][]byte, sp *peer.SignedProposal) peer.Response {
	stub.Args = args
	stub.MockTransactionStart(uuid)
	stub.signedProposal = sp
	res := stub.Cc.Invoke(stub)
	stub.MockTransactionEnd(uuid)
	stub.isRO = false
	return res
}

// GetPrivateData ...
func (stub *MockStub) GetPrivateData(collection string, key string) ([]byte, error) {
	collection = strings.ToLower(collection)

	m, in := stub.PvtState[collection]

	if !in {
		return nil, nil
	}

	return m[key], nil
}

// GetPrivateDataHash ...
func (stub *MockStub) GetPrivateDataHash(collection, key string) ([]byte, error) {
	return nil, errors.New("Not Implemented")
}

// PutPrivateData ...
func (stub *MockStub) PutPrivateData(collection string, key string, value []byte) error {
	collection = strings.ToLower(collection)

	if stub.isRO {
		return fmt.Errorf(ErrPaginatedTmpl, "PUT_PRIVATE_DATA", stub.TxID, stub.TxID)
	}

	m, in := stub.PvtState[collection]
	if !in {
		stub.PvtState[collection] = make(map[string][]byte)
		m, in = stub.PvtState[collection]
	}

	m[key] = value

	stub.CouchDBMock.PutPrivateData(collection, key, value)
	return nil
}

// DelPrivateData ...
func (stub *MockStub) DelPrivateData(collection string, key string) error {
	collection = strings.ToLower(collection)

	if stub.isRO {
		return fmt.Errorf(ErrPaginatedTmpl, "DEL_PRIVATE_DATA", stub.TxID, stub.TxID)
	}

	m, in := stub.PvtState[collection]
	if !in {
		return nil
	}

	delete(m, key)
	stub.PvtState[collection] = m

	stub.CouchDBMock.DelPrivateData(collection, key)
	return nil
}

// GetPrivateDataByRange ...
func (stub *MockStub) GetPrivateDataByRange(collection, startKey, endKey string) (shim.StateQueryIteratorInterface, error) {
	return nil, errors.New("Not Implemented")
}

// GetPrivateDataByPartialCompositeKey ...
func (stub *MockStub) GetPrivateDataByPartialCompositeKey(collection, objectType string, attributes []string) (shim.StateQueryIteratorInterface, error) {
	return nil, errors.New("Not Implemented")
}

// GetPrivateDataQueryResult ...
func (stub *MockStub) GetPrivateDataQueryResult(collection, query string) (shim.StateQueryIteratorInterface, error) {
	stub.isRO = true
	//TODO pageSize and bookmark is not supported by shim API yet
	iterator, _ := stub.CouchDBMock.GetQueryResult(collection, query, 1000, "")
	return iterator, nil
}

// GetState retrieves the value for a given key from the ledger
func (stub *MockStub) GetState(key string) ([]byte, error) {
	value := stub.State[key]
	return value, nil
}

// PutState writes the specified `value` and `key` into the ledger.
func (stub *MockStub) PutState(key string, value []byte) error {
	if stub.TxID == "" {
		err := errors.New("cannot PutState without a transactions - call stub.MockTransactionStart()?")
		return err
	}

	if stub.isRO {
		return fmt.Errorf(ErrPaginatedTmpl, "PUT_STATE", stub.TxID, stub.TxID)
	}

	// If the value is nil or empty, delete the key
	if len(value) == 0 {
		return stub.DelState(key)
	}
	stub.State[key] = value

	// insert key into ordered list of keys
	for elem := stub.Keys.Front(); elem != nil; elem = elem.Next() {
		elemValue := elem.Value.(string)
		comp := strings.Compare(key, elemValue)
		if comp < 0 {
			// key < elem, insert it before elem
			stub.Keys.InsertBefore(key, elem)
			break
		} else if comp == 0 {
			// keys exists, no need to change
			break
		} else { // comp > 0
			// key > elem, keep looking unless this is the end of the list
			if elem.Next() == nil {
				stub.Keys.PushBack(key)
				break
			}
		}
	}

	// special case for empty Keys list
	if stub.Keys.Len() == 0 {
		stub.Keys.PushFront(key)
	}

	stub.CouchDBMock.PutState(key, value)

	return nil
}

// DelState removes the specified `key` and its value from the ledger.
func (stub *MockStub) DelState(key string) error {
	if stub.isRO {
		return fmt.Errorf(ErrPaginatedTmpl, "DEL_STATE", stub.TxID, stub.TxID)
	}

	delete(stub.State, key)

	for elem := stub.Keys.Front(); elem != nil; elem = elem.Next() {
		if strings.Compare(key, elem.Value.(string)) == 0 {
			stub.Keys.Remove(elem)
		}
	}

	return nil
}

// GetStateByRange ...
func (stub *MockStub) GetStateByRange(startKey, endKey string) (shim.StateQueryIteratorInterface, error) {
	if err := validateSimpleKeys(startKey, endKey); err != nil {
		return nil, err
	}
	return NewMockStateRangeQueryIterator(stub, startKey, endKey), nil
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

// GetQueryResult function can be invoked by a chaincode to perform a
// rich query against state database.  Only supported by state database implementations
// that support rich query.  The query string is in the syntax of the underlying
// state database. An iterator is returned which can be used to iterate (next) over
// the query result set
func (stub *MockStub) GetQueryResult(query string) (shim.StateQueryIteratorInterface, error) {
	// Not implemented since the mock engine does not have a query engine.
	// However, a very simple query engine that supports string matching
	// could be implemented to test that the framework supports queries
	iterator, _ := stub.CouchDBMock.GetQueryResult("", query, 1000, "")
	return iterator, nil
}

// GetHistoryForKey function can be invoked by a chaincode to return a history of
// key values across time. GetHistoryForKey is intended to be used for read-only queries.
func (stub *MockStub) GetHistoryForKey(key string) (shim.HistoryQueryIteratorInterface, error) {
	return nil, errors.New("not implemented")
}

// GetStateByPartialCompositeKey function can be invoked by a chaincode to query the
// state based on a given partial composite key. This function returns an
// iterator which can be used to iterate over all composite keys whose prefix
// matches the given partial composite key. This function should be used only for
// a partial composite key. For a full composite key, an iter with empty response
// would be returned.
func (stub *MockStub) GetStateByPartialCompositeKey(objectType string, attributes []string) (shim.StateQueryIteratorInterface, error) {
	partialCompositeKey, err := stub.CreateCompositeKey(objectType, attributes)
	if err != nil {
		return nil, err
	}
	return NewMockStateRangeQueryIterator(stub, partialCompositeKey, partialCompositeKey+string(utf8.MaxRune)), nil
}

// CreateCompositeKey combines the list of attributes
// to form a composite key.
func (stub *MockStub) CreateCompositeKey(objectType string, attributes []string) (string, error) {
	return shim.CreateCompositeKey(objectType, attributes)
}

// SplitCompositeKey splits the composite key into attributes
// on which the composite key was formed.
func (stub *MockStub) SplitCompositeKey(compositeKey string) (string, []string, error) {
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

// GetStateByRangeWithPagination ...
func (stub *MockStub) GetStateByRangeWithPagination(startKey, endKey string, pageSize int32,
	bookmark string) (shim.StateQueryIteratorInterface, *peer.QueryResponseMetadata, error) {
	return nil, nil, nil
}

// GetStateByPartialCompositeKeyWithPagination ...
func (stub *MockStub) GetStateByPartialCompositeKeyWithPagination(objectType string, keys []string,
	pageSize int32, bookmark string) (shim.StateQueryIteratorInterface, *peer.QueryResponseMetadata, error) {
	return nil, nil, nil
}

// GetQueryResultWithPagination ...
func (stub *MockStub) GetQueryResultWithPagination(query string, pageSize int32,
	bookmark string) (shim.StateQueryIteratorInterface, *peer.QueryResponseMetadata, error) {
	stub.isRO = true
	iterator, metadata := stub.CouchDBMock.GetQueryResult("", query, pageSize, bookmark)
	return iterator, metadata, nil
}

// InvokeChaincode locally calls the specified chaincode `Invoke`.
// E.g. stub1.InvokeChaincode("othercc", funcArgs, channel)
// Before calling this make sure to create another MockStub stub2, call shim.NewMockStub("othercc", Chaincode)
// and register it with stub1 by calling stub1.MockPeerChaincode("othercc", stub2, channel)
func (stub *MockStub) InvokeChaincode(chaincodeName string, args [][]byte, channel string) peer.Response {
	// Internally we use chaincode name as a composite name
	if channel != "" {
		chaincodeName = chaincodeName + "/" + channel
	}
	// TODO "Args" here should possibly be a serialized pb.ChaincodeInput
	otherStub := stub.Invokables[chaincodeName]
	//	function, strings := getFuncArgs(Args)
	res := otherStub.MockInvoke(stub.TxID, args)
	return res
}

// GetCreator ...
func (stub *MockStub) GetCreator() ([]byte, error) {
	return stub.Creator, nil
}

// SetTransient set TransientMap to mockStub
func (stub *MockStub) SetTransient(tMap map[string][]byte) error {
	if stub.signedProposal == nil {
		return fmt.Errorf("signedProposal is not initialized")
	}
	payloadByte, err := proto.Marshal(&peer.ChaincodeProposalPayload{
		TransientMap: tMap,
	})
	if err != nil {
		return err
	}
	proposalByte, err := proto.Marshal(&peer.Proposal{
		Payload: payloadByte,
	})
	if err != nil {
		return err
	}
	stub.signedProposal.ProposalBytes = proposalByte
	stub.TransientMap = tMap
	return nil
}

// GetTransient ...
func (stub *MockStub) GetTransient() (map[string][]byte, error) {
	return stub.TransientMap, nil
}

// GetBinding Not implemented ...
func (stub *MockStub) GetBinding() ([]byte, error) {
	return nil, nil
}

// GetSignedProposal Not implemented ...
func (stub *MockStub) GetSignedProposal() (*peer.SignedProposal, error) {
	return stub.signedProposal, nil
}

func (stub *MockStub) setSignedProposal(sp *peer.SignedProposal) {
	stub.signedProposal = sp
}

// GetArgsSlice Not implemented ...
func (stub *MockStub) GetArgsSlice() ([]byte, error) {
	return nil, nil
}

func (stub *MockStub) setTxTimestamp(time *timestamp.Timestamp) {
	stub.TxTimestamp = time
}

// GetTxTimestamp ...
func (stub *MockStub) GetTxTimestamp() (*timestamp.Timestamp, error) {
	if stub.TxTimestamp == nil {
		return nil, errors.New("TxTimestamp not set")
	}
	return stub.TxTimestamp, nil
}

// SetEvent sets chaincode event
func (stub *MockStub) SetEvent(name string, payload []byte) error {
	if name == "" {
		return errors.New("event name cannot be empty string")
	}

	if stub.ignoringEvents {
		return nil
	}

	stub.ChaincodeEvent = &peer.ChaincodeEvent{EventName: name, Payload: payload}

	for _, sub := range stub.chaincodeEventSubscriptions {
		sub <- stub.ChaincodeEvent
	}

	return nil
}

// SetStateValidationParameter ...
func (stub *MockStub) SetStateValidationParameter(key string, ep []byte) error {
	return stub.SetPrivateDataValidationParameter("", key, ep)
}

// GetStateValidationParameter ...
func (stub *MockStub) GetStateValidationParameter(key string) ([]byte, error) {
	return stub.GetPrivateDataValidationParameter("", key)
}

// SetPrivateDataValidationParameter ...
func (stub *MockStub) SetPrivateDataValidationParameter(collection, key string, ep []byte) error {
	m, in := stub.EndorsementPolicies[collection]
	if !in {
		stub.EndorsementPolicies[collection] = make(map[string][]byte)
		m, in = stub.EndorsementPolicies[collection]
	}

	m[key] = ep
	return nil
}

// GetPrivateDataValidationParameter ...
func (stub *MockStub) GetPrivateDataValidationParameter(collection, key string) ([]byte, error) {
	m, in := stub.EndorsementPolicies[collection]

	if !in {
		return nil, nil
	}

	return m[key], nil
}

/*****************************
 Range Query Iterator
*****************************/

// MockStateRangeQueryIterator ...
type MockStateRangeQueryIterator struct {
	Closed   bool
	Stub     *MockStub
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
			if comp2 < 0 {
				return true
			}
			return false
		}
		current = current.Next()
	}
	return false
}

// Next returns the next key and value in the range query iterator.
func (iter *MockStateRangeQueryIterator) Next() (*queryresult.KV, error) {
	if iter.Closed == true {
		err := errors.New("MockStateRangeQueryIterator.Next() called after Close()")
		return nil, err
	}

	if iter.HasNext() == false {
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
	if iter.Closed == true {
		err := errors.New("MockStateRangeQueryIterator.Close() called after Close()")
		return err
	}

	iter.Closed = true
	return nil
}

// NewMockStateRangeQueryIterator ...
func NewMockStateRangeQueryIterator(stub *MockStub, startKey string, endKey string) *MockStateRangeQueryIterator {
	iter := new(MockStateRangeQueryIterator)
	iter.Closed = false
	iter.Stub = stub
	iter.StartKey = startKey
	iter.EndKey = endKey
	iter.Current = stub.Keys.Front()
	return iter
}

func getBytes(function string, args []string) [][]byte {
	bytes := make([][]byte, 0, len(args)+1)
	bytes = append(bytes, []byte(function))
	for _, s := range args {
		bytes = append(bytes, []byte(s))
	}
	return bytes
}

func getFuncArgs(bytes [][]byte) (string, []string) {
	function := string(bytes[0])
	args := make([]string, len(bytes)-1)
	for i := 1; i < len(bytes); i++ {
		args[i-1] = string(bytes[i])
	}
	return function, args
}

// ===== KompiTech

// From mock tx creator
func (stub *MockStub) From(txCreator ...interface{}) *MockStub {
	mspID, certPEM, err := TransformCreator(txCreator...)
	if err != nil {
		log.Fatal(err)
	}

	creator, err := proto.Marshal(&msp.SerializedIdentity{
		Mspid:   mspID,
		IdBytes: certPEM,
	})
	if err != nil {
		log.Fatal(err)
	}

	stub.Creator = creator
	return stub
}

func (stub *MockStub) generateTxUID() string {
	id := make([]byte, 32)
	rand.Seed(time.Now().UnixNano())
	rand.Read(id)
	return fmt.Sprintf("0x%x", id)
}

// Init func of chaincode - sugared version with autogenerated tx uuid
func (stub *MockStub) Init(iargs ...interface{}) peer.Response {
	arg1 := [][]byte{[]byte("Init")}

	args, err := convert2.ArgsToBytes(iargs...)
	if err != nil {
		return shim.Error(err.Error())
	}

	fullArgs := append(arg1, args...)

	return stub.MockInit(stub.generateTxUID(), fullArgs)
}

// Invoke sugared invoke function with autogenerated tx uuid
func (stub *MockStub) Invoke(funcName string, iargs ...interface{}) peer.Response {
	fargs, err := convert2.ArgsToBytes(iargs...)
	if err != nil {
		return shim.Error(err.Error())
	}
	args := append([][]byte{[]byte(funcName)}, fargs...)
	return stub.MockInvoke(stub.generateTxUID(), args)
}

// SetArgs set mocked Args
func (stub *MockStub) SetArgs(args [][]byte) {
	stub._args = args
}

func (stub *MockStub) EventSubscription() chan *peer.ChaincodeEvent {
	subscription := make(chan *peer.ChaincodeEvent, EventChannelBufferSize)
	stub.chaincodeEventSubscriptions = append(stub.chaincodeEventSubscriptions, subscription)
	return subscription
}

// ClearEvents clears chaincode events channel -
func (stub *MockStub) ClearEvents() {
	for len(stub.ChaincodeEventsChannel) > 0 {
		<-stub.ChaincodeEventsChannel
	}
}

// TravelInTime enables to simulate elapsed time.
// If you want to switch to default behavior, call ResetTime()
func (stub *MockStub) TravelInTime(sec int64, nsec int32) {
	ts, _ := stub.GetTxTimestamp()
	stub.useSetTime = true
	ts.Seconds += sec
	ts.Nanos += nsec
	stub.TxTimestamp = ts
}

//SetTime sets mock time to some absolute value
func (stub *MockStub) SetTime(value time.Time) {
	stub.useSetTime = true
	ts := timestamp.Timestamp{
		Seconds: value.Unix(),
		Nanos:   int32(value.Nanosecond()),
	}
	stub.TxTimestamp = &ts
}

// ResetTime stops using time value set by TravelInTime()
func (stub *MockStub) ResetTime() {
	stub.useSetTime = false
}

func (stub *MockStub) IgnoreEvents() {
	stub.ignoringEvents = true
}

func (stub *MockStub) AllowEvents() {
	stub.ignoringEvents = false
}

// GetLastEventPayload returns JSON representation of the last event payload
// Convenient method for events testing
func (stub *MockStub) GetLastEventPayload() (eventPayload map[string]interface{}) {
	payloadObj := stub.ChaincodeEvent.GetPayload()
	if payloadObj == nil {
		return nil
	}

	if err := json.Unmarshal(payloadObj, &eventPayload); err != nil {
		log.Fatalf("error getting last event: %v\n", err)
	}

	return eventPayload
}
