module Spellcheck.Main where

import Common
import System.Process
import Text.Regex

import Control.Concurrent
import System.IO
import System.Environment


main :: IO ()
main = do
	hSetBuffering stdout LineBuffering
	programParams <- (getArgs >>= (return . calcProgramParams))
	putStrLn $ "program params: " ++ show programParams
	-- copy remote files onto this machine:
	putStrLn $ "fetching \"" ++ show (fetchParams programParams) ++ "\""
	fetchFile (fetchParams $! programParams) localTextFile
	-- # TODO: add error handling

	putStrLn "dividing source file..."
	text <- readFile $ localTextFile
	let hostParams = calcHostParams (hosts programParams) text
	hosts <- spawnHosts $ zip (hosts programParams) hostParams
	putStrLn $ "hosts: " ++ show hostParams
	printOutput hosts

	return ()

printOutput [] = return ()
printOutput ((stdIn,stdOut,_,_):rest) = do
	{-hSetBinaryMode stdIn False 
	hSetBinaryMode stdOut False
	hSetBuffering stdIn LineBuffering 
	hSetBuffering stdOut NoBuffering -}
	hGetContents stdOut >>= putStrLn
	printOutput rest

--spawnHosts :: [(ServerInfo,String)] -> IO [(Handle, Handle, Handle, ProcessHandle)]
spawnHosts hostParams = case hostParams of
	[] -> return []
	((host,params):ps) -> do
		let command = "ssh " ++ params
		putStrLn $ "executing: " ++ command
		ret <- (runInteractiveCommand command)
		rest <- spawnHosts ps
		return $ ret : rest

calcHostParams :: [ServerInfo] -> String -> [String]
calcHostParams hosts text = calcHostParams' $ divText (length hosts) text
	where
		calcHostParams' fromToTuples = zipWith concWithSpace (map showServerInfo hosts) $
			map (\(from,to) -> (show from ++ " " ++ show to)) fromToTuples
		concWithSpace host fromTo = host ++ ":check " ++ "" ++ fromTo

showServerInfo serverInfo = (show $ server serverInfo) ++ case userName serverInfo of
	Nothing -> ""
	Just userName -> "@" ++ userName

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
	(textFile : hosts) -> ProgramParams {
		fetchParams = fetchParams,
		hosts = hosts' }
		where
			fetchParams = fileInfoFromString textFile
			hosts' = map serverInfoFromString hosts
			
	_ -> error "usage: Spellcheck [user@server:]FILE [user@server:]DICT HOST..."

data ProgramParams = ProgramParams {
	fetchParams :: FileInfo,
	hosts :: [ServerInfo]
} deriving( Show )
