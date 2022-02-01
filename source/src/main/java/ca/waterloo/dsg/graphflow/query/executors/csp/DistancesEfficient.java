package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.util.ArrayUtils;
import ca.waterloo.dsg.graphflow.util.Report;
import ca.waterloo.dsg.graphflow.util.VisibleForTesting;

import java.util.*;
import java.util.Map.Entry;

/**
 * Represents a data structure that keeps track of the distances that each vertex took in different
 * iterations. Contains utility methods to add new distances or clear distances after some iteration
 * and keep track of the size of distances.
 */
public class DistancesEfficient {
    protected final static List<Object> emptyList = Collections.unmodifiableList(new ArrayList<>());
    protected Map<Integer, List<Object>> vIterDistPairMap;
    @VisibleForTesting
    protected VertexIterationDistancePair[] minFrontierDistances;
    // The frontier of the BFS. As an invariant the algorithm always maintains the frontier.
    protected Set<Integer> frontier;
    protected Set<Integer> previousFrontier;

    // This includes each vertex that we ever visited during a query
    // The pair keeps #of times it was added for fix, and the maximum distance size for this vertex.
    protected Map<Integer, VertexStats> vertexHistory;

    protected int nextFrontierSize;
    protected Graph.Direction direction;

    // The latest iteration number. We maintain the invariant that the IntQueue frontier above
    // always contains the vertices in the frontier of the BFS at latestIteration. For example,
    // consider unweighted unidirectional BFS. If latest iteration is 2, then we ran the BFS for
    // 2 steps from the source and the vertices that are 2 steps from the source, we have their
    // distances set to 2 and all of them are in the frontier.
    protected short latestIteration;
    protected int source;

    protected Object pair1, pair2;


    /**
     * Default constructor with no source.
     */
    public DistancesEfficient() {
        this.vIterDistPairMap = new HashMap<>();
        this.frontier = new HashSet<>();
        this.previousFrontier = new HashSet<>();
        this.vertexHistory = new HashMap<Integer, VertexStats>();
        this.latestIteration = 0;
        minFrontierDistances = new VertexIterationDistancePair[100];
        this.direction = Graph.Direction.FORWARD; // default value - only used to detect the nextFrontierSize!
        nextFrontierSize = 0;

        pair1 = new IterationDistancePair((short) -1, Double.MAX_VALUE);
        pair2 = new IterationDistancePair((short) -1, Double.MAX_VALUE);
    }


    /**
     * Default public constructor.
     */
    public DistancesEfficient(int source) {
        this.vIterDistPairMap = new HashMap<>();
        this.frontier = new HashSet<>();
        this.previousFrontier = new HashSet<>();
        this.vertexHistory = new HashMap<Integer, VertexStats>();
        this.latestIteration = 0;
        minFrontierDistances = new VertexIterationDistancePair[100];
        setVertexDistance(source, (short) 0 /* iteration number */, 0.0 /* distance */);
        this.direction = Graph.Direction.FORWARD; // default value - only used to detect the nextFrontierSize!
        nextFrontierSize = 0;
        this.source = source;

        pair1 = new IterationDistancePair((short) -1, Double.MAX_VALUE);
        pair2 = new IterationDistancePair((short) -1, Double.MAX_VALUE);
    }

    public DistancesEfficient(int source, Graph.Direction d) {

        this.vIterDistPairMap = new HashMap<>();
        this.frontier = new HashSet<>();
        this.previousFrontier = new HashSet<>();
        this.vertexHistory = new HashMap<Integer, VertexStats>();
        this.latestIteration = 0;
        minFrontierDistances = new VertexIterationDistancePair[100];
        this.direction = d; // only used to detect the nextFrontierSize!
        nextFrontierSize = 0;
        this.source = source;

        setVertexDistance(source, (short) 0 /* iteration number */, 0.0 /* distance */);

        pair1 = new IterationDistancePair((short) -1, Double.MAX_VALUE);
        pair2 = new IterationDistancePair((short) -1, Double.MAX_VALUE);
    }


    void vertexToFixCount(int vertexId) {
        VertexStats stats = vertexHistory.get(vertexId);
        if (null == stats) {
            stats = new VertexStats();
        }
        stats.addedToFix++;
        return;
    }

