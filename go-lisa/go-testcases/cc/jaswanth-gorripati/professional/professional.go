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
type Professionalchaincode struct{

}

//response request from the student to get approved or not by university admin
type ExperienceReport struct{

	EmployeeId string `json:"employeeId"`
	Designation string `json:"designation"`
	YearOfJoining time.Time `json:"yoj"`
	CompanyName string `json:"companyname"`
	Location string `json:"location"`
	WorkExperience float64 `json:"experience"`
	Timeregistred time.Time `json:"timeenrolled"`
	StillWorking  bool `json:"working"`  
    DateoFReliving time.Time `json:"dor"`
	Status string      `json:"status"`  
	Remarks string     `json:"remarks"`
	RequestType string `json:requestType`

}


var logger = shim.NewLogger("Professional_CC")

func main() {
	err := shim.Start(new(Professionalchaincode))
	if err != nil {
		logger.Errorf("Error while Initializing Professional chaincode- %s",err)
	}
}

func(t *Professionalchaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	logger.Info("########### Professional Chaincode Init ###########")
	fmt.Println("The Network as Been started by Professional smartcontract")
	fmt.Println("Ready To Take Approval and Requests for digital Expernice ")
	return shim.Success(nil)
}

func (t *Professionalchaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	fmt.Println(" Invoke ");
	function,args := stub.GetFunctionAndParameters()
	if function == "init" {
		return t.Init(stub)	
	}else if function == "ExperienceRequest" {
		return ExperienceRequest(stub, args)
	}else if function =="getdetailsbyattributes"{
		return getdetailsbyattributes(stub,args)
	}else if function=="hypernymprocess"{
		return hypernymprocess(stub,args)
	}
	
    logger.Errorf("Received unknown invoke function name -%s",function)
	return shim.Error("Received unknown invoke function name -'" + function + "'")
}


func diff(a, b time.Time) (year, month int) {

    if a.Location() != b.Location() {
        b = b.In(a.Location())
    }
    if a.After(b) {
        a, b = b, a
    }
    y1, M1, d1 := a.Date()
    y2, M2, d2 := b.Date()

    h1, m1, s1 := a.Clock()
    h2, m2, s2 := b.Clock()

    year = int(y2 - y1)
    month = int(M2 - M1)
    day := int(d2 - d1)
    hour := int(h2 - h1)
    min := int(m2 - m1)
    sec := int(s2 - s1)

    // Normalize negative values 90:e7:c4:18:76:9d
    if sec < 0 {
        sec += 60
        min--
    }
    if min < 0 {
        min += 60
        hour--
    }
    if hour < 0 {
        hour += 24
        day--
    }
    if day < 0 {
        // days in month:
        t := time.Date(y1, M1, 32, 0, 0, 0, 0, time.UTC)
        day += 32 - t.Day()
        month--
    }
    if month < 0 {
        month += 12
        year--
    } 
    return
}

func  ExperienceRequest(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	if len(args)<7{
		return shim.Error("The Requested  Expernice not full fill the all Requriments")
	}

    const shortForm = "2006-01-02"
	EmployeeId, ok, err := cid.GetAttributeValue(stub, "id")
    if err !=nil{
    	shim.Error(err.Error())
	}else if !ok {
		shim.Error("The client identity does not possess the attribute ");
	}
	RequestType := args[0]
	Designation :=args[1]
	YearOfJoining,err:= time.Parse(shortForm,args[2])
	CompanyName :=args[3]
	Location :=args[4]
	dor,err:= time.Parse(shortForm,args[5])
    StillWorking,err:=strconv.ParseBool(args[6])

	if err!=nil{
		return shim.Error(err.Error())
	}
	status:="pending" 
	Current_date:=time.Now().Local()


	if StillWorking {

		fmt.Println(dor)
		fmt.Println(StillWorking)
			
		year,month:=diff(YearOfJoining,Current_date)
		year1:=strconv.Itoa(year)
		month1:=strconv.Itoa(month)
		working:=""+year1+"."+month1+""
		exp,err:=strconv.ParseFloat(working,64)
		if err!=nil{
			return shim.Error(err.Error())
		}        
			
		experiencereport:=ExperienceReport{EmployeeId,Designation,YearOfJoining,CompanyName,
		Location,exp,Current_date,StillWorking,dor,status,"",RequestType}
		employeerequestmarshall,err:=json.Marshal(experiencereport)
		if err !=nil{
			logger.Errorf("error occured while converting to json")
			return shim.Error(err.Error())
		}
		err=stub.PutState(""+EmployeeId+"_"+CompanyName+"",employeerequestmarshall)
		if err!=nil{
			logger.Errorf("error occured while updating  to ledger")
			return shim.Error(err.Error())
		}
		return shim.Success(nil);
	}else{

		year2,month2:=diff(YearOfJoining,dor)
		year3:=strconv.Itoa(year2)
		month3:=strconv.Itoa(month2)
		working:=""+year3+"."+month3+""
		exp,err:=strconv.ParseFloat(working,64)
		if err!=nil{
			return shim.Error(err.Error())
		}        

		experiencereport:=ExperienceReport{EmployeeId,Designation,YearOfJoining,CompanyName,
		Location,exp,Current_date,StillWorking,dor,status,"",RequestType}
		employeerequestmarshall,err:=json.Marshal(experiencereport)
		if err !=nil{
			logger.Errorf("error occured while converting to json")
			return shim.Error(err.Error())
		}
		err=stub.PutState(""+EmployeeId+"_"+CompanyName+"",employeerequestmarshall)
		if err!=nil{
			logger.Errorf("error occured while updating  to ledger")
			return shim.Error(err.Error())
		}
		logger.Info("details are entered")
		return shim.Success(nil);
	}

} 


