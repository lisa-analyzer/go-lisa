package chaincode_test

import (
	"fmt"
	"testing"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	"github.com/hyperledger/fabric-contract-api-go/contractapi"
	"github.com/stretchr/testify/require"

	"github.com/tokenERC20/chaincode"
	"github.com/tokenERC20/chaincode/mocks"
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

func TestInit(t *testing.T) {
	chaincodeStub := &mocks.ChaincodeStub{}
	transactionContext := &mocks.TransactionContext{}
	transactionContext.GetStubReturns(chaincodeStub)

	init := chaincode.SmartContract{}
	resp, err := init.Init(transactionContext, "0xxx")
	fmt.Println(resp)
	require.NoError(t, err)
}
func TestMint(t *testing.T) {
	chaincodeStub := &mocks.ChaincodeStub{}
	transactionContext := &mocks.TransactionContext{}
	transactionContext.GetStubReturns(chaincodeStub)

	mint := chaincode.SmartContract{}
	resp, err := mint.Mint(transactionContext, 10)
	require.NoError(t, err)
	fmt.Println(resp)

}

func TestBurn(t *testing.T) {
	chaincodeStub := &mocks.ChaincodeStub{}
	transactionContext := &mocks.TransactionContext{}
	transactionContext.GetStubReturns(chaincodeStub)

	burn := chaincode.SmartContract{}
	resp, err := burn.Burn(transactionContext, 10)
	require.NoError(t, err)
	fmt.Println(resp)
}

func TestTransfer(t *testing.T) {
	chaincodeStub := &mocks.ChaincodeStub{}
	transactionContext := &mocks.TransactionContext{}
	transactionContext.GetStubReturns(chaincodeStub)

	transfer := chaincode.SmartContract{}
	resp, err := transfer.TransferFrom(transactionContext, "00x", "00x5", 10, "sign", "msg")
	require.NoError(t, err)
	fmt.Println(resp)
}

func TestGetInfo(t *testing.T) {
	chaincodeStub := &mocks.ChaincodeStub{}
	transactionContext := &mocks.TransactionContext{}
	transactionContext.GetStubReturns(chaincodeStub)

	info := chaincode.SmartContract{}
	resp, err := info.GetDetails(transactionContext)
	fmt.Println(resp)
	require.NoError(t, err)
}
