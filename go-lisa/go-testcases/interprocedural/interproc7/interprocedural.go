package main

import "fmt"

func main() {
	a := func(f int) int {
			return f * f
		}(5)
	fmt.Println(a)
}