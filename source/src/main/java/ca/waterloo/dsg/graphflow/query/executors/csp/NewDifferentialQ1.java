package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.Graph.Direction;
import ca.waterloo.dsg.graphflow.graph.SortedAdjacencyList;
import ca.waterloo.dsg.graphflow.graph.TypeAndPropertyKeyStore;
import ca.waterloo.dsg.graphflow.util.IntArrayList;
import ca.waterloo.dsg.graphflow.util.Report;

import java.util.Set;

/**
 * An implementation of Unidirectional unweighted differential BFS. The class takes
 * as input a direction in which to do the BFS.
 */
public class NewDifferentialQ1 extends NewUnidirectionalUnweightedDifferentialBFS {
    /**
     * @see {@link NewUnidirectionalDifferentialBFS}.
     */

    static short KNOWS = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShort("knows");
    public NewDifferentialQ1(int queryId, int source, Direction direction, boolean backtrack, Queries queryType) {
        super(queryId, source, -1, direction, backtrack, queryType);
    }

    public NewDifferentialQ1(int queryId, int source, Direction direction, boolean backtrack, DropIndex dropIndex,
                             float prob, DistancesWithDropBloom.DropType dropType, String bloomType, int minimumDegree, int maxDegree, Queries queryType) {
        super(queryId, source, -1, direction, backtrack, dropIndex, prob, dropType, bloomType, minimumDegree,
                maxDegree, queryType);
    }

    public static SortedAdjacencyList filtered = new SortedAdjacencyList(1000,false);

    /**
     * Q1 should stop as soon as the maximum number of hops is reached.
     *
     * @see {@link NewUnidirectionalDifferentialBFS#shouldStopBFSEarly(short)}.
     */
    @Override
    public boolean shouldStopBFSEarly(short currentIterationNo) {
        return false;
    }

    public long getSrcDstDistance() {
        return distances.getVerticesWithDiff().size();
    }


    /**
     * This function is used to filter neighbours as we traverse them.
     * It is useful for RPQ queries
     *
     * @param edgeType
     * @param iterationNo
     * @return
     */
    static boolean staticGoodNeighbour(short edgeType, short iterationNo){
        if (edgeType == KNOWS)
            return true;
        else
            return false;
    }

    @Override
    boolean goodNeighbour(short edgeType, short iterationNo){
        return staticGoodNeighbour(edgeType,iterationNo);
    }
}
