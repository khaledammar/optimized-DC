''' module docstring '''

from __future__ import print_function

from collections import defaultdict
from os import getcwd
from random import shuffle
import argparse
import os
import sys
import random


def prep_data(file_name, percentage):
    ''' this is the docstring '''
    print ("percentage: %d", percentage)
    edges = []
    # getcwd() + '/'
    with open(file_name, 'r+') as original:
        edges = original.read().splitlines()
    chunk = percentage/100
    print (" chunk is : %s ", str(chunk))

    #This shuffle implementation is not efficient.
    #It requires large memory
    #I will comment it, and use the unix command `shuf` instead
    #shuffle(edges)
    print ("number of edges is: %s" % len(edges))
    print ("number of edges in %s/100 of the data is %s" % (percentage, len(edges)*chunk))
    

    input_filename, file_extension = os.path.splitext(file_name) 
    file_one_name = input_filename + '-' + str(int(percentage))+file_extension
    file_two_name = input_filename + '-' + str(int(100-percentage))+file_extension
 
    file_one = open(file_one_name, 'w')
    file_two = open(file_two_name, 'w')
    chunkSize = int(len(edges)*chunk)
    print ("chunkSize: %d", chunkSize)
    for edge_idx in range(chunkSize):
        file_one.write("%s\n" % edges[edge_idx])
    for edge_idx in range(chunkSize, len(edges)):
        file_two.write("%s\n" % edges[edge_idx])
    file_one.close()
    file_two.close()

    return file_one_name
        
def prep_delete(file_name):
    ''' The docstring '''
    edges = []
    # getcwd() + '/'
    with open(file_name, 'r+') as lj_90:
        edges = lj_90.read().splitlines()

    input_filename, file_extension = os.path.splitext(file_name)

    #This is an expensive operation
    #find edges with random ids instead
    #shuffle(edges)

    number_deleted_edges = int(len(edges)*0.1)
    file10del = open(input_filename + '-del'+file_extension, 'w') 

    # create a list of edge ids to be added to the delete file
    deleted_edges_list = random.sample(xrange(len(edges)), number_deleted_edges)

    for edge_idx in deleted_edges_list:
        # This 10% is 9% of the original data
        file10del.write("%s\n" % edges[edge_idx])

def prep_csv():
    ''' this will take in a file and '''
    edges = defaultdict(list)
    highest_vertex_id = 0
    num_lines = 0
    loop_count = 0
    count_mark = 1000000
    with open(getcwd() + "/lj-90.txt", 'r+') as lj_90:
        for line in lj_90:
            edge = line.split('\t')
            num_lines += 1
            from_vertex = int(edge[0])
            to_vertex = int(edge[1])
            edges[from_vertex].append(to_vertex)
            highest_vertex_id = max(highest_vertex_id, from_vertex)
            highest_vertex_id = max(highest_vertex_id, to_vertex)
            loop_count += 1
            if loop_count % count_mark == 0:
                print("%s edges written." % loop_count)
    file90csv = open('lj-90-test', 'w')
    file90csv.write("%s,%s\n" % (highest_vertex_id, num_lines))
    loop_count = 0
    for from_vertex, neighbours in edges.items():
        for to_vertex in neighbours:
            file90csv.write("%s,V,%s,V,E%s\n" % (from_vertex, to_vertex, 1))
            loop_count += 1
            if loop_count % count_mark == 0:
                print("%s edges written." % loop_count)

def split_file(file_name, percentage):
    ''' The Docstring '''
    lines = []
    with open(getcwd() + '/' + file_name, 'r+') as original:
        lines = original.read().splitlines()
    for i in range(0, 100, 10):
        from_idx = int(i/100*len(lines))
        to_idx = int((i+10)/100*len(lines))
        file_90_chunk = open("lj-90-txt-" + str(int(i/10+1)), 'w')
        for line in lines[from_idx:to_idx]:
            file_90_chunk.write("%s\n" % line)
        print("finished chunk " + str(i+1))

def shuffle_file(file_name):
    ''' String '''
    lines = []
    with open(getcwd() + '/' + file_name, 'r+') as to_be_shuffled:
        lines = to_be_shuffled.read().splitlines()
    shuffle(lines)
    new_name = file_name + '-shuffled'
    output_file = open(new_name, 'w')
    for line in lines:
        output_file.write('%s\n' % line)

def add_weight(file_name, is_random):
    ''' the docstring '''
    lines = []
    with open(getcwd() + '/' + file_name, 'r+') as unweighted:
        lines = unweighted.read().splitlines()
    new_name = file_name + '-weighted'
    output_file = open(new_name, 'w')
    for line in lines:
        line += ('\t')
        if not is_random:
            line += ('1')
        output_file.write("%s\n" % line)


if __name__ == "__main__":
    parser = argparse.ArgumentParser( description="Prepare dataset files using edge-list input files" )
    parser.add_argument('-i', '--input', dest='input', default='')
    parser.add_argument('-p', '--percentage',  dest='percentage', default=90.0)

    args = parser.parse_args()

    sourceFile = args.input
    percent = args.percentage

    chunck_data_file = prep_data(sourceFile, percent)
    prep_delete(chunck_data_file)




#prep_data("/Users/semihsalihoglu/Desktop/research/waterloo/graphflow/github/datasets/cit-Patents.txt", 90.0)
#prep_delete("/Users/semihsalihoglu/Desktop/research/waterloo/graphflow/github/datasets/cit-Patents.txt-90.0")
