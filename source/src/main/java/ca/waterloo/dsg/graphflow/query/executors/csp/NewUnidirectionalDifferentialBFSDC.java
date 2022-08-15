package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.Graph.Direction;
import ca.waterloo.dsg.graphflow.util.Report;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Base class for different implementations of a unidirectional differential BFS.
 */
public abstract class NewUnidirectionalDifferentialBFSDC implements DifferentialBFS {
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
    protected DistancesDC distancesJ;
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
    public NewUnidirectionalDifferentialBFSDC(int queryId, int source, int destination, Direction direction) {

//        Report.INSTANCE
//                .debug("------ Initialize NewUnidirectionalDifferentialBFS query " + source + " -> " + direction);

        this.queryId = queryId;
        this.source = source;
        this.destination = destination;
        this.direction = direction;
        verticesToFix = new VerticesToFix();
        this.shortestPath = new ShortestPath(source, destination);
        this.didShortestPathChange = false;

        initFrontierAndSourceDistance(queryId);
    }

    public int getQueryId() {
        return queryId;
    }

    public int getSetVertexChangeNumbers()
    {
        return 0;
    }
    public int getMaxIteration(){
        return distancesR.latestIteration;
    }
    public void copyDiffs(DifferentialBFS initDiff) {
        distancesJ.copyDiffs(((NewUnidirectionalDifferentialBFS) initDiff).distances);
        distancesR.copyDiffs(((NewUnidirectionalDifferentialBFS) initDiff).distances);
    }

    // dummy function required by interface and used by Landmark
    public void preProcessing(int batchNumber) {
    }

    @Override
    public void mergeDeltaDiff() {
        distancesR.mergeDeltaDiffs();
        distancesJ.mergeDeltaDiffs();
        //Report.INSTANCE.debug("Diffs after batch:");
        distancesJ.print();
        distancesR.print();
    }


    public void printDiffs() {
        distancesR.print();
    }


    public void printStats() {
        distancesR.printStats();
        distancesJ.printStats();

        Map<Integer,Integer> degreeCount = new HashMap<Integer,Integer> (1);
        Map<Integer,Integer> degreeTotal = new HashMap<Integer,Integer> (1);
        Map<Integer,Float> degreeAvg = new HashMap<Integer,Float> (1);
        int degree;

        int stateType = Distances.VertexStats.DIFF_SIZE;
        for (var e : distancesR.vertexHistory.entrySet()) {
            degree = Graph.INSTANCE.getVertexDegree(e.getKey(), Graph.Direction.BACKWARD);
            degreeCount.put(degree, degreeCount.getOrDefault(degree, 0) + 1);
            degreeTotal.put(degree,
                    degreeTotal.getOrDefault(degree, 0) + e.getValue().getValue(stateType));
        }

        for (var e : distancesJ.vertexHistory.entrySet()) {
            degree = Graph.INSTANCE.getVertexDegree(e.getKey(), Graph.Direction.BACKWARD);
            degreeCount.put(degree, degreeCount.getOrDefault(degree, 1));
            degreeTotal.put(degree,
                    degreeTotal.getOrDefault(degree, 0) + e.getValue().getValue(stateType));
        }

        for (var d:degreeCount.keySet()){
            if (degreeTotal.get(d) > 0)
                degreeAvg.put(d,(float) degreeTotal.get(d)/degreeCount.get(d));
        }

        Report.INSTANCE.error("DistanceSize " + degreeAvg);
    }

    public void printDiffs(Report.Level l) { //distances.print(l);
        System.out.println("WHY is this?");
        distancesR.printStats();
        distancesJ.printStats();
    }

    public int sizeOfDistances() {
        return distancesR.size() + distancesJ.size();
    }
    public int getNumberOfVertices() {
        HashSet<Integer> uniqueVertices = new HashSet<Integer>();
        uniqueVertices.addAll(distancesR.getVerticesWithDiff());
        uniqueVertices.addAll(distancesJ.getVerticesWithDiff());
        return uniqueVertices.size();
    }

