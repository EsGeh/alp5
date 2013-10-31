module Main where

import System.IO
import System.Environment
import System.Exit
import System.Directory(doesFileExist)
import System.Cmd
import Text.Regex
import Control.Monad
--import Data.List.Split

-- | destination for the local copies of the text and dictionary
localTextFile = "/tmp/text"
localDictFile = "/tmp/dict"

main = do
	hSetBuffering stdout LineBuffering
	programParams <- (getArgs >>= (return . calcProgramParams))
	-- copy remote files onto this machine:
	fetchFiles $! fetchParams $! programParams
	-- # TODO: add error handling

	putStrLn "processing text..."
	text <- textFromFile localTextFile
	dict <- dictFromFile localDictFile
	let range = checkParams programParams

	let textIsValid = check range dict text 
	--putStrLn $ show $ textIsValid

	case textIsValid of
		CheckResult [] -> putStrLn "ok"
		CheckResult words -> mapM_ print words


data CheckResult = CheckResult {
	getWordList :: [Word]
}
type Word = String

check range dict text = --splitAtNewLine dict
	check' (splitAtNewLine dict) $ splitRegex separators text
	where
		-- this regEx should match one ore more occurences of any character, that is not a letter or a number
		separators = mkRegex "[^[:alnum:]]+"
		dictSeparator = mkRegex "$"

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
		checkParams = CheckParams { 
			startPos = startPos,
			count = endPos - startPos } }
		where
			(startPos, endPos) = (read from, read to)
			fetchParams = FetchParams {
				textFileInfo = fileInfoFromString textFile,
				dictFileInfo = fileInfoFromString dictFile
			}
			
	_ -> error "usage: check [user@server:]FILE [user@server:]DICT FROM TO"

-- | copy files from the server, and copy it to
--   localTextFile and localDictFile
fetchFiles args = do
	textFileExists <- doesFileExist localTextFile 
	dictFileExists <- doesFileExist localDictFile
	fetchTextReturn <- if textFileExists then return Nothing else do
		putStrLn "fetching text file..." 
		fetchFile (textFileInfo args) localTextFile
	fetchDictReturn <- if dictFileExists then return Nothing else do
		putStrLn "fetching dictionary..." 
		fetchFile (textFileInfo args) localTextFile
	return $ FetchFilesReturn {
		fetchTextReturn = fetchTextReturn,
		fetchDictReturn = fetchDictReturn
	}

	--when dictFileExists $ system $ "rsync -au " ++ scpParam (dictFileInfo args) ++ " " ++ localDictFile
	--return ()

fetchFile fileInfo dest = do
		exitCodeText <- system $ "rsync -au " ++ scpParam fileInfo ++ " " ++ dest
		case exitCodeText of
			ExitSuccess -> return $ Nothing
			ExitFailure i -> return $ Just $ "unable to fetch file \"" ++ show fileInfo ++ "\""

data FetchFilesReturn = FetchFilesReturn {
	fetchTextReturn :: FetchFileReturn,
	fetchDictReturn :: FetchFileReturn
}

type FetchFileReturn = Maybe ErrorMsg
type ErrorMsg = String

scpParam fileInfo = case serverInfo fileInfo of
	Nothing -> path fileInfo
	Just serverInfo -> case userName serverInfo of
		Nothing -> server serverInfo ++ ":" ++ path fileInfo
		Just userName -> userName ++ "@" ++ server serverInfo ++ ":" ++ path fileInfo

data ProgramParams = ProgramParams {
	fetchParams :: FetchParams,
	checkParams :: CheckParams
}

data CheckParams = CheckParams {
	startPos :: Int,
	count :: Int
} deriving( Show )

data FetchParams = FetchParams {
	textFileInfo :: FileInfo,
	dictFileInfo :: FileInfo
}

data FileInfo = FileInfo {
	serverInfo :: Maybe ServerInfo,
	path :: String
} deriving( Show )

data ServerInfo = ServerInfo {
	server :: String,
	userName :: Maybe String
} deriving( Show )

fileInfoFromString string = FileInfo {
		serverInfo = serverInfo,
		path = p }
		where
			p = string
			serverInfo = Nothing
			{-
			user:rest = split (endsWith "@") string
			srvr:f:_ = split (s ":"
			-}
