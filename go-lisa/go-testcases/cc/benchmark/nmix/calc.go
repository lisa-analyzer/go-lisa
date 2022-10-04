package main

import (
	"fmt"
	"github.com/hyperledger/fabric-chaincode-go/shim"
	"github.com/hyperledger/fabric-protos-go/peer"
	"strconv"
)

type CalcToken struct {
}

const InitialTokenValue = 100
const TN = "Token"

func (token *CalcToken) Init(stub shim.ChaincodeStubInterface) peer.Response {
	stub.PutState(TN, []byte(strconv.Itoa(InitialTokenValue)))
	payload := fmt.Sprintf("Token initialized with value %d", InitialTokenValue)
	return shim.Success([]byte(payload))
}

// Invoke method
func (token *CalcToken) Invoke(stub shim.ChaincodeStubInterface) peer.Response {

	funcName, _ := stub.GetFunctionAndParameters()
	if funcName == "add" {
		return add(stub)
	} else if funcName == "substract" {
		return substract(stub)
	}
	return shim.Error("Bad Function Name: " + funcName)
}

func add(stub shim.ChaincodeStubInterface) peer.Response {
	_, params := stub.GetFunctionAndParameters()
	if len(params) != 1 {
		return shim.Error("there must be one argument")
	}
	addValue, err := strconv.Atoi(params[0])
	if err != nil {
		return shim.Error("the argument must be a number")
	}
	if addValue <= 0 {
		return shim.Error("the argument must be a positive number")
	}

	value, _ := stub.GetState(TN)

	tValue, _ := strconv.Atoi(string(value))

	tValue += addValue

	value = []byte(strconv.Itoa(tValue))

	stub.PutState(TN, value)

	return shim.Success(value)
}

func substract(stub shim.ChaincodeStubInterface) peer.Response {
	_, params := stub.GetFunctionAndParameters()
	if len(params) != 1 {
		return shim.Error("there must be one argument")
	}
	subValue, err := strconv.Atoi(params[0])
	if err != nil {
		return shim.Error("the argument must be a number")
	}
	if subValue <= 0 {
		return shim.Error("the argument must be a positive number")
	}

	value, _ := stub.GetState(TN)

	tValue, _ := strconv.Atoi(string(value))

	if subValue > tValue {
		return shim.Error("the argument should be less than the current balance")
	}

	tValue -= subValue

	value = []byte(strconv.Itoa(tValue))

	stub.PutState(TN, value)

	return shim.Success(value)
}

func main() {
	err := shim.Start(new(CalcToken))
	if err != nil {
		fmt.Printf("Error starting chaincode: %s", err)
	}
}
