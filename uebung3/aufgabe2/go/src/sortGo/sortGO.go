package main

import "f"

import "log"
import "io"
import "fmt"
import "os"

/*
	usage: sort 
	spec: reads a list from stdin using LIST_SYNTAX, and writes the sorted list to stdout using LIST_SYNTAX.
	syntax:
		LIST_SYNTAX:
			just the elements seperated by spaces or newlines. The input is terminated by EOF.
*/

var countDigits int = 0

func main() {
	// 1. parse command line args:
	if len(os.Args) != 1 {
		log.Fatal("usage: sort")
	}

	// 2. read stdin and retrieve the list of numbers to be sorted:
	list := getInput()

	// 3. call sort
	list_:= sort(list)
	output(list_)
}

func sort(list []uint) (list_ []uint) {
	stopSort := make(chan f.Stop)
	stop := make(chan f.Stop)
	in, out := make( chan uint ), make( chan uint )
	go sort_(
		int(countDigits),
		in, out,
		stopSort )
	// Feed sort_ :
	go func() {
		for i:=0; i<len(list); i++ {
			in <- list[i]
		}
	} ()
	// eat from sort_ :
	go func() {
		for i:=0; i<len(list); i++ {
			e := <- out
			list_ = append(list_, e)
		}
		stop <- f.Stop{}
	} ()
	
	<- stop
	stopSort <- f.Stop{}
	return
}

// uses MSD to sort the list:
func sort_(
	digit int,
	in, out chan uint,
	stop chan f.Stop ) {

	fmt.Println("sort_",digit)

	// stop recursion:
	if digit == -1 {
		fmt.Println("sort_",digit, "just copy")
		for {
			select {
				case e := <- in:
					out <- e
				case <- stop:
					break
			}
		}
		return
	}

	// if digit >= 0
	waitForAllOutput := make( chan f.Stop )
	
	greaterIn := make(chan uint)
	smallerOut, greaterOut := make(chan uint), make(chan uint)
	stopF := make(chan f.Stop)
	// execute F:
	go f.F(
		uint(digit),
		in, greaterIn,
		smallerOut, greaterOut,
		stopF )

	// eat from F:
	stopSmaller, stopGreater := make(chan f.Stop), make(chan f.Stop)
	recSmallerOut, recGreaterOut := make(chan uint), make(chan uint)
	go sort_(
		digit-1,
		smallerOut, recSmallerOut,
		stopSmaller )
	go sort_(
		digit-1,
		greaterOut, recGreaterOut,
		stopGreater )

	//eat the output from the subcalls, and concatenate them to generate the output:
	listSmallerOut, listGreaterOut := make([]uint, 0), make([]uint, 0)
	go func() {
		for {
			select {
				case e := <- recSmallerOut :
					listSmallerOut = append(listSmallerOut, e)
				case e := <- recGreaterOut :
					listGreaterOut = append(listGreaterOut, e)
				case <- stop:
					waitForAllOutput <- f.Stop{}
					break
			}
		}
	} ()
	<- waitForAllOutput
	stopF <- f.Stop{}
	stopSmaller <- f.Stop{}
	stopGreater <- f.Stop{}
	// the result is in listSmallerOut and listGreaterOut now. Concatenating them gives us the return value:
	return
}

// reads a list of unsigned integers from stdin and returns a slice containing them:
func getInput() (list []uint) {
	list = make([]uint,0,100)

	var currentInt uint
	for {
		_ , err := fmt.Scanf("%d", &currentInt)
		if err != nil {
			if err == io.EOF {
				break
			} else {
				log.Fatal("ERROR while parsing input: ", err)
			}
		}
		list = append( list, currentInt )
	}
	return
}

// prints a slice to stdout using the same syntax as getInput can read:
func output(list []uint) {
	for _,elem := range list {
		fmt.Print(elem, " ")
	}
}
