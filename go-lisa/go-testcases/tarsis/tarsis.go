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
		res = res + value + "!"
	}
}


func toString(names []string) {
	res := "People: {"
	
	for i := 0; i < len(names); i++ {
		res = res + names[i] // names[i] not right type
		if i != len(names) -1 {
			res = res + ","
		}
	}
	
	res = res + "}"
	return res
}

func main() {
	var helloWorld = "hello world!"
	var hello = helloWorld[:5]
	var world = helloWorld[5:]
}