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

	f, err := os.Open("file.txt")
    fmt.Print(rand.Intn(100))
    fmt.Println(rand.Float64())
    s1 := time.Now().UnixNano()
	

}