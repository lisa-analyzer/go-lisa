package main

func main(value string, nondet bool) {
	res := "Repeat: "
	for nondet {
		res = res + value + "!"
	}
}