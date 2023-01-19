package main

func main(nondet bool) {
	res := "test"
	if (nondet) {
		res = res + " passed"
	} else {
		res = res + " failed"
	}

	res = res[2:10]
}