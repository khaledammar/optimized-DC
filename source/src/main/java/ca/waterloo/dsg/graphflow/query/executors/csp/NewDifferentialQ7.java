package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph.Direction;
import ca.waterloo.dsg.graphflow.graph.SortedAdjacencyList;
import ca.waterloo.dsg.graphflow.graph.TypeAndPropertyKeyStore;

/**
 * An implementation of RPQ Q7.
 *
 * Q7: A.B.C*
 * Q7: likes . hasCreator . knows
 *
 */
public class NewDifferentialQ7 extends NewUnidirectionalUnweightedDifferentialBFS {
    /**
     * @see {@link NewUnidirectionalDifferentialBFS}.
     */
    static short KNOWS = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShort("knows");
    static short HASCREATOR = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShort("hasCreator");
    static short LIKES = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShort("likes");

    public NewDifferentialQ7(int queryId, int source, Direction direction, boolean backtrack, Queries queryType) {
        super(queryId, source, -1, direction, backtrack, queryType);
    }

    public NewDifferentialQ7(int queryId, int source, Direction direction, boolean backtrack, DropIndex dropIndex,
                             float prob, DistancesWithDropBloom.DropType dropType, String bloomType, int minimumDegree, int maxDegree, Queries queryType) {
        super(queryId, source, -1, direction, backtrack, dropIndex, prob, dropType, bloomType, minimumDegree,
                maxDegree, queryType);
    }


    /**
     *
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
        /* this is a special case when we do not care about the filter.
         * It is used in the NewUnidirectionalDifferentialBFS.FixSelfIfNeeded, where a vertex try to see if any of its in neighbours,
         * could be reachable in future iterations so that it can add itself to be fixed in this iteration. */
        if (iterationNo == -1)
            return true;
        else if(iterationNo == 1 && edgeType == LIKES)
            return true;
        else if (iterationNo == 2 && edgeType == HASCREATOR)
            return true;
        else if (edgeType == KNOWS)
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

        if (iteration == 1)
            edgeType = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShort("likes");
        else if (iteration == 2)
            edgeType = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShort("hasCreator");
        else
            edgeType = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShort("knows");

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
