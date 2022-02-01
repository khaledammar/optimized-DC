package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph.Direction;

/**
 * An implementation of Unidirectional unweighted differential BFS. The class takes
 * as input a direction in which to do the BFS.
 */
public class NewDifferentialSPSPDCJOD extends NewUnidirectionalDifferentialBFSDCJOD {

    /**
     * @see {@link NewUnidirectionalDifferentialBFS}.
     */
    public NewDifferentialSPSPDCJOD(int queryId, int source, int destination) {
        super(queryId, source, destination, Direction.FORWARD);
    }


    @Override
    public int getQueryId() {
        return 0;
    }

    @Override
    public void copyDiffs(DifferentialBFS initDiff) {

    }

    @Override
    boolean shouldStopEarly(short iteration) {
        return false;
    }

    @Override
    void getNeighborsData(DistancesDC distancesJ, int vertexId, long currentDistance, short diff) {

    }

    @Override
    protected boolean shouldSkipEdge(int vertex, short eType) {
        return false;
    }


    @Override
    public void updateNbrsDistance(int currentVertexId, long vertexsCurrentDist, int neighbourId,
                                   long currentNbrEdgeWeight, short currentIterationNo) {

        double nbrsCurrentDist = distancesR.getDistance(neighbourId, currentIterationNo, true);

        if (vertexsCurrentDist + currentNbrEdgeWeight < nbrsCurrentDist) {
            distancesR.setVertexDistance(neighbourId, currentIterationNo, vertexsCurrentDist + currentNbrEdgeWeight);
            distancesR.frontier.add(neighbourId);
        }
    }
}
