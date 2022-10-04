package models

import (
	"encoding/json"
	"errors"
	"github.com/abbeydabiri/hdlchaincode/utils"
	"time"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	pb "github.com/hyperledger/fabric-protos-go/peer"
)

//RoleBase data structure
type RoleBase struct {
	RoleID       int       `json:"roleID"`
	UserCategory string    `json:"usercategory"`
	UserType     string    `json:"usertype"`
	RoleName     string    `json:"rolename"`
	RoleDesc     string    `json:"roledesc"`
	Created      time.Time `json:"created"`
	Createdby    string    `json:"createdby"`
}

//Role struct for chain state
type Role struct {
	ObjectType string   `json:"docType"` // default is 'ROLEOO'
	RoleID     string   `json:"roleID"`
	Data       RoleBase `json:"data"` // composition
}

//PutState Write asset state to ledger
func (asset *Role) PutState(stub shim.ChaincodeStubInterface) pb.Response {
	// check if asset already exists
	c, cErr := utils.CheckAsset(stub, asset.RoleID)
	if cErr != nil {
		return shim.Error(cErr.Error())
	}
	if c {
		e := &utils.ChainError{FCN: utils.RoleW, KEY: asset.RoleID, CODE: utils.CODEAlRDEXIST, ERR:  errors.New("Asset with key already exists")}
		return shim.Error(e.Error())
	}

	// Marshal the struct to []byte
	b, err := json.Marshal(asset)
	if err != nil {
		cErr = &utils.ChainError{FCN: utils.RoleW, KEY: asset.RoleID, CODE: utils.CODEGENEXCEPTION, ERR: err}
		return shim.Error(cErr.Error())
	}
	// Write key-value to ledger
	err = stub.PutState(asset.RoleID, b)
	if err != nil {
		cErr = &utils.ChainError{FCN: utils.RoleW, KEY: asset.RoleID, CODE: utils.CODEGENEXCEPTION, ERR: err}
		return shim.Error(cErr.Error())
	}

	// Emit transaction event for listeners
	txID := stub.GetTxID()
	stub.SetEvent((asset.RoleID + utils.RoleW + txID), nil)
	r := utils.Response{Code: utils.CODEALLAOK, Message: asset.RoleID, Payload: nil}
	return shim.Success((r.FormatResponse()))
}

//GetState Read asset state from the ledger
func (asset *Role) GetState(stub shim.ChaincodeStubInterface) pb.Response {
	obj, cErr := utils.QueryAsset(stub, asset.RoleID)
	if cErr != nil {
		return shim.Error(cErr.Error())
	}
	r := utils.Response{Code: utils.CODEALLAOK, Message: "OK", Payload: obj}
	return shim.Success((r.FormatResponse()))
}
