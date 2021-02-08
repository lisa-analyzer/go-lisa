package str

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

func substring (nondet bool) {
	var res string = "substring test"
	if (nondet) {
		res = res + " passed"
	} else {
		res = res + " failed"
	}
}

func rep(value string, nondet bool) {
	var res string = "Repeat: "
	for ; nondet ; {
		res = res + value + "!"
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