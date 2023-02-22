package main

import "strings"

func Count(s, substr string) int {
	// special case
	if len(substr) == 0 {
		return len(s) + 1
	}

	n := 0
	for true {
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
		str = "this is the thing";
	} else {
		str = "the throat";
	}
	
	count := Count(str, "th");
	
	assert (count > 0); 
	assert (count == 0); 
	assert (count == 3);
}