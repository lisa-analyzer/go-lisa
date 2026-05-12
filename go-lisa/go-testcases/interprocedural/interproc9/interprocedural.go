package main

func f() (int,int) {
	return 1,2
}

//https://golang.org/doc/effective_go#blank_assign
func blankAssig(){
    x, _ := f()
    _, y := f()
}

func main() {
	blankAssig()
}


