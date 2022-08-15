package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.SortedAdjacencyList;
import ca.waterloo.dsg.graphflow.query.plans.ContinuousDiffBFSShortestPathPlan;
import ca.waterloo.dsg.graphflow.query.plans.ContinuousShortestPathPlan;
import ca.waterloo.dsg.graphflow.util.ArrayUtils;
import ca.waterloo.dsg.graphflow.util.Report;
import ca.waterloo.dsg.graphflow.util.VisibleForTesting;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

import java.util.*;
import java.util.Map.Entry;


/**
 * Represents a data structure that keeps track of the distances that each vertex took in different
 * iterations. Contains utility methods to add new distances or clear distances after some iteration
 * and keep track of the size of distances.
 */
public class Distances {
    protected final static short[] emptyList = new short[]{0, 0, 0, 0, 0, 0};
    // query id
    public static int numQueries = 0;
    public static boolean realDiffInitialized = false;
    public static boolean caching = false;
    public int setVertexDistanceCounter = 0;
    // (iter:short, distance:long) stored as 5 consecutive shorts in short[] array. Index 0 is always the count.
    public static Map<Integer, short[]> deltaDiffs = new HashMap<>();
    protected static MultiQueryDiff realDiff;
    protected static IntOpenHashSet toBeDeleted = new IntOpenHashSet();
    protected static WeakHashMap<Map.Entry<Integer, Short>, Long> newCache = new WeakHashMap<>(1);
    protected static WeakHashMap<Map.Entry<Integer, Short>, Long> oldCache = new WeakHashMap<>(1);
    public int recalculateNumber = 0;
    public Map<Integer,Integer> recalculateState = new HashMap<>(1);
    public NewUnidirectionalDifferentialBFS.Queries queryType;
    protected int queryId;
    @VisibleForTesting
    protected VertexIterationDistancePair[] minFrontierDistances = new VertexIterationDistancePair[100];
    // The frontier of the BFS. As an invariant the algorithm always maintains the frontier.
    protected Set<Integer> frontier = new HashSet<>();
    // Row-Key is the vertex ID
    // Column-Key is the iteration ID
    // value is the new distance
    //protected TreeBasedTable<Integer, Short, Double> deltaDiffs;
    protected Set<Integer> previousFrontier = new HashSet<>();
    protected boolean frontierReady = true;
    // This includes each vertex that we ever visited during a query
    // The pair keeps #of times it was added for fix, and the maximum distance size for this vertex.
    protected Map<Integer, VertexStats> vertexHistory = new HashMap<>();
    protected int nextFrontierSize = 0;
    protected Graph.Direction direction;
    // The latest iteration number. We maintain the invariant that the IntQueue frontier above
    // always contains the vertices in the frontier of the BFS at latestIteration. For example,
    // consider unweighted unidirectional BFS. If latest iteration is 2, then we ran the BFS for
    // 2 steps from the source and the vertices that are 2 steps from the source, we have their
    // distances set to 2 and all of them are in the frontier.
    protected short latestIteration = 0;
    protected int source, destination;
    protected IterationDistancePair pair1, pair2;
    protected static final long SIXM = 6000000;
    protected static final long ONEM = 1000000;
    protected static final int ONEK = 1000;

    /**
     * Default constructor with no source.
     */
    public Distances() {
        this(0, 0, 0);
    }

    /**
     * Default public constructor.
     */
    public Distances(int queryId, int source, int destination, NewUnidirectionalDifferentialBFS.Queries queryType) {
        this(queryId, source, destination, Graph.Direction.FORWARD, queryType);
    }

    public Distances(int queryId, int source, int destination) {
        this(queryId, source, destination, Graph.Direction.FORWARD);
    }

    public Distances(int queryId, int source, int destination, Graph.Direction d) {
        this(queryId, source, destination, d, null, NewUnidirectionalDifferentialBFS.Queries.NOT_SUPPORTED);
    }

    public Distances(int queryId, int source, int destination, Graph.Direction d, String type,
                     NewUnidirectionalDifferentialBFS.Queries queryType) {
        this.queryId = queryId;
        this.queryType = queryType;
        initializeRealDiff(type);
        if (type == null) {
            initializeRealDiff();
        } else {
            initializeRealDiff(type);
        }
        this.direction = d; // only used to detect the nextFrontierSize!
        this.source = source;
        this.destination = destination;

        if(queryType == NewUnidirectionalDifferentialBFS.Queries.PR)
            setVertexDistance((short) 0 /* iteration number */, SIXM /* initial PR */);
        else if (queryType == NewUnidirectionalDifferentialBFS.Queries.WCC)
            setVertexDistance((short) 0 /* iteration number */);
        else
            setVertexDistance(source, (short) 0 /* iteration number */, 0 /* distance */);

        pair1 = new IterationDistancePair((short) -1, Long.MAX_VALUE);
        pair2 = new IterationDistancePair((short) -1, Long.MAX_VALUE);
    }


    public Distances(int queryId, int source, int destination, Graph.Direction d,
                     NewUnidirectionalDifferentialBFS.Queries queryType) {
        this(queryId, source, destination, d, null, queryType);
    }

    public static void reset() {
        realDiffInitialized = false;
        deltaDiffs.clear();
        toBeDeleted.clear();
        newCache.clear();
        oldCache.clear();
    }

    public static String distancesString(short[] distances) {
        StringBuilder out = new StringBuilder();
        int limit = (distances[0]) * 5 + 1;
        for (int i = 1; i < limit; i += 5) {
            short iter = distances[i];
            long distance = getDistanceFromArray(distances, i);
            out.append("(").append(iter).append(",").append(distance).append("),");
        }
        return out.toString();
    }

    /**
     * This function is used to allow for Dropped diffs to be created.
     */
    void initializeDroppedDiff(String type) {
        return;
    }

    void initializeDroppedDiff() {
        return;
    }

    public void initializeRecalculate() {
        System.out.println("---2----- initializeRecalculate : " + recalculateNumber);
        recalculateNumber = 0;
        setVertexDistanceCounter = 0;
        return;
    }

    void initializeRealDiff(String type) {
        initializeDroppedDiff(type);
        if (!realDiffInitialized) {
            realDiff = new MultiQueryDiff(numQueries + 1);
            realDiffInitialized = true;
        } else {
            return;
        }
    }

    void initializeRealDiff() {
        initializeRealDiff(null);
    }

    /**
     * Because Frontier could be expensive, we do not keep it around between iterations.
     * However, we might need to compute it again when needed.
     * This function gets the iteration that we need to compute the frontier at and rebuild it.
     */
    public void prepareFrontier(short iteration) {
        // No need to do any action if the frontier is already ready
        if (frontierReady) {
            return;
        }

        for (int v : getVerticesWithDiff()) {
            if (this.isDiffExist(queryId, v, iteration)) {
                addVtoFrontier(v);
            }
        }
        frontierReady = true;
    }

    public Set<Integer> getVerticesWithDiff() {

        IntOpenHashSet allV = new IntOpenHashSet();
        allV.addAll(realDiff.getVerticesWithDiff(queryId));
        allV.addAll(deltaDiffs.keySet());
        for (int delete : toBeDeleted) {
            allV.remove(delete);
        }

        return allV;
    }

    public boolean goodNeighbour(short type, short itr) {
        switch (queryType) {
            case Q1:
                return NewDifferentialQ1.staticGoodNeighbour(type, itr);

            case Q2:
                return NewDifferentialQ2.staticGoodNeighbour(type, itr);
            case Q7:
                return NewDifferentialQ7.staticGoodNeighbour(type, itr);
            case Q11:
                return NewDifferentialQ11.staticGoodNeighbour(type, itr);

            default:
                return NewUnidirectionalDifferentialBFS.staticGoodNeighbour(type, itr);
        }
    }

    public SortedAdjacencyList getInNeighbours(int vertex, boolean merged, short iteration) {
        switch (queryType) {
            case Q7:
                return NewDifferentialQ7.staticGetInNeighbours(vertex, merged, direction, iteration);
            case Q11:
                return NewDifferentialQ11.staticGetInNeighbours(vertex, merged, direction, iteration);

            default:
                return NewUnidirectionalDifferentialBFS.staticGetInNeighbours(vertex, merged, direction, iteration);
        }
    }

