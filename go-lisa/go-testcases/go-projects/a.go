package main

import (
	"fmt"
	"example/user/hello/morestrings"
)

func sum(a, b int) {
	return a + b + 1
}

func main() {
	x := morestrings.sum(1,2)
	y := sum(1,2)
}