    public int sizeOfDeltaDistances() {
        return distancesR.deltaSize() + distancesJ.deltaSize();
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

    abstract void initFrontierAndSourceDistance(int queryId);

    abstract boolean shouldStopEarly(short iteration);

    abstract void getNeighborsData(DistancesDC distancesJ, int vertexId, short iteration, long currentDistance,
                                   short diff, boolean shouldRetract);

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
        while (distancesR.lastDiffs.size() > 0) {
            distancesR.incrementIterationNoAndSetPreviousDiffs();
            distancesJ.lastDiffs.clear();
            //Report.INSTANCE.debug("---- Iteration = " + distancesR.latestIteration);
            specialOperation(distancesJ, distancesR.latestIteration);
            takeBFSStepAt(distancesR.latestIteration, true, false);
            if (shouldStopEarly(distancesR.latestIteration)) {
                break;
            }
        }
    }

    public void takeBFSStepAt(short iteration, boolean print, boolean isDiff) {

        // Look at last diffs of `reduce` and generate new diffs for `join`.
        for (var entry : distancesR.lastDiffsPrevious.entrySet()) {
            var currentVertexId = (int) entry.getKey();
            var values = entry.getValue();

            for (var vEntry : values.entrySet()) {
                var currentDistance = vEntry.getKey();
                var diff = vEntry.getValue();
                getNeighborsData(distancesJ, currentVertexId, iteration, currentDistance, diff, !isDiff);
            }
        }

        //System.out.println(distancesJ.lastDiffs);
        distancesJ.mergeLastDiffsIntoDelta(iteration);

        distancesR.lastDiffs = new HashMap<>();
        var frontier = distancesJ.lastDiffs.keySet();
        // For the diffs of `join`, compute the reduce diffs.
        for (var vertex : frontier) {
            operate(vertex, iteration);
        }
        for (int vertex : distancesR.toBeChecked) {
            if (!frontier.contains(vertex)) {
                // For vertices to be checked that were not already in the frontier.
                var previous = distancesJ.getOldDiffs(vertex);
                for (int i = 1; i <= previous[0] * 2; i += 2) {
                    if (previous[i] == iteration) {
                        operate(vertex, iteration);
                    }
                }
            }
        }

        distancesR.toBeChecked.addAll(frontier);
        //System.out.println(distancesR.lastDiffs);
        distancesR.mergeLastDiffsIntoDelta(iteration);
        if (print) {
            distancesJ.print();
            distancesR.print();
        }
    }

    protected void operate(int vertex, short iteration) {
        var current = distancesR.lastDiffs.getOrDefault(vertex, new HashMap<>());

        if (shouldNegateDiffs()) {
            // Get the previously emitted diffs and flip them.
            var previous = distancesR.getMergedDiffs(vertex);
            for (int i = 1; i <= previous[0] * 2; i += 2) {
                if (previous[i] > iteration) { // Add all until current iteration
                    break;
                }
                var diffs = distancesR.diffsStore.get(previous[i + 1]);
                for (int j = 1; j < diffs[0] * 5 + 1; j += 5) {
                    var distance = Distances.getDistanceFromArray(diffs, j);
                    var diff = (short) -diffs[j]; // flip
                    DistancesDC.mergeDiff(current, distance, diff);
                }
            }
        }
        var newValue = aggregateFunction(vertex, iteration, distancesJ);

        if (newValue != getVertexDefaultValue(vertex)) {
            DistancesDC.mergeDiff(current, newValue, (short) 1);
        }
        if (!current.isEmpty()) {
            distancesR.lastDiffs.put(vertex, current);
        }
    }

