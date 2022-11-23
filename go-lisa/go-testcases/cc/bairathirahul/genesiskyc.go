package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"strconv"
	"time"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)

/*
	Personas -
		BankX
		BankY
		FinCEN
		Embassy
		Administrator
		Customer
*/

//GenesisChainCode Chaincode Interface...
type GenesisChainCode struct{}

//BasicInfo - Basic Information of the customer
type BasicInfo struct {
	Salutation  string
	FirstName   string
	MiddleName  string
	LastName    string
	Suffix      string
	DateOfBirth uint64
	Gender      string
	SSN         string
	Passport    string
	Type        string // Customer type
	//Mandatory   []String
}

//Address - Addresses of houses owned and rented by the customer
type Address struct {
	AddID   int
	Street1 string
	Street2 string
	City    string
	State   string
	Country string
	Zip     string
	Deleted bool
	//Mandatory   []String
}

//Contact - Contact information of the customer
type Contact struct {
	PrimaryContact string
	HomeNumber     string //change when address change
	WorkNumber     string //change when employment change
	MobileNumber   string
	EmailAddress   string
	Operator       string
	//Mandatory      []String
}

//Employment - Current employment and work history of the customer
type Employment struct {
	EmpID          int
	EmploymentType string
	CompanyName    string
	Street1        string
	Street2        string
	City           string
	State          string
	Country        string
	Zip            string
	Designation    string
	StartDate      uint64
	EndDate        uint64
	IsCurrent      bool
	GrossSalary    int
	Deleted        bool
	//Mandatory      []String
}

//BankAccount - Bank accounts (Savings, Checkings, Loan) accounts owned by the customer
type BankAccount struct {
	AccID       int
	AccountType string
	BankName    string
	AccountNo   string
	RoutingNo   string
	Deleted     bool
	Closed      bool
	//Mandatory   []String
}

//Document - Documents uploaded by the customer
type Document struct {
	//if req by bank ssn mandatory
	//if req by embassy ssn passport other visa mandatory
	//ssn mandatory
	//if address/contact/employment/account added  document mandatory
	DocumentType string
	DocumentID   int
	DocumentDesc string
	Deleted      bool
	//Mandatory   []String
}

//BankTransaction - Store transactions performed by the customer's bank account
type BankTransaction struct {
	//BankID		string	// BankID (to be used later)
	//BankName		string	// BankName (to be used later)
	TransactionID   string
	TransactionDate uint64
	TransactionType string
	Description     string
	Amount          float64
	Flagged         bool // default false
}

//Comment - Store comments sent by the administrator while verification
type Comment struct {
	Created int64  // Comment Date
	Text    string // Comment text
}

//Customer - Bringing it all together
type Customer struct {
	ID               string              // Customer ID
	BasicInfo        BasicInfo           // Basic Information
	Addresses        map[int]Address     // Addresses
	Contact          Contact             // Contact Information
	Employments      map[int]Employment  // Employment History
	Documents        map[int]Document    // Customer Documents (only identifiers)
	BankAccounts     map[int]BankAccount // Bank Accounts
	BankTransactions []BankTransaction   // Bank Transactions
	Accesses         map[string]bool     // Access requests of personas
	Options          map[string]string   // Options like like Auto Increment values
	Comments         []Comment           // Administrator comments
	Rating           int                 // Customer rating for CDD
	Mandatory        []string            // Mandatory fields (as required by the personas, to be used later)
	Status           string              // Customer verification status
}

//Main - Entry point of the chaincode
func main() {
	// Instantiate chaincode
	err := shim.Start(new(GenesisChainCode))
	if err != nil {
		fmt.Printf("Error occurred in starting chaincode: %s", err)
	} else {
		fmt.Printf("Chaincode started successfully")
	}
}

//Init - Initializes chaincode, nothing to do in this
func (t *GenesisChainCode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	return shim.Success(nil)
}

