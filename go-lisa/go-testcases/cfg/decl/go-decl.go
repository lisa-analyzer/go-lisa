package main

import "fmt"

func simpleDecl(x, y int) {
	var i int = 1
	var j int = 2
}

func multDecl() {
	var i, j, k int = 1, 2, 3
}

func multDecls() {
	var (	
		i, j int = 1, 2
		k, l string =  "abc", "def"
	)
}