    public SortedAdjacencyList getOutNeighbours(int vertex, boolean merged, short iteration) {
        switch (queryType) {
            case Q7:
                return NewDifferentialQ7.staticGetOutNeighbours(vertex, merged, direction, iteration);
            case Q11:
                return NewDifferentialQ11.staticGetOutNeighbours(vertex, merged, direction, iteration);

            default:
                return NewUnidirectionalDifferentialBFS.staticGetOutNeighbours(vertex, merged, direction, iteration);
        }
    }

    /**
     * copy diffs from the initial query # 0
     *
     * @param iniDistance
     */
    public void copyDiffs(Distances iniDistance) {

        frontierReady = iniDistance.frontierReady;
        frontier = iniDistance.frontier;
        previousFrontier = iniDistance.previousFrontier;
        nextFrontierSize = iniDistance.nextFrontierSize;

        recalculateNumber = 0;
        recalculateState = new HashMap<>(1);
        latestIteration = iniDistance.latestIteration;
        minFrontierDistances = iniDistance.minFrontierDistances;
        prepareRealDiffs(0);
        vertexHistory = iniDistance.vertexHistory;

        //Report.INSTANCE.debug("size of copied before clear = %d", realDiff.size(0));
        realDiff.clear(0);
        //Report.INSTANCE.debug("size of copied after clear = %d", realDiff.size(0));
    }

    void prepareRealDiffs(int from) {
        realDiff.setCopiedDiffs(queryId, realDiff.getDiffs(from));
    }

    public void mergeDeltaDiffs() {
        // merge two lists
        realDiff.mergeDeltaDiffs(queryId, deltaDiffs, toBeDeleted);

        // Create vertex stats when needed
        if (Report.INSTANCE.appReportingLevel == Report.Level.INFO) {
            for (Integer v : deltaDiffs.keySet()) {
                VertexStats stats = vertexHistory.get(v);

                if (null == stats) {
                    stats = new VertexStats();
                    vertexHistory.put(v, stats);
                }
            }
        }

        deltaDiffs = new HashMap<>(1);
        toBeDeleted.clear();
        newCache.clear();
        oldCache.clear();
    }

    public void reInitialize() {
        this.realDiff.clear(queryId);
        this.deltaDiffs.clear();
        this.toBeDeleted.clear();
        this.frontier.clear();
        this.previousFrontier.clear();
        this.vertexHistory.clear();
        this.latestIteration = 0;
        minFrontierDistances = new VertexIterationDistancePair[100];
        nextFrontierSize = 0;
        setVertexDistance(source, (short) 0 /* iteration number */, 0 /* distance */);
        frontierReady = true;
    }

    /**
     * Update the number of times a vertex was fixed
     * @param vertexId
     */
    void countVertexFix(int vertexId) {
        VertexStats stats = vertexHistory.get(vertexId);
        if (null == stats) {
            stats = new VertexStats();
        }
        stats.addedToFix++;
        vertexHistory.put(vertexId,stats);
    }


    /**
     * Update the number of times a vertex was fixed
     * @param vertexId
     */
    void countLargestDiffSize(int vertexId, int size) {
        Distances.VertexStats stats = vertexHistory.get(vertexId);
        if (null == stats) {
            stats = new Distances.VertexStats();
        }
        if (size > stats.largestDiffSize ) {
            stats.largestDiffSize = size;
            vertexHistory.put(vertexId, stats);
        }
    }


    /**
     * Update the number of times a vertex was recalculated
     * @param vertexId
     */

    void countVertexRecalculate(int vertexId) {
        VertexStats stats = vertexHistory.get(vertexId);
        if (null == stats) {
            stats = new VertexStats();
        }
        stats.recalculated++;
        vertexHistory.put(vertexId,stats);
    }

    void countVertexChange(int vertexId) {
        VertexStats stats = vertexHistory.get(vertexId);
        if (null == stats) {
            stats = new VertexStats();
        }
        stats.valueChage++;
        vertexHistory.put(vertexId,stats);
    }

    /**
     * Increments the latestIteration, returns the current frontier, which now that
     * we incremented an iteration, will be the previous frontier, and starts an
     * empty frontier.
     *
     * @return the current frontier (which will be the previous frontier).
     */
    Set<Integer> incrementIterationNoAndGetPreviousFrontier() {

        //Report.INSTANCE.debug("** increase iteration number to " + (this.latestIteration + 1));

        this.latestIteration++;
        Set<Integer> tmp = frontier;
        frontier = previousFrontier;
        previousFrontier = tmp;
        frontier.clear();
        nextFrontierSize = 0;
        return previousFrontier;
    }

    /**
     * @return the current frontier.
     */
    Set<Integer> getCurrentFrontier() {
        return frontier;
    }

    int getCurrentFrintierSize() {
        return frontier.size();
    }

    int getNextFrontierSize() {
        return nextFrontierSize;
    }

    /**
     * @param iterationNo iteration number to set the latestIteration to.
     */
    void setLatestIterationNumber(short iterationNo) {

        //Report.INSTANCE.debug("**set Iteration number to " + iterationNo);

        this.latestIteration = iterationNo;
        clearDistancesAfterT(iterationNo);
        frontier.clear();
        nextFrontierSize = 0;
        // get all vertices that have diffs
        for (Integer vertex : getVerticesWithDiff()) {
            if (isDiffExist(queryId, vertex, iterationNo)) {
                addVtoFrontier(vertex);
                nextFrontierSize += Graph.getInstance().getVertexDegree(vertex, direction);
            }
        }
    }

    /**
     * Clears the distances that are greater than t but not including t.
     *
     * @param t iteration number. Distances that vertices took in later
     *          iterations than t will be removed.
     */
    void clearDistancesAfterT(short t) {
        List<Integer> verticesToRemove = new ArrayList<>();

        for (Integer vertex : this.getVerticesWithDiff()) {
            short[] iterDistPairs = getMergedDiffs(vertex);
            int len = iterDistPairs[0];
            int limit = ((len) * 5) + 1;
            int toRemove = 0;
            for (int j = limit - 5; j >= 1; j -= 5) {
                short iter = iterDistPairs[j];
                if (iter <= t) {
                    break;
                }
                toRemove++;
            }
            iterDistPairs[0] -= toRemove;
            if (iterDistPairs[0] == 0) {
                verticesToRemove.add(vertex);
            }
        }
        for (int vertexToRemove : verticesToRemove) {
            this.removeDiff(vertexToRemove);
        }
        for (int i = t + 1; i < minFrontierDistances.length; ++i) {
            minFrontierDistances[i] = null;
        }
    }

    /***
     * Check if a diff is useful by comparing its distance at iteration and at iteration-1
     * @param vertexId
     * @param iterationNo
     * @return
     */
    boolean isDiffUseful(int vertexId, short iterationNo) {
        if(queryType == NewUnidirectionalDifferentialBFS.Queries.PR)
            return isDiffUsefulPR(vertexId,iterationNo);
        if(queryType == NewUnidirectionalDifferentialBFS.Queries.WCC)
            return isDiffUsefulWCC(vertexId,iterationNo);

        long distanceCurrent = recalculateDistance(vertexId, iterationNo, true);
        return isDiffUseful(vertexId, iterationNo, distanceCurrent);
    }

    /***
     * Check if a diff is useful by comparing its distance at iteration and at iteration-1
     * @param vertexId
     * @param iterationNo
     * @param distanceCurrent
     * @return
     */
    boolean isDiffUseful(int vertexId, short iterationNo, long distanceCurrent) {
        if(queryType == NewUnidirectionalDifferentialBFS.Queries.PR)
            return isDiffUsefulPR(vertexId,iterationNo,distanceCurrent);
        if(queryType == NewUnidirectionalDifferentialBFS.Queries.WCC)
            return isDiffUsefulWCC(vertexId,iterationNo,distanceCurrent);

        long distancePrevious = getDistance(vertexId, (short) (iterationNo - 1), true);

        // check if this diff has an impact
        if (distanceCurrent < distancePrevious && Math.abs(distanceCurrent - distancePrevious) > 0.0001) {
            return true;
        } else {
            return false;
        }
    }

