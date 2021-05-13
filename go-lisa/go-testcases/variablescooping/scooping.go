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
	}
	
	a = b + 2

}