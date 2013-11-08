package f

import "math"
//import "fmt"


type Stop struct {}

var MaxCountDigits int = 2

/* this version of "f" internally uses concurrency (namely "f"), but hides it from the caller */
func FSync( digit uint, smaller []uint, greater []uint) (smaller_ , greater_ []uint) {

	smallerIn, greaterIn := make(chan uint), make(chan uint)
	smallerOut, greaterOut := make(chan uint), make(chan uint)

	eatAllL, eatAllR := make(chan Stop), make(chan Stop)

	go F(
		digit,
		smallerIn, greaterIn,
		smallerOut, greaterOut )

	// feed F:
	smallerIn <- uint( len(smaller) )
	greaterIn <- uint( len(greater) )
	//fmt.Println("starting to feed:")
	go func() {
		for i:=0; i<len(smaller); i++ {
			//fmt.Println("sending", smaller[i])
			smallerIn <- smaller[i]
		}
	} ()
	go func() {
		for i:=0; i<len(greater); i++ {
			//fmt.Println("sending", greater[i])
			greaterIn <- greater[i]
		}
	} ()
	// eat from F:

	//fmt.Println("starting to eat:")
	go func() {
		count := int( <- smallerOut )

		//fmt.Println("countLeft:", count)
		for i:=0; i<count; i++ {
		//for {
			select {
				case e:= <- smallerOut:
					smaller_ = append(smaller_, e)
			}
		}
		eatAllL <- Stop{}
	} ()
	go func() {
		count := int( <- greaterOut )
		//fmt.Println("countRight:", count)
		for i:=0; i<count; i++ {
		//for {
			select {
				case e:= <- greaterOut:
					greater_ = append(greater_, e)
			}
		}
		eatAllR <- Stop{}
	} ()
	
	<- eatAllL
	<- eatAllR
	return
}

/* this implementation is to be used concurrently, just communicating via channels */
func F(
	//countList int,
	digit uint,
	smallerIn chan uint, greaterIn chan uint,
	smallerOut chan uint, greaterOut chan uint ) {

	stopSmaller, stopGreater := make(chan Stop), make(chan Stop)
	stopEatL, stopEatR := make( chan Stop ), make( chan Stop )

	countSmallerIn, countGreaterIn := <- smallerIn, <- greaterIn
	/*fmt.Println("countSmallerIn",countSmallerIn)
	fmt.Println("countGreaterIn",countGreaterIn)*/

	sortLSmallerOut, sortLGreaterOut := make( chan uint ), make( chan uint )
	sortRSmallerOut, sortRGreaterOut := make( chan uint ), make( chan uint )

	var a, b, c, d []uint //:= make([]uint), make([]uint), make([]uint), make([]uint)

	go sort(
		digit,
		smallerIn,
		sortLSmallerOut, sortLGreaterOut,
		stopSmaller )
	go sort(
		digit,
		greaterIn,
		sortRSmallerOut, sortRGreaterOut,
		stopGreater )

	// eat from sort left:
	go func() {
		//fmt.Println("starting to eat from left:")
		for i:=0; i<int(countSmallerIn); i++{
			//fmt.Println("left!")
			select {
				case e := <- sortLSmallerOut:
					a = append( a, e )
				case e := <- sortLGreaterOut:
					b = append( b, e )
			}
		}
		//fmt.Println("stopping to eat from left:")
		stopEatL <- Stop{}
	} ()
	// eat from sort right:
	go func() {
		//fmt.Println("starting to eat from right:")
		for i:=0; i<int(countGreaterIn); i++{
			//fmt.Println("left!")
			select {
				case e := <- sortRSmallerOut:
					c = append( c, e )
				case e := <- sortRGreaterOut:
					d = append( d, e )
			}
		}
		//fmt.Println("stopping to eat from right:")
		stopEatR <- Stop{}
	} ()

	<- stopEatL
	<- stopEatR
	//fmt.Println("Holla")
	a = append( a, c... )
	smallerOut <- uint( len(a) )
	for i:=0; i<len(a); i++ {
		smallerOut <- a[i]
	}
	b = append( b, d... )
	greaterOut <- uint( len(b) )
	for i:=0; i<len(b); i++ {
		greaterOut <- b[i]
	}
	
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
				//fmt.Println("sorting with ", digit, elem)
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
