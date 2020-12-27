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

func noGuardWithAsg() {
	switch x := f(); {  // missing switch expression means "true"
		case x < 0: y = -x
		case x >= 0: y =  x
	}
}

func multiExps(tag int) {
	switch tag {
		case 0, 1, 2, 3: s1()
		case 4, 5, 6, 7: s2()
	}
}