    void clearVertexDistanceAtT(int vertexId, short iterationNo, long newValue) {

        if (vertexId == source) {
            System.out.println(
                    "This is an error at clearVertexDistanceAtT - you cannot clear distance from source vertex = " +
                            vertexId);
            //System.exit(1); // cannot clear a diff from source
        }

        if (isDiffUseful(vertexId, iterationNo, newValue)) {
            return;
        }

        removeVertexDistance(vertexId, iterationNo);
        List<Short> iterations = getAllIterations(vertexId);
        for (short iter : iterations) {
            if (isDiffUseful(vertexId, iter)) {
                // if any diff is useful then return
                return;
            }
        }

        removeDiff(vertexId);

        if (DistancesWithDropBloom.debug(vertexId)) {
            Report.INSTANCE.error("=========---- clearVertexDistanceAtT v=" + vertexId + " iter= " + iterationNo +
                    " NewDistances = " + Arrays.toString(getMergedDiffs(vertexId)) + " vs OLD : " +
                    Arrays.toString(getOldDiffs(vertexId)));
        }
    }

    protected void removeVertexDistance(int vertexId, short iterationNo) {
        var distances = this.getMergedDiffs(vertexId);
        removeVertexDistance(vertexId, iterationNo, distances);
    }

    public static long getDistanceFromArray(short[] distances, int index) {
        long distance = distances[index + 4];
        for (int j = 3; j >= 1; j--) {
            distance = distance << 16;
            distance = distance | (distances[index + j] & 0xffff);
        }
        return distance;
    }

    public static void setDistanceToArray(short[] distances, long distance, int index) {
        distances[index + 1] = (short) (distance & 0xffff);
        distances[index + 2] = (short) ((distance >> 16) & 0xffff);
        distances[index + 3] = (short) ((distance >> 32) & 0xffff);
        distances[index + 4] = (short) ((distance >> 48) & 0xffff);
    }

    public static void insertDistanceToArray(int vertexId, short[] distances, long distance, short iterationNo, int len, int limit, int indexToAdd) {
        if (((len + 1) * 5) + 1 > distances.length) {
            var distances2 = new short[((len + 2) * 5) + 1];
            distances2[0] = (short) (len + 1);
            System.arraycopy(distances, 1, distances2, 1, indexToAdd - 1);
            distances2[indexToAdd] = iterationNo;
            setDistanceToArray(distances2, distance, indexToAdd);
            System.arraycopy(distances, indexToAdd, distances2, indexToAdd + 5, limit - indexToAdd);
            deltaDiffs.put(vertexId, distances2);
        } else {
            if (indexToAdd <= limit) {
                System.arraycopy(distances, indexToAdd, distances, indexToAdd + 5, limit - indexToAdd);
            }
            distances[0]++;
            distances[indexToAdd] = iterationNo;
            setDistanceToArray(distances, distance, indexToAdd);
        }
    }

    protected short[] removeVertexDistance(int vertexId, int iterationNo, short[] distances) {
        int indexToRemove = -1;

        int len = distances[0];
        int limit = ((len) * 5) + 1;
        short iter = -1;
        long distance = -1;
        for (int i = 1; i < limit; i += 5) {
            iter = distances[i];
            if (iter == iterationNo) {
                indexToRemove = i;
                distance = getDistanceFromArray(distances, i);
                break;
            } else if (iter > iterationNo) {
                break;
            }
        }
        if (indexToRemove >= 0) {
            if (!deltaDiffs.containsKey(vertexId)) {
                distances = distances.clone();
                deltaDiffs.put(vertexId, distances);
            }
            if ((indexToRemove + 5) != limit) {
                System.arraycopy(distances, indexToRemove + 5, distances, indexToRemove, limit - indexToRemove - 5);
            }
            distances[0]--;
            updateMinFrontierDistancesIfNecessary(vertexId, iter, distance, true /* isDeletion */);
        }
        return distances;
    }

    public void removeDiff(int v) {
        toBeDeleted.add(v);
    }

    public short[] getMergedDiffs(Integer v) {
        return getMergedDiffs(queryId, v);
    }

    public short[] getMergedDiffsCopiedIfReal(Integer v) {
        return getMergedDiffsCopiedIfReal(queryId, v);
    }

    public short[] getMergedDiffs(int q, Integer v) {
        if (deltaDiffs.containsKey(v)) {
            return deltaDiffs.get(v);
        } else if (toBeDeleted.contains(v.intValue())) {
            emptyList[0] = 0;
            return emptyList;
        } else {
            return realDiff.getDiffs(q, v);
        }
    }

    public short[] getMergedDiffsCopiedIfReal(int q, Integer v) {
        if (deltaDiffs.containsKey(v)) {
            return deltaDiffs.get(v);
        } else if (toBeDeleted.contains(v.intValue())) {
            emptyList[0] = 0;
            return emptyList;
        } else {
            var distances = realDiff.getDiffs(q, v).clone();
            deltaDiffs.put(v, distances);
            return distances;
        }
    }

    public short[] getOldDiffs(int v) {

        if (DistancesWithDropBloom.debug(v)) {
            if (realDiff.containsVertex(queryId, v)) {
                Report.INSTANCE.error("---- getOldDiffs v=" + v + " distances = " +
                                Distances.distancesString(realDiff.getDiffs(queryId, v)));
            } else {
                Report.INSTANCE.error("---- getOldDiffs v=" + v + " No distances! ");
            }
        }

        return realDiff.getDiffs(queryId, v);
    }

    public void addVtoFrontier(int vertexId){
        if ((queryType!= NewUnidirectionalDifferentialBFS.Queries.Landmark_SPSP && queryType != NewUnidirectionalDifferentialBFS.Queries.Landmark_W_SPSP) ||
                (queryType== NewUnidirectionalDifferentialBFS.Queries.Landmark_SPSP && LandmarkUnidirectionalUnweightedDifferentialBFS.canAddVtoFrontier(vertexId)) ||
                (queryType== NewUnidirectionalDifferentialBFS.Queries.Landmark_W_SPSP && LandmarkUnidirectionalWeightedDifferentialBFS.canAddVtoFrontier(vertexId))
        )
            frontier.add(vertexId);
    }

    /**
     * @param vertexId  ID of a vertex.
     * @param iteration iteration number for which to update the distance
     * @param distance  distance of the given vertex to the source in the given iteration number.
     */
    void setVertexDistance(int vertexId, short iterationNo, long distance) {

        setVertexDistanceCounter++;
        countVertexChange(vertexId);
        //Graph.INSTANCE.getVertexFWDDegree();
        Report.INSTANCE
                .error("---- setVertexDistance v=" + vertexId + " iter= " + iterationNo
                        + " FWDdegree= " + Graph.INSTANCE.getForwardMergedAdjacencyList(vertexId).getSize()
                        + " BWDdegree= " + Graph.INSTANCE.getBackwardMergedAdjacencyList(vertexId).getSize());
        Report.INSTANCE
                .error("---- setVertexDistance v=" + vertexId + " iter= " + iterationNo
                        + " FWDdegree= " + Graph.INSTANCE.getVertexFWDDegree(vertexId)
                        + " BWDdegree= " + Graph.INSTANCE.getVertexBWDDegree(vertexId));
        // if we are going to add a new diff for this distance, but previously remove it we should not remember that
        // any more because vertices in this set will be deleted during merge time.
        toBeDeleted.remove(vertexId);

        if (DistancesWithDropBloom.debug(vertexId)) {
            Report.INSTANCE
                    .error("---- setVertexDistance v=" + vertexId + " iter= " + iterationNo + " distance= " + distance);
        }

        if (iterationNo == latestIteration && Long.MAX_VALUE != distance) {
            addVtoFrontier(vertexId);

            nextFrontierSize += Graph.getInstance().getVertexDegree(vertexId, direction);

            //Report.INSTANCE.debug("** Adding vertex " + vertexId + " with direction " + direction +
            //        " to make nextFrontier size = " + nextFrontierSize);
        }

        short[] distances = getMergedDiffsCopiedIfReal(vertexId);

        int len = distances[0];
        int limit = ((len) * 5) + 1;
        int indexToAdd = 1;
        while (indexToAdd < limit) {
            short iter = distances[indexToAdd];

            if (iter == iterationNo) {
                setDistanceToArray(distances, distance, indexToAdd);
                updateMinFrontierDistancesIfNecessary(vertexId, iter, distance, false); // not a deletion
                return;
            } else if (iter > iterationNo) {
                break;
            }
            indexToAdd += 5;
        }

        insertDistanceToArray(vertexId, distances, distance, iterationNo, len, limit, indexToAdd);
        updateMinFrontierDistancesIfNecessary(vertexId, iterationNo, distance, false /* is not deletion */);
        if (Report.INSTANCE.appReportingLevel == Report.Level.DEBUG)
            countLargestDiffSize(vertexId, distances[0]);
    }

