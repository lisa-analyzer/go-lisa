package main

import "fmt"
import "math/rand"

func main() {
	var v uint8 = 0
	arr := rand.Perm(4)

	// using for loop
	for i := 0; i < len(arr); i++ {
			v = v + 1
	}
	fmt.Println(v)
}
