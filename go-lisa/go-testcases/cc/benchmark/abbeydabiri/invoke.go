package main

import (
	"errors"
	"github.com/abbeydabiri/hdlchaincode/logic"
	"github.com/abbeydabiri/hdlchaincode/utils"
	"log"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	pb "github.com/hyperledger/fabric-protos-go/peer"
)

// These are function names from Invoke first parameter
const ()

// Invoke - Implements shim.Chaincode interface Invoke() method
func (hdlc *HDLChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {

	// Retrieve function and in argumentsR. This is not a recommended approach.
	// Instead, GetArgsW() is a more suitable method and works perfectly with protocol buffers
	function, args := stub.GetFunctionAndParameters()
	log.Printf("\nStarting Phantom chaincode Invoke for %s and no of argument passed are %d\n", function, len(args))

	switch function {
	case utils.Init:
		return hdlc.Init(stub)
	case utils.BankR:
		return logic.BankR(stub, args)
	case utils.BankW:
		return logic.BankW(stub, args)
	case utils.BankBranchR:
		return logic.BankBranchR(stub, args)
	case utils.BankBranchW:
		return logic.BankBranchW(stub, args)
	case utils.BranchConfigR:
		return logic.BranchConfigR(stub, args)
	case utils.BranchConfigW:
		return logic.BranchConfigW(stub, args)
	case utils.BuyerR:
		return logic.BuyerR(stub, args)
	case utils.BuyerW:
		return logic.BuyerW(stub, args)
	case utils.LoanR:
		return logic.LoanR(stub, args)
	case utils.LoanW:
		return logic.LoanW(stub, args)
	case utils.LoanBuyerR:
		return logic.LoanBuyerR(stub, args)
	case utils.LoanBuyerW:
		return logic.LoanBuyerW(stub, args)
	case utils.LoanDocR:
		return logic.LoanDocR(stub, args)
	case utils.LoanDocW:
		return logic.LoanDocW(stub, args)
	case utils.LoanMarketShareR:
		return logic.LoanMarketShareR(stub, args)
	case utils.LoanMarketShareW:
		return logic.LoanMarketShareW(stub, args)
	case utils.LoanRatingR:
		return logic.LoanRatingR(stub, args)
	case utils.LoanRatingW:
		return logic.LoanRatingW(stub, args)
	case utils.MessageR:
		return logic.MessageR(stub, args)
	case utils.MessageW:
		return logic.MessageW(stub, args)
	case utils.PermissionsR:
		return logic.PermissionsR(stub, args)
	case utils.PermissionsW:
		return logic.PermissionsW(stub, args)
	case utils.PropertyR:
		return logic.PropertyR(stub, args)
	case utils.PropertyW:
		return logic.PropertyW(stub, args)
	case utils.RoleR:
		return logic.RoleR(stub, args)
	case utils.RoleW:
		return logic.RoleW(stub, args)
	case utils.SellerR:
		return logic.SellerR(stub, args)
	case utils.SellerW:
		return logic.SellerW(stub, args)
	case utils.TransactionR:
		return logic.TransactionR(stub, args)
	case utils.TransactionW:
		return logic.TransactionW(stub, args)
	case utils.UserR:
		return logic.UserR(stub, args)
	case utils.UserW:
		return logic.UserW(stub, args)
	case utils.UserCategoryR:
		return logic.UserCategoryR(stub, args)
	case utils.UserCategoryW:
		return logic.UserCategoryW(stub, args)
	}

	e := utils.ChainError{FCN: "Invoke", KEY: "", CODE: utils.CODEUNKNOWNINVOKE, ERR: errors.New("Unknown function invoke")}
	return shim.Error(e.Error())
}