    /**
     * Compare two PR values and return true if they are similar to each other
     *
     * @param oldPR
     * @param newPR
     * @return
     */
    public boolean samePR(long oldPR, long newPR){
        if (Math.abs(oldPR-newPR)<=ONEK)
            return true;
        else
            return false;
    }

    /***
     * Check if a diff is useful by comparing its distance at iteration and at iteration-1
     * @param vertexId
     * @param iterationNo
     * @return
     */
    boolean isDiffUsefulPR(int vertexId, short iterationNo) {
        long distanceCurrent = recalculateDistance(vertexId, iterationNo, true);
        return isDiffUseful(vertexId, iterationNo, distanceCurrent);
    }

    /***
     * Check if a diff is useful by comparing its distance at iteration and at iteration-1
     * @param vertexId
     * @param iterationNo
     * @param distanceCurrent
     * @return
     */
    boolean isDiffUsefulPR(int vertexId, short iterationNo, long distanceCurrent) {
        long distancePrevious = getDistance(vertexId, (short) (iterationNo - 1), true);

        // check if this diff has an impact
        if (samePR(distanceCurrent, distancePrevious)) {
            return false;
        } else {
            return true;
        }
    }

    /***
     * Check if a diff is useful by comparing its distance at iteration and at iteration-1
     * @param vertexId
     * @param iterationNo
     * @return
     */
    boolean isDiffUsefulWCC(int vertexId, short iterationNo) {
        if(iterationNo ==0)
            return true;
        long distanceCurrent = recalculateDistance(vertexId, iterationNo, true);
        return isDiffUsefulWCC(vertexId, iterationNo, distanceCurrent);
    }

    /***
     * Check if a diff is useful by comparing its distance at iteration and at iteration-1
     * @param vertexId
     * @param iterationNo
     * @param distanceCurrent
     * @return
     */
    boolean isDiffUsefulWCC(int vertexId, short iterationNo, long distanceCurrent) {
        if(iterationNo ==0)
            return true;

        long distancePrevious = getDistance(vertexId, (short) (iterationNo - 1), true);

        // check if this diff has an impact
        if (distanceCurrent >= distancePrevious) {
            return false;
        } else {
            return true;
        }
    }



    /**
     * This function is used to set the initial value for vertices in case of PR
     *
     * @param iterationNo iteration number for which to update the distance
     * @param distance  distance of the given vertex to the source in the given iteration number.
     */
    void setVertexDistance(short iterationNo, long distance) {

        // This is probably not the best way to initialize the distance of all vertices but it would do!
        for (int vertex=0; vertex <= Graph.INSTANCE.getHighestVertexId(); vertex++)
            setVertexDistance(vertex, iterationNo, distance);
    }

    /**
     * This function is used to set the initial value for vertices in case of WCC.
     * In this case, we will use the id of each vertex as its initial label (distance)
     *
     * @param iterationNo iteration number for which to update the distance
     *
     */
    void setVertexDistance(short iterationNo) {
        // This is probably not the best way to initialize the distance of all vertices but it would do!
        for (int vertex=0; vertex <= Graph.INSTANCE.getHighestVertexId(); vertex++)
            setVertexDistance(vertex, iterationNo, (long) vertex);
    }



    /**
     * This function is needed if a vertex's distance increase due to changing in weight or a delete
     * At the moment, we will not support these cases using bloom filters
     */
    /**
     * Checks whether or not the min distance vertex needs to change in the frontier of the iteration,
     * in which the given update to the distance of a vertex is happening.
     *
     * @param vertexId     ID of the vertex that is being updated.
     * @param iterDistPair the iteration number and the distance of the vertex.
     * @param isDeletion   whether or not the iterDistPair is being deleted for the vertex.
     */
    protected void updateMinFrontierDistancesIfNecessary(int vertexId, Diff iterDistPair, boolean isDeletion) {

        updateMinFrontierDistancesIfNecessary(vertexId, iterDistPair.iterationNo,
                ((IterationDistancePair) iterDistPair).distance, isDeletion);
        /*
        short iterationNo = iterDistPair.iterationNo;
        if (isDeletion) {
            VertexIterationDistancePair currentMinVIterDistPair = minFrontierDistances[iterationNo];
            // Warning: We don't explicitly check it but currentMinVIterDistPair has to be non null
            // because if we are deleting the distance of a vertex in a particular iteration i,
            // there must be at least one vertex in the frontier for iteration i. Therefore there must
            // be a vertex with the minimum distance in that frontier.
            if (vertexId == currentMinVIterDistPair.vertexId) {
                findNewMinDistanceVertexInFrontier(iterDistPair.iterationNo);
            }
        } else {
            if (iterationNo + 1 > minFrontierDistances.length) {
                minFrontierDistances = (VertexIterationDistancePair[]) ArrayUtils
                        .resizeIfNecessary(minFrontierDistances, iterationNo + 1);
            }

            double pairDistance = getDistance(vertexId,iterationNo,true);
            double minFrontierDistance = (minFrontierDistances!=null && minFrontierDistances.length>iterationNo && minFrontierDistances[iterationNo]!= null )?
                    ((IterationDistancePair) minFrontierDistances[iterationNo].iterDistPair).distance: Double.MAX_VALUE;

            if (minFrontierDistances[iterationNo] == null || pairDistance < minFrontierDistance) {
                minFrontierDistances[iterationNo] = new VertexIterationDistancePair(vertexId, new IterationDistancePair(iterationNo,pairDistance));
            } else {
                VertexIterationDistancePair currentMinVIterDistPair = minFrontierDistances[iterationNo];
                if (vertexId == currentMinVIterDistPair.vertexId) {
                    if (pairDistance > ((IterationDistancePair) currentMinVIterDistPair.iterDistPair).distance) {
                        findNewMinDistanceVertexInFrontier(iterDistPair.iterationNo);
                    }
                }
            }
        }

         */
    }

    /**
     * This function is needed if a vertex's distance increase due to changing in weight or a delete
     * At the moment, we will not support these cases using bloom filters
     */
    /**
     * Checks whether or not the min distance vertex needs to change in the frontier of the iteration,
     * in which the given update to the distance of a vertex is happening.
     *
     * @param vertexId     ID of the vertex that is being updated.
     * @param iterDistPair the iteration number and the distance of the vertex.
     * @param isDeletion   whether or not the iterDistPair is being deleted for the vertex.
     */
    protected void updateMinFrontierDistancesIfNecessary(int vertexId, short iterationNo, long distance,
                                                         boolean isDeletion) {

        if (isDeletion) {
            VertexIterationDistancePair currentMinVIterDistPair = minFrontierDistances[iterationNo];
            // Warning: We don't explicitly check it but currentMinVIterDistPair has to be non null
            // because if we are deleting the distance of a vertex in a particular iteration i,
            // there must be at least one vertex in the frontier for iteration i. Therefore there must
            // be a vertex with the minimum distance in that frontier.
            if (vertexId == currentMinVIterDistPair.vertexId) {
                findNewMinDistanceVertexInFrontier(iterationNo);
            }
        } else {
            if (iterationNo + 1 > minFrontierDistances.length) {
                minFrontierDistances = (VertexIterationDistancePair[]) ArrayUtils
                        .resizeIfNecessary(minFrontierDistances, iterationNo + 1);
            }

            long pairDistance = distance;
            long minFrontierDistance = (minFrontierDistances != null && minFrontierDistances.length > iterationNo &&
                    minFrontierDistances[iterationNo] != null) ?
                    minFrontierDistances[iterationNo].iterDistPair.distance :
                    Long.MAX_VALUE;

            if (minFrontierDistances[iterationNo] == null || pairDistance < minFrontierDistance) {
                minFrontierDistances[iterationNo] =
                        new VertexIterationDistancePair(vertexId, new IterationDistancePair(iterationNo, pairDistance));
            } else {
                VertexIterationDistancePair currentMinVIterDistPair = minFrontierDistances[iterationNo];
                if (vertexId == currentMinVIterDistPair.vertexId) {
                    if (pairDistance > currentMinVIterDistPair.iterDistPair.distance) {
                        findNewMinDistanceVertexInFrontier(iterationNo);
                    }
                }
            }
        }
    }


