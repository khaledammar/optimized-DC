package ca.waterloo.dsg.graphflow.query.executors.csp;

import java.util.*;

/**
 * This is a class that represent a data structure to store diffs permanently as we process
 * multiple batches. We do not want to interact with the map directly because we want to encapsulate the details of
 * how the data structure of diffs are implemented inside this class.
 */
public class OneQueryDiff implements PermanentDiff {
    Map<Integer, List<Distances.Diff>> vIterDistPairMap;

    /**
     * Default constructor
     */
    public OneQueryDiff() {
        vIterDistPairMap = new HashMap<>();
    }


    /**
     * A function that returns a set of all vertices with one or more diffs
     *
     * @return
     */
    public Set<Integer> getVerticesWithDiff() {
        return vIterDistPairMap.keySet();
    }

    public void mergeDeltaDiffs(Map<Integer, List<Distances.Diff>> deltaDiffs) {
        // merge two lists
        //vIterDistPairMap.putAll(deltaDiffs);
        /*
        I used this code to understand the memory overhead of this function. Memory allocation comes from the last line
        when we put/allocate new pairs in the hash table

        for(Map.Entry<Integer,List<Distances.Diff>> e:deltaDiffs.entrySet()){
            if(vIterDistPairMap.containsKey(e.getKey()))
                vIterDistPairMap.replace(e.getKey(),e.getValue());
            else
                vIterDistPairMap.put(e.getKey(),e.getValue());
        }
        */
    }

    public void clear() {
        vIterDistPairMap.clear();
    }

    /**
     * This function use vIterDistPairMap to construct the forntier based on the latestIteration
     *
     * @return
     */
    public Set<Integer> constructLatestFrontier(int iteration_number) {

        Set<Integer> frontier = new HashSet<>(100);

        for (Map.Entry<Integer, List<Distances.Diff>> pair : vIterDistPairMap.entrySet()) {
            for (Distances.Diff d : pair.getValue()) {
                if (d.iterationNo == iteration_number) {
                    frontier.add(pair.getKey());
                }
            }
        }
        return frontier;
    }


    public void removeVertex(int v) {
        vIterDistPairMap.remove(v);
    }


    public List<Distances.Diff> getDiffs(int vertex) {
        if (vIterDistPairMap.containsKey(vertex)) {
            return vIterDistPairMap.get(vertex);
        } else {
            return new ArrayList<>();
        }
    }

    public void addDiffArray(int vertex, List<Distances.Diff> diffArray) {
        vIterDistPairMap.put(vertex, diffArray);
    }

    public void addDiffValues(int vertex, short iteration, long distance) {
        addDiff(vertex, new Distances.IterationDistancePair(iteration, distance));
    }

    public void addDiffValues(int vertex, short iteration) {
        addDiff(vertex, new Distances.Diff(iteration));
    }

    public void addDiff(int vertex, Distances.Diff diff) {
        List<Distances.Diff> diffs = vIterDistPairMap.get(vertex);
        diffs.add(diff);
        vIterDistPairMap.put(vertex, diffs);
    }

    public int getNumberVertices() {
        return vIterDistPairMap.size();
    }

    public boolean containsVertex(int vertex) {
        return vIterDistPairMap.containsKey(vertex);
    }
}
