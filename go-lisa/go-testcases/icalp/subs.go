package main

import "fmt"

func strings(x,y string) {
	var y string = "b";
	if (strings.Contains(x, "abc")) {
		y = y + "a";
	} else {
		y = y + "a";
	}
}


func secName(pr1, pr2, javaName string) {
	pr1 = "r"
	pr2 = "p"
	javaName = "j"
	if strings.HasPrefix(javaName, pr1) {
	  	res = pr2 + javaName[4: 8]
	 } else if strings.HasPrefix(javaName, pr2)  {
 		res = pr1 + javaName[4 : 8]
	 } else {
  		res = javaName
 	}

	return res
}