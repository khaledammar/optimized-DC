#!/bin/bash


# Check for number of arguments
if [ $# -ne 13 ]; then
  echo "usage: $0 graph_name workload_name execution_mode number_queries connected_probability batch_size number_updates delete_probability number_runs sign range log_prefix drop_prob"
  echo "#Expected graph names =(EP LJ PA CA TX)"
  echo "#Expected workloads =(SPSP+UW SPSP+W  SPSP-W+)"
  echo "#Expected execution mode =(BaseLine+BF BaseLine+DJ Diff+JOT  Diff+JOT+ES Diff+JOT+BI)"
  echo "#Expected DeleteProbability =(0 0.5 1)"
  echo "# weighted graph file is <graph_name>_weighted_<sign>_<range>"
  echo "#Example: "
  exit -1
fi

#Log directory:
global_log=/home/kammar/semih-new-project/2019-jan_logs/

# Datasets
datasetDir=/home/kammar/semih-new-project/evaluation/

graph_name=$1

#Data files
# preload unweighted = ${graph_name}_90.shuf
# preload weighted = ${graph_name}_90_weighted_1-10.shuf
#
# Add unweighted = ${graph_name}_10.shuf
# Add weighted = ${graph_name}_10_weighted_1-10.shuf
#
# Del unweighted = ${graph_name}_del.shuf
# Del weighted = ${graph_name}_del_weighted_1-10.shuf
#


#Workload
#Expected workloads =(SPSP+UW SPSP+W  SPSP-W+)
workload_name=$2


#Execution Mode
#ExecMode=(BaseLine+BF BaseLine+DJ Diff+JOT  Diff+JOT+ES Diff+JOT+BI)
exec_mode=$3
number_queries=$4
connected_probability=$5
batch_size=$6

#typical number of updates is 131072
number_updates=$7
delete_probability=$8
number_runs=$9
sign=${10}
range=${11}

log_prefix=${12}

drop_prob=${13}

# Weighted graph files
W_graph_load_file=${graph_name}_90_weighted_${sign}-${range}.shuf
W_graph_add_file=${graph_name}_10_weighted_${sign}-${range}.shuf
W_graph_delete_file=${graph_name}_del_weighted_${sign}-${range}.shuf

graph_delete_file=${graph_name}_del.shuf


echo ${graph_name}
echo ${sign}
echo ${range}
echo ${graph_name}_90_weighted_${sign}-${range}.shuf
echo $W_graph_load_file


# Query files
connected_query_file=${graph_name}_connectedQueries.txt
disconnected_query_file=${graph_name}_disconnectedQueries.txt


let execType="unw-baseline"

  load_file=${W_graph_load_file}
  add_file=${W_graph_add_file}
  
  case "${exec_mode}" in
    "BaseLine+BF") execType="w-baseline" ;;
    "Diff+JOT") execType="uni-w-diff-bfs" ;;
    "Dijkstra") execType="w-dijkstra" ;;
    "Diff+POS") execType="uni-posw-diff-bfs" ;;
    "FAKE") execType="uni-w-diff-bfs-fake" ;;
    *) echo "Algorithm not found for $m and $w"
        continue
       ;;
  esac

# number of batches
let nb=$(( ${number_updates}/${batch_size} ))

echo ${load_file}

for ((i = 1; i <= ${number_runs}; i++)); do
  my_log=${global_log}/${graph_name}_${log_prefix}_${workload_name}_${exec_mode}_${number_queries}_${nb}_${batch_size}_${delete_probability}_${connected_probability}_${drop_prob}_${i}_"$(date +%Y%m%d-%H%M%S)"

  ./bench-init.sh ${my_log}

  echo "experiment-cspq -graphFileToLoad=$load_file -connQueryFile=$connected_query_file  -disconnQueryFile=$disconnected_query_file -addEdgesFile=$add_file -deleteEdgesFile=$graph_delete_file -baseDir=$datasetDir -DropProbability=${drop_prob}   -numQueries=${number_queries} -batchSize=${batch_size} -numBatches=${nb} -executorType=${execType} -deletionProbability=${delete_probability} -connectedQueryPercentage=${connected_probability} > ${my_log}.log"
  /usr/bin/time -v ./experiment-cspq-invistegate  -graphFileToLoad=$load_file -connQueryFile=$connected_query_file -printdistances="true"   -disconnQueryFile=$disconnected_query_file -addEdgesFile=$add_file -deleteEdgesFile=$graph_delete_file  -baseDir=$datasetDir -DropProbability=${drop_prob}  -numQueries=${number_queries} -batchSize=${batch_size} -numBatches=${nb} -executorType=${execType} -deletionProbability=${delete_probability} -connectedQueryPercentage=${connected_probability} > ${my_log}.log 2>&1 

  ./bench-finish.sh ${my_log}

done

exit
