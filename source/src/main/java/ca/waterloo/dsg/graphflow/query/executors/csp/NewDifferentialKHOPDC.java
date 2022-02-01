package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.Graph.Direction;
import ca.waterloo.dsg.graphflow.graph.SortedAdjacencyList;

/**
 * An implementation of Unidirectional unweighted differential BFS. The class takes
 * as input a direction in which to do the BFS.
 */
public class NewDifferentialKHOPDC extends NewUnidirectionalDifferentialBFSDC {
    short maxHop;

    public NewDifferentialKHOPDC(int queryId, int source, int maxHop, Direction direction) {
        super(queryId, source, -1, direction);
        this.maxHop = (short) maxHop;
    }

    @Override
    boolean shouldStopEarly(short iteration) {
        return iteration >= maxHop;
    }

    @Override
    void getNeighborsData(DistancesDC distancesJ, int currentVertexId, short iteration, long currentDistance, short diff) {
        // TODO(sid)
        SortedAdjacencyList adjList = Graph.INSTANCE.getForwardMergedAdjacencyList(currentVertexId);
        if (SortedAdjacencyList.isNullOrEmpty(adjList)) {
            return;
        }

        for (int j = 0; j < adjList.getSize(); j++) {
            // If the vertex is not the destination, has no outgoing neighbors that may help this query/direction, then no need to it to frontier!
            int neighbourVertexId = adjList.getNeighbourId(j);
            if (neighbourVertexId != source && currentVertexId != neighbourVertexId) {
                var newDist = currentDistance + 1;
                distancesJ.addTmpDiff(neighbourVertexId, newDist, diff);
            }
        }
    }

    @Override
    protected boolean shouldSkipEdge(short iteration, short eType) {
        return false;
    }

    public long getSrcDstDistance() {
        return distancesR.getVerticesWithDiff().size();
    }
}
