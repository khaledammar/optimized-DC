package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.SortedAdjacencyList;
import ca.waterloo.dsg.graphflow.util.ArrayUtils;
import ca.waterloo.dsg.graphflow.util.Report;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

import java.util.*;


/**
 * Represents a data structure that keeps track of the distances that each vertex took in different
 * iterations. Contains utility methods to add new distances or clear distances after some iteration
 * and keep track of the size of distances.
 */
public class DistancesDC {
    protected final static short[] emptyList = new short[]{0, 0, 0, 0, 0, 0};
    protected final static int[] emptyList2 = new int[]{0, 0, 0};
    // query id
    public static int numQueries = 0;
    protected Map<Integer, Distances.VertexStats> vertexHistory = new HashMap<>();
    public Map<Integer,Integer> recalculateState = new HashMap<>(1);
    public int recalculateNumber = 0;
    //protected Map<Integer, List<Diff>> vIterDistPairMap;
    //protected OneQueryDiff realDiff;
    protected MultiQueryDiffDC realDiff;
    // (`iter`:short, `pos`:int) stored as 2 consecutive ints in int[] array. Index 0 is always the count of entries.
    // `pos` points to a position in the `diffsStore`.
    protected Map<Integer, int[]> deltaDiffs = new HashMap<>();
    // (`diffCount`:short, `distance`:long) stored as 5 consecutive shorts in short[] array. Index 0 is always the count of entries.
    protected List<short[]> diffsStore = new ArrayList<>();
    // Stores list of positions in `diffsStore` that have been deleted and can be reused. When inserting into `diffsStore`,
    // `diffsPool` should be checked first for available positions, otherwise append at the end.
    protected List<Integer> diffsPool = new ArrayList<>();
    protected IntOpenHashSet toBeChecked = new IntOpenHashSet();
    protected int queryId;
    protected Map<Integer, Map<Long, Short>> lastDiffs = new HashMap<>();
    protected Map<Integer, Map<Long, Short>> lastDiffsPrevious = new HashMap<>();
    protected Map<Integer, AbstractMap.SimpleEntry<Long, Long>> distancesCurrentIter = new HashMap<>();
    protected Set<Integer> frontier = new HashSet<>();
    protected Set<Integer> frontierPrevious = new HashSet<>();
    public NewUnidirectionalDifferentialBFS.Queries queryType;

    protected Graph.Direction direction;
    // The latest iteration number. We maintain the invariant that the IntQueue frontier above
    // always contains the vertices in the frontier of the BFS at latestIteration. For example,
    // consider unweighted unidirectional BFS. If latest iteration is 2, then we ran the BFS for
    // 2 steps from the source and the vertices that are 2 steps from the source, we have their
    // distances set to 2 and all of them are in the frontier.
    protected short latestIteration = 0;
    protected int source;
    String name;

    public DistancesDC(int queryId, int source, int destination, Graph.Direction d, NewUnidirectionalDifferentialBFS.Queries queryType, String name) {
        this(queryId, source, destination, d, null, queryType, name);
    }

    public DistancesDC(int queryId, int source, int destination, Graph.Direction d, String type,
                       NewUnidirectionalDifferentialBFS.Queries queryType, String name) {
        this.queryId = queryId;
        this.name = name;
        this.queryType = queryType;
        if (type == null) {
            initializeRealDiff();
        } else {
            initializeRealDiff(type);
        }
        this.direction = d; // only used to detect the nextFrontierSize!
        this.source = source;

        if(queryType == NewUnidirectionalDifferentialBFS.Queries.PR) {
            if (name.equals("Reduce")) {
                setVertexDistancePR();
            }
        }
        else if (queryType == NewUnidirectionalDifferentialBFS.Queries.WCC) {
            setVertexDistanceWCC();
        }
        else {
            setVertexDistance(source, (short) 0 /* iteration number */, 0 /* distance */);
            addTmpDiff(source, 0, (short) 1);
        }
    }

    public static void mergeDiff(Map<Long, Short> diffs, long distance, short diff) {
        var y = diffs.get(distance);
        if (y != null) {
            diff = (short) (diff + y);
            if (diff == 0) {
                diffs.remove(distance);
            }
        }
        if (diff != 0) {
            diffs.put(distance, diff);
        }
    }

