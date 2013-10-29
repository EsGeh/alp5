module Main where

import System.Environment
import System.Exit
import System.Cmd
import Text.Regex
--import Data.List.Split

-- | destination for the local copies of the text and dictionary
localTextFile = "/tmp/text"
localDictFile = "/tmp/dict"

main = do
	programParams <- (getArgs >>= (return . calcProgramParams))
	-- copy remote files onto this machine:
	fetchFiles (fetchParams programParams)
	
	text <- textFromFile localTextFile
	dict <- dictFromFile localDictFile
	let range = checkParams programParams

 	case check range dict text of
		True -> return $ exitSuccess
		False -> return $ exitFailure

check range dict text = and $ map (findWordInDic (lines dict)) $ splitRegex separators text
	where
		-- this regEx should match one ore more occurences of any character, that is not a letter or a number
		separators = mkRegex "[^[:alnum:]]+"

findWordInDic dict word = elem word dict

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
	system $ "rsync -au " ++ scpParam (textFileInfo args) ++ " " ++ localTextFile
	system $ "rsync -au " ++ scpParam (dictFileInfo args) ++ " " ++ localDictFile

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
