package main

import (
	"encoding/json"
	"fmt"
	"strconv"
	"time"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)

type PatientInformation_Chaincode struct {
}

type PatientInformation struct {
	ObjectType                   string `json:"doctype"`
	ID                           string `json:"photo_id"`
	InsuranceCard                string `json:"insurance_card"`
	CurrentMedicationInformation string `json:"current_medication_information"`
	RelatedMedicalRecords        string `json:"related_medical_records"`
	MakeNoteOfAppointmentDate    string `json:"make_note_of_appointment_date"`
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
	err := shim.Start(new(PatientInformation_Chaincode))
	if err != nil {
		fmt.Printf("cannot initiate heathcare chaincode: %s", err)
	}
}

// Init chaincode
func (t *PatientInformation_Chaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	return shim.Success(nil)
}

// Invoke
func (t *PatientInformation_Chaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	function, args := stub.GetFunctionAndParameters()
	fmt.Printf("invoke is running" + function)

	switch function {
	case "createPatientInformation":
		return t.createPatientInformation(stub, args)
	case "modifyData":
		return t.modifyData(stub, args)
	case "query":
		return t.query(stub, args)

	default:
		fmt.Println("Invoke did not find function: " + function)
		return shim.Error("Received unknown function invocation")
	}
}

/**
 * create patient information
 * @param: patientid
 * @param: insuranceCard
 * @param: currentMedicationInformation
 * @param: relatedMedicalRecords
 * @param: makeNoteOfAppointmentDate
 */
func (t *PatientInformation_Chaincode) createPatientInformation(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	fmt.Println("\n=============== start createPatientInformation function ===============")
	start := time.Now()
	time.Sleep(time.Second)

	if len(args) != 5 {
		return shim.Error("there must be 5 argument")
	}

	for i := 0; i < len(args); i++ {
		if len(args[i]) == 0 {
			return shim.Error("argument " + strconv.Itoa(i+1) + " must be declare")
		}
	}

	patientId := args[0]
	insuranceCard := args[1]
	currentMedicationInformation := args[2]
	relatedMedicalRecords := args[3]
	makeNoteOfAppointmentDate := args[4]

	//convert variable to json
	objectType := "PatientInformation"
	// user := &User{}
	patient := &PatientInformation{objectType, patientId, insuranceCard,
		currentMedicationInformation, relatedMedicalRecords, makeNoteOfAppointmentDate}
	// patientData := user.Name + " " + strconv.Itoa(user.Age) + " " + user.Number + " " + user.Address + " " + patient.Data
	PatientInformationAsByte, errPatientInformationAsByte := json.Marshal(patient)
	if errPatientInformationAsByte != nil {
		return shim.Error(errPatientInformationAsByte.Error())
	}

	//save to database
	errPatientInformationAsByte = stub.PutPrivateData("PatientInformationCollection", patientId, PatientInformationAsByte)
	if errPatientInformationAsByte != nil {
		return shim.Error(errPatientInformationAsByte.Error())
	}

	//create index key
	indexName := "id~insurance_card"
	patientIndexKey, errPatientIndexKey := stub.CreateCompositeKey(indexName, []string{patient.ID, patient.InsuranceCard, patient.CurrentMedicationInformation, patient.RelatedMedicalRecords, patient.MakeNoteOfAppointmentDate})
	if errPatientIndexKey != nil {
		return shim.Error(errPatientIndexKey.Error())
	}

	//save index
	value := []byte{0x00}
	stub.PutPrivateData("PatientInformationCollection", patientIndexKey, value)

	end := time.Now()
	elapsed := time.Since(start)

	fmt.Println("\nfunction createPatientInformation")
	fmt.Printf("time start: %s", start.String())
	fmt.Printf("time end: %s", end.String())
	fmt.Println("time execute: ", elapsed.String())
	fmt.Println("=============== end createPatientInformation function ===============")
	return shim.Success(nil)
}

//query data
func (t *PatientInformation_Chaincode) query(stub shim.ChaincodeStubInterface, args []string) pb.Response {
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
 * modify data of patient and save id of user execute query
 * @param: userid
 * @param: patientid
 * @param: location
 * @param: collection
 * @param: newInsuranceCard
 * @param: newCurrentMedicationInformation
 * @param: newRelatedMedicalRecords
 * @param: newmakeNoteOfAppointmentDate
 */
func (t *PatientInformation_Chaincode) modifyPatientInformation(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	fmt.Println("\n=============== start modifyPatientInformation function ===============")
	start := time.Now()
	time.Sleep(time.Second)

	var jsonResp string

	if len(args) != 8 {
		return shim.Error("expecting 8 argument")
	}

	userid := args[0]
	patientid := args[1]
	location := args[2]
	collection := args[3]

	newInsuranceCard := args[4]
	newCurrentMedicationInformation := args[5]
	newRelatedMedicalRecords := args[6]
	newmakeNoteOfAppointmentDate := args[7]
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
	patientAsBytes, errPatientAsByte := stub.GetPrivateData("PatientInformationCollection", patientid)
	if errPatientAsByte != nil {
		jsonResp = "{\"Error\":\"Failed to get state for " + patientid + ": " + errPatientAsByte.Error() + "\"}"
		return shim.Error(jsonResp)
	} else if errPatientAsByte == nil {
		return shim.Error("patient's data does not exist")
	}

	//convert data of patient to json
	patient := &PatientInformation{}
	errPatientAsByte = json.Unmarshal(patientAsBytes, patient)

	//change data
	patient.InsuranceCard = newInsuranceCard
	patient.CurrentMedicationInformation = newCurrentMedicationInformation
	patient.RelatedMedicalRecords = newRelatedMedicalRecords
	patient.MakeNoteOfAppointmentDate = newmakeNoteOfAppointmentDate

	patientAsByte, errPatientAsByte := json.Marshal(patient)

	errPatientAsByte = stub.PutPrivateData("PatientInformationCollection", patientid, patientAsByte)
	if errPatientAsByte != nil {
		return shim.Error("cannot patient's data")
	}

	end := time.Now()
	elapsed := time.Since(start)
	fmt.Println("function modifyPatientInformation")
	fmt.Println("time start: ", start.String())
	fmt.Println("time end: ", end.String())
	fmt.Println("time execute: ", elapsed.String())
	fmt.Println("=============== end modifyPatientInformation function ===============")

	return shim.Success(nil)
}
