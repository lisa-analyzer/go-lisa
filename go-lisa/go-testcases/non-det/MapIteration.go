package main

import (
    "fmt"
    "math/rand"
    "time"
	"os"
)

func Invoke( stub shim.ChaincodeStubInterface ) {
 	returnValue := 0

	var myMap = map[int]int{}

	for i,ii := range myMap{
		returnValue = returnValue * i - ii
	}
	
	shim.Success([]byte("value: " + string(returnValue)))
}

func main() {

    messages := make(chan string)

    go func() { messages <- "ping" }()

    msg := <-messages
    
    shim.Success([]byte("value: " + string(msg)))
}