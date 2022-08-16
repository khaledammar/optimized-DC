#!/bin/bash
# Initiate data logging/collection in all machines.

if [ $# -ne 1 ]; then
echo "usage: $0 log-name-prefix "
exit -1
fi


logname=$1



cpufile=${logname}_cpu.txt   # cpu usage
netfile=${logname}_net.txt   # network usage
memfile=${logname}_mem.txt   # memory usage
nbtfile=${logname}_nbt.txt   # network bytes total
cttfile=${logname}_ctt.txt   # CPU time total
topFile=${logname}_topMemory.txt   # top memory monitoring

sudo swapoff -a & sudo sh -c 'echo 1 >/proc/sys/vm/drop_caches' & sudo sh -c 'echo 2 >/proc/sys/vm/drop_caches'  & sudo sh -c 'echo 3 >/proc/sys/vm/drop_caches' & sar 1 > ${cpufile} & sar -r 1 > ${memfile} & sar -n DEV 1 | stdbuf -o0 grep 'lo\|eth0' > ${netfile} & cat /proc/net/dev > ${nbtfile} & cat /proc/stat | grep '^cpu ' > ${cttfile} & top -b -d 1 | grep java > ${topFile} &



