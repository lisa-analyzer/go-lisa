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

		i = 1
		for resultsIterator.HasNext() {
			queryResult, err := resultsIterator.Next()
			if err != nil {
				return shim.Error([]byte("Error!"))
			}

			a = queryResult
			b = a.Value

			stub.PutState("KEY"+i, b) // KO: the value of b come from a GetQueryResult

			i = i + 1
		}

	return shim.Success([]byte("Hello!"))
}
