#!/bin/bash

# Finish data logging/collection in all machines.
if [ $# -ne 1 ]; then
echo "usage: $0 log-name-prefix"
exit -1
fi


logname=$1


nbtfile=${logname}_nbt.txt   # network bytes total
cttfile=${logname}_ctt.txt   # CPU time total


kill $(pgrep  top) &
cat /proc/net/dev >> ${nbtfile} &  cat /proc/stat | grep '^cpu ' >> ${cttfile} & kill $(pgrep sar)



