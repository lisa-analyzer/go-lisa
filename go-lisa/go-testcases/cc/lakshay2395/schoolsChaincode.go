package main

import (
	"encoding/json"
	"fmt"
	"time"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	uuid "github.com/satori/go.uuid"
)

type SchoolsChaincode struct {
	testing bool
}

func (sc *SchoolsChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	return shim.Success(nil)
}

func (sc *SchoolsChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	function, _ := stub.GetFunctionAndParameters()
	switch function {
	case "AddStudentDetails":
		return sc.AddStudentDetails(stub)
	case "UpdateStudentDetails":
		return sc.UpdateStudentDetails(stub)
	case "DeleteStudentDetails":
		return sc.DeleteStudentDetails(stub)
	case "GetStudentDetails":
		return sc.GetStudentDetails(stub)
	case "InitiateSchoolLeavingRequest":
		return sc.InitiateSchoolLeavingRequest(stub)
	case "ResubmitSchoolLeavingRequest":
		return sc.ResubmitSchoolLeavingRequest(stub)
	case "GetSchoolLeavingRequests":
		return sc.GetSchoolLeavingRequests(stub)
	case "InitiateSchoolAdmissionRequest":
		return sc.InitiateSchoolAdmissionRequest(stub)
	case "ResubmitSchoolAdmissionRequest":
		return sc.ResubmitSchoolAdmissionRequest(stub)
	case "GetSchoolAdmissionRequests":
		return sc.GetSchoolAdmissionRequests(stub)
	case "RejectSchoolLeavingApplication":
		return sc.RejectSchoolLeavingApplication(stub)
	case "AcceptSchoolLeavingApplication":
		return sc.AcceptSchoolLeavingApplication(stub)
	case "RejectSchoolAdmissionApplication":
		return sc.RejectSchoolAdmissionApplication(stub)
	case "AcceptSchoolAdmissionApplication":
		return sc.AcceptSchoolAdmissionApplication(stub)
	}
	return shim.Error("Invoke Failed : No method named as " + function)
}

func (sc *SchoolsChaincode) AddStudentDetails(stub shim.ChaincodeStubInterface) pb.Response {
	autherr := sc.CheckSchoolAuthorization(stub)
	if autherr != nil {
		return shim.Error(autherr.Error())
	}
	_, args := stub.GetFunctionAndParameters()
	err := ValidateArgumentCount(5, len(args))
	if err != nil {
		return shim.Error(err.Error())
	}
	uuid, err := uuid.NewV4()
	if err != nil {
		return shim.Error(err.Error())
	}
	dateOfJoining := time.Now()
	student := Student{uuid.String(), args[0], args[1], Gender(args[2]), args[3], args[4], MEMBER, dateOfJoining, dateOfJoining, STUDENT}
	studentKey, err := GetStudentKey(stub, uuid.String())
	data, err := json.Marshal(student)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(studentKey, data)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(data)
}

