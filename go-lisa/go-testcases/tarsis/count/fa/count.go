package main

import "strings"

func Count(s, substr string) int {
	count := 0
	len := len(substr)
	
	for (strings.Contains(s, substr)) {
		idx := strings.Index(s, substr)
		count = count + 1
		start := idx + len
		end := len(s)
		s = s[start:end]
		
	}

	return count
}

func main(nondet boolean) {
	str := ""
	if nondet {
		str = "this is the string";
	} else {
		str = "the throat";
	}
	
	count := Count(str, "th");
}