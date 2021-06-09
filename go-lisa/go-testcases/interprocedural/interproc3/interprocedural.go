package main

func f1() (int,string) {
	return 1, "hello"
}

func f2() (int,int) {
	return 2, 3
}

func main() {
	a, b := f1()	
	c, d := f2()
}