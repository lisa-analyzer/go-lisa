package main

import (
	"fmt"

	"github.com/hyperledger/fabric/common/util"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)

type example struct{}

func (e *example) Init(stub shim.ChaincodeStubInterface) pb.Response {
	fmt.Println("Successsssssssssss")
	return shim.Success(nil)
}

func (e *example) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	function, args := stub.GetFunctionAndParameters()
	fmt.Println("inside the invoke function")
	if function == "callothercc" {
		fmt.Println("inside the if statement")
		/*x := c.PrintThis(stub, args)
		fmt.Println("After the function call")*/
		ccname := args[0]
		//x := strings.Join(args[2], ",")
		chainCodeArgs := util.ToChaincodeArgs("printThis", args[1])
		fmt.Println("calling the other chaincode")
		response := stub.InvokeChaincode(ccname, chainCodeArgs, "myc")

		if response.Status != shim.OK {
			return shim.Error(response.Message)
		}
		return shim.Success(nil)

	}
	fmt.Println("outside if")

	return shim.Success(nil)
}

func main() {
	err := shim.Start(new(example))
	if err != nil {
		fmt.Println("Unable to start the chaincode")
	}

}
