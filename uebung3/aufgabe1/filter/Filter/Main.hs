module Check.Main where


import Common
import System.IO
import System.Environment
import Text.Regex
import Control.Monad

import Data.List
--import Data.List.Split


main = do
	hSetBuffering stdout LineBuffering
	programParams <- (getArgs >>= (return . calcProgramParams))

	-- copy remote files onto this machine:
	fetchFiles $! fetchParams $! programParams
	-- # TODO: add error handling

	putStrLn "processing text..."
	text <- textFromFile localTextFile
	dict <- dictFromFile localDictFile
	
	let unsortedtuples = wordmapper [] text
	putStrLn unsortedtuples

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


textFromFile textFile = readFile textFile
dictFromFile dictFile = readFile dictFile

--
calcProgramParams args = case args of
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

data ProgramParams = ProgramParams {
	fetchParams :: FetchParams,
	checkParams :: CheckParams
}

type CheckParams = (Int,Int)

partoftuple :: Eq b => b -> (a,b) -> Bool
partoftuple word tuple
  |snd(tuple) == word = True
  |otherwise          = False

whereisstring word list = case findIndex (partoftuple word) list of
  Just val -> val
  Nothing  -> -1

useword :: (Eq a) =>[(Int,a)] -> a -> [(Int,a)]
useword list word
  |whereisstring word list == -1 = [(1,word)] ++ list
  |otherwise                     = updatelist (whereisstring word list) (modtuple (whereisstring word list) list) list

updatelist:: Int -> a -> [a] -> [a]
updatelist index new list =
  take index list ++ [new] ++ drop(index+1) list

modtuple :: Int -> [(Int,a)] -> (Int,a)
modtuple index list = case drop index list of
  (count,word) : rest -> (count + 1, word)
  [] -> error "list empty!"
 
wordmapper tuplelist wordlist = foldl useword tuplelist wordlist
