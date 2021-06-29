package main

func test(b int) {

	a := 0
	sum := 0
	
	for i := 0; i < 10; i++ {
		sum += i
		b = b + 1
		c:= a
		c = c + b
		a =  a + b
		if(a == i) {
			d := 9
			for j := 2; j < 4; j++ {
				d = d + j
			}
			a = d + 4
		} else {
			e := 8
			b = e + 3
		}
	}
	
	a = b + 2
	return
}