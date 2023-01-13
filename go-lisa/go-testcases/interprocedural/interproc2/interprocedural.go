package main

func build(a int ,b int ,c int) {
	res := [3]int{a,b,c}
	return identity(res)
}
	
func identity(x [3]int) {
	y := x;
	return y;
}

func main() {
    arr1 := build(1,2,3)
    arr2 := build(-5,5,6)
    x, y := f()
    return
}
    
func f() (int,int) {
	return 1,2
}
