/*
 * SPDX-License-Identifier: Apache-2.0
 * Aangevuld door Joeri Heyvaert en Paul Schevelenbos i.k.v. S3 Blockchain Project/HOWEST
 * Datum: 10/01/2021
 */

package main

import (
	"encoding/json"
	"fmt"
	"strconv"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	sc "github.com/hyperledger/fabric-protos-go/peer"
	// Paul: strconv toegevoegd voor conversion van string naar int
)

// Chaincode is the definition of the chaincode structure.
type Chaincode struct {
}

type Stakeholder struct {
	ObjectType    string `json:"docType"`
	ContractID    string `json:"contractId"`
	UID           string `json:"uId"`
	Rol           string `json:"rol"`
	WalletBalance string `json:"walletBalance"`
	Fee           string `json:"fee"`
}

type CDR struct {
	ObjectType             string           `json:"docType"`
	RecordID               string           `json:"recordId"`
	CPOContractID          string           `json:"cpoContractId"`
	CountryCode            string           `json:"countryCode"`
	StartDateTime          string           `json:"startDateTime"`
	EndDateTime            string           `json:"endDateTime"`
	SessionID              string           `json:"sessionId"`
	CDRTokenUID            string           `json:"cdrTokenUid"`
	CDRTokenType           string           `json:"cdrTokenType"`
	EVDRContractID         string           `json:"evdrContractId"`
	AuthMethod             string           `json:"authMethod"`
	AuthorizationReference string           `json:"authorizationReference"`
	CDRLocation            string           `json:"cdrLocation"`
	MeterId                string           `json:"meterId"`
	Currency               string           `json:"currency"`
	Tariffs                []Tariff         `json:"tariffs"`
	ChargingPeriods        []ChargingPeriod `json:"chargingPeriods"`
	SignedData             string           `json:"signedData"`
	TotalCost              string           `json:"totalCost"`
	TotalFixedCost         string           `json:"totalFixedCost"`
	TotalEnergy            string           `json:"totalEnergy"`
	TotalEnergyCost        string           `json:"totalEnergyCost"`
	TotalTime              string           `json:"totalTime"`
	TotalTimeCost          string           `json:"totalTimeCost"`
	TotalParkingTime       string           `json:"totalParkingTime"`
	TotalParkingCost       string           `json:"totalParkingCost"`
	TotalReservationCost   string           `json:"totalReservationCost"`
	Remark                 string           `json:"remark"`
	InvoiceReferenceID     string           `json:"invoiceReferenceId"`
	Credit                 string           `json:"credit"`
	CreditReferenceID      string           `json:"creditReferenceId"`
	LastUpdated            string           `json:"lastUpdated"`
}

type Tariff struct {
	TariffID           string `json:"tariffId"`
	PriceComponentType string `json:"priceComponentType"`
	Price              string `json:"price"`
	Vat                string `json:"vat"`
	StepSize           string `json:"stepSize"`
	TariffRestriction  string `json:"tariffRestriction"`
}

type ChargingPeriod struct {
	UID             string `json:"uId"`
	StartDateTime   string `json:"startDateTime"`
	DimensionType   string `json:"dimensionType"`
	DimensionVolume string `json:"dimensionVolume"`
	TariffID        string `json:"tariffId"`
}

func main() {
	err := shim.Start(new(Chaincode))
	if err != nil {
		panic(err)
	}
}

// Init is called when the chaincode is instantiated by the blockchain network.
func (cc *Chaincode) Init(stub shim.ChaincodeStubInterface) sc.Response {
	fcn, params := stub.GetFunctionAndParameters()
	fmt.Println("Init()", fcn, params)
	return shim.Success(nil)
}

