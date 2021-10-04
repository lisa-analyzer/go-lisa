package main

import "strings"

func main() {
	a := "abc"
	b := "bcd"
	x := Contains(a,b)
	y := HasPrefix(a,b)
	z := HasSuffix(a,b)
	w := Index(a,b)
	j := Replace(a,b,b)
	return
}