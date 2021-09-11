package main

import "fmt"

func m1() {
	goto l1

	l1: 
		x := 1
}

// from https://www.tutorialspoint.com/go/go_goto_statement.htm
func m2() {
   var a int = 10

   LOOP: for a < 20 {
      if a == 15 {
         a = a + 1
         goto LOOP
      }
      fmt.Printf("value of a: %d", a)
      a++     
   }  
}