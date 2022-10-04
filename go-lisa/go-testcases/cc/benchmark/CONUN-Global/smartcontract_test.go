package chaincode_test

import (
	"fmt"
	"testing"

	"github.com/drive/chaincode"
	"github.com/drive/chaincode/mocks"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	"github.com/hyperledger/fabric-contract-api-go/contractapi"
	"github.com/stretchr/testify/require"
)

// go generate counterfeither -o mocks/transaction.go -fake-name TransactionContext . transactionContext

type transactionContext interface {
	contractapi.TransactionContextInterface
}

// go generate counterfeither -o mocks/chaincodestub.go -fake-name ChaincodeStub . chaincodeStub
type chaincodeStub interface {
	shim.ChaincodeStubInterface
}

// go generate counterfeither -o mocks/statequeryiterator.go -fake-name StateQueryIterator . stateQueryIterator
type stateQueryIterator interface {
	shim.StateQueryIteratorInterface
}

// test ============
func TestCreatFile(t *testing.T) {
	chaincodeStub := &mocks.ChaincodeStub{}
	transactionContext := &mocks.TransactionContext{}
	transactionContext.GetStubReturns(chaincodeStub)

	createFile := chaincode.SmartContract{}
	res, err := createFile.CreateFile(transactionContext, "someId1", "aziz")
	fmt.Println("should be nill", res)
	require.NoError(t, err)

	chaincodeStub.GetStateReturns([]byte{}, nil)
	res, err = createFile.CreateFile(transactionContext, "someId1", "aziz")
	fmt.Println(res)
	require.EqualError(t, err, "The file someId1 already exists")

}

func TestApprove(t *testing.T) {
	chaincodeStub := &mocks.ChaincodeStub{}
	transactionContext := &mocks.TransactionContext{}
	transactionContext.GetStubReturns(chaincodeStub)

	approve := chaincode.SmartContract{}
	res, err := approve.Approve(transactionContext, "someId", "", "secondUser")
	require.NoError(t, err, res)

	chaincodeStub.GetStateReturns([]byte{}, nil)
	res, err = approve.Approve(transactionContext, "someId", "User1", "secondUser")
	require.EqualError(t, err, "owner are wrong address , User1")
}

func TestAllowance(t *testing.T) {
	chaincodeStub := &mocks.ChaincodeStub{}
	transactionContext := &mocks.TransactionContext{}
	transactionContext.GetStubReturns(chaincodeStub)

	allowance := chaincode.SmartContract{}
	res, err := allowance.Allowance(transactionContext, "someid", "azzi")
	require.NoError(t, err, res)

	chaincodeStub.GetStateReturns([]byte{}, nil)
	res, err = allowance.Allowance(transactionContext, "someid", "azzi")
	require.EqualError(t, err, "allowance is empty")

}

func TestLikeContent(t *testing.T) {
	chaincodeStub := &mocks.ChaincodeStub{}
	transactionContext := &mocks.TransactionContext{}
	transactionContext.GetStubReturns(chaincodeStub)

	likeContent := chaincode.SmartContract{}
	res, err := likeContent.LikeContent(transactionContext, "someid", "walletd")
	require.NoError(t, err, res)

	chaincodeStub.GetStateReturns([]byte{}, nil)
	res, err = likeContent.LikeContent(transactionContext, "", "wallet")
	require.EqualError(t, err, "error getting file doesnt exists")
}

func TestDislikeContent(t *testing.T) {
	chaincodeStub := &mocks.ChaincodeStub{}
	transactionContext := &mocks.TransactionContext{}
	transactionContext.GetStubReturns(chaincodeStub)

	dislikeContent := chaincode.SmartContract{}
	res, err := dislikeContent.DislikeContent(transactionContext, "someid", "walletd")
	require.NoError(t, err, res)

	chaincodeStub.GetStateReturns([]byte{}, nil)
	res, err = dislikeContent.DislikeContent(transactionContext, "", "wallet")
	require.EqualError(t, err, "error getting file doesnt exists")
}

func TestCountDownloads(t *testing.T) {
	chaincodeStub := &mocks.ChaincodeStub{}
	transactionContext := &mocks.TransactionContext{}
	transactionContext.GetStubReturns(chaincodeStub)

	countDownloads := chaincode.SmartContract{}
	res, err := countDownloads.CountDownloads(transactionContext, "someid", "walletd")
	require.NoError(t, err, res)

	chaincodeStub.GetStateReturns([]byte{}, nil)
	res, err = countDownloads.CountDownloads(transactionContext, "", "wallet")
	require.EqualError(t, err, "error getting file doesnt exists")
}