    /**
     * Increments the latestIteration, returns the current frontier, which now that
     * we incremented an iteration, will be the previous frontier, and starts an
     * empty frontier.
     *
     * @return the current frontier (which will be the previous frontier).
     */
    Set<Integer> incrementIterationNoAndGetPreviousFrontier() {

        Report.INSTANCE.debug("** increase iteration number to " + (this.latestIteration + 1));

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

        Report.INSTANCE.debug("**set Iteration number to " + iterationNo);

        this.latestIteration = iterationNo;
        clearDistancesAfterT(iterationNo);
        frontier.clear();
        nextFrontierSize = 0;
        for (Entry<Integer, List<Object>> vertexDistancesEntry : vIterDistPairMap.entrySet()) {

            List<Object> distancePairs = vertexDistancesEntry.getValue();
            Diff diff = (Diff) distancePairs.get(distancePairs.size() - 1);
            if (iterationNo == diff.iterationNo) {
                frontier.add(vertexDistancesEntry.getKey());
                nextFrontierSize += Graph.getInstance().getVertexDegree(vertexDistancesEntry.getKey(), direction);
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

        Diff diff = null;
        for (Entry<Integer, List<Object>> entry : vIterDistPairMap.entrySet()) {
            List<Object> iterDistPairs = entry.getValue();
            int indexToRemove = iterDistPairs.size();
            for (int j = iterDistPairs.size() - 1; j >= 0; --j) {

                diff = (Diff) iterDistPairs.get(j);
                if (diff.iterationNo <= t) {
                    break;
                }
                indexToRemove--;
            }
            int size = iterDistPairs.size();
            for (int i = indexToRemove; i < size; ++i) {
                iterDistPairs.remove(indexToRemove);
            }
            if (iterDistPairs.isEmpty()) {
                verticesToRemove.add(entry.getKey());
            }
        }
        for (int vertexToRemove : verticesToRemove) {
            vIterDistPairMap.remove(vertexToRemove);
        }
        for (int i = t + 1; i < minFrontierDistances.length; ++i) {
            minFrontierDistances[i] = null;
        }
    }

    /**
     * Clears each distance that a vertex took before and including a particular
     * iteration number.
     *
     * @param vertexId ID of the vertex whose distances should be cleared.
     * @param iterNo   iteration number. The vertex's value at iterations less than or equal t will be
     *                 removed.
     */
    void clearVertexDistancesAtAndBeforeT(int vertexId, short iterNo) {
        List<Object> distances = vIterDistPairMap.get(vertexId);
        if (distances == null) {
            return;
        }

        while (!distances.isEmpty()) {
            if (((Diff) distances.get(0)).iterationNo <= iterNo) {
                Object iterDistPair = distances.remove(0);
                updateMinFrontierDistancesIfNecessary(vertexId, iterDistPair, true /* isDeletion */);
            } else {
                break;
            }
        }

        if (distances.isEmpty()) {
            vIterDistPairMap.remove(vertexId);
            frontier.remove(vertexId);
            nextFrontierSize -= Graph.getInstance().getVertexDegree(vertexId, direction);
        }
    }

    void clearVertexDistanceAfterIterNo(int vertexId, short iterNo) {
        List<Object> distances = vIterDistPairMap.get(vertexId);
        if (distances == null) {
            return;
        }
        while (!distances.isEmpty()) {
            if (((Diff) distances.get(distances.size() - 1)).iterationNo > iterNo) {
                Object iterDistPair = distances.remove(distances.size() - 1);
                updateMinFrontierDistancesIfNecessary(vertexId, iterDistPair, true /* isDeletion */);
            } else {
                break;
            }
        }
    }

    void clearVertexDistanceAtT(int vertexId, short iterationNo) {

        Report.INSTANCE.debug("----- clearVertexDistanceAtT for " + vertexId + " @ " + iterationNo);

        List<Object> distances = vIterDistPairMap.get(vertexId);
        if (distances == null) {
            return;
        }

        int indexToRemove = -1;
        for (int i = 0; i < distances.size(); ++i) {
            Diff iterationDistPair = (Diff) distances.get(i);
            if (iterationDistPair.iterationNo == iterationNo) {
                indexToRemove = i;
                break;
            } else if (iterationDistPair.iterationNo > iterationNo) {
                break;
            }
        }
        if (indexToRemove >= 0) {
            Object iterDistPair = distances.remove(indexToRemove);
            updateMinFrontierDistancesIfNecessary(vertexId, iterDistPair, true /* isDeletion */);
        }
    }

    /**
     * @param vertexId    ID of a vertex.
     * @param iterationNo iteration number for which to update the distance
     * @param distance    distance of the given vertex to the source in the given iteration number.
     */
    void setVertexDistance(int vertexId, short iterationNo, double distance) {

        //System.out.println("---- setVertexDistance v=" + vertexId + " iter= " + iterationNo + " distance= " + distance);

        if (Double.MAX_VALUE == distance) {
            clearVertexDistancesAtAndBeforeT(vertexId, iterationNo);
        }
        if (iterationNo == latestIteration) {
            frontier.add(vertexId);
            nextFrontierSize += Graph.getInstance().getVertexDegree(vertexId, direction);

            //Report.INSTANCE.debug("** Adding vertex " + vertexId + " with direction " + direction +
            //        " to make nextFrontier size = " + nextFrontierSize);
        }

        List<Object> distances = vIterDistPairMap.get(vertexId);
        if (null == distances) {
            distances = new ArrayList<>(1);
            vIterDistPairMap.put(vertexId, distances);
        }

        // Create vertex stats when needed
        VertexStats stats = vertexHistory.get(vertexId);
        if (null == stats) {
            stats = new VertexStats();
            vertexHistory.put(vertexId, stats);
        }

        int indexToAdd = 0;
        while (indexToAdd < distances.size()) {
            IterationDistancePair iterationDistancePair = (IterationDistancePair) distances.get(indexToAdd);

            if (iterationDistancePair.iterationNo == iterationNo) {
                iterationDistancePair.distance = distance;
                updateMinFrontierDistancesIfNecessary(vertexId, iterationDistancePair, false /* not a deletion */);
                return;
            } else if (iterationDistancePair.iterationNo > iterationNo) {
                break;
            }
            indexToAdd++;
        }
        Object iterDistPair = new IterationDistancePair(iterationNo, distance);
        distances.add(indexToAdd, iterDistPair);

        if (stats.LargestDiffSize < distances.size()) {
            stats.LargestDiffSize = distances.size();
        }

        updateMinFrontierDistancesIfNecessary(vertexId, iterDistPair, false /* is not deletion */);
    }

    /**
     * Checks whether or not the min distance vertex needs to change in the frontier of the iteration,
     * in which the given update to the distance of a vertex is happening.
     *
     * @param vertexId     ID of the vertex that is being updated.
     * @param iterDistPair the iteration number and the distance of the vertex.
     * @param isDeletion   whether or not the iterDistPair is being deleted for the vertex.
     */
    private void updateMinFrontierDistancesIfNecessary(int vertexId, Object iterDistPair, boolean isDeletion) {

        IterationDistancePair pair = (IterationDistancePair) iterDistPair;

        short iterationNo = pair.iterationNo;
        if (isDeletion) {
            VertexIterationDistancePair currentMinVIterDistPair = minFrontierDistances[iterationNo];
            // Warning: We don't explicitly check it but currentMinVIterDistPair has to be non null
            // because if we are deleting the distance of a vertex in a particular iteration i,
            // there must be at least one vertex in the frontier for iteration i. Therefore there must
            // be a vertex with the minimum distance in that frontier.
            if (vertexId == currentMinVIterDistPair.vertexId) {
                findNewMinDistanceVertexInFrontier(pair.iterationNo);
            }
        } else {
            if (iterationNo + 1 > minFrontierDistances.length) {
                minFrontierDistances = (VertexIterationDistancePair[]) ArrayUtils
                        .resizeIfNecessary(minFrontierDistances, iterationNo + 1);
            }
            if (minFrontierDistances[iterationNo] == null ||
                    pair.distance < minFrontierDistances[iterationNo].iterDistPair.distance) {
                minFrontierDistances[iterationNo] = new VertexIterationDistancePair(vertexId, pair);
            } else {
                VertexIterationDistancePair currentMinVIterDistPair = minFrontierDistances[iterationNo];
                if (vertexId == currentMinVIterDistPair.vertexId) {
                    if (pair.distance > currentMinVIterDistPair.iterDistPair.distance) {
                        findNewMinDistanceVertexInFrontier(pair.iterationNo);
                    }
                }
            }
        }
    }

    private void findNewMinDistanceVertexInFrontier(short iterationNo) {
        minFrontierDistances[iterationNo] = null;
        int minVertexId = -1;
        double minDistance = Double.MAX_VALUE;
        IterationDistancePair minIterDistPair = null;
        for (Entry<Integer, List<Object>> vertexDistancesEntry : vIterDistPairMap.entrySet()) {
            for (Object object : vertexDistancesEntry.getValue()) {

                IterationDistancePair iterDistPair = (IterationDistancePair) object;

                if (iterDistPair.iterationNo == iterationNo && iterDistPair.distance < minDistance) {
                    minVertexId = vertexDistancesEntry.getKey();
                    minIterDistPair = iterDistPair;
                    minDistance = iterDistPair.distance;
                }
            }
        }
        if (null != minIterDistPair) {
            minFrontierDistances[iterationNo] = new VertexIterationDistancePair(minVertexId, minIterDistPair);
        }
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
    void clearAndSetOnlyVertexDistance(int vertexId, short iterationNo, double distance) {

        Report.INSTANCE.debug("------ clearAndSetOnlyVertexDistance = Set " + vertexId + " @ " + iterationNo + " to " +
                distance);

        List<Object> distances = vIterDistPairMap.get(vertexId);

        if (null != distances) {
            //Report.INSTANCE.debug("* Previous distances that is going to be deleted : " + Arrays.toString(distances.toArray()));

            while (!distances.isEmpty()) {
                Object iterDistPair = distances.remove(0);
                updateMinFrontierDistancesIfNecessary(vertexId, iterDistPair, true /* isDeletion */);
            }
        } else {
            //Report.INSTANCE.debug("* Previous distances that is going to be deleted : []");
        }
        setVertexDistance(vertexId, iterationNo, distance);
    }

    /**
     * @param vertexId ID of a vertex.
     * @return Distance of the vertex at the maximum known iteration.
     */
    double getLatestDistance(int vertexId) {
        List<Object> distances = vIterDistPairMap.get(vertexId);
        if (null == distances || distances.isEmpty()) {
            return Double.MAX_VALUE;
        } else {
            return ((IterationDistancePair) distances.get(distances.size() - 1)).distance;
        }
    }

    /**
     * @param vertexId ID of a vertex.
     * @return The iteration with the best Distance of the vertex.
     */
    int getIterationWithBestDistance(int vertexId) {
        List<Object> distances = vIterDistPairMap.get(vertexId);
        if (null == distances || distances.isEmpty()) {
            return Integer.MAX_VALUE;
        } else {
            return ((Diff) distances.get(distances.size() - 1)).iterationNo;
        }
    }


    /**
     * @param vertexId ID of a vertex.
     * @return Distances the vertex took at different iterations. In other words, for
     * each iteration the distance of the vertex changed, the returned list contains one
     * {@link Object}.
     */
    List<Object> getAllDistances(int vertexId) {
        List<Object> retVal = vIterDistPairMap.get(vertexId);
        return (null == retVal) ? emptyList : retVal;
    }


    /**
     * Very similar to getAllDistances but it returns a list of iterations instead
     *
     * @param vertexId ID of a vertex.
     * @return
     */
    List<Short> getAllIterations(int vertexId) {

        List<Object> retVal = vIterDistPairMap.get(vertexId);

        if (null != retVal) {
            List<Short> result = new ArrayList<>(retVal.size());
            for (Object pair : retVal) {
                result.add(((Diff) pair).iterationNo);
            }
            return result;
        } else {
            return null;
        }
    }

    void resetPair1() {
        ((IterationDistancePair) pair1).distance = Double.MAX_VALUE;
        ((IterationDistancePair) pair1).iterationNo = (short) -1;
    }

    void resetPair2() {
        ((IterationDistancePair) pair2).distance = Double.MAX_VALUE;
        ((IterationDistancePair) pair2).iterationNo = (short) -1;
    }


    IterationDistancePair getDistancePair(int vertexId, short iterationNo) {
        List<Object> distances = vIterDistPairMap.get(vertexId);

        resetPair1();

        if (null == distances) {
            ((IterationDistancePair) pair1).iterationNo = iterationNo;
            return (IterationDistancePair) pair1;
        } else {
            for (Object object : distances) {

                IterationDistancePair iterationDistance = (IterationDistancePair) object;

                if (iterationDistance.iterationNo > iterationNo) {
                    break;
                } else {
                    ((IterationDistancePair) pair1).distance = iterationDistance.distance;
                    ((IterationDistancePair) pair1).iterationNo = iterationDistance.iterationNo;
                }
            }

            Report.INSTANCE.debug("** v = " + vertexId + " distances = " + Arrays.toString(distances.toArray()));
            Report.INSTANCE.debug("** v = " + vertexId + " @ " + ((IterationDistancePair) pair1).iterationNo + " = " +
                    ((IterationDistancePair) pair1).distance);
            return ((IterationDistancePair) pair1);
        }
    }

    /**
     * @param vertexId    ID of a vertex.
     * @param iterationNo iteration at which the distance of the given vertex should be returned.
     * @return Distance of the vertex at the given iteration number. Note that if the vertex's
     * distance was not updated in the given iterationNo, the distance in the latest iteration less
     * than iterationNo is returned.
     */
    double getDistance(int vertexId, short iterationNo) {
        List<Object> distances = vIterDistPairMap.get(vertexId);
        if (null == distances) {
            return Double.MAX_VALUE;
        } else {
            double latestDistance = Double.MAX_VALUE;
            for (Object object : distances) {

                IterationDistancePair iterationDistance = (IterationDistancePair) object;

                if (iterationDistance.iterationNo > iterationNo) {
                    break;
                } else {
                    latestDistance = iterationDistance.distance;
                }
            }

            Report.INSTANCE.debug("** v = " + vertexId + " distances = " + Arrays.toString(distances.toArray()));
            Report.INSTANCE.debug("** v = " + vertexId + " @ " + iterationNo + " = " + latestDistance);
            return latestDistance;
        }
    }


    public void printStats() {

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
            histogram_Diffs[iterDistPair.getValue().LargestDiffSize]++;

            if (iterDistPair.getValue().addedToFix > 1000) {
                System.out.println("**** v= " + iterDistPair.getKey() + " " + iterDistPair.getValue().addedToFix);
            }

            if (iterDistPair.getValue().addedToFix > max_fix) {
                max_fix = iterDistPair.getValue().addedToFix;
            }
            if (iterDistPair.getValue().LargestDiffSize > max_size) {
                max_size = iterDistPair.getValue().LargestDiffSize;
            }
        }

        for (int i = 0; i <= max_fix; i++) {
            fixJoiner.add(String.valueOf(histogram_toFix[i]));
        }

        for (int i = 0; i <= max_size; i++) {
            sizeJoiner.add(String.valueOf(histogram_Diffs[i]));
        }

        System.out.println("#Fix-Histogram " + fixJoiner.toString());
        System.out.println("#DiffSize-Histogram " + sizeJoiner.toString());

        Report.INSTANCE.error("=======================================");
        //Report.INSTANCE.error("         Histogram (#fix, maxSize) ");
        //for(int i=0;i<100;i++)
        //    if(histogram_toFix[i] > 0 || histogram_Diffs[i] > 0)
        //        Report.INSTANCE.error( i +" : "+ histogram_toFix[i]+ " - "+histogram_Diffs[i]);
        //Report.INSTANCE.error("\n=======================================");
    }

    public void print() {

        Report.INSTANCE.debug("=======================================");
        Report.INSTANCE.debug("======== Printing distances ===========\n");
        for (Entry<Integer, List<Object>> iterDistPair : vIterDistPairMap.entrySet()) {

            Report.INSTANCE
                    .debug(iterDistPair.getKey() + " --> " + vertexHistory.get(iterDistPair.getKey()).toString() +
                            "   (" + Arrays.toString(iterDistPair.getValue().toArray()) + ")");
        }

        Report.INSTANCE.debug("\n=======================================");
    }

    public int numberOfVertices() {
        int size = 0;
        for (Entry<Integer, List<Object>> iterDistPair : vIterDistPairMap.entrySet()) {
            size++;
        }
        return size;
    }


    public int size() {
        int size = 0;
        for (Entry<Integer, List<Object>> iterDistPair : vIterDistPairMap.entrySet()) {
            size += iterDistPair.getValue().size();
        }
        return size;
    }

    /**
     * @return whether the frontier is empty or not.
     */
    boolean isFrontierEmpty(short iterationNo) {
        return minFrontierDistances[iterationNo] == null;
    }


    /**
     * Keeps information about vertices for testing purposes
     */
    static class VertexStats {
        int addedToFix;
        int LargestDiffSize;

        VertexStats(int addedToFix, int LargestDiffSize) {
            this.addedToFix = addedToFix;
            this.LargestDiffSize = LargestDiffSize;
        }

        VertexStats() {
            this.addedToFix = 0;
            this.LargestDiffSize = 0;
        }

        @Override
        public String toString() {
            String str = "[" + addedToFix + " - " + LargestDiffSize + "]";
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
        double distance;

        IterationDistancePair(short iterationNo, double distance) {
            super(iterationNo);
            this.distance = distance;
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
