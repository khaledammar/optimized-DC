package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.Graph.Direction;
import ca.waterloo.dsg.graphflow.graph.SortedAdjacencyList;
import ca.waterloo.dsg.graphflow.util.Report;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Base class for different implementations of a unidirectional differential BFS.
 */
public abstract class NewUnidirectionalDifferentialBFSDCJOD implements DifferentialBFS {
    /**
     *
     */
    public enum DropIndex {
        NO_DROP,
        BLOOM,
        HASH_TABLE
    }

    public static VerticesToFix verticesToFix;
    // query id
    protected int queryId;
    // The source vertex.
    protected int source;
    // The destination vertex.
    protected int destination;
    // Distances different vertices took at different BFS iterations.
    protected DistancesDC distancesR;
    // Direction of the BFS.
    protected Direction direction;
    // boolean that triggers backtracking
    protected boolean didShortestPathChange;
    // stores that actual shortest path
    protected ShortestPath shortestPath;

    /**
     * @param source      ID of the source vertex.
     * @param destination ID of the destination vertex.
     * @param direction   direction of the BFS.
     */
    public NewUnidirectionalDifferentialBFSDCJOD(int queryId, int source, int destination, Direction direction) {

        Report.INSTANCE
                .debug("------ Initialize NewUnidirectionalDifferentialBFS query " + source + " -> " + direction);

        this.queryId = queryId;
        this.source = source;
        this.destination = destination;
        this.direction = direction;
        verticesToFix = new VerticesToFix();
        this.shortestPath = new ShortestPath(source, destination);
        this.didShortestPathChange = false;

        initFrontierAndSourceDistance(queryId);
    }

    // dummy function required by interface and used by Landmark
    public void preProcessing() {
    }

    @Override
    public void mergeDeltaDiff() {
        distancesR.mergeDeltaDiffsJOD();
        //Report.INSTANCE.debug("Diffs after batch:");
        distancesR.print();
    }


    public void printDiffs() {
        distancesR.print();
    }


    public void printStats() {
        //distances.printStats();

    }

    public void printDiffs(Report.Level l) { //distances.print(l);
        distancesR.printStats();
    }

    public int sizeOfDistances() {
        return distancesR.size();
    }
    public int getNumberOfVertices() {
        return distancesR.numberOfVerticesWithDiff();
    }

    public int sizeOfDeltaDistances() {
        return distancesR.deltaSize();
    }

    public int getRecalculateNumbers() {
        return 0;
    }
    public Map<Integer, Integer> getRecalculateStats() {
        return new HashMap<Integer,Integer>(0);
    }

    @Override
    public void initRecalculateNumbers() {

    }

    public int minimumSizeOfDistances() {
        return distancesR.numberOfVerticesWithDiff();
    }

    private void initFrontierAndSourceDistance(int queryId) {
        distancesR = new DistancesDC(queryId, source, destination, direction, "Reduce");
        distancesR.frontier.add(source);
    }

    abstract boolean shouldStopEarly(short iteration);

    abstract void getNeighborsData(DistancesDC distancesJ, int vertexId, long currentDistance, short diff);

    /**
     * Runs a unidirectional BFS starting from the frontier. This method is called twice. First,
     * when the continuous query is first registered and an initial BFS from scratch runs. Second,
     * each time the differentially maintained BFS is stuck and needs to extend the existing
     * frontier.
     * <p>
     * This is called when we want to run further BFS steps to find the destination
     */
    public void continueBFS() {
        // Khaled:
        // change the order, because if frontier is empty, then shouldStopBFSEarly will Fail with Null Pointer Exception

        //Report.INSTANCE.debug("------ continueBFS ");
        while (distancesR.frontier.size() > 0) {
            distancesR.incrementIterationNoAndSetPreviousFrontier();
            //Report.INSTANCE.debug("---- Iteration = " + distancesR.latestIteration);
            takeBFSStepAt(distancesR.latestIteration);
            if (shouldStopEarly(distancesR.latestIteration)) {
                break;
            }
        }
    }