//Invoke Chaincode methods
func (t *GenesisChainCode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	//Retrieve the requested Smart Contract function and arguments
	function, args := stub.GetFunctionAndParameters()

	//Route to the appropriate handler function to interact with the ledger appropriately
	if function == "updateCustomer" {
		return t.updateCustomer(stub, args)
	} else if function == "insertCustomerTransaction" {
		return t.insertCustomerTransaction(stub, args)
	} else if function == "requestCustomerAccess" {
		return t.requestCustomerAccess(stub, args)
	} else if function == "approveCustomerAccess" {
		return t.approveCustomerAccess(stub, args)
	} else if function == "updateCustomerStatus" {
		return t.updateCustomerStatus(stub, args)
	} else if function == "queryCustomer" {
		return t.queryCustomer(stub, args)
	} else if function == "queryCustomerTransactionHistory" {
		return t.queryCustomerTransactionHistory(stub, args)
	} else if function == "searchCustomer" {
		return t.searchCustomer(stub, args)
	} else if function == "searchPendingCustomer" {
		return t.searchPendingCustomer(stub, args)
	}

	return shim.Error("Invalid Smart Contract function name.")
}

//=====================================
//Update Customer
//This method updates the Basic Information, Addresses, Contact, Employment,
//Bank Accounts and Documents of the customer. It also creates the customer
//with the given ID
//args[0] = Structure field to be updated
//args[1] = Customer ID
//args[2] = Value of the field (JSON)
//=====================================
func (t *GenesisChainCode) updateCustomer(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 3 {
		return shim.Error("Incorrect number of arguments. Expecting 3")
	}

	info := args[0]

	customer := Customer{}
	customerJSONAsBytes, err := stub.GetState(args[1])
	if err != nil {
		return shim.Error("Error connecting to the database")
	} else if len(customerJSONAsBytes) == 0 {
		// Create new customer with the given ID
		customer.ID = args[1]
	} else if len(customerJSONAsBytes) > 0 {
		// Unmarshal existing customer
		json.Unmarshal(customerJSONAsBytes, &customer)
	}

	// Set the updated flag for re-verification
	customer.Status = "updated"

	// Initializes the Options structure
	if customer.Options == nil {
		customer.Options = map[string]string{}
	}

	fmt.Println("CHAINCODE: After Unmarshalling customer")

	switch {
	case info == "BasicInfo":
		// Unmarshall BasicInfo
		basicInfo := BasicInfo{}
		json.Unmarshal([]byte(args[2]), &basicInfo)
		fmt.Println("CHAINCODE: After Unmarshalling basicInfo")

		// Update BasicInfo
		customer.BasicInfo = basicInfo

	case info == "Addresses":
		// Initializes address structure
		if customer.Addresses == nil {
			customer.Addresses = map[int]Address{}
		}

		// Unmarshal Addresses
		addresses := []Address{}
		json.Unmarshal([]byte(args[2]), &addresses)
		fmt.Println("CHAINCODE: After Unmarshalling addresses")
		fmt.Println(addresses)

		// Find the Max ID corresponding to this customer
		maxID := 0
		maxIDStr, ok := customer.Options["max_add_id"]
		if ok {
			maxID, err = strconv.Atoi(maxIDStr)
		}

		for _, address := range addresses {
			// Delete the address
			if address.Deleted == true {
				_, ok := customer.Addresses[address.AddID]
				if ok {
					delete(customer.Addresses, address.AddID)
				}
			} else {
				if address.AddID == 0 {
					// Use MaxID if it is a new address
					maxID++
					address.AddID = maxID
				}
				// Set the address
				customer.Addresses[address.AddID] = address
			}
		}
		// Update the MaxID
		customer.Options["max_add_id"] = strconv.Itoa(maxID)

	case info == "Contact":
		// Unmarshal contact information
		contact := Contact{}
		json.Unmarshal([]byte(args[2]), &contact)
		fmt.Println("CHAINCODE: After Unmarshalling contact")
		// Update the contact information
		customer.Contact = contact

	case info == "BankAccounts":
		// Initializes Bank Accounts structure
		if customer.BankAccounts == nil {
			customer.BankAccounts = map[int]BankAccount{}
		}

		// Unmarshal bank Accounts
		bankAccounts := []BankAccount{}
		json.Unmarshal([]byte(args[2]), &bankAccounts)
		fmt.Println("CHAINCODE: After Unmarshalling bankAccounts")

		// Find the Max ID corresponding to this customer
		maxID := 0
		maxIDStr, ok := customer.Options["max_acc_id"]
		if ok {
			maxID, err = strconv.Atoi(maxIDStr)
		}

		for _, bankAccount := range bankAccounts {
			// Delete the Bank Account
			if bankAccount.Deleted == true {
				_, ok := customer.BankAccounts[bankAccount.AccID]
				if ok {
					delete(customer.BankAccounts, bankAccount.AccID)
				}
			} else {
				if bankAccount.AccID == 0 {
					// Use the MaxID if it is a new account
					maxID++
					bankAccount.AccID = maxID
				}
				// Set the account
				customer.BankAccounts[bankAccount.AccID] = bankAccount
			}
		}
		// Update the MaxID
		customer.Options["max_acc_id"] = strconv.Itoa(maxID)

	case info == "Employments":
		// Initializes Employments Structure
		if customer.Employments == nil {
			customer.Employments = map[int]Employment{}
		}

		// UnMarshal employment
		employments := []Employment{}
		fmt.Println(args[2])
		json.Unmarshal([]byte(args[2]), &employments)
		fmt.Println("CHAINCODE: After Unmarshalling employments")

		// Find the Max ID corresponding to this customer
		maxID := 0
		maxIDStr, ok := customer.Options["max_emp_id"]
		if ok {
			maxID, err = strconv.Atoi(maxIDStr)
		}

		for _, employment := range employments {
			// Delete the Employment
			if employment.Deleted == true {
				_, ok := customer.Employments[employment.EmpID]
				if ok {
					delete(customer.Employments, employment.EmpID)
				}
			} else {
				if employment.EmpID == 0 {
					// Use the MaxID if new employment
					maxID++
					employment.EmpID = maxID
				}
				// Set employment
				customer.Employments[employment.EmpID] = employment
			}
		}
		// Update the MaxID
		customer.Options["max_emp_id"] = strconv.Itoa(maxID)

	case info == "Documents":
		// Initializes the Documents structure
		if customer.Documents == nil {
			customer.Documents = map[int]Document{}
		}

		// Unmarshal Documents
		documents := []Document{}
		json.Unmarshal([]byte(args[2]), &documents)
		fmt.Println("CHAINCODE: After Unmarshalling documents")

		for _, document := range documents {
			// Delete the document
			if document.Deleted == true {
				_, ok := customer.Documents[document.DocumentID]
				if ok {
					delete(customer.Documents, document.DocumentID)
				}
			} else {
				// Add / Update document
				customer.Documents[document.DocumentID] = document
			}
		}

	default:
		fmt.Println("Good evening.")
	}

	// Update customer
	fmt.Println("CHAINCODE: Writing customer back to ledger")
	jsonAsBytes, _ := json.Marshal(customer)
	err = stub.PutState(customer.ID, jsonAsBytes)
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(jsonAsBytes)
}

