package main

import "math"
import "log"
import "io"
import "fmt"
import "os"

/*
	usage: f <digit>
	spec: reads 2 lists from stdin using LIST_SYNTAX, and writes 2 Lists to stdout using LIST_SYNTAX.
	syntax:
		LIST_SYNTAX:
			"i1 i2 i3 ... | i4 i5 i6 ..." represents two lists ([i1, i2, i3, ... ], [i4, i5, i6, ...])
*/

func main() {
	// 1. parse command line args:
	if len(os.Args) != 2 {
		log.Fatal("usage: f <digit>")
	}
	var digit uint
	_,err := fmt.Sscan(os.Args[1], &digit)
	if err != nil {
		log.Fatal("usage: f <digit>")
	}

	// 2. read stdin and retrieve the list of numbers to be sorted:
	smaller, greater := getInput()

	// 3. call f:
	smaller_, greater_ := f(digit, smaller, greater)
	output(smaller_ , greater_ )
}


func f( digit uint, smaller []uint, greater []uint) (smaller_ , greater_ []uint) {

	smaller_s, smaller_g := sort( digit, smaller )
	greater_s, greater_g := sort( digit, greater )

	smaller_ = append(smaller_s, greater_s...)
	greater_ = append(smaller_g, greater_g...)

	return
}

func sort(digit uint, list []uint) (smaller,greater []uint) {
	for _, elem := range list {
		bit := (elem / uint(math.Pow(2,float64(digit)))) % 2
		if bit == 0 {
			smaller = append(smaller, elem)
		} else {
			greater = append(greater, elem)
		}
	}
	return 
}

// reads 2 lists of unsigned integers from stdin and returns two slices containing them:
func getInput() (smaller,greater []uint) {
	smaller = make([]uint,0,100)
	greater = make([]uint,0,100)
	var scanningSmaller bool = true
	var currentInt uint
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
	}
	return
}

// prints 2 slices to stdout using the same syntax as getInput can read:
func output(smaller, greater []uint) {
	for _,elem := range smaller {
		fmt.Print(elem, " ")
	}
	fmt.Print("| ")
	for _,elem := range greater {
		fmt.Print(elem, " ")
	}
}
