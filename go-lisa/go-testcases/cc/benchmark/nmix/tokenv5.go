package main

/**
 * tokenv5
 * Shows the use of ChaincodeStub API for getting & setting key/value pairs
 **/
import (
	"fmt"
	// Conversion functions - needed to convert MyToken to Integer
	"strconv"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	"github.com/hyperledger/fabric-protos-go/peer"
)

// TokenChaincode Represents our chaincode object
type TokenChaincode struct {
}

// Init Implements the Init method
func (token *TokenChaincode) Init(stub shim.ChaincodeStubInterface) peer.Response {

	// Simply print a message
	fmt.Println("Init executed")

	// Lets create a Key-Value pair
	stub.PutState("MyToken", []byte("2000"))

	// Return success
	return shim.Success([]byte("true"))
}

// Invoke method
func (token *TokenChaincode) Invoke(stub shim.ChaincodeStubInterface) peer.Response {

	funcName, _ := stub.GetFunctionAndParameters()
	fmt.Println("Function=", funcName)

	// V5
	if funcName == "set" {
		// Sets the value
		return SetToken(stub)

	} else if funcName == "get" {

		// Gets the value
		return GetToken(stub)

	} else if funcName == "delete" {
		return DeleteToken(stub)
	}

	// This is not good
	return shim.Error(("Bad Function Name = " + funcName + "!!!"))
}

// SetToken inrements the value of the token by 10
// V5
// Returns true if successful
func SetToken(stub shim.ChaincodeStubInterface) peer.Response {

	// Get the current value
	value, err := stub.GetState("MyToken")

	// If there is error in retrieve send back an error response
	if err != nil {
		return shim.Error(err.Error())
	}

	// Convert value to integer
	intValue, err := strconv.Atoi(string(value))

	// If there is an error in conversion - return false
	if err != nil {
		// May also return sh.Error
		return shim.Success([]byte("false"))
	}

	// Increment the value by 10
	intValue += 10

	value = []byte(strconv.Itoa(intValue))

	// Execute PutState - overwrites the current value
	stub.PutState("MyToken", value)

	stub.SetEvent("TokenValueChanged", value)

	return shim.Success([]byte("true"))
}

// GetToken reads the value of the token from the database
// V5
// Reurns the value or -1 in case MyToken doesn't exist
func GetToken(stub shim.ChaincodeStubInterface) peer.Response {
	// Holds a string for the response
	var myToken string

	// Local variables for value & error
	var value []byte
	var err error

	if value, err = stub.GetState("MyToken"); err != nil {

		fmt.Println("Get Failed!!! ", err.Error())

		return shim.Error(("Get Failed!! " + err.Error() + "!!!"))

	}

	// nil indicates non existent key
	if value == nil {
		// Return value -1 is to indicate to caller that MyToken
		// Does NOT exist in state data
		myToken = "-1"

	} else {

		myToken = "MyToken=" + string(value)

	}

	return shim.Success([]byte(myToken))
}

func DeleteToken(stub shim.ChaincodeStubInterface) peer.Response {
	val, err := stub.GetState("MyToken")
	if val == nil {
		return shim.Success([]byte("false"))
	}
	err = stub.DelState("MyToken")
	if err != nil {
		return shim.Error("DelState Failed " + err.Error())
	}
	return shim.Success([]byte("true"))
}

// Chaincode registers with the Shim on startup
func main() {
	fmt.Printf("Started Chaincode. tokenv5\n")
	err := shim.Start(new(TokenChaincode))
	if err != nil {
		fmt.Printf("Error starting chaincode: %s", err)
	}
}
