package structs


type Vertex struct {
	x int
	y int
}


func allocs() {
	v1 := Vertex{0,1}
	v2 := Vertex{1,2}
}
