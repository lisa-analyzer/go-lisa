package golang

import (
	"fmt"
	. "github.com/davidkhala/goutils"
	"github.com/golang/protobuf/ptypes/timestamp"
	"github.com/hyperledger/fabric-chaincode-go/shim"
	"github.com/hyperledger/fabric-protos-go/peer"
	"runtime/debug"
)

func PanicPeerResponse(resp peer.Response) {
	if resp.Status >= shim.ERRORTHRESHOLD {
		var errorPB = PeerResponse{
			resp.Status,
			resp.Message,
			string(resp.Payload),
		}
		PanicString(string(ToJson(errorPB)))
	}
}

func (cc CommonChaincode) InvokeChaincode(chaincodeName string, args [][]byte, channel string) peer.Response {
	var resp = cc.CCAPI.InvokeChaincode(chaincodeName, args, channel)
	PanicPeerResponse(resp)
	return resp
}

func (cc CommonChaincode) SplitCompositeKey(compositeKey string) (string, []string) {
	objectType, attributes, err := cc.CCAPI.SplitCompositeKey(compositeKey)
	PanicError(err)
	return objectType, attributes
}
func (cc CommonChaincode) CreateCompositeKey(objectType string, attributes []string) string {
	var key, err = cc.CCAPI.CreateCompositeKey(objectType, attributes)
	PanicError(err)
	return key
}
func (cc CommonChaincode) GetBinding() []byte {
	var result, err = cc.CCAPI.GetBinding()
	PanicError(err)
	return result
}
func (cc CommonChaincode) GetState(key string) []byte {
	var bytes, err = cc.CCAPI.GetState(key)
	PanicError(err)
	return bytes
}
func (cc CommonChaincode) GetStateObj(key string, v interface{}) bool {
	var bytes = cc.GetState(key)
	if bytes == nil {
		return false
	}
	FromJson(bytes, v)
	return true
}
func (cc CommonChaincode) GetTransient() map[string][]byte {
	transient, err := cc.CCAPI.GetTransient()
	PanicError(err)
	return transient
}
func (cc CommonChaincode) PutStateObj(key string, v interface{}) {
	var bytes = ToJson(v)
	cc.PutState(key, bytes)
}
func (cc CommonChaincode) PutState(key string, value []byte) {
	var err = cc.CCAPI.PutState(key, value)
	PanicError(err)
}
func (cc CommonChaincode) DelState(key string) {
	var err = cc.CCAPI.DelState(key)
	PanicError(err)
}
func (cc CommonChaincode) GetTxTimestamp() timestamp.Timestamp {
	ts, err := cc.CCAPI.GetTxTimestamp()
	PanicError(err)
	return *ts
}

func (cc CommonChaincode) GetHistoryForKey(key string) shim.HistoryQueryIteratorInterface {
	var r, err = cc.CCAPI.GetHistoryForKey(key)
	PanicError(err)
	return r
}
func (cc CommonChaincode) GetStateByPartialCompositeKey(objectType string, keys []string) shim.StateQueryIteratorInterface {
	var r, err = cc.CCAPI.GetStateByPartialCompositeKey(objectType, keys)
	PanicError(err)
	return r
}
func (cc CommonChaincode) GetStateByRange(startKey string, endKey string) shim.StateQueryIteratorInterface {
	var r, err = cc.CCAPI.GetStateByRange(startKey, endKey)
	PanicError(err)
	return r
}

// This call is only supported in a read only transaction.
func (cc CommonChaincode) GetStateByPartialCompositeKeyWithPagination(objectType string, keys []string, pageSize int, bookmark string) (shim.StateQueryIteratorInterface, QueryResponseMetadata) {
	var iterator, r, err = cc.CCAPI.GetStateByPartialCompositeKeyWithPagination(objectType, keys, int32(pageSize), bookmark)
	PanicError(err)
	return iterator, QueryResponseMetadata{int(r.FetchedRecordsCount), r.Bookmark}
}

// This call is only supported in a read only transaction.
func (cc CommonChaincode) GetStateByRangeWithPagination(startKey, endKey string, pageSize int, bookmark string) (shim.StateQueryIteratorInterface, QueryResponseMetadata) {
	var iterator, r, err = cc.CCAPI.GetStateByRangeWithPagination(startKey, endKey, int32(pageSize), bookmark)
	PanicError(err)
	return iterator, QueryResponseMetadata{int(r.FetchedRecordsCount), r.Bookmark}
}

func (cc CommonChaincode) SetEvent(name string, payload []byte) {
	var err = cc.CCAPI.SetEvent(name, payload)
	PanicError(err)
}

var DeferHandlerPeerResponse = func(errString string, params ...interface{}) bool {
	var response = params[0].(*peer.Response)
	response.Status = shim.ERROR
	response.Message = errString
	response.Payload = []byte(errString)
	fmt.Println("DeferHandlerPeerResponse", errString)
	debug.PrintStack()
	return true
}

// From https://github.com/hyperledger/fabric-chaincode-go/commit/2d899240a7ed642a381ba9df2f6b0c303cb149dc
func GetMSPID() string {
	var mspId, err = shim.GetMSPID()
	PanicError(err)
	return mspId
}
func (cc CommonChaincode) GetFunctionAndArgs() (string, [][]byte) {
	var allArgs = cc.CCAPI.GetArgs()
	var fcn = ""
	var args = [][]byte{}
	if len(allArgs) >= 1 {
		fcn = string(allArgs[0])
		args = allArgs[1:]
	}
	return fcn, args
}
func ChaincodeStart(cc shim.Chaincode) {
	var err = shim.Start(cc)
	PanicError(err)
}
