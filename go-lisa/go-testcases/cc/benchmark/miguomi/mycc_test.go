package main

import (
	"fmt"
	"github.com/hyperledger/fabric-chaincode-go/shim"

	//"github.com/hyperledger/fabric-chaincode-go/shim"
	"github.com/hyperledger/fabric-chaincode-go/shimtest"
	"testing"
)

func TestMycc(t *testing.T) {
	//模拟链码部署
	scc := new(MyChainCode)
	stub := shimtest.NewMockStub("MyChainCode", scc)
	CheckInit(t, stub, [][]byte{[]byte("schoolId_A"), []byte("学校1")})
	//调用链码
	CheckGet(t, stub, []string{"get", "schoolId_A"})
	CheckSet(t, stub, []string{"set", "schoolId_C","学习3"})
	CheckGet(t, stub, []string{"get", "schoolId_C"})

}

func CheckInit(t *testing.T, stub *shimtest.MockStub, args [][]byte) {
	res := stub.MockInit("1", args)
	if res.Status != shim.OK {
		fmt.Println("Init failed", string(res.Message))
		t.FailNow()
	}
}

func CheckGet(t *testing.T, stub *shimtest.MockStub, args []string) {
	res := stub.MockInvoke("1", [][]byte{[]byte(args[0]), []byte(args[1])})
	if res.Status != shim.OK {
		fmt.Println("InitSchool failed:", args[0], string(res.Message))
		t.FailNow()
	}
	fmt.Printf("checkGet[%s]\n", string(res.Payload))
}


func CheckSet(t *testing.T, stub *shimtest.MockStub, args []string) {
	res := stub.MockInvoke("1", [][]byte{[]byte(args[0]), []byte(args[1]),[]byte(args[2])})
	if res.Status != shim.OK {
		fmt.Println("InitSchool failed:", args[0], string(res.Message))
		t.FailNow()
	}
	fmt.Printf("checkSet[%s]\n", string(res.Payload))
}



