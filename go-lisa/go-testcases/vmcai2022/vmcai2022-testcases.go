package str

func copy(rep, pref string) (string, string){
	a := pref;
	b := pref;
	
	for true {
		a = a + rep
		b =  b + rep
	}
}

func bultan1(x1, x2, c string) {
	if x1 == x2 {
		x1 = x1 + c;
	} else {
		x1 = x2 + c;
	}
}


// From https://github.com/viant/afs/blob/fc60e2906d0a042a04ce644f6dff7557193f72b9/matcher/ignore.go
func ignore(expr, location, pref string) bool {
	if HasPrefix(expr, pref) {
		index := Index(expr, pref)
		suffix := expr[index+3:]
		
		if HasSuffix(location, suffix) {
			return true
		}
	}
	
	return false
}

func strings(x string) {
	var y string = "b";
	if (Contains(x, "abc")) {
		z = z + "a";
	} else {
		z = z + "a";
	}
}


func secName(pr1, pr2, javaName string) string {
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

func rep(value string, nondet bool) string {
	var res string = "Repeat: "
	for ; nondet ; {
		res = res + value + "!"
	}
	return res
}

func SanitizePhoneNumber(phoneNumber string, pref string) string {
	ret := phoneNumber
	
	if strings.HasPrefix(phoneNumber, pref) {
		ret = phoneNumber
	}
	
	if strings.HasPrefix(phoneNumber, "08") {
		ret = pref + phoneNumber[2:]
	}
	if strings.HasPrefix(phoneNumber, "8") {
		ret = pref + phoneNumber[1:]
	}
	
	return ret
}

func main() {
	x := "ab"
	y := "a"
	z := "b"
	w := y + z
	return
}