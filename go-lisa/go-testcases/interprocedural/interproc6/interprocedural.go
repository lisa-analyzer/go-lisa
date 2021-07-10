package main

func main() {
    x, _ := f()
    _, y := f()
}
    
func f() (int,int) {
	return 1,2
}