    boolean isDiffExist(int q, int vertex, short iteration) {
        if (toBeDeleted.contains(vertex)) {
            return false;
        }

        return isDiffExist(getMergedDiffs(q, vertex), iteration);
    }

    boolean isDiffExist(short[] distances, short iteration) {
        int limit = (distances[0] * 5) + 1;
        for (int i = 1; i < limit; i += 5) {
            if (distances[i] == iteration) {
                return true;
            }
        }
        return false;
    }

    /**
     * This function is needed if a vertex's distance increase due to changing in weight or a delete
     * At the moment, we will not support these cases using bloom filters
     *
     * @param iteration
     */
    protected void findNewMinDistanceVertexInFrontier(short iterationNo) {

        int minVertexId = -1;
        long minDistance = Long.MAX_VALUE;
        long vertexDist = 0;

        for (Integer vertex : getVerticesWithDiff()) {
            if (isDiffExist(queryId, vertex, iterationNo)) {
                vertexDist = getDistance(vertex, iterationNo, true);
            } else {
                continue;
            }

            if (DistancesWithDropBloom.debug(vertex)) {
                System.out.println(
                        "found new Min distance for vertex " + vertex + " @ " + iterationNo + " = " + vertexDist);
                System.out.println("current minimum distance " + minDistance);
            }
            if (vertexDist < minDistance) {
                minVertexId = vertex;
                minDistance = vertexDist;
            }
        }

        minFrontierDistances[iterationNo].vertexId = minVertexId;
        minFrontierDistances[iterationNo].iterDistPair.distance = minDistance;
    }

    /**
     * Clears all the distances of a vertex and adds a single {@link Object} for the
     * vertex with the given iterationNo and distance.
     * <p>
     * Note: This is a specialized method provided for unidirectional unweighted differential BFS.
     *
     * @param vertexId    ID of the vertex.
     * @param iterationNo iteration number.
     * @param distance    distance of the vertex from the source at the given iteration number.
     */
    void clearAndSetOnlyVertexDistance(int vertexId, short iterationNo, long distance) {

        //Report.INSTANCE.debug("------ clearAndSetOnlyVertexDistance = Set " + vertexId + " @ " + iterationNo + " to " + distance);
        short[] distances = getMergedDiffs(vertexId);//vIterDistPairMap.get(vertexId);
        int limit = (distances[0] * 5) + 1;
        for (int i = 1; i < limit; i += 5) {
            short iter = distances[i];
            long oldDist = getDistanceFromArray(distances, i);
            updateMinFrontierDistancesIfNecessary(vertexId, iter, oldDist, true /* isDeletion */);
        }
        if (!deltaDiffs.containsKey(vertexId)) {
            distances = distances.clone();
            deltaDiffs.put(vertexId, distances);
        }
        distances[0] = 0;
        setVertexDistance(vertexId, iterationNo, distance);
    }

    /**
     * @param vertexId ID of a vertex.
     * @return Distance of the vertex at the maximum known iteration. (Merged distances)
     */
    long getLatestDistance(int vertexId) {
        return getDistance(vertexId, latestIteration, true);
    }

    /**
     * @param vertexId ID of a vertex.
     * @return Distances the vertex took at different iterations. In other words, for
     * each iteration the distance of the vertex changed, the returned list contains one
     * {@link Object}.
     */
    short[] getAllDistances(int vertexId) {
        return this.getMergedDiffs(vertexId);  //vIterDistPairMap.get(vertexId);
    }


    /**
     * Very similar to getAllDistances but it returns a list of iterations instead
     *
     * @param vertexId ID of a vertex.
     * @return
     */
    List<Short> getAllIterations(int vertexId) {
        return getAllIterations(getMergedDiffs(vertexId));
    }

    List<Short> getAllIterations(short[] distances) {
        int len = distances[0];
        var result = new ArrayList<Short>(len);
        int limit = ((len) * 5) + 1;
        for (int i = 1; i < limit; i += 5) {
            short iter = distances[i];
            result.add(iter);
        }
        return result;
    }

    void resetPair1() {
        pair1.distance = Long.MAX_VALUE;
        pair1.iterationNo = (short) -1;
    }

    long getDistance(int vertexId, short iteration) {
        return getDistance(vertexId, iteration, true);
    }

    /*
    This function is only useful when we drop vertices using Bloom. When we check if a diff exist, it is possible that Bloom says yes while no diff found yet.
    In this case, the getDistance will call recalculateDistance and will use the vertex (withoutVertexId) to find the current distance.
    Then, it will not report it because it think that it already exist but it was dropped.
     */
    long getNewDistance(int vertexId, short iteration,
                        boolean newBatches /*get distances from old batches or the current one*/,
                        int withoutVertexId) {
        return getDistance(vertexId, iteration, newBatches);
    }

    /**
     * @param vertexId    ID of a vertex.
     * @param iterationNo iteration at which the distance of the given vertex should be returned.
     * @param old         has no impact on this version - it does not matter
     * @return Distance of the vertex at the given iteration number. Note that if the vertex's
     * distance was not updated in the given iterationNo, the distance in the latest iteration less
     * than iterationNo is returned.
     */
    long getDistance(int vertexId, short iteration,
                     boolean newBatches/*get distances from old batches or the current one*/) {

        long defaultValue = Long.MAX_VALUE;
        if (queryType == NewUnidirectionalDifferentialBFS.Queries.WCC)
            defaultValue = vertexId;

        if (queryType == NewUnidirectionalDifferentialBFS.Queries.PR)
            defaultValue = SIXM;

        if (DistancesWithDropBloom.debug(vertexId)) {
            Report.INSTANCE
                    .error("Distances.getDistance - v= " + vertexId + " i= " + iteration + " newBatch? " + newBatches);
        }

        short[] distances;
        if (newBatches) {
            distances = this.getMergedDiffs(vertexId); //vIterDistPairMap.get(vertexId);
        } else {
            distances = this.getOldDiffs(vertexId); //vIterDistPairMap.get(vertexId);
        }
        if (null == distances) {
            return defaultValue;
        } else {
            long latestDistance = defaultValue;
            int limit = ((distances[0]) * 5) + 1;
            for (int i = 1; i < limit; i += 5) {
                short iter = distances[i];
                if (iter > iteration) {
                    break;
                } else {
                    latestDistance = getDistanceFromArray(distances, i);
                }
            }

            //            if (DistancesWithDropBloom.debug(vertexId)) {
            //                Report.INSTANCE.error(
            //                        "** v = " + vertexId + " distances = " + Arrays.toString(distances.toArray()) + " distance @ " +
            //                                iterationNo + " = " + latestDistance);
            //            }

            return latestDistance;
        }
    }


