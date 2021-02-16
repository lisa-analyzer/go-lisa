package main

import "fmt"

func simpleIf() {
	var i int = 1
	
	if i == 2 {
		var a int = 1
	} else {
		var b int = 2
	}
	
	return 
}

func ifWithAsg() {
	if x := 100; x == 100 {
		y := 5
	}
	
	return 
}
func asgs() {
	if x == 100 {
		x := 1
		y := 2
	} else {
		w := 3
		z := 4
	}	

	return
}

func elseIf() {
	if x == 100 {
		x := 1
		y := 2
	} else if x == 99 {
		z := 3
	} else {
		w := 4
	}
	
	return
}

func elseIfAsgs() {
	var x,y,w,z int = 1,2,3,4
	
	if x == 100 {
		x = 1
		y = 2
	} else if x == 99 {
		z = 3
	} else {
		w = 4
	}
	
	return
}

func ifWithoutElse() {
	var x,y,z int = 1,2,3
	
	if x == 100 {
		x = 1
		y = 2
	} else if x == 99 {
		z = 3
	} 
	
	return
}