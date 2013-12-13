module Common where

import System.Exit
import System.Cmd
import System.Directory(doesFileExist)

-- | destination for the local copies of the text and dictionary
localDictFolder = "/tmp/"
getDictFileName filename = localDictFolder ++ filename

-- | copy files from the server, and copy it to
--   localTextFile and localDictFile
{-
fetchFiles fileInfo = do
	dictFileExists <- doesFileExist localDictFile
	fetchDictReturn <- if dictFileExists then return Nothing else do
		putStrLn $ "fetching dictionary... " ++ (show $ fileInfo)
		fetchFile fileInfo localDictFile
	return $ fetchDictReturn
-}

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

{-data FetchFilesReturn = FetchFilesReturn {
	fetchTextReturn :: FetchFileReturn,
	fetchDictReturn :: FetchFileReturn
}-}

{-data FetchParams = FetchParams {
	textFileInfo :: FileInfo,
	dictFileInfo :: FileInfo
}-}

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

-- # TODO
fileInfoFromString string = FileInfo {
		serverInfo = serverInfo,
		path = p }
		where
			(serverInfo, p) = case elem ':' string of
				False -> (Nothing, string)
				True -> case span (/=':') string of
					(srvr, _:f) -> 
						(Just $ serverInfoFromString srvr, f)

serverInfoFromString string = case elem '@' string of
	False -> ServerInfo {
		server = string,
		userName = Nothing }
	True -> ServerInfo {
		server = server,
		userName = Just userName }
		where
			(userName, _:server) = span (/='@') string
