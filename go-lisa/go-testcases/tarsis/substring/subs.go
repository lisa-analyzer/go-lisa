package main

import "strings"

func main(nondet bool) {
	res := "substring test"
	if (nondet) {
		res = res + " passed"
	} else {
		res = res + " failed"
	}

	res = res[5:18]
	
	assert (containsChar(res, "g"));
	assert (containsChar(res, "p"));
	assert (containsChar(res, "f"));
	assert (containsChar(res, "d"));
}