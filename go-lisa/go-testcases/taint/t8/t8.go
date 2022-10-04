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
		v:= source()
		{
			v = "qwe"
			sink(v) // Ok
		}
		fmt.Println(v)
	}
	sink(v) // warning
}
