package main

import (
	"fmt"
	"strings"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	"github.com/hyperledger/fabric-protos-go/peer"
)

// SimpleAssetChaincode ...
type SimpleAssetChaincode struct {
}

// Init ...
func (cc *SimpleAssetChaincode) Init(stub shim.ChaincodeStubInterface) peer.Response {
	return shim.Success(nil)
}

// Invoke ...
func (cc *SimpleAssetChaincode) Invoke(stub shim.ChaincodeStubInterface) peer.Response {
	var (
		err    error
		result string
	)

	function, args := stub.GetFunctionAndParameters()

	switch function {
	case "init":
		return cc.Init(stub)
	case "query":
		result, err = query(stub, args)
	case "queryPrivateData":
		result, err = queryPrivateData(stub, args)
	case "store":
		result, err = store(stub, args)
	case "storePrivateData":
		result, err = storePrivateData(stub, args)
	case "setEvent":
		result, err = setEvent(stub, args)
	default:
		return shim.Error("Unknown function")
	}

	if err != nil {
		return shim.Error(fmt.Sprintf("[%s] with args (%s) failed: %s", function, strings.Join(args, " | "), err.Error()))
	}

	return shim.Success([]byte(result))

}
