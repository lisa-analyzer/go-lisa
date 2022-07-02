package main

import "fmt"

func source() string {
	return "asd"
}

func sink(v string) {
	fmt.Println(v)
}

func main() {
	v := source()
	{
		sink(v) // Warning
	}
	
}
