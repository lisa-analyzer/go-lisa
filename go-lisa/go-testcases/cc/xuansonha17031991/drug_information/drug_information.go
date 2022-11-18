package main

import (
	"encoding/json"
	"fmt"
	"strconv"
	"time"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)

type DrugInformation_Chainode struct {
}

type DrugInformation struct {
	ObjectType     string `json:"doctype"`
	ID             string `json:"id"`
	PatientName    string `json:"patient_name"`
	DrugName       string `json:"drug_name"`
	ExpirationDate string `json:"expiration_date"`
	Quantity       string `json:"quantity"`
	PrescribedBy   string `json:"prescribed_by"`
}

type Query struct {
	ObjectType string `json:"docType"`
	UserID     string `json:"userid"`
	PatientID  string `json:"patientid"`
	Location   string `json:"location"`
	Time       string `json:"time"`
	Purpose    string `json:"purpose"`
}

/*main*/
func main() {
	err := shim.Start(new(DrugInformation_Chainode))
	if err != nil {
		fmt.Printf("cannot initiate drug information chaincode: %s", err)
	}
}

// Init chaincode
func (t *DrugInformation_Chainode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	return shim.Success(nil)
}

// Invoke
func (t *DrugInformation_Chainode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	function, args := stub.GetFunctionAndParameters()
	fmt.Printf("invoke is running" + function)

	switch function {
	case "createDrugInformation":
		return t.createDrugInformation(stub, args)
	case "modifyDrugData":
		return t.modifyDrugData(stub, args)
	case "query":
		return t.query(stub, args)

	default:
		fmt.Println("Invoke did not find function: " + function)
		return shim.Error("Received unknown function invocation")
	}
}

//query data
func (t *DrugInformation_Chainode) query(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	fmt.Println("\n=============== start query function ===============")
	start := time.Now()
	time.Sleep(time.Second)

	var jsonResp string

	if len(args) != 4 {
		return shim.Error("expecting 4 argument")
	}

	userid := args[0]
	patientid := args[1]
	location := args[2]
	collection := args[3]
	timeQuery := time.Now().String()

	//get user identity before query
	userIdentityAsBytes, errUserIdentityAsByte := stub.GetPrivateData(collection, userid)
	if errUserIdentityAsByte != nil {
		return shim.Error("cannot get user identity")
	} else if userIdentityAsBytes == nil {
		return shim.Error("user does not exist")
	}

	objectType := "Query"
	query := &Query{objectType, userid, patientid, location, timeQuery, "query"}
	queryAsByte, errQueryAsByte := json.Marshal(query)
	if errQueryAsByte != nil {
		return shim.Error(errQueryAsByte.Error())
	}

	//save to database
	errQueryAsByte = stub.PutPrivateData("queryCollection", userid, queryAsByte)
	if errQueryAsByte != nil {
		return shim.Error(errQueryAsByte.Error())
	}

	//create index key
	indexName := "userid~patientid"
	queryIndexKey, errQueryIndexKey := stub.CreateCompositeKey(indexName, []string{query.UserID, query.PatientID, query.Location, query.Purpose})
	if errQueryIndexKey != nil {
		return shim.Error(errQueryIndexKey.Error())
	}

	//save index
	value := []byte{0x00}
	stub.PutPrivateData("queryCollection", queryIndexKey, value)

	//get data
	valueAsBytes, errValueAsByte := stub.GetPrivateData("PatientInformationCollection", patientid)
	if errValueAsByte != nil {
		jsonResp = "{\"Error\":\"Failed to get state for " + patientid + ": " + errValueAsByte.Error() + "\"}"
		return shim.Error(jsonResp)
	} else if valueAsBytes == nil {
		jsonResp = "user id does not exist"
		return shim.Error(jsonResp)
	}

	end := time.Now()
	elapsed := time.Since(start)
	fmt.Println("function query")
	fmt.Println("time start: ", start.String())
	fmt.Println("time end: ", end.String())
	fmt.Println("time execute: ", elapsed.String())
	fmt.Println("=============== end query function ===============")

	return shim.Success(valueAsBytes)
}

/**
 * modify drug's data of patient
 * @param: userid
 * @param: patientid
 * @param: location
 * @param: collection
 * @param: newPatientName
 * @param: newDrugName
 * @param: newExpirationDate
 * @param: newQuantity
 * @param: newPrescribedBy
 */
