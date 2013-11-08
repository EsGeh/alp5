#!/bin/bash


#echo "$1"

MAX_COUNT_DIGIT=16


if [ "$1" == "$MAX_COUNT_DIGIT" ] ; then
	var=$(cat <&0)
	echo "$var" | ./go/bin/fProg "$1"
else
	var=$(cat <&0)
	echo "$var" | ./go/bin/fProg "$1" | ./sort.sh $(($1 + 1))
fi
