package models

import (
	"encoding/json"
	"errors"
	"time"

	"github.com/abbeydabiri/hdlchaincode/utils"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	pb "github.com/hyperledger/fabric-protos-go/peer"
)

//BankBase data structure
type BankBase struct {
	BankID          int       `json:"bankID"`
	BankName        string    `json:"bankname"`
	HqAddress       string    `json:"hqaddress"`
	BankCategory    string    `json:"bankcategory"`
	BankAdminUserID int       `json:"bankadminuserID"`
	Location        string    `json:"location"`
	LocationLat     string    `json:"locationlat"`
	LocationLong    string    `json:"locationlong"`
	Created         time.Time `json:"created"`
	Createdby       string    `json:"createdby"`
}

//Bank struct for chain state
type Bank struct {
	ObjectType string   `json:"docType"` // default is 'BANKOO'
	BankID     string   `json:"bankID"`
	Data       BankBase `json:"data"` // composition
}

// PutState write asset state to ledger
func (asset *Bank) PutState(stub shim.ChaincodeStubInterface) pb.Response {
	
	// check if asset already exists
	c, cErr := utils.CheckAsset(stub, asset.BankID)
	if cErr != nil {
		return shim.Error(cErr.Error())
	}
	if c {
		e := &utils.ChainError{FCN: utils.BankW, KEY: asset.BankID, CODE: utils.CODEAlRDEXIST, ERR: errors.New("Asset with key already exists")}
		return shim.Error(e.Error())
	}

	// Marshal the struct to []byte
	b, err := json.Marshal(asset)
	if err != nil {
		cErr = &utils.ChainError{FCN: utils.BankW, KEY: asset.BankID, CODE: utils.CODEGENEXCEPTION, ERR: err}
		return shim.Error(cErr.Error())
	}
	// Write key-value to ledger
	err = stub.PutState(asset.BankID, b)
	if err != nil {
		cErr = &utils.ChainError{FCN: utils.BankW, KEY: asset.BankID, CODE: utils.CODEGENEXCEPTION, ERR: err}
		return shim.Error(cErr.Error())
	}

	// Emit transaction event for listeners
	txID := stub.GetTxID()
	stub.SetEvent((asset.BankID + utils.BankW + txID), nil)
	r := utils.Response{Code: utils.CODEALLAOK, Message: asset.BankID, Payload: nil}
	return shim.Success((r.FormatResponse()))
}

// GetState Read asset state from the ledger
func (asset *Bank) GetState(stub shim.ChaincodeStubInterface) pb.Response {
	obj, cErr := utils.QueryAsset(stub, asset.BankID)
	if cErr != nil {
		return shim.Error(cErr.Error())
	}
	r := utils.Response{Code: utils.CODEALLAOK, Message: "OK", Payload: obj}
	return shim.Success((r.FormatResponse()))
}
