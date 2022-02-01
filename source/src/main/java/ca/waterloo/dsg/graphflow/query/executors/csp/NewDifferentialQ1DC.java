package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.Graph.Direction;
import ca.waterloo.dsg.graphflow.graph.SortedAdjacencyList;
import ca.waterloo.dsg.graphflow.graph.TypeAndPropertyKeyStore;

/**
 * An implementation of Unidirectional unweighted differential BFS. The class takes
 * as input a direction in which to do the BFS.
 */
public class NewDifferentialQ1DC extends NewUnidirectionalDifferentialBFSDC {

    public NewDifferentialQ1DC(int queryId, int source, Direction direction) {
        super(queryId, source, -1, direction);
    }

    @Override
    boolean shouldStopEarly(short iteration) {
        return false;
    }

    @Override
    void getNeighborsData(DistancesDC distancesJ, int currentVertexId, short iteration, long currentDistance, short diff) {
        // TODO(sid)
        var edgeFilterId = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShort("knows");
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

    @Override
    protected boolean shouldSkipEdge(short iteration, short eType) {
        return eType != TypeAndPropertyKeyStore.getInstance().mapStringTypeToShort("knows");
    }

    public long getSrcDstDistance() {
        return distancesR.getVerticesWithDiff().size();
    }
}
