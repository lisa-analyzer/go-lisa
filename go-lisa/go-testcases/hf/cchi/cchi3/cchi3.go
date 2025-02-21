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
	
	channel := "channelA"
	if string(args[0]) == "planB" { 
		channel = "channelB" // control flow dependency
	}
	
	resp := stub.InvokeChaincode("contract", []byte("10"), channel) // KO: different channels can be choosen at runtime

	return shim.Success([]byte("Hello!"))
}
