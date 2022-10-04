package main

import (
	"fmt"
	"github.com/hyperledger/fabric-chaincode-go/shim"
	"github.com/hyperledger/fabric-protos-go/peer"

)

type MyChainCode struct {
	
}

func (t *MyChainCode)Init(stub shim.ChaincodeStubInterface) peer.Response{
	args := stub.GetStringArgs()
	err := stub.PutState(args[0],[]byte(args[1]))
	if err != nil{
		shim.Error(err.Error())
	}

	return shim.Success(nil)
}


func (t *MyChainCode)Invoke(stub shim.ChaincodeStubInterface) peer.Response{

	fn,args := stub.GetFunctionAndParameters()
	if fn == "set"{
		return t.set(stub,args)
	}else if fn == "get"{
		return t.get(stub,args)
	}

	return shim.Error("Invlide fuction error")
}
func (t *MyChainCode)set(stub shim.ChaincodeStubInterface,args []string) peer.Response{
	err := stub.PutState(args[0],[]byte(args[1]))
	if err != nil{
		shim.Error(err.Error())
	}

	return shim.Success(nil)
}

func (t *MyChainCode)get(stub shim.ChaincodeStubInterface,args []string) peer.Response{
	value,err :=stub.GetState(args[0])
	if err != nil{
		shim.Error(err.Error())
	}

	return shim.Success(value)
}


func main()  {
	err := shim.Start(new(MyChainCode))
	if err != nil{
		fmt.Printf("MychainCode start failed,err:[%s]\n",err.Error())
	}
}


