package main

import (
	"fmt"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	"github.com/hyperledger/fabric-protos-go/peer"
)

type SimpleAsset struct {
}

func (t *SimpleAsset) Invoke(stub shim.ChaincodeStubInterface) peer.Response {
	
	resp := stub.InvokeChaincode("contract", []byte("10"), "channel1") // KO: there are two InvokeChaincode with different channels
	
	resp := stub.InvokeChaincode("contract", []byte("10"), "channel2")

	return shim.Success([]byte("Hello!"))
}
