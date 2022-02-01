package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.Graph.Direction;
import ca.waterloo.dsg.graphflow.graph.SortedAdjacencyList;
import ca.waterloo.dsg.graphflow.util.IntQueue;
import ca.waterloo.dsg.graphflow.util.Report;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * An implementation of Unidirectional weighted differential BFS. The direction of the BFS
 * is always assumed to be forward.
 */
public class NewUnidirectionalWeightedDifferentialBFSDropRandomVertexDiff extends NewUnidirectionalDifferentialBFS {

    /**
     * @see {@link NewUnidirectionalDifferentialBFS}.
     */
    public NewUnidirectionalWeightedDifferentialBFSDropRandomVertexDiff(int queryId, int source, int destination,
                                                                        boolean backtrack, float dropProbability, Queries queryType) {
        super(queryId, source, destination, Direction.FORWARD, backtrack, queryType);

        //Report.INSTANCE.debug("------ Initialize NewUnidirectionalWeightedDifferentialBFSDropRandomVertexDiff query " +
        //        source + " -> " + direction + " prob = " + dropProbability);

        // change the type of my distances
        distances = new DistancesWithDropBloom(queryId, source, destination, direction, dropProbability, queryType);

        //Report.INSTANCE.debug("*** distance are set");
    }

    public void mergeDeltaDiff() {
        distances.mergeDeltaDiffs();
    }

    /**
     * In unidirectional weighted BFS, when edge weights can be anything, BFS should
     * never stop early. It should only stop when the frontier is empty.
     *
     * @see {@link NewUnidirectionalDifferentialBFS#shouldStopBFSEarly(int)}.
     */
    @Override
    public boolean shouldStopBFSEarly(short currentIterationNo) {
        return false;
    }

    // dummy function required by interface and used by Landmark
    public void preProcessing() {
    }


