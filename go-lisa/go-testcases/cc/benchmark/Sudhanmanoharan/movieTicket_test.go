package main

import (
	"fmt"
	"testing"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	"github.com/hyperledger/fabric-contract-api-go/contractapi"
	"github.com/hyperledger/fabric-samples/asset-transfer-basic/chaincode-go/chaincode/mocks"
	"github.com/stretchr/testify/require"
)

//go:generate counterfeiter -o mocks/transaction.go -fake-name TransactionContext . transactionContext
type transactionContext interface {
	contractapi.TransactionContextInterface
}

//go:generate counterfeiter -o mocks/chaincodestub.go -fake-name ChaincodeStub . chaincodeStub
type chaincodeStub interface {
	shim.ChaincodeStubInterface
}

//go:generate counterfeiter -o mocks/statequeryiterator.go -fake-name StateQueryIterator . stateQueryIterator
type stateQueryIterator interface {
	shim.StateQueryIteratorInterface
}

func Test_Init(t *testing.T) {
	chaincodeStub := &mocks.ChaincodeStub{}
	transactionContext := &mocks.TransactionContext{}
	transactionContext.GetStubReturns(chaincodeStub)
	movieTicketCC := new(SmartContract)
	err := movieTicketCC.Init(transactionContext)
	require.NoError(t, err)
	fmt.Printf("******* Init Ledger *******")
}

func Test_Register_Theatre(t *testing.T) {
	chaincodeStub := &mocks.ChaincodeStub{}
	transactionContext := &mocks.TransactionContext{}
	transactionContext.GetStubReturns(chaincodeStub)
	movieTicketCC := new(SmartContract)
	args := []string{"theatreName\":\"IMax Studios\",\"windows\":4,\"ticketsPerShow\":100,\"showsDaily\":4,\"sodaStock\":200,\"halls\":5"}
	_, err := movieTicketCC.registerTheatre(transactionContext, args)
	require.NoError(t, err)
}

func Test_Create_Show(t *testing.T) {
	chaincodeStub := &mocks.ChaincodeStub{}
	transactionContext := &mocks.TransactionContext{}
	transactionContext.GetStubReturns(chaincodeStub)
	movieTicketCC := new(SmartContract)
	args := []string{"TheatreNo\":4728,\"Shows\":[{\"movie\":\"Mission Impossible\",\"hallNo\":1},{\"movie\":\"Pirates of the Caribbean\",\"hallNo\":2},{\"movie\":\"Avengers\",\"hallNo\":3},{\"movie\":\"Iron Man\",\"hallNo\":4}]"}
	_, err := movieTicketCC.createShow(transactionContext, args)
	require.NoError(t, err)
}

func Test_Purchase_Ticket(t *testing.T) {
	chaincodeStub := &mocks.ChaincodeStub{}
	transactionContext := &mocks.TransactionContext{}
	transactionContext.GetStubReturns(chaincodeStub)
	movieTicketCC := new(SmartContract)
	args := []string{"show\":{\"showID\":1},\"window\":{\"windowNo\":1},\"quantity\":2"}
	_, err := movieTicketCC.purchaseTicket(transactionContext, args)
	require.NoError(t, err)
}

func Test_Issue_Coupon(t *testing.T) {
	chaincodeStub := &mocks.ChaincodeStub{}
	transactionContext := &mocks.TransactionContext{}
	transactionContext.GetStubReturns(chaincodeStub)
	movieTicketCC := new(SmartContract)
	args := []string{"ticketNo\":6"}
	_, err := movieTicketCC.issueCoupon(transactionContext, args)
	require.NoError(t, err)
}

func Test_Coupon_Exchanged(t *testing.T) {
	chaincodeStub := &mocks.ChaincodeStub{}
	transactionContext := &mocks.TransactionContext{}
	transactionContext.GetStubReturns(chaincodeStub)
	movieTicketCC := new(SmartContract)
	args := []string{"ticketNo\":6"}
	_, err := movieTicketCC.couponExchanged(transactionContext, args)
	require.NoError(t, err)
}
