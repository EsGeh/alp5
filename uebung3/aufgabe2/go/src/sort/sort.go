package main

import "f"

import "log"
//import "io"
import "fmt"
import "os"

/*
	usage: f <digit>
	spec: reads 2 lists from stdin using LIST_SYNTAX, and writes 2 Lists to stdout using LIST_SYNTAX.
	syntax:
		LIST_SYNTAX:
			"i1 i2 i3 ... | i4 i5 i6 ..." represents two lists ([i1, i2, i3, ... ], [i4, i5, i6, ...])
*/

var countDigits uint = 32

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
	in, out := make( chan uint), make( chan uint)
	go sort_(
		int(countDigits),
		in, out,
		stop)
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

	// stop recursion:
	if digit == -1 {
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

	waitForAllOutput := make( chan f.Stop )
	
	// if digit >= 0
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

// reads 2 lists of unsigned integers from stdin and returns two slices containing them:
func getInput() (list []uint) {
	list = make([]uint,0,100)
	/*var currentInt uint
	var currentString string
	for {
		_ , err := fmt.Scanf("%s", &currentString)
		if err != nil {
			if err == io.EOF {
				break;
			} else {
				//fmt.Println("ERROR while parsing input: ", err)
				log.Fatal("ERROR while parsing input: ", err)
			}
		}
		_ , err = fmt.Sscan(currentString, &currentInt)
		if err != nil {
			if scanningSmaller {
				_,err = fmt.Sscanf(currentString, "|")
				if err != nil {
					log.Fatal("ERROR2 while parsing input: ", err)
				} else {
					scanningSmaller = false
					err = nil
				}
			}

			if err != nil {
				log.Fatal("ERROR while parsing input: ", err)
			}
		} else {
			if scanningSmaller {
				smaller = append(smaller,currentInt)
			} else {
				greater = append(greater,currentInt)
			}
		}
	}*/
	return
}

// prints 2 slices to stdout using the same syntax as getInput can read:
func output(list []uint) {
	for _,elem := range list {
		fmt.Print(elem, " ")
	}
}
