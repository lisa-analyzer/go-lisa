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
	
	channel := "channel"
	sum := 0
	for i := 1; i < int(args[0]); i++ {
		channel += "1" // control flow dependency
	}
	channel += "A"
	
	resp := stub.InvokeChaincode("contract", []byte("10"), channel) // KO: different channels can be choosen at runtime

	return shim.Success([]byte("Hello!"))
}
