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
	
	assert (strings.Contains(res, "g"));
	assert (strings.Contains(res, "p"));
	assert (strings.Contains(res, "f"));
	assert (strings.Contains(res, "d"));
}