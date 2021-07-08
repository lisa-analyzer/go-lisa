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

// from https://tour.golang.org/basics/4
func add1(x int, y int) int {
	return x + y
}

// from https://tour.golang.org/basics/5
func add2(x, y int) int {
	return x + y
}

// from https://tour.golang.org/basics/6
func swap(x, y int) (int, int) {
	return y, x
}

func main() {
	a, b := f1()	
	c, d := f2()
	e, f := f3()
	g := add1(42, 13)
	h := add2(42, 13)
	i, j := swap(1,2)
	l, k := split(17)
	return
}

// from https://tour.golang.org/basics/7
func split(sum int) (x, y int) {
	x = sum * 4 / 9
	y = sum - x
	return
}