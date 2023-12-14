package main

import (
	"fmt"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	"github.com/hyperledger/fabric-protos-go/peer"
)

type SimpleAsset struct {
}

func (t *SimpleAsset) Invoke(stub shim.ChaincodeStubInterface) peer.Response {

		resultsIterator, err := stub.GetQueryResult(queryString) // OK: Safe
		if err != nil {
			return nil, err
		}
		defer resultsIterator.Close()

		for resultsIterator.HasNext() {
			queryResult, err := resultsIterator.Next()
			if err != nil {
				fmt.Println("Error!")
			}

			fmt.Println(queryResult.Value)
		}

	return shim.Success([]byte("Hello!"))
}
