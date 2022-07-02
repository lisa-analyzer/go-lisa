package mocking

import (
	"github.com/hyperledger/fabric-chaincode-go/pkg/cid"
	"github.com/hyperledger/fabric-chaincode-go/shim"
)

// MockTransactionContext implements the TransactionContextInterface for unit testing chaincode.
type MockTransactionContext struct {
	stub shim.ChaincodeStubInterface
	cid  cid.ClientIdentity
}

func (t *MockTransactionContext) GetStub() shim.ChaincodeStubInterface {
	return t.stub
}

func (t *MockTransactionContext) GetClientIdentity() cid.ClientIdentity {
	return t.cid
}

func NewMockTransactionContext(stub shim.ChaincodeStubInterface, cid cid.ClientIdentity) *MockTransactionContext {
	return &MockTransactionContext{stub, cid}
}
