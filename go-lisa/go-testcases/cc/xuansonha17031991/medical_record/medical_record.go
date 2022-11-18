package main

import (
	"encoding/json"
	"fmt"
	"strconv"
	"time"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)

type MedicalRecord_Chaincode struct {
}

type MedicalRecord struct {
	ObjectType                        string `json:"doctype"`
	ID                                string `json:"id"`
	PersonalIdentificationInformation string `json:"personal_identification"`
	MedicalHistory                    string `json:"medical_history"`
	FamilyMedicalHistory              string `json:"family_medical_history"`
	MedicationHistory                 string `json:"medication_history"`
	TreatmentHistory                  string `json:"treatment_history"`
	MedicalDirectives                 string `json:"medical_directives"`
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
	err := shim.Start(new(MedicalRecord_Chaincode))
	if err != nil {
		fmt.Printf("cannot initiate heathcare chaincode: %s", err)
	}
}

// Init chaincode
func (t *MedicalRecord_Chaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	return shim.Success(nil)
}

// Invoke
func (t *MedicalRecord_Chaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	function, args := stub.GetFunctionAndParameters()
	fmt.Printf("invoke is running" + function)

	switch function {
	case "createMedicalRecord":
		return t.createMedicalRecord(stub, args)
	case "modifyMedicalData":
		return t.modifyMedicalData(stub, args)
	case "query":
		return t.query(stub, args)

	default:
		fmt.Println("Invoke did not find function: " + function)
		return shim.Error("Received unknown function invocation")
	}
}

/**
 * modify data of medical record and store with id of user execute query
 * @param: patientId
 * @param: personalIdentificationInformation
 * @param: medicalHistory
 * @param: familyMedicalHistory
 * @param: medicationHistory
 * @param: treatmentHistory
 * @param: medicalDirectives
 */
func (t *MedicalRecord_Chaincode) createMedicalRecord(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	fmt.Println("\n=============== start createMedicalRecord function ===============")
	start := time.Now()
	time.Sleep(time.Second)

	if len(args) != 7 {
		return shim.Error("there must be 7 argument")
	}

	for i := 0; i < len(args); i++ {
		if len(args[i]) == 0 {
			return shim.Error("argument " + strconv.Itoa(i+1) + " must be declare")
		}
	}
	patientId := args[0]
	personalIdentificationInformation := args[1]
	medicalHistory := args[2]
	familyMedicalHistory := args[3]
	medicationHistory := args[4]
	treatmentHistory := args[5]
	medicalDirectives := args[6]

	//convert variable to json
	objectType := "MedicalRecord"
	medialRecord := &MedicalRecord{objectType, patientId, personalIdentificationInformation,
		medicalHistory, familyMedicalHistory, medicationHistory,
		treatmentHistory, medicalDirectives}

	//convert data to byte
	MedicalRecordAsByte, errMedicalRecordAsByte := json.Marshal(medialRecord)
	if errMedicalRecordAsByte != nil {
		return shim.Error(errMedicalRecordAsByte.Error())
	}

	//save to database
	errMedicalRecordAsByte = stub.PutPrivateData("MedicalRecordCollection", patientId, MedicalRecordAsByte)
	if errMedicalRecordAsByte != nil {
		return shim.Error(errMedicalRecordAsByte.Error())
	}

	//create index key
	indexName := "id"
	medicalRecordIndexKey, errMedicalRecordIndexKey := stub.CreateCompositeKey(indexName, []string{medialRecord.ID, medialRecord.PersonalIdentificationInformation, medialRecord.MedicalHistory, medialRecord.FamilyMedicalHistory, medialRecord.MedicationHistory, medialRecord.TreatmentHistory, medialRecord.MedicalDirectives})
	if errMedicalRecordIndexKey != nil {
		return shim.Error(errMedicalRecordIndexKey.Error())
	}

	//save index
	value := []byte{0x00}
	stub.PutPrivateData("MedicalRecordCollection", medicalRecordIndexKey, value)

	end := time.Now()
	elapsed := time.Since(start)

	fmt.Println("\nfunction createMedicalRecord")
	fmt.Printf("time start: %s", start.String())
	fmt.Printf("time end: %s", end.String())
	fmt.Println("time execute: ", elapsed.String())
	fmt.Println("=============== end createMedicalRecord function ===============")
	return shim.Success(nil)
}

