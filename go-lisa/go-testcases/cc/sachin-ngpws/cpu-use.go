package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"time"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	pb "github.com/hyperledger/fabric-protos-go/peer"
)

//SimpleChaincode is the artifact of teh chaincode
type SimpleChaincode struct {
}

var nameSpace string = "org.cpu-use.Usage"

//Usage is the asset
type Usage struct {
	Time time.Time
	CPU  []string
}

func main() {
	err := shim.Start(new(SimpleChaincode))
	if err != nil {
		fmt.Println("Error starting chaincode server")
	}
}

// Init executes at the start
func (c *SimpleChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	fmt.Println("Chaincode Initiated")
	return shim.Success(nil)
}

// Invoke acts as a router
func (c *SimpleChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	fun, args := stub.GetFunctionAndParameters()

	fmt.Println("Executing => " + fun)

	switch fun {
	case "init":
		return c.init(stub, args)
	case "AddCPU":
		return c.AddCPU(stub, args)
	case "AddUsage":
		return c.AddUsage(stub, args)
	case "GetUsage":
		return c.GetUsage(stub, args)
	case "GetHistory":
		return c.GetHistory(stub, args)
	default:
		return shim.Error("Not a vaild function")
	}
}

// init just for instantiate
func (c *SimpleChaincode) init(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	fmt.Println("DONE !!!")
	return shim.Success(nil)
}

//AddCPU register a CPU
func (c *SimpleChaincode) AddCPU(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 1 {
		shim.Error("Incorrect number or arguments")
	}

	name := args[0]
	key, err := stub.CreateCompositeKey(nameSpace, []string{name})

	if err != nil {
		return shim.Error(err.Error())
	}

	usageGet, err := stub.GetState(key)

	if err != nil {
		return shim.Error(err.Error())
	} else if usageGet != nil {
		return shim.Error("Asset already exists")
	}

	usageVal := &Usage{
		Time: time.Now(),
		CPU:  make([]string, 0, 1000),
	}

	usageByte, err := json.Marshal(usageVal)
	if err != nil {
		return shim.Error(err.Error())
	}

	err = stub.PutState(key, usageByte)

	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(usageByte)
}

// GetUsage returns stored value
func (c *SimpleChaincode) GetUsage(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 1 {
		shim.Error("Incorrect number or arguments")
	}

	name := args[0]
	key, err := stub.CreateCompositeKey(nameSpace, []string{name})

	if err != nil {
		return shim.Error(err.Error())
	}

	usageGet, err := stub.GetState(key)

	if err != nil {
		return shim.Error(err.Error())
	} else if usageGet == nil {
		return shim.Error("Empty asset")
	}

	return shim.Success(usageGet)
}

// AddUsage to update the CPU asset
func (c *SimpleChaincode) AddUsage(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 3 {
		shim.Error("Incorrect number or arguments")
	}

	name := args[0]
	key, err := stub.CreateCompositeKey(nameSpace, []string{name})

	if err != nil {
		return shim.Error(err.Error())
	}

	usageGet, err := stub.GetState(key)

	if err != nil {
		return shim.Error(err.Error())
	} else if usageGet == nil {
		return shim.Error("Empty asset")
	}

	var usageVal Usage

	err = json.Unmarshal([]byte(usageGet), &usageVal)

	if err != nil{
		shim.Error("marshling err : "+err.Error())
	}

	usageVal.CPU = []string{args[1], args[2]}

	usageVal.Time = time.Now()

	usageByte, err := json.Marshal(usageVal)
	if err != nil {
		return shim.Error(err.Error())
	}

	err = stub.PutState(key, usageByte)

	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(usageByte)
}

// GetHistory returns entire history of the asset
func (c *SimpleChaincode) GetHistory(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 1 {
		shim.Error("Incorrect number or arguments")
	}

	name := args[0]
	key, err := stub.CreateCompositeKey(nameSpace, []string{name})

	if err != nil {
		return shim.Error(err.Error())
	}

	usageGet, err := stub.GetState(key)

	if err != nil {
		return shim.Error(err.Error())
	} else if usageGet == nil {
		return shim.Error("Empty asset")
	}

	resultsIterator, err := stub.GetHistoryForKey(key)
	if err != nil {
		return shim.Error(err.Error())
	}
	defer resultsIterator.Close()

	var buffer bytes.Buffer
	buffer.WriteString("[")

	arrayWritten := false

	for resultsIterator.HasNext() {
		response, err := resultsIterator.Next()
		if err != nil {
			return shim.Error(err.Error())
		}

		if arrayWritten == true {
			buffer.WriteString(",")
		}
		buffer.WriteString("{\"Value\":")
		if response.IsDelete {
			buffer.WriteString("Deleted")
		} else {
			buffer.WriteString(string(response.Value))
		}
		buffer.WriteString("}")

		arrayWritten = true
	}
	buffer.WriteString("]")

	fmt.Println("History is : " + buffer.String())

	return shim.Success(buffer.Bytes())
}
