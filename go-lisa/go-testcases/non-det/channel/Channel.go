package main

import (

    "github.com/hyperledger/shim"
)

func Invoke( stub shim.ChaincodeStubInterface ) {

    messages := make(chan string)

    go func() { messages <- "ping" }()

    msg := <- messages
    
    stub.PutState("key", msg)
}

func main() {
    

}