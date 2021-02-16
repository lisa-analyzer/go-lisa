package main

import "fmt"

func returnInLoop() {
	
	for i := 0; i < 10; i++ {
		i = i - 1;
	}
	
	return i;
}

func returnInIfStmt1(i int) {
	if i < 3 {
		return 1
	} else {
		return 2
	}
}

func returnInIfStmt2(i int) {
	if i < 3 {
		i++
		return 1
	} else {
		i--
		return 2
	}
}
