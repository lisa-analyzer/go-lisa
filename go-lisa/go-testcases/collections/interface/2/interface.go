// From // From https://tour.golang.org/methods/10
package main

import "fmt"

type I interface {
	M() string
}

type T struct {
	S string
}

// This method means type T implements the interface I,
// but we don't need to explicitly declare that it does so.
func (t T) M() string {
	return t.S
}

func main() {
	var i I = T{"hello"} // assign i = ref$pp1 cast-as I
	x := i.M()
	return
}