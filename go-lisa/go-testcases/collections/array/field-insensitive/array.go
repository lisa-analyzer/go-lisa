package array


func emptyAllocs() {
	v1 := [3]int{}
	v1[0] = 0
	v1[1] = 1
	return
}

func allocs() {
	v1 := [3]int{7,8,9}
	v1[0] = 0
	v1[1] = 1
	return
}

func alias() {
	v1 := [3]int{7,8,9}
	v2 := v1
	x := v2[0] + v1[0]
	return
}

func defaultArray() { 
	var x [3]int
	return
}
