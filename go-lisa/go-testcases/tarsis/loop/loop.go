package main

import "strings"

func main(value string, nondet bool) {
	res := "Repeat: "
	for nondet {
		res = res + value + "!"
	}
	assert (containsChar(res, "t")); 
	assert (containsChar(res, "!")); 
	assert (containsChar(res, "f"));
}