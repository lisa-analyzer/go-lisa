package main

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	"encoding/json"
	"fmt"
	"github.com/op/go-logging"
	"time"
)

var logger = logging.MustGetLogger("chaincode-test")

type SimpleChaincode struct {
}

const STATE_OPEN = 0

const STATE_PAID = 1

type AccountsHolder struct {
	Accounts []string `json:"accounts"`
}

type Payment struct {
	Status     int    `json:"status"`
	Amount     string `json:"amount"`
	Owner      string `json:"owner"`
	AccountKey string `json:"acct_key"`
	Maturity   string `json:"maturity"`
	PaidAt     string `json:"paid_at"`
}

//====================================================================================================================
// Inicializa o chaincode
//====================================================================================================================
func (t *SimpleChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {

	var accountsHolder AccountsHolder

	bytes, err := json.Marshal(accountsHolder)

	if err != nil {
		return shim.Error("Error creating AccountsHolder record")
	}

	err = stub.PutState("accounts", bytes)

	return shim.Success(nil)
}

//====================================================================================================================
// Roteamento de funçoes
//====================================================================================================================
func (t *SimpleChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {

	function, args := stub.GetFunctionAndParameters()

	if function == "register_payment" {
		logger.Info("Register Payment ", args)
		return t.RegisterPayment(stub, args)
	} else if function == "payment_details" {
		logger.Info("Payment Details ", args)
		return t.GetPaymentDetails(stub, args[0])
	} else if function == "make_payment" {
		logger.Info("Make payment ", args)
		return t.MakePayment(stub, args[0])
	}

	return shim.Success(nil)
}

func (t *SimpleChaincode) GetPaymentDetails(stub shim.ChaincodeStubInterface, accountKey string) pb.Response {

	bytes, err := stub.GetState(accountKey)

	if err != nil {
		return shim.Error("Error when quering records")
	}

	return shim.Success(bytes)

}

func (t *SimpleChaincode) MakePayment(stub shim.ChaincodeStubInterface, accountKey string) pb.Response {

	var payment Payment

	bytes, err := stub.GetState(accountKey)

	err = json.Unmarshal(bytes, &payment)

	if payment.Status == STATE_PAID {
		return shim.Error("It was Paid")
	}

	if err != nil {
		return shim.Error("Error when unmarshal json")
	}

	payment.PaidAt = time.Now().Format("2006-01-02")
	payment.Status = STATE_PAID

	bytes, err = json.Marshal(payment)

	if err != nil {
		return shim.Error("Error when marshal json")
	}

	err = stub.PutState(accountKey, bytes)

	if err != nil {
		return shim.Error("Error when put state to the block")
	}

	return shim.Success(nil)

}

func (chaincode *SimpleChaincode) RegisterPayment(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	payment := Payment{
		Status:   STATE_OPEN, Amount: args[0],
		Owner:    args[1], AccountKey: args[2],
		Maturity: args[3]}

	bytes, err := json.Marshal(payment)

	if err != nil {
		fmt.Printf("register_payment: error converting payment record: %s", err);
		return shim.Error("register_payment: error converting payment record")
	}

	err = stub.PutState(args[2], bytes)

	if err != nil {
		fmt.Printf("register_payment: error storing payment record: %s", err);
		return shim.Error("register_payment: error storing payment record")
	}

	return shim.Success(nil)

}

func main() {
	err := shim.Start(new(SimpleChaincode))

	if err != nil {
		fmt.Printf("Error starting Simple chaincode: %s", err)
	}
}
