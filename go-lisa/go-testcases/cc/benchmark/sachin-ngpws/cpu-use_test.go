package main

import (
	"fmt"
	"testing"
	"encoding/json"

	"github.com/hyperledger/fabric-chaincode-go/shimtest"
)

func TestBaseFunctinality(t *testing.T){
	simpleChaincode := new(SimpleChaincode)
	mockStub := shimtest.NewMockStub("Test Feature AddCPU", simpleChaincode)
	testTable := []struct{
		name	string
		assetName	string
		functionName	string
		txID	string
		assetValue	[]string
	}{
		{"Adding test","test1","AddCPU","1",make([]string, 0)},
		{"Get test","test1","GetUsage","2",make([]string, 0)},
		{"Adding usage","test1","AddUsage","3",[]string{"cpu1","cpu2"}},
	}

	for _, testCase := range testTable{
		t.Run(testCase.name,func(t *testing.T){
		if testCase.functionName == "AddUsage"{
		res := mockStub.MockInvoke(testCase.txID,[][]byte{[]byte(testCase.functionName),[]byte(testCase.assetName),[]byte(string(testCase.assetValue[0])),[]byte(string(testCase.assetValue[1]))})
		if res.Status != 200{
			t.Fatalf("There was an error status = %v : msg = %v",res.Status,res.Message)
		}
		fmt.Println(string(res.Payload))
		if res.Payload == nil{
			t.Fatalf("Its nil")
		}

		var payload Usage

		err := json.Unmarshal(res.Payload,&payload)
		if err != nil{
			fmt.Printf("The following error %v",err)
		}

		if len(payload.CPU) != 2{
			t.Fatalf("Usage not added, found = %v",payload.CPU)
		}

		} else {
		res := mockStub.MockInvoke(testCase.txID,[][]byte{[]byte(testCase.functionName),[]byte(testCase.assetName)})
		if res.Status != 200{
			t.Fatalf("There was an error status = %v : msg = %v",res.Status,res.Message)
		}
		fmt.Println(string(res.Payload))
		if res.Payload == nil{
			t.Fatalf("Its nil")
		}
		}
		})
	}
}

func TestGetHistory(t *testing.T){
	simpleChaincode := new(SimpleChaincode)
	mockStub := shimtest.NewMockStub("Test Feature AddCPU", simpleChaincode)

	err, res := mockStub.GetHistoryForKey("test1")

	if err != nil{
		t.Errorf("The error is %v",err)
	}

	if res == nil{
		t.Errorf("Its empty")
	}
}