////////////rich query function for getting particular student  ////////////////

func hypernymprocess(stub shim.ChaincodeStubInterface,args []string) pb.Response{

	if len(args)<3{
		return shim.Error("the arguments which passed or not upto the mark")
	}
    
    EmployeeId:=args[0]
    CompanyName:=args[1]
    status:=args[2]
    remarks:=args[3]
	accountType,ok,err := cid.GetAttributeValue(stub,"accountType");
	if err != nil {
		shim.Error("There was an error trying to retrieve accountType attribute");
	}
	if !ok {
		shim.Error("The client identity does not possess accountType attribute");
	}
	if accountType!="employer"{
		shim.Error("not authorized for this request");
	}
	invokerCompany,ok,err := cid.GetAttributeValue(stub,"worksin");
	if err != nil {
		shim.Error("There was an error trying to retrieve accountType attribute");
	}
	if !ok {
		shim.Error("The client identity does not possess accountType attribute");
	}
	if invokerCompany!=CompanyName{
		shim.Error("Invoker is not working for this organisation");
	}
    var variable=""+EmployeeId+"_"+CompanyName+""
    //strconv.string(variable)
    Employeerequestjson:=ExperienceReport{}
    newemployeerequest,err:=stub.GetState(variable)

  	if err!=nil{
    	logger.Errorf("error occured while updating  to ledger")
		return shim.Error(err.Error())
    }
    json.Unmarshal([]byte(newemployeerequest),&Employeerequestjson)

    Current_date:=time.Now().Local()
    experiencereport:=ExperienceReport{Employeerequestjson.EmployeeId,Employeerequestjson.Designation,Employeerequestjson.YearOfJoining,Employeerequestjson.CompanyName,
            	Employeerequestjson.Location,Employeerequestjson.WorkExperience,Current_date,Employeerequestjson.StillWorking,Employeerequestjson.DateoFReliving,status,remarks,Employeerequestjson.RequestType}
             
    employeeresponsemarshall,err:=json.Marshal(experiencereport)

	if err !=nil{
		logger.Errorf("error occured while converting to json")
		return shim.Error(err.Error())
	}

    err=stub.PutState(""+EmployeeId+"_"+CompanyName+"",employeeresponsemarshall)
    if err!=nil{
    	logger.Errorf("error occured while updating  to ledger")
		return shim.Error(err.Error())
    }

	logger.Info("details are updated")
	return shim.Success(nil);
 }

func getdetailsbyattributes(stub shim.ChaincodeStubInterface,args []string) pb.Response{

	if len(args)>0{
		return shim.Error("this function did not  expect any argument")
	}
	queryString := "";
	accountType,ok,err := cid.GetAttributeValue(stub,"accountType");
	if err != nil {
		shim.Error("There was an error trying to retrieve accountType attribute");
	}
	if !ok {
		shim.Error("The client identity does not possess accountType attribute");
	}
	if accountType == "employee"{
		id,_,_ := cid.GetAttributeValue(stub,"id");
		queryString = fmt.Sprintf("{\"selector\":{\"employeeId\":\"%s\"}}}",id)
	}else if accountType == "employer"{
		companyname,_,_ := cid.GetAttributeValue(stub,"worksin");
		queryString = fmt.Sprintf("{\"selector\":{\"companyname\":\"%s\"}}}",companyname)
	}else{
		shim.Error("Not able to get invoker's certificate ")
	}
	fmt.Printf("query string %v",queryString);
	resultsIterator,err:= stub.GetQueryResult(queryString)
	if err != nil {
		return shim.Error(err.Error())
	}
	defer resultsIterator.Close()
  	var buffer bytes.Buffer
	buffer.WriteString("[")
	bArrayMemberAlreadyWritten := false
    for resultsIterator.HasNext() {
    	fmt.Println("step-1")
		queryResponse, err := resultsIterator.Next()

		if err != nil {
			return shim.Error(err.Error())
		}
	    var employeerequest ExperienceReport
		json.Unmarshal(queryResponse.Value, &employeerequest)   
        // Add a comma before array members, suppress it for the first array member
		if bArrayMemberAlreadyWritten == true {
			buffer.WriteString(",")
		}
		 
		buffer.WriteString("{")
        buffer.WriteString("\"Record\":")
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