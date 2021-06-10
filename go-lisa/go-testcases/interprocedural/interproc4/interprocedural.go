package main


type Vertex struct {
	X, Y int
}

// from https://tour.golang.org/methods/2
func f2(v Vertex)  int {
	return v.X*v.X + v.Y*v.Y
}

// from https://tour.golang.org/methods/1
func (v Vertex) f1() int {
	return v.X*v.X + v.Y*v.Y
}

func main() {
	v := Vertex{3, 4}
	abs1 := v.f1()
	abs2 := f2(v)
}