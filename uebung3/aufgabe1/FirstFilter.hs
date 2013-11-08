module FirstFilter where

import Data.List
import Data.Char

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
 
wordmapper tuplelist wordlist = foldl useword tuplelist (map (map toLower) wordlist)

sorttuple tuple1 tuple2
  |snd(tuple1) >  snd(tuple2) = GT
  |snd(tuple1) == snd(tuple2) = EQ
  |snd(tuple1) <  snd(tuple2) = LT

sorttuplelist wordlist = sortBy sorttuple (wordmapper [] wordlist)
