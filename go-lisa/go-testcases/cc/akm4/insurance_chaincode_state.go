package main

import (
	"encoding/json"
	"errors"
	"fmt"
	//"strconv"
	"time"
	//"strings"

	//"os"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)

var (
	// prefix for saving person data
	personPrfx = "Person:"
	// prefix for saving history of person
	personHistoryPrfx = "PersonHistory:"
	//prefix for saving serches for person
	personSearchPrfx = "PersonSearch:"
	logger           = shim.NewLogger("insurance")
)

const ACTION_INSERT = "create"
const ACTION_UPDATE = "update"
//const ACTION_DELETE = "delete"
//const ACTION_SEARCH = "search"

//const STATUS_OK = "trusted"
//const STATUS_SUSP = "banned"
//const STATUS_DELETED = "wrong-data"
const STATUS_NOT_FOUND = "not-initialized"

// SimpleChaincode example simple Chaincode implementation
type SimpleChaincode struct {
}

//type for audit record
type Action struct {
	Company string    `json:"company"`
	User    string    `json:"user"`
	Date    time.Time `json:"date"`
	Status  string    `json:"status"`
	Method  string    `json:"method"`
}

//type for search record
type SearchResult struct {
	Company string    `json:"company"`
	User    string    `json:"user"`
	Date    time.Time `json:"date"`
	Status  string    `json:"status"`
}

//type for person data
type Person struct {
	Hash       string    `json:"hash"`
	Status     string    `json:"status"`
	ModifyDate time.Time `json:"modifyDate"`
}

//---------------------------------------------------- MAIN
func main() {

	logger.SetLevel(shim.LogDebug)
	//TODO fix to env variable, now constant
	logLevel, _ := shim.LogLevel("DEBUG")
	shim.SetLoggingLevel(logLevel)
	err := shim.Start(new(SimpleChaincode))
	if err != nil {
		fmt.Printf("Error starting Simple chaincode: %s", err)
		logger.Errorf("Error starting Simple chaincode: %s", err)
	}
}

//Init - shim method
func (t *SimpleChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	_, args := stub.GetFunctionAndParameters()
	//check arguments length
	if len(args) != 1 {
		shim.Error("Incorrect number of arguments. Expecting 1")
	}
	return shim.Success(nil)
}

//Query - shim method
func (t *SimpleChaincode) Query(stub shim.ChaincodeStubInterface, function string, args []string) ([]byte, error) {
	// Handle different functions

	fmt.Println("query did not find func: " + function)
	logger.Errorf("query did not find func%s:", function)
	return nil, errors.New("Received unknown function query")
}

//Invoke - shim method
func (t *SimpleChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	function, args := stub.GetFunctionAndParameters()
	fmt.Println("Invoke is running this function :" + function)
	logger.Infof("Invoke is running this function : %s", function)
	// Handle different functions
	if function == "init" { //initialize the chaincode state, used as reset
		return t.Init(stub)
		//////// write state functions
	} else if function == "write" {
		return t.write(stub, args)
	} else if function == "insertPerson" { //create a new person
		return t.insertPerson(stub, args)
	} else if function == "updatePerson" { // update a person
		return t.updatePerson(stub, args)
	} else if function == "searchPerson" {
		return t.searchPerson(stub, args)
		/// read  state functions
	} else if function == "getPersonInfo" { //read person by hash
		return t.getPersonInfo(stub, args)
	} else if function == "read" { // read data by name from state
		return t.read(stub, args)
	} else if function == "getPersonHistory" { // read history of person from state
		return t.getPersonHistory(stub, args)
	} else if function == "getPersonSearches" {
		return t.getPersonSearches(stub, args)
	} else if function == "getPersonHistoryIter" {
		return t.getPersonHistoryIter(stub, args)
		////// util functions
	} else if function == "setLoggingLevel" {
		return t.setLoggingLevel(stub, args)
		/// read-write function
	} else if function == "searchPersonAndReturn" {
		return t.searchPersonAndReturn(stub, args)
	}

	return shim.Error("Received unknown function invocation")
}
func (t *SimpleChaincode) getPersonHistoryIter(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	var history []string
	//parse parameters  - need 1
	argsMap, err := getUnmarshalledArgument(args)
	if err != nil {
		return shim.Error(err.Error())
	}
	hash, err := getStringParamFromArgs("hash", argsMap)
	if err != nil {
		return shim.Error(err.Error())
	}
	logger.Debugf("search history for %s",personPrfx + hash)
	resultsIterator, err := stub.GetHistoryForKey(personPrfx + hash)
	if err != nil {
		return shim.Error(err.Error())
	}
	defer resultsIterator.Close()

	for resultsIterator.HasNext() {
		historicValue, err := resultsIterator.Next()
		if err != nil {
			return shim.Error(err.Error())
		}
		logger.Debugf("historicValue%+v",historicValue)
		if historicValue != nil && historicValue.Value != nil {
			history = append(history, "{", time.Unix(historicValue.Timestamp.Seconds, int64(historicValue.Timestamp.Nanos)).String(), ":", string(historicValue.Value), "}") //add this tx to the list
		}

	}
	fmt.Printf("- readKeyHistory returning:\n%v", history)

	//change to array of bytes
	historyAsBytes, _ := json.MarshalIndent(history, "", "\t") //convert to array of bytes
	return shim.Success(historyAsBytes)
}

func (t *SimpleChaincode) read(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting 1")
	}
	key := args[0]
	response, err := stub.GetState(key)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(response)
}