func (sc *SchoolsChaincode) UpdateStudentDetails(stub shim.ChaincodeStubInterface) pb.Response {
	autherr := sc.CheckSchoolAuthorization(stub)
	if autherr != nil {
		return shim.Error(autherr.Error())
	}
	_, args := stub.GetFunctionAndParameters()
	err := ValidateArgumentCount(6, len(args))
	if err != nil {
		return shim.Error(err.Error())
	}
	uuid, err := GetStudentKey(stub, args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	studentBytes, err := GetState(stub, uuid)
	if err != nil {
		return shim.Error(err.Error())
	}
	studentDetails := Student{}
	err = json.Unmarshal(studentBytes, &studentDetails)
	if err != nil {
		return shim.Error(err.Error())
	}
	student := Student{args[0], args[1], args[2], Gender(args[3]), args[4], args[5], MEMBER, studentDetails.DateOfJoining, studentDetails.DateOfJoining, STUDENT}
	data, err := json.Marshal(student)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(uuid, data)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(data)
}

func (sc *SchoolsChaincode) DeleteStudentDetails(stub shim.ChaincodeStubInterface) pb.Response {
	autherr := sc.CheckSchoolAuthorization(stub)
	if autherr != nil {
		return shim.Error(autherr.Error())
	}
	_, args := stub.GetFunctionAndParameters()
	err := ValidateArgumentCount(1, len(args))
	if err != nil {
		return shim.Error(err.Error())
	}
	uuid, err := GetStudentKey(stub, args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	_, err = GetState(stub, uuid)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.DelState(uuid)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success([]byte("student with id -> " + string(args[0]) + " successfully deleted from ledger"))
}

func (sc *SchoolsChaincode) GetStudentDetails(stub shim.ChaincodeStubInterface) pb.Response {
	autherr := sc.CheckMutualAuthorization(stub)
	if autherr != nil {
		return shim.Error(autherr.Error())
	}
	_, args := stub.GetFunctionAndParameters()
	err := ValidateArgumentCount(1, len(args))
	if err != nil {
		return shim.Error(err.Error())
	}
	uuid, err := GetStudentKey(stub, args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	data, err := GetState(stub, uuid)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(data)
}

func (sc *SchoolsChaincode) InitiateSchoolLeavingRequest(stub shim.ChaincodeStubInterface) pb.Response {
	autherr := sc.CheckCBSEAuthorization(stub)
	if autherr != nil {
		return shim.Error(autherr.Error())
	}
	_, args := stub.GetFunctionAndParameters()
	err := ValidateArgumentCount(2, len(args))
	if err != nil {
		return shim.Error(err.Error())
	}
	id, err := GetStudentKey(stub, args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	studentBytes, err := GetState(stub, id)
	if err != nil {
		return shim.Error(err.Error())
	}
	studentDetails := Student{}
	err = json.Unmarshal(studentBytes, &studentDetails)
	if err != nil {
		return shim.Error(err.Error())
	}
	if studentDetails.Status != MEMBER {
		return shim.Error("Student with id -> " + args[0] + " is not currently a member of school. Current status = " + string(studentDetails.Status))
	}
	uuid, err := uuid.NewV4()
	if err != nil {
		return shim.Error(err.Error())
	}
	applicationKey, err := GetStudentTransferApplicationKey(stub, uuid.String())
	if err != nil {
		return shim.Error(err.Error())
	}
	dateOfRequest := time.Now()
	application := StudentTransferApplication{uuid.String(), args[0], dateOfRequest, dateOfRequest, args[1], "", LEAVE_REQUEST, PENDING, STUDENT_TRANSFER_APPLICATION}
	applicationBytes, err := json.Marshal(application)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(applicationKey, applicationBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(applicationBytes)
}

func (sc *SchoolsChaincode) ResubmitSchoolLeavingRequest(stub shim.ChaincodeStubInterface) pb.Response {
	autherr := sc.CheckCBSEAuthorization(stub)
	if autherr != nil {
		return shim.Error(autherr.Error())
	}
	_, args := stub.GetFunctionAndParameters()
	err := ValidateArgumentCount(2, len(args))
	if err != nil {
		return shim.Error(err.Error())
	}
	id, err := GetStudentTransferApplicationKey(stub, args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	applicationBytes, err := GetState(stub, id)
	if err != nil {
		return shim.Error(err.Error())
	}
	applicationDetails := StudentTransferApplication{}
	err = json.Unmarshal(applicationBytes, &applicationDetails)
	if err != nil {
		return shim.Error(err.Error())
	}
	if applicationDetails.ApplicationType != LEAVE_REQUEST {
		return shim.Error("Application with ID -> " + args[0] + " is not a school leaving request")
	}
	switch applicationDetails.ApplicationStatus {
	case PENDING:
		return shim.Error("Application with ID -> " + args[0] + " is already under consideration")
	case REJECTED_BY_CBSE:
		return shim.Error("Application with ID -> " + args[0] + " is already rejected by CBSE")
	case LEAVE_REQUEST_ACCEPTED:
		return shim.Error("Application with ID -> " + args[0] + " has already been accepted")
	}
	applicationDetails.ApplicationStatus = PENDING
	applicationDetails.CBSERemark = args[0]
	applicationBytes, err = json.Marshal(applicationDetails)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(id, applicationBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success([]byte("Application with ID -> " + args[0] + " successfully resubmitted"))
}

func (sc *SchoolsChaincode) GetSchoolLeavingRequests(stub shim.ChaincodeStubInterface) pb.Response {
	autherr := sc.CheckSchoolAuthorization(stub)
	if autherr != nil {
		return shim.Error(autherr.Error())
	}
	_, args := stub.GetFunctionAndParameters()
	err := ValidateArgumentCount(0, len(args))
	if err != nil {
		return shim.Error(err.Error())
	}
	query := `{
		"selector" : {
			"doctype" : "` + string(STUDENT_TRANSFER_APPLICATION) + `",
			"applicationType" : ` + string(LEAVE_REQUEST) + `,
			"applicationStatus" : ` + string(PENDING) + `
		}
	}`
	resultsIterator, err := stub.GetQueryResult(query)
	if err != nil {
		return shim.Error(err.Error())
	}
	list := []StudentTransferApplication{}
	for resultsIterator.HasNext() {
		data, err := resultsIterator.Next()
		application := StudentTransferApplication{}
		err = json.Unmarshal(data.Value, &application)
		if err != nil {
			return shim.Error(err.Error())
		}
		list = append(list, application)
	}
	listBytes, err := json.Marshal(list)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(listBytes)
}

func (sc *SchoolsChaincode) InitiateSchoolAdmissionRequest(stub shim.ChaincodeStubInterface) pb.Response {
	autherr := sc.CheckCBSEAuthorization(stub)
	if autherr != nil {
		return shim.Error(autherr.Error())
	}
	_, args := stub.GetFunctionAndParameters()
	err := ValidateArgumentCount(7, len(args))
	if err != nil {
		return shim.Error(err.Error())
	}
	id, err := GetStudentKey(stub, args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	studentBytes, err := GetState(stub, id)
	if err != nil {
		dateOfJoining := time.Now()
		studentDetails := Student{id, args[1], args[2], Gender(args[3]), args[4], args[5], ADMISSION_PENDING, dateOfJoining, dateOfJoining, STUDENT}
		studentBytes, err := json.Marshal(studentDetails)
		if err != nil {
			return shim.Error(err.Error())
		}
		err = stub.PutState(id, studentBytes)
		if err != nil {
			return shim.Error(err.Error())
		}
	}
	studentDetails := Student{}
	err = json.Unmarshal(studentBytes, &studentDetails)
	if err != nil {
		return shim.Error(err.Error())
	}
	if studentDetails.Status != MEMBER {
		return shim.Error("Student with id -> " + args[0] + " is not currently a member of school. Current status = " + string(studentDetails.Status))
	}
	uuid, err := uuid.NewV4()
	if err != nil {
		return shim.Error(err.Error())
	}
	applicationKey, err := GetStudentTransferApplicationKey(stub, uuid.String())
	if err != nil {
		return shim.Error(err.Error())
	}
	dateOfRequest := time.Now()
	application := StudentTransferApplication{uuid.String(), args[0], dateOfRequest, dateOfRequest, args[6], "", ADMISSION_REQUEST, PENDING, STUDENT_TRANSFER_APPLICATION}
	applicationBytes, err := json.Marshal(application)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(applicationKey, applicationBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(applicationBytes)
}

func (sc *SchoolsChaincode) ResubmitSchoolAdmissionRequest(stub shim.ChaincodeStubInterface) pb.Response {
	autherr := sc.CheckCBSEAuthorization(stub)
	if autherr != nil {
		return shim.Error(autherr.Error())
	}
	_, args := stub.GetFunctionAndParameters()
	err := ValidateArgumentCount(2, len(args))
	if err != nil {
		return shim.Error(err.Error())
	}
	id, err := GetStudentTransferApplicationKey(stub, args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	applicationBytes, err := GetState(stub, id)
	if err != nil {
		return shim.Error(err.Error())
	}
	applicationDetails := StudentTransferApplication{}
	err = json.Unmarshal(applicationBytes, &applicationDetails)
	if err != nil {
		return shim.Error(err.Error())
	}
	if applicationDetails.ApplicationType != ADMISSION_REQUEST {
		return shim.Error("Application with ID -> " + args[0] + " is not a school admission request")
	}
	switch applicationDetails.ApplicationStatus {
	case PENDING:
		return shim.Error("Admission application with ID -> " + args[0] + " is already under consideration")
	case REJECTED_BY_CBSE:
		return shim.Error("Admission application with ID -> " + args[0] + " is already rejected by CBSE")
	case ADMISSION_REQUEST_ACCEPTED:
		return shim.Error("Admission application with ID -> " + args[0] + " has already been accepted")
	}
	applicationDetails.ApplicationStatus = PENDING
	applicationDetails.CBSERemark = args[0]
	applicationBytes, err = json.Marshal(applicationDetails)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(id, applicationBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success([]byte("Admission application with ID -> " + args[0] + " successfully resubmitted"))
}

func (sc *SchoolsChaincode) GetSchoolAdmissionRequests(stub shim.ChaincodeStubInterface) pb.Response {
	autherr := sc.CheckSchoolAuthorization(stub)
	if autherr != nil {
		return shim.Error(autherr.Error())
	}
	_, args := stub.GetFunctionAndParameters()
	err := ValidateArgumentCount(0, len(args))
	if err != nil {
		return shim.Error(err.Error())
	}
	query := `{
		"selector" : {
			"doctype" : "` + string(STUDENT_TRANSFER_APPLICATION) + `",
			"applicationType" : ` + string(ADMISSION_REQUEST) + `,
			"applicationStatus" : ` + string(PENDING) + `
		}
	}`
	resultsIterator, err := stub.GetQueryResult(query)
	if err != nil {
		return shim.Error(err.Error())
	}
	list := []StudentTransferApplication{}
	for resultsIterator.HasNext() {
		data, err := resultsIterator.Next()
		application := StudentTransferApplication{}
		err = json.Unmarshal(data.Value, &application)
		if err != nil {
			return shim.Error(err.Error())
		}
		list = append(list, application)
	}
	listBytes, err := json.Marshal(list)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(listBytes)
}

func (sc *SchoolsChaincode) RejectSchoolLeavingApplication(stub shim.ChaincodeStubInterface) pb.Response {
	autherr := sc.CheckMutualAuthorization(stub)
	if autherr != nil {
		return shim.Error(autherr.Error())
	}
	_, args := stub.GetFunctionAndParameters()
	err := ValidateArgumentCount(2, len(args))
	if err != nil {
		return shim.Error(err.Error())
	}
	id, err := GetStudentTransferApplicationKey(stub, args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	applicationBytes, err := GetState(stub, id)
	if err != nil {
		return shim.Error(err.Error())
	}
	applicationDetails := StudentTransferApplication{}
	err = json.Unmarshal(applicationBytes, &applicationDetails)
	if err != nil {
		return shim.Error(err.Error())
	}
	if applicationDetails.ApplicationType != LEAVE_REQUEST {
		return shim.Error("Application with ID -> " + args[0] + " is not a school leaving request")
	}
	switch applicationDetails.ApplicationStatus {
	case REJECTED_BY_CBSE:
		return shim.Error("School leaving application with ID -> " + args[0] + " is already rejected by CBSE")
	case REJECTED_BY_SCHOOL:
		return shim.Error("School leaving application with ID -> " + args[0] + " is already rejected by school")
	case LEAVE_REQUEST_ACCEPTED:
		return shim.Error("School leaving application with ID -> " + args[0] + " has already been accepted")
	}
	if IsCBSE(stub) {
		applicationDetails.ApplicationStatus = REJECTED_BY_CBSE
	} else {
		applicationDetails.ApplicationStatus = REJECTED_BY_SCHOOL
	}
	applicationDetails.LastModified = time.Now()
	applicationBytes, err = json.Marshal(applicationDetails)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(id, applicationBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success([]byte("School leaving application with ID -> " + args[0] + " successfully rejected"))
}

func (sc *SchoolsChaincode) AcceptSchoolLeavingApplication(stub shim.ChaincodeStubInterface) pb.Response {
	autherr := sc.CheckSchoolAuthorization(stub)
	if autherr != nil {
		return shim.Error(autherr.Error())
	}
	_, args := stub.GetFunctionAndParameters()
	err := ValidateArgumentCount(2, len(args))
	if err != nil {
		return shim.Error(err.Error())
	}
	id, err := GetStudentTransferApplicationKey(stub, args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	applicationBytes, err := GetState(stub, id)
	if err != nil {
		return shim.Error(err.Error())
	}
	applicationDetails := StudentTransferApplication{}
	err = json.Unmarshal(applicationBytes, &applicationDetails)
	if err != nil {
		return shim.Error(err.Error())
	}
	if applicationDetails.ApplicationType != LEAVE_REQUEST {
		return shim.Error("Application with ID -> " + args[0] + " is not a school leaving request")
	}
	switch applicationDetails.ApplicationStatus {
	case REJECTED_BY_CBSE:
		return shim.Error("School leaving application with ID -> " + args[0] + " is already rejected by CBSE")
	case REJECTED_BY_SCHOOL:
		return shim.Error("School leaving application with ID -> " + args[0] + " is already rejected by school")
	case LEAVE_REQUEST_ACCEPTED:
		return shim.Error("School leaving application with ID -> " + args[0] + " has already been accepted")
	}
	applicationDetails.ApplicationStatus = LEAVE_REQUEST_ACCEPTED
	applicationDetails.LastModified = time.Now()
	applicationBytes, err = json.Marshal(applicationDetails)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(id, applicationBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success([]byte("School leaving application with ID -> " + args[0] + " successfully accepted"))
}

func (sc *SchoolsChaincode) RejectSchoolAdmissionApplication(stub shim.ChaincodeStubInterface) pb.Response {
	autherr := sc.CheckMutualAuthorization(stub)
	if autherr != nil {
		return shim.Error(autherr.Error())
	}
	_, args := stub.GetFunctionAndParameters()
	err := ValidateArgumentCount(2, len(args))
	if err != nil {
		return shim.Error(err.Error())
	}
	id, err := GetStudentTransferApplicationKey(stub, args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	applicationBytes, err := GetState(stub, id)
	if err != nil {
		return shim.Error(err.Error())
	}
	applicationDetails := StudentTransferApplication{}
	err = json.Unmarshal(applicationBytes, &applicationDetails)
	if err != nil {
		return shim.Error(err.Error())
	}
	if applicationDetails.ApplicationType != ADMISSION_REQUEST {
		return shim.Error("Application with ID -> " + args[0] + " is not a school admission request")
	}
	switch applicationDetails.ApplicationStatus {
	case REJECTED_BY_CBSE:
		return shim.Error("Admission application with ID -> " + args[0] + " is already rejected by CBSE")
	case REJECTED_BY_SCHOOL:
		return shim.Error("Admission application with ID -> " + args[0] + " is already rejected by school")
	case ADMISSION_REQUEST_ACCEPTED:
		return shim.Error("Admission application with ID -> " + args[0] + " has already been accepted")
	}
	if IsCBSE(stub) {
		applicationDetails.ApplicationStatus = REJECTED_BY_CBSE
	} else {
		applicationDetails.ApplicationStatus = REJECTED_BY_SCHOOL
	}
	applicationDetails.LastModified = time.Now()
	applicationBytes, err = json.Marshal(applicationDetails)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(id, applicationBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success([]byte("School admission application with ID -> " + args[0] + " successfully rejected"))
}

func (sc *SchoolsChaincode) AcceptSchoolAdmissionApplication(stub shim.ChaincodeStubInterface) pb.Response {
	autherr := sc.CheckSchoolAuthorization(stub)
	if autherr != nil {
		return shim.Error(autherr.Error())
	}
	_, args := stub.GetFunctionAndParameters()
	err := ValidateArgumentCount(2, len(args))
	if err != nil {
		return shim.Error(err.Error())
	}
	id, err := GetStudentTransferApplicationKey(stub, args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	applicationBytes, err := GetState(stub, id)
	if err != nil {
		return shim.Error(err.Error())
	}
	applicationDetails := StudentTransferApplication{}
	err = json.Unmarshal(applicationBytes, &applicationDetails)
	if err != nil {
		return shim.Error(err.Error())
	}
	if applicationDetails.ApplicationType != ADMISSION_REQUEST {
		return shim.Error("Application with ID -> " + args[0] + " is not a school admission request")
	}
	switch applicationDetails.ApplicationStatus {
	case REJECTED_BY_CBSE:
		return shim.Error("Admission application with ID -> " + args[0] + " is already rejected by CBSE")
	case REJECTED_BY_SCHOOL:
		return shim.Error("Admission application with ID -> " + args[0] + " is already rejected by school")
	case ADMISSION_REQUEST_ACCEPTED:
		return shim.Error("Admission application with ID -> " + args[0] + " has already been accepted")
	}
	applicationDetails.ApplicationStatus = ADMISSION_REQUEST_ACCEPTED
	applicationDetails.LastModified = time.Now()
	applicationBytes, err = json.Marshal(applicationDetails)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(id, applicationBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success([]byte("School admission application with ID -> " + args[0] + " successfully accepted"))
}

func main() {
	scc := new(SchoolsChaincode)
	scc.testing = false
	err := shim.Start(scc)
	if err != nil {
		fmt.Println(fmt.Sprintf("Error occurring in starting chaincode : %s", err.Error()))
	}
}
