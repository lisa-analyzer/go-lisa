package main

func f1() (int, string) {
	return 1, "hello"
}

func f2() (int, int) {
	return 2, 3
}

func f3() (x int, y int) {
	x = 4
	y = 5
	return
}

// https://tour.golang.org/basics/4
func add1(x int, y int) int {
	return x + y
}

// from https://tour.golang.org/basics/5
func add2(x, y int) int {
	return x + y
}

func main() {
	a, b := f1()	
	c, d := f2()
	e, f := f3()
	g := add1(42, 13)
	h := add2(42, 13)
}