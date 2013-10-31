module Spellcheck.Main where

import Common
import System.Process
import Text.Regex

import System.IO
import System.Environment


main :: IO ()
main = do
	hSetBuffering stdout LineBuffering
	programParams <- (getArgs >>= (return . calcProgramParams))
	-- copy remote files onto this machine:
	putStrLn $ "fetching \"" ++ show (fetchParams programParams) ++ "\""
	fetchFile (fetchParams $! programParams) localTextFile
	-- # TODO: add error handling

	putStrLn "dividing source file..."
	text <- readFile $ localTextFile
	return $ calcHostParams (hosts programParams) text

	return ()

calcHostParams hosts text = divText (length hosts) text

divText count text = rangesFromIndices $ filter (/=0) $ calcTextPiecesLength count text

rangesFromIndices [] = []
rangesFromIndices [l] = [(0, l-1)]
rangesFromIndices (l:ls) = (0, l-1) : (map (addHeadLength l) $ rangesFromIndices ls)
	where
		addHeadLength l (start,end) = (start+l,end+l)

calcTextPiecesLength :: Int -> String -> [Int]
calcTextPiecesLength count text = case count of
	0 -> error "no hosts!"
	1 -> [length text]
	_ -> textHeadLength : calcTextPiecesLength (count-1) restText
		where
			textHeadLength = roughTextHeadLength + shift
			shift = length appendToHead
			(appendToHead, restText) = case (matchRegexAll (mkRegex "[[:alnum:]]*") restText') of
				Nothing -> ("",restText')
				Just bla -> (snd' bla, thd' bla)
			restText' = drop roughTextHeadLength text
			roughTextHeadLength = length text `div` count
snd' (_,x,_,_) = x
thd' (_,_,x,_) = x

calcProgramParams args = case args of
	(textFile : dictFile : hosts) -> ProgramParams {
		fetchParams = fetchParams,
		hosts = hosts }
		where
			fetchParams = fileInfoFromString textFile
			
	_ -> error "usage: Spellcheck [user@server:]FILE [user@server:]DICT HOST..."


data ProgramParams = ProgramParams {
	fetchParams :: FileInfo,
	hosts :: [String]
}
