package main

import (
	"encoding/json"
	"fmt"
	"testing"

	"github.com/abbeydabiri/hdlchaincode/models"
	"github.com/abbeydabiri/hdlchaincode/utils"

	"github.com/google/uuid"
	"github.com/hyperledger/fabric-chaincode-go/shim"
	"github.com/hyperledger/fabric-chaincode-go/shimtest"
	"github.com/stretchr/testify/assert"
)

func TestBankBranchWR(t *testing.T) {
	fmt.Println("Entering TestBankBranchLogic")

	assert := assert.New(t)
	// Instantiate mockStub using HDLChaincode as the target chaincode to unit test
	stub := shimtest.NewMockStub("TestStub", new(HDLChaincode))
	//Verify stub is available
	assert.NotNil(stub, "Stub is nil, Test stub creation failed")

	uid := uuid.New().String()

	writeResp := stub.MockInvoke(uid,
		[][]byte{[]byte(utils.BankBranchW),
			[]byte("1"),
			[]byte("1"),
			[]byte("Test Branch Name"),
			[]byte("1"),
			[]byte("1"),
			[]byte("Test Location"),
			[]byte("Test Location Lat"),
			[]byte("Test Location Long"),
		})
	assert.EqualValues(shim.OK, writeResp.GetStatus(), writeResp.GetMessage())

	testID := "1"
	readResp := stub.MockInvoke(uid,
		[][]byte{[]byte(utils.BankBranchR),
			[]byte(testID),
		})
	assert.EqualValues(shim.OK, readResp.GetStatus(), readResp.GetMessage())

	var ccResp struct {
		Code    string            `json:"code"`
		Message string            `json:"message"`
		Payload models.BankBranch `json:"payload"`
	}
	if err := json.Unmarshal(readResp.GetPayload(), &ccResp); err != nil {
		panic(err)
	}

	assert.Equal(testID, ccResp.Payload.BranchID, "Retrieved BankBranch ID mismatch")
	assert.Equal(utils.BNKBRA, ccResp.Payload.ObjectType, "Retrieved Object Type mismatch")
}
