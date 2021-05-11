package main

import (
	"fmt"
	"math"
)

type Vertex struct {
	X, Y float64
}

func main() {
	v := Vertex{3, 4}
	v.X = 2
	v.Y = 3
}

func alloc() {
	v1 := Vertex{3,4}
	v2 := Vertex{10,11}

}

func ifAlloc(a,b int) float64 {

	if a == b {
		x := Vertex{1,2}
	} else {
		x := Vertex{3,4}
	}
	return x.X
}

func forAlloc() {
	x := Vertex{0,0}
	for i := 0; i < 100; i++ {
		x.X = x.X + 1
		x.Y = x.Y + 1
	}
	
	return
}
