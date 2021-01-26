package main

import "fmt"

func strings(x,y string) {
	var y string = "b";
	if (Contains(x, "abc")) {
		y = y + "a";
	} else {
		y = y + "a";
	}
}


func secName(pr1, pr2, javaName string) {
	if HasPrefix(javaName, pr1) {
	  	res = pr2 + javaName[4: 8]
	 } else if HasPrefix(javaName, pr2)  {
 		res = pr1 + javaName[4 : 8]
	 } else {
  		res = javaName[4 : 8]
 	}

	return res
}