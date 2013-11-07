import Data.List

partoftuple :: Eq b => b -> (a,b) -> Bool
partoftuple word tuple
  |snd(tuple) == word = True
  |otherwise          = False

whereisstring word list = case findIndex (partoftuple word) list of
  Just val -> val
  Nothing  -> -1

useword :: (Eq a) =>a -> [(Int,a)] -> [(Int,a)]
useword word list
  |whereisstring word list == -1 = [(1,word)] ++ list
  |otherwise                     = list

updatelist:: Int -> a -> [a] -> [a]
updatelist index new list =
  take index list ++ [new] ++ drop(index+1) list

modtuple :: Int -> [(Int,a)] -> (Int,a)
modtuple index list = case drop index list of
  (count,word) : rest -> (count + 1, word)
  [] -> error "list empty!"
