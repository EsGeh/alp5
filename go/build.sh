#!/bin/bash

export GOPATH=$(pwd)

go clean

cd "$GOPATH/src"
cd process
go install

cd "$GOPATH/src"
cd test
go install

#cd "$GOPATH/src"
#cd f
#go install