    public long recalculateDistance(int vertexId, short iterationNo, boolean newBatches, int withoutVertexId) {

        if (caching) {
            var pairCheck = new HashMap.SimpleEntry<>(vertexId, iterationNo);
            if (newBatches) {
                if (newCache.containsKey(pairCheck)) {
                    return newCache.get(pairCheck);
                }
            } else {
                if (oldCache.containsKey(pairCheck)) {
                    return oldCache.get(pairCheck);
                }
            }
        }

        if (withoutVertexId == -1) {
            return recalculateDistance(vertexId, iterationNo, newBatches);
        }

        recalculateNumber++;
        if(Report.INSTANCE.appReportingLevel == Report.Level.DEBUG){
            countVertexRecalculate(vertexId);
            recalculateState.put(vertexId,recalculateState.getOrDefault(vertexId,0)+1);
        }

        //System.out.println("---2----- recalculateDistance : "+recalculateNumber);
        if (DistancesWithDropBloom.debug(vertexId)) {
            Report.INSTANCE
                    .error("---2----- recalculateDistance : v= " + vertexId + " i= " + iterationNo + " newBatches? " +
                            newBatches);
        }

        short previousIteration = (short) (iterationNo - 1);

        if (vertexId == source) {
            return 0;
        }

        //This is overhead of not keeping all diffs
        SortedAdjacencyList inNeighbours = getInNeighbours(vertexId, newBatches, iterationNo);

        long minFound = Long.MAX_VALUE;

        // loop over inNeighbours to find the minimum distance
        for (int i = 0; i < inNeighbours.getSize(); i++) {
            // if this neighbour should not be considered for this iteration because of the edge type, ignore it!
            if(!goodNeighbour(inNeighbours.getEdgeType(i),iterationNo))
                continue;

            Integer nbr = inNeighbours.neighbourIds[i];
            long w = (long) inNeighbours.getNeighbourWeight(i);

            // we do not want to use this nbr when we recalculate the distance
            if (withoutVertexId == nbr) {
                continue;
            }

            /* In this step, I am trying to know the most up-to-date distance of the beighbour
             * to compute the distance of self based on old/new edges between me and my in neighbours */

            long nbr_dist = this.getDistance(nbr, previousIteration,
                    newBatches /* I may want to use old or new batches based on the original request*/);

            if (DistancesWithDropBloom.debug(vertexId)) {
                System.out.println("*2* from " + nbr + " dist= " + nbr_dist);
            }

            if (minFound > (nbr_dist + w)) {
                minFound = nbr_dist + w;
            }
        }

        if (DistancesWithDropBloom.debug(vertexId)) {
            System.out.println("***2*** final reported dist @ " + iterationNo + " is " + minFound);
        }
        if (caching) {
            if (newBatches) {
                newCache.put(new HashMap.SimpleEntry<>(vertexId, iterationNo), minFound);
            } else {
                oldCache.put(new HashMap.SimpleEntry<>(vertexId, iterationNo), minFound);
            }
        }
        return minFound;
    }

    public long recalculateDistance(int vertexId, short iterationNo, boolean newBatches) {

        if (queryType == NewUnidirectionalDifferentialBFS.Queries.PR)
            return recalculatePR(vertexId,iterationNo,newBatches);
        else if (queryType == NewUnidirectionalDifferentialBFS.Queries.WCC)
            return recalculateWCC(vertexId,iterationNo,newBatches);

        Map.Entry<Integer, Short> pairCheck = new HashMap.SimpleEntry<>(vertexId, iterationNo);
        if (caching) {
            if (newBatches) {
                if (newCache.containsKey(pairCheck)) {
                    return newCache.get(pairCheck);
                }
            } else {
                if (oldCache.containsKey(pairCheck)) {
                    return oldCache.get(pairCheck);
                }
            }
        }

        recalculateNumber++;
        if(Report.INSTANCE.appReportingLevel == Report.Level.DEBUG){
            countVertexRecalculate(vertexId);
            recalculateState.put(vertexId,recalculateState.getOrDefault(vertexId,0)+1);
        }
        //System.out.println("-------- recalculateDistance : v= "+vertexId+ " i= "+iterationNo + " newBatches? "+newBatches);
        if (DistancesWithDropBloom.debug(vertexId)) {
            Report.INSTANCE
                    .error("-------- recalculateDistance : v= " + vertexId + " i= " + iterationNo + " newBatches? " +
                            newBatches);
        }

        short previousIteration = (short) (iterationNo - 1);

        if (vertexId == source) {
            return 0;
        }

        //This is overhead of not keeping all diffs
        SortedAdjacencyList inNeighbours = getInNeighbours(vertexId, newBatches, iterationNo);

        long minFound = Long.MAX_VALUE;

        // loop over inNeighbours to find the minimum distance
        for (int i = 0; i < inNeighbours.getSize(); i++) {
            // if this neighbour should not be considered for this iteration because of the edge type, ignore it!
            if(!goodNeighbour(inNeighbours.getEdgeType(i),iterationNo))
                continue;

            Integer nbr = inNeighbours.getNeighbourId(i);
            long w = (long) inNeighbours.getNeighbourWeight(i);


            /* In this step, I am trying to know the most up-to-date distance of the neighbour
             * to compute the distance of self based on old/new edges between me and my in neighbours */

            long nbr_dist = this.getDistance(nbr, previousIteration,
                    newBatches /* I may want to use old or new batches based on the original request*/);

            if (DistancesWithDropBloom.debug(nbr) || DistancesWithDropBloom.debug(vertexId)) {
                System.out.println(
                        "** " + i + " from " + nbr + " vertex " + vertexId + " is reached by nbr-dist= " + nbr_dist +
                                " new-distance= " + (nbr_dist + w) + " minFound = " + minFound);
            }

            if (nbr_dist != Long.MAX_VALUE && minFound > (nbr_dist + w)) {
                minFound = nbr_dist + w;
            }
        }

        /**
         * This should not happen, I need to report it and stop the experiment to make sure it is handeled.
         *
         *
         */

        /*
        This is alright when we use Bloom Filter
         *
        if(minFound == Double.MAX_VALUE && newBatches){

            System.out.println("ERROR: Vertex "+vertexId+" @ iteration "+iterationNo+" has a best distance as MAX_VALUE!");

            /**
             * This was added for debugging
             *


            if(direction == Graph.Direction.FORWARD)
                inNeighbours = Graph.getInstance().getBackwardMergedAdjacencyList(vertexId);
            else
                inNeighbours = Graph.getInstance().getForwardMergedAdjacencyList(vertexId);


            List<Diff> v_diffs =  this.getMergedDiffs(vertexId); //vIterDistPairMap.get(vertexId);
            String v_diffs_string = "[]";

            if(v_diffs!=null)
                v_diffs_string = Arrays.toString(v_diffs.toArray());

            System.out.println("*** Distances list = "+v_diffs_string);
            System.out.println("*** In neighbours list = "+Arrays.toString(inNeighbours.neighbourIds));

            for (int i=0;i<inNeighbours.neighbourIds.length;i++){

                int nbr_id = inNeighbours.neighbourIds[i];
                List<Diff> nbr_diff = this.getMergedDiffs(nbr_id);

                if(null != nbr_diff)
                    System.out.println("*** Nbr "+nbr_id+" with distances "+Arrays.toString(nbr_diff.toArray()));
                else
                    System.out.println("*** Nbr "+nbr_id+" with distances []");

                SortedAdjacencyList nbrInNeighbours;
                if(direction == Graph.Direction.FORWARD)
                    nbrInNeighbours = Graph.getInstance().getBackwardMergedAdjacencyList(inNeighbours.neighbourIds[i]);
                else
                    nbrInNeighbours = Graph.getInstance().getForwardMergedAdjacencyList(inNeighbours.neighbourIds[i]);


                System.out.println("***=== In neighbours list = "+Arrays.toString(nbrInNeighbours.neighbourIds));

                for (int j=0;j<nbrInNeighbours.neighbourIds.length;j++){

                    int nbr_id2 = nbrInNeighbours.neighbourIds[j];
                    List<Diff> nbr_Diff_2 = this.getMergedDiffs(nbr_id2);
                    if(null != nbr_Diff_2)
                        System.out.println("***====== Nbr "+nbr_Diff_2+" with distances "+Arrays.toString(nbr_Diff_2.toArray()) );
                    else
                        System.out.println("***====== Nbr "+nbr_Diff_2+" with distances []");

                }
            }
             *
            System.exit(1);
        }

         */

        if (DistancesWithDropBloom.debug(vertexId)) {
            System.out.println("****** final reported dist for " + vertexId + " @ " + iterationNo + " is " + minFound);
        }

        if (caching) {
            if (newBatches) {
                newCache.put(new HashMap.SimpleEntry<>(vertexId, iterationNo), minFound);
            } else {
                oldCache.put(new HashMap.SimpleEntry<>(vertexId, iterationNo), minFound);
            }
        }
        return minFound;
    }