    public static String distancesString(int[] iterAndPos, List<short[]> diffsStore) {
        StringBuilder out = new StringBuilder();
        for (int i = 1; i <= iterAndPos[0] * 2; i += 2) {
            int iter = iterAndPos[i];
            var index = iterAndPos[i + 1];
            out.append(iter).append(":[").append(index).append("]").append("(");
            var diffs = diffsStore.get(index);
            for (int j = 1; j < diffs[0] * 5 + 1; j += 5) {
                out.append(Distances.getDistanceFromArray(diffs, j)).append(":").append(diffs[j]).append(";");
            }
            out.append("),");
        }
        return out.toString();
    }

    static int getNewDiffArrPosFrom(List<short[]> diffsStore, List<Integer> diffsPool, short[] diffs) {
        if (diffsPool.isEmpty()) {
            diffsStore.add(diffs);
            return diffsStore.size() - 1;
        } else {
            int pos = diffsPool.remove(diffsPool.size() - 1);
            diffsStore.set(pos, diffs);
            return pos;
        }
    }

    int addAndGetDiffsPosAt(int vertexId, int[] iterAndPos, short iterationNo, int diffLen) {
        int len = iterAndPos[0];
        int indexToAdd = 1;
        while (indexToAdd <= len * 2) {
            short iter = (short) iterAndPos[indexToAdd];

            if (iter == iterationNo) {
                return iterAndPos[indexToAdd + 1];
            } else if (iter > iterationNo) {
                break;
            }
            indexToAdd += 2;
        }
        short[] diffs = new short[diffLen * 5 + 1];
        return insertDiffsAndGetPos(vertexId, iterAndPos, diffs, iterationNo, indexToAdd);
    }

    int insertDiffsAndGetPos(int vertexId, int[] iterAndPos, short[] diffs, short iterationNo, int indexToAdd) {
        int len = iterAndPos[0];
        int pos = getNewDiffArrPosFrom(diffsStore, diffsPool, diffs);
        if ((len * 2) + 3 > iterAndPos.length) {
            var iterAndPosNew = new int[Math.max((len * 2) + 3, (int) (iterAndPos.length * 1.2))];
            iterAndPosNew[0] = len + 1;
            // Copy elements before `indexToAdd`.
            System.arraycopy(iterAndPos, 1, iterAndPosNew, 1, indexToAdd - 1);
            iterAndPosNew[indexToAdd] = iterationNo;
            iterAndPosNew[indexToAdd + 1] = pos;
            // Copy elements after `indexToAdd`.
            System.arraycopy(iterAndPos, indexToAdd, iterAndPosNew, indexToAdd + 2, (len * 2) - indexToAdd + 1);
            deltaDiffs.put(vertexId, iterAndPosNew);
        } else {
            if (indexToAdd <= (len * 2)) {
                // Shift elements after `indexToAdd`.
                System.arraycopy(iterAndPos, indexToAdd, iterAndPos, indexToAdd + 2, (len * 2) - indexToAdd + 1);
            }
            iterAndPos[0]++;
            iterAndPos[indexToAdd] = iterationNo;
            iterAndPos[indexToAdd + 1] = pos;
        }
        if (Report.INSTANCE.appReportingLevel == Report.Level.DEBUG)
            countLargestDiffSize(vertexId, len+1);

        return pos;
    }

    public void clear() {
        lastDiffs = new HashMap<>();
        lastDiffsPrevious = new HashMap<>();
        frontier = new HashSet<>();
        frontierPrevious = new HashSet<>();
        toBeChecked = new IntOpenHashSet();
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
        return;
    }

    void initializeRealDiff(String type) {
        initializeDroppedDiff(type);
        realDiff = new MultiQueryDiffDC();
    }

    void initializeRealDiff() {
        initializeDroppedDiff();
        realDiff = new MultiQueryDiffDC();
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
        Distances.VertexStats stats = vertexHistory.get(vertexId);
        if (null == stats) {
            stats = new Distances.VertexStats();
        }
        stats.recalculated++;
        vertexHistory.put(vertexId,stats);
    }

    void incrementIterationNoAndSetPreviousFrontier() {
        this.latestIteration++;
        setPreviousFrontier();
    }

    void setPreviousFrontier() {
        frontierPrevious = frontier;
        frontier = new HashSet<>();
    }

    public Set<Integer> getVerticesWithDiff() {
        IntOpenHashSet allV = new IntOpenHashSet();
        allV.addAll(realDiff.getVerticesWithDiff());
        allV.addAll(deltaDiffs.keySet());
        return allV;
    }

