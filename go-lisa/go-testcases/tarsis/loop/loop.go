package main

import "strings"

func main(value string, nondet bool) {
	res := "Repeat: "
	for nondet {
		res = res + value + "!"
	}
	assert (strings.Contains(res, "t")); 
	assert (strings.Contains(res, "!")); 
	assert (strings.Contains(res, "f"));
}