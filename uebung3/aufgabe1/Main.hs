module Main where


import FirstFilter

import System.IO
import System.Environment
import Text.Regex
import Control.Monad

import Data.List
--import Data.List.Split

textFile = "testtext"

main = do
	hSetBuffering stdout LineBuffering
	--programParams <- (getArgs >>= (return . calcProgramParams))

	-- copy remote files onto this machine:
	--fetchFiles $! fetchParams $! programParams
	-- # TODO: add error handling

	putStrLn "processing text..."
	text <- readFile textFile
	--dict <- dictFromFile localDictFile
	
	let unsortedtuples = sorttuplelist $ lines text
	putStrLn $ unlines (map show unsortedtuples)

data CheckResult = CheckResult {
	getWordList :: [Word]
}
type Word = String

check range dict text =
	check' (splitAtNewLine dict) $ splitRegex separators $ cutFromText range text
	where
		-- this regEx should match one ore more occurences of any character, that is not a letter or a number
		separators = mkRegex "[^[:alnum:]]+"
		dictSeparator = mkRegex "$"

cutFromText (from,to) text = take (to-from) $ drop from text

-- | this is a bit complicated, because a line ending can be one of "\r\n" "\n" or "\r"
splitAtNewLine string = 
	let (firstLine, restText) = break isLineEnding string
	in
		firstLine : case restText of
			'\r':'\n':rest -> splitAtNewLine rest
			'\n':rest -> splitAtNewLine rest
			'\r':rest -> splitAtNewLine rest
			_ -> []
	where
		isLineEnding c = (c =='\r') || (c == '\n')

check' dict [] = CheckResult $ []
check' dict (firstWord:restWords) = case findWordInDic dict firstWord of
	Nothing -> check' dict restWords
	Just word -> CheckResult $ word : (getWordList $ check' dict restWords)

findWordInDic dict word = case elem word dict of
	True -> Nothing
	_ -> Just word

--
{-calcProgramParams args = case args of
	(textFile : dictFile : from : to : []) -> ProgramParams {
		fetchParams = fetchParams,
		checkParams = (startPos,endPos - startPos) }
		where
			(startPos, endPos) = (read from, read to)
			fetchParams = FetchParams {
				textFileInfo = fileInfoFromString textFile,
				dictFileInfo = fileInfoFromString dictFile
			}
			
	_ -> error "usage: check [user@server:]FILE [user@server:]DICT FROM TO"
-}

{-
data ProgramParams = ProgramParams {
	fetchParams :: FetchParams,
	checkParams :: CheckParams
}
-}

type CheckParams = (Int,Int)
