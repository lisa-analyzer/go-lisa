package main

import "fmt"

// from https://tour.golang.org/methods/16
func typeSwitch1() {	
	var i interface{} = "hello"
	switch v := i.(type) {
	case int:
		fmt.Printf("Twice %v is %v\n", v, v*2)
	case string:
		fmt.Printf("%q is %v bytes long\n", v, len(v))
	default:
		fmt.Printf("I don't know about type %T!\n", v)
	}
}

// from https://gobyexample.com/switch
func typeSwitch2() {
	var i interface{} = true
 	switch t := i.(type) {
        case bool:
            fmt.Println("I'm a bool")
        case int:
            fmt.Println("I'm an int")
        default:
            fmt.Printf("Don't know type %T\n", t)
        }
}

func typeSwitch3() {
	var i interface{} = true
 	switch t := i.(type) {
        case bool, string:
            fmt.Println("I'm a bool or string")
        case int:
            fmt.Println("I'm an int")
        default:
            fmt.Printf("Don't know type %T\n", t)
    }
}

