package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"net/http"
	"strconv"
	"time"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/hyperledger/fabric/protos/peer"
)

type Event struct {
	Id          string    `json:"id"`
	Latitude    float64   `json:"latitude"`
	Longitude   float64   `json:"longitude"`
	Orientation float64   `json:"orientation"`
	Image       []byte    `json:"image"`
	CreatedAt   time.Time `json:"created_at"`
	Metadata    string    `json:"metadata"`
}

type SimpleChaincode struct {
}

func (t *SimpleChaincode) Init(stub shim.ChaincodeStubInterface) peer.Response {
	return shim.Success(nil)
}

func (t *SimpleChaincode) Invoke(stub shim.ChaincodeStubInterface) peer.Response {
	fn, args := stub.GetFunctionAndParameters()

	if fn == "register-event" {
		return t.RegisterEvent(stub, args)
	} else if fn == "get-event" {
		return t.GetEvent(stub, args)
	}
	return shim.Error("Unknown function")
}

func (t *SimpleChaincode) RegisterEvent(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	Latitude, err := strconv.ParseFloat(args[1], 64)
	if err != nil {
		fmt.Println("Error while converting latitude to float")
	}
	Longitude, err := strconv.ParseFloat(args[2], 64)
	if err != nil {
		fmt.Println("Error while converting longitude to float")
	}
	Orientation, err := strconv.ParseFloat(args[3], 64)
	if err != nil {
		fmt.Println("Error while converting orientation to float")
	}
	TimestampInt, err := strconv.ParseInt(args[5], 10, 64)
	if err != nil {
		fmt.Println("Error while converting timestamp to int")
	}
	Timestamp := time.Unix(TimestampInt, 0)
	var event = Event{
		Id:          args[0],
		Latitude:    Latitude,
		Longitude:   Longitude,
		Orientation: Orientation,
		Image:       []byte(args[4]),
		CreatedAt:   Timestamp,
		Metadata:    args[6],
	}
	postBody, _ := json.Marshal(event)
	responseBody := bytes.NewBuffer(postBody)

	resp, err := http.Post("http://mock:3000/check", "application/json", responseBody)
	if err != nil {
		fmt.Println(err)
		return shim.Error(err.Error())
	}
	defer resp.Body.Close()
	bodyBytes, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		fmt.Println(err)
		return shim.Error(err.Error())
	}
	if string(bodyBytes) == "Yes" {
		stub.PutState(args[0], postBody)
		return shim.Success(nil)
	}
	return shim.Error("Error")
}

func (t *SimpleChaincode) GetEvent(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	value, err := stub.GetState(args[0])
	if err != nil {
		fmt.Println(err.Error())
		return shim.Error(err.Error())
	}
	return shim.Success(value)
}

func main() {
	if err := shim.Start(new(SimpleChaincode)); err != nil {
		fmt.Println("Error starting...")
	}
}
