package maps


func main() {
  // creating a map
  subjectMarks := map[int32]int32{1: 85, 2: 80, 3: 81}
}

func emptyAllocs() {
	m := map[int32]int32{}
	m[0] = 0
	m[1] = 86
	return
}

func allocs() {
	m := map[int32]int32{1: 85, 2: 80, 3: 81}
	m[0] = 0
	m[1] = 86
	return
}

func alias() {
	m1 := map[int32]int32{1: 1, 2: 2, 3: 3}
	m2 := m1
	m1[1] = 50;
	m2[2] = 100;
	return
}