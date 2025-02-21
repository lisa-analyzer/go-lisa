package main

import (
	"fmt"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	"github.com/hyperledger/fabric-protos-go/peer"
)

type SimpleContract struct {
}

func (t *SimpleContract) Invoke(stub shim.ChaincodeStubInterface) peer.Response {
	
	f, args := stub.GetFunctionAndParameters()
	resp := stub.InvokeChaincode("contract", []byte("10"), "ch" + string(args[0])) // KO: different channels can be choosen at runtime

	return shim.Success([]byte("Hello!"))
}
