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

func TestPermissionsWR(t *testing.T) {
	fmt.Println("Entering TestPermissionsLogic")

	assert := assert.New(t)
	// Instantiate mockStub using HDLChaincode as the target chaincode to unit test
	stub := shimtest.NewMockStub("TestStub", new(HDLChaincode))
	//Verify stub is available
	assert.NotNil(stub, "Stub is nil, Test stub creation failed")

	uid := uuid.New().String()

	writeResp := stub.MockInvoke(uid,
		[][]byte{[]byte(utils.PermissionsW),
			[]byte("1"),
			[]byte("1"),
			[]byte("Test Perm Name"),
			[]byte("Test Perm Module"),
		})
	assert.EqualValues(shim.OK, writeResp.GetStatus(), writeResp.GetMessage())

	testID := "1"
	readResp := stub.MockInvoke(uid,
		[][]byte{[]byte(utils.PermissionsR),
			[]byte(testID),
		})
	assert.EqualValues(shim.OK, readResp.GetStatus(), readResp.GetMessage())

	var ccResp struct {
		Code        string             `json:"code"`
		Permissions string             `json:"message"`
		Payload     models.Permissions `json:"payload"`
	}
	if err := json.Unmarshal(readResp.GetPayload(), &ccResp); err != nil {
		panic(err)
	}

	assert.Equal(testID, ccResp.Payload.PermID, "Retrieved Permissions ID mismatch")
	assert.Equal(utils.PERMSN, ccResp.Payload.ObjectType, "Retrieved Object Type mismatch")
}
