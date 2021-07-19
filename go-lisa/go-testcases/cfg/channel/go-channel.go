package main

import "fmt"



func main() {
	s := []int{7, 2, 8, -9, 4, 0}

	c := make(chan int)
	c <- 1
	c <- 2
	x, y := <-c, <-c // receive from c

	fmt.Println(x, y, x+y)
}

func chan1() {
    messages := make(chan string)
    msg := <-messages
    fmt.Println(msg)
}