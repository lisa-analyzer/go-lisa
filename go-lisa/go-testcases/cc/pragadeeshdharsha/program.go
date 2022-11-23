package main

import (
	"encoding/json"
	"fmt"
	"strconv"
	"strings"
	"time"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)

type chainCode struct {
}

type programInfo struct {
	ProgramName        string    //[1]
	ProgramAnchor      string    //BusinessID //[2]
	ProgramType        string    //[3]
	ProgramStartDate   time.Time //auto generated as created
	ProgramEndDate     time.Time //[4]
	ProgramLimit       int64     //[5]
	ProgramROI         int64     //[6]
	ProgramExposure    string    //[7]
	DiscountPercentage int64     //[8]
	DiscountPeriod     int64     //[9]
	SanctionAuthority  string    //[10]
	SanctionDate       time.Time //auto generated as created
	RepaymentAcNum     string    //[11]
	RepaymentWalletID  string    //taken from program anchors business id
}

func (c *chainCode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	return shim.Success(nil)
}

func (c *chainCode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	function, args := stub.GetFunctionAndParameters()

	if function == "writeProgram" {
		//Creates a new Program Information
		return writeProgram(stub, args)
	} else if function == "getProgram" {
		//Retrieves the Program Information
		return getProgram(stub, args)
	} else if function == "programIDexists" {
		//Checks the existence of ProgramID
		return programIDexists(stub, args[0])
	} else if function == "updateProgramInfo" {
		/*
			Updates Program Limit, Program ROI,
			Discount Percentage,Discount Period and Program end date if required
		*/
		return updateProgramInfo(stub, args)
	}
	return shim.Error("No function named " + function + " in Programsssssss")
}

func writeProgram(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 11 {
		xLenStr := strconv.Itoa(len(args))
		return shim.Error("Invalid number of arguments in writeProgram (required:11) given:" + xLenStr)
	}

	//Checking existence of programID
	response := programIDexists(stub, args[0])
	if response.Status != shim.OK {
		return shim.Error(response.Message)
	}

	//Checking existence of businessID
	chaincodeArgs := toChaincodeArgs("busIDexists", args[2])
	response = stub.InvokeChaincode("businesscc", chaincodeArgs, "myc")
	if response.Status == shim.OK {
		return shim.Error("BusinessId " + args[2] + " does not exits")
	}

	pTypes := map[string]bool{
		"ar":                  true,
		"ap":                  true,
		"df":                  true,
		"accounts payable":    true,
		"accounts receivable": true,
		"dealer finance":      true,
	}

	//Checking whether the given argument is a valid type
	pTypeLower := strings.ToLower(args[3])
	if !pTypes[pTypeLower] {
		return shim.Error("Invalid program type" + pTypeLower)
	}

	//ProgramStartDate -> pSDate
	pSDate := time.Now()

	//ProgramEndDate -> pEDate
	pEDate, err := time.Parse("02/01/2006", args[4])
	if err != nil {
		return shim.Error(err.Error())
	}

	pLimit, err := strconv.ParseInt(args[5], 10, 64)
	if err != nil {
		return shim.Error("Invalid Program limit " + args[6])
	}

	pROI, err := strconv.ParseInt(args[6], 10, 64)
	if err != nil {
		return shim.Error("Invalid Rate of Interest in writeProgram")
	}

	pExposure := map[string]bool{
		"buyer":  true,
		"seller": true,
	}

	pExposureLower := strings.ToLower(args[7])

	if !pExposure[pExposureLower] {
		return shim.Error("Invalid Program Exposure " + pExposureLower)
	}

	dPercentage, err := strconv.ParseInt(args[8], 10, 64)
	if err != nil {
		return shim.Error("Invalid discount percentage")
	}

	dPeriod, err := strconv.ParseInt(args[9], 10, 64)
	if err != nil {
		return shim.Error("Invalid discount period")
	}

	//SanctionDate -> sDate
	sDate := time.Now()

	//Wallet ID for repayment
	chaincodeArgs = toChaincodeArgs("getWalletID", args[2], "main")
	response = stub.InvokeChaincode("businesscc", chaincodeArgs, "myc")
	if response.Status != shim.OK {
		return shim.Error(response.Message)
	}
	repayWalletID := string(response.GetPayload())
	pInfo := programInfo{args[1], args[2], pTypeLower, pSDate, pEDate, pLimit, pROI, pExposureLower, dPercentage, dPeriod, args[10], sDate, args[11], repayWalletID}
	programInfoBytes, _ := json.Marshal(pInfo)
	err = stub.PutState(args[0], programInfoBytes)
	return shim.Success(nil)
}

func programIDexists(stub shim.ChaincodeStubInterface, prgrmID string) pb.Response {
	ifExists, _ := stub.GetState(prgrmID)
	if ifExists != nil {
		fmt.Println(ifExists)
		return shim.Error("ProgramId " + prgrmID + " exits. Cannot create new ID")
	}
	return shim.Success(nil)
}
func updateProgramInfo(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	/*
		args[0] -> ProgramID
		args[1] -> Program Limit, Program ROI, Discount Percentage,Discount Period and Program End Date
		args[2] -> values
	*/

	pInfo := programInfo{}
	pInfoBytes, err := stub.GetState(args[0])

	if err != nil {
		return shim.Error(err.Error())
	} else if pInfoBytes == nil {
		return shim.Error("No information on this programID(updateProgramInfo): " + args[0])
	}

	err = json.Unmarshal(pInfoBytes, &pInfo)
	if err != nil {
		return shim.Error(err.Error())
	}

	lowerStr := strings.ToLower(args[1])

	if lowerStr == "program end date" {
		pEDate, err := time.Parse("02/01/2006", args[2])
		if err != nil {
			return shim.Error("updateProgramInfo updating programEndDate" + err.Error())
		}
		pInfo.ProgramEndDate = pEDate
	}

	value, err := strconv.ParseInt(args[2], 10, 64)
	if err != nil {
		return shim.Error("value (updateProgramInfo):" + err.Error())
	}

	if lowerStr == "program limit" {
		pInfo.ProgramLimit = value
	} else if lowerStr == "program roi" {
		pInfo.ProgramROI = value
	} else if lowerStr == "discount percentage" {
		pInfo.DiscountPercentage = value
	} else if lowerStr == "discount period" {
		pInfo.DiscountPeriod = value
	}

	return shim.Success([]byte("Program info updation successful"))
}
func getProgram(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	if len(args) != 1 {
		xLenStr := strconv.Itoa(len(args))
		return shim.Error("Invalid number of arguments in getProgram (required:1) given:" + xLenStr)
	}

	pInfo := programInfo{}
	pInfoBytes, err := stub.GetState(args[0])

	if err != nil {
		return shim.Error(err.Error())
	} else if pInfoBytes == nil {
		return shim.Error("No information on this programID: " + args[0])
	}

	err = json.Unmarshal(pInfoBytes, &pInfo)
	if err != nil {
		return shim.Error(err.Error())
	}

	printProgramInfo := fmt.Sprintf("%+v", pInfo)

	return shim.Success([]byte(printProgramInfo))

}
func toChaincodeArgs(args ...string) [][]byte {
	bargs := make([][]byte, len(args))
	for i, arg := range args {
		bargs[i] = []byte(arg)
	}
	return bargs
}

func main() {
	err := shim.Start(new(chainCode))
	if err != nil {
		fmt.Printf("Error starting Program chaincode: %s\n", err)
	}
}
