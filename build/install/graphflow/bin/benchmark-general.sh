#!/bin/bash


# Check for number of arguments
if [ $# -ne 20 ]; then
  echo "usage: $0 graph_name workload_name execution_mode number_queries connected_probability batch_size number_updates delete_probability number_runs sign range log_prefix drop_probdrop_type bloom_type java_max_memory bloomSize dropMinimum dropMaximum landmark"
  echo "# weighted graph file is <graph_name>_weighted_<sign>_<range>"
  exit -1
fi

#Log directory:
global_log=$DC_HOME/logs/
echo "$global_log"

# Datasets
datasetDir=$DC_HOME/dataset/

graph_name=$1


#Workload
workload_name=$2


#Execution Mode
exec_mode=$3
number_queries=$4
connected_probability=$5
batch_size=$6

#typical number of updates is 100
number_updates=$7
delete_probability=$8
number_runs=$9
sign=${10}
range=${11}

log_prefix=${12}

drop_prob=${13}

drop_type=${14}
bloom_type=${15}

java_max_mem=${16}

bloomSize=${17}

dropMinimum=${18}
dropMaximum=${19}

landmark=${20}

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


case "${workload_name}" in 

"SPSP+W") 
  case "${exec_mode}" in
    "BaseLine+BF") execType="w-baseline" ;;
    "Diff") execType="SPSP_W_DC" ;;
    "Diff+JOD") execType="SPSP_W_DC_JOD" ;;
    "Diff+JOT") execType="uni-w-diff-bfs" ;;
    "landmark+CDD") execType="Landmark_W_DIFF" ;;
    "landmark+BaseLine") execType="Landmark_W_SPSP" ;;
    "Diff+JOT+DROP") execType="uni-w-diff-bfs-bloom" ;;
    "Diff+JOT+DROP+HASH") execType="uni-w-diff-bfs-hash" ;;
    "Diff+JOT+UNREACHABLE") execType="uni-w-diff-bfs-unreachable" ;;
    "Dijkstra") execType="w-dijkstra" ;;
    "BiDijkstra") execType="bi-w-dijkstra" ;;
    "Diff+POS") execType="uni-posw-diff-bfs" ;;
    "FAKE") execType="uni-w-diff-bfs-fake" ;;
    *) echo "Algorithm not found for $m and $w"
        continue
       ;;
  esac;;

"Q1")
   connected_query_file=${graph_name}_Queries.txt.${workload_name}
   case "${exec_mode}" in
    "BaseLine+BF") execType="${workload_name}-baseline" ;;
    "Diff") execType="${workload_name}_DC" ;;
    "Diff+JOT") execType="${workload_name}-diff" ;;
    "Diff+JOT+DROP") execType="${workload_name}-drop" ;;
    "Diff+JOT+DROP+HASH") execType="${workload_name}-drop-hash" ;;
    *) echo "Algorithm not found for $m and $w"
        continue
       ;;
   esac;;


"Q2")
   connected_query_file=${graph_name}_Queries.txt.${workload_name}
   case "${exec_mode}" in
    "BaseLine+BF") execType="${workload_name}-baseline" ;;
    "Diff") execType="${workload_name}_DC" ;;
    "Diff+JOT") execType="${workload_name}-diff" ;;
    "Diff+JOT+DROP") execType="${workload_name}-drop" ;;
    "Diff+JOT+DROP+HASH") execType="${workload_name}-drop-hash" ;;
    *) echo "Algorithm not found for $m and $w"
        continue
       ;;
   esac;;


"Q7")
   connected_query_file=${graph_name}_Queries.txt.${workload_name}
   case "${exec_mode}" in
    "BaseLine+BF") execType="${workload_name}-baseline" ;;
    "Diff") execType="${workload_name}_DC" ;;
    "Diff+JOT") execType="${workload_name}-diff" ;;
    "Diff+JOT+DROP") execType="${workload_name}-drop" ;;
    "Diff+JOT+DROP+HASH") execType="${workload_name}-drop-hash" ;;
    *) echo "Algorithm not found for $m and $w"
        continue
       ;;
   esac;;



