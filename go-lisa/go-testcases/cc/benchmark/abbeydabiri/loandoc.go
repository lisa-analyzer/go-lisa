package models

import (
	"encoding/json"
	"errors"
	"github.com/abbeydabiri/hdlchaincode/utils"
	"time"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	pb "github.com/hyperledger/fabric-protos-go/peer"
)

//LoanDoc data structure
type LoanDocBase struct {
	DocID     int       `json:"docID"`
	LoanID    int       `json:"loanID"`
	DocName   string    `json:"docname"`
	DocDesc   string    `json:"docdesc"`
	DocLink   string    `json:"doclink"`
	Created   time.Time `json:"created"`
	Createdby string    `json:"createdby"`
}

//LoanDoc struct for chain state
type LoanDoc struct {
	ObjectType string      `json:"docType"` // default is 'LONDOC'
	DocID      string      `json:"docID"`
	Data       LoanDocBase `json:"data"` // composition
}

//PutState Write asset state to ledger
func (asset *LoanDoc) PutState(stub shim.ChaincodeStubInterface) pb.Response {
	// check if asset already exists
	c, cErr := utils.CheckAsset(stub, asset.DocID)
	if cErr != nil {
		return shim.Error(cErr.Error())
	}
	if c {
		e := &utils.ChainError{FCN: utils.LoanDocW, KEY: asset.DocID, CODE: utils.CODEAlRDEXIST, ERR:  errors.New("Asset with key already exists")}
		return shim.Error(e.Error())
	}

	// Marshal the struct to []byte
	b, err := json.Marshal(asset)
	if err != nil {
		cErr = &utils.ChainError{FCN: utils.LoanDocW, KEY: asset.DocID, CODE: utils.CODEGENEXCEPTION, ERR: err}
		return shim.Error(cErr.Error())
	}
	// Write key-value to ledger
	err = stub.PutState(asset.DocID, b)
	if err != nil {
		cErr = &utils.ChainError{FCN: utils.LoanDocW, KEY: asset.DocID, CODE: utils.CODEGENEXCEPTION, ERR: err}
		return shim.Error(cErr.Error())
	}

	// Emit transaction event for listeners
	txID := stub.GetTxID()
	stub.SetEvent((asset.DocID + utils.LoanDocW + txID), nil)
	r := utils.Response{Code: utils.CODEALLAOK, Message: asset.DocID, Payload: nil}
	return shim.Success((r.FormatResponse()))
}

//GetState Read asset state from the ledger
func (asset *LoanDoc) GetState(stub shim.ChaincodeStubInterface) pb.Response {
	obj, cErr := utils.QueryAsset(stub, asset.DocID)
	if cErr != nil {
		return shim.Error(cErr.Error())
	}
	r := utils.Response{Code: utils.CODEALLAOK, Message: "OK", Payload: obj}
	return shim.Success((r.FormatResponse()))
}