    @Override
    public void fixVertexAndAddNewVerticesToFix(int vertexToFix, short currentFixedIter) {

        //Report.INSTANCE.debug("------ fixVertexAndAddNewVerticesToFix - weighted - v= " + vertexToFix + " i= " +
        //        currentFixedIter);

        if (vertexToFix == this.source) {
            // We assume there are no negative weight cycles. So the source's
            // distance can never be updated.
            return;
        }
        double oldDistanceAtFixedIter = distances.getDistance(vertexToFix, currentFixedIter);

        //Report.INSTANCE.debug("** old distance = " + oldDistanceAtFixedIter);

        // First we compute the new distance of the vertex at iteration iterNo.
        long newDistanceAtFixedIter = Long.MAX_VALUE;
        SortedAdjacencyList incomingAdjList = Graph.INSTANCE.getBackwardMergedAdjacencyList(vertexToFix);

/*        if (SortedAdjacencyList.isNullOrEmpty(incomingAdjList)) {
            Report.INSTANCE.debug("** incoming adjacency list is EMPTY ");
        } else {
            Report.INSTANCE
                    .debug("** incoming adjacency list = " + Arrays.toString(incomingAdjList.neighbourIds) + " W = " +
Arrays.toString(incomingAdjList.weights));
        }
*/
        int nbrID;
        long nbrDistance, nbrCurrentVertexEdgeWeight;
        if (!SortedAdjacencyList.isNullOrEmpty(incomingAdjList)) {
            for (int idx = 0; idx < incomingAdjList.getSize(); idx++) {

                nbrID = incomingAdjList.neighbourIds[idx];

                // TODO: Khaled, this is where I need previous iterations
                // I need to tell what was the distance at a specific previous iteration
                nbrDistance = distances.getDistance(nbrID, currentFixedIter);

                //Report.INSTANCE.debug("**** Nbr = " + nbrID + " dst = " + nbrDistance);

                if (Double.MAX_VALUE == nbrDistance) {
                    continue;
                }
                nbrCurrentVertexEdgeWeight = (long) incomingAdjList.weights[idx];
                if (nbrDistance + nbrCurrentVertexEdgeWeight < newDistanceAtFixedIter) {
                    newDistanceAtFixedIter = nbrDistance + nbrCurrentVertexEdgeWeight;
                    //Report.INSTANCE.debug("**** Found a better distance " + newDistanceAtFixedIter);
                }
            }
        }

        // If the vertex u's distance changed at the current iteration iterNo.
        // Suppose u has been updated in times t_u1,t_u2,...,t_up. We do the
        // following two things: (1) For each incoming neighbor w of u, if w has been updated
        // at iterations t_1, ..., t_k, if t_i is greater than iterNo, we add u to be fixed at
        // t_i + 1. (2) In addition, we add for each outgoing neighbor w' of u for t_uj+1 for
        // each t_uj > iterNo.
        if (newDistanceAtFixedIter != oldDistanceAtFixedIter) {

            //Report.INSTANCE.debug("**** Distance changed from OLD - do majic");

            distances.setVertexDistance(vertexToFix, currentFixedIter, newDistanceAtFixedIter);
            SortedAdjacencyList incomingPermanentAdjList = Graph.INSTANCE.getBackwardUnMergedAdjacencyList(vertexToFix);
            if (!SortedAdjacencyList.isNullOrEmpty(incomingPermanentAdjList)) {
                for (int j = 0; j < incomingPermanentAdjList.getSize(); j++) {
                    nbrID = incomingPermanentAdjList.neighbourIds[j];
                    for (Short iteration : distances.getAllIterations(nbrID)) {
                        if (iteration > currentFixedIter) {
                            //verticesToFix.addVToFix(vertexToFix, iteration + 1);
                            addVFORFix(vertexToFix, (short) (iteration + 1));

                            //if(Report.INSTANCE.appReportingLevel == Report.Level.DEBUG)
                            //    distances.vertexToFixCount(vertexToFix);
                        }
                    }
                }
            }

            SortedAdjacencyList outgoingPermanentAdjList = Graph.INSTANCE.getForwardUnMergedAdjacencyList(vertexToFix);
            if (!SortedAdjacencyList.isNullOrEmpty(outgoingPermanentAdjList)) {
                for (Short vertexToFixIter : distances.getAllIterations(vertexToFix)) {
                    if (vertexToFixIter >= currentFixedIter) {
                        for (int j = 0; j < outgoingPermanentAdjList.getSize(); j++) {
                            nbrID = outgoingPermanentAdjList.neighbourIds[j];
                            //verticesToFix.addVToFix(nbrID, vertexToFixIter + 1);
                            addVFORFix(nbrID, (short) (vertexToFixIter + 1));

                            //if(Report.INSTANCE.appReportingLevel == Report.Level.DEBUG)
                            //    distances.vertexToFixCount(nbrID);
                        }
                    }
                }
            }
            //distances.clearVertexDistanceAfterIterNo(vertexToFix, currentFixedIter);
        }
    }

    @Override
    public void updateNbrsDistance(int currentVertexId, SortedAdjacencyList currentVsAdjList, int neighborIdIndex,
                                   short currentIterationNo) {
        int neighbourId = currentVsAdjList.neighbourIds[neighborIdIndex];
        long currentNbrEdgeWeight = (long) currentVsAdjList.weights[neighborIdIndex];
        long vertexsCurrentDist = distances.getLatestDistance(currentVertexId);
        long nbrsCurrentDist = distances.getLatestDistance(neighbourId);
        if (vertexsCurrentDist + currentNbrEdgeWeight < nbrsCurrentDist) {
            distances.setVertexDistance(neighbourId, currentIterationNo, vertexsCurrentDist + currentNbrEdgeWeight);
        }
    }


    @Override
    public void updateNbrsDistance(int currentVertexId, long vertexsCurrentDist, int neighbourId,
                                   long currentNbrEdgeWeight, short currentIterationNo) {

        //Report.INSTANCE.debug("** Weighted updateNbrsDistance - from " + currentVertexId + " @ iteration " +
        //        currentIterationNo + "  : update " + neighbourId + " neighbor weight = " + currentNbrEdgeWeight);

        //double vertexsCurrentDist = distances.getLatestDistance(currentVertexId);
        long nbrsCurrentDist = distances.getLatestDistance(neighbourId);
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

