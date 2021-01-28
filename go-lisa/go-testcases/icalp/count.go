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
		x1 = x1 + c;
	} else {
		x1 = x2 + c;
	}
}

// From https://github.com/viant/afs/blob/fc60e2906d0a042a04ce644f6dff7557193f72b9/matcher/ignore.go
func ingnore(expr, location, pref string) {
	if HasPrefix(expr, pref) {
		index := Index(expr, pref)
		suffix := expr[index+3:]
		
		if HasSuffix(location, suffix) {
			return true
		}
	}
}

func SanitizePhoneNumber(phoneNumber string, pref string) string {
	ret := phoneNumber
	
	if strings.HasPrefix(phoneNumber, pref) {
		ret := phoneNumber
	}
	
	if strings.HasPrefix(phoneNumber, "08") {
		ret := pref + phoneNumber[2:]
	}
	if strings.HasPrefix(phoneNumber, "8") {
		ret := pref + phoneNumber[1:]
	}
	
	return ret
}