package main

import (
	"container/list"
	"sync"
	"fmt"
    "github.com/hyperledger/shim"
)

var wg sync.WaitGroup

func Invoke( stub shim.ChaincodeStubInterface ) {

	wg.Add(2)
	total := 10000
	l := list.New()

	go func() {
		defer wg.Done()
		for idx := 1; idx <= total; idx++ {
			l.PushBack(idx)
		}
	}()

	go func() {
		defer wg.Done()
		for idx := 1; idx <= total; idx++ {
			l.PushBack(idx)
		}
	}()

	wg.Wait()


	s := ""
	for e := l.Front(); e != nil; e = e.Next() {
		s += fmt.Sprint(e.Value)
	}
		
	stub.PutState("key", []byte{s})
}

func main() {
    

}