/**
 * modify data of medical record and store with id of user execute query
 * @param: userid
 * @param: patientid
 * @param: location
 * @param: collection
 */
func (t *MedicalRecord_Chaincode) query(stub shim.ChaincodeStubInterface, args []string) pb.Response {
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
 * modify data of medical record and store with id of user execute query
 * @param: userid
 * @param: patientid
 * @param: location
 * @param: collection of user execute query
 * @param: newPersonalIdentificationInformation
 * @param: newMedicalHistory
 * @param: newFamilyMedicalHistory
 */
func (t *MedicalRecord_Chaincode) modifyMedicalData(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	fmt.Println("\n=============== start modifyMedicalData function ===============")
	start := time.Now()
	time.Sleep(time.Second)

	var jsonResp string

	if len(args) != 8 {
		return shim.Error("expecting 4 argument")
	}

	//define identity of query-er and new value of medical record
	userid := args[0]
	patientid := args[1]
	location := args[2]
	collection := args[3]

	newPersonalIdentificationInformation := args[4]
	newMedicalHistory := args[5]
	newFamilyMedicalHistory := args[6]
	newMedicationHistory := args[7]
	newTreatmentHistory := args[8]
	newMedicalDirectives := args[9]
	timeQuery := time.Now().String()

	//get user identity before query
	userIdentityAsBytes, errUserIdentityAsByte := stub.GetPrivateData(collection, userid)
	if errUserIdentityAsByte != nil {
		return shim.Error("cannot get user identity")
	} else if userIdentityAsBytes == nil {
		return shim.Error("user does not exist")
	}

	//create query object with purpose: modify
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

	//get medical record data
	medicalRecordAsBytes, errMedicalRecordAsByte := stub.GetPrivateData("MedicalRecordCollection", patientid)
	if errMedicalRecordAsByte != nil {
		jsonResp = "{\"Error\":\"Failed to get state for " + patientid + ": " + errMedicalRecordAsByte.Error() + "\"}"
		return shim.Error(jsonResp)
	} else if errMedicalRecordAsByte == nil {
		return shim.Error("patient's data does not exist")
	}

	//convert data of patient to json
	medicalRecord := &MedicalRecord{}
	errMedicalRecordAsByte = json.Unmarshal(medicalRecordAsBytes, medicalRecord)

	//change data
	medicalRecord.PersonalIdentificationInformation = newPersonalIdentificationInformation
	medicalRecord.MedicalHistory = newMedicalHistory
	medicalRecord.FamilyMedicalHistory = newFamilyMedicalHistory
	medicalRecord.MedicationHistory = newMedicationHistory
	medicalRecord.TreatmentHistory = newTreatmentHistory
	medicalRecord.MedicalDirectives = newMedicalDirectives

	//convert new medical record data to byte
	newMedicalRecordAsByte, errNewMedicalRecordAsByte := json.Marshal(medicalRecord)

	//store new data
	errNewMedicalRecordAsByte = stub.PutPrivateData("MedicalRecordCollection", patientid, newMedicalRecordAsByte)
	if errNewMedicalRecordAsByte != nil {
		return shim.Error("cannot save new medical record's data")
	}

	end := time.Now()
	elapsed := time.Since(start)
	fmt.Println("function modifyMedicalData")
	fmt.Println("time start: ", start.String())
	fmt.Println("time end: ", end.String())
	fmt.Println("time execute: ", elapsed.String())
	fmt.Println("=============== end modifyMedicalData function ===============")

	return shim.Success(nil)
}
