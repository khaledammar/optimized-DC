package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.Graph.Direction;
import ca.waterloo.dsg.graphflow.graph.SortedAdjacencyList;

/**
 * An implementation of Unidirectional unweighted differential BFS. The class takes
 * as input a direction in which to do the BFS.
 */
public class NewDifferentialSPSPDC extends NewUnidirectionalDifferentialBFSDC {
    public NewDifferentialSPSPDC(int queryId, int source, int destination, Direction direction) {
        super(queryId, source, destination, direction);
    }

    @Override
    boolean shouldStopEarly(short iteration) {
        return false;
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
                long newDist = currentDistance + (long)adjList.getNeighbourWeight(j);
                distancesJ.addTmpDiff(neighbourVertexId, newDist, diff);
            }
        }
    }

    @Override
    protected boolean shouldSkipEdge(short iteration, short eType) {
        return false;
    }
}
