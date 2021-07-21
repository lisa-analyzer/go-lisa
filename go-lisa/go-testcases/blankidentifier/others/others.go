//This file contains some cases not yet covered or to investigate

import (
    "fmt"
    "io"
)

//https://golang.org/doc/effective_go#for
func rangeTest(){

	array := []int{2, 3, 4}
	sum := 0
	for _, value := range array {
		sum += value
	}
}

//https://golang.org/doc/effective_go#blank_import
import _ "net/http/pprof"

//https://golang.org/doc/effective_go#blank_unused
var _ = fmt.Printf
var _ io.Reader 

//https://golang.org/doc/effective_go#constants
type ByteSize float64
const (
    _           = iota // ignore first value by assigning to blank identifier
    KB ByteSize = 1 << (10 * iota)
    MB
    GB
    TB
    PB
    EB
    ZB
    YB
)

const (
    C1 = iota + 1
    _
    C3
    C4
)
fmt.Println(C1, C3, C4) // "1 3 4"