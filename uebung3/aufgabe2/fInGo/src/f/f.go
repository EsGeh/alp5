package f

import "math"

func F( digit uint, smaller []uint, greater []uint) (smaller_ , greater_ []uint) {

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
