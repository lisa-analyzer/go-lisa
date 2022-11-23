package main


import (
	 "fmt"
	 "bytes"
     "encoding/json"
     "strconv"
     "time"


	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/hyperledger/fabric/core/chaincode/lib/cid"
	pb "github.com/hyperledger/fabric/protos/peer"
)

//simple chaincode for univeristy 
type Academicchaincode struct{

}

//response request from the student to get approved or not by university admin
type studentrequest struct{

	StudentId string `json:"studentId"`
	Degree string `json:"degree"`
	Percentage  float64 `json:marks"`
	YearOfPassOut string `json:"yop"`
	CollegeName string `json:"clgname"`
	Location string `json:"location"`
	University_Board string `json:"board"`
	RequestType string `json:requestType`
	Timeregistred time.Time `json:"timeenrolled"`
	Status string `json:"status"`  
	Remarks string    `json:"remarks"`

}


var logger = shim.NewLogger("university_CC")

func main() {
	   err := shim.Start(new(Academicchaincode))
	   if err != nil {
	   logger.Errorf("Error while Initializing Academic Chaincode - %s",err)
	    }
}

func(t *Academicchaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {

		logger.Info("########### Academic Chaincode Init ###########")
     	fmt.Println("The Network as Been started by University smartcontract")
    	fmt.Println("Ready For Approval and Requests of Degree")
    	return shim.Success(nil)
}

func (t *Academicchaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	fmt.Println(" Invoke ");
	function,args := stub.GetFunctionAndParameters()
	if function == "init" {
		return t.Init(stub)	
	}else if function == "RequestEnroll" {
		return RequestEnroll(stub, args);
	}else if function =="getallrequests"{
		return getallrequests(stub,args)
	}else if function=="hypernymprocess"{
		return hypernymprocess(stub,args)
	}
	
    logger.Errorf("Received unknown invoke function name -%s",function)
	return shim.Error("Received unknown invoke function name -'" + function + "'")
}


func  RequestEnroll(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	if len(args)%7 != 0 {
		return shim.Error("The Requested  Degree not full fill the all Requriments")
	}
	val,ok,err := cid.GetAttributeValue(stub,"accountType");
	if err != nil {
		shim.Error("There was an error trying to retrieve accountType attribute");
	}
	if !ok {
		shim.Error("The client identity does not possess accountType attribute");
	}
	if(val != "student"){
		shim.Error("Authorisation Failed : Not a Student account To Request")
	}
	val1,ok,err := cid.GetAttributeValue(stub,"id")
	if err != nil {
		shim.Error("There was an error trying to retrieve accountType attribute");
	}
	if !ok {
		shim.Error("The student identity does not possess accountType attribute");
	}
	if(val1 == ""){
		shim.Error("NO id Found")
	}
	studentId:= val1;
	stauts:="pending" 
	Current_date:=time.Now().Local();
	var request []studentrequest ;
	for i:=0;i<len(args);i+=7{
		degree  := args[i]
		percentage,err :=strconv.ParseFloat(args[i+1],64)
		if err!=nil{
			shim.Error("cannot convert percentage")
		}
		yop:=args[i+2]
		clgname:=args[i+3]
		localArea:= args[i+4]
		university_board:=args[i+5]
		requestType := args[i+6]
		temp := studentrequest{studentId,degree,percentage,yop,clgname,localArea,university_board,requestType,Current_date,stauts,""}
		request = append(request,temp);
	}
	
	//studentrequest:=studentrequest{studentId,degree,percentage,yop,clgname,localArea,university_board,Current_date,stauts,""}
	for j:=0;j<len(request);j++{
		studentrequestmarshall,err:=json.Marshal(request[j])
		
			if err !=nil{
				logger.Errorf("error occured while converting to json")
				return shim.Error(err.Error())
			}
		
		
			err=stub.PutState(""+request[j].StudentId+"_"+request[j].Degree+"",studentrequestmarshall)
			if err!=nil{
				logger.Errorf("error occured while updating  to ledger")
				return shim.Error(err.Error())
			}
		
	}

     logger.Info("details are entered")
     return shim.Success(nil);
} 


////////////rich query function for getting particular student  ////////////////

func hypernymprocess(stub shim.ChaincodeStubInterface,args []string) pb.Response{

     
	if len(args)<3{
		return shim.Error("the arguments which passed or not upto the mark")
	}
	val,ok,err := cid.GetAttributeValue(stub,"accountType");
	if err != nil {
		shim.Error("There was an error trying to retrieve accountType attribute");
	}
	if !ok {
		shim.Error("The client identity does not possess accountType attribute");
	}
	if(val != "universityStaff"){
		shim.Error("not Authorized");
	}
	if(val == "universityStaff"){
		val1,_,_ := cid.GetAttributeValue(stub,"isAdmin");
		if(val1 != "true"){
			shim.Error("Authorisation Failed : No right to approve or Denie")
		}
	}
	studentId:=args[0]
    degree:=args[1]
    status:=args[2]
    remarks:=args[3]

    var variable=""+studentId+"_"+degree+""
    //strconv.string(variable)
    studentrequestjson:=studentrequest{}

    newstudentrequest,err:=stub.GetState(variable)

   if err!=nil{
    	logger.Errorf("error occured while updating  to ledger")
		return shim.Error(err.Error())
    }
    json.Unmarshal([]byte(newstudentrequest),&studentrequestjson)


    Current_date:=time.Now().Local()
    studentupdaterespnose:=studentrequest{studentrequestjson.StudentId,studentrequestjson.Degree,studentrequestjson.Percentage,studentrequestjson.YearOfPassOut,
    	studentrequestjson.CollegeName,studentrequestjson.Location,studentrequestjson.University_Board,studentrequestjson.RequestType,
    	Current_date,status,remarks}

    studentresponsemarshall,err:=json.Marshal(studentupdaterespnose)

	if err !=nil{
		logger.Errorf("error occured while converting to json")
		return shim.Error(err.Error())
	}


    err=stub.PutState(""+studentId+"_"+degree+"",studentresponsemarshall)
    if err!=nil{
    	logger.Errorf("error occured while updating  to ledger")
		return shim.Error(err.Error())
    }


     logger.Info("details are updated")
     return shim.Success(nil);
 }

func getallrequests(stub shim.ChaincodeStubInterface,args []string) pb.Response{

	mspid, err := cid.GetMSPID(stub);
	fmt.Printf("got MSP : %v",mspid);

	val,ok,err := cid.GetAttributeValue(stub,"accountType");
	if err != nil {
		shim.Error("There was an error trying to retrieve accountType attribute");
	 }
	 if !ok {
		shim.Error("The client identity does not possess accountType attribute");
	 }
	 queryString := "";
	 fmt.Printf("account Type : %v",val)
	if(val == "student" || val =="employee"){
		val1,_,_ := cid.GetAttributeValue(stub,"id");
		fmt.Printf("got request for ID  = %v",val1);
		queryString = fmt.Sprintf("{\"selector\":{\"studentId\":\"%s\"}}}",val1)
	}else if(val == "universityStaff"){
		val2,_,_ := cid.GetAttributeValue(stub,"worksin");
		queryString = fmt.Sprintf("{\"selector\":{\"board\":\"%s\"}}}",val2)
	}else if(val == "employer"){
		shim.Error("employer cannot request this function");
	}
	fmt.Printf("querystring = %v",queryString)
	resultsIterator,err:= stub.GetQueryResult(queryString)
	if err != nil {
		return shim.Error(err.Error())
	}
	defer resultsIterator.Close()
  	var buffer bytes.Buffer
	buffer.WriteString("[")
	bArrayMemberAlreadyWritten := false
    for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()

		if err != nil {
		return shim.Error(err.Error())
		}
	    var studentrequest1 studentrequest
		json.Unmarshal(queryResponse.Value, &studentrequest1)   
        // Add a comma before array members, suppress it for the first array member
		if bArrayMemberAlreadyWritten == true {
			buffer.WriteString(",")
		}
		 
		buffer.WriteString("{\"Details\":")
     	// Record is a JSON object, so we write as-is
		buffer.WriteString(string(queryResponse.Value))
		buffer.WriteString("}")
		bArrayMemberAlreadyWritten = true
		//fmt.Printf("value is %s",string(queryResponse.Value))
	}
	buffer.WriteString("]")
   	fmt.Printf("- getQueryResultForQueryString queryResult:\n%s\n", buffer.String())
	return shim.Success(buffer.Bytes()) 
}