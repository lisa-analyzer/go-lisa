package main

import "fmt"
import "strings"

func simpleTyping() {
	var a int = 1 + 1
	var b float32 = 2 + 2
	var c int = a * a
	var d float32 = 2 - 2
	var e float32 =  +d
	var x string = "a" + "b"
	var subs string = x[1:3]
}

func typeError1(x, y int) {
	var i int = 1 + 1.2
}

func typeError2(x, y int)  {
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
	d := 2.2
	e := c < 2.3
}

func arrayTyping() {
	v1 := [3]int{1,2,3}
	v2 := [5]int{1,2,3,4,5}
	x := v1[0]
	y := v2[1]
}