func (t *DrugInformation_Chainode) modifyDrugData(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	fmt.Println("\n=============== start modifyDrugData function ===============")
	start := time.Now()
	time.Sleep(time.Second)

	var jsonResp string

	if len(args) != 9 {
		return shim.Error("expecting 9 argument")
	}

	userid := args[0]
	patientid := args[1]
	location := args[2]
	collection := args[3]

	newPatientName := args[4]
	newDrugName := args[5]
	newExpirationDate := args[6]
	newQuantity := args[7]
	newPrescribedBy := args[8]

	timeQuery := time.Now().String()

	//get user identity before query
	userIdentityAsBytes, errUserIdentityAsByte := stub.GetPrivateData(collection, userid)
	if errUserIdentityAsByte != nil {
		return shim.Error("cannot get user identity")
	} else if userIdentityAsBytes == nil {
		return shim.Error("user does not exist")
	}

	objectType := "Query"
	query := &Query{objectType, userid, patientid, location, timeQuery, "modify"}
	queryAsByte, errQueryAsByte := json.Marshal(query)
	if errQueryAsByte != nil {
		return shim.Error(errQueryAsByte.Error())
	}

	//save to database
	errQueryAsByte = stub.PutPrivateData("modifyCollection", userid, queryAsByte)
	if errQueryAsByte != nil {
		return shim.Error(errQueryAsByte.Error())
	}

	//create index key
	indexName := "userid~patientid"
	queryIndexKey, errQueryIndexKey := stub.CreateCompositeKey(indexName, []string{query.UserID, query.PatientID, query.Location, query.Purpose})
	if errQueryIndexKey != nil {
		return shim.Error(errQueryIndexKey.Error())
	}

	//save index
	value := []byte{0x00}
	stub.PutPrivateData("modifyCollection", queryIndexKey, value)

	//get data
	drugAsBytes, errDrugAsByte := stub.GetPrivateData("drugInformationCollection", patientid)
	if errDrugAsByte != nil {
		jsonResp = "{\"Error\":\"Failed to get state for " + patientid + ": " + errDrugAsByte.Error() + "\"}"
		return shim.Error(jsonResp)
	} else if errDrugAsByte == nil {
		return shim.Error("patient's data does not exist")
	}

	//convert data of patient to json
	drug := &DrugInformation{}
	errDrugAsByte = json.Unmarshal(drugAsBytes, drug)

	//change data
	drug.PatientName = newPatientName
	drug.DrugName = newDrugName
	drug.ExpirationDate = newExpirationDate
	drug.Quantity = newQuantity
	drug.PrescribedBy = newPrescribedBy

	//store new data of drug information
	newDrugInformationAsByte, errNewDruvInformationAsByte := json.Marshal(drug)
	errNewDruvInformationAsByte = stub.PutPrivateData("drugInformationCollection", patientid, newDrugInformationAsByte)
	if errNewDruvInformationAsByte != nil {
		return shim.Error("cannot store new drug's data")
	}

	end := time.Now()
	elapsed := time.Since(start)
	fmt.Println("function modifyDrugData")
	fmt.Println("time start: ", start.String())
	fmt.Println("time end: ", end.String())
	fmt.Println("time execute: ", elapsed.String())
	fmt.Println("=============== end modifyDrugData function ===============")

	return shim.Success(nil)
}

//create drug information
func (t *DrugInformation_Chainode) createDrugInformation(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	fmt.Println("\n=============== start createDrugInformation function ===============")
	start := time.Now()
	time.Sleep(time.Second)

	if len(args) != 6 {
		return shim.Error("expecting 6 argument")
	}

	for i := 0; i < len(args); i++ {
		if len(args[i]) == 0 {
			return shim.Error("argument " + strconv.Itoa(i+1) + " must be declare")
		}
	}

	//define argument
	patientId := args[0]
	patientName := args[1]
	drugName := args[2]
	expirationDate := args[3]
	quantity := args[4]
	prescribedBy := args[5]

	//convert to json
	objectType := "DrugInformation"
	drugInformation := &DrugInformation{objectType, patientId, patientName, drugName,
		expirationDate, quantity, prescribedBy}
	drugInformationAsByte, errDrugInformationAsByte := json.Marshal(drugInformation)
	if errDrugInformationAsByte != nil {
		return shim.Error(errDrugInformationAsByte.Error())
	}

	//save to ledger
	errDrugInformationAsByte = stub.PutPrivateData("DrugInformationCollection", patientId, drugInformationAsByte)
	if errDrugInformationAsByte != nil {
		return shim.Error(errDrugInformationAsByte.Error())
	}

	//create and save key
	indexName := "id~patient_name"
	DrugInformationIndexKey, errDrugInformationIndexKey := stub.CreateCompositeKey(indexName, []string{drugInformation.ID, drugInformation.PatientName, drugInformation.DrugName, drugInformation.ExpirationDate, drugInformation.Quantity, drugInformation.ExpirationDate})
	if errDrugInformationIndexKey != nil {
		return shim.Error(errDrugInformationIndexKey.Error())
	}
	value := []byte{0x00}
	stub.PutPrivateData("DrugInformationCollection", DrugInformationIndexKey, value)

	end := time.Now()
	elapsed := time.Since(start)

	fmt.Println("\nfunction createDrugInformation")
	fmt.Println("time start: ", start.String())
	fmt.Println("time end: ", end.String())
	fmt.Println("time execute: ", elapsed.String())
	fmt.Println("=============== end createDrugInformation function ===============")

	return shim.Success(nil)
}
