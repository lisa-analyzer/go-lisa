package main


func f(a int, b int, c int) [3]int {
	v := [3]int{a,b,c}
	return v
}

func main() {
	x := f(1,2,3)	
	y := f(4,5,6)
	return
}