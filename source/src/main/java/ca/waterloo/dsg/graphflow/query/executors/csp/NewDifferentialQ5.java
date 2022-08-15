package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph.Direction;
import ca.waterloo.dsg.graphflow.graph.SortedAdjacencyList;
import ca.waterloo.dsg.graphflow.graph.TypeAndPropertyKeyStore;

/**
 * An implementation of RPQ Q5.
 *
 * Q5: A.B*.C
 * hasCreator . knows* . likes
 *
 */
public class NewDifferentialQ5 extends NewUnidirectionalUnweightedDifferentialBFS {
    /**
     * @see {@link NewUnidirectionalDifferentialBFS}.
     */
    public NewDifferentialQ5(int queryId, int source, Direction direction, boolean backtrack, Queries queryType) {
        super(queryId, source, -1, direction, backtrack, queryType);
    }

    public NewDifferentialQ5(int queryId, int source, Direction direction, boolean backtrack, DropIndex dropIndex,
                             float prob, String dropType, String bloomType, int minimumDegree, int maxDegree, Queries queryType) {
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

    public double getSrcDstDistance() {
        return distances.getVerticesWithDiff().size();
    }


    public static SortedAdjacencyList staticFilterNeighbours(SortedAdjacencyList neighbours, short iteration){

        /* this is a special case when we do not care about the filter.
        * It is used in the NewUnidirectionalDifferentialBFS.FixSelfIfNeeded, where a vertex try to see if any of its in neighbours,
        * could be reachable in future iterations so that it can add itself to be fixed in this iteration. */
        if(iteration == -1)
            return neighbours;

        SortedAdjacencyList filtered = new SortedAdjacencyList(neighbours.getSize(), false);

        short edgeType;

        if (iteration == 1)
            edgeType = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShort("hasCreator");
        else if (iteration >= 2)
            edgeType = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShort("knows");
        else
            edgeType = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShort("knows");

        for (int i=0;i<neighbours.getSize();i++){
            if (neighbours.getEdgeType(i)==edgeType)
                filtered.add(neighbours.getNeighbourId(i), neighbours.getEdgeType(i),-1);
        }
        return filtered;
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
