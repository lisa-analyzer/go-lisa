package main

import (
    "fmt"
    "math/rand"
    "time"
)

func Invoke( stub shim.ChaincodeStubInterface ) {
 a := stub.GetHistoryForKey("key")
}

func main() {

    fmt.Print(rand.Intn(100))
    fmt.Println(rand.Float64())
    s1 := time.Now().UnixNano()

}