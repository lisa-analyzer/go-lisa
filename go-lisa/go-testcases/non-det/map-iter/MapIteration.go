package main

import (
    "fmt"
    "math/rand"
    "time"
	"os"
	"github.com/hyperledger/shim"
)

func Invoke( stub shim.ChaincodeStubInterface ) {
 	returnValue := 0

	var myMap = map[string]int{"a":0, "b":1}

	for i,ii := range myMap{
		returnValue = returnValue * ii
	}
	
	shim.Success([]byte{"value: " + string(returnValue)})
}

func main() {
}