    public void takeBFSStepAt(short iteration) {
        for (var currentVertexId : distancesR.frontierPrevious) {
            var currentDistance = distancesR.getDistance(currentVertexId, (short) (iteration - 1), true);
            if (currentDistance == Long.MAX_VALUE) {
                continue;
            }
            SortedAdjacencyList adjList = Graph.INSTANCE.getForwardMergedAdjacencyList(currentVertexId);
            if (SortedAdjacencyList.isNullOrEmpty(adjList)) {
                continue;
            }

            for (int j = 0; j < adjList.getSize(); j++) {
                // If the vertex is not the destination, has no outgoing neighbors that may help this query/direction, then no need to it to frontier!
                int neighbourVertexId = adjList.getNeighbourId(j);
                if (neighbourVertexId != source && currentVertexId != neighbourVertexId) {
                    updateNbrsDistance(currentVertexId, currentDistance, neighbourVertexId,
                            (long) adjList.getNeighbourWeight(j), iteration);
                }
            }
        }
        distancesR.mergeDeltaDiffs();
        distancesR.print();
    }

    /**
     * Fixes the BFS differentially.
     * This is called from ContinuousDiffBFSShortestPathPlan.execute()
     */
    public void executeDifferentialBFS() {
        distancesR.clear();

        for (var eEntries : Graph.INSTANCE.edgeDiffs.entrySet()) {
            var eDiffSource = eEntries.getKey();
            var rDiffs = distancesR.getMergedDiffs(eDiffSource);
            for (var eEntry : eEntries.getValue().entrySet()) {
                var eDiffDest = eEntry.getKey();
                if (eDiffDest == source) {
                    return;
                }
                int limit = rDiffs[0] * 2;
                for (int j = 1; j <= limit; j += 2) {
                    verticesToFix.addVToFix(eDiffDest, (short) (rDiffs[j] + 1));
                }
            }
        }

        if (verticesToFix.isEmpty) {
            //System.out.println("size-end = "+ distances.deltaDiffs.size());
            distancesR.mergeDeltaDiffs();
            //System.out.println("size-end2 = "+ distances.deltaDiffs.size());
            return;
        }

        for (short t = 1; t <= distancesR.latestIteration; t++) {
            //Report.INSTANCE.debug("---- Iteration = " + t);
            if (verticesToFix.iterVPairsList.size() > t && !verticesToFix.getItemFromIterVPairsList(t).isEmpty()) {

                for (int vertexToFixAtT : verticesToFix.getItemFromIterVPairsList(t)) {

                    //Report.INSTANCE.debug("** fix vertex %d and look for neighbors", vertexToFixAtT);

                    // this is only used if we are doing backtracking!
                    //                if (shortestPath.contains(vertexToFixAtT)) {
                    //                    didShortestPathChange = true;
                    //                }

                    fixVertexAndAddNewVerticesToFix(vertexToFixAtT, t);
                }
            }
            distancesR.print();
        }

        if (!distancesR.lastDiffs.isEmpty() && !shouldStopEarly(distancesR.latestIteration)) {
            distancesR.latestIteration--;
            continueBFS();
        }

        mergeDeltaDiff();
    }

    public abstract void updateNbrsDistance(int currentVertexId, long currentDistance, int neighbourId,
                                            long neighbourWeight, short currentIterationNo);


    public void fixVertexAndAddNewVerticesToFix(int vertexToFix, short currentFixedIter) {

        if (vertexToFix == this.source) {
            // We assume there are no negative weight cycles. So the source's
            // distance can never be updated.
            return;
        }

        long oldDistanceAtFixedIter =
                distancesR.getDistance(vertexToFix, currentFixedIter, false /* do not consider current batch*/);
        long distanceAtPreviousIter = distancesR.getDistance(vertexToFix, (short) (currentFixedIter - 1), true);
        long newValue = distancesR.recalculateDistance(vertexToFix, currentFixedIter, true);

        if (oldDistanceAtFixedIter != newValue) {
            // Update self if in-neighbours were updated after currentIter
            // This is important because if the vertex became unreachable or worse, we need to revisit it again
            // in iterations after its in-neighbours were updated

            if (newValue < distanceAtPreviousIter) {
                // set the new distance
                distancesR.setVertexDistance(vertexToFix, currentFixedIter, newValue);
            }
            fixSelfIfNecessary(vertexToFix, currentFixedIter);
            fixSelfInFutureIterations(vertexToFix, currentFixedIter);
            // Fix out neighbours
            fixOutNeighbours(vertexToFix, currentFixedIter, oldDistanceAtFixedIter, newValue);
        }

        // if this iteration does not enhance the distance of this vertex
        // remove this iteration if exist
        if (newValue >= distanceAtPreviousIter) {
            // this might mean that vertex is now unreachable
            distancesR.clearVertexDistanceAtT(vertexToFix, currentFixedIter);
        }
    }

