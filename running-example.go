package main

import (
"bytes"
"encoding/json"
"errors"
"fmt"
"strconv"
"github.com/hyperledger/fabric/common/util"
"github.com/hyperledger/fabric/core/chaincode/shim"
pb "github.com/hyperledger/fabric/protos/peer"
)

type Transfer struct {
}

func main() {
err := shim.Start(new(Transfer))
if err != nil {
fmt.Printf("Error starting Simple chaincode: %s", err)
}
}

//Init - shim method
func (t *Transfer) Init(stub shim.ChaincodeStubInterface) pb.Response {
//check arguments length
_, args := stub.GetFunctionAndParameters()
if len(args) != 1 {
return shim.Error("Incorrect number of arguments. Expecting 1")
}
return shim.Success(nil)
}

//Invoke - shim method
func (t *Transfer) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
function, args := stub.GetFunctionAndParameters()
fmt.Println("Invoke is running this function :" + function)
// Handle different functions
if function == "init" { //initialize the chaincode state, used as reset
return t.Init(stub)
} else if function == "invoke" {
return t.invoke(stub, args)
} 

return shim.Error("Received unknown function invocation")
}

func (t *Transfer) invoke(stub shim.ChaincodeStubInterface, args []string) pb.Response {
var A, B string
var Aval, Bval, X int

var Avalbytes, Bvalbytes []byte
A = args[1]

 // ....
    Avalbytes, err := stub.GetState(A)
 // ....
    Aval, _ = strconv.Atoi(string(Avalbytes))
 // ....
    Bval = 2
Bval = Bval + 2
X, err = strconv.Atoi(args[2])
 // ....
    Aval = Aval - X
Bval = Bval + X
    err = stub.PutState(A, []byte(strconv.Itoa(Aval)))
err = stub.PutState(B, []byte(strconv.Itoa(Bval)))
 // ....
    return shim.Success(nil)
}