    public long recalculateWCC(int vertexId, short iterationNo, boolean newBatches) {

        Map.Entry<Integer, Short> pairCheck = new HashMap.SimpleEntry<>(vertexId, iterationNo);
        if (caching) {
            if (newBatches) {
                if (newCache.containsKey(pairCheck)) {
                    return newCache.get(pairCheck);
                }
            } else {
                if (oldCache.containsKey(pairCheck)) {
                    return oldCache.get(pairCheck);
                }
            }
        }

        recalculateNumber++;
        if(Report.INSTANCE.appReportingLevel == Report.Level.DEBUG){
            countVertexRecalculate(vertexId);
            recalculateState.put(vertexId,recalculateState.getOrDefault(vertexId,0)+1);
        }
        //System.out.println("-------- recalculateDistance : v= "+vertexId+ " i= "+iterationNo + " newBatches? "+newBatches);
        if (DistancesWithDropBloom.debug(vertexId)) {
            Report.INSTANCE
                    .error("-------- recalculateDistanceWCC : v= " + vertexId + " i= " + iterationNo + " newBatches? " +
                            newBatches);
        }

        short previousIteration = (short) (iterationNo - 1);
        long label = vertexId;

        //This is overhead of not keeping all diffs
        SortedAdjacencyList neighbours = getInNeighbours(vertexId, newBatches, iterationNo);
        for (int i = 0; i < neighbours.getSize(); i++) {

            Integer nbr = neighbours.getNeighbourId(i);
            long nbr_dist = this.getDistance(nbr, previousIteration, newBatches /* I may want to use old or new batches based on the original request*/);

            if (DistancesWithDropBloom.debug(nbr) || DistancesWithDropBloom.debug(vertexId)) {
                System.out.println(
                        "** " + i + " from " + nbr + " vertex " + vertexId + " is reached by nbr-dist= " + nbr_dist +
                                " new-distance= " + (nbr_dist) + " label = " + label);

                if (DistancesWithDropBloom.debug(vertexId)){
                    System.out.println("Neighbour ( "+nbr+") = "+Distances.distancesString(getMergedDiffs(nbr)));
                }
            }

            if (nbr_dist < label) {
                label = nbr_dist;
            }
        }

        if (DistancesWithDropBloom.debug(vertexId)){
            System.out.println("**** Looking at out neighbours");
        }

        // Repeate for out-neighbours too (WCC looks at undirected version of the graph)
        neighbours = getOutNeighbours(vertexId, newBatches, iterationNo);
        for (int i = 0; i < neighbours.getSize(); i++) {

            Integer nbr = neighbours.getNeighbourId(i);
            long nbr_dist = this.getDistance(nbr, previousIteration, newBatches /* I may want to use old or new batches based on the original request*/);

            if (DistancesWithDropBloom.debug(nbr) || DistancesWithDropBloom.debug(vertexId)) {
                System.out.println(
                        "** " + i + " from " + nbr + " vertex " + vertexId + " is reached by nbr-dist= " + nbr_dist +
                                " new-distance= " + (nbr_dist) + " label = " + label);

                if (DistancesWithDropBloom.debug(vertexId)){
                    System.out.println("Neighbour ( "+nbr+") = "+Distances.distancesString(getMergedDiffs(nbr)));
                }
            }

            if (nbr_dist < label) {
                label = nbr_dist;
            }
        }

        if (DistancesWithDropBloom.debug(vertexId)) {
            System.out.println("****** final reported dist for " + vertexId + " @ " + iterationNo + " is " + label);
        }

        if (caching) {
            if (newBatches) {
                newCache.put(new HashMap.SimpleEntry<>(vertexId, iterationNo), label);
            } else {
                oldCache.put(new HashMap.SimpleEntry<>(vertexId, iterationNo), label);
            }
        }
        return label;

    }

    public long recalculatePR(int vertexId, short iterationNo, boolean newBatches) {

        if(iterationNo == 0)
            return SIXM;

        //if(vertexId == 0)
        //    System.out.println("recalculate PR for vertex "+vertexId+" on iteration "+iterationNo);

        Map.Entry<Integer, Short> pairCheck = new HashMap.SimpleEntry<>(vertexId, iterationNo);
        if (caching) {
            if (newBatches) {
                if (newCache.containsKey(pairCheck)) {
                    return newCache.get(pairCheck);
                }
            } else {
                if (oldCache.containsKey(pairCheck)) {
                    return oldCache.get(pairCheck);
                }
            }
        }

        recalculateNumber++;
        if(Report.INSTANCE.appReportingLevel == Report.Level.DEBUG){
            countVertexRecalculate(vertexId);
            recalculateState.put(vertexId,recalculateState.getOrDefault(vertexId,0)+1);
        }

        if (DistancesWithDropBloom.debug(vertexId)) {
            Report.INSTANCE
                    .error("-------- recalculateDistance : v= " + vertexId + " i= " + iterationNo + " newBatches? " +
                            newBatches);
        }

        short previousIteration = (short) (iterationNo - 1);

        //This is overhead of not keeping all diffs
        SortedAdjacencyList inNeighbours = getInNeighbours(vertexId, newBatches, iterationNo);

        long total = 0;
        // loop over inNeighbours to find the minimum distance
        for (int i = 0; i < inNeighbours.getSize(); i++) {

            Integer nbr = inNeighbours.getNeighbourId(i);
            int nbrDegree = Graph.getInstance().getForwardMergedAdjacencyList(nbr).getSize();

            /* In this step, I am trying to know the most up-to-date distance of the neighbour
             * to compute the distance of self based on old/new edges between me and my in neighbours */

            long nbr_dist = this.getDistance(nbr, previousIteration,
                    newBatches /* I may want to use old or new batches based on the original request*/);


            total += nbr_dist / nbrDegree;

            //if(vertexId==0){
            //    System.out.println("nbr = "+nbr+ " degree= "+nbrDegree+ " PR="+nbr_dist+ " total = "+total);
            //}
        }

        long pageRank = ONEM + 5 * total / 6;

        if (DistancesWithDropBloom.debug(vertexId)) {
            System.out.println("****** final reported dist for " + vertexId + " @ " + iterationNo + " is " + pageRank);
        }

        if (caching) {
            if (newBatches) {
                newCache.put(new HashMap.SimpleEntry<>(vertexId, iterationNo), pageRank);
            } else {
                oldCache.put(new HashMap.SimpleEntry<>(vertexId, iterationNo), pageRank);
            }
        }

        //if(vertexId == 0)
        //    System.out.println("recalculate PR for vertex "+vertexId+" on iteration "+iterationNo+ " = "+pageRank + " or "+ Math.round(pageRank));
        return pageRank;
    }


    /**
     * Request a histogram for a certain statistics about this query. It receives one of the following variables:
     * 1- Fix stats
     * 2- Max diff size
     * 3- Requested stats
     * 4- Recalculated stats
     *
     * @param stateType
     * @return
     */
    public Map<Integer,Float> getVertexStats(int stateType) {
        int degree = 0;
        Map<Integer,Integer> degreeCount = new HashMap<Integer,Integer> (1);
        Map<Integer,Integer> degreeTotal = new HashMap<Integer,Integer> (1);
        Map<Integer,Float> degreeAvg = new HashMap<Integer,Float> (1);

        for (var e : vertexHistory.entrySet()) {
            // For distance sizes, we are interested in in-degree
            if (stateType == VertexStats.DIFF_SIZE)
                degree = Graph.INSTANCE.getVertexDegree(e.getKey(), Graph.Direction.BACKWARD);
            else
                degree = Graph.INSTANCE.getVertexDegree(e.getKey(), Graph.Direction.FORWARD);
            degreeCount.put(degree, degreeCount.getOrDefault(degree, 0) + 1);
            degreeTotal.put(degree,
                    degreeTotal.getOrDefault(degree, 0) + e.getValue().getValue(stateType));
        }

        for (var d:degreeCount.keySet()){
            if (degreeTotal.get(d) > 0)
                degreeAvg.put(d,(float) degreeTotal.get(d)/degreeCount.get(d));
        }

        return degreeAvg;
    }

    /**
     * Request a histogram for a certain statistics about this query. It receives one of the following variables:
     * 1- Fix stats
     * 2- Max diff size
     * 3- Requested stats
     * 4- Recalculated stats
     *
     * @param stateType
     * @return
     */
    public Map<Integer,Integer> getVertexAbsoluteStats(int stateType) {
        int degree = 0;

        Map<Integer,Integer> degreeTotal = new HashMap<Integer,Integer> (1);

        for (var e : vertexHistory.entrySet()) {
            // For distance sizes, we are interested in in-degree
            if (stateType == VertexStats.DIFF_SIZE)
                degree = Graph.INSTANCE.getVertexDegree(e.getKey(), Graph.Direction.BACKWARD);
            else
                degree = Graph.INSTANCE.getVertexDegree(e.getKey(), Graph.Direction.FORWARD);

            degreeTotal.put(degree,
                    degreeTotal.getOrDefault(degree, 0) + e.getValue().getValue(stateType));
        }

        return degreeTotal;
    }