//=====================================
//Request Customer Access
//This method will be executed by Persona (except Administrator)
//to request access to the data from the customer
//args[0] = CustomerID
//args[1] = Persona ID
//=====================================
func (t *GenesisChainCode) requestCustomerAccess(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Expecting 2")
	}

	// Read the Customer
	customer := Customer{}
	customerJSONAsBytes, err := stub.GetState(args[0])
	if err != nil {
		return shim.Error("Error connecting to the database")
	} else if len(customerJSONAsBytes) == 0 {
		return shim.Error("Customer not found")
	} else if len(customerJSONAsBytes) > 0 {
		json.Unmarshal(customerJSONAsBytes, &customer)
	}

	// Validate necessary fields (for later)
	/*var validSSN = regexp.MustCompile(`^.*bank.*$`)
	var validPassport = regexp.MustCompile(`^.*embassy.*$`)
	switch {
	case validSSN.MatchString(args[2]) == true:
		customer.Mandatory = append(customer.Mandatory, "ssn")
		fmt.Println("Documents mandatory for Bank Account for verification")
	case validPassport.MatchString(args[2]) == true:
		customer.Mandatory = append(customer.Mandatory, "passport")
		fmt.Println("Documents mandatory for Embassy for verification")
	default:
		fmt.Println("DEFAULT")
	} */

	// Initializes the access structure
	if customer.Accesses == nil {
		customer.Accesses = map[string]bool{}
	}
	// Set access request
	customer.Accesses[args[1]] = false

	// Update customer
	fmt.Println("CHAINCODE: Writing customer back to ledger")
	jsonAsBytes, _ := json.Marshal(customer)
	err = stub.PutState(customer.ID, jsonAsBytes)
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(jsonAsBytes)
}

