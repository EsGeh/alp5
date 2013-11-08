#!/bin/bash

var=$(cat <&0)
echo "$var |" | ./sort.sh 0