"Q11")
   connected_query_file=${graph_name}_Queries.txt.${workload_name}
   case "${exec_mode}" in
    "BaseLine+BF") execType="${workload_name}-baseline" ;;
    "Diff") execType="${workload_name}_DC" ;;
    "Diff+JOT") execType="${workload_name}-diff" ;;
    "Diff+JOT+DROP") execType="${workload_name}-drop" ;;
    "Diff+JOT+DROP+HASH") execType="${workload_name}-drop-hash" ;;
    *) echo "Algorithm not found for $m and $w"
        continue
       ;;
   esac;;



"Khop")
   connected_query_file=${graph_name}_connectedQueries.txt.khop
   case "${exec_mode}" in
    "BaseLine+BF") execType="unw-baseline-khop" ;;
    "Diff") execType="KHOP_DC" ;;
    "Diff+JOT") execType="uni-diff-khop" ;;
    "Diff+JOT+DROP") execType="uni-diff-khop-bloom" ;;
    "Diff+JOT+DROP+HASH") execType="uni-diff-khop-hash" ;;
    *) echo "Algorithm not found for $m and $w"
        continue
       ;;
   esac;;


"PR")
   connected_query_file=${graph_name}_connectedQueries.txt.pr
   case "${exec_mode}" in
    "BaseLine+BF") execType="PR_Baseline" ;;
    "Diff") execType="PR_DC" ;;
    "Diff+JOT") execType="PR_CDD" ;;
    "Diff+JOT+DROP") execType="PR_CDD_PROB" ;;
    "Diff+JOT+DROP+HASH") execType="PR_CDD_DET" ;;
    *) echo "Algorithm not found for $m and $w"
        continue
       ;;
   esac;;


"WCC")
   connected_query_file=${graph_name}_connectedQueries.txt.wcc
   case "${exec_mode}" in
    "BaseLine+BF") execType="WCC_Baseline" ;;
    "Diff") execType="WCC_DC" ;;
    "Diff+JOT") execType="WCC_CDD" ;;
    "Diff+JOT+DROP") execType="WCC_CDD_PROB" ;;
    "Diff+JOT+DROP+HASH") execType="WCC_CDD_DET" ;;
    *) echo "Algorithm not found for $m and $w"
        continue
       ;;
   esac;;




*)
    echo "wrong workload name"
    exit
esac

# number of batches
let nb=$(( ${number_updates}/${batch_size} ))

echo ${load_file}
export JAVA_OPTS="-Xmx${java_max_mem}g"

for ((i = 1; i <= ${number_runs}; i++)); do
  my_log=${global_log}/${graph_name}_${log_prefix}_${workload_name}_${exec_mode}_${number_queries}_${nb}_${batch_size}_${delete_probability}_${connected_probability}_${drop_prob}_${drop_type}_${bloom_type}_${dropMinimum}_${dropMaximum}_${java_max_mem}_${landmark}_"$(date +%Y%m%d-%H%M%S)"

echo `pwd`
  ./bench-init.sh ${my_log}

  echo "experiment-cspq-control -printdistances=false -LandmarkNumber=$landmark  -graphFileToLoad=$load_file -connQueryFile=$connected_query_file  -disconnQueryFile=$disconnected_query_file -addEdgesFile=$add_file -deleteEdgesFile=$graph_delete_file -baseDir=$datasetDir -DropProbability=${drop_prob} -DropType=${drop_type} -BloomType=${bloom_type} -DropMinimum=${dropMinimum} -DropMaximum=${dropMaximum} -addDeleteSeed ${bloomSize}  -numQueries=${number_queries} -batchSize=${batch_size} -numBatches=${nb} -executorType=${execType} -deletionProbability=${delete_probability} -connectedQueryPercentage=${connected_probability} > ${my_log}.log"
  /usr/bin/time -v ./experiment-cspq-control -printdistances=false -LandmarkNumber=$landmark  -graphFileToLoad=$load_file -connQueryFile=$connected_query_file -printdistances="true"   -disconnQueryFile=$disconnected_query_file -addEdgesFile=$add_file -deleteEdgesFile=$graph_delete_file  -baseDir=$datasetDir -DropProbability=${drop_prob}  -DropType=${drop_type} -BloomType=${bloom_type}  -DropMinimum=${dropMinimum} -DropMaximum=${dropMaximum} -addDeleteSeed ${bloomSize}  -numQueries=${number_queries} -batchSize=${batch_size} -numBatches=${nb} -executorType=${execType} -deletionProbability=${delete_probability} -connectedQueryPercentage=${connected_probability} > ${my_log}.log 2>&1 

  ./bench-finish.sh ${my_log}

done

exit