    public void mergeLastDiffsIntoDelta(short iteration) {
        for (var entry : lastDiffs.entrySet()) {
            var vertex = entry.getKey();
            var newDiffs = entry.getValue();
            if (newDiffs.isEmpty()) {
                continue;
            }
            var iterAndPos = getMergedDiffsCopiedIfReal(vertex);

            // Find if iteration is present.
            var pos = -1;
            int len = iterAndPos[0];
            int index = 1;
            while (index <= len * 2) {
                short iter = (short) iterAndPos[index];

                if (iter == iteration) {
                    pos = iterAndPos[index + 1];
                    break;
                } else if (iter > iteration) {
                    break;
                }
                index += 2;
            }

            short[] diffs;
            Map<Long, Short> newDiffsCopy;
            if (pos == -1) {
                diffs = new short[newDiffs.size() * 5 + 1];
                newDiffsCopy = newDiffs;
            } else {
                diffs = diffsStore.get(pos);
                // Need to create a copy as `lastDiffs` will be reused in next iteration.
                newDiffsCopy = new HashMap<>(newDiffs);
                for (int i = 1; i < diffs[0] * 5 + 1; i += 5) {
                    // Merge same distances.
                    DistancesDC.mergeDiff(newDiffsCopy, Distances.getDistanceFromArray(diffs, i), diffs[i]);
                }
                if (newDiffsCopy.isEmpty()) {
                    // Delete diffs from array.
                    if (pos == diffsStore.size() - 1) {
                        diffsStore.remove(pos);
                    } else {
                        diffsPool.add(pos);
                        diffsStore.set(pos, null);
                    }
                    iterAndPos[0]--;
                    if ((len * 2) > (index + 1)) {
                        // Delete iteration.
                        System.arraycopy(iterAndPos, index + 2, iterAndPos, index, (len * 2) - index - 1);
                    }
                    continue;
                }
                len = newDiffsCopy.size() * 5 + 1;
                if (diffs.length < len) { // Resize
                    diffs = new short[len];
                    diffsStore.set(pos, diffs);
                }
            }

            diffs[0] = (short) newDiffsCopy.size();
            int i = 1;
            for (var vEntry : newDiffsCopy.entrySet()) {
                Distances.setDistanceToArray(diffs, vEntry.getKey(), i);
                diffs[i] = vEntry.getValue();
                i += 5;
            }

            if (pos == -1) {
                insertDiffsAndGetPos(vertex, iterAndPos, diffs, iteration, index);
            }
        }
    }

    public void mergeDeltaDiffs() {
        // merge two lists
        realDiff.mergeDeltaDiffs(queryId, deltaDiffs, diffsStore, diffsPool);

        // Create vertex stats when needed
        //        if (Report.INSTANCE.appReportingLevel != Report.Level.ERROR) {
        //            for (Integer v : deltaDiffs.keySet()) {
        //                VertexStats stats = vertexHistory.get(v);
        //
        //                if (null == stats) {
        //                    stats = new VertexStats();
        //                    vertexHistory.put(v, stats);
        //                }
        //            }
        //        }

        deltaDiffs = new HashMap<>();
    }

    public void mergeDeltaDiffsJOD() {
        // merge two lists
        realDiff.mergeDeltaDiffs(queryId, deltaDiffs, diffsStore, diffsPool);

        // Create vertex stats when needed
        //        if (Report.INSTANCE.appReportingLevel != Report.Level.ERROR) {
        //            for (Integer v : deltaDiffs.keySet()) {
        //                VertexStats stats = vertexHistory.get(v);
        //
        //                if (null == stats) {
        //                    stats = new VertexStats();
        //                    vertexHistory.put(v, stats);
        //                }
        //            }
        //        }

        deltaDiffs = new HashMap<>();
    }

    /**
     * Increments the latestIteration, returns the current frontier, which now that
     * we incremented an iteration, will be the previous frontier, and starts an
     * empty frontier.
     *
     * @return the current frontier (which will be the previous frontier).
     */
    void incrementIterationNoAndSetPreviousDiffs() {
        this.latestIteration++;
        setPreviousDiffs();
    }

    void setPreviousDiffs() {
        lastDiffsPrevious = lastDiffs;
        lastDiffs = new HashMap<>();
    }

