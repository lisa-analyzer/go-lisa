package models

import (
	"encoding/json"
	"errors"
	"github.com/abbeydabiri/hdlchaincode/utils"
	"time"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	pb "github.com/hyperledger/fabric-protos-go/peer"
)

// BuyerBase data structure
type BuyerBase struct {
	BuyerID   int       `json:"buyerID"`
	UserID    int       `json:"userID"`
	BuyerType string    `json:"buyertype"`
	Details   string    `json:"details"`
	RegDate   string    `json:"regdate"`
	Created   time.Time `json:"created"`
	Createdby string    `json:"createdby"`
}

// Buyer struct for chain state
type Buyer struct {
	ObjectType string    `json:"docType"` // default is 'BUYERO'
	BuyerID    string    `json:"buyerID"`
	Data       BuyerBase `json:"data"` // composition
}

//PutState Write asset state to ledger
func (asset *Buyer) PutState(stub shim.ChaincodeStubInterface) pb.Response {
	// check if asset already exists
	c, cErr := utils.CheckAsset(stub, asset.BuyerID)
	if cErr != nil {
		return shim.Error(cErr.Error())
	}
	if c {
		e := &utils.ChainError{FCN: utils.BuyerW, KEY: asset.BuyerID, CODE: utils.CODEAlRDEXIST, ERR:  errors.New("Asset with key already exists")}
		return shim.Error(e.Error())
	}

	// Marshal the struct to []byte
	b, err := json.Marshal(asset)
	if err != nil {
		cErr = &utils.ChainError{FCN: utils.BuyerW, KEY: asset.BuyerID, CODE: utils.CODEGENEXCEPTION, ERR: err}
		return shim.Error(cErr.Error())
	}
	// Write key-value to ledger
	err = stub.PutState(asset.BuyerID, b)
	if err != nil {
		cErr = &utils.ChainError{FCN: utils.BuyerW, KEY: asset.BuyerID, CODE: utils.CODEGENEXCEPTION, ERR: err}
		return shim.Error(cErr.Error())
	}

	// Emit transaction event for listeners
	txID := stub.GetTxID()
	stub.SetEvent((asset.BuyerID + utils.BuyerW + txID), nil)
	r := utils.Response{Code: utils.CODEALLAOK, Message: asset.BuyerID, Payload: nil}
	return shim.Success((r.FormatResponse()))
}

//GetState Read asset state from the ledger
func (asset *Buyer) GetState(stub shim.ChaincodeStubInterface) pb.Response {
	obj, cErr := utils.QueryAsset(stub, asset.BuyerID)
	if cErr != nil {
		return shim.Error(cErr.Error())
	}
	r := utils.Response{Code: utils.CODEALLAOK, Message: "OK", Payload: obj}
	return shim.Success((r.FormatResponse()))
}
