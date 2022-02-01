package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.Graph.Direction;
import ca.waterloo.dsg.graphflow.graph.SortedAdjacencyList;
import ca.waterloo.dsg.graphflow.graph.TypeAndPropertyKeyStore;

/**
 * An implementation of RPQ Q11 using DC instead of CDD
 *
 * Q11: A1.A2.A3. .... Ak
 * Q11: likes. replyOf. hasCreator.knows. likes
 *
 */
public class NewDifferentialQ11DC extends NewUnidirectionalDifferentialBFSDC {
    short maxHop;

    public NewDifferentialQ11DC(int queryId, int source, Direction direction) {
        super(queryId, source, -1, direction);
        this.maxHop = 5;
    }

    @Override
    boolean shouldStopEarly(short iteration) {
        return iteration >= maxHop;
    }

    short getEdgeType(short iteration){
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
             * vertex in iteration 5 and beyond, this code is a hack to ensure no further out-neighbours will be visited. */
            default:
                edgeType = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShort("knows");
                break;
        }

        return edgeType;
    }
    protected boolean shouldSkipEdge(short iteration, short eType) {
        var edgeType = getEdgeType(iteration);
        return eType != edgeType;
    }

    @Override
    void getNeighborsData(DistancesDC distancesJ, int currentVertexId, short iteration, long currentDistance, short diff) {
        // TODO(sid)

        var edgeFilterId = getEdgeType(iteration);

        SortedAdjacencyList adjList = Graph.INSTANCE.getForwardMergedAdjacencyList(currentVertexId);
        if (SortedAdjacencyList.isNullOrEmpty(adjList)) {
            return;
        }

        for (int j = 0; j < adjList.getSize(); j++) {
            if (adjList.getEdgeType(j) != edgeFilterId) {
                continue;
            }
            int neighbourVertexId = adjList.getNeighbourId(j);
            if (neighbourVertexId != source && currentVertexId != neighbourVertexId) {
                var newDist = currentDistance + 1;
                distancesJ.addTmpDiff(neighbourVertexId, newDist, diff);
            }
        }
    }

    public long getSrcDstDistance() {
        return distancesR.getVerticesWithDiff().size();
    }
}
