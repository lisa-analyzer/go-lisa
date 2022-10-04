package main

import (
	"encoding/json"
	"fmt"
	"testing"
	"time"

	"github.com/abbeydabiri/hdlchaincode/models"
	"github.com/abbeydabiri/hdlchaincode/utils"

	"github.com/google/uuid"
	"github.com/hyperledger/fabric-chaincode-go/shim"
	"github.com/hyperledger/fabric-chaincode-go/shimtest"
	"github.com/stretchr/testify/assert"
)

func TestBankWR(t *testing.T) {
	fmt.Println("Entering TestBankLogic")

	assert := assert.New(t)
	// Instantiate mockStub using HDLChaincode as the target chaincode to unit test
	stub := shimtest.NewMockStub("TestStub", new(HDLChaincode))

	//Verify stub is available
	assert.NotNil(stub, "Stub is nil, Test stub creation failed")

	uid := uuid.New().String()

	writeResp := stub.MockInvoke(uid,
		[][]byte{[]byte(utils.BankW),
			[]byte("2"),
			[]byte("Test Bank 2"),
			[]byte("Test Hq 2"),
			[]byte("Test BankCategory 2"),
			[]byte("2"),
			[]byte("Test Location 2"),
			[]byte("Test LocationLat 2"),
			[]byte("Test LocationLong 2"),
		})
	assert.EqualValues(shim.OK, writeResp.GetStatus(), "logic.BankW write test Bank to state failed.")

	testID := "2"
	readResp := stub.MockInvoke(uid,
		[][]byte{[]byte(utils.BankR),
			[]byte(testID),
		})
	assert.EqualValues(shim.OK, readResp.GetStatus(), "logic.BankR read test Bank from state failed.")

	var ccResp struct {
		Code    string      `json:"code"`
		Message string      `json:"message"`
		Payload models.Bank `json:"payload"`
	}
	if err := json.Unmarshal(readResp.GetPayload(), &ccResp); err != nil {
		panic(err)
	}

	assert.Equal(testID, ccResp.Payload.BankID, "Retrieved Bank ID mismatch")
	assert.Equal(utils.BANKOO, ccResp.Payload.ObjectType, "Retrieved Object Type mismatch")
}

func TestBankModel(t *testing.T) {
	fmt.Println("Entering TestBankModel")

	assert := assert.New(t)
	// Instantiate mockStub using HDLChaincode as the target chaincode to unit test
	stub := shimtest.NewMockStub("TestStub", new(HDLChaincode))
	//Verify stub is available
	assert.NotNil(stub, "Stub is nil, Test stub creation failed")

	epochTime, _ := stub.GetTxTimestamp()
	created := time.Unix(epochTime.GetSeconds(), 0)

	putBankBase := models.BankBase{
		BankID:          1, //args[0],
		BankName:        "Test Bank",
		HqAddress:       "Test HqAddress",
		BankCategory:    "Test BankCategory",
		BankAdminUserID: 1, //args[4],
		Location:        "Test Location",
		LocationLat:     "Test LocationLat",
		LocationLong:    "Test LocationLong",
		Created:         created,
		Createdby:       "Test Models Caller",
	}

	putBank := &models.Bank{ObjectType: utils.BANKOO, BankID: "1", Data: putBankBase}
	stub.MockTransactionStart("2")

	putResp := putBank.PutState(stub)
	assert.EqualValues(shim.OK, putResp.GetStatus(), "models.Bank.PutState test Bank to chain state failed.")

	var ccResp struct {
		Code    string      `json:"code"`
		Message string      `json:"message"`
		Payload models.Bank `json:"payload"`
	}

	getBank := &models.Bank{BankID: "1"}
	getResp := getBank.GetState(stub)
	assert.EqualValues(shim.OK, getResp.GetStatus(), "models.Bank.GetState test Bank from chain state failed ")

	if err := json.Unmarshal(getResp.GetPayload(), &ccResp); err != nil {
		panic(err)
	}

	assert.Equal(putBank.BankID, ccResp.Payload.BankID, "Retrieved Bank ID mismatch")
	assert.Equal(putBank.ObjectType, ccResp.Payload.ObjectType, "Retrieved Object Type mismatch")
}
