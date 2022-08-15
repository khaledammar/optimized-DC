package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.Graph.Direction;
import ca.waterloo.dsg.graphflow.graph.SortedAdjacencyList;
import ca.waterloo.dsg.graphflow.graph.TypeAndPropertyKeyStore;

/**
 * An implementation of RPQ Q7 using DC instead of CDD
 *
 * Q7: A.B.C*  ==> likes . hasCreator . knows*
 *
 */
public class NewDifferentialQ7DC extends NewUnidirectionalDifferentialBFSDC {

    public NewDifferentialQ7DC(int queryId, int source, Direction direction) {
        super(queryId, source, -1, direction);
    }

    @Override
    protected void initFrontierAndSourceDistance(int queryId) {
        distancesJ = new DistancesDC(queryId, source, destination, direction, NewUnidirectionalDifferentialBFS.Queries.Q7, "Join");
        distancesR = new DistancesDC(queryId, source, destination, direction, NewUnidirectionalDifferentialBFS.Queries.Q7,"Reduce");
    }


    @Override
    boolean shouldStopEarly(short iteration) {
        return false;
    }

    short getEdgeType(short iteration){
        short edgeType;
        if (iteration == 1)
            edgeType = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShort("likes");
        else if (iteration == 2)
            edgeType = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShort("hasCreator");
        else
            edgeType = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShort("knows");

        return edgeType;
    }
    protected boolean shouldSkipEdge(short iteration, short eType) {
        var edgeType = getEdgeType(iteration);
        return eType != edgeType;
    }

    @Override
    void getNeighborsData(DistancesDC distancesJ, int currentVertexId, short iteration, long currentDistance,
                          short diff, boolean shouldRetract) {
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
