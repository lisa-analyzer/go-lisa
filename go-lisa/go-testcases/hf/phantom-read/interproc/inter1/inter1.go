package main

import (
	"fmt"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	"github.com/hyperledger/fabric-protos-go/peer"
)

type SimpleAsset struct {
}

func (t *SimpleAsset) Invoke(stub shim.ChaincodeStubInterface) peer.Response {

		resultsIterator, err := stub.GetQueryResult(queryString)
		if err != nil {
			return nil, err
		}
		defer resultsIterator.Close()

		var assets []*Asset
		i = 1
		for resultsIterator.HasNext() {
			queryResult, err := resultsIterator.Next()
			if err != nil {
				return shim.Error([]byte("Error!"))
			}

			foo(stub, i, queryResult)

			i = i + 1
		}

	return shim.Success([]byte("Hello!"))
}

func foo(stub shim.ChaincodeStubInterface, n int, iter StateQueryIteratorInterface) {
	stub.PutState("KEY"+n, iter.Value) // KO: the value come from a GetQueryResult
}
