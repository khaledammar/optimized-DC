package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.Graph.Direction;
import ca.waterloo.dsg.graphflow.graph.SortedAdjacencyList;
import ca.waterloo.dsg.graphflow.util.IntQueue;
import ca.waterloo.dsg.graphflow.util.Report;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * An implementation of Unidirectional weighted differential BFS. The direction of the BFS
 * is always assumed to be forward.
 * <p>
 * This is a special version that make a special handeling for unreachable queries. For example, it can reinitialize its
 * diffs in order to reduce the memory usage.
 */
public class NewUnidirectionalWeightedDifferentialBFSHandelUnreachable extends NewUnidirectionalDifferentialBFS {

    /**
     * @see {@link NewUnidirectionalDifferentialBFS}.
     */
    public NewUnidirectionalWeightedDifferentialBFSHandelUnreachable(int queryId, int source, int destination,
                                                                     boolean backtrack, DropIndex dropIndex,
                                                                     float prob, Queries queryType) {

        super(queryId, source, destination, Direction.FORWARD, backtrack, queryType);

        //Report.INSTANCE.debug("------ Initialize NewUnidirectionalWeightedDifferentialBFS query "+ source + " -> "+realDiffs + " -- "+ prob);

        // check if we should use regular or FAKE diffs
        if (dropIndex == DropIndex.BLOOM) {
            distances = new DistancesWithDropBloom2(queryId, source, destination, direction, prob, queryType);
        } else if (dropIndex == DropIndex.HASH_TABLE) {
            distances = new DistancesWithDropHash(queryId, source, destination, direction, prob, queryType);
        }
    }


    public void mergeDeltaDiff() {
        distances.mergeDeltaDiffs();
    }

    /**
     * Fixes the BFS differentially.
     * This is called from ContinuousDiffBFSShortestPathPlan.execute()
     */
    public void executeDifferentialBFS() {

        Report.INSTANCE.debug("------ executeDifferentialBFS ");
        if (getSrcDstDistance() == Double.MAX_VALUE) {
            continueBFS();
        }

        super.executeDifferentialBFS();
    }

    /**
     * This is the list of actions we can do after running the query.
     */
    public void postProcessingForUnreachable() {
        reInitializeDiffs();
    }

