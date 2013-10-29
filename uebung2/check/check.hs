module Main where

import System.Environment
import System.Cmd
--import Data.List.Split

localTextFile = "/tmp/text"
localDictFile = "/tmp/dict"

main = do
	programParams <- (getArgs >>= (return . calcProgramParams))
	fetchFiles (fetchParams programParams)
	
	text <- textFromFile localTextFile
	dict <- dictFromFile localDictFile
	let range = checkParams programParams

 	return $ check text dict range

check text dict range = undefined
textFromFile textFile = undefined
dictFromFile dictFile = undefined

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
	system $ "scp " ++ scpParam (textFileInfo args) ++ " " ++ localTextFile
	system $ "scp " ++ scpParam (dictFileInfo args) ++ " " ++ localDictFile

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
