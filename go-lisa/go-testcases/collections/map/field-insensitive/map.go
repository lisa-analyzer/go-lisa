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
