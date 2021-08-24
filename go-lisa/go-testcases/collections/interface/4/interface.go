package main

import "fmt"

func main() {
	var i interface{} = "hello"

	s := i.(string)

	f := i.(float64)
}