package mocking

import (
	"crypto/x509"

	"github.com/hyperledger/fabric-chaincode-go/pkg/attrmgr"
	"github.com/hyperledger/fabric-chaincode-go/shim"
)

// MockClientIdentity Implements the ClientIdentity interface for unit testing chaincode.
type MockClientIdentity struct {
	stub  shim.ChaincodeStubInterface
	mspID string
	cert  *x509.Certificate
	attrs *attrmgr.Attributes
}

func NewMockClientIdentity(stub shim.ChaincodeStubInterface, mspID string, cert *x509.Certificate, attrs *attrmgr.Attributes) *MockClientIdentity {
	return &MockClientIdentity{stub, mspID, cert, attrs}
}

func (m *MockClientIdentity) GetID() (string, error) {
	return "", nil
}

func (m *MockClientIdentity) GetMSPID() (string, error) {
	return m.mspID, nil
}

func (m *MockClientIdentity) GetAttributeValue(string) (string, bool, error) {
	return "", false, nil
}

func (m *MockClientIdentity) AssertAttributeValue(string, string) error {
	return nil
}

func (m *MockClientIdentity) GetX509Certificate() (*x509.Certificate, error) {
	return m.cert, nil
}
