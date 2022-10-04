package main

import "fmt"

func source() string {
	return "asd"
}

func sink(v string) {
	fmt.Println(v)
}

func main() {

	const n string = "hello"
	{
		n := source()
		{
			n = "world"

		}
		
		sink(n) // ok
		n = source()
		sink(n) // warning
	}
	sink(n) // ok
}
