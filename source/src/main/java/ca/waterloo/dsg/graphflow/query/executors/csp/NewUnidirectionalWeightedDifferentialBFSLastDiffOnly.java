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
public class NewUnidirectionalWeightedDifferentialBFSLastDiffOnly extends NewUnidirectionalDifferentialBFS {

    /**
     * @see {@link NewUnidirectionalDifferentialBFS}.
     */
    public NewUnidirectionalWeightedDifferentialBFSLastDiffOnly(int queryId, int source, int destination,
                                                                boolean backtrack, Queries queryType) {
        super(queryId, source, destination, Direction.FORWARD, backtrack, queryType);
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

    public void mergeDeltaDiff() {
        mergeDeltaDiff();
    }


    /**
     * Fixes the BFS differentially.
     * This is called from ContinuousDiffBFSShortestPathPlan.execute()
     */
    public void executeDifferentialBFS() {

        Report.INSTANCE.debug("------ executeDifferentialBFS - weighted");

        // Prepare a list of vertices that may impact the shortest path
        // based on added/deleted edges
        addVerticesToFixFromDiffEdges();

        // if there is no changes in SP, return!
        if (verticesToFix.isEmpty) {
            return;
        }

        // go through all iterations, and fix one step at a time
        // Stop when:
        // 1- destination found
        // 2- Frontier is empty
        // 3- last iteration is reached
        short t = 1;

        Report.INSTANCE.debug("== revisiting all iterations");

        while (!verticesToFix.isEmptyHash()) {
            Report.INSTANCE.debug("** empty hash? " + verticesToFix.isEmptyHash() + ", Size = " +
                    verticesToFix.iterVPairsList.size());
            t = 0;

            // TODO, Khaled, this is for testing only
            while (!verticesToFix.isEmptyHash() && t < verticesToFix.iterVPairsList.size()) {
                //while (t <= distances.latestIteration) {
                t++;

                if (Report.INSTANCE.appReportingLevel == Report.Level.DEBUG) {
                    Report.INSTANCE.debug("== iteration #" + t + " : " +
                            Arrays.toString(verticesToFix.getItemFromIterVPairsList(t).toArray()));
                }

                if (verticesToFix.getItemFromIterVPairsList(t).size() > 0) {
                    fixOneBFSStep(t);

                    if (distances.isFrontierEmpty(t) || shouldStopBFSEarly(t)) {
                        Report.INSTANCE.debug("** Frontier is empty or destination found");
                        break;
                    }
                }

                //if (t < distances.latestIteration) {
                //    ++t;
                //} else {
                //    break;
                //}
            }
        }

        // clear list of vertices
        Report.INSTANCE.debug("== Clear list of vertices to fix");
        verticesToFix.clear();

        // Empty frontier or destination found?
        if (t < verticesToFix.iterVPairsList.size() && (distances.isFrontierEmpty(t) || shouldStopBFSEarly(t))) {

            // change latest iteration

            if (t < distances.latestIteration) {
                distances.setLatestIterationNumber(t);
            }

            // update path when necessary!
            if (backtrack && doesPathExist() && (shortestPath.isEmpty() || didShortestPathChange)) {
                backtrack();
            } else if (!doesPathExist() && distances.isFrontierEmpty(t) && !shortestPath.isEmpty()) {
                shortestPath.clear();
            }
        } else {

            Report.INSTANCE.debug("== more BFS is needed");
            // continue BFS if more iteration needed
            continueBFS();
        }
    }


    public void fixOneBFSStep(short t) {

        Report.INSTANCE.debug("------ fixOneBFSStep - weighted " + t);

        // ShortestPath is only used if we are doing backtracking!
        // System.out.println("== "+shortestPath.toString());

        if (verticesToFix.iterVPairsList.size() > t && !verticesToFix.getItemFromIterVPairsList(t).isEmpty()) {

            for (int vertexToFixAtT : verticesToFix.getItemFromIterVPairsList(t)) {
                Report.INSTANCE.debug("** fix vertex " + vertexToFixAtT + " and look for neighbors");

                // this is only used if we are doing backtracking!
                if (shortestPath.contains(vertexToFixAtT)) {
                    didShortestPathChange = true;
                }
                fixVertexAndAddNewVerticesToFix(vertexToFixAtT, t);
                //fixVertexAndAddNewVerticesToFixLazyVersion(vertexToFixAtT, t);
            }

            verticesToFix.removeDeletedVertices();
        }
    }


    @Override
    public void fixVertexAndAddNewVerticesToFix(int vertexToFix, short currentFixedIter) {

        Report.INSTANCE.debug("------ fixVertexAndAddNewVerticesToFix - weighted - v= " + vertexToFix + " i= " +
                currentFixedIter);

        if (vertexToFix == this.source) {
            // We assume there are no negative weight cycles. So the source's
            // distance can never be updated.
            return;
        }
        double oldDistanceAtFixedIter = distances.getDistance(vertexToFix, currentFixedIter);

        Report.INSTANCE.debug("** old distance = " + oldDistanceAtFixedIter);

        // First we compute the new distance of the vertex at iteration iterNo.
        double newDistanceAtFixedIter = Double.MAX_VALUE;
        SortedAdjacencyList incomingAdjList = Graph.INSTANCE.getBackwardMergedAdjacencyList(vertexToFix);

        if (SortedAdjacencyList.isNullOrEmpty(incomingAdjList)) {
            Report.INSTANCE.debug("** incoming adjacency list is EMPTY ");
        } else {
            Report.INSTANCE
                    .debug("** incoming adjacency list = " + Arrays.toString(incomingAdjList.neighbourIds) + " W = " +
                            Arrays.toString(incomingAdjList.weights));
        }

        int nbrID;
        double nbrDistance, nbrCurrentVertexEdgeWeight;
        int fixingIteration = currentFixedIter;

        if (!SortedAdjacencyList.isNullOrEmpty(incomingAdjList)) {
            for (int idx = 0; idx < incomingAdjList.getSize(); idx++) {

                nbrID = incomingAdjList.neighbourIds[idx];

                // TODO: Khaled, this is where I need previous iterations
                // I need to tell what was the distance at a specific previous iteration
                nbrDistance = distances.getDistance(nbrID, currentFixedIter);

                Report.INSTANCE.debug("**** Nbr = " + nbrID + " dst = " + nbrDistance + " @ i = " + currentFixedIter);

                nbrDistance = distances.getLatestDistance(nbrID);
                int potentialFixingIteration = distances.getIterationWithBestDistance(nbrID);

                Report.INSTANCE.debug("**** Nbr = " + nbrID + " dst = " + nbrDistance + " @ latest iteration = " +
                        potentialFixingIteration);

                if (Double.MAX_VALUE == nbrDistance) {
                    continue;
                }
                nbrCurrentVertexEdgeWeight = incomingAdjList.weights[idx];
                if (nbrDistance + nbrCurrentVertexEdgeWeight < newDistanceAtFixedIter) {
                    newDistanceAtFixedIter = nbrDistance + nbrCurrentVertexEdgeWeight;
                    fixingIteration = potentialFixingIteration;
                    Report.INSTANCE.debug("**** Found a better distance = " + newDistanceAtFixedIter);
                }
            }
        }

        // TODO: Khaled, this is also how we use previous diffs

        // If the vertex u's distance changed at the current iteration iterNo.
        // Suppose u has been updated in times t_u1,t_u2,...,t_up. We do the
        // following two things: (1) For each incoming neighbor w of u, if w has been updated
        // at iterations t_1, ..., t_k, if t_i is greater than iterNo, we add u to be fixed at
        // t_i + 1. (2) In addition, we add for each outgoing neighbor w' of u for t_uj+1 for
        // each t_uj > iterNo.
        if (newDistanceAtFixedIter != oldDistanceAtFixedIter) {

            Report.INSTANCE.debug("**** Distance changed from OLD - do majic");

            //distances.setVertexDistance(vertexToFix, currentFixedIter, newDistanceAtFixedIter);
            distances.clearAndSetOnlyVertexDistance(vertexToFix, (short) (fixingIteration + 1), newDistanceAtFixedIter);

            if (Report.INSTANCE.appReportingLevel == Report.Level.DEBUG) {
                Report.INSTANCE.debug("**** New distances = " +
                        Arrays.toString(distances.getAllDistances(vertexToFix).toArray()));
            }

            /**
             * I do not need to worry about incoming edges
             *
             SortedAdjacencyList incomingPermanentAdjList =
             Graph.INSTANCE.backwardAdjLists[vertexToFix];
             if (!SortedAdjacencyList.isNullOrEmpty(incomingPermanentAdjList)) {
             for (int j = 0; j < incomingPermanentAdjList.getSize(); j++) {
             nbrID = incomingPermanentAdjList.neighbourIds[j];
             for (IterationDistancePair nbrIterDistPair : distances.getAllDistances(nbrID)) {
             if (nbrIterDistPair.iterationNo > currentFixedIter) {
             verticesToFix.addVToFix(vertexToFix, nbrIterDistPair.iterationNo + 1);
             }
             }
             }
             }
             */

            SortedAdjacencyList outgoingPermanentAdjList = Graph.INSTANCE.getForwardUnMergedAdjacencyList(vertexToFix);

            if (!SortedAdjacencyList.isNullOrEmpty(outgoingPermanentAdjList)) {

                if (Report.INSTANCE.appReportingLevel == Report.Level.DEBUG) {
                    Report.INSTANCE.debug("**** Fix outgoing neighbors");
                    Report.INSTANCE.debug("******* " + Arrays.toString(outgoingPermanentAdjList.neighbourIds));
                }
                for (Distances.Diff vertexToFixIterDistPair : distances.getAllDistances(vertexToFix)) {

                    if (vertexToFixIterDistPair.iterationNo >= fixingIteration) {
                        for (int j = 0; j < outgoingPermanentAdjList.getSize(); j++) {
                            nbrID = outgoingPermanentAdjList.neighbourIds[j];
                            //verticesToFix.addVToFix(nbrID,  currentFixedIter + 1);
                            addVFORFix(nbrID, (short) (currentFixedIter + 1));
                        }
                    }
                }
            }
            // already cleared
            //distances.clearVertexDistanceAfterIterNo(vertexToFix, currentFixedIter);
        }

        // Khaled, avoid removing V because we are doing that inside an iterator!
        verticesToFix.removeVLazy(vertexToFix, currentFixedIter);
    }

    @Override
    public void updateNbrsDistance(int currentVertexId, SortedAdjacencyList currentVsAdjList, int neighborIdIndex,
                                   short currentIterationNo) {
        int neighbourId = currentVsAdjList.neighbourIds[neighborIdIndex];
        double currentNbrEdgeWeight = currentVsAdjList.weights[neighborIdIndex];
        double vertexsCurrentDist = distances.getLatestDistance(currentVertexId);
        double nbrsCurrentDist = distances.getLatestDistance(neighbourId);
        if (vertexsCurrentDist + currentNbrEdgeWeight < nbrsCurrentDist) {

            //distances.setVertexDistance(neighbourId, currentIterationNo, vertexsCurrentDist + currentNbrEdgeWeight);
            distances.clearAndSetOnlyVertexDistance(neighbourId, currentIterationNo,
                    vertexsCurrentDist + currentNbrEdgeWeight);
        }
    }


    @Override
    public void updateNbrsDistance(int currentVertexId, double vertexsCurrentDist, int neighbourId,
                                   double currentNbrEdgeWeight, short currentIterationNo) {

        Report.INSTANCE.debug("** WeightedLASTDIFF updateNbrsDistance - from " + currentVertexId + " @ iteration " +
                currentIterationNo + "  : update " + neighbourId + " neighbor weight = " + currentNbrEdgeWeight);

        //double vertexsCurrentDist = distances.getLatestDistance(currentVertexId);
        double nbrsCurrentDist = distances.getLatestDistance(neighbourId);
        if (vertexsCurrentDist + currentNbrEdgeWeight < nbrsCurrentDist) {

            //distances.setVertexDistance(neighbourId, currentIterationNo, vertexsCurrentDist + currentNbrEdgeWeight);
            distances.clearAndSetOnlyVertexDistance(neighbourId, currentIterationNo,
                    vertexsCurrentDist + currentNbrEdgeWeight);
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
