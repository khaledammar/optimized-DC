package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.util.Report;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

import java.util.*;

import static ca.waterloo.dsg.graphflow.query.executors.csp.Distances.emptyList;

/**
 * Instead of representing a diff for each query, we want to share the overhead among queries.
 * In this class, the map key is the iteration, but the value is
 * This is a class that represent a data structure to store diffs permanently as we process
 * multiple batches. We do not want to interact with the map directly because we want to encapsulate the details of
 * how the data structure of diffs are implemented inside this class.
 */
public class MultiQueryDiff {

    // (iter:short, distance:long) stored as 5 consecutive shorts in short[] array. Index 0 is always the count.
    ArrayList<Map<Integer, short[]>> sharedDiffs;

    /**
     * Default constructor
     */
    public MultiQueryDiff() {
        sharedDiffs = new ArrayList<>();
    }

    /**
     * @param q = number of queries
     */
    public MultiQueryDiff(int q) {
        sharedDiffs = new ArrayList<>(q);
        for (int i = 0; i < q; i++) {
            sharedDiffs.add(i, new HashMap<>());
        }
    }

    /**
     * A function that returns a set of all vertices with one or more diffs
     *
     * @return
     */
    public Set<Integer> getVerticesWithDiff(int q) {
        return sharedDiffs.get(q).keySet();
    }

    public void mergeDeltaDiffs(int q, Map<Integer, short[]> deltaDiffs, IntOpenHashSet deleted) {
        // merge two lists
        var x = sharedDiffs.get(q);
        for (var entry : deltaDiffs.entrySet()) {
            var vt = entry.getKey();
            var v = entry.getValue();
            if (v[0] == 0) {
                x.remove(vt);
            } else {
                x.put(vt, v);
            }
        }
        for (int v : deleted.toIntArray()) {
            x.remove(v);
        }
    }

    public void clear(int q) {
        sharedDiffs.set(q, new HashMap<>());
    }

    public int size(int q) {
        var x = sharedDiffs.get(q);
        return x.size();
    }

    public Map<Integer, short[]> getDiffs(int q) {
        return sharedDiffs.get(q);
    }

    public short[] getDiffs(int q, int vertex) {
        var x = sharedDiffs.get(q);
        var result = x.get(vertex);
        if (result == null) {
            emptyList[0] = 0;
            result = emptyList;
        }
        return result;
    }

    public void setCopiedDiffs(int q, Map<Integer, short[]> diffs) {
        sharedDiffs.set(q, new HashMap<>(diffs));
    }

    public void setExactDiffs(int q, Map<Integer, short[]> diffs) {
        sharedDiffs.set(q, diffs);
    }

    public boolean containsVertex(int q, int vertex) {
        return sharedDiffs.get(q).containsKey(vertex);
    }

    public void print() {
        for (int q = 0; q < sharedDiffs.size(); q++) {
            var x = sharedDiffs.get(q);
            for (var entry : x.entrySet()) {
                //Report.INSTANCE.debug(q + ": " + entry.getKey() + " --> [" + Distances.distancesString(entry.getValue()) + "]");
            }
        }
    }
}
