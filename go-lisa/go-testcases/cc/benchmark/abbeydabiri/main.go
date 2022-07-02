package main

import (
	"github.com/hyperledger/fabric-chaincode-go/shim"
	"log"
)

// var logger = shim.NewLogger("HDLChaincode")

// Chaincode entry point
func main() {
	// logger.SetLevel(shim.LogInfo)
	err := shim.Start(new(HDLChaincode))
	if err != nil {
		log.Fatalf("\nError starting HDLChaincode - %v\n", err)
	}

}
