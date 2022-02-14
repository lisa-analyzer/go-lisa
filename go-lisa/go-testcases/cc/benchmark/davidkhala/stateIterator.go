package golang

import (
	. "github.com/davidkhala/goutils"
	"github.com/hyperledger/fabric-chaincode-go/shim"
)

func parse100States(iterator shim.StateQueryIteratorInterface, filter func(StateKV) bool) ([]StateKV, string) {
	defer PanicError(iterator.Close())
	var kvs []StateKV
	var index = 0
	var lastKey = ""
	for iterator.HasNext() {
		if index < 99 {
			kv, err := iterator.Next()
			PanicError(err)
			lastKey = kv.Key
			var stateKV = StateKV{kv.Namespace, kv.Key, string(kv.Value)}
			if filter == nil || filter(stateKV) {
				kvs = append(kvs, stateKV)
			}
			index++
		} else {
			return kvs, lastKey
		}
	}
	return kvs, ""
}
func (cc CommonChaincode) WorldStatesPrivate(collection, startKey string, filter func(StateKV) bool) []StateKV {
	var keysIterator shim.StateQueryIteratorInterface
	keysIterator = cc.GetPrivateDataByRange(collection, startKey, "")

	var states, bookmark = parse100States(keysIterator, filter)
	if bookmark != "" {
		return append(states, cc.WorldStatesPrivate(collection, bookmark, filter)[1:]...)
	} else {
		return states
	}
}

func (cc CommonChaincode) WorldStates(startKey string, filter func(StateKV) bool) []StateKV {
	var keysIterator shim.StateQueryIteratorInterface
	keysIterator = cc.GetStateByRange(startKey, "")
	var states, bookmark = parse100States(keysIterator, filter)
	if bookmark != "" {
		return append(states, cc.WorldStates(bookmark, filter)[1:]...)
	} else {
		return states
	}
}
