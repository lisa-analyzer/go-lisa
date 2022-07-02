package models

import (
	"encoding/json"
	"errors"
	"github.com/abbeydabiri/hdlchaincode/utils"
	"time"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	pb "github.com/hyperledger/fabric-protos-go/peer"
)

//BankBranchBase data structure
type BankBranchBase struct {
	BranchID            int       `json:"branchID"`
	BankID              int       `json:"bankID"`
	BranchName          string    `json:"branchname"`
	BranchManagerUserID int       `json:"branchmanageruserID"`
	BranchManagerRoleID int       `json:"branchmanagerroleID"`
	Location            string    `json:"location"`
	LocationLat         string    `json:"locationlat"`
	LocationLong        string    `json:"locationlong"`
	Created             time.Time `json:"created"`
	Createdby           string    `json:"createdby"`
}

//BankBranch struct for chain state
type BankBranch struct {
	ObjectType string         `json:"docType"` // default is 'BNKBRA'
	BranchID   string         `json:"branchID"`
	Data       BankBranchBase `json:"data"` // composition
}

//PutState Write asset state to ledger
func (asset *BankBranch) PutState(stub shim.ChaincodeStubInterface) pb.Response {
	// check if asset already exists
	c, cErr := utils.CheckAsset(stub, asset.BranchID)
	if cErr != nil {
		return shim.Error(cErr.Error())
	}
	if c {
		e := &utils.ChainError{FCN: utils.BankBranchW, KEY: asset.BranchID, CODE: utils.CODEAlRDEXIST, ERR:  errors.New("Asset with key already exists")}
		return shim.Error(e.Error())
	}

	// Marshal the struct to []byte
	b, err := json.Marshal(asset)
	if err != nil {
		cErr = &utils.ChainError{FCN: utils.BankBranchW, KEY: asset.BranchID, CODE: utils.CODEGENEXCEPTION, ERR: err}
		return shim.Error(cErr.Error())
	}
	// Write key-value to ledger
	err = stub.PutState(asset.BranchID, b)
	if err != nil {
		cErr = &utils.ChainError{FCN: utils.BankBranchW, KEY: asset.BranchID, CODE: utils.CODEGENEXCEPTION, ERR: err}
		return shim.Error(cErr.Error())
	}

	// Emit transaction event for listeners
	txID := stub.GetTxID()
	stub.SetEvent((asset.BranchID + utils.BankBranchW + txID), nil)
	r := utils.Response{Code: utils.CODEALLAOK, Message: asset.BranchID, Payload: nil}
	return shim.Success((r.FormatResponse()))
}

//GetState Read asset state from the ledger
func (asset *BankBranch) GetState(stub shim.ChaincodeStubInterface) pb.Response {
	obj, cErr := utils.QueryAsset(stub, asset.BranchID)
	if cErr != nil {
		return shim.Error(cErr.Error())
	}
	r := utils.Response{Code: utils.CODEALLAOK, Message: "OK", Payload: obj}
	return shim.Success((r.FormatResponse()))
}
