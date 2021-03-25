package main

import "fmt"
import "strings"

func strings() {
	a := "abc"
	b := "bcd"
	x := Contains(a,b)
	y := HasPrefix(a,b)
	z := HasSuffix(a,b)
	w := Index(a,b)
	j := Replace(a,b,b)
}


func simpleTyping() int {
	var a int = 1 + 1
	var b float32 = 2 + 2
	var c int = a * a
	var d float32 = 2 - 2
	var e float32 =  +d
	var x string = "a" + "b"
	var subs string = x[1:3]
}

func typeError1(x, y int) int {
	var i int = 1 + 1.2
}

func typeError2(x, y int) int {
	var i int = "a" + "b"
}


func comparisonTyping(x,y int) {
	b := 2 == 2
	var d int = 2 + 2
	e := d == 2
	c := 2 != 2
}

func relTyping() {
	x := 5
	y := 7
	var c int =  x * y
	b := x < 2
	c := 2.2
	d := c < 2.3
}