// Invoke is called as a result of an application request to run the chaincode.
func (cc *Chaincode) Invoke(stub shim.ChaincodeStubInterface) sc.Response {
	fn, args := stub.GetFunctionAndParameters()

	var result string
	var err error

	if fn == "query" {
		result, err = query(stub, args)
	} else if fn == "getBalance" {
		result, err = getBalance(stub, args)
	} else if fn == "createStakeholder" {
		result, err = createStakeholder(stub, args)
	} else if fn == "transfer" {
		result, err = transfer(stub, args)
	} else if fn == "registerCDR" {
		result, err = registerCDR(stub, args)
	} else if fn == "settlementCDR" {
		result, err = settlementCDR(stub, args)
	} else if fn == "processCDR" {
		result, err = processCDR(stub, args)
	}

	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success([]byte(result))
}

func query(stub shim.ChaincodeStubInterface, args []string) (string, error) {
	if len(args) != 1 {
		return "", fmt.Errorf("Incorrect arguments, expecting key")
	}

	value, err := stub.GetState(args[0])

	if err != nil {
		return "", fmt.Errorf("Error while retrieving record with key: %s", args[0])
	}

	if value == nil {
		return "", fmt.Errorf("No value found when retrieving record with key: %s", args[0])
	}

	return string(value), nil // Paul: in JSON byte formaat returned
}

func getBalance(stub shim.ChaincodeStubInterface, args []string) (string, error) {
	if len(args) != 1 {
		return "", fmt.Errorf("Incorrect arguments, expecting one contractId")
	}

	contractId := args[0]

	// ==== Get Stakeholder based on contractId ====
	myStakeholderAsBytes, err := stub.GetState(contractId)
	myStakeholder := Stakeholder{}
	if err != nil {
		return "", fmt.Errorf("getBalance failed retrieving stakeholder with error: %s", err)
	}

	if myStakeholderAsBytes == nil {
		return "", fmt.Errorf("getBalance failed retrieving stakeholder: %s", contractId)
	}

	json.Unmarshal(myStakeholderAsBytes, &myStakeholder)

	return string(myStakeholder.WalletBalance), nil
}

func createStakeholder(stub shim.ChaincodeStubInterface, args []string) (string, error) {
	if len(args) != 5 {
		return "", fmt.Errorf("Incorrect arguments, expecting 5")
	}

	contractId := args[0]
	uId := args[1]
	rol := args[2]
	walletBalance := args[3]
	fee := args[4]

	objectType := "stakeholder"
	stakeholder := &Stakeholder{objectType, contractId, uId, rol, walletBalance, fee}
	stakeholderJSONasBytes, err := json.Marshal(stakeholder)

	if err != nil {
		return "", fmt.Errorf("Failed to set stakeholder with contract ID: %s", args[0])
	}

	err = stub.PutState(contractId, stakeholderJSONasBytes)
	if err != nil {
		return "", fmt.Errorf("Failed to put state of stakeholder with contract ID: %s", args[0])
	}

	return string(stakeholderJSONasBytes), nil
}

