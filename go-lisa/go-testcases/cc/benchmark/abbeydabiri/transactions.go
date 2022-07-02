package models

import (
	"encoding/json"
	"errors"
	"github.com/abbeydabiri/hdlchaincode/utils"
	"time"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	pb "github.com/hyperledger/fabric-protos-go/peer"
)

//TransactionBase data structure
type TransactionBase struct {
	TxnID        int       `json:"txnID"`
	TxnDate      string    `json:"txndate"`
	BuyerID      int       `json:"buyerID"`
	UserID       int       `json:"userID"`
	Repayment    float64   `json:"repayment"`
	Amount       float64   `json:"amount"`
	InterestRate float64   `json:"interestrate"`
	Outstanding  float64   `json:"outstanding"`
	DueDate      string    `json:"duedate"`
	Bank         string    `json:"bank"`
	LoanStatus   string    `json:"loanstatus"`
	Created      time.Time `json:"created"`
	Createdby    string    `json:"createdby"`
}

//Transactions struct for chain state
type Transactions struct {
	ObjectType string          `json:"docType"` // default is 'TRNSAC'
	TxnID      string          `json:"txnID"`
	Data       TransactionBase `json:"data"` // composition
}

//PutState Write asset state to ledger
func (asset *Transactions) PutState(stub shim.ChaincodeStubInterface) pb.Response {
	// check if asset already exists
	c, cErr := utils.CheckAsset(stub, asset.TxnID)
	if cErr != nil {
		return shim.Error(cErr.Error())
	}
	if c {
		e := &utils.ChainError{FCN: utils.TransactionW, KEY: asset.TxnID, CODE: utils.CODEAlRDEXIST, ERR: errors.New("Asset with key already exists")}
		return shim.Error(e.Error())
	}

	// Marshal the struct to []byte
	b, err := json.Marshal(asset)
	if err != nil {
		cErr = &utils.ChainError{FCN: utils.TransactionW, KEY: asset.TxnID, CODE: utils.CODEGENEXCEPTION, ERR: err}
		return shim.Error(cErr.Error())
	}
	// Write key-value to ledger
	err = stub.PutState(asset.TxnID, b)
	if err != nil {
		cErr = &utils.ChainError{FCN: utils.TransactionW, KEY: asset.TxnID, CODE: utils.CODEGENEXCEPTION, ERR: err}
		return shim.Error(cErr.Error())
	}

	// Emit transaction event for listeners
	txID := stub.GetTxID()
	stub.SetEvent((asset.TxnID + utils.TransactionW + txID), nil)
	r := utils.Response{Code: utils.CODEALLAOK, Message: asset.TxnID, Payload: nil}
	return shim.Success((r.FormatResponse()))
}

//GetState Read asset state from the ledger
func (asset *Transactions) GetState(stub shim.ChaincodeStubInterface) pb.Response {
	obj, cErr := utils.QueryAsset(stub, asset.TxnID)
	if cErr != nil {
		return shim.Error(cErr.Error())
	}
	r := utils.Response{Code: utils.CODEALLAOK, Message: "OK", Payload: obj}
	return shim.Success((r.FormatResponse()))
}
