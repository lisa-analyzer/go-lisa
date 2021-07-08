package main

import "fmt"

func simpleFor() {
	const A,B int = 1,2
	
	sum := 0
	for i := 0; i < 10; i++ {
		sum += i
	}
	
	res := "Hello"
}

func noInitFor() int {
	const (
		// Big number
		Big = 100
		// Small number
		Small = 0
	)
	
	sum := 0
	for ; i < 10 ; i++ {
		sum = sum + i
	}
	
	return sum
}

func noIncFor() int {
	sum := 0
	for i := 0 ; i < 10 ; {
		sum = sum + i
	}
	
	return sum
}