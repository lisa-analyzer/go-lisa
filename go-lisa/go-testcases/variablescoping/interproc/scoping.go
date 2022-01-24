package main

func f1() {
	return 1, 2
}

func f2() {
	return 3, 4
}


func main() {
	x, y := f1()

	{
		x , y := f2()
		x++
		y++
	}

	x++
	y++
}
