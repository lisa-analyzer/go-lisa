package golang

import "github.com/hyperledger/fabric-chaincode-go/shim"

func (cc CommonChaincode) WorldStatesPrivateComposite(collection, objectType string, filter func(StateKV) bool) []StateKV {
	var keysIterator shim.StateQueryIteratorInterface
	keysIterator = cc.GetPrivateDataByPartialCompositeKey(collection, objectType, nil)
	return ParseStates(keysIterator, filter)
}

func (cc CommonChaincode) WorldStatesComposite(objectType string, filter func(StateKV) bool) []StateKV {
	var keysIterator shim.StateQueryIteratorInterface
	keysIterator = cc.GetStateByPartialCompositeKey(objectType, nil)
	return ParseStates(keysIterator, filter)
}
