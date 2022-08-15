package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph.Direction;
import ca.waterloo.dsg.graphflow.graph.SortedAdjacencyList;
import ca.waterloo.dsg.graphflow.util.IntQueue;
import ca.waterloo.dsg.graphflow.util.Report;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * An implementation of Unidirectional unweighted differential BFS. The class takes
 * as input a direction in which to do the BFS.
 */
public class NewUnidirectionalUnweightedDifferentialBFS extends NewUnidirectionalDifferentialBFS {
    /**
     * @see {@link NewUnidirectionalDifferentialBFS}.
     */
    public NewUnidirectionalUnweightedDifferentialBFS(int queryId, int source, int destination, Direction direction,
                                                      boolean backtrack, Queries queryType) {
        super(queryId, source, destination, direction, backtrack, queryType);
    }

    public NewUnidirectionalUnweightedDifferentialBFS(int queryId, int source, int destination, Direction direction,
                                                      boolean backtrack, DropIndex dropIndex, float prob,
                                                      DistancesWithDropBloom.DropType dropType, String bloomType, int minimumDegree,
                                                      int maxDegree, Queries queryType) {

        super(queryId, source, destination, direction, backtrack, queryType);

        if (dropIndex == DropIndex.BLOOM) {
            distances = new DistancesWithDropBloom(queryId, source, destination, direction, prob, dropType, bloomType,
                    minimumDegree, maxDegree, queryType);
        } else if (dropIndex == DropIndex.HASH_TABLE) {
            distances = new DistancesWithDropHash(queryId, source, destination, direction, prob, dropType, bloomType,
                    minimumDegree, maxDegree, queryType);
        }
    }

    boolean doesPathExist() {
        return false;
    }

    public void mergeDeltaDiff() {
        distances.mergeDeltaDiffs();
    }

    /**
     * BFS should stop as soon as the destination is reached.
     *
     * @see {@link NewUnidirectionalDifferentialBFS#shouldStopBFSEarly(int)}.
     */
    @Override
    public boolean shouldStopBFSEarly(short currentIterationNo) {

        //Report.INSTANCE.debug("------ shouldStopBFSEarly - UNIUNW");

        return distances.getDistance(destination, currentIterationNo) != Long.MAX_VALUE;
    }

    // dummy function required by interface and used by Landmark
    public void preProcessing() {
    }

