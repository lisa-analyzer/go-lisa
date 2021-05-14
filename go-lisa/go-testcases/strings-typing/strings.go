package main

import "strings"

func main() {
	a := "abc"
	b := "bcd"
	x := strings.Contains(a,b)
	y := strings.HasPrefix(a,b)
	z := strings.HasSuffix(a,b)
	w := strings.Index(a,b)
	j := strings.Replace(a,b,b)
}