    public void addTmpDiff(int vertexId, long distance, short diff) {
        var current = lastDiffs.computeIfAbsent(vertexId, k -> new HashMap<>());
        DistancesDC.mergeDiff(current, distance, diff);
    }

    public int[] getMergedDiffs(Integer v) {
        if (deltaDiffs.containsKey(v)) {
            return deltaDiffs.get(v);
        } else {
            return realDiff.getDiffs(v);
        }
    }

    public int[] getMergedDiffsCopiedIfReal(Integer v) {
        if (deltaDiffs.containsKey(v)) {
            return deltaDiffs.get(v);
        } else {
            var iterAndPos = realDiff.getDiffsCopied(v, diffsStore);
            deltaDiffs.put(v, iterAndPos);
            return iterAndPos;
        }
    }

    public short[] getDiffsAt(Integer v, short iteration, boolean newBatches) {
        var iterAndPos = newBatches ? getMergedDiffs(v) : getOldDiffs(v);
        for (int i = 1; i <= iterAndPos[0] * 2; i += 2) {
            if (iterAndPos[i] == iteration) {
                return diffsStore.get(iterAndPos[i + 1]);
            }
        }
        emptyList[0] = 0;
        return emptyList;
    }

    public int[] getOldDiffs(int v) {

        //        if (DistancesWithDropBloom.debug(v)) {
        //            if (realDiff.containsVertex(queryId, v)) {
        //                Report.INSTANCE.error( "---- getOldDiffs v=" + v + " distances = " +
        //                        Arrays.toString(realDiff.getDiffs(queryId, v).toArray()));
        //            } else {
        //                Report.INSTANCE.error( "---- getOldDiffs v=" + v + " No distances! ");
        //            }
        //        }

        return realDiff.getDiffs(v);
    }

    /**
     * Used for PR
     *
     */
    void setVertexDistancePR() {
        // This is probably not the best way to initialize the distance of all vertices but it would do!
        for (int vertex=0; vertex <= Graph.INSTANCE.getHighestVertexId(); vertex++) {
            setVertexDistance(vertex, (short) 0, Distances.SIXM);
            addTmpDiff(vertex, Distances.SIXM, (short) 1);
        }
    }

    /**
     * Used for WCC
     */
    void setVertexDistanceWCC() {
        // This is probably not the best way to initialize the distance of all vertices but it would do!
        for (int vertex=0; vertex <= Graph.INSTANCE.getHighestVertexId(); vertex++) {
            setVertexDistance(vertex, (short) 0, vertex);
            if (name.equals("Reduce")) {
                addTmpDiff(vertex, vertex, (short) 1);
            }
        }
    }



    /**
     * @param vertexId    ID of a vertex.
     * @param iterationNo iteration number for which to update the distance
     * @param distance    distance of the given vertex to the source in the given iteration number.
     */
    void setVertexDistance(int vertexId, short iterationNo, long distance) {
        int[] iterAndPos = getMergedDiffsCopiedIfReal(vertexId);
        var tmp = getDistanceAllFrom(iterAndPos, iterationNo);
        short diffsLen = (short) (tmp.size() + 1);
        var pos = addAndGetDiffsPosAt(vertexId, iterAndPos, iterationNo, diffsLen);
        var diffs = diffsStore.get(pos);
        diffsLen += diffs[0];
        if (diffsLen * 5 + 1 > diffs.length) {
            var diffsNew = new short[(diffsLen + 1) * 5 + 1];
            System.arraycopy(diffs, 0, diffsNew, 0, diffs[0] * 5 + 1);
            diffs = diffsNew;
            diffsStore.set(pos, diffs);
        }
        int index = diffs[0] * 5 + 1;
        for (var entry : tmp.entrySet()) {
            diffs[index] = (short) -entry.getValue();
            Distances.setDistanceToArray(diffs, entry.getKey(), index);
            index += 5;
        }
        diffs[index] = 1;
        Distances.setDistanceToArray(diffs, distance, index);
        diffs[0] = diffsLen;


        if (Report.INSTANCE.appReportingLevel == Report.Level.DEBUG)
            countLargestDiffSize(vertexId, diffsLen);

    }

    /**
     * @param vertexId ID of a vertex.
     * @return Distance of the vertex at the maximum known iteration. (Merged distances)
     */
    long getLatestDistance(int vertexId) {
        return getDistance(vertexId, latestIteration, true);
    }

