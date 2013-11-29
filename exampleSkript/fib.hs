

fib n = case n of
	0 -> 1
	1 -> 1
	otherwise -> fib (n-1) + fib (n-2)
