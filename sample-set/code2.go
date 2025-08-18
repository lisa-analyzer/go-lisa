package main

import (
	"bytes"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	sc "github.com/hyperledger/fabric/protos/peer"
)

type Contract2 struct {
}

func (s *Contract2) Invoke(APIstub shim.ChaincodeStubInterface) sc.Response {
  // ...
  err := APIstub.PutState("key1", []byte("Bob"))
  // ...
  return shim.Success([]byte("OK"))
}