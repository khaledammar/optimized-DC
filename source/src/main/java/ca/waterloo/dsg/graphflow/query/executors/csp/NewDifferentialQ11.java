package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph.Direction;
import ca.waterloo.dsg.graphflow.graph.SortedAdjacencyList;
import ca.waterloo.dsg.graphflow.graph.TypeAndPropertyKeyStore;

import java.util.Arrays;

/**
 * An implementation of RPQ Q11.
 *
 * Q11: A1.A2.A3. .... Ak
 * Q11: likes. replyOf. hasCreator.knows. likes
 *
 */
public class NewDifferentialQ11 extends NewUnidirectionalUnweightedDifferentialBFS {
    /**
     * @see {@link NewUnidirectionalDifferentialBFS}.
     */
    public NewDifferentialQ11(int queryId, int source, Direction direction, boolean backtrack, Queries queryType) {
        super(queryId, source, -1, direction, backtrack, queryType);
    }

    public NewDifferentialQ11(int queryId, int source, Direction direction, boolean backtrack, DropIndex dropIndex,
                              float prob, DistancesWithDropBloom.DropType dropType, String bloomType, int minimumDegree, int maxDegree, Queries queryType) {
        super(queryId, source, -1, direction, backtrack, dropIndex, prob, dropType, bloomType, minimumDegree,
                maxDegree, queryType);
    }

    static short KNOWS = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShort("knows");
    static short HASCREATOR = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShort("hasCreator");
    static short LIKES = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShort("likes");
    static short REPLYOF = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShort("replyOf");

    /**
     *
     *
     * @see {@link NewUnidirectionalDifferentialBFS#shouldStopBFSEarly(short)}.
     */
    @Override
    public boolean shouldStopBFSEarly(short currentIterationNo) {
        if (currentIterationNo >= 5)
            return true;
        else
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
        /* this is a special case when we do not care about the filter.
         * It is used in the NewUnidirectionalDifferentialBFS.FixSelfIfNeeded, where a vertex try to see if any of its in neighbours,
         * could be reachable in future iterations so that it can add itself to be fixed in this iteration. */
        if (iterationNo == -1)
            return true;
        else if((iterationNo == 1 || iterationNo == 5) && edgeType == LIKES)
            return true;
        else if (iterationNo == 2 && edgeType == REPLYOF)
            return true;
        else if (iterationNo == 3 && edgeType == HASCREATOR)
            return true;
        else if (iterationNo == 4 && edgeType == KNOWS)
            return true;
        else
            return false;
    }

    @Override
    boolean goodNeighbour(short edgeType, short iterationNo){
        return staticGoodNeighbour(edgeType,iterationNo);
    }

    public static SortedAdjacencyList staticFilterNeighbours(SortedAdjacencyList neighbours, short iteration){

        return neighbours;
        /* this is a special case when we do not care about the filter.
         * It is used in the NewUnidirectionalDifferentialBFS.FixSelfIfNeeded, where a vertex try to see if any of its in neighbours,
         * could be reachable in future iterations so that it can add itself to be fixed in this iteration. */

        /*
        if(iteration == -1)
            return neighbours;

        SortedAdjacencyList filtered = new SortedAdjacencyList(neighbours.getSize(), false);

        short edgeType;

        switch (iteration){
            case 1:
            case 5:
                edgeType = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShort("likes");
                break;
            case 2:
                edgeType = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShort("replyOf");
                break;
            case 3:
                edgeType = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShort("hasCreator");
                break;
                /* This is not the real default.
                * This edge type is suitable for iteration 4 only.
                *
                * For any further iterations after that, the query should stop and ideally we do not want any vertex
                * to return a neighbour. Since we know that our graph (SF) cannot have an edge "knows" for a message
                * vertex in iteration 5 and beyond, this code is a hack to ensure no further out-neighbours will be visited.
                * *

        default:
                edgeType = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShort("knows");
                break;

        }

        for (int i=0;i<neighbours.getSize();i++){
            if (neighbours.getEdgeType(i)==edgeType)
                filtered.add(neighbours.getNeighbourId(i), neighbours.getEdgeType(i),-1);
        }
        return filtered;

         */
    }
    @Override
    public SortedAdjacencyList filterNeighbours(SortedAdjacencyList neighbours, short iteration){
        return staticFilterNeighbours(neighbours,iteration);
    }

    public static SortedAdjacencyList staticGetOutNeighbours(int vertex, boolean merged, Direction direction, short iteration){
        SortedAdjacencyList neighbours = NewUnidirectionalDifferentialBFS.staticGetOutNeighbours(vertex,merged,direction,iteration);
        return staticFilterNeighbours(neighbours, iteration);
    }

    @Override
    public SortedAdjacencyList getOutNeighbours(int vertex, boolean merged, Direction direction, short iteration){
        return staticGetOutNeighbours(vertex,merged,direction,iteration);
    }

    public static SortedAdjacencyList staticGetInNeighbours(int vertex, boolean merged, Direction direction, short iteration){
        SortedAdjacencyList neighbours = NewUnidirectionalDifferentialBFS.staticGetInNeighbours(vertex,merged,direction,iteration);
        return staticFilterNeighbours(neighbours, iteration);
    }

    public SortedAdjacencyList getInNeighbours(int vertex, boolean merged, Direction direction, short iteration){
        return staticGetInNeighbours(vertex,merged,direction,iteration);
    }

}