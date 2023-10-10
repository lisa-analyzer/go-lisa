package main

import (
	"bytes"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	sc "github.com/hyperledger/fabric/protos/peer"
)

// Define the Smart Contract structure
type SmartContract struct {
}


func (s *SmartContract) Init(APIstub shim.ChaincodeStubInterface) sc.Response {
	return shim.Success(nil)
}

func (s *SmartContract) Invoke(APIstub shim.ChaincodeStubInterface) sc.Response {

  defer s.Write()

  defer APIstub.GetState("samekey") // SAFE (LIFO order)


 return shim.Success(nil)

}

func (s *SmartContract) Write() {

	err := APIstub.PutState("samekey", []byte("myvalue"))

}
