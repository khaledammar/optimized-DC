package ca.waterloo.dsg.graphflow.query.executors.csp;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PermanentDiff {


    Set<Integer> getVerticesWithDiff();

    void mergeDeltaDiffs(Map<Integer, List<Distances.Diff>> deltaDiffs);

    /**
     * Delete all diffs
     */
    void clear();

    Set<Integer> constructLatestFrontier(int iteration_number);

    void removeVertex(int v);

    List<Distances.Diff> getDiffs(int vertex);

    void addDiffArray(int vertex, List<Distances.Diff> diffArray);

    void addDiffValues(int vertex, short iteration, long distance);

    void addDiffValues(int vertex, short iteration);

    boolean containsVertex(int vertex);

    void addDiff(int vertex, Distances.Diff diff);

    int getNumberVertices();
}