    protected long aggregateFunction(int vertex, short iteration, DistancesDC distancesJ){
        var jOutput = distancesJ.getMergedDiffs(vertex);
        long newValue = getVertexDefaultValue(vertex);
        for (int i = 1; i <= jOutput[0] * 2; i += 2) {
            if (jOutput[i] > iteration) { // Check all until current iteration
                break;
            }
            var diffs = distancesJ.diffsStore.get(jOutput[i + 1]);
            // @sidd, is there a bug here?
            // Why do we need to go through all distances from all iterations instead of taking the last one?
            // If the reason because we need to accumulate all diffs, then "distance" is not comparable to "newValue".
            for (int j = 1; j < diffs[0] * 5 + 1; j += 5) {
                long distance = Distances.getDistanceFromArray(diffs, j);
                if (distance < newValue) {
                    newValue = distance;
                }
            }
        }
        return newValue;
    }

    protected void specialOperation(DistancesDC distancesJ, short iteration) {
        // Should be overridden when needed, eg in PR
    }

    protected long getVertexDefaultValue(int vertex){
        return Long.MAX_VALUE;
    }

    protected boolean shouldNegateDiffs(){
        return true;
    }

    /**
     * Fixes the BFS differentially.
     * This is called from ContinuousDiffBFSShortestPathPlan.execute()
     */
    public void executeDifferentialBFS() {
        distancesJ.clear();
        distancesR.clear();

        // Check all iterations.
        for (short i = 1; i <= distancesR.latestIteration; i++) {
            //Report.INSTANCE.debug("---- Iteration = " + i);
            distancesJ.setPreviousDiffs();
            distancesR.setPreviousDiffs();
            for (var eEntries : Graph.INSTANCE.edgeDiffs.entrySet()) {
                processUpdate(i, eEntries);
            }
            // Then proceed normally.
            takeBFSStepAt(i, true, true);
        }

        if (!distancesR.lastDiffs.isEmpty() && !shouldStopEarly(distancesR.latestIteration)) {
            continueBFS();
        }

        mergeDeltaDiff();
    }

    protected void processUpdate(short iter, Map.Entry<Integer, Map<Integer, DistancesDC.Diff2>> eEntries) {
        var eDiffSource = eEntries.getKey();

        // For each update, check if `src` had a diff output from `reduce` in previous iteration.
        var rDiffs = distancesR.getDiffsAt(eDiffSource, (short) (iter - 1), false);
        if (rDiffs[0] == 0) {
            return;
        }

        // For each update, get diffs from previous iteration and compute new distances based on the update.
        for (var eEntry : eEntries.getValue().entrySet()) {
            var eDiffDest = eEntry.getKey();
            var eDiffs = eEntry.getValue();
            var x = distancesJ.lastDiffs.computeIfAbsent(eDiffDest, l -> new HashMap<>());

            for (int j = 0; j < eDiffs.count; j++) {
                if (shouldSkipEdge(iter, eDiffs.types[j])) {
                    continue;
                }
                long eDistance = (long) eDiffs.distances[j];
                var eDiff = eDiffs.diffs[j];
                for (int k = 1; k < rDiffs[0] * 5 + 1; k += 5) {
                    long rDistance = Distances.getDistanceFromArray(rDiffs, k);
                    var rDiff = rDiffs[k];
                    var newDist = eDistance + rDistance; // Use distance of `src` from previous iteration and new distance of `dest` from update.
                    short newDiff = (short) (eDiff * rDiff);
                    DistancesDC.mergeDiff(x, newDist, newDiff);
                }
            }
        }
    }

    protected abstract boolean shouldSkipEdge(short iteration, short eType);

    public long getSrcDstDistance() {
        return distancesR.getLatestDistance(destination);
    }

    /**
     * Returns the latest distance of each vertex in the graph as a
     * double array.
     */
    public long[] getDistancesAsArray() {
        long[] temp = new long[Graph.INSTANCE.getHighestVertexId() + 1];
        for (int i = 0; i <= Graph.INSTANCE.getHighestVertexId(); i++) {
            temp[i] = distancesR.getLatestDistance(i);
        }
        return temp;
    }
}
