package models

import (
	"encoding/json"
	"errors"
	"github.com/abbeydabiri/hdlchaincode/utils"
	"time"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	pb "github.com/hyperledger/fabric-protos-go/peer"
)

// LoanBase data structure
type LoanBase struct {
	LoanID     int       `json:"loanID"`
	PropertyID int       `json:"propertyID"`
	UserID     int       `json:"userID"`
	BuyerID    int       `json:"buyerID"`
	Repayment  float64   `json:"repayment"`
	LoanStatus string    `json:"loanstatus"`
	PerfRating float64   `json:"perfrating"`
	Created    time.Time `json:"created"`
	Createdby  string    `json:"createdby"`
}

//Loan struct for chain state
type Loan struct {
	ObjectType string   `json:"docType"` // default is 'LOANOO'
	LoanID     string   `json:"loanID"`
	Data       LoanBase `json:"data"` // composition
}

//PutState Write asset state to ledger
func (asset *Loan) PutState(stub shim.ChaincodeStubInterface) pb.Response {
	// check if asset already exists
	c, cErr := utils.CheckAsset(stub, asset.LoanID)
	if cErr != nil {
		return shim.Error(cErr.Error())
	}
	if c {
		e := &utils.ChainError{FCN: utils.LoanW, KEY: asset.LoanID, CODE: utils.CODEAlRDEXIST, ERR:  errors.New("Asset with key already exists")}
		return shim.Error(e.Error())
	}

	// Marshal the struct to []byte
	b, err := json.Marshal(asset)
	if err != nil {
		cErr = &utils.ChainError{FCN: utils.LoanW, KEY: asset.LoanID, CODE: utils.CODEGENEXCEPTION, ERR: err}
		return shim.Error(cErr.Error())
	}
	// Write key-value to ledger
	err = stub.PutState(asset.LoanID, b)
	if err != nil {
		cErr = &utils.ChainError{FCN: utils.LoanW, KEY: asset.LoanID, CODE: utils.CODEGENEXCEPTION, ERR: err}
		return shim.Error(cErr.Error())
	}

	// Emit transaction event for listeners
	txID := stub.GetTxID()
	stub.SetEvent((asset.LoanID + utils.LoanW + txID), nil)
	r := utils.Response{Code: utils.CODEALLAOK, Message: asset.LoanID, Payload: nil}
	return shim.Success((r.FormatResponse()))
}

//GetState Read asset state from the ledger
func (asset *Loan) GetState(stub shim.ChaincodeStubInterface) pb.Response {
	obj, cErr := utils.QueryAsset(stub, asset.LoanID)
	if cErr != nil {
		return shim.Error(cErr.Error())
	}
	r := utils.Response{Code: utils.CODEALLAOK, Message: "OK", Payload: obj}
	return shim.Success((r.FormatResponse()))
}
