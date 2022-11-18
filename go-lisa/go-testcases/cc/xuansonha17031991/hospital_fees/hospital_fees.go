package main

import (
	"encoding/json"
	"fmt"
	"time"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)

type HospitalFees_Chaincode struct {
}

type HospitalFees struct {
	ObjectType               string `json:"docType"`
	ID                       string `json:"id"`
	PatientName              string `json:"patient_name"`
	Account                  string `json:"account"`
	DateOfService            string `json:"date_of_service"`
	PatientService           string `json:"patient_service"`
	PrimaryInsuranceBilled   string `json:"primary_insurance_billed"`
	SecondaryInsuranceBilled string `json:"secondary_insurance_billed"`
	Pharmacy                 string `json:"pharmacy"`
	Room                     string `json:"room"`
	AmountDue                string `json:"amount_due"`
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
	err := shim.Start(new(HospitalFees_Chaincode))
	if err != nil {
		fmt.Printf("cannot initiate heathcare chaincode: %s", err)
	}
}

// Init chaincode
func (t *HospitalFees_Chaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	return shim.Success(nil)
}

// Invoke
func (t *HospitalFees_Chaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	function, args := stub.GetFunctionAndParameters()
	fmt.Printf("invoke is running" + function)

	switch function {
	case "createHospitalFees":
		return t.createHospitalFees(stub, args)
	case "query":
		return t.query(stub, args)

	default:
		fmt.Println("Invoke did not find function: " + function)
		return shim.Error("Received unknown function invocation")
	}
}

//query data
func (t *HospitalFees_Chaincode) query(stub shim.ChaincodeStubInterface, args []string) pb.Response {
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
	valueAsBytes, errValueAsByte := stub.GetPrivateData("HospitalFeesCollection", patientid)
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
 * create hospital fees of patients
 * @param: userid
 * @param: patientName
 * @param: account
 * @param: dateOfService
 * @param: patientService
 * @param: primaryInsuranceBilled
 * @param: secondaryInsuranceBilled
 * @param: pharmacy
 * @param: room
 * @param: amountDue
 * ouput: nil
 */
func (t *HospitalFees_Chaincode) createHospitalFees(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	fmt.Println("\n=============== start createHospitalFees function ===============")
	start := time.Now()
	time.Sleep(time.Second)

	//check length of data
	if len(args) != 3 {
		return shim.Error("expecting 3 argument")
	}

	//define data variable
	id := args[0]
	patientName := args[1]
	account := args[2]
	dateOfService := args[3]
	patientService := args[4]
	primaryInsuranceBilled := args[5]
	secondaryInsuranceBilled := args[6]
	pharmacy := args[7]
	room := args[8]
	amountDue := args[9]

	ObjectType := "HospitalFees"
	hospitalFees := &HospitalFees{ObjectType, id, patientName, account, dateOfService,
		patientService, primaryInsuranceBilled, secondaryInsuranceBilled, pharmacy,
		room, amountDue}

	//marshal delivery to byte
	hospitalFeesAsByte, errHospitalFeesAsByte := json.Marshal(hospitalFees)
	if errHospitalFeesAsByte != nil {
		return shim.Error("cannot marshal pharmacy's data")
	}

	//put data to ledger
	errHospitalFeesAsByte = stub.PutPrivateData("HospitalFeesCollection", id, hospitalFeesAsByte)
	if errHospitalFeesAsByte != nil {
		return shim.Error("cannot put private data of pharmacy")
	}

	//create index key
	indexKey := "id~patient_name"
	hospitalFeesIndexKey, errHospitalFeesIndexKey := stub.CreateCompositeKey(indexKey, []string{hospitalFees.ID, hospitalFees.PatientName, hospitalFees.Account, hospitalFees.DateOfService, hospitalFees.PatientService, hospitalFees.PrimaryInsuranceBilled, hospitalFees.SecondaryInsuranceBilled, hospitalFees.Pharmacy, hospitalFees.Room, hospitalFees.AmountDue})
	if errHospitalFeesIndexKey != nil {
		return shim.Error("cannot create index key of delivery")
	}

	//save key
	value := []byte{0x00}
	stub.PutPrivateData("HospitalFeesCollection", hospitalFeesIndexKey, value)

	end := time.Now()
	elapsed := time.Since(start)

	fmt.Println("\nfunction createHospitalFees")
	fmt.Println("time start: ", start.String())
	fmt.Println("time end: ", end.String())
	fmt.Println("time execute: ", elapsed.String())
	fmt.Println("=============== end createHospitalFees function ===============")
	time.Sleep(time.Second)

	return shim.Success(nil)
}
