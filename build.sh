#!/bin/bash

echo DC_HOME=`pwd`
echo "export DC_HOME=`pwd`" > scripts/config.sh
source scripts/config.sh
echo "DC home directory is " $DC_HOME

cd $DC_HOME/source
./gradlew build installDist -x test 
cd $DC_HOME/

# Create logs and dataset directories
mkdir $DC_HOME/logs

# Copy benchmarking script
cp scripts/* $DC_HOME/source/build/install/graphflow/bin/


echo "EXEC_DIR='$DC_HOME/source/build/install/graphflow/bin/'" > config.py
python3 experiment-template.py