//=====================================
//Approve Customer Access
//This method will be executed by the customer to approve
//persona request to access its data
//args[0] = CustomerID
//args[1] = Persona ID
//=====================================
func (t *GenesisChainCode) approveCustomerAccess(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Expecting 2")
	}

	// Read customer
	customer := Customer{}
	customerJSONAsBytes, err := stub.GetState(args[0])
	if err != nil {
		return shim.Error("Error connecting to the database")
	} else if len(customerJSONAsBytes) == 0 {
		return shim.Error("Customer not found")
	} else if len(customerJSONAsBytes) > 0 {
		json.Unmarshal(customerJSONAsBytes, &customer)
	}

	// Set access to true
	customer.Accesses[args[1]] = true

	// Update customer
	fmt.Println("CHAINCODE: Writing customer back to ledger")
	jsonAsBytes, _ := json.Marshal(customer)
	err = stub.PutState(customer.ID, jsonAsBytes)
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(jsonAsBytes)
}

//=====================================
//Insert Customer Transaction
//This method inserts Bank Transaction in the customer structure
//It also flags transaction on the go
//args[0] = CustomerID
//args[1] = Bank Transaction
//=====================================
func (t *GenesisChainCode) insertCustomerTransaction(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Expecting 2")
	}

	// Read customer
	customer := Customer{}
	customerJSONAsBytes, err := stub.GetState(args[0])
	if err != nil {
		return shim.Error("Error connecting to the database")
	} else if len(customerJSONAsBytes) == 0 {
		return shim.Error("Customer not found")
	} else if len(customerJSONAsBytes) > 0 {
		json.Unmarshal(customerJSONAsBytes, &customer)
	}

	// Initialize Bank Transaction structure
	if customer.BankTransactions == nil {
		customer.BankTransactions = []BankTransaction{}
	}

	// Unmarshall bank transaction
	bankTransaction := BankTransaction{}
	json.Unmarshal([]byte(args[1]), &bankTransaction)
	fmt.Println("CHAINCODE: After unmarshalling bank transaction")

	// Add bank transaction
	bankTransaction.Flagged = bankTransaction.Amount >= 3000.0
	customer.BankTransactions = append(customer.BankTransactions, bankTransaction)

	// Risk rating
	if bankTransaction.Flagged {
		customer.Rating++
	}

	// Update customer
	fmt.Println("CHAINCODE: Writing customer back to ledger")
	jsonAsBytes, _ := json.Marshal(customer)
	err = stub.PutState(customer.ID, jsonAsBytes)
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(jsonAsBytes)
}

