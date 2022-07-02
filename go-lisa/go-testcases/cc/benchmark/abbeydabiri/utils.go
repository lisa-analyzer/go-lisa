package utils

import (
	"bytes"
	"encoding/base64"
	"errors"
	"strings"

	"github.com/hyperledger/fabric-chaincode-go/pkg/cid"
	"github.com/hyperledger/fabric-chaincode-go/shim"
)

// Response struct to have consistent Response structure for all chaincode invokes
type Response struct {
	Code    string `json:"code"`
	Message string `json:"message"`
	Payload []byte `json:"payload"`
}

// FormatResponse - present response in json format
func (resp *Response) FormatResponse() []byte {
	var buffer bytes.Buffer
	buffer.WriteString("{\"code\":")
	buffer.WriteString("\"")
	buffer.WriteString(resp.Code)
	buffer.WriteString("\",")
	buffer.WriteString("\"message\":")
	buffer.WriteString("\"")
	buffer.WriteString(resp.Message)
	if resp.Payload == nil {
		buffer.WriteString("\"}")
	} else {
		buffer.WriteString("\",")
		buffer.WriteString("\"payload\":")
		buffer.Write(resp.Payload)
		buffer.WriteString("}")
	}
	return buffer.Bytes()
}

// ChainError - custom error
type ChainError struct {
	FCN  string // function/method
	KEY  string // associated KEY, if any
	CODE string // Error CODE
	ERR  error  // Error.
}

func (e *ChainError) Error() string {
	return e.FCN + " " + e.KEY + " " + e.CODE + ": " + e.ERR.Error()
}

//CheckAsset - check if an asset ( with a key) is already available on the ledger
func CheckAsset(stub shim.ChaincodeStubInterface, assetID string) (bool, *ChainError) {

	assetBytes, err := stub.GetState(assetID)

	if err != nil {
		e := &ChainError{FCN: "CheckAsset", KEY: assetID, CODE: CODEGENEXCEPTION, ERR: err}
		return false, e
	} else if assetBytes != nil {
		e := &ChainError{FCN: "CheckAsset", KEY: assetID, CODE: CODEAlRDEXIST, ERR: errors.New("Asset with key already exists")}
		return true, e
	}
	return false, nil
}

//QueryAsset - return query state from the ledger
func QueryAsset(stub shim.ChaincodeStubInterface, assetID string) ([]byte, *ChainError) {

	assetBytes, err := stub.GetState(assetID)

	if err != nil {
		e := &ChainError{FCN: "QueryAsset", KEY: assetID, CODE: CODEGENEXCEPTION, ERR: err}
		return nil, e
	} else if assetBytes == nil {
		e := &ChainError{FCN: "QueryAsset", KEY: assetID, CODE: CODENOTFOUND, ERR: errors.New("Asset ID not found")}
		return nil, e
	}
	return assetBytes, nil
}

//GetCallerID - reterive caller id from ECert
func GetCallerID(stub shim.ChaincodeStubInterface) (string, *ChainError) {
	id, err := cid.New(stub)
	callerID, err := id.GetID()
	if err != nil {
		e := &ChainError{FCN: "GetCallerID", KEY: "", CODE: CODEGENEXCEPTION, ERR: err}
		return "", e
	}
	// decode the returned base64 string
	data, err := base64.StdEncoding.DecodeString(callerID)
	if err != nil {
		e := &ChainError{FCN: "GetCallerID", KEY: "", CODE: CODEGENEXCEPTION, ERR: err}
		return "", e
	}
	l := strings.Split(string(data), "::")
	return l[1], nil
}
