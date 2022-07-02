package models

import (
	"encoding/json"
	"errors"
	"github.com/abbeydabiri/hdlchaincode/utils"
	"time"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	pb "github.com/hyperledger/fabric-protos-go/peer"
)

//MessageBase data structure
type MessageBase struct {
	MessageID   int       `json:"messageID"`
	From        string    `json:"from"`
	To          string    `json:"to"`
	Subject     string    `json:"subject"`
	Message     string    `json:"message"`
	MessageDate string    `json:"messagedate"`
	Created     time.Time `json:"created"`
	Createdby   string    `json:"createdby"`
}

//Message struct for chain state
type Message struct {
	ObjectType string      `json:"docType"` // default is 'MESSAG'
	MessageID  string      `json:"messageID"`
	Data       MessageBase `json:"data"` // composition
}

//PutState Write asset state to ledger
func (asset *Message) PutState(stub shim.ChaincodeStubInterface) pb.Response {
	// check if asset already exists
	c, cErr := utils.CheckAsset(stub, asset.MessageID)
	if cErr != nil {
		return shim.Error(cErr.Error())
	}
	if c {
		e := &utils.ChainError{FCN: utils.MessageW, KEY: asset.MessageID, CODE: utils.CODEAlRDEXIST, ERR: errors.New("Asset with key already exists")}
		return shim.Error(e.Error())
	}

	// Marshal the struct to []byte
	b, err := json.Marshal(asset)
	if err != nil {
		cErr = &utils.ChainError{FCN: utils.MessageW, KEY: asset.MessageID, CODE: utils.CODEGENEXCEPTION, ERR: err}
		return shim.Error(cErr.Error())
	}
	// Write key-value to ledger
	err = stub.PutState(asset.MessageID, b)
	if err != nil {
		cErr = &utils.ChainError{FCN: utils.MessageW, KEY: asset.MessageID, CODE: utils.CODEGENEXCEPTION, ERR: err}
		return shim.Error(cErr.Error())
	}

	// Emit transaction event for listeners
	txID := stub.GetTxID()
	stub.SetEvent((asset.MessageID + utils.MessageW + txID), nil)
	r := utils.Response{Code: utils.CODEALLAOK, Message: asset.MessageID, Payload: nil}
	return shim.Success((r.FormatResponse()))
}

//GetState Read asset state from the ledger
func (asset *Message) GetState(stub shim.ChaincodeStubInterface) pb.Response {
	obj, cErr := utils.QueryAsset(stub, asset.MessageID)
	if cErr != nil {
		return shim.Error(cErr.Error())
	}
	r := utils.Response{Code: utils.CODEALLAOK, Message: "OK", Payload: obj}
	return shim.Success((r.FormatResponse()))
}
