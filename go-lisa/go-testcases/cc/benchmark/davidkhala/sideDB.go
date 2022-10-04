package golang

import (
	. "github.com/davidkhala/goutils"
	"github.com/hyperledger/fabric-chaincode-go/shim"
)

func (cc CommonChaincode) GetPrivateData(collection, key string) []byte {
	var r, err = cc.CCAPI.GetPrivateData(collection, key)
	PanicError(err)
	return r
}
func (cc CommonChaincode) GetPrivateObj(collection, key string, v interface{}) bool {
	var r, err = cc.CCAPI.GetPrivateData(collection, key)
	PanicError(err)
	if r == nil {
		return false
	}
	FromJson(r, v)
	return true
}
func (cc CommonChaincode) PutPrivateObj(collection, key string, v interface{}) {
	var err = cc.CCAPI.PutPrivateData(collection, key, ToJson(v))
	PanicError(err)
}
func (cc CommonChaincode) PutPrivateData(collection, key string, value []byte) {
	var err = cc.CCAPI.PutPrivateData(collection, key, value)
	PanicError(err)
}

func (cc CommonChaincode) GetPrivateDataByPartialCompositeKey(collection, objectType string, keys []string) shim.StateQueryIteratorInterface {
	var r, err = cc.CCAPI.GetPrivateDataByPartialCompositeKey(collection, objectType, keys)
	PanicError(err)
	return r
}
func (cc CommonChaincode) GetPrivateDataByRange(collection, startKey, endKey string) shim.StateQueryIteratorInterface {
	var r, err = cc.CCAPI.GetPrivateDataByRange(collection, startKey, endKey)

	PanicError(err)
	return r
}
func (cc CommonChaincode) GetPrivateDataQueryResult(collection, query string) shim.StateQueryIteratorInterface {
	var r, err = cc.CCAPI.GetPrivateDataQueryResult(collection, query)
	PanicError(err)
	return r
}
func (cc CommonChaincode) DelPrivateData(collection, key string) {
	var err = cc.CCAPI.DelPrivateData(collection, key)
	PanicError(err)
}

// TODO is it used as getAll state starting with prefix?
func (cc CommonChaincode) GetStateRange(collection, prefix string) shim.StateQueryIteratorInterface {
	return cc.GetPrivateDataByRange(collection, prefix, prefix+"\x7f")
}

func ImplicitCollection(mspid string) string {
	return "_implicit_org_" + mspid
}