func transfer(stub shim.ChaincodeStubInterface, args []string) (string, error) {

	if len(args) != 3 {
		return "", fmt.Errorf("Incorrect number of arguments for transfer, expecting 3")
	} // parameters: from_stakeholder_id, to_stakeholder_id, amount

	// var fromStakeholderId, toStakeholderId, amount string
	fromStakeholderId := args[0]
	toStakeholderId := args[1]
	amount := args[2]

	// ==== Retrieve fromStakeholder info ====
	fromStakeholderAsBytes, err := stub.GetState(fromStakeholderId)
	if err != nil {
		return "", fmt.Errorf("failed during transfer, retrieving fromStakeholder with error: %s", err)
	}
	if fromStakeholderAsBytes == nil {
		return "", fmt.Errorf("failed during transfer, in getting fromStakeholder: %s", fromStakeholderId)
	}
	fromStakeholder := Stakeholder{}
	json.Unmarshal(fromStakeholderAsBytes, &fromStakeholder)

	fromBalance, err := strconv.ParseFloat(fromStakeholder.WalletBalance, 64)
	if err != nil {
		return "", fmt.Errorf("failed converting fromBalance from string to float: %s", fromStakeholder.WalletBalance)
	}

	// ==== Retrieve toStakeholder info ====
	toStakeholderAsBytes, err := stub.GetState(toStakeholderId)
	if err != nil {
		return "", fmt.Errorf("failed during transfer, retrieving toStakeholder with error: %s", err)
	}
	if toStakeholderAsBytes == nil {
		return "", fmt.Errorf("failed during transfer, in getting toStakeholder: %s", toStakeholderId)
	}
	toStakeholder := Stakeholder{}
	json.Unmarshal(toStakeholderAsBytes, &toStakeholder)

	toBalance, err := strconv.ParseFloat(toStakeholder.WalletBalance, 64)
	if err != nil {
		return "", fmt.Errorf("failed converting toBalance from string to float: %s", toStakeholder.WalletBalance)
	}

	// ==== transfer amount fromStakeholder -> toStakeholder ====
	amountAsFloat, _ := strconv.ParseFloat(amount, 64)
	fromStakeholder.WalletBalance = fmt.Sprintf("%g", (fromBalance - amountAsFloat))
	toStakeholder.WalletBalance = fmt.Sprintf("%g", (toBalance + amountAsFloat))

	fromStakeholderAsBytes, _ = json.Marshal(fromStakeholder)
	err = stub.PutState(fromStakeholderId, fromStakeholderAsBytes)
	if err != nil {
		return "", fmt.Errorf("failed to put state during transfer for fromStakeholder: %s", fromStakeholderId)
	}

	toStakeholderAsBytes, _ = json.Marshal(toStakeholder)
	err = stub.PutState(toStakeholderId, toStakeholderAsBytes)
	if err != nil {
		return "", fmt.Errorf("failed to put state during transfer for toStakeholder: %s", toStakeholderId)
	}

	return string(fromStakeholderAsBytes), nil

}

func registerCDR(stub shim.ChaincodeStubInterface, args []string) (string, error) {
	if len(args) != 29 {
		return "", fmt.Errorf("Incorrect number of arguments for registerCDR, expecting 29")
	}

	var tariffs []Tariff
	var chargingPeriods []ChargingPeriod

	recordId := args[0]
	cpoContractId := args[1]
	countryCode := args[2]
	startDateTime := args[3]
	endDateTime := args[4]
	sessionId := args[5]
	cdrTokenUid := args[6]
	cdrTokenType := args[7]
	evdrContractId := args[8]
	authMethod := args[9]
	authorizationReference := args[10]
	cdrLocation := args[11]
	meterId := args[12]
	currency := args[13]
	signedData := args[14]
	totalCost := args[15]
	totalFixedCost := args[16]
	totalEnergy := args[17]
	totalEnergyCost := args[18]
	totalTime := args[19]
	totalTimeCost := args[20]
	totalParkingTime := args[21]
	totalParkingCost := args[22]
	totalReservationCost := args[23]
	remark := args[24]
	invoiceReferenceId := args[25]
	credit := args[26]
	creditReferenceId := args[27]
	lastUpdated := args[28]

	// ==== Check if user with userId already exists ====
	userAsBytes, err := stub.GetState(evdrContractId)
	if err != nil {
		return "", fmt.Errorf("failed retrieving EVDR certificate with error: %s", err)
	}

	if userAsBytes == nil {
		return "", fmt.Errorf("failed to get EVDR stakeholder: %s", evdrContractId)
	}

	// ==== Create CDR object and marshal to JSON ====
	objectType := "cdr"
	cdr := &CDR{objectType, recordId, cpoContractId, countryCode, startDateTime, endDateTime, sessionId, cdrTokenUid, cdrTokenType, evdrContractId, authMethod, authorizationReference, cdrLocation, meterId, currency, tariffs, chargingPeriods, signedData, totalCost, totalFixedCost, totalEnergy, totalEnergyCost, totalTime, totalTimeCost, totalParkingTime, totalParkingCost, totalReservationCost, remark, invoiceReferenceId, credit, creditReferenceId, lastUpdated}
	cdrJSONasBytes, err := json.Marshal(cdr)

	if err != nil {
		return "", fmt.Errorf("failed to set CDR: %s", recordId)
	}

	err = stub.PutState(recordId, cdrJSONasBytes)

	if err != nil {
		return "", fmt.Errorf("failed to put state of cdr: %s", recordId)
	}

	return string(cdrJSONasBytes), nil
}

