package main

import "strings"

func Count(s, substr string) int {
	// special case
	if len(substr) == 0 {
		return len(s) + 1
	}

	n := 0
	for {
		i := strings.Index(s, substr)
		if i == -1 {
			return n
		}
		n++
		s = s[i+len(substr):]
	}
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