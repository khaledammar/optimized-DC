package ca.waterloo.dsg.graphflow.query.executors.csp;

/**
 * An special case for Unidirectional weighted BFS implementation where the edges are always
 * assumed to have positive weights
 */
public class NewWeightedDifferentialBFSWithPositiveEdges extends NewUnidirectionalWeightedDifferentialBFS {

    /**
     * @see {@link NewUnidirectionalWeightedDifferentialBFS}.
     */
    public NewWeightedDifferentialBFSWithPositiveEdges(int queryId, int source, int destination, boolean backtrack, Queries queryType) {
        super(queryId, source, destination, backtrack, DropIndex.NO_DROP /*use regular diffs*/, queryType);
    }

    /**
     * In unidirectional weighted BFS, when edge weights are strictly positive, BFS can stop
     * early if the minimum distance in the current frontier to any of the nodes is at least as
     * much as the current known distance to the destination.
     *
     * @see {@link NewUnidirectionalWeightedDifferentialBFS#shouldStopBFSEarly(int)}.
     */
    @Override
    public boolean shouldStopBFSEarly(short currentIterationNo) {

        return distances.minFrontierDistances[currentIterationNo].iterDistPair.distance >=
                distances.getDistance(destination, currentIterationNo);
    }
}