//=====================================
//Verify customer
//This method will update the customer status and add a comment
//to the profile
//args[0] = Cutomer ID
//args[1] = Status
//args[2] = Comment
//=====================================
func (t *GenesisChainCode) updateCustomerStatus(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 3 {
		return shim.Error("Incorrect number of arguments. Expecting 3")
	}

	// Read customer
	customer := Customer{}
	customerJSONAsBytes, err := stub.GetState(args[0])
	if err != nil {
		return shim.Error("Error connecting to the database")
	} else if len(customerJSONAsBytes) == 0 {
		return shim.Error("Customer not found")
	} else if len(customerJSONAsBytes) > 0 {
		json.Unmarshal(customerJSONAsBytes, &customer)
	}

	// Initialize comments structure
	if customer.Comments == nil {
		customer.Comments = []Comment{}
	}

	// Set status and comment
	customer.Status = args[1]
	if args[2] != "" {
		comment := Comment{}
		comment.Created = time.Now().UnixNano() / int64(time.Millisecond)
		comment.Text = args[2]
		customer.Comments = append(customer.Comments, comment)
	}

	// Update customer
	fmt.Println("CHAINCODE: Writing customer back to ledger")
	jsonAsBytes, _ := json.Marshal(customer)
	err = stub.PutState(customer.ID, jsonAsBytes)
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(jsonAsBytes)
}

//=====================================
//Query Customer
//This method can be used to query customer using its ID
//args[0] - Customer ID
//=====================================
func (t *GenesisChainCode) queryCustomer(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	// Read state with ID
	jsonAsBytes, err := stub.GetState(args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	fmt.Println(string(jsonAsBytes))
	return shim.Success(jsonAsBytes)
}

//=====================================
//Search Customer
//This method will search the data of the customer by his SSN or Email Address
//args[0] - Search Query
//=====================================
func (t *GenesisChainCode) searchCustomer(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) < 1 {
		return shim.Error("Incorrect number of arguments. Expecting 1")
	}

	// Need to skip these as leveldb does not support complex queries
	//var query = fmt.Sprintf("{\"selector\":{\"$or\":[{\"BasicInfo.SSN\":\"%s\"},{\"BasicInfo.Passport\":\"%s\"},{\"Contact.EmailAddress\":\"%s\"}]}}", args[0], args[0], args[0])
	//queryResults, err := getQueryResultForQueryString(stub, query)

	// Pick all customers in the range (need to implement alternative suggested by Mark)
	resultsIterator, err := stub.GetStateByRange("0", "99999")
	if err != nil {
		return shim.Error(err.Error())
	}
	defer resultsIterator.Close()

	// Create buffer of results
	var buffer bytes.Buffer
	buffer.WriteString("[")
	var first = true
	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		if err != nil {
			return shim.Error(err.Error())
		}

		// Unmarshal customer
		customer := Customer{}
		json.Unmarshal(queryResponse.Value, &customer)

		// Select customers according to the search query
		if customer.BasicInfo.SSN == args[0] || customer.BasicInfo.Passport == args[0] || customer.Contact.EmailAddress == args[0] {
			if !first {
				buffer.WriteString(",")
			}
			first = false
			buffer.WriteString(string(queryResponse.Value))
		}
	}
	buffer.WriteString("]")

	return shim.Success(buffer.Bytes())
}

//=====================================
//Search Pending Customer
//This method will return all customers who are pending for verification
//=====================================
func (t *GenesisChainCode) searchPendingCustomer(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	// Need to skip these as leveldb does not support complex queries
	// var query = "{\"selector\": {\"Status\": \"updated\"}}"
	// queryResults, err := getQueryResultForQueryString(stub, query)

	// Pick all customers in the range (need to implement alternative suggested by Mark)
	resultsIterator, err := stub.GetStateByRange("0", "99999")
	if err != nil {
		return shim.Error(err.Error())
	}
	defer resultsIterator.Close()

	// Create buffer of results
	var buffer bytes.Buffer
	buffer.WriteString("[")
	var first = true
	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		if err != nil {
			return shim.Error(err.Error())
		}

		// Unmarshal customer
		customer := Customer{}
		json.Unmarshal(queryResponse.Value, &customer)
		fmt.Println(customer)

		// Select customers with status = updated
		if customer.Status == "updated" {
			if !first {
				buffer.WriteString(",")
			}
			first = false
			buffer.WriteString(string(queryResponse.Value))
		}
	}
	buffer.WriteString("]")

	return shim.Success(buffer.Bytes())
}

