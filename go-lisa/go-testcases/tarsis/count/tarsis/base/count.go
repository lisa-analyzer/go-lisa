package main

import "strings"

func Count(s, substr string) int {
	count := 0
	l := len(substr)
	
	for (strings.Contains(s, substr)) {
		idx := strings.Index(s, substr)
		count = count + 1
		start := idx + l
		end := len(s)
		s = s[start:end]	
	}

	return count
}

func main(nondet boolean) {
	str := ""
	if nondet {
		str = "this is the thing";
	} else {
		str = "the throat";
	}
	
	count := Count(str, "th");
}