import subprocess
import time
import sys, os
from config import *

workloads=["SPSP+W", "Khop", "WCC"]

# The execution mode names has been kept for legacy reasons.
# Diff = Vanilla Differential Computation (VDC)
# Diff+JOT = Complete Drop (CD)
# Diff+JOT+DROP = Probabilistic Partial Drop (Prob)
# Diff+JOT+DROP+HASH = Deterministic Partial Drop (Det)

execMode=["BaseLine+BF" "Diff+JOT" "Diff+JOT+DROP" "Diff+JOT+DROP+HASH" "Diff"]



UPDATES = 100      # Number of total updates, this is devided by "batchS" to determine the total number of batches
TIMEOUT = 1800000  # Execution timeout

graph="PA" # graph name, look at preparing datasets in ReadMe
memory=30  # Maximum memory allowed in the machine
w="Khop"   # Workload name



print(EXEC_DIR)
os.chdir(EXEC_DIR)


batchS = 1        # batch size
q=10              # number of queries
dp = 0            # drop probability
bmax = 13         # degree maximum drop
bmin = 2          # degree minimum drop
bloomS = 5000000  # bloom size
landmark = 0      # Number of landmarks

for delete in [0,0.25,0.5]:
    for e in ["Diff","Diff+JOT"]:
        log_prefix = "myExperimentName_"
        command = "timeout {} bash benchmark-general.sh {} {} {} {} 1 {} {} {} 1 2 10 {} {} Selective Query {} {} {} {} {}".format(TIMEOUT,graph, w, e,q,batchS, UPDATES,delete,log_prefix,dp, memory, bloomS, bmin, bmax, landmark)
        print(command)
        proc = subprocess.run(command, shell=True)
        time.sleep(10)


exit()


