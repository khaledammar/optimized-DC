package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.Graph.Direction;
import ca.waterloo.dsg.graphflow.graph.SortedAdjacencyList;
import ca.waterloo.dsg.graphflow.util.IntQueue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An implementation of differential BFS that is tailored for
 * computing variable-length path queries continuously.
 */
public class VariableLengthPathDifferentialBFS extends NewUnidirectionalDifferentialBFS {

    private int minLength;
    private int maxLength;

    public VariableLengthPathDifferentialBFS(int queryId, int source, int destination, int minLength, int maxLength,
                                             boolean backtrack, Queries queryType) {
        super(queryId, source, destination, Direction.FORWARD, backtrack, queryType);
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    public void mergeDeltaDiff() {
        distances.mergeDeltaDiffs();
    }

    /**
     * We continue BFS until maxLength (unless the frontier is empty before).
     */
    @Override
    public boolean shouldStopBFSEarly(short currentIterationNo) {
        return currentIterationNo == maxLength;
    }

    @Override
    public void backtrack() {
        didShortestPathChange = false;
        shortestPath.clear();
        if (distances.getLatestDistance(destination) == Double.MAX_VALUE) {
            return;
        }

        IntQueue nodesOnPath = new IntQueue();
        nodesOnPath.enqueue(destination);
        int lastVertexOnFrontier = destination;
        Set<Integer> visited = new HashSet<>();
        int lowerBound = minLength - 1;
        int upperBound = maxLength - 1;
        while (!nodesOnPath.isEmpty()) {
            int currNode = nodesOnPath.dequeue();
            if (visited.contains(currNode)) {
                continue;
            }
            visited.add(currNode);
            Set<Integer> parents = new HashSet<>();
            SortedAdjacencyList incomingNodes = Graph.INSTANCE.getBackwardMergedAdjacencyList(currNode);
            List<Distances.Diff> nbrDistances;
            for (int i = 0; i < incomingNodes.getSize(); i++) {
                int nbrId = incomingNodes.neighbourIds[i];
                nbrDistances = distances.getAllDistances(nbrId);

                for (Distances.Diff object : nbrDistances) {

                    Distances.IterationDistancePair iterDistance = (Distances.IterationDistancePair) object;

                    if (iterDistance.distance <= upperBound && iterDistance.distance >= lowerBound) {
                        parents.add(nbrId);
                        nodesOnPath.enqueue(nbrId);
                        break;
                    }
                }
            }
            shortestPath.add(currNode, parents);
            if (lastVertexOnFrontier == currNode) {
                lastVertexOnFrontier = !nodesOnPath.isEmpty() ? nodesOnPath.peekLast() : -1;
                upperBound = (upperBound == 0) ? 0 : upperBound - 1;
                lowerBound = (lowerBound == 0) ? 0 : lowerBound - 1;
            }
        }
    }

    @Override
    public void fixVertexAndAddNewVerticesToFix(int vertexToFix, short currentFixedIter) {
        double oldDistanceAtCurrentFixedIter = distances.getDistance(vertexToFix, currentFixedIter);
        // We need to fix the distance of vertexToFix at the current time t.
        // And we need to see if any of the vertexToFix's neighbors need to be
        // fixed.
        double newDistanceAtTimeT = Double.MAX_VALUE;
        SortedAdjacencyList incomingAdjList = Graph.INSTANCE.getBackwardMergedAdjacencyList(vertexToFix);
        int nbrID;
        double nbrDistanceAtPreviousIteration;
        if (!SortedAdjacencyList.isNullOrEmpty(incomingAdjList)) {
            for (int idx = 0; idx < incomingAdjList.getSize(); idx++) {
                nbrID = incomingAdjList.neighbourIds[idx];
                nbrDistanceAtPreviousIteration = distances.getDistance(nbrID, (short) (currentFixedIter - 1));
                if (nbrDistanceAtPreviousIteration != currentFixedIter - 1) {
                    continue;
                }
                if (nbrDistanceAtPreviousIteration + 1 < newDistanceAtTimeT) {
                    newDistanceAtTimeT = currentFixedIter;
                    break;
                }
            }
        }

        if (newDistanceAtTimeT != oldDistanceAtCurrentFixedIter) {
            if (Double.MAX_VALUE == newDistanceAtTimeT) {
                distances.clearVertexDistanceAtT(vertexToFix, (short) currentFixedIter, newDistanceAtTimeT);
            } else {
                assert currentFixedIter == newDistanceAtTimeT : "newDistanceAtTimeT has to equal to currentFixedIter";
                distances.setVertexDistance(vertexToFix, currentFixedIter, newDistanceAtTimeT);
            }
            SortedAdjacencyList outgoingAdjList = Graph.INSTANCE.getForwardMergedAdjacencyList(vertexToFix);
            if (null != outgoingAdjList && currentFixedIter < distances.latestIteration) {
                for (int nbrIndex = 0; nbrIndex < outgoingAdjList.getSize(); nbrIndex++) {
                    nbrID = outgoingAdjList.neighbourIds[nbrIndex];
                    //verticesToFix.addVToFix(nbrID, currentFixedIter + 1);

                    addVFORFix(nbrID, (short) (currentFixedIter + 1));
                }
            }
        }
    }

    @Override
    public void updateNbrsDistance(int currentVertexId, SortedAdjacencyList currentVsAdjList, int nbrIndex,
                                   short currentIterationNo) {
        int neighbourId = currentVsAdjList.neighbourIds[nbrIndex];
        distances.setVertexDistance(neighbourId, currentIterationNo, currentIterationNo);
    }

    @Override
    public void updateNbrsDistance(int currentVertexId, double vertexsCurrentDist, int neighbourId,
                                   double neighbourWeight, short currentIterationNo) {
        distances.setVertexDistance(neighbourId, currentIterationNo, currentIterationNo);
    }


    boolean doesPathExist() {
        double destinationDistance = distances.getLatestDistance(destination);
        return destinationDistance >= minLength && destinationDistance <= maxLength;
    }
}
