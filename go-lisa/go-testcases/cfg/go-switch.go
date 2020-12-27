package main

import "fmt"

func simpleSwitch(x int) {
	y = 0;
	switch x {
		case 1: y + 1; 
		case 2: y + 2;
		case 3: y + 3;
	}
}

func noGuard(x, y int) {
	switch {
		case x < y: f1()
		case x < z: f2()
		case x == 4: f3()
	}
}