module Check.Main where

import Common

import System.IO
import System.Environment
import Text.Regex
import Control.Monad
--import Data.List.Split


main = do
	hSetBuffering stdout LineBuffering
	programParams <- (getArgs >>= (return . calcProgramParams))

	-- copy remote files onto this machine:
	fetchFiles $! fetchParams $! programParams
	-- # TODO: add error handling

	putStrLn "processing text..."
	text <- getContents
	--text <- textFromFile localTextFile
	dict <- readFile localDictFile
	--let range = checkParams programParams

	let textIsValid = check dict text 
	--let textIsValid = check range dict text 
	--putStrLn $ show $ textIsValid

	case textIsValid of
		CheckResult [] -> putStrLn "ok"
		CheckResult words -> mapM_ print words


data CheckResult = CheckResult {
	getWordList :: [Word]
}
type Word = String

check dict text =
	check' (splitAtNewLine dict) $ splitRegex separators {-$ cutFromText range-} text
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


--textFromFile textFile = readFile textFile
--dictFromFile dictFile = readFile dictFile

--
calcProgramParams args = case args of
	(dictFile : modeString : []) -> ProgramParams {
		fetchParams = fetchParams,
		mode = mode' }
		--checkParams = (startPos,endPos - startPos) }
		where
			--(startPos, endPos) = (read from, read to)
			fetchParams = fileInfoFromString dictFile
			{-fetchParams = FetchParams {
				--textFileInfo = fileInfoFromString textFile,
				dictFileInfo = fileInfoFromString dictFile
			}-}
			mode' = case modeString of
				"+" -> PrintValid
				"-" -> PrintInvalid
				_ -> error $ "invalid mode \"" ++ modeString ++ "\""
			
	_ -> error "usage: check2 [user@server:]DICT (+|-)"

data ProgramParams = ProgramParams {
	fetchParams :: FileInfo,
	--checkParams :: CheckParams
	mode :: Mode
}

data Mode = PrintValid | PrintInvalid

--type CheckParams = (Int,Int)
