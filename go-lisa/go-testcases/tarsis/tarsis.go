package tarsis


func substring (nondet bool) {
	var res string = "substring test"
	if (nondet) {
		res = res + " passed"
	} else {
		res = res + " failed"
	}
}

func loop(value string, nondet bool) {
	var res string = "Repeat: "
	for ; nondet ; {
		res = res + value
	}
}
