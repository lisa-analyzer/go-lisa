package models

import (
	"encoding/json"
	"errors"
	"github.com/abbeydabiri/hdlchaincode/utils"
	"time"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	pb "github.com/hyperledger/fabric-protos-go/peer"
)

//SellerBase data structure
type SellerBase struct {
	SellerID   int       `json:"sellerID"`
	UserID     int       `json:"userID"`
	SellerType string    `json:"sellertype"`
	Details    string    `json:"details"`
	RegDate    string    `json:"regdate"`
	Created    time.Time `json:"created"`
	Createdby  string    `json:"createdby"`
}

//Seller struct for chain state
type Seller struct {
	ObjectType string     `json:"docType"` // default is 'SELLER'
	SellerID   string     `json:"sellerID"`
	Data       SellerBase `json:"data"` // composition
}

//PutState Write asset state to ledger
func (asset *Seller) PutState(stub shim.ChaincodeStubInterface) pb.Response {
	// check if asset already exists
	c, cErr := utils.CheckAsset(stub, asset.SellerID)
	if cErr != nil {
		return shim.Error(cErr.Error())
	}
	if c {
		e := &utils.ChainError{FCN: utils.SellerW, KEY: asset.SellerID, CODE: utils.CODEAlRDEXIST, ERR: errors.New("Asset with key already exists")}
		return shim.Error(e.Error())
	}

	// Marshal the struct to []byte
	b, err := json.Marshal(asset)
	if err != nil {
		cErr = &utils.ChainError{FCN: utils.SellerW, KEY: asset.SellerID, CODE: utils.CODEGENEXCEPTION, ERR: err}
		return shim.Error(cErr.Error())
	}
	// Write key-value to ledger
	err = stub.PutState(asset.SellerID, b)
	if err != nil {
		cErr = &utils.ChainError{FCN: utils.SellerW, KEY: asset.SellerID, CODE: utils.CODEGENEXCEPTION, ERR: err}
		return shim.Error(cErr.Error())
	}

	// Emit transaction event for listeners
	txID := stub.GetTxID()
	stub.SetEvent((asset.SellerID + utils.SellerW + txID), nil)
	r := utils.Response{Code: utils.CODEALLAOK, Message: asset.SellerID, Payload: nil}
	return shim.Success((r.FormatResponse()))
}

//GetState Read asset state from the ledger
func (asset *Seller) GetState(stub shim.ChaincodeStubInterface) pb.Response {
	obj, cErr := utils.QueryAsset(stub, asset.SellerID)
	if cErr != nil {
		return shim.Error(cErr.Error())
	}
	r := utils.Response{Code: utils.CODEALLAOK, Message: "OK", Payload: obj}
	return shim.Success((r.FormatResponse()))
}
