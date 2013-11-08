import System.Environment

main = do
	args <- getArgs
	let sort = case args of
		"-lsd":_ -> sortLSD
		"-msd":_ -> sortMSD
		_ -> error "usage: sort [-lsd | -msd]"
	input <- getContents
	let listInt = map read $ lines $ input
	mapM_ (putStrLn . show) $ sort listInt


f :: Int -> ([Int],[Int]) -> ([Int],[Int])
f s (in0,in1)= collate (distribute s in0) (distribute s in1)

collate (a,b) (c,d) = (a++c, b++d)

distribute s [] = ([],[])
distribute s (i:is)
	| bit == 0  = (i:a, b)
	| bit == 1  = (a, i:b)
	where
		(a,b) = distribute s is
		bit = (i `div` 2^s)`mod` 2


-- just chaining fs:
sort list = let (s,g) = sort_ (-1) ([],list) in
	s ++ g

sort_ (-1) input = sort_ 0 input
sort_ n input 
	| n < countDigits = sort_ (n+1) $ f n input 
	| otherwise = input

	
-- least to most significant bit
sortLSD list = let (s,g) = sortLSD' 0 ([],list) in
	s ++ g
sortLSD' 0 (s,g) = sortLSD' 1 $ f 0 (s,g)
sortLSD' n (s,g) 
	| n < (countDigits-1) = sortLSD' (n+1) $ f n (s,g)
	| otherwise = (s,g) 

-- most to least significant bit
sortMSD list = sortMSD' (countDigits-1) list
sortMSD' (-1) list = list
sortMSD' n list = let (smaller, bigger) = f n (list,[]) in
	sortMSD' (n-1) smaller ++ sortMSD' (n-1) bigger


countDigits :: Int
countDigits = 16
{-
countDigits = log2 $ (fromIntegral (maxBound :: Int) :: Integer) - (fromIntegral (minBound :: Int) :: Integer)
log2 n = ceiling $ logBase 2 (fromIntegral n)
-}