    /*
        @Override
        public void fixVertexAndAddNewVerticesToFix(int vertexToFix, int currentFixedIter) {

            Report.INSTANCE.debug("------ fixVertexAndAddNewVerticesToFix - UNIUNW (vertex "+vertexToFix+ " @ iteration "+currentFixedIter+")");


            if (vertexToFix == this.source) {
                // You cannot fix the source, because its distance is zero anyway!
                return;
            }

            // Get the distance value before fixing
            double oldDistanceAtCurrentFixedIter = distances.getDistance(vertexToFix, currentFixedIter, false);
            Report.INSTANCE.debug("** old distance of vertex "+vertexToFix+ " @ "+currentFixedIter+ " is "+oldDistanceAtCurrentFixedIter);

            // Any vertex we fix at time t will either have a time of t or infinity. If
            // vertexToFix's oldDistanceAtTimeT is less than t, then either (i) this vertex has
            // already been fixed at a previous time; or (ii) an edge was added or deleted to a
            // neighbor of vertexToFix that had a distance of t-1. For (i), we will have generated
            // the necessary diffs necessary for this vertex at an earlier time t' < t that we already
            // fixed. For (ii) the update to the nbr cannot generate diffs for vertexToFix. As an
            // example: suppose vertexToFix=v3, t=4 and v3's oldDistanceAtTimeT is 1. In case (i) v3's
            // distance was updated to 1 from dist > 1, say 5. In this case for time 2 we generated a
            // diff for each out nbr of v3 already. Or (ii) a nbr, say v10 that was at a distance 3,
            // had an edge (v10, v3) added or deleted. And that's why we are attempting to fix v10. In
            // this case the diff that will be generated for v3 is empty because previously v3 didn't
            // try to update any of its neighbors at time 3 and neither will it now, so the diff is
            // empty. So in either case we can skip updating v3.
            if (oldDistanceAtCurrentFixedIter < currentFixedIter) {
                return;
            }

            // We need to fix the distance of vertexToFix at the current time t.
            // And we need to see if any of the vertexToFix's neighbors need to be
            // fixed.

            // Here we find the smallest distance to the vertexToFix using all its neighbours including
            // recently added/deleted ones (hence using MergedAdjacencyList)
            double newDistanceAtTimeT = Double.MAX_VALUE;
            SortedAdjacencyList incomingAdjList = (direction == Direction.FORWARD) ?
                    Graph.INSTANCE.getBackwardMergedAdjacencyList(vertexToFix) :
                    Graph.INSTANCE.getForwardMergedAdjacencyList(vertexToFix);
            int nbrID;
            double nbrDistance;
            if (!SortedAdjacencyList.isNullOrEmpty(incomingAdjList)) {
                for (int idx = 0; idx < incomingAdjList.getSize(); idx++) {


                    nbrID = incomingAdjList.neighbourIds[idx];
                    nbrDistance = distances.getDistance(nbrID, currentFixedIter, true);

                    Report.INSTANCE.debug("*** checking reverse neighbor "+nbrID+" with distance "+nbrDistance);

                    // We ignore nbrs whose distances are infinity at time t. These
                    // are exactly the ones whose distance >= t.
                    if (nbrDistance >= currentFixedIter) {
                        continue;
                    }
                    if (nbrDistance + 1 < newDistanceAtTimeT) {
                        newDistanceAtTimeT = nbrDistance + 1;
                    }
                }
            }

            if (newDistanceAtTimeT != oldDistanceAtCurrentFixedIter) {
                if (newDistanceAtTimeT < oldDistanceAtCurrentFixedIter) {
                    distances.clearAndSetOnlyVertexDistance(vertexToFix, currentFixedIter, newDistanceAtTimeT);
                } else if (Double.MAX_VALUE == newDistanceAtTimeT) {
                    distances.clearVertexDistancesAtAndBeforeT(vertexToFix, currentFixedIter);
                    // If the distance of vertexToFixAtT became infinity then: go through all of the
                    // incoming neighbours nbr of vertexToFixAtT and vertexToFixAtT be fixed at
                    // nbr.distance+1 for if nbr.distance >= t and nbr.distance < latestIteration. We don't
                    // need to check for nbr.distance > t explicitly because if nbr.distance was < t, then
                    // newDistanceAtTimeT would not be infinity.
                    SortedAdjacencyList incomingPermanentAdjList = (direction == Direction.FORWARD) ?
                        Graph.INSTANCE.backwardAdjLists[vertexToFix] :
                        Graph.INSTANCE.forwardAdjLists[vertexToFix];;
                    if (!SortedAdjacencyList.isNullOrEmpty(incomingPermanentAdjList)) {
                        for (int j = 0; j < incomingPermanentAdjList.getSize(); j++) {
                            nbrID = incomingPermanentAdjList.neighbourIds[j];
                            nbrDistance = distances.getLatestDistance(nbrID);
                            if (nbrDistance < distances.latestIteration) {

                                //verticesToFix.addVToFix(vertexToFix, (int) nbrDistance + 1);
                                addVFORFix(vertexToFix,(int) nbrDistance + 1);

                            }
                        }
                    }
                }
                else{
                    // what if it was larger but not infinity?!
                    Report.INSTANCE.debug("** Warning - new distance of "+vertexToFix+" @ "+currentFixedIter+" is larger than oldDistanceAtCurrentFixedIter ("+oldDistanceAtCurrentFixedIter+")");
                }


                Report.INSTANCE.debug("** Let us go through all neighbors and fix them too!");
                // Go through all FWD neighbors and add them to be fixed
                SortedAdjacencyList outgoingAdjList = (direction == Direction.FORWARD) ?
                    Graph.INSTANCE.getForwardMergedAdjacencyList(vertexToFix) :
                    Graph.INSTANCE.getBackwardMergedAdjacencyList(vertexToFix);

                Report.INSTANCE.debug("** Let us go through all neighbors and fix them too! #Nbr= "+outgoingAdjList.getSize());

                if (null != outgoingAdjList ) { // && currentFixedIter < distances.latestIteration) { // this is commented
                    // because it is possible I am fixing a vertex that was not reachable before and so the latest iteration did not reach it
                    // However, this is probably should be fixed by continueBFS not this one!
                    for (int nbrIndex = 0; nbrIndex < outgoingAdjList.getSize(); nbrIndex++) {
                        nbrID = outgoingAdjList.neighbourIds[nbrIndex];
                        //verticesToFix.addVToFix(nbrID, currentFixedIter + 1);
                        addVFORFix(nbrID,currentFixedIter+1);
                    }
                }
            }
        }
    */
    public void backtrack() {
        didShortestPathChange = false;
        shortestPath.clear();
        if (distances.getLatestDistance(destination) == Long.MAX_VALUE) {
            return;
        }
        backtrack(new HashSet<>(Arrays.asList(this.destination)));
    }