func (t *SimpleChaincode) write(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Expecting 2")
	}
	key := args[0]
	val := []byte(args[1])
	err := stub.PutState(key, val)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(nil)
}

//print person data by hash
func (t *SimpleChaincode) getPersonInfo(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//parse parameters  - need 1
	argsMap, err := getUnmarshalledArgument(args)
	if err != nil {
		return shim.Error(err.Error())
	}
	hash, err := getStringParamFromArgs("hash", argsMap)
	if err != nil {
		return shim.Error(err.Error())
	}
	fmt.Println("get info for person " + hash)
	logger.Infof("get info for person %s", hash)
	//get person from state
	res, err := stub.GetState(personPrfx + hash)

	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(res)
}

//print person history by hash
func (t *SimpleChaincode) getPersonHistory(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//parse parameters  - need 1
	argsMap, err := getUnmarshalledArgument(args)
	if err != nil {
		return shim.Error(err.Error())
	}
	hash, err := getStringParamFromArgs("hash", argsMap)
	if err != nil {
		return shim.Error(err.Error())
	}
	//get person from state
	fmt.Println("get person history for person " + hash)
	logger.Infof("get person history for person %s", hash)
	res, err := stub.GetState(personHistoryPrfx + hash)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(res)
}

//print person searches by hash
func (t *SimpleChaincode) getPersonSearches(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//parse parameters  - need 1
	argsMap, err := getUnmarshalledArgument(args)
	if err != nil {
		return shim.Error(err.Error())
	}
	hash, err := getStringParamFromArgs("hash", argsMap)
	if err != nil {
		return shim.Error(err.Error())
	}
	//get person from state
	fmt.Println("get person searches for person " + hash)
	logger.Infof("get person searches for person %s ", hash)
	res, err := stub.GetState(personSearchPrfx + hash)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(res)
}