    public void fixOutNeighbours(int vertexToFix, short currentFixedIter, long vertexOldDistance,
                                 long vertexNewDistance) {
        SortedAdjacencyList outComingAdjList = NewUnidirectionalDifferentialBFS
                .staticGetOutNeighbours(vertexToFix, true, direction, (short) (currentFixedIter + 1));

        var distances = distancesR.getMergedDiffs(vertexToFix);
        int limit = distances[0] * 2;

        for (int i = 0; i < outComingAdjList.getSize(); i++) {
            int out = outComingAdjList.getNeighbourId(i);
            long outDistance = distancesR.getDistance(out, (short) (currentFixedIter + 1), false);

            if (outDistance >= vertexOldDistance || outDistance >= vertexNewDistance) {
                addVFORFix(out, (short) (currentFixedIter + 1));
            }

            for (int j = 1; j <= limit; j += 2) {
                if (distances[j] > currentFixedIter) {
                    addVFORFix(out, (short) (distances[j] + 1));
                }
            }
        }
    }

    public void fixSelfInFutureIterations(int vertexToFix, short currentFixedIter) {
        // check if in-neighbours were updated after current iteration
        // if yes, then we should add current vertex to be fixed too.

        var distances = distancesR.getMergedDiffs(vertexToFix);
        int limit = distances[0] * 2;
        for (int j = 1; j <= limit; j += 2) {
            if (distances[j] > currentFixedIter) {
                //Report.INSTANCE.error("------ addVToFix v="+vertexToFix+" because it was fixed on a previous iteration :"+currentFixedIter);
                addVFORFix(vertexToFix, (short) distances[j]);
            }
        }
    }


    public void fixSelfIfNecessary(int vertexToFix, short currentFixedIter) {
        // check if in-neighbours were updated after current iteration
        // if yes, then we should add current vertex to be fixed too.

        Set<Short> toBeFixedIterations = new HashSet<>();

        SortedAdjacencyList incomingAdjList =
                NewUnidirectionalDifferentialBFS.staticGetInNeighbours(vertexToFix, true, direction, (short) (-1));

        for (int in_nbr = 0; in_nbr < incomingAdjList.getSize(); in_nbr++) {
            if (incomingAdjList.neighbourIds[in_nbr] < 0) {
                continue;
            }

            var distances = distancesR.getMergedDiffs(incomingAdjList.neighbourIds[in_nbr]);
            int limit = distances[0] * 2;
            for (int j = 1; j <= limit; j += 2) {
                if (distances[j] >= currentFixedIter) {
                    //Report.INSTANCE.error("------ addVToFix v="+vertexToFix+" because its incoming neighbor was changed in the future org_v "+incomingAdjList.neighbourIds[in_nbr]+ " with iteration:"+iter);
                    toBeFixedIterations.add((short) (distances[j] + 1));
                }
            }
        }

        for (Short iter : toBeFixedIterations) {
            addVFORFix(vertexToFix, iter);
        }
    }

    public void addVFORFix(int vertexForFix, short iterationForFix) {

        if (vertexForFix == source) {
            return;
        }

        if (null != verticesToFix.getItemFromIterVPairsList(iterationForFix)) {
            if (!verticesToFix.getItemFromIterVPairsList(iterationForFix).contains(vertexForFix)) {
                verticesToFix.addVToFix(vertexForFix /* vToFix */, iterationForFix /* iterationNo */);
            }
        }
    }

    protected abstract boolean shouldSkipEdge(int vertex, short eType);

    public long getSrcDstDistance() {
        return distancesR.getLatestDistance(destination);
    }

    /**
     * Returns the latest distance of each vertex in the graph as a
     * double array.
     */
    public double[] getDistancesAsArray() {
        double[] temp = new double[Graph.INSTANCE.getHighestVertexId() + 1];
        for (int i = 0; i <= Graph.INSTANCE.getHighestVertexId(); i++) {
            temp[i] = distancesR.getLatestDistance(i);
        }
        return temp;
    }
}
