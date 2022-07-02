package chaincode_test

import (
	"testing"

	"github.com/conun/wrapchain/chaincode"
	"github.com/conun/wrapchain/chaincode/mocks"
	"github.com/hyperledger/fabric-chaincode-go/shim"
	"github.com/hyperledger/fabric-contract-api-go/contractapi"
	"github.com/stretchr/testify/require"
)

// go generate counterfeither -o mocks/trnasction.go -fake -name Transaction
type transactionContext interface {
	contractapi.SettableTransactionContextInterface
}

type chaincodeStub interface {
	shim.ChaincodeStubInterface
}

type stateQueryIterator interface {
	shim.StateQueryIteratorInterface
}

func TestActionWrite(t *testing.T) {
	chaincodeStub := &mocks.ChaincodeStub{}
	transactionContext := &mocks.TransactionContext{}
	transactionContext.GetStubReturns(chaincodeStub)

	actionWrite := chaincode.SmartContract{}
	err := actionWrite.ActionWrite(transactionContext, "aziiz", 1, 200, "minttest", "00x")
	require.NoError(t, err)

	chaincodeStub.GetStateReturns([]byte{}, nil)
	err = actionWrite.ActionWrite(transactionContext, "aziiz", 1, 200, "minttest", "00x")
	require.EqualError(t, err, "The action already exists: 1")
}

func TestGetActionById(t *testing.T) {
	chaincodeStub := &mocks.ChaincodeStub{}
	transactionContext := &mocks.TransactionContext{}
	transactionContext.GetStubReturns(chaincodeStub)

	getActionById := chaincode.SmartContract{}
	_, err := getActionById.GetActionById(transactionContext, 1)
	require.NoError(t, err)

	chaincodeStub.GetStateReturns([]byte{}, nil)
	_, err = getActionById.GetActionById(transactionContext, 22)
	require.EqualError(t, err, "The action with: 1 does not exists")
}
