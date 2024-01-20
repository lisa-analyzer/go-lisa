package main

import "fmt"

func main() {
	var v uint8 = 0
	i := 1
	for i <= 3 {
			fmt.Println(i)
			v = v + 1
			i = i + 1
	}
	fmt.Println(v)
}
