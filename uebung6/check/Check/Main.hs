{-# LANGUAGE BangPatterns #-}
module Check.Main where

import Common

import System.IO
import System.Environment
import Text.Regex
import Control.Monad


main = do
	hSetBuffering stdout LineBuffering
	! programParams <- (getArgs >>= (return . (id $!) . calcProgramParams))

	--let dummy $! programParams

	-- copy remote files onto this machine:
	--fetchFiles $! fetchParams $! programParams
	-- # TODO: add error handling

	--putStrLn "processing text..."
	text <- getContents
	--text <- textFromFile localTextFile
	dict <- readFile (getDictFileName $ dictFile programParams)
	--let range = checkParams programParams
	let mode' = mode programParams

	let textIsValid = check mode' dict text 
	--let textIsValid = check range dict text 
	--putStrLn $ show $ textIsValid

	case textIsValid of
		CheckResult [] -> return () --putStrLn "ok"
		CheckResult words -> mapM_ print words


data CheckResult = CheckResult {
	getWordList :: [Word]
}
type Word = String

check mode dict text =
	check' mode (splitAtNewLine dict) $ splitRegex separators {-$ cutFromText range-} text
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

check' mode dict [] = CheckResult $ []
check' mode dict (firstWord:restWords) = case mode of
	PrintValid -> case findWordInDic dict firstWord of
		True -> CheckResult $ firstWord : getWordList (check' mode dict restWords)
		False -> check' mode dict restWords
	PrintInvalid -> case findWordInDic dict firstWord of
		True -> check' mode dict restWords
		False -> CheckResult $ firstWord : getWordList (check' mode dict restWords)

findWordInDic dict word = elem word dict {-case elem word dict of
	True -> Nothing
	_ -> Just word-}


--textFromFile textFile = readFile textFile
--dictFromFile dictFile = readFile dictFile

--
calcProgramParams args = case args of
	(dictFile : modeString : []) -> ProgramParams {
		dictFile = dictFile,
		mode = mode' }
		--checkParams = (startPos,endPos - startPos) }
		where
			--(startPos, endPos) = (read from, read to)
			--fetchParams = fileInfoFromString dictFile
			{-fetchParams = FetchParams {
				--textFileInfo = fileInfoFromString textFile,
				dictFileInfo = fileInfoFromString dictFile
			}-}
			mode' = case modeString of
				"+" -> PrintValid
				"-" -> PrintInvalid
				_ -> error $ "invalid mode \"" ++ modeString ++ "\""
			
	_ -> error "usage: check2 DICT (+|-)"
	--_ -> error "usage: check2 [user@server:]DICT (+|-)"

data ProgramParams = ProgramParams {
	dictFile :: Filename,
	--checkParams :: CheckParams
	mode :: Mode
}

type Filename = String

data Mode = PrintValid | PrintInvalid

--type CheckParams = (Int,Int)
