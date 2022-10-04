package main

/**
 * Demonstrates the use of CID
 **/
import (
	// For printing messages on console
	"fmt"
	// Standard go crypto package
	"crypto/x509"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	"github.com/hyperledger/fabric-protos-go/peer"
	// Client Identity Library
	"github.com/hyperledger/fabric-chaincode-go/pkg/cid"
)

// CidChaincode Represents our chaincode object
type CidChaincode struct {
}

// Invoke method
func (clientdid *CidChaincode) Invoke(stub shim.ChaincodeStubInterface) peer.Response {
	// Get the function name and parameters
	funcName, _ := stub.GetFunctionAndParameters()

	// Just to satisfy the compiler - otherwise it will complain that args declared but not used
	// fmt.Println(len(args))

	if funcName == "PrintCallerIDs" {

		// Return JSON
		return clientdid.PrintCallerIDs(stub)

	} else if funcName == "AsssertOnCallersDepartment" {

		// Returns the Trade Rejecton/Approval result
		return clientdid.AssertOnCallersDepartment(stub)

	} else if funcName == "ApproveTrade" {
		// To be coded in an exercise
		// return clientdid.ApproveTrade(stub, args)
	}

	return shim.Error("Bad Func Name!!!")
}

// ReadAttributesOfCaller reads the attributes of the callers cert and return it as JSON
func (clientdid *CidChaincode) PrintCallerIDs(stub shim.ChaincodeStubInterface) peer.Response {

	id, _ := cid.GetID(stub)
	mspid, _ := cid.GetMSPID(stub)
	affiliation, _, _ := cid.GetAttributeValue(stub, "hf.Affiliation")
	enrollID, _, _ := cid.GetAttributeValue(stub, "hf.EnrollmentID")
	userType, _, _ := cid.GetAttributeValue(stub, "hf.Type")
	role, _, _ := cid.GetAttributeValue(stub, "app.accounting.role")
	dep, _, _ := cid.GetAttributeValue(stub, "department")
	// https://godoc.org/crypto/x509#Certificate
	var cert *x509.Certificate
	cert, _ = cid.GetX509Certificate(stub)

	fmt.Println("\nGerID() =             ", id)
	fmt.Println("GetMSPID() =          ", mspid)
	fmt.Println("hf.Affiliation =      ", affiliation)
	fmt.Println("hf.EnrollmentID =     ", enrollID)
	fmt.Println("hf.Type =             ", userType)
	fmt.Println("department =          ", dep)
	fmt.Println("app.accounting.role = ", role)
	fmt.Println("\nCertificate data")
	fmt.Println("Version:              ", cert.Version)
	fmt.Println("Issuer:               ", cert.Issuer)
	fmt.Println("Subject:              ", cert.Subject)
	fmt.Println("NotBefore:            ", cert.NotBefore)
	fmt.Println("NotAfter:             ", cert.NotAfter)
	return shim.Success([]byte("ok"))
}

// AssertOnCallersDepartment uses the cid AsssertAttributeValue
// Rule = Only a caller with department=accounting can invoke this function
func (clientdid *CidChaincode) AssertOnCallersDepartment(stub shim.ChaincodeStubInterface) peer.Response {

	// Get the enrollID and dept
	enrollID, _, _ := cid.GetAttributeValue(stub, "hf.EnrollmentID")
	dept, _, _ := cid.GetAttributeValue(stub, "department")

	// We can use if statement or Assert call to check the rule
	// Check if the department attribute is set to "accounting)"
	err := cid.AssertAttributeValue(stub, "department", "accounting")

	// Check if valid err returned
	if err != nil {
		return shim.Error("Access Denied to " + enrollID + " from " + dept + " !!!")
	}

	// Return success
	return shim.Success([]byte("Access Granted to " + enrollID + " from " + dept))
}

// SetJSONNV returns a name value pair in JSON format
func SetJSONNV(attr, value string) string {
	return " \"" + attr + "\":\"" + value + "\""
}

// Init Implements the Init method
func (clientdid *CidChaincode) Init(stub shim.ChaincodeStubInterface) peer.Response {
	// Simply print a message
	// fmt.Println("Init executed in history")

	// Return success
	return shim.Success(nil)
}

// Chaincode registers with the Shim on startup
func main() {
	fmt.Printf("Started Chaincode. cid\n")
	err := shim.Start(new(CidChaincode))
	if err != nil {
		fmt.Printf("Error starting chaincode: %s", err)
	}
}
