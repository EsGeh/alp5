#!/bin/bash

export GOPATH=$(pwd)

cd "$GOPATH/src"
cd process
go install

#cd "$GOPATH/src"
#cd sortGo
#go install

#cd "$GOPATH/src"
#cd f
#go install