func (t *SimpleChaincode) insertPerson(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//parse parameters  - need 4
	argsMap, err := getUnmarshalledArgument(args)
	if err != nil {
		return shim.Error(err.Error())
	}
	hash, err := getStringParamFromArgs("hash", argsMap)
	if err != nil {
		return shim.Error(err.Error())
	}
	fmt.Println("insert man with hash " + hash)
	logger.Infof("insert man with hash %s", hash)
	user, err := getStringParamFromArgs("user", argsMap)
	if err != nil {
		return shim.Error(err.Error())
	}
	fmt.Println("for user " + user)
	logger.Infof("for user %s", user)
	company, err := getStringParamFromArgs("company", argsMap)
	if err != nil {
		return shim.Error(err.Error())
	}
	fmt.Println("company=" + company)
	logger.Infof("company= %s", company)
	status, err := getStringParamFromArgs("status", argsMap)
	if err != nil {
		return shim.Error(err.Error())
	}
	fmt.Println("status=" + status)
	logger.Infof("status= %s", status)
	//-----add person hash to state
	newPerson := &Person{}
	newPerson.ModifyDate = time.Now()
	newPerson.Status = status
	newPerson.Hash = hash
	err = createOrUpdatePerson(stub, hash, *newPerson)
	if err != nil {
		return shim.Error("error inserting person")
	}
	//------add record to person history
	err = addHistoryRecord(stub, hash, ACTION_INSERT, user, company, status)
	if err != nil {
		return shim.Error("Error putting new history record " + hash + " to state")
	}
	return shim.Success(nil)
}

func (t *SimpleChaincode) updatePerson(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//parse parameters  - need 4
	argsMap, err := getUnmarshalledArgument(args)
	if err != nil {
		return shim.Error(err.Error())
	}
	hash, err := getStringParamFromArgs("hash", argsMap)
	if err != nil {
		return shim.Error(err.Error())
	}
	logger.Infof("update man with hash %s", hash)
	user, err := getStringParamFromArgs("user", argsMap)
	if err != nil {
		return shim.Error(err.Error())
	}
	fmt.Println("user=" + user)
	logger.Infof("for user %s", user)
	company, err := getStringParamFromArgs("company", argsMap)
	if err != nil {
		return shim.Error(err.Error())
	}
	fmt.Println("company=" + company)
	logger.Infof("company= %s", company)
	status, err := getStringParamFromArgs("status", argsMap)
	if err != nil {
		return shim.Error(err.Error())
	}
	fmt.Println("status=" + status)
	logger.Infof("status= %s", status)
	//-----add person hash to state
	newPerson := &Person{}
	newPerson.Hash = hash
	newPerson.ModifyDate = time.Now()
	newPerson.Status = status
	err = createOrUpdatePerson(stub, hash, *newPerson)
	if err != nil {
		return shim.Error("error updating person")
	}
	//------add record to person history
	err = addHistoryRecord(stub, hash, ACTION_UPDATE, user, company, status)
	if err != nil {
		return shim.Error("Error putting new history record " + hash + " to state")
	}

	return shim.Success(nil)
}

func (t *SimpleChaincode) searchPerson(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	//parse parameters  - need 3
	argsMap, err := getUnmarshalledArgument(args)
	if err != nil {
		return shim.Error(err.Error())
	}
	hash, err := getStringParamFromArgs("hash", argsMap)
	if err != nil {
		return shim.Error(err.Error())
	}
	fmt.Println("hash=" + hash)
	logger.Infof("search man with hash %s", hash)
	user, err := getStringParamFromArgs("user", argsMap)
	if err != nil {
		return shim.Error(err.Error())
	}
	fmt.Println("user=" + user)
	logger.Infof("for user %s", user)
	company, err := getStringParamFromArgs("company", argsMap)
	if err != nil {
		return shim.Error(err.Error())
	}
	fmt.Println("company=" + company)
	logger.Infof("company= %s", company)

	res := &SearchResult{}
	//check existence
	var found bool
	res.Status = STATUS_NOT_FOUND
	//retrieve Person from state by hash
	personBytes, err := stub.GetState(personPrfx + hash)
	found = err == nil && len(personBytes) != 0
	if found { // if exists
		var oldperson Person
		err = json.Unmarshal(personBytes, &oldperson)
		if err != nil {
			return shim.Error("Error unmarshalling person " + hash + " from state")
		}
		//fill response record
		res.Status = oldperson.Status
	} else {
		//create new
		//-----add person hash to state
		newPerson := &Person{}
		newPerson.Hash = hash
		newPerson.ModifyDate = time.Now()
		newPerson.Status = STATUS_NOT_FOUND
		err = createOrUpdatePerson(stub, hash, *newPerson)
		if err != nil {
			return shim.Error("error inserting person")
		}
		//------add record to person history
		err = addHistoryRecord(stub, hash, ACTION_INSERT, user, company, STATUS_NOT_FOUND)
		if err != nil {
			return shim.Error("Error putting new history record " + hash + " to state")
		}
	}
	//add record to history
	err = addSearchRecord(stub, hash, user, company, res.Status)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(nil)
}

