package models

import (
	"encoding/json"
	"errors"
	"github.com/abbeydabiri/hdlchaincode/utils"
	"time"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	pb "github.com/hyperledger/fabric-protos-go/peer"
)

// PermissionsBase data structure
type PermissionsBase struct {
	PermID     int       `json:"permID"`
	PermRoleID int       `json:"permroleID"`
	PermName   string    `json:"permname"`
	PermModule string    `json:"permmodule"`
	Created    time.Time `json:"created"`
	Createdby  string    `json:"createdby"`
}

//Permissions struct for chain state
type Permissions struct {
	ObjectType string          `json:"docType"` // default is 'PERMSN'
	PermID     string          `json:"permID"`
	Data       PermissionsBase `json:"data"` // composition
}

//PutState Write asset state to ledger
func (asset *Permissions) PutState(stub shim.ChaincodeStubInterface) pb.Response {
	// check if asset already exists
	c, cErr := utils.CheckAsset(stub, asset.PermID)
	if cErr != nil {
		return shim.Error(cErr.Error())
	}
	if c {
		e := &utils.ChainError{FCN: utils.PermissionsW, KEY: asset.PermID, CODE: utils.CODEAlRDEXIST, ERR:  errors.New("Asset with key already exists")}
		return shim.Error(e.Error())
	}

	// Marshal the struct to []byte
	b, err := json.Marshal(asset)
	if err != nil {
		cErr = &utils.ChainError{FCN: utils.PermissionsW, KEY: asset.PermID, CODE: utils.CODEGENEXCEPTION, ERR: err}
		return shim.Error(cErr.Error())
	}
	// Write key-value to ledger
	err = stub.PutState(asset.PermID, b)
	if err != nil {
		cErr = &utils.ChainError{FCN: utils.PermissionsW, KEY: asset.PermID, CODE: utils.CODEGENEXCEPTION, ERR: err}
		return shim.Error(cErr.Error())
	}

	// Emit transaction event for listeners
	txID := stub.GetTxID()
	stub.SetEvent((asset.PermID + utils.PermissionsW + txID), nil)
	r := utils.Response{Code: utils.CODEALLAOK, Message: asset.PermID, Payload: nil}
	return shim.Success((r.FormatResponse()))
}

//GetState Read asset state from the ledger
func (asset *Permissions) GetState(stub shim.ChaincodeStubInterface) pb.Response {
	obj, cErr := utils.QueryAsset(stub, asset.PermID)
	if cErr != nil {
		return shim.Error(cErr.Error())
	}
	r := utils.Response{Code: utils.CODEALLAOK, Message: "OK", Payload: obj}
	return shim.Success((r.FormatResponse()))
}
