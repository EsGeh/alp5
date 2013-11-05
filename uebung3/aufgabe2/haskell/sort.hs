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


-- least to most significant bit
sortLSD list = let (s,g) = sortLSD' 0 ([],list) in
	s ++ g
sortLSD' 0 (s,g) = sortLSD' 1 $ f 0 (s,g)
sortLSD' n (s,g) = case g of
	[] -> (s,g)
	_ -> sortLSD' (n+1) $ f n (s,g)

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
