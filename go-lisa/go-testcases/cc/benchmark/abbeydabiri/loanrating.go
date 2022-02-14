package models

import (
	"encoding/json"
	"errors"
	"github.com/abbeydabiri/hdlchaincode/utils"
	"time"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	pb "github.com/hyperledger/fabric-protos-go/peer"
)

//LoanRatingBase data strucuture
type LoanRatingBase struct {
	RatingID   int       `json:"ratingID"`
	LoanID     int       `json:"loanID"`
	Rating     float64   `json:"rating"`
	RatingDesc string    `json:"ratingdesc"`
	Created    time.Time `json:"created"`
	Createdby  string    `json:"createdby"`
}

// LoanRating struct for chain state
type LoanRating struct {
	ObjectType string         `json:"docType"` // default is 'LONRAT'
	RatingID   string         `json:"ratingID"`
	Data       LoanRatingBase `json:"data"` // composition
}

//PutState Write asset state to ledger
func (asset *LoanRating) PutState(stub shim.ChaincodeStubInterface) pb.Response {
	// check if asset already exists
	c, cErr := utils.CheckAsset(stub, asset.RatingID)
	if cErr != nil {
		return shim.Error(cErr.Error())
	}
	if c {
		e := &utils.ChainError{FCN: utils.LoanRatingW, KEY: asset.RatingID, CODE: utils.CODEAlRDEXIST, ERR:  errors.New("Asset with key already exists")}
		return shim.Error(e.Error())
	}

	// Marshal the struct to []byte
	b, err := json.Marshal(asset)
	if err != nil {
		cErr = &utils.ChainError{FCN: utils.LoanRatingW, KEY: asset.RatingID, CODE: utils.CODEGENEXCEPTION, ERR: err}
		return shim.Error(cErr.Error())
	}
	// Write key-value to ledger
	err = stub.PutState(asset.RatingID, b)
	if err != nil {
		cErr = &utils.ChainError{FCN: utils.LoanRatingW, KEY: asset.RatingID, CODE: utils.CODEGENEXCEPTION, ERR: err}
		return shim.Error(cErr.Error())
	}

	// Emit transaction event for listeners
	txID := stub.GetTxID()
	stub.SetEvent((asset.RatingID + utils.LoanRatingW + txID), nil)
	r := utils.Response{Code: utils.CODEALLAOK, Message: asset.RatingID, Payload: nil}
	return shim.Success((r.FormatResponse()))
}

//GetState Read asset state from the ledger
func (asset *LoanRating) GetState(stub shim.ChaincodeStubInterface) pb.Response {
	obj, cErr := utils.QueryAsset(stub, asset.RatingID)
	if cErr != nil {
		return shim.Error(cErr.Error())
	}
	r := utils.Response{Code: utils.CODEALLAOK, Message: "OK", Payload: obj}
	return shim.Success((r.FormatResponse()))
}
