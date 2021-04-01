package structs


type Vertex struct {
	x int
	y int
}


func allocs() {
	v1 := Vertex{0,0}
	v2 := Vertex{1,1}
}