    Map<Long, Short> getDistanceAll(int vertexId, short iteration,
                                    boolean newBatches/*get distances from old batches or the current one*/) {
        int[] iterAndPos;
        if (newBatches) {
            iterAndPos = this.getMergedDiffs(vertexId); //vIterDistPairMap.get(vertexId);
        } else {
            iterAndPos = this.getOldDiffs(vertexId); //vIterDistPairMap.get(vertexId);
        }
        return getDistanceAllFrom(iterAndPos, iteration);
    }

    Map<Long, Short> getDistanceAllFrom(int[] iterAndPos, short iteration) {
        Map<Long, Short> distancesStore = new HashMap<>();
        if (iterAndPos != null) {
            for (int i = 1; i <= iterAndPos[0] * 2; i += 2) {
                if (iterAndPos[i] > iteration) {
                    break;
                } else {
                    var diffs = diffsStore.get(iterAndPos[i + 1]);
                    for (int j = 1; j < diffs[0] * 5 + 1; j += 5) {
                        var df = diffs[j];
                        var ds = Distances.getDistanceFromArray(diffs, j);
                        distancesStore.put(ds, (short) (distancesStore.getOrDefault(ds, (short) 0) + df));
                    }
                }
            }
            distancesStore.values().removeIf(val -> val == 0);
        }
        return distancesStore;
    }

