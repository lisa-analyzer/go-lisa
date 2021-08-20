package main

import (
    "fmt"
    "math/rand"
)

func main() {
	m1()
	m2(rand.Intn(10))
	m3(rand.Intn(10))
	m4(rand.Intn(10))
}


func m5() {
	x := 0
	fmt.Println(x)
}

func m2( a int) {
	x := 0
	fmt.Println(x)
    if a == 0 {
        return
    }
}


func m3( a int) {
	x := 0
	fmt.Println(x)
    if a == 0 {
	
    } else {
		return
	}
}

func m4(a int) {
	x := 0
	fmt.Println(x)
    if a == 0 {
        return
    } else {
		return 
	}

}
