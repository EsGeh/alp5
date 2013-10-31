module Common where

import System.Exit
import System.Cmd
import System.Directory(doesFileExist)

-- | destination for the local copies of the text and dictionary
localTextFile = "/tmp/text"
localDictFile = "/tmp/dict"

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

fetchFile fileInfo dest = do
		exitCodeText <- system $ "rsync -au " ++ scpParam fileInfo ++ " " ++ dest
		case exitCodeText of
			ExitSuccess -> return $ Nothing
			ExitFailure i -> return $ Just $ "unable to fetch file \"" ++ show fileInfo ++ "\""

scpParam fileInfo = case serverInfo fileInfo of
	Nothing -> path fileInfo
	Just serverInfo -> case userName serverInfo of
		Nothing -> server serverInfo ++ ":" ++ path fileInfo
		Just userName -> userName ++ "@" ++ server serverInfo ++ ":" ++ path fileInfo

data FetchFilesReturn = FetchFilesReturn {
	fetchTextReturn :: FetchFileReturn,
	fetchDictReturn :: FetchFileReturn
}

data FetchParams = FetchParams {
	textFileInfo :: FileInfo,
	dictFileInfo :: FileInfo
}

type FetchFileReturn = Maybe ErrorMsg
type ErrorMsg = String


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