func addHistoryRecord(stub shim.ChaincodeStubInterface, hash string, action string, user string, company string, status string) error {
	var history []Action
	historyBytes, err := stub.GetState(personHistoryPrfx + hash)
	fmt.Println("current list: " + string(historyBytes))
	logger.Infof("current list: %s", string(historyBytes))
	if err != nil {
		return errors.New("Error getting history for person ")
	}
	if historyBytes != nil {
		err = json.Unmarshal(historyBytes, &history)
		if err != nil {
			return errors.New("Error unmarshalling history for person ")
		}
	}
	newAction := &Action{}
	newAction.Status = status
	newAction.Method = action
	newAction.User = user
	newAction.Date = time.Now()
	newAction.Company = company
	//insert action to history in LIFO order
	history = append([]Action{*newAction}, history...)
	//put history to state
	newHistoryBytes, err := json.Marshal(&history)
	if err != nil {
		return errors.New("Error parsing history for person " + hash)
	}
	err = stub.PutState(personHistoryPrfx+hash, newHistoryBytes)
	if err != nil {
		return errors.New("Error parsing history for person" + hash)
	}
	return nil
}

func addSearchRecord(stub shim.ChaincodeStubInterface, hash string, user string, company string, status string) error {
	var search []SearchResult
	searchBytes, err := stub.GetState(personSearchPrfx + hash)
	fmt.Println("current list: " + string(searchBytes))
	logger.Infof("current list: %s", string(searchBytes))
	if err != nil {
		return errors.New("Error getting search result for person ")
	}
	if searchBytes != nil {
		err = json.Unmarshal(searchBytes, &search)
		if err != nil {
			return errors.New("Error unmarshalling search result for person ")
		}
	}
	newSearch := &SearchResult{}
	newSearch.Status = status
	newSearch.User = user
	newSearch.Date = time.Now()
	newSearch.Company = company
	//insert search to search list in LIFO order
	search = append([]SearchResult{*newSearch}, search...)
	//put search list to state
	newSearchBytes, err := json.Marshal(&search)
	if err != nil {
		return errors.New("Error parsing search list for person " + hash)
	}
	err = stub.PutState(personSearchPrfx+hash, newSearchBytes)
	if err != nil {
		return errors.New("Error parsing search list for person" + hash)
	}
	return nil
}

func createOrUpdatePerson(stub shim.ChaincodeStubInterface, hash string, newPerson Person) error {
	var oldPerson Person
	//retrieve Person from state by hash
	personBytes, err := stub.GetState(personPrfx + hash)
	if err != nil || len(personBytes) == 0 {
		//data not found, create scenario
		oldPerson = newPerson
	} else {
		//update scenario
		err = json.Unmarshal(personBytes, &oldPerson)
		if err != nil {
			return errors.New("error unmarshalling person from state")
		}
		//TODO merge data, now only replace
		oldPerson = newPerson
		if err != nil {
			return errors.New("error mergin data of Person")
		}
	}
	//put Person in state
	err = putPersonInState(stub, hash, oldPerson)
	if err != nil {
		return err
	}
	return nil
}

