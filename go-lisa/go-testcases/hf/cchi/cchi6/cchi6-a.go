package main

import (
	"fmt"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	"github.com/hyperledger/fabric-protos-go/peer"
)

type ContractA struct {
}

func (s *ContractA) Invoke(stub shim.ChaincodeStubInterface) peer.Response {
	
	resp := stub.InvokeChaincode("contractB", []byte("10"), "mychannel")

	return shim.Success([]byte("Hello!"))
}