    public void reInitializeDiffs() {
        distances.reInitialize();
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


    public void fixVertexAndAddNewVerticesToFix(int vertexToFix, short currentFixedIter) {

        if (DistancesWithDropBloom.debug(vertexToFix)) {
            Report.INSTANCE.error("------ fixVertexAndAddNewVerticesToFix - weighted - v= " + vertexToFix + " i= " +
                    currentFixedIter);
        }

        if (vertexToFix == this.source) {
            // We assume there are no negative weight cycles. So the source's
            // distance can never be updated.
            return;
        }

        double oldDistanceAtFixedIter =
                distances.getDistance(vertexToFix, currentFixedIter, false /* do not consider current batch*/);
        double distanceAtPreviousIter = distances.getDistance(vertexToFix, (short) (currentFixedIter - 1), true);
        double newValue = distances.recalculateDistance(vertexToFix, currentFixedIter, true);

        if (DistancesWithDropBloom.debug(vertexToFix)) {
            Report.INSTANCE.error("** old distance = " + oldDistanceAtFixedIter + " previous Dist = " +
                    distanceAtPreviousIter + " newValue = " + newValue);
        }

        // get new diffs
        List<Distances.Diff> v_diffs =
                distances.getMergedDiffs(vertexToFix);  //distances.vIterDistPairMap.get(vertexToFix);
        String v_diff_string = "[]";
        if (null != v_diffs) {
            v_diff_string = Arrays.toString(v_diffs.toArray());
        }

        if (oldDistanceAtFixedIter != newValue || newValue <= distanceAtPreviousIter) {

            if (DistancesWithDropBloom.debug(vertexToFix)) {
                Report.INSTANCE.error("**** FIX v= " + vertexToFix + " newValue = " + newValue + " while old= " +
                        oldDistanceAtFixedIter + " @ " + currentFixedIter + " Diff= " + v_diff_string);
            }

            // Update self if in-neighbours were updated after currentIter
            // This is important because if the vertex became unreachable or worse, we need to revisit it again
            // in iterations after its in-neighbours were updated
            fixSelfIfNecessary(vertexToFix, currentFixedIter);

            fixSelfInFutureIterations(vertexToFix, currentFixedIter);

            if (newValue < distanceAtPreviousIter) {
                // set the new distance
                distances.setVertexDistance(vertexToFix, currentFixedIter, newValue);
            }
            // if this iteration does not enhance the distance of this vertex
            // remove this iteration if exist
            else {
                distances.clearVertexDistanceAtT(vertexToFix, (short) currentFixedIter, newValue);
            }

            // Fix out neighbours
            fixOutNeighbours(vertexToFix, currentFixedIter);
        } else {
            if (DistancesWithDropBloom.debug(vertexToFix)) {
                Report.INSTANCE.error("**** Distance at Previous Iter cannot be better than current iteration : v= " +
                        vertexToFix + " newVal= " + newValue + " vs. " + distanceAtPreviousIter + " @ iter " +
                        currentFixedIter + " || Diffs = " + v_diff_string);
            }
        }
        return;
    }


    public void fixOutNeighbours(int vertexToFix, int currentFixedIter) {

        if (DistancesWithDropBloom.debug(vertexToFix)) {
            Report.INSTANCE.error("**** Add out neighbours to be fixed after " + currentFixedIter);
        }

        SortedAdjacencyList outComingAdjList = getOutNeighbours(vertexToFix,true,direction, (short) (currentFixedIter+1));

        for (Integer out : outComingAdjList.neighbourIds) {
            for (Distances.Diff diff : distances.getMergedDiffs(vertexToFix)) {
                if (diff.iterationNo >= currentFixedIter) {
                    addVFORFix(out, (short) (diff.iterationNo + 1));
                }
            }
        }
    }

    public void fixSelfInFutureIterations(int vertexToFix, int currentFixedIter) {
        // check if in-neighbours were updated after current iteration
        // if yes, then we should add current vertex to be fixed too.

        if (DistancesWithDropBloom.debug(vertexToFix)) {
            Report.INSTANCE.error("**** Add self to fix for future iterations");
        }

        for (Distances.Diff iter : distances.getMergedDiffs(vertexToFix)) {
            if (iter.iterationNo > currentFixedIter) {
                addVFORFix(vertexToFix, iter.iterationNo);
            }
        }
    }


    public void fixSelfIfNecessary(int vertexToFix, int currentFixedIter) {
        // check if in-neighbours were updated after current iteration
        // if yes, then we should add current vertex to be fixed too.

        if (DistancesWithDropBloom.debug(vertexToFix)) {
            Report.INSTANCE.error("**** Add self to fix based on in-nbrs ");
        }

        Set<Short> toBeFixedIterations = new HashSet<>();

        SortedAdjacencyList incomingAdjList = Graph.INSTANCE.getBackwardMergedAdjacencyList(vertexToFix);
        for (int in_nbr = 0; in_nbr < incomingAdjList.getSize(); in_nbr++) {
            for (Distances.Diff iter : distances.getMergedDiffs(incomingAdjList.neighbourIds[in_nbr])) {
                if (iter.iterationNo > currentFixedIter) {
                    toBeFixedIterations.add(iter.iterationNo);
                }
            }
        }

        for (Short iter : toBeFixedIterations) {
            addVFORFix(vertexToFix, iter);
        }
    }

    @Override
    public void updateNbrsDistance(int currentVertexId, SortedAdjacencyList currentVsAdjList, int neighborIdIndex,
                                   short currentIterationNo) {

        int neighbourId = currentVsAdjList.neighbourIds[neighborIdIndex];
        double currentNbrEdgeWeight = currentVsAdjList.weights[neighborIdIndex];
        /**
         * There was a bug here by using the latest Distance instead of the distance from last iteration
         */
        double vertexsCurrentDist = distances.getDistance(currentVertexId, (short) (currentIterationNo - 1), true);
        double nbrsCurrentDist = distances.getDistance(neighbourId, currentIterationNo, true);

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
    public void updateNbrsDistance(int currentVertexId, double vertexsCurrentDist, int neighbourId,
                                   double currentNbrEdgeWeight, short currentIterationNo) {

        if (DistancesWithDropBloom.debug(neighbourId) || DistancesWithDropBloom.debug(currentVertexId)) {
            Report.INSTANCE.error("** Weighted updateNbrsDistance - from " + currentVertexId + " @ iteration " +
                    currentIterationNo + "  : update " + neighbourId + " neighbor weight = " + currentNbrEdgeWeight);
        }

        /**
         * There was a bug here by using the latest Distance instead of the distance from last iteration
         */
        //double vertexsCurrentDist = distances.getDistance(currentVertexId,currentIterationNo-1, true);
        // I should look at the Nbrs current iteration because I compare it with vertex previous iteration + Weight
        double nbrsCurrentDist = distances.getDistance(neighbourId, currentIterationNo, true);

        if (Report.INSTANCE.appReportingLevel == Report.Level.ERROR && DistancesWithDropBloom.debug(neighbourId)) {
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

