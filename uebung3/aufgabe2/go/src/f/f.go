package f

import "math"
import "fmt"


type Stop struct {}

var MaxCountDigits int = 2

/* this version of "f" internally uses concurrency (namely "f"), but hides it from the caller */
func FSync( digit uint, smaller []uint, greater []uint) (smaller_ , greater_ []uint) {

	smallerIn, greaterIn := make(chan uint), make(chan uint)
	smallerOut, greaterOut := make(chan uint), make(chan uint)
	stopF := make(chan Stop)

	go F(
		digit,
		smallerIn, greaterIn,
		smallerOut, greaterOut,
		stopF )
	// feed F:
	go func() {
		//fmt.Println("starting to feed:")
		for i:=0; i<len(smaller); i++ {
			//fmt.Println("sending", smaller[i])
			smallerIn <- smaller[i]
		}
		for i:=0; i<len(greater); i++ {
			//fmt.Println("sending", greater[i])
			greaterIn <- greater[i]
		}
	} ()
	// eat from F:
	for i:=0; i<len(smaller)+len(greater); i++ {
		select {
			case e:= <- smallerOut:
				smaller_ = append(smaller_, e)
			case e:= <- greaterOut:
				greater_ = append(greater_, e)
		}
	}
	
	//<- eatAll
	stopF <- Stop{}
	return
}

/* this implementation is to be used concurrently, just communicating via channels */
func F(
	digit uint,
	smallerIn chan uint, greaterIn chan uint,
	smallerOut chan uint, greaterOut chan uint,
	stop chan Stop ) {
	stopSmaller, stopGreater := make(chan Stop), make(chan Stop)
	go sort(
		digit,
		smallerIn,
		smallerOut, greaterOut,
		stopSmaller )
	go sort(
		digit,
		greaterIn,
		smallerOut, greaterOut,
		stopGreater )
	<- stop
	stopSmaller <- Stop{}
	stopGreater <- Stop{}
}

func sort(
	digit uint,
	in chan uint,
	smallerOut, greaterOut chan uint,
	stop chan Stop ) {
	for {
		select {
			case elem:= <- in:
				fmt.Println("sorting with ", digit, elem)
				bit := (elem / uint(math.Pow(2,float64(digit)))) % 2
				if bit == 0 {
					smallerOut <- elem
				} else {
					greaterOut <- elem
				}
			case <- stop:
				break
		}
	}
}

// this was the first try:
// no concurrency:
/*func FSync( digit uint, smaller []uint, greater []uint) (smaller_ , greater_ []uint) {

	smaller_s, smaller_g := sort( digit, smaller )
	greater_s, greater_g := sort( digit, greater )

	smaller_ = append(smaller_s, greater_s...)
	greater_ = append(smaller_g, greater_g...)

	return
}*/

/*func sort(digit uint, list []uint) (smaller,greater []uint) {
	for _, elem := range list {
		bit := (elem / uint(math.Pow(2,float64(digit)))) % 2
		if bit == 0 {
			smaller = append(smaller, elem)
		} else {
			greater = append(greater, elem)
		}
	}
	return 
}*/
