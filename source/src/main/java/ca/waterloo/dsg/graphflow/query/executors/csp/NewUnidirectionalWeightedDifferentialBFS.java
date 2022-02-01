package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.Graph.Direction;
import ca.waterloo.dsg.graphflow.graph.SortedAdjacencyList;
import ca.waterloo.dsg.graphflow.util.IntQueue;
import ca.waterloo.dsg.graphflow.util.Report;

import java.util.HashSet;
import java.util.Set;


/**
 * An implementation of Unidirectional weighted differential BFS. The direction of the BFS
 * is always assumed to be forward.
 */
public class NewUnidirectionalWeightedDifferentialBFS extends NewUnidirectionalDifferentialBFS {

    /**
     * @see {@link NewUnidirectionalDifferentialBFS}.
     */
    public NewUnidirectionalWeightedDifferentialBFS(int queryId, int source, int destination, boolean backtrack,
                                                    DropIndex dropIndex, Queries queryType) {

        super(queryId, source, destination, Direction.FORWARD, backtrack, queryType);
    }

    /**
     * @see {@link NewUnidirectionalDifferentialBFS}.
     */
    public NewUnidirectionalWeightedDifferentialBFS(int queryId, int source, int destination, boolean backtrack,
                                                    DropIndex dropIndex, float prob, DistancesWithDropBloom.DropType dropType, String bloomType,
                                                    int minimumDegree, int maxDegree, Queries queryType) {

        super(queryId, source, destination, Direction.FORWARD, backtrack, queryType);

        //Report.INSTANCE.debug("------ Initialize NewUnidirectionalWeightedDifferentialBFS query "+ source + " -> "+realDiffs + " -- "+ prob);

        if (dropIndex == DropIndex.BLOOM) {
            distances = new DistancesWithDropBloom(queryId, source, destination, direction, prob, dropType, bloomType,
                    minimumDegree, maxDegree, queryType);
        } else if (dropIndex == DropIndex.HASH_TABLE) {
            distances = new DistancesWithDropHash(queryId, source, destination, direction, prob, dropType, bloomType,
                    minimumDegree, maxDegree, queryType);
        }
    }


    public void mergeDeltaDiff() {
        distances.mergeDeltaDiffs();
    }

    /**
     * In unidirectional weighted BFS, when edge weights can be anything, BFS should
     * never stop early. It should only stop when the frontier is empty.
     *
     * @see {@link NewUnidirectionalDifferentialBFS#shouldStopBFSEarly(short)}.
     */
    @Override
    public boolean shouldStopBFSEarly(short currentIterationNo) {

        //return ((Distances.IterationDistancePair) distances.minFrontierDistances[currentIterationNo].iterDistPair).distance >=
        //        distances.getDistance(destination, currentIterationNo);
        return false;
    }

    // dummy function required by interface and used by Landmark
    public void preProcessing() {
    }


    @Override
    public void updateNbrsDistance(int currentVertexId, SortedAdjacencyList currentVsAdjList, int neighborIdIndex,
                                   short currentIterationNo) {

        int neighbourId = currentVsAdjList.neighbourIds[neighborIdIndex];
        //TODO: This is a bug that need to be fixed, we should never get a negative id - check the neighbor size before visiting all itemes in neighbors list
        if (neighbourId < 0) {
            return;
        }

        long currentNbrEdgeWeight = (long) currentVsAdjList.weights[neighborIdIndex];
        /**
         * There was a bug here by using the latest Distance instead of the distance from last iteration
         */
        long vertexsCurrentDist = distances.getDistance(currentVertexId, (short) (currentIterationNo - 1), true);
        long nbrsCurrentDist = distances.getDistance(neighbourId, currentIterationNo, true);

        if (Report.INSTANCE.appReportingLevel == Report.Level.ERROR && DistancesWithDropBloom.debug(neighbourId)) {
            System.out.println(
                    "++ dist( " + currentVertexId + " ) = " + vertexsCurrentDist + " + " + currentNbrEdgeWeight);
            System.out.println("++ dist( " + neighbourId + " ) = " + nbrsCurrentDist);
        }
        if (vertexsCurrentDist + currentNbrEdgeWeight < nbrsCurrentDist) {
            distances.setVertexDistance(neighbourId, currentIterationNo, vertexsCurrentDist + currentNbrEdgeWeight);
        }
    }


    @Override
    public void updateNbrsDistance(int currentVertexId, long vertexsCurrentDist, int neighbourId,
                                   long currentNbrEdgeWeight, short currentIterationNo) {

        if (DistancesWithDropBloom.debug(neighbourId) || DistancesWithDropBloom.debug(currentVertexId)) {
            Report.INSTANCE.error("** Weighted updateNbrsDistance - from " + currentVertexId + " with distance " +
                    vertexsCurrentDist + " @ iteration " + currentIterationNo + "  : update " + neighbourId +
                    " neighbor weight = " + currentNbrEdgeWeight);
        }

        /**
         * There was a bug here by using the latest Distance instead of the distance from last iteration
         */
        //double vertexsCurrentDist = distances.getDistance(currentVertexId,currentIterationNo-1, true);
        // I should look at the Nbrs current iteration because I compare it with vertex previous iteration + Weight
        double nbrsCurrentDist = distances.getNewDistance(neighbourId, currentIterationNo, true, currentVertexId);

        if (Report.INSTANCE.appReportingLevel == Report.Level.ERROR && DistancesWithDropBloom.debug(neighbourId)) {
            System.out.println(
                    "Checking if there is a better distance found @ iteration " + currentIterationNo + " for vertex " +
                            neighbourId);
            System.out.println(
                    "++ dist( " + currentVertexId + " ) = " + vertexsCurrentDist + " + " + currentNbrEdgeWeight);
            System.out.println("++ dist( " + neighbourId + " ) = " + nbrsCurrentDist);
        }

        if (vertexsCurrentDist + currentNbrEdgeWeight < nbrsCurrentDist) {
            distances.setVertexDistance(neighbourId, currentIterationNo, vertexsCurrentDist + currentNbrEdgeWeight);
        }
    }


    /**
     * This algorithm is to backtrack assuming all edge weights are positive
     */
    public void backtrack() {
        didShortestPathChange = false;
        shortestPath.clear();
        if (distances.getLatestDistance(destination) == Double.MAX_VALUE) {
            return;
        }

        IntQueue nodesOnPath = new IntQueue();
        Set<Integer> visited = new HashSet<>();

        nodesOnPath.enqueue(this.destination);
        while (!nodesOnPath.isEmpty()) {
            int currNode = nodesOnPath.dequeue();
            if (visited.contains(currNode)) {
                continue;
            }
            visited.add(currNode);
            Set<Integer> parents = new HashSet<>();
            double vertexDistance = distances.getLatestDistance(currNode);
            SortedAdjacencyList incomingNodes = Graph.INSTANCE.getBackwardMergedAdjacencyList(currNode);

            for (int i = 0; i < incomingNodes.getSize(); i++) {
                int nbrId = incomingNodes.neighbourIds[i];
                //TODO: This is a bug that need to be fixed, we should never get a negative id - check the neighbor size before visiting all itemes in neighbors list
                if (nbrId < 0) {
                    continue;
                }
                double nbrWeight = incomingNodes.weights[i];
                if (distances.getLatestDistance(nbrId) + nbrWeight == vertexDistance) {
                    parents.add(nbrId);
                    nodesOnPath.enqueue(nbrId);
                }
            }

            shortestPath.add(currNode, parents);
        }
    }
}

