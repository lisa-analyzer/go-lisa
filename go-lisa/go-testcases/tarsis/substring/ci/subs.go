package main

func main(nondet bool) {
	res := "substring test"
	if (nondet) {
		res = res + " passed"
	} else {
		res = res + " failed"
	}

	res = res[5:18]
}