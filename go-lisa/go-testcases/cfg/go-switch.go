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

func simpleSwitchWithFt(x int) {
	y = 0;
	switch x {
		case 1: y + 1; fallthrough
		case 2: y + 2; fallthrough
		case 3: y + 3; fallthrough
	}
}

func simpleSwitchWithDefault(x int) {
	y = 0;
	switch x {
		case 1: y + 1; 
		case 2: y + 2;
		case 3: y + 3;
		default: y + 4;
	}
}

func simpleSwitchWithDefault2(x int) {
	y = 0;
	switch x {
		default: y + 4;
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

func mixed(tag int) {
	switch tag {
		case 0: s0();
		case 1: s1();
		case 2: s2(); fallthrough
		case 3: s3();
		default: def();
		case 4: s4();
	}
}