    long getDistance(int vertexId, short iteration,
                     boolean newBatches/*get distances from old batches or the current one*/) {

        var nbr_dists = getDistanceAll(vertexId, iteration,
                newBatches /* I may want to use old or new batches based on the original request*/);
        return nbr_dists.isEmpty() ? Long.MAX_VALUE : Collections.min(nbr_dists.keySet());
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
    public Map getVertexStats(int stateType) {
        int degree = 0;
        Map<Integer,Integer> degreeCount = new HashMap<Integer,Integer> (1);
        Map<Integer,Integer> degreeTotal = new HashMap<Integer,Integer> (1);
        Map<Integer,Float> degreeAvg = new HashMap<Integer,Float> (1);

        System.out.println("Size of Vertex History: "+vertexHistory.size());

        for (var e : vertexHistory.entrySet()) {
            // For distance sizes, we are interested in in-degree
            if (stateType == Distances.VertexStats.DIFF_SIZE)
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

    public void printStats() {
        Report.INSTANCE.error("======== Printing Statistics ===========");
        Report.INSTANCE.error("======== # vertices " + vertexHistory.keySet().size() + " ==========");

        StringJoiner fixJoiner = new StringJoiner(",");
        StringJoiner sizeJoiner = new StringJoiner(",");
        int[] histogram_toFix = new int[1000000];
        int[] histogram_Diffs = new int[1000000];
        int max_fix = 0;
        int max_size = 0;

        for (Map.Entry<Integer, Distances.VertexStats> iterDistPair : vertexHistory.entrySet()) {
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

        System.out.println("#Fix-Histogram " + fixJoiner.toString());
        System.out.println("#DiffSize-Histogram " + sizeJoiner.toString());

        Report.INSTANCE.error("=======================================");
        Report.INSTANCE.error("RecalculateStats " + getVertexStats(Distances.VertexStats.RECALCULATE_STATS));
        Report.INSTANCE.error("=======================================");
        Report.INSTANCE.error("DistanceSizeDC " + getVertexStats(Distances.VertexStats.DIFF_SIZE));
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
            Report.INSTANCE.print("----- Distances: " + name);

            Report.INSTANCE.print("  deltaDiffs:");
            /*
            for (var iterDistPair : deltaDiffs.entrySet()) {
                Report.INSTANCE.print(iterDistPair.getKey() + " --> [" + distancesString(iterDistPair.getValue(), diffsStore) + "]");
            }


            Report.INSTANCE.print("  realDiffs:");
            //realDiff.print(diffsStore);
            Report.INSTANCE.print("  lastDiffs:");
            System.out.println(lastDiffs);
            Report.INSTANCE.print("  lastDiffsPrevious:");
            System.out.println(lastDiffsPrevious);
            Report.INSTANCE.print("  toBeChecked:");
            System.out.println(toBeChecked);
            Report.INSTANCE.print("  diffpool:");
            System.out.println(diffsPool);
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
        int size = 0;
        for (var iterAndPos : realDiff.sharedDiffs.values()) {
            for (int i = 1; i <= iterAndPos[0] * 2; i += 2) {
                var diffs = diffsStore.get(iterAndPos[i + 1]);
                for (int j = 1; j < diffs[0] * 5 + 1; j += 5) {
                    var df = diffs[j];
                    size += Math.abs(df);
                }
            }
        }
        return size;
    }

    public void copyDiffs(Distances distances) {
        throw new RuntimeException("Unreachable");
    }

    public long recalculateDistance(int vertexId, short iterationNo, boolean newBatches) {
        short previousIteration = (short) (iterationNo - 1);

        recalculateNumber++;
        if(Report.INSTANCE.appReportingLevel == Report.Level.DEBUG){
            countVertexRecalculate(vertexId);
            recalculateState.put(vertexId,recalculateState.getOrDefault(vertexId,0)+1);
        }

        if (vertexId == source) {
            return 0;
        }

        //This is overhead of not keeping all diffs
        SortedAdjacencyList inNeighbours = getInNeighbours(vertexId, newBatches, iterationNo);

        long minFound = Long.MAX_VALUE;

        // loop over inNeighbours to find the minimum distance
        for (int i = 0; i < inNeighbours.getSize(); i++) {

            Integer nbr = inNeighbours.getNeighbourId(i);
            long w = (long) inNeighbours.getNeighbourWeight(i);


            /* In this step, I am trying to know the most up-to-date distance of the neighbour
             * to compute the distance of self based on old/new edges between me and my in neighbours */

            long nbr_dist = this.getDistance(nbr, previousIteration,
                    newBatches /* I may want to use old or new batches based on the original request*/);

            if (nbr_dist != Long.MAX_VALUE && minFound > (nbr_dist + w)) {
                minFound = nbr_dist + w;
            }
        }

        return minFound;
    }

    public SortedAdjacencyList getInNeighbours(int vertex, boolean merged, short iteration) {
        return NewUnidirectionalDifferentialBFS.staticGetInNeighbours(vertex, merged, direction, iteration);
    }

    public void clearVertexDistanceAtT(int vertexToFix, short currentFixedIter) {
        var iterAndPos = this.getMergedDiffsCopiedIfReal(vertexToFix);
        int limit = iterAndPos[0] * 2;
        short iter = -1;
        int pos = -1;
        for (int i = 1; i <= limit; i += 2) {
            iter = (short) iterAndPos[i];
            if (iter == currentFixedIter) {
                pos = iterAndPos[i + 1];
                break;
            } else if (iter > currentFixedIter) {
                break;
            }
        }
        if (pos >= 1) {
            var diffs = diffsStore.get(pos);
            diffs[0] = 0;
        }
    }

    /**
     * Keeps information about vertices for testing purposes
     */
    static class VertexStats {
        int addedToFix;
        int LargestDiffSize;

        @Override
        public String toString() {
            return "[" + addedToFix + " - " + LargestDiffSize + "]";
        }
    }


    public static class Diff {
        short iterationNo;
        double[] distances;
        short[] diffs;
        int count;

        public Diff(short iterationNo) {
            this.iterationNo = iterationNo;
            this.distances = new double[]{0, 0};
            this.diffs = new short[]{0, 0};
            count = 0;
        }

        @Override
        public String toString() {
            return "[" + iterationNo + " - " + count + " - " + Arrays.toString(distances) + "," +
                    Arrays.toString(diffs) + "]";
        }
    }

    public static class Diff2 extends Diff {
        short[] types;


        public Diff2(short iterationNo) {
            super(iterationNo);
            this.types = new short[]{0, 0};
        }

        public void addDiff(double distance, short diff, short type) {
            distances = ArrayUtils.resizeIfNecessary(distances, count + 1);
            diffs = ArrayUtils.resizeIfNecessary(diffs, count + 1);
            types = ArrayUtils.resizeIfNecessary(types, count + 1);
            distances[count] = distance;
            diffs[count] = diff;
            types[count] = type;
            count += 1;
        }

        @Override
        public String toString() {
            return "[" + iterationNo + " - " + count + " - " + Arrays.toString(distances) + "," +
                    Arrays.toString(diffs) + "," + Arrays.toString(types) + "]";
        }
    }

    /**
     * Represents a vertex and iteration number and a distance pair.
     */
    static class VertexDiff {
        int vertexId;
        Diff iterDistPair;
    }
}
