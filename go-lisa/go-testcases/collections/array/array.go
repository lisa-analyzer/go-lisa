package array


func emptyAllocs() {
	v1 := [3]int{}
	v1[0] = 0
	v1[1] = 1
}

func allocs() {
	v1 := [3]int{7,8,9}
	v1[0] = 0
	v1[1] = 1
}