/*
Copyright 2017 IBM Corporation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

// This chaincode is intended for a single channel producer/consumer type relationship.

package main

import (
	"encoding/json"
	"fmt"
	"time"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	sc "github.com/hyperledger/fabric/protos/peer"

	"github.com/satori/go.uuid"
)

// ProductChaincode implements the smart contract for a Product asset
type ProductChaincode struct {
}

// Product contains relevant information about a product's lifecycle
// Structure tags are used by encoding/json library
type Product struct {
	SerialNumber string `json:"serial_number"` // uuidsting
	ProducerID   string `json:"producer_id"`
	ProducedDate int64  `json:"produced_date"` // seconds since Jan 1, 1970 (epoch)
	OffererID    string `json:"offerer_id"`
	OfferedDate  int64  `json:"offered_date"`
	TraderID     string `json:"trader_id"`
	TradedDate   int64  `json:"traded_date"`
	TradedForSN  string `json:"traded_for_serial_number"` // only set when traded product is created
	ConsumerID   string `json:"consumer_id"`
	ConsumedDate int64  `json:"consumed_date"`
	ProductType  string `json:"product_type"` // simply stored as "orgX" depending creator
	State        string `json:"state"`        // (produced -> offered) OR (traded -> consumed)
}

// Init method is called when the Smart Contract is instantiated by the blockchain network
// creates a key called "lookup"
func (s *ProductChaincode) Init(stub shim.ChaincodeStubInterface) sc.Response {
	fmt.Println("Product Ledger Initializing")
	// Retrieve the requested Smart Contract function and arguments
	_, args := stub.GetFunctionAndParameters()
	if len(args) != 0 {
		return shim.Error("Incorrect number of arguments. Expecting 0")
	}
	// create a listing of UUIDs stored on the ledger with key "index"
	var index []uuid.UUID
	indexAsBytes, _ := json.Marshal(index)
	stub.PutState("index", indexAsBytes)
	return shim.Success(nil)
}

// Invoke method is called as a result of an application request to run the Smart Contract.
// The calling application program has also specified the particular smart contract function to be called, with arguments.
// - createProduct
// - tradeProduct
// - consumeProduct
// - getProduct
// - getIndex
func (s *ProductChaincode) Invoke(stub shim.ChaincodeStubInterface) sc.Response {
	fmt.Println("Product Chaincode Invoked")
	// Retrieve the requested Smart Contract function and arguments
	function, args := stub.GetFunctionAndParameters()
	// Route to the appropriate handler function to interact with the ledger appropriately
	switch function {
	case "createProduct":
		return s.createProduct(stub, args)
	case "tradeProduct":
		return s.tradeProduct(stub, args)
	case "offerProduct":
		return s.offerProduct(stub, args)
	case "consumeProduct":
		return s.consumeProduct(stub, args)
	case "getProduct":
		return s.getProduct(stub, args)
	case "getIndex":
		return s.getIndex(stub)
	default:
		return shim.Error("Invalid function name.")
	}
}

func (s *ProductChaincode) createProduct(stub shim.ChaincodeStubInterface, args []string) sc.Response {
	//TODO set the ProductType based on the GetCreator results
	fmt.Println("Invoking createProduct")
	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Must provide the serial number UUID as a key.")
	}
	// ensure key is a uuid
	serialNumber, err := uuid.FromString(args[0])
	if err != nil {
		return shim.Error("Couldn't convert serial number to UUID.")
	}
	// ensure key doesn't exist
	state, err := stub.GetState(serialNumber.String())
	if state != nil {
		return shim.Error("Product with that serial number already exists.")
	}
	// set timestamp and user requesting the production
	producedDate := time.Now().Unix()
	creator, _ := stub.GetCreator()
	//TODO: set the product type based on creator
	// create the product
	var product = Product{SerialNumber: args[0], ProducerID: string(creator), ProducedDate: producedDate, State: "produced"}
	productAsBytes, _ := json.Marshal(product)
	stub.PutState(args[0], productAsBytes)
	// append the new serial number uuid to the index
	indexAsBytes, _ := stub.GetState("index")
	var index []uuid.UUID
	json.Unmarshal(indexAsBytes, &index)
	index = append(index, serialNumber)
	indexAsBytes, _ = json.Marshal(index)
	stub.PutState("index", indexAsBytes)
	return shim.Success(nil)
}

/*
When a trader from orgX offers a product, it is marked as "offered".
When a trade is fulfulled, a new product is created with the attributes of the traded
product and the state is set to "traded". It is the responsibility of the trader
to perform these actions.
TODO: can this also be enforced in the chaincode?
*/

