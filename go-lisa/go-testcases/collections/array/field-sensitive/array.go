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

func matrix() {
   var a [4][2]int = [4][2]int{ {0,0}, {1,2}, {2,4}, {3,6}}
   
   a00 := a[0][0] // 0
   a01 := a[0][1] // 0
   a10 := a[1][0] // 1
   a11 := a[1][1] // 2
   a20 := a[2][0] // 2
   a21 := a[2][1] // 4
   a30 := a[3][0] // 3
   a31 := a[3][1] // 6
   return
}
