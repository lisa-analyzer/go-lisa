/*
Copyright IBM Corp. 2016 All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package main

import (
	"encoding/json"
	"fmt"
	"strconv"

	"github.com/hyperledger/fabric/common/util"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)

// 作为p2p借贷系统中投资方所拥有的资产合约，表示谁欠他钱
type UnionLoanChainCode struct {
}

//json序列化 字段需要大写开头

//参加贷款的银行以及参贷金额
type Participant struct {
	BankName string
	Balance  float64
}

type UnionLoan struct {
	Customer     string
	Balance      float64
	Leader       string
	Participants [10]Participant
}

// Init 函数初始化造币，
func (t *UnionLoanChainCode) Init(stub shim.ChaincodeStubInterface) pb.Response {

	return shim.Success(nil)
}
func (t *UnionLoanChainCode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	function, args := stub.GetFunctionAndParameters()
	if function == "offer" {
		return t.offer(stub, args)
	} else if function == "query" {
		return t.query(stub, args)
	}

	return shim.Error("Invalid invoke function name. Expecting \"invoke\" \"delete\" \"query\"")
}

func (t *UnionLoanChainCode) offer(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	var amt float64 //temp
	var err error

	var chainCodeToCall = args[0] //转账调用的chaincodeid
	var loanId = args[1]
	var customer = args[2]
	var leader = args[3]
	var balance float64 //贷款总额，参贷行的求和
	var unionLoan UnionLoan
	var participants [10]Participant

	if len(args) < 5 || len(args)%2 == 0 {
		return shim.Error("Incorrect number of arguments. Expecting Odd and >=5")
	}

	//循环读取参贷行信息
	var count = 3
	var i = 0
	for count < len(args) {
		participants[i].BankName = args[count]
		amt, err = strconv.ParseFloat(args[count+1], 64)
		if err != nil {
			return shim.Error("Invalid transaction amount, expecting a float value")
		}
		participants[i].Balance = amt
		balance += amt
		count += 2
		i += 1
	}
	var bankNum = i

	unionLoan.Customer = customer
	unionLoan.Balance = balance
	unionLoan.Leader = leader
	unionLoan.Participants = participants

	loanByte, err := json.Marshal(unionLoan)
	if err != nil {
		return shim.Error("build json failed!")
	}

	err = stub.PutState(loanId, loanByte)
	if err != nil {
		return shim.Error(err.Error())
	}

	//调用转账合约
	f := "invoke"
	i = 0
	for i < bankNum {
		invokeArgs := util.ToChaincodeArgs(f, participants[i].BankName, customer,
			strconv.FormatFloat(participants[i].Balance, 'f', 0, 64))
		response := stub.InvokeChaincode(chainCodeToCall, invokeArgs, "")
		if response.Status != shim.OK {
			errStr := fmt.Sprintf("Failed to invoke chaincode. Got error: %s", string(response.Payload))
			fmt.Printf(errStr)
			return shim.Error(errStr)
		}
		i += 1
	}

	return shim.Success(loanByte)
}

// query callback representing the query of a chaincode
func (t *UnionLoanChainCode) query(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	var loanId string // Entities
	var err error

	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting name of the person to query")
	}

	loanId = args[0]

	// Get the state from the ledger
	loanByte, err := stub.GetState(loanId)
	if err != nil {
		jsonResp := "{\"Error\":\"Failed to get state for " + loanId + "\"}"
		return shim.Error(jsonResp)
	}

	if loanByte == nil {
		jsonResp := "{\"Error\":\"Nil context for " + loanId + "\"}"
		return shim.Error(jsonResp)
	}

	var unionLoan UnionLoan
	error := json.Unmarshal(loanByte, &unionLoan)
	if error != nil {
		jsonResp := "{\"Error\":\"json ummarshal " + fmt.Sprint(loanByte) + "\"}"
		return shim.Error(jsonResp)
	}
	return shim.Success(loanByte)
}

func main() {
	err := shim.Start(new(UnionLoanChainCode))
	if err != nil {
		fmt.Printf("Error starting Simple chaincode: %s", err)
	}
}
