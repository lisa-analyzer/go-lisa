package main

import "fmt"

// Employee is an interface for printing employee details
type Employee interface {
	GetFullName(name string) string
	GetSalary(basic int, tax int) int
}

// Emp user-defined type
type Emp struct {
	id int
}

// PrintName method to print employee name
func (e Emp) GetFullName(name string) string {
	return name;
}

// PrintSalary method to calculate employee salary
func (e Emp) GetSalary(basic int, tax int) int {
	var salary = (basic * tax) / 100
	return basic - salary
}

func main() {
	var e1 Employee = Emp{1}
	r1 := e1.GetFullName("Vincenzo Arceri")
	r2 := e1.GetSalary(25000, 5)
}