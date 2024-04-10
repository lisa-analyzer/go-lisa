package main

import (
	"fmt"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	"github.com/hyperledger/fabric-protos-go/peer"
)

type SimpleAsset struct {
}

func (t *SimpleAsset) Invoke(stub shim.ChaincodeStubInterface) peer.Response {
	
	fn, args := stub.GetFunctionAndParameters()

	value1, _ := stub.GetState(args[0]) // KO: error is discarded
	
	value2, err := stub.GetState(args[1]) // KO: error is not handled

	return shim.Success([]byte("Hello!"))
}
