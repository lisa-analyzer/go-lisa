package main

import (
	"fmt"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	"github.com/hyperledger/fabric-protos-go/peer"
)

type ContractB struct {
}

func (t *ContractB) Invoke(stub shim.ChaincodeStubInterface) peer.Response {
	
	stub.PutState("key1", "Bob")

	return shim.Success([]byte("OK"))
}
