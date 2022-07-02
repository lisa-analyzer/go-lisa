package main

import (
	"os"
	"testing"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	"github.com/hyperledger/fabric-chaincode-go/shimtest"
)

var mstub *shimtest.MockStub

func init() {
	mstub = shimtest.NewMockStub("TestStub", new(SimpleAssetChaincode))
	if mstub == nil {
		panic("mock stub creation failed")
	}
}

func TestInvokeUnkownFunction(t *testing.T) {
	result := mstub.MockInvoke("unknownInvokeFunc", [][]byte{[]byte("unknown")})
	if result.Status != shim.ERROR {
		t.Log("chaincode invoke should have failed as we requested to exectue a handler which does not exist")
		t.Fail()
	}
}

func TestMain(m *testing.M) {
	os.Exit(m.Run())
}
