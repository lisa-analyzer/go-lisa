package main

import (
	"fmt"
	"math"
)

type Vertex struct {
	X, Y float64
}


func (v Vertex) Abs() float64 {
	return v.X * v.X + v.Y * v.Y
}

func main() {
	v := Vertex{3, 4}
	v.X = 2
	v.Y = 3
	x:= v.Abs()
}

func alloc() {
	v1 := Vertex{3,4}
	v2 := Vertex{10,11}

}
