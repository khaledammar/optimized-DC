package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.util.Report;
import ca.waterloo.dsg.graphflow.util.VisibleForTesting;

import java.util.Map;

/**
 * Interface for classes that implement Differential BFS.
 * Warning: We introduced this interface to make
 * {@link NewBidirUnweightedDifferentialBFS}, which cannot extend
 * {@link NewUnidirectionalDifferentialBFS} because it contains
 * two {@link NewUnidirectionalUnweightedDifferentialBFS}s internally. To make
 * {@link NewBidirUnweightedDifferentialBFS} and other differential BFS
 * implementations work, we make all of these classes implement this interface.
 */
public interface DifferentialBFS {

    /**
     * Executes basic BFS, so not differentially.
     */
    void continueBFS();

    /**
     * Executes BFS differentially, so fixes the BFS.
     */
    void executeDifferentialBFS();

    @VisibleForTesting
    int sizeOfDistances();
    int getMaxIteration();
    int getNumberOfVertices();

    public int getQueryId();

    public void copyDiffs(DifferentialBFS initDiff);

    int getRecalculateNumbers();
    int getSetVertexChangeNumbers();
    Map<Integer,Integer> getRecalculateStats();

    void initRecalculateNumbers();

    int minimumSizeOfDistances();

    @VisibleForTesting
    long getSrcDstDistance();

    void printStats();

    void printDiffs();

    void printDiffs(Report.Level l);

    void preProcessing(int batchNumber);

    void mergeDeltaDiff();
}
