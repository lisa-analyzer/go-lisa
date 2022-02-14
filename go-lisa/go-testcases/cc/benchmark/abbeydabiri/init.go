package main

import (
	"fmt"

	"github.com/abbeydabiri/hdlchaincode/utils"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	pb "github.com/hyperledger/fabric-protos-go/peer"
)

// HDLChaincode - define a type for chaincode.
// HDLChaincode type must implements shim.Chaincode interface
type HDLChaincode struct{}

// Init - Implements shim.Chaincode interface Init() method
func (t *HDLChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	fmt.Println("########### HDLChaincode Init ###########")

	resp := utils.Response{Code: utils.CODEALLAOK, Message: "HDL Chaincode started", Payload: nil}
	return shim.Success((resp.FormatResponse()))
}
