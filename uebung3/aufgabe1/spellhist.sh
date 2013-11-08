#!/bin/bash

(./check/dist/build/check/check andreroehrig@wuhan.imp.fu-berlin.de:/home/mi/lohr/lehre/alp5/ws13/text.txt andreroehrig@wuhan.imp.fu-berlin.de:/home/mi/lohr/lehre/alp5/ws13/german.dic 0 $1;./check/dist/build/check/check andreroehrig@wuhan.imp.fu-berlin.de:/home/mi/lohr/lehre/alp5/ws13/text.txt andreroehrig@wuhan.imp.fu-berlin.de:/home/mi/lohr/lehre/alp5/ws13/german.dic $1 $2) | ./dist/build/aufgabe1/aufgabe1
