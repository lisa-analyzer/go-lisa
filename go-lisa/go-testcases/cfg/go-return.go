package main

import "fmt"


func returns() {
	return 1
	return 2
	return 3
}

func returnInLoop() {
	
	for i := 0; i < 10; i++ {
		return i
	}
	
	return i;
}

func returnInIfStmt1(i int) {
	if i < 3 {
		return 1
	} else {
		return 2
	}
	
	return 3
}

func returnInIfStmt2(i int) {
	if i < 3 {
		i++
		return 1
	} else {
		i--
		return 2
	}
	
	return 3
}
