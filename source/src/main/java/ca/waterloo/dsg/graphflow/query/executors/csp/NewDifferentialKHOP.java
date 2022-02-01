package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph.Direction;
import ca.waterloo.dsg.graphflow.util.Report;

/**
 * An implementation of Unidirectional unweighted differential BFS. The class takes
 * as input a direction in which to do the BFS.
 */
public class NewDifferentialKHOP extends NewUnidirectionalUnweightedDifferentialBFS {
    int maxHop;

    /**
     * @see {@link NewUnidirectionalDifferentialBFS}.
     */
    public NewDifferentialKHOP(int queryId, int source, int k, Direction direction, boolean backtrack, Queries queryType) {
        super(queryId, source, -1, direction, backtrack, queryType);

        maxHop = k;
    }

    public NewDifferentialKHOP(int queryId, int source, int k, Direction direction, boolean backtrack,
                               DropIndex dropIndex, float prob, DistancesWithDropBloom.DropType dropType, String bloomType, int minimumDegree,
                               int maxDegree, Queries queryType) {
        super(queryId, source, -1, direction, backtrack, dropIndex, prob, dropType, bloomType, minimumDegree,
                maxDegree, queryType);

        maxHop = k;
    }

    /**
     * KHOP should stop as soon as the maximum number of hops is reached.
     *
     * @see {@link NewUnidirectionalDifferentialBFS#shouldStopBFSEarly(short)}.
     */
    @Override
    public boolean shouldStopBFSEarly(short currentIterationNo) {

        //Report.INSTANCE.debug("------ shouldStopBFSEarly - KHOP");

        return currentIterationNo >= maxHop;
    }

    public long getSrcDstDistance() {
        return distances.getVerticesWithDiff().size();
    }
}
