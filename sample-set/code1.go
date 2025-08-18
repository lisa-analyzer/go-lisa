package main

import (
	"fmt"
	"github.com/hyperledger/fabric-chaincode-go/shim"
	"github.com/hyperledger/fabric-protos-go/peer"
)

type Contract1 struct {
}

func (s *Contract1) Invoke(stub shim.ChaincodeStubInterface) peer.Response {
	// ...
	contractName := "Contract2"
	channelName := "channel-A"
	resp := stub.InvokeChaincode(contractName, []byte(""),channelName)
	// ...
	return shim.Success([]byte("OK"))
}
