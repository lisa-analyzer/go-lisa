package str

func Count(s, substr string) int {
	
	var res string;
	
	s := "abcabc"
	substr := "b"

	n := 0
	for {
		i := Index(s, substr)
		if i == -1 {
			res = n
			break;
		}
		n++
		s = s[i+len(substr):]
	}
	
	return res
}


func copy(rep, pref string) {
	a := pref;
	b := pref;
	
	for true {
		a = a + rep
		b =  b + rep
	}
	
	return a, b
}

func bultan1(x1, x2 string) {
	if x1 == x2 {
		x1 = x2 + c;
	} else {
		x1 = x1 + c;
	}
}