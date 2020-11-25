package main


type MyInt = int

type rectangle struct {
	length  int
	breadth int
	color   string
}


func add(m map[string]map[string]int, path, country string) {
    var x int = 1;
    var y int = 2;

	var a [5]int = [5]int{0:1, 1:2, 2:3, 3:4, 4:5}
	a[4] = 100

	b := [5]int{0:1, 1:2, 2:3, 3:4, 4:5}

	var twoD [2][3]int = b;
	for i := 0; i < 2; i++ {
		for j := 0; j < 3; j++ {
			twoD[i][j] = i + j
		}
	}
}