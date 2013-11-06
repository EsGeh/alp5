#!/bin/bash

export GOPATH=$(pwd)

#cd src

cd "$GOPATH/src"
cd fProg
go install

cd "$GOPATH/src"
cd f
go install