func (s *ProductChaincode) offerProduct(stub shim.ChaincodeStubInterface, args []string) sc.Response {
	fmt.Println("Invoking offerProduct")
	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Must provide the serial number UUID as a key.")
	}
	// ensure key is a uuid
	_, err := uuid.FromString(args[0])
	if err != nil {
		return shim.Error("Couldn't convert serial number to UUID.")
	}
	offeredDate := time.Now().Unix()
	creator, _ := stub.GetCreator()
	productAsBytes, _ := stub.GetState(args[0])
	product := Product{}
	json.Unmarshal(productAsBytes, &product)
	// ensure product state is "produced"
	if product.State == "produced" {
		product.State = "offered"
		product.OffererID = string(creator)
		product.OfferedDate = offeredDate
	} else {
		return shim.Error("Cannot offer this product, state not 'produced'.")
	}
	productAsBytes, _ = json.Marshal(product)
	stub.PutState(args[0], productAsBytes)
	return shim.Success(nil)
}

/* tradeProduct is much like createProduct, however it is creating a new instance of traded product.
only trader id's can call tradeProduct and most of the values of the product will be set based on the
product acquire by the trader.
Two args are passed in. The first is the uuid of the product that was traded for this one.
The 2nd arg is a byte array of the acquired product.
SerialNumber
ProducerID
ProducedDate
OffererID
OfferedDate
ConsumerID
ConsumedDate
ProductType

set here:
TraderID
TradedDate
TradedForSN (first arg)
State (to 'traded')
*/
func (s *ProductChaincode) tradeProduct(stub shim.ChaincodeStubInterface, args []string) sc.Response {
	fmt.Println("Invoking tradeProduct")
	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments.")
	}
	// ensure key is a uuid
	serialNumber, err := uuid.FromString(args[0])
	if err != nil {
		return shim.Error("Couldn't convert serial number to UUID.")
	}
	// try to unmarshal the incoming product
	product := Product{}
	json.Unmarshal([]byte(args[1]), &product)
	tradedDate := time.Now().Unix()
	creator, _ := stub.GetCreator()
	// TODO might want to ensure all the values are set inside the incoming product
	product.TraderID = string(creator)
	product.TradedDate = tradedDate
	product.State = "traded"
	product.TradedForSN = args[0]
	productAsBytes, _ := json.Marshal(product)
	// note that we're using the serial number of the acquired product here
	stub.PutState(product.SerialNumber, productAsBytes)
	// also, update the index
	indexAsBytes, _ := stub.GetState("index")
	var index []uuid.UUID
	json.Unmarshal(indexAsBytes, &index)
	index = append(index, serialNumber)
	indexAsBytes, _ = json.Marshal(index)
	stub.PutState("index", indexAsBytes)
	return shim.Success(nil)
}

func (s *ProductChaincode) consumeProduct(stub shim.ChaincodeStubInterface, args []string) sc.Response {
	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting they key to be provided.")
	}
	productAsBytes, _ := stub.GetState(args[0])
	if productAsBytes == nil {
		return shim.Error("Unable to determine product by given key: " + args[0])
	}
	product := Product{}
	json.Unmarshal(productAsBytes, &product)
	consumedDate := time.Now().Unix()
	creator, _ := stub.GetCreator()
	// already consumed?
	if product.State == "consumed" {
		return shim.Error("Product already consumed. Try again!")
	}
	// state traded?
	if product.State == "traded" {
		product.ConsumedDate = consumedDate
		product.ConsumerID = string(creator)
		product.State = "consumed"
	} else {
		return shim.Error("Cannot consume this product, state not 'traded'.")
	}
	productAsBytes, _ = json.Marshal(product)
	stub.PutState(args[0], productAsBytes)
	return shim.Success(nil)
}

func (s *ProductChaincode) getProduct(stub shim.ChaincodeStubInterface, args []string) sc.Response {
	fmt.Println("Invoking getProduct")
	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Must provide the serial number UUID as a key.")
	}
	_, err := uuid.FromString(args[0])
	if err != nil {
		return shim.Error("Couldn't convert serial number to UUID.")
	}
	productAsBytes, _ := stub.GetState(args[0])
	if productAsBytes == nil {
		return shim.Error("Couldn't find product with that serial number.")
	}
	return shim.Success(productAsBytes)
}

func (s *ProductChaincode) getIndex(stub shim.ChaincodeStubInterface) sc.Response {
	fmt.Println("Invoking getIndex")
	indexAsBytes, _ := stub.GetState("index")
	return shim.Success(indexAsBytes)
}

// The main function is used b the unit test only.
func main() {
	err := shim.Start(new(ProductChaincode))
	if err != nil {
		fmt.Printf("Error creating new Smart Contract: %s", err)
	}
}
