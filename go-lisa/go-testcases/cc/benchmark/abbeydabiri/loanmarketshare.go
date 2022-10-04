package models

import (
	"encoding/json"
	"errors"
	"github.com/abbeydabiri/hdlchaincode/utils"
	"time"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	pb "github.com/hyperledger/fabric-protos-go/peer"
)

//LoanMarketShareBase data structure
type LoanMarketShareBase struct {
	ShareID     int       `json:"shareID"`
	TitleHolder string    `json:"titleholder"`
	Amount      float64   `json:"amount"`
	Repayments  float64   `json:"repayments"`
	Statutes    string    `json:"statutes"`
	Rating      float64   `json:"rating"`
	Status      string    `json:"status"`
	Created     time.Time `json:"created"`
	Createdby   string    `json:"createdby"`
}

//LoanMarketShare struct for chain state
type LoanMarketShare struct {
	ObjectType string              `json:"docType"` // default is 'LONMRK'
	ShareID    string              `json:"shareID"`
	Data       LoanMarketShareBase `json:"data"` // composition
}

//PutState Write asset state to ledger
func (asset *LoanMarketShare) PutState(stub shim.ChaincodeStubInterface) pb.Response {
	// check if asset already exists
	c, cErr := utils.CheckAsset(stub, asset.ShareID)
	if cErr != nil {
		return shim.Error(cErr.Error())
	}
	if c {
		e := &utils.ChainError{FCN: utils.LoanMarketShareW, KEY: asset.ShareID, CODE: utils.CODEAlRDEXIST, ERR:  errors.New("Asset with key already exists")}
		return shim.Error(e.Error())
	}

	// Marshal the struct to []byte
	b, err := json.Marshal(asset)
	if err != nil {
		cErr = &utils.ChainError{FCN: utils.LoanMarketShareW, KEY: asset.ShareID, CODE: utils.CODEGENEXCEPTION, ERR: err}
		return shim.Error(cErr.Error())
	}
	// Write key-value to ledger
	err = stub.PutState(asset.ShareID, b)
	if err != nil {
		cErr = &utils.ChainError{FCN: utils.LoanMarketShareW, KEY: asset.ShareID, CODE: utils.CODEGENEXCEPTION, ERR: err}
		return shim.Error(cErr.Error())
	}

	// Emit transaction event for listeners
	txID := stub.GetTxID()
	stub.SetEvent((asset.ShareID + utils.LoanMarketShareW + txID), nil)
	r := utils.Response{Code: utils.CODEALLAOK, Message: asset.ShareID, Payload: nil}
	return shim.Success((r.FormatResponse()))
}

//GetState Read asset state from the ledger
func (asset *LoanMarketShare) GetState(stub shim.ChaincodeStubInterface) pb.Response {
	obj, cErr := utils.QueryAsset(stub, asset.ShareID)
	if cErr != nil {
		return shim.Error(cErr.Error())
	}
	r := utils.Response{Code: utils.CODEALLAOK, Message: "OK", Payload: obj}
	return shim.Success((r.FormatResponse()))
}