func settlementCDR(stub shim.ChaincodeStubInterface, args []string) (string, error) {
	if len(args) != 3 {
		return "", fmt.Errorf("Incorrect arguments, expecting 3")
	}

	recordId := args[0]
	contractIdFI := args[1]
	contractIdEMSP := args[2]

	// ==== Check if CDR record with recordId exists and retrieve ====
	cdrAsBytes, err := stub.GetState(recordId)
	cdr := CDR{}
	if err != nil {
		return "", fmt.Errorf("settementCDR failed retrieving CDR with error: %s", err)
	}

	if cdrAsBytes == nil {
		return "", fmt.Errorf("settementCDR failed retrieving CDR: %s", recordId)
	}

	json.Unmarshal(cdrAsBytes, &cdr)

	// ==== Get EVDriver based on contractId (buyer) in CDR record ====
	evDriverAsBytes, err := stub.GetState(cdr.EVDRContractID)
	evDriver := Stakeholder{}
	if err != nil {
		return "", fmt.Errorf("settementCDR failed retrieving evDriver with error: %s", err)
	}

	if evDriverAsBytes == nil {
		return "", fmt.Errorf("settementCDR failed retrieving evDriver: %s", cdr.EVDRContractID)
	}

	json.Unmarshal(evDriverAsBytes, &evDriver)

	// ==== Get CPO stakeholder object based on ownerCPO in CDR record ====
	cpoAsBytes, err := stub.GetState(cdr.CPOContractID)
	cpo := Stakeholder{}
	if err != nil {
		return "", fmt.Errorf("settementCDR failed retrieving CPO with error: %s", err)
	}

	if cpoAsBytes == nil {
		return "", fmt.Errorf("settementCDR failed retrieving CPO: %s", cdr.CPOContractID)
	}

	json.Unmarshal(cpoAsBytes, &cpo)

	// ==== Get FI stakeholder object based on contractIdFI argument ====
	fiAsBytes, err := stub.GetState(contractIdFI)
	fi := Stakeholder{}
	if err != nil {
		return "", fmt.Errorf("settementCDR failed retrieving FI with error: %s", err)
	}

	if fiAsBytes == nil {
		return "", fmt.Errorf("settementCDR failed retrieving FI: %s", contractIdFI)
	}

	json.Unmarshal(fiAsBytes, &fi)

	// ==== Get eMSP stakeholder object based on contractIdEMSP argument ====
	emspAsBytes, err := stub.GetState(contractIdEMSP)
	eMSP := Stakeholder{}
	if err != nil {
		return "", fmt.Errorf("settementCDR failed retrieving eMSP with error: %s", err)
	}

	if emspAsBytes == nil {
		return "", fmt.Errorf("settementCDR failed retrieving eMSP: %s", contractIdEMSP)
	}

	json.Unmarshal(emspAsBytes, &eMSP)

	// ==== convert user wallet balance to integer for calculations and back to String ====
	costOfCharging, _ := strconv.ParseFloat(cdr.TotalCost, 64)
	buyerCurrentBalance, _ := strconv.ParseFloat(evDriver.WalletBalance, 64)
	CPOCurrentBalance, _ := strconv.ParseFloat(cpo.WalletBalance, 64)
	FICurrentBalance, _ := strconv.ParseFloat(fi.WalletBalance, 64)
	eMSPCurrentBalance, _ := strconv.ParseFloat(eMSP.WalletBalance, 64)
	fee_FI, _ := strconv.ParseFloat(fi.Fee, 64)
	fee_eMSP, _ := strconv.ParseFloat(eMSP.Fee, 64)

	evDriver.WalletBalance = fmt.Sprintf("%g", buyerCurrentBalance-costOfCharging-fee_FI-fee_eMSP)
	cpo.WalletBalance = fmt.Sprintf("%g", CPOCurrentBalance+costOfCharging)
	fi.WalletBalance = fmt.Sprintf("%g", FICurrentBalance+fee_FI)
	eMSP.WalletBalance = fmt.Sprintf("%g", eMSPCurrentBalance+fee_eMSP)

	// ==== update state ====
	evDriverAsBytes, _ = json.Marshal(evDriver)
	err = stub.PutState(evDriver.ContractID, evDriverAsBytes)

	if err != nil {
		return "", fmt.Errorf("settementCDR failed updating state for evDriver: %s", evDriver.ContractID)
	}

	cpoAsBytes, _ = json.Marshal(cpo)
	err = stub.PutState(cpo.ContractID, cpoAsBytes)

	if err != nil {
		return "", fmt.Errorf("settementCDR failed updating state for CPO: %s", cpo.ContractID)
	}

	fiAsBytes, _ = json.Marshal(fi)
	err = stub.PutState(fi.ContractID, fiAsBytes)

	if err != nil {
		return "", fmt.Errorf("settementCDR failed updating state FI: %s", fi.ContractID)
	}

	emspAsBytes, _ = json.Marshal(eMSP)
	err = stub.PutState(eMSP.ContractID, emspAsBytes)

	if err != nil {
		return "", fmt.Errorf("settementCDR failed updating state eMSP: %s", eMSP.ContractID)
	}

	return string(evDriverAsBytes), nil
}

