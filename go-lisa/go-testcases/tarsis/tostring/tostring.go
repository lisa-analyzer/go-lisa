package main

import "strings"

func main(names []string) {
	res := "People : {"
	i := 0;
	for i < len(names) {
		res = res + names[i];
		if i != len(names) -1 {
			res = res + ",";
		}
		i++;
	}

	res = res + "}";
	
	assert (strings.Contains(res, "People"));
	assert (strings.Contains(res, ","));
	assert (strings.Contains(res, "not"));
}