    public void printStats() {

        Report.INSTANCE.debug("======== Printing Diffs ===========");
        print();

        Report.INSTANCE.error("======== Printing Statistics ===========");
        Report.INSTANCE.error("======== # vertices " + vertexHistory.keySet().size() + " ==========");

        StringJoiner fixJoiner = new StringJoiner(",");
        StringJoiner sizeJoiner = new StringJoiner(",");
        int[] histogram_toFix = new int[1000000];
        int[] histogram_Diffs = new int[1000000];
        int max_fix = 0;
        int max_size = 0;

        for (Entry<Integer, VertexStats> iterDistPair : vertexHistory.entrySet()) {
            histogram_toFix[iterDistPair.getValue().addedToFix]++;
            histogram_Diffs[iterDistPair.getValue().largestDiffSize]++;

            if (iterDistPair.getValue().addedToFix > 1000) {
                System.out.println("**** v= " + iterDistPair.getKey() + " " + iterDistPair.getValue().addedToFix);
            }

            if (iterDistPair.getValue().addedToFix > max_fix) {
                max_fix = iterDistPair.getValue().addedToFix;
            }
            if (iterDistPair.getValue().largestDiffSize > max_size) {
                max_size = iterDistPair.getValue().largestDiffSize;
            }
        }

        for (int i = 0; i <= max_fix; i++) {
            fixJoiner.add(String.valueOf(histogram_toFix[i]));
        }

        for (int i = 0; i <= max_size; i++) {
            sizeJoiner.add(String.valueOf(histogram_Diffs[i]));
        }

        System.out.println("#Vertices " + vertexHistory.keySet().size());
        System.out.println("#Fix-Histogram " + fixJoiner.toString());
        System.out.println("#DiffSize-Histogram " + sizeJoiner.toString());

        Report.INSTANCE.error("=======================================");
        Report.INSTANCE.error("RecalculateStats " + getVertexStats(VertexStats.RECALCULATE_STATS));
        Report.INSTANCE.error("RecalculateStats - Absolute " + getVertexAbsoluteStats(VertexStats.RECALCULATE_STATS));
        Report.INSTANCE.error("=======================================");
        Report.INSTANCE.error("DistanceSize " + getVertexStats(VertexStats.DIFF_SIZE));
        Report.INSTANCE.error("DistanceSize - Absolute " + getVertexAbsoluteStats(VertexStats.DIFF_SIZE));
        Report.INSTANCE.error("=======================================");
        Report.INSTANCE.error("VertexChange " + getVertexStats(VertexStats.VertexChange_STATS));
        Report.INSTANCE.error("VertexChange - Absolute " + getVertexAbsoluteStats(VertexStats.VertexChange_STATS));
        Report.INSTANCE.error("=======================================");
        //Report.INSTANCE.error("         Histogram (#fix, maxSize) ");
        //for(int i=0;i<100;i++)
        //    if(histogram_toFix[i] > 0 || histogram_Diffs[i] > 0)
        //        Report.INSTANCE.error( i +" : "+ histogram_toFix[i]+ " - "+histogram_Diffs[i]);
        //Report.INSTANCE.error("\n=======================================");
    }

    public void print() {
        /*
        if (Report.INSTANCE.appReportingLevel == Report.Level.DEBUG) {
            Report.INSTANCE.debug("======== Printing distances ===========");

/*
            Report.INSTANCE.debug("DeltaDiffs:");
            for (var iterDistPair : deltaDiffs.entrySet()) {
                Report.INSTANCE.debug(iterDistPair.getKey() + " --> [" + distancesString(iterDistPair.getValue()) + "]");
            }
            Report.INSTANCE.debug("RealDiffs:");
            realDiff.print();


        }
         */
    }

    public int numberOfVerticesWithDiff() {
        return this.getVerticesWithDiff().size();
    }


    public int deltaSize() {
        int size = 0;
        for (var entry : deltaDiffs.entrySet()) {
            size += entry.getValue()[0];
        }
        return size;
    }

    public int size() {
        return realDiff.size(queryId);
    }

    /**
     * @return whether the frontier is empty or not.
     */
    boolean isFrontierEmpty(int iterationNo) {

        //System.out.println("Is frontier @ "+iterationNo+" empty? "+minFrontierDistances.length+ " -- "+minFrontierDistances[iterationNo]);
        return minFrontierDistances.length <= iterationNo || minFrontierDistances[iterationNo] == null;
    }


    /**
     * Keeps information about vertices for testing purposes
     */
    static class VertexStats {
        public static final int FIX_STATS = 1;
        public static final int DIFF_SIZE = 2;
        public static final int REQUEST_STATS = 3;
        public static final int RECALCULATE_STATS = 4;
        public static final int VertexChange_STATS = 5;


        int addedToFix; // tracks number of times a vertex was added to fix
        int largestDiffSize; // tracks the largest diff size of a vertex
        int valueRequested;  // tracks the number of times vertex value requested
        int recalculated;    // tracks the number of times vertex value was recalculated
        int valueChage;     // tracks the number of times vertex value has changed

        int getValue(int type){
            switch (type){
                case FIX_STATS: return addedToFix;
                case DIFF_SIZE: return largestDiffSize;
                case REQUEST_STATS: return valueRequested;
                case RECALCULATE_STATS: return recalculated;
                case VertexChange_STATS: return valueChage;
            }
            return -1;
        }
        VertexStats(int addedToFix, int largestDiffSize, int valueRequested, int recalculated, int valueChage) {
            this.addedToFix = addedToFix;
            this.largestDiffSize = largestDiffSize;
            this.valueRequested = valueRequested;
            this.recalculated = recalculated;
            this.valueChage = valueChage;
        }

        VertexStats() {
            this.addedToFix = 0;
            this.largestDiffSize = 0;
            this.valueRequested = 0;
            this.recalculated = 0;
            this.valueChage = 0;
        }

        @Override
        public String toString() {
            String str = "[addedToFix, largestDiffSize, valueRequested, recalculated, valueChage] = [" + addedToFix + " - " + largestDiffSize + " - " + valueRequested + " - " + recalculated + " - " + valueChage +"]";
            return str;
        }
    }


    static class Diff {
        short iterationNo;

        Diff(short iterationNo) {
            this.iterationNo = iterationNo;
        }

        public String toString() {
            String str = "[" + iterationNo + "]";
            return str;
        }
    }

    /**
     * Represents an iteration number and a distance that a vertex took during
     * a particular iteration of the BFS.
     */
    static class IterationDistancePair extends Diff {
        long distance;

        /**
         * The memory requirement for Diff should be 2 (short), while the memory requirement for IterationDistancePair
         * should be 2+8 (short + double) - so IterationDistancePair should require 5X Diff.
         * However, due to the JVM requirement, each one has a 16 bytes used for Object - note that we are ignoring the
         * badding overhead here.
         * <p>
         * So, in order to keep the correct 5X ration, we need to add 8 double dummy variables to represent the dummy 64 bytes.
         * The total memory for Diff is going to be: 16 + 2 = 18
         * The total memory for IterationDistancePair is going to be: 16 + 2 + 8 +(64 dummy) = 90
         * <p>
         * <p>
         * double dummy1;
         * double dummy2;
         * double dummy3;
         * double dummy4;
         * double dummy5;
         * double dummy6;
         * double dummy7;
         * double dummy8;
         */

        IterationDistancePair(short iterationNo, long distance) {
            super(iterationNo);
            this.distance = distance;
/*
            dummy1 = 0;
            dummy2 = 0;
            dummy3 = 0;
            dummy4 = 0;
            dummy5 = 0;
            dummy6 = 0;
            dummy7 = 0;
            dummy8 = 0;

 */
        }

        @Override
        public String toString() {
            String str = "[" + iterationNo + " - " + distance + "]";
            return str;
        }
    }

    /**
     * Represents a vertex and iteration number and a distance pair.
     */
    static class VertexIterationDistancePair {
        int vertexId;
        IterationDistancePair iterDistPair;

        public VertexIterationDistancePair(int vertexId, IterationDistancePair iterDistPair) {
            this.vertexId = vertexId;
            this.iterDistPair = iterDistPair;
        }
    }
}