func processCDR(stub shim.ChaincodeStubInterface, args []string) (string, error) {
	// combination of registerCDR and settlementCDR
	// motivation: avoid error caused by intermediate function calls
	// Error: Commit of transaction failed on peer [..] with status MVCC_READ_CONFLICT

	if len(args) != 31 {
		return "", fmt.Errorf("Incorrect number of arguments for registerCDR, expecting 29")
	}

	var tariffs []Tariff
	var chargingPeriods []ChargingPeriod

	recordId := args[0]
	cpoContractId := args[1]
	countryCode := args[2]
	startDateTime := args[3]
	endDateTime := args[4]
	sessionId := args[5]
	cdrTokenUid := args[6]
	cdrTokenType := args[7]
	evdrContractId := args[8]
	authMethod := args[9]
	authorizationReference := args[10]
	cdrLocation := args[11]
	meterId := args[12]
	currency := args[13]
	signedData := args[14]
	totalCost := args[15]
	totalFixedCost := args[16]
	totalEnergy := args[17]
	totalEnergyCost := args[18]
	totalTime := args[19]
	totalTimeCost := args[20]
	totalParkingTime := args[21]
	totalParkingCost := args[22]
	totalReservationCost := args[23]
	remark := args[24]
	invoiceReferenceId := args[25]
	credit := args[26]
	creditReferenceId := args[27]
	lastUpdated := args[28]

	contractIdFI := args[29]
	contractIdEMSP := args[30]

	// ==== Get EVDriver based on contractId (buyer) in CDR record ====
	evDriverAsBytes, err := stub.GetState(evdrContractId)
	evDriver := Stakeholder{}
	if err != nil {
		return "", fmt.Errorf("processCDR failed retrieving evDriver with error: %s", err)
	}

	if evDriverAsBytes == nil {
		return "", fmt.Errorf("processCDR failed retrieving evDriver: %s", evdrContractId)
	}

	json.Unmarshal(evDriverAsBytes, &evDriver)

	// ==== Get CPO stakeholder object based on ownerCPO in CDR record ====
	cpoAsBytes, err := stub.GetState(cpoContractId)
	cpo := Stakeholder{}
	if err != nil {
		return "", fmt.Errorf("processCDR failed retrieving CPO with error: %s", err)
	}

	if cpoAsBytes == nil {
		return "", fmt.Errorf("processCDR failed retrieving CPO: %s", cpoContractId)
	}

	json.Unmarshal(cpoAsBytes, &cpo)

	// ==== Get FI stakeholder object based on contractIdFI argument ====
	fiAsBytes, err := stub.GetState(contractIdFI)
	fi := Stakeholder{}
	if err != nil {
		return "", fmt.Errorf("processCDR failed retrieving FI with error: %s", err)
	}

	if fiAsBytes == nil {
		return "", fmt.Errorf("processCDR failed retrieving FI: %s", contractIdFI)
	}

	json.Unmarshal(fiAsBytes, &fi)

	// ==== Get eMSP stakeholder object based on contractIdEMSP argument ====
	emspAsBytes, err := stub.GetState(contractIdEMSP)
	eMSP := Stakeholder{}
	if err != nil {
		return "", fmt.Errorf("processCDR failed retrieving eMSP with error: %s", err)
	}

	if emspAsBytes == nil {
		return "", fmt.Errorf("processCDR failed retrieving eMSP: %s", args[3])
	}

	json.Unmarshal(emspAsBytes, &eMSP)

	// ==== Create CDR object and marshal to JSON ====
	objectType := "cdr"
	cdr := &CDR{objectType, recordId, cpoContractId, countryCode, startDateTime, endDateTime, sessionId, cdrTokenUid, cdrTokenType, evdrContractId, authMethod, authorizationReference, cdrLocation, meterId, currency, tariffs, chargingPeriods, signedData, totalCost, totalFixedCost, totalEnergy, totalEnergyCost, totalTime, totalTimeCost, totalParkingTime, totalParkingCost, totalReservationCost, remark, invoiceReferenceId, credit, creditReferenceId, lastUpdated}
	cdrJSONasBytes, err := json.Marshal(cdr)

	if err != nil {
		return "", fmt.Errorf("failed to set CDR in processCDR: %s", recordId)
	}

	err = stub.PutState(recordId, cdrJSONasBytes)

	if err != nil {
		return "", fmt.Errorf("failed to put state of cdr in processCDR: %s", recordId)
	}

	// ==== convert user wallet balance to integer for calculations and back to String ====
	costOfCharging, _ := strconv.ParseFloat(totalCost, 64)
	buyerCurrentBalance, _ := strconv.ParseFloat(evDriver.WalletBalance, 64)
	CPOCurrentBalance, _ := strconv.ParseFloat(cpo.WalletBalance, 64)
	FICurrentBalance, _ := strconv.ParseFloat(fi.WalletBalance, 64)
	eMSPCurrentBalance, _ := strconv.ParseFloat(eMSP.WalletBalance, 64)
	fee_FI, _ := strconv.ParseFloat(fi.Fee, 64)
	fee_eMSP, _ := strconv.ParseFloat(eMSP.Fee, 64)

	evDriver.WalletBalance = fmt.Sprintf("%g", buyerCurrentBalance-costOfCharging-fee_FI-fee_eMSP)
	cpo.WalletBalance = fmt.Sprintf("%g", CPOCurrentBalance+costOfCharging)
	fi.WalletBalance = fmt.Sprintf("%g", FICurrentBalance+fee_FI)
	eMSP.WalletBalance = fmt.Sprintf("%g", eMSPCurrentBalance+fee_eMSP)

	// ==== update state ====
	evDriverAsBytes, _ = json.Marshal(evDriver)
	err = stub.PutState(evDriver.ContractID, evDriverAsBytes)

	if err != nil {
		return "", fmt.Errorf("settementCDR failed updating state for evDriver: %s", evDriver.ContractID)
	}

	cpoAsBytes, _ = json.Marshal(cpo)
	err = stub.PutState(cpo.ContractID, cpoAsBytes)

	if err != nil {
		return "", fmt.Errorf("settementCDR failed updating state for CPO: %s", cpo.ContractID)
	}

	fiAsBytes, _ = json.Marshal(fi)
	err = stub.PutState(fi.ContractID, fiAsBytes)

	if err != nil {
		return "", fmt.Errorf("settementCDR failed updating state FI: %s", fi.ContractID)
	}

	emspAsBytes, _ = json.Marshal(eMSP)
	err = stub.PutState(eMSP.ContractID, emspAsBytes)

	if err != nil {
		return "", fmt.Errorf("settementCDR failed updating state eMSP: %s", eMSP.ContractID)
	}

	return string(cdrJSONasBytes), nil
}
