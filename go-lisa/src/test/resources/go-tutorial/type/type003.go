package main

import "fmt"

func swap(x, y string) (y string, x string) {
	return y, x
}

type WinnerTakesAll struct {
	int
	bool
	string
}


 
func main() {
	x := WinnerTakesAll{1, true, "aaa"}
}