func putPersonInState(stub shim.ChaincodeStubInterface, hash string, person Person) error {
	personAsBytes, err := json.Marshal(&person)
	if err != nil {
		return errors.New("Error marhalling new person")
	}
	err = stub.PutState(personPrfx+hash, personAsBytes)
	if err != nil {
		return errors.New("Error puttin new person")
	}
	fmt.Println("put record for " + hash)
	logger.Infof("put record for %s", hash)
	return nil
}

func (t *SimpleChaincode) setLoggingLevel(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	type LogLevelArg struct {
		Level string `json:"logLevel"`
	}
	var level LogLevelArg
	var err error
	if len(args) != 1 {
		err = errors.New("Incorrect number of arguments. Expecting a JSON encoded LogLevel.")
		logger.Errorf(err.Error())
		return shim.Error(err.Error())
	}
	err = json.Unmarshal([]byte(args[0]), &level)
	if err != nil {
		err = fmt.Errorf("setLoggingLevel failed to unmarshal arg: %s", err)
		logger.Errorf(err.Error())
		return shim.Error(err.Error())
	}

	switch level.Level {
	case "DEBUG":
		logger.SetLevel(shim.LogDebug)
	case "INFO":
		logger.SetLevel(shim.LogInfo)
	case "NOTICE":
		logger.SetLevel(shim.LogNotice)
	case "WARNING":
		logger.SetLevel(shim.LogWarning)
	case "ERROR":
		logger.SetLevel(shim.LogError)
	case "CRITICAL":
		logger.SetLevel(shim.LogCritical)
	default:
		err = fmt.Errorf("setLoggingLevel failed with unknown arg: %s", level.Level)
		logger.Errorf(err.Error())
		return shim.Error(err.Error())
	}

	return shim.Success(nil)
}

func (t *SimpleChaincode) searchPersonAndReturn(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	//parse parameters  - need 3
	argsMap, err := getUnmarshalledArgument(args)
	if err != nil {
		return shim.Error(err.Error())
	}
	hash, err := getStringParamFromArgs("hash", argsMap)
	if err != nil {
		return shim.Error(err.Error())
	}
	fmt.Println("hash=" + hash)
	logger.Infof("search man with hash %s", hash)
	user, err := getStringParamFromArgs("user", argsMap)
	if err != nil {
		return shim.Error(err.Error())
	}
	fmt.Println("user=" + user)
	logger.Infof("for user %s", user)
	company, err := getStringParamFromArgs("company", argsMap)
	if err != nil {
		return shim.Error(err.Error())
	}
	fmt.Println("company=" + company)
	logger.Infof("company= %s", company)

	res := &SearchResult{}
	//check existence
	var found bool
	res.Status = STATUS_NOT_FOUND
	//retrieve Person from state by hash
	personBytes, err := stub.GetState(personPrfx + hash)
	found = err == nil && len(personBytes) != 0
	if found { // if exists
		var oldperson Person
		err = json.Unmarshal(personBytes, &oldperson)
		if err != nil {
			return shim.Error("Error unmarshalling person " + hash + " from state")
		}
		//fill response record
		res.Status = oldperson.Status
		res.Date = oldperson.ModifyDate
	} else {
		//create new
		//-----add person hash to state
		newPerson := &Person{}
		newPerson.Hash = hash
		newPerson.ModifyDate = time.Now()
		newPerson.Status = STATUS_NOT_FOUND
		err = createOrUpdatePerson(stub, hash, *newPerson)
		if err != nil {
			return shim.Error("error inserting person")
		}
		//------add record to person history
		err = addHistoryRecord(stub, hash, ACTION_INSERT, user, company, STATUS_NOT_FOUND)
		if err != nil {
			return shim.Error("Error putting new history record " + hash + " to state")
		}
		res.Status = newPerson.Status
		res.Date = newPerson.ModifyDate
	}
	//add record to history
	err = addSearchRecord(stub, hash, user, company, res.Status)
	if err != nil {
		return shim.Error(err.Error())
	}
	//prepare response
	resBytes, err := json.Marshal(res)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(resBytes)
}