    public void backtrack(Set<Integer> verticesToStartFrom) {
        IntQueue nodesOnPath = new IntQueue();
        Set<Integer> visited = new HashSet<>();

        for (Integer vertexToStartFrom : verticesToStartFrom) {
            nodesOnPath.enqueue(vertexToStartFrom);
        }
        while (!nodesOnPath.isEmpty()) {
            int currNode = nodesOnPath.dequeue();
            if (visited.contains(currNode)) {
                continue;
            }
            visited.add(currNode);
            Set<Integer> parents = new HashSet<>();
            long vertexDistance = distances.getLatestDistance(currNode);
            // TODO: We currently do not use backtrack, the iteration number need to be tracked properly if we need it
            SortedAdjacencyList incomingNodes = getInNeighbours(currNode, true, direction, (short) -1);

            for (int i = 0; i < incomingNodes.getSize(); i++) {
                // if this neighbour should not be considered for this iteration because of the edge type, ignore it!
                if(!goodNeighbour(incomingNodes.getEdgeType(i),(short) (-1)))
                    continue;

                int nbrId = incomingNodes.neighbourIds[i];
                if (distances.getLatestDistance(nbrId) + 1 == vertexDistance) {
                    parents.add(nbrId);
                    nodesOnPath.enqueue(nbrId);
                }
            }

            shortestPath.add(currNode, parents);
        }
    }


    @Override
    public void updateNbrsDistance(int currentVertexId, SortedAdjacencyList currentVsAdjList, int neighborIdIndex,
                                   short currentIterationNo) {
        System.out.println("++++++++++++++= WHY");
        int neighbourId = currentVsAdjList.neighbourIds[neighborIdIndex];
        if (Long.MAX_VALUE == distances.getLatestDistance(neighbourId)) {
            distances.clearAndSetOnlyVertexDistance(neighbourId, currentIterationNo /* iteration no */,
                    currentIterationNo /* distance */);
        }
    }


    /**
     * This function only runs if the neighbor distance is infinity, otherwise it does nothing!
     *
     * @param currentVertexId
     * @param neighbourId
     * @param neighbourWeight
     * @param currentIterationNo
     */
    @Override
    public void updateNbrsDistance(int currentVertexId, long vertexsCurrentDist, int neighbourId,
                                   long neighbourWeight, short currentIterationNo) {

        /*
        if (Report.INSTANCE.appReportingLevel == Report.Level.DEBUG) {
            Report.INSTANCE.debug("------ UNIUNW updateNbrsDistance - v= " + currentVertexId + " current distance =" +
                    vertexsCurrentDist + " nbr=" + neighbourId + " nbr_weight=" + neighbourWeight);
            Report.INSTANCE.debug("== Neighbor distance is " +
                    distances.getNewDistance(neighbourId, currentIterationNo, true, currentVertexId) + " vs " +
                    Long.MAX_VALUE + " ? " + (Long.MAX_VALUE == distances.getLatestDistance(neighbourId)));
            Report.INSTANCE.debug("== Neighbor distance is " +
                    distances.getNewDistance(neighbourId, currentIterationNo, true, -1) + " vs " + Long.MAX_VALUE +
                    " ? " + (Long.MAX_VALUE == distances.getLatestDistance(neighbourId)));
        }

         */
        if (vertexsCurrentDist + neighbourWeight <
                distances.getNewDistance(neighbourId, currentIterationNo, true, currentVertexId)) {
            distances.clearAndSetOnlyVertexDistance(neighbourId, currentIterationNo /* iteration no */,
                    currentIterationNo /* distance */);
        }
    }
}
