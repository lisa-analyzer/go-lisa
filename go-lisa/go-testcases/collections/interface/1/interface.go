package main

type geometry interface {
    perim() int
}

type rect struct {
    width, height int
}

func (r rect) perim() int {
    return 2 * r.width + 2 * r.height
}


func measure(g geometry) int {
    return g.perim()
}

func main() {
    ret := rect{width: 3, height: 4}
    a := measure(ret)
	return
}