//=====================================
//Query Customer's Transaction
//This method will return the Transaction data of the customer for banks to perform customer Due diligence. Apart from the customer ID
//the type of requested information must also be provided. It will only return
//the type of information requested
//TransactionID   string
//	TransactionDate uint//32
//	TransactionType string
//	Description     string
//	Amount          float64
//=====================================
func (t *GenesisChainCode) queryCustomerTransactionHistory(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	if len(args) < 1 {
		return shim.Error("Incorrect number of arguments. Expecting 1")
	}

	customerID := args[0]

	fmt.Printf("- start getHistoryForCustomer: %s\n", customerID)
	//CHECK IF NEED TO FIND ALL CUSTOMERS
	resultsIterator, err := stub.GetHistoryForKey(customerID)
	if err != nil {
		return shim.Error(err.Error())
	}
	defer resultsIterator.Close()

	// buffer is a JSON array containing historic values for the marble
	var buffer bytes.Buffer
	buffer.WriteString("[")

	bArrayMemberAlreadyWritten := false
	for resultsIterator.HasNext() {
		response, err := resultsIterator.Next()
		if err != nil {
			return shim.Error(err.Error())
		}
		// Add a comma before array members, suppress it for the first array member
		if bArrayMemberAlreadyWritten == true {
			buffer.WriteString(",")
		}
		buffer.WriteString("{\"TxId\":")
		buffer.WriteString("\"")
		buffer.WriteString(response.TxId)
		buffer.WriteString("\"")

		buffer.WriteString(", \"Value\":")
		// if it was a delete operation on given key, then we need to set the
		//corresponding value null. Else, we will write the response.Value
		//as-is (as the Value itself a JSON marble)
		if response.IsDelete {
			buffer.WriteString("null")
		} else {
			buffer.WriteString(string(response.Value))
		}

		buffer.WriteString(", \"Timestamp\":")
		buffer.WriteString("\"")
		buffer.WriteString(time.Unix(response.Timestamp.Seconds, int64(response.Timestamp.Nanos)).String())
		buffer.WriteString("\"")

		buffer.WriteString(", \"IsDelete\":")
		buffer.WriteString("\"")
		buffer.WriteString(strconv.FormatBool(response.IsDelete))
		buffer.WriteString("\"")

		buffer.WriteString("}")
		bArrayMemberAlreadyWritten = true
	}
	buffer.WriteString("]")

	fmt.Printf("- getHistoryForCustomer returning:\n%s\n", buffer.String())

	return shim.Success(buffer.Bytes())
}

// =========================================================================================
// getQueryResultForQueryString executes the passed in query string.
// Result set is built and returned as a byte array containing the JSON results.
// =========================================================================================
func getQueryResultForQueryString(stub shim.ChaincodeStubInterface, queryString string) ([]byte, error) {

	fmt.Printf("- getQueryResultForQueryString queryString:\n%s\n", queryString)

	resultsIterator, err := stub.GetQueryResult(queryString)
	if err != nil {
		return nil, err
	}
	defer resultsIterator.Close()

	// Create buffer of results
	var buffer bytes.Buffer
	buffer.WriteString("[")
	var first = true
	for resultsIterator.HasNext() {
		if !first {
			buffer.WriteString(",")
		}
		first = false

		queryResponse, err := resultsIterator.Next()
		if err != nil {
			return nil, err
		}
		buffer.WriteString(string(queryResponse.Value))
	}
	buffer.WriteString("]")

	fmt.Printf("Result: %s", buffer.String())
	return buffer.Bytes(), nil
}
