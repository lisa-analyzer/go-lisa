package models

import (
	"encoding/json"
	"errors"
	"github.com/abbeydabiri/hdlchaincode/utils"
	"time"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	pb "github.com/hyperledger/fabric-protos-go/peer"
)

// BranchConfigBase data structure
type BranchConfigBase struct {
	ConfigID   int       `json:"configID"`
	BankID     int       `json:"bankID"`
	ConfigName string    `json:"configname"`
	ConfigDesc string    `json:"configdesc"`
	Item       string    `json:"item"`
	Value      string    `json:"value"`
	Created    time.Time `json:"created"`
	Createdby  string    `json:"createdby"`
}

//BranchConfig struct for chain state
type BranchConfig struct {
	ObjectType string           `json:"docType"` // default is 'BNKCFG'
	ConfigID   string           `json:"configID"`
	Data       BranchConfigBase `json:"data"` // composition
}

//PutState Write asset state to ledger
func (asset *BranchConfig) PutState(stub shim.ChaincodeStubInterface) pb.Response {
	// check if asset already exists
	c, cErr := utils.CheckAsset(stub, asset.ConfigID)
	if cErr != nil {
		return shim.Error(cErr.Error())
	}
	if c {
		e := &utils.ChainError{FCN: utils.BranchConfigW, KEY: asset.ConfigID, CODE: utils.CODEAlRDEXIST, ERR:  errors.New("Asset with key already exists")}
		return shim.Error(e.Error())
	}

	// Marshal the struct to []byte
	b, err := json.Marshal(asset)
	if err != nil {
		cErr = &utils.ChainError{FCN: utils.BranchConfigW, KEY: asset.ConfigID, CODE: utils.CODEGENEXCEPTION, ERR: err}
		return shim.Error(cErr.Error())
	}
	// Write key-value to ledger
	err = stub.PutState(asset.ConfigID, b)
	if err != nil {
		cErr = &utils.ChainError{FCN: utils.BranchConfigW, KEY: asset.ConfigID, CODE: utils.CODEGENEXCEPTION, ERR: err}
		return shim.Error(cErr.Error())
	}

	// Emit transaction event for listeners
	txID := stub.GetTxID()
	stub.SetEvent((asset.ConfigID + utils.BranchConfigW + txID), nil)
	r := utils.Response{Code: utils.CODEALLAOK, Message: asset.ConfigID, Payload: nil}
	return shim.Success((r.FormatResponse()))
}

//GetState Read asset state from the ledger
func (asset *BranchConfig) GetState(stub shim.ChaincodeStubInterface) pb.Response {
	obj, cErr := utils.QueryAsset(stub, asset.ConfigID)
	if cErr != nil {
		return shim.Error(cErr.Error())
	}
	r := utils.Response{Code: utils.CODEALLAOK, Message: "OK", Payload: obj}
	return shim.Success((r.FormatResponse()))
}
