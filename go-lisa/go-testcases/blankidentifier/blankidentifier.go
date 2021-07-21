package main

func f() (int,int) {
		return 1,2
}

//https://golang.org/doc/effective_go#blank_assign
func blankAssig(){
    x, _ := f()
    _, y := f()
}


//https://golang.org/doc/effective_go#maps
fun mapTest(tz string) {

	var timeZone = map[string]int{
    "UTC":  0*60*60,
    "EST": -5*60*60,
    "CST": -6*60*60,
    "MST": -7*60*60,
    "PST": -8*60*60,
	}
	
	_, present := timeZone[tz]
	
}

func main() {

	blankAssig()
	mapTest("PST")
}


