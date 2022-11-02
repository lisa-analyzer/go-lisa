package main

import "fmt"



func main() {
	s := []int{7, 2, 8, -9, 4, 0}

	c := make(chan int)
	c <- 1
	c <- 2
	x, y := <-c, <-c // receive from c

	fmt.Println(x, y)
}

// from https://gobyexample.com/channels
func channels() {
    messages := make(chan string)

    go func() { messages <- "ping" }()

    msg := <-messages
    fmt.Println(msg)
}


// from https://gobyexample.com/channel-buffering
func bufferedChannels() {
	messages := make(chan string, 2)

    messages <- "buffered"
    messages <- "channel"

    fmt.Println(<-messages)
    fmt.Println(<-messages)
}