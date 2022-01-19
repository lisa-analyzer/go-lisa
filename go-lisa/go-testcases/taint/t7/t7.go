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
		v:= "asd" 
		{
			v = "Hello World!"
			sink(v) // Ok
		}
		fmt.Println(v)
	}
	fmt.Println(v)
}
