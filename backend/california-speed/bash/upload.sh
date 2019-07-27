#!/bin/bash

jarPath=$(ls -t ../build/libs/*.jar | head -1)

if [[ "$jarPath" != "" ]]; then
	scp $jarPath web@www.ezekielnewren.com:private/speed.jar
else
	echo "you must run this script in the same directory as this script"
	exit
fi
