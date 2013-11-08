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

//var countDigits int = 1 

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
	var smaller, greater []uint // := make( chan uint ), make( chan uint )
	smallerIn, greaterIn := make(chan uint), make(chan uint)
	smallerOut, greaterOut := make(chan uint), make(chan uint)
	stop := make(chan f.Stop)

	go sort_(
		0,
		smallerIn, greaterIn,
		smallerOut, greaterOut )
		//stop )
	greaterIn <- uint( len(list) )
	smallerIn <- 0
	// feed sort_:
	go func() {
		//fmt.Println("starting to feed:")
		for i:=0; i<len(list); i++ {
			fmt.Println("sending", list[i])
			greaterIn <- list[i]
		}
	} ()
	// eat from F:
	for i:=0; i<len(list); i++ {
		select {
			case e:= <- smallerOut:
				fmt.Println("receiving smaller", e)
				smaller = append(smaller, e)
			case e:= <- greaterOut:
				fmt.Println("receiving greater", e)
				greater = append(greater, e)
		}
	}
	
	//<- eatAll
	stop <- f.Stop{}
	list_ = smaller
	list_ = append( list_, greater... )
	return
}

func sort_(
	//count int,
	digit int,
	smallerIn, greaterIn chan uint,
	smallerOut, greaterOut chan uint ) {
	//stop chan f.Stop ) {

	//fmt.Println("sort_",digit)

	if digit > f.MaxCountDigits {
		//input -> output
		countSmaller , countGreater := <-smallerIn, <- greaterIn
		for i:=0; i<int(countSmaller+countGreater); i++{
			select {
				case e := <- smallerIn:
					smallerOut <- e
				case e := <- greaterIn:
					greaterOut <- e
			}
		}
		return
	}

	// digit <= f.MaxCountDigits :
	//stopF := make(chan f.Stop)
	//stopRecSort := make(chan f.Stop)
	fSmallerOut, fGreaterOut := make(chan uint), make(chan uint)
	go f.F(
		uint(digit),
		smallerIn, greaterIn,
		fSmallerOut, fGreaterOut )
	go sort_(
		digit + 1,
		fSmallerOut, fGreaterOut,
		smallerOut, greaterOut)
		//stopRecSort )

	//<- stop
	//stopF <- f.Stop{}
	//stopRecSort <- f.Stop{}
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
