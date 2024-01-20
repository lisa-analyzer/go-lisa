package main

import "fmt"

func main() {
	var v uint8 = 0
	arr := [5]int{1, 2, 3, 4, 5}

	// using for loop
	for i := 0; i < len(arr); i++ {
			v = v + 1
	}
	fmt.Println(v)
}
