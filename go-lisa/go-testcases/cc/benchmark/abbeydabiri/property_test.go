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

func TestPropertyWR(t *testing.T) {
	fmt.Println("Entering TestPropertyLogic")

	assert := assert.New(t)
	// Instantiate mockStub using HDLChaincode as the target chaincode to unit test
	stub := shimtest.NewMockStub("TestStub", new(HDLChaincode))
	//Verify stub is available
	assert.NotNil(stub, "Stub is nil, Test stub creation failed")

	uid := uuid.New().String()

	writeResp := stub.MockInvoke(uid,
		[][]byte{[]byte(utils.PropertyW),
			[]byte("1"),
			[]byte("1"),
			[]byte("Test ProType"),
			[]byte("Test ProName"),
			[]byte("Test Desc"),
			[]byte("Test Address"),
			[]byte("Test Location"),
			[]byte("Test LocationLat"),
			[]byte("Test LocationLong"),
			[]byte("Test Views"),
			[]byte("Test ViewerStats"),
			[]byte("Test EntryDate"),
			[]byte("Test ExpiryDate"),
			[]byte("Test Status"),
		})
	assert.EqualValues(shim.OK, writeResp.GetStatus(), writeResp.GetMessage())

	testID := "1"
	readResp := stub.MockInvoke(uid,
		[][]byte{[]byte(utils.PropertyR),
			[]byte(testID),
		})
	assert.EqualValues(shim.OK, readResp.GetStatus(), readResp.GetMessage())

	var ccResp struct {
		Code     string          `json:"code"`
		Property string          `json:"message"`
		Payload  models.Property `json:"payload"`
	}
	if err := json.Unmarshal(readResp.GetPayload(), &ccResp); err != nil {
		panic(err)
	}

	assert.Equal(testID, ccResp.Payload.ProID, "Retrieved Property ID mismatch")
	assert.Equal(utils.PRPRTY, ccResp.Payload.ObjectType, "Retrieved Object Type mismatch")
}
