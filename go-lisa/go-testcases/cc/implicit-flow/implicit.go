//SPDX-License-Identifier: Apache-2.0

// Import dependencies
// Import the chaincode shim package and the peer protobuf package

/*  This code is based on code written by the Hyperledger Fabric community.
  Original code can be found here: https://github.com/hyperledger/fabric-samples/blob/release/chaincode/chaincode_example02/chaincode_example02.go
 */

package main

import (
    "fmt"
	"math/rand"
    "github.com/hyperledger/fabric/core/chaincode/shim"
    "github.com/hyperledger/fabric/protos/peer"
)

// SampleChaincode implements a simple chaincode to manage an asset
type SampleChaincode struct {

}

// Init is called during chaincode instantiation to initialize
// data. We'll be adding more in this function later on.
func (t *SampleChaincode) Init(stub shim.ChaincodeStubInterface) peer.Response {
	args := stub.GetArgs()
	if rand.Int() % 2 == 0 {
		stub.PutState(args[0], []byte(0))
	} else {
		stub.PutState(args[0], []byte(1))
	}
    return shim.Success(nil)
}

// Invoke is called per transaction on the chaincode. Each transaction is
// either a 'get' or a 'set' on the asset created by Init function. The Set
// method may create a new asset by specifying a new key-value pair.
func (t *SampleChaincode) Invoke(stub shim.ChaincodeStubInterface) peer.Response {

    // Return the result as success payload
    return shim.Success(nil)
}


// main function starts up the chaincode in the container during instantiate
func main() {
    err := shim.Start(new(SampleChaincode))
    if err != nil {
        fmt.Println("Could not start SampleChaincode")
    } else {
        fmt.Println("SampleChaincode successfully started")
    }
}
