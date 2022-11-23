package main

import (
	"fmt"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/hyperledger/fabric/protos/peer"
	"time"
	"encoding/json"
)

type ApartementRegister struct {
}

type Renter struct {
	name    string
	surname string
	movedIn time.Time
}

type Block struct {
	id       string
	street   string
	number   string
	renters  []Renter
	nOfRooms string
}

//cache of blocks id
var blocks map[string]bool

func createId(street string, number string) string {
	return fmt.Sprintf("%s%d", street, number)
}

//retrieve a block on the ledger
func getBlock(stub shim.ChaincodeStubInterface, key string) (*Block, error) {
	var block Block
	block_marshalled, err := stub.GetState(key)
	err = json.Unmarshal(block_marshalled, &block)
	return &block, err
}

//save a block on the ledger
func putBlock(stub shim.ChaincodeStubInterface, key string, block *Block) error {
	block_marshalled, _ := json.Marshal(*block)
	return stub.PutState(key, block_marshalled)
}

//returns information about a renter
func queryRenter(stub shim.ChaincodeStubInterface, street string, number string, name string) peer.Response {
	id := createId(street, number)
	if !blocks[id] {
		return shim.Error(fmt.Sprintf("No block %s registered", id))
	}
	block, _ := getBlock(stub, id)
	var renter *Renter
	for _, r := range block.renters {
		if r.name == name {
			renter = &r
		}
	}
	if renter == nil {
		return shim.Error(fmt.Sprintf("Could not find renter %s in block %s", name, id))
	} else {
		renter_marshalled, _ := json.Marshal(*renter)
		return shim.Success(renter_marshalled)
	}
}

//registers a new renter in an apartmentblock
func registerNewRenter(stub shim.ChaincodeStubInterface, street string, number string, name string, surname string) peer.Response {
	id := createId(street, number)
	if !blocks[id] {
		return shim.Error(fmt.Sprintf("No block %s registered", id))
	}
	block, err := getBlock(stub, id)
	if err != nil {
		return shim.Error(fmt.Sprintf("could not retrieve %s", id))
	}

	now := time.Now()
	renter := Renter{
		name:    name,
		surname: surname,
		movedIn: now,
	}

	block.renters = append(block.renters, renter)

	block_marshalled, _ := json.Marshal(*block)
	err = stub.PutState(id, block_marshalled)
	if err != nil {
		return shim.Error(fmt.Sprintf("could not update %s", id))
	}
	block_marshalled, err = stub.GetState(id)
	json.Unmarshal(block_marshalled, block)
	return shim.Success([]byte(fmt.Sprintf("Block %s has now %d renters", id, len(block.renters))))
}

//creates a new block
func newBlock(stub shim.ChaincodeStubInterface, street string, number string, nOfRooms string) peer.Response {
	id := createId(street, number)
	if blocks[id] {
		return shim.Error(fmt.Sprintf("Block %s already exists", id))
	}
	blocks[id] = true

	block := new(Block)
	block.id = id
	block.street = street
	block.number = number
	block.nOfRooms = nOfRooms
	putBlock(stub, id, block)

	blocks_marshalled, _ := json.Marshal(blocks)
	stub.PutState("blocksIdCache", blocks_marshalled)

	return shim.Success([]byte(fmt.Sprintf("Successfully created block %s.", id)))
}

//returns the number of blocks
func blocksCount() peer.Response {
	count := len(blocks)
	return shim.Success([]byte(fmt.Sprintf("%d blocks found", count)))
}

//returns the number of renters in a specific block
func rentersCount(stub shim.ChaincodeStubInterface, street string, number string) peer.Response {
	id := createId(street, number)
	block, err := getBlock(stub, id)
	if block == nil || err != nil {
		return shim.Error(fmt.Sprintf("could not retrieve %s %d", street, number))
	}

	return shim.Success([]byte(fmt.Sprintf("%d renters in %s found", len(block.renters), block.id)))
}

//Finds an apartmentblock whithout any renters
func findEmptyBlock(stub shim.ChaincodeStubInterface) peer.Response {
	for id, _ := range blocks {
		block, err := getBlock(stub, id)
		if err != nil {
			shim.Error(fmt.Sprintf("Could not find block %s", id))
		}
		if len(block.renters) == 0 {
			block_marshalled, err := json.Marshal(*block)
			if err != nil {
				return shim.Error("Could not marshal block.")
			}
			return shim.Success(block_marshalled)
		}
	}
	return shim.Error("No blocks empty")
}

//Initialisation of the Chaincode
func (m *ApartementRegister) Init(stub shim.ChaincodeStubInterface) peer.Response {
	blocks = make(map[string]bool)
	return shim.Success([]byte("Successfully initialized Chaincode."))
}

//Entry Point of an invocation
func (m *ApartementRegister) Invoke(stub shim.ChaincodeStubInterface) peer.Response {
	function, para := stub.GetFunctionAndParameters()

	switch(function) {
	case "queryRenter":
		if len(para) < 3 {
			return shim.Error("not enough arguments for queryRenter. 3 required")
		} else {
			return queryRenter(stub, para[0], para[1], para[2])
		}
	case "registerRenter":
		if len(para) < 3 {
			return shim.Error("not enough arguments for registerRenter. 4 required")
		} else {
			return registerNewRenter(stub, para[0], para[1], para[2], para[3])
		}
	case "newBlock":
		return newBlock(stub, para[0], para[1], para[2])
	case "blocksCount":
		return blocksCount()
	case "rentersCount":
		if len(para) < 2 {
			return shim.Error("not enough arguments for rentersCount. 2 required")
		} else {
			return rentersCount(stub, para[0], para[1])
		}
	case "findEmptyBlock":
		return findEmptyBlock(stub)
	}
	return shim.Error(fmt.Sprintf("No function %s implemented", function))
}

func main() {
	if err := shim.Start(new(ApartementRegister)); err != nil {
		fmt.Printf("Error starting SimpleAsset chaincode: %s", err)
	}
}
