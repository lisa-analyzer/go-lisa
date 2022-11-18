package main

import (
	"encoding/json"
	"bytes"
	"fmt"
	"time"
	"os"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	pb "github.com/hyperledger/fabric-protos-go/peer"
)

type SimpleChaincode struct{
}

var name_space string = "org.cpu-use.Usage"

type Usage struct{
	Time time.Time
	Cpu []string
}

func main() {

	ccid := os.Getenv("CHAINCODE_ID")
	if ccid == "" {
		fmt.Println("No Chaincode ID")
	} else {
		fmt.Println("ID : "+ccid)
	}
	add := os.Getenv("CHAINCODE_SERVER_ADDRESS")
	if add == "" {
		fmt.Println("No Address assigned")
	} else {
		fmt.Println("ADD : "+add)
	}
	chaincode := new(SimpleChaincode)
	server := &shim.ChaincodeServer{
		CCID: ccid,
		Address: add,
		CC: chaincode,
		TLSProps: shim.TLSProperties{
			Disabled: true,
		},
	}
	err := server.Start()
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

	fmt.Println("Executing => "+fun)

	switch fun{
	case "init":
		return c.init(stub,args)
	case "AddCpu":
		return c.AddCpu(stub,args)
	case "AddUsage":
		return c.AddUsage(stub,args)
	case "GetUsage":
		return c.GetUsage(stub,args)
	case "GetHistory":
		return c.GetHistory(stub,args)
	default:
		return shim.Error("Not a vaild function")	
	}
}

// init just for instantiate
func (c *SimpleChaincode) init(stub shim.ChaincodeStubInterface, args []string) pb.Response{
	fmt.Println("DONE !!!")
	return shim.Success(nil)
}

//AddCpu register a cpu
func (c *SimpleChaincode) AddCpu(stub shim.ChaincodeStubInterface, args []string) pb.Response{
	if len(args) != 1 {
		shim.Error("Incorrect number or arguments")
	}

	name := args[0]
	key, err:= stub.CreateCompositeKey(name_space,[]string{name})

	if err != nil {
		return shim.Error(err.Error())
	}

	usageGet, err:= stub.GetState(key)

	if err != nil {
		return shim.Error(err.Error())
	} else if usageGet != nil {
		return shim.Error("Asset already exists")
	}

	usageVal := &Usage{
		Time: time.Now(),
		Cpu: make([]string,0,1000),
	}

	usageByte, err := json.Marshal(usageVal)
	if err != nil{
		return shim.Error(err.Error())
	}

	err = stub.PutState(key,usageByte)

	if err != nil{
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
	key, err:= stub.CreateCompositeKey(name_space,[]string{name})

	if err != nil {
		return shim.Error(err.Error())
	}

	usageGet, err:= stub.GetState(key)

	if err != nil {
		return shim.Error(err.Error())
	} else if usageGet == nil {
		return shim.Error("Empty asset")
	}

	return shim.Success(usageGet)
}

// AddUsage to update the cpu asset
func (c *SimpleChaincode) AddUsage(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 3 {
		shim.Error("Incorrect number or arguments")
	}

	name := args[0]
	key, err:= stub.CreateCompositeKey(name_space,[]string{name})

	if err != nil {
		return shim.Error(err.Error())
	}

	usageGet, err:= stub.GetState(key)

	if err != nil {
		return shim.Error(err.Error())
	} else if usageGet == nil {
		return shim.Error("Empty asset")
	}

	var usageVal Usage

	err = json.Unmarshal([]byte(usageGet), &usageVal)

	usageVal.Cpu = append(usageVal.Cpu,args[1],args[2])

	usageVal.Time = time.Now()

	usageByte, err := json.Marshal(usageVal)
	if err != nil{
		return shim.Error(err.Error())
	}

	err = stub.PutState(key,usageByte)

	if err != nil{
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
	key, err:= stub.CreateCompositeKey(name_space,[]string{name})

	if err != nil {
		return shim.Error(err.Error())
	}

	resultsIterator, err := stub.GetHistoryForKey(key)
	if err != nil {
		return shim.Error(err.Error())
	}
	defer resultsIterator.Close()

	var buffer bytes.Buffer
	buffer.WriteString("[")

	arrayWritten := false

	for resultsIterator.HasNext(){
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

	fmt.Println("History is : "+buffer.String())

	return shim.Success(buffer.Bytes())
}