// blank.go - это простая заготовка для чейнкода

package main

import (
	"github.com/hyperledger/fabric-chaincode-go/shim"
	"github.com/hyperledger/fabric-protos-go/peer"
)

type BlankChaincode struct {
}

func (token *BlankChaincode) Init(stub shim.ChaincodeStubInterface) peer.Response {
	return shim.Success(nil)
}

// Invoke method
func (token *BlankChaincode) Invoke(stub shim.ChaincodeStubInterface) peer.Response {

	funcName, _ := stub.GetFunctionAndParameters()
	// if funcName == "set" {
	// 	return SetToken(stub)
	// } else if funcName == "get" {
	// 	return GetToken(stub)
	// } else if funcName == "delete" {
	// 	return DeleteToken(stub)
	// }
	return shim.Error("Bad Function Name: " + funcName)
}

// func SetToken(stub shim.ChaincodeStubInterface) peer.Response {
// 	return shim.Success(nil)
// }

// func GetToken(stub shim.ChaincodeStubInterface) peer.Response {
// 	return shim.Success(nil)
// }

// func DeleteToken(stub shim.ChaincodeStubInterface) peer.Response {
// 	return shim.Success(nil)
// }

func main() {
	err := shim.Start(new(BlankChaincode))
	if err != nil {
		fmt.Printf("Error starting chaincode: %s", err)
	}
}
