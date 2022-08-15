package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.util.Report;

import java.util.*;

/**
 * Represents a data structure that keeps track of the distances that each vertex took in different
 * iterations. Contains utility methods to add new distances or clear distances after some iteration
 * and keep track of the size of distances.
 * <p>
 * We keep diffs in Hash table instead of Bloom!
 */
public class DistancesWithDropBloom2 extends Distances {

    /**
     * This version is used for Random Drop
     *
     * @param vertexId
     * @return
     */
    static public int keepCuonter = 0;
    static public int dropoCuonter = 0;
    protected static MultiQueryDiffBloom droppedDiffs;
    protected static boolean droppedDiffsReady = false;
    static int generatorSeed = 7293857;
    static int generatorRange = 100;
    static int[] debug_list = {};//, 3809302
    protected int skipDiff;
    protected Random dropRandomGenerator;
    protected float dropProbability;
    String type;
    String dropType;
    int minimumDegree; // any vertex has less or equal degree will be dropped
    int maximumDegree; // any vertex has more or equal degree will be kept


    /**
     * Default constructor with no source.
     */
    public DistancesWithDropBloom2() {

        super();
        initializeDroppedDiff();
        skipDiff = 0;
        dropRandomGenerator = new Random(generatorSeed);
        dropProbability = 0;
    }


    /**
     * Default public constructor.
     */
    public DistancesWithDropBloom2(int queryId, int source, int destination, float prob,
                                   NewUnidirectionalDifferentialBFS.Queries queryType) {
        super(queryId, source, destination, queryType); // includes a call to init Dropped and Real Diff

        skipDiff = 0;
        dropRandomGenerator = new Random(generatorSeed);
        dropProbability = prob;
    }


    /**
     * Default public constructor.
     */
    public DistancesWithDropBloom2(int queryId, int source, int destination,
                                   NewUnidirectionalDifferentialBFS.Queries queryType) {
        super(queryId, source, destination, queryType); // includes a call to init Dropped and Real Diff
        skipDiff = 0;
        dropRandomGenerator = new Random(generatorSeed);
        dropProbability = 0;
    }

    public DistancesWithDropBloom2(int queryId, int source, int destination, Graph.Direction d, float prob,
                                   NewUnidirectionalDifferentialBFS.Queries queryType) {
        super(queryId, source, destination, d, queryType); // includes a call to init Dropped and Real Diff
        skipDiff = 0;
        dropRandomGenerator = new Random(generatorSeed);
        dropProbability = prob * 100;
    }

    public DistancesWithDropBloom2(int queryId, int source, int destination, Graph.Direction d, float prob,
                                   String dropType, String type, int minimumDegree, int maximumDegree,
                                   NewUnidirectionalDifferentialBFS.Queries queryType) {
        super(queryId, source, destination, d, type, queryType); // includes a call to init Dropped and Real Diff
        skipDiff = 0;
        dropRandomGenerator = new Random(generatorSeed);
        dropProbability = prob * 100;
        this.type = type;
        this.dropType = dropType;
        this.minimumDegree = minimumDegree;
        this.maximumDegree = maximumDegree;
    }
/*
    // This version is used for Selective Drop
    // TODO: make a new computation model to represent Selective Drop with the same code
    boolean skip(int vertexId){

        int vertexDegree = Graph.getInstance().getVertexDegree(vertexId, direction);
        boolean doNotskip = (dropRandomGenerator== null || dropRandomGenerator.nextInt(generatorRange) >= dropProbability);

        if(vertexId==destination || vertexId==source || vertexDegree>=maximumDegree || (vertexDegree>minimumDegree &&  doNotskip))
            return false;
        else
            return true;
    }
*/


    public DistancesWithDropBloom2(int queryId, int source, int destination, Graph.Direction d,
                                   NewUnidirectionalDifferentialBFS.Queries queryType) {
        super(queryId, source, destination, d, queryType); // includes a call to init Dropped and Real Diff
        skipDiff = 0;
        dropRandomGenerator = new Random(generatorSeed);
        dropProbability = 0;
    }

    public static boolean debug(int vertexId) {

        //int[] debug_list = {3548303, 3916142, 3992819, 4172249, 4318075, 4378549, 4381441, 4906966, 5290991, 4172741, 4240094};
        //int[] debug_list = {3916212, 4670661, 5138142, 5490117, 5627416, 5627416};
/*
        int[] debug_list={}; // 53547

        for (int v:debug_list){
            if (vertexId == v)
                return true;
        }

 */
        return false;
    }

    public static void reset() {
        if (droppedDiffsReady) {
            droppedDiffs.clear();
            droppedDiffsReady = false;
        }
    }

    void initializeDroppedDiff(String type) {
        if (!droppedDiffsReady) {
            if (type.equals("Iteration")) {
                System.out.println("We do not support iteration Hash.");
                System.exit(1);
            } else {
                Report.INSTANCE.debug("Query Hash");
                droppedDiffs = new MultiQueryDiffBloomPerQuery(numQueries);
            }

            droppedDiffsReady = true;
        } else {
            return;
        }
    }

    /**
     * Clears the distances that are greater than t but not including t.
     *
     * @param t iteration number. Distances that vertices took in later
     *          iterations than t will be removed.
     */
    /*
    void clearDistancesAfterT(int t) {
        List<Integer> verticesToRemove = new ArrayList<>();
        for (Entry<Integer, List<Diff>> entry : vIterDistPairMap.entrySet()) {
            List<Diff> iterDistPairs = entry.getValue();
            int indexToRemove = iterDistPairs.size();
            for (int j = iterDistPairs.size() - 1; j >= 0; --j) {
                if (((Diff) iterDistPairs.get(j)).iterationNo <= t) {
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
        for (int i = t+1; i < minFrontierDistances.length; ++i) {
            minFrontierDistances[i] = null;
        }
    }

    */

    boolean skip(int vertexId) {

        if (vertexId == destination || vertexId == source) {
            return false;
        }

        boolean doNotskip =
                (dropRandomGenerator == null || dropRandomGenerator.nextInt(generatorRange) >= dropProbability);

        if (doNotskip) {
            keepCuonter++;
        } else {
            dropoCuonter++;
        }

        if (vertexId == destination || vertexId == source) {
            return false;
        }

        if ("Random".equals(dropType)) { // Random
            if (doNotskip) {
                return false;
            } else {
                return true;
            }
        } else { // Selective
            int vertexDegree = Graph.getInstance().getVertexDegree(vertexId, direction);
            if (vertexDegree >= maximumDegree || (vertexDegree > minimumDegree && doNotskip)) {
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * Check if there is any IN-neighbour that has a diff with iteration less than iterationNo
     * If not, then clear this iterationNo!
     *
     * @param vertexId
     * @param iterationNo
     */
    void clearVertexDistanceAtT(int vertexId, short iterationNo) {

        if (vertexId == source) {
            System.out.println(
                    "This is an error at clearVertexDistanceAtT - you cannot clear distance from source vertex = " +
                            vertexId);
            System.exit(1); // cannot clear a diff from source
        }

        // if the diff is useful then do not clear it
        if (isDiffUseful(vertexId, iterationNo)) {
            return;
        }

        removeVertexDistance(vertexId, iterationNo);

        // check in-neighbours to see if the vertex should be removed all together
        List<Short> iterations = getAllIterations(vertexId);
        for (short iter : iterations) {
            if (isDiffUseful(vertexId, iter)) {
                // if any diff is useful then return
                return;
            }
        }

        // if all diffs are not useful, then remove this vertex all together
        removeDiff(vertexId);
    }

    protected void removeVertexDistance(int vertexId, short iterationNo) {
        super.removeVertexDistance(vertexId, iterationNo);
        if (droppedDiffs.checkVertexIteration(queryId, vertexId, iterationNo)) {
            droppedDiffs.removeIteration(queryId, vertexId, iterationNo);
        }
    }

    /**
     * @param vertexId    ID of a vertex.
     * @param iterationNo iteration number for which to update the distance
     * @param distance    distance of the given vertex to the source in the given iteration number.
     */
    void setVertexDistance(int vertexId, short iterationNo, double distance) {

        setVertexDistanceCuonter++;
        // if we are going to add a new diff for this distance, but previously remove it we should not remember that
        // any more because vertices in this set will be deleted during merge time.
        toBeDeleted.remove(vertexId);

        if (debug(vertexId)) {
            Report.INSTANCE.error("----FAKE setVertexDistance v=" + vertexId + " iter= " + iterationNo + " distance= " +
                    distance + " ---- skipDiff " + (skipDiff + 1));
        }

        if (iterationNo == latestIteration && Double.MAX_VALUE != distance) {
            frontier.add(vertexId);
            nextFrontierSize += Graph.getInstance().getVertexDegree(vertexId, direction);

            if (debug(vertexId)) {
                Report.INSTANCE.error("** Adding vertex " + vertexId + " with direction " + direction +
                        " to make nextFrontier size = " + nextFrontierSize);
            }
        }

        /**
         * If the vertex/iteration have been dropped, it does not matter what new distance it will get.
         * We do not know if it exist because we are using bloom filter
         *
         boolean vertex_found_dropped = droppedDiffs.checkVertexIteration(queryId,vertexId,iterationNo);
         if (vertex_found_dropped)
         return;
         */

        long[] distances = getMergedDiffs(vertexId);
        if (!deltaDiffs.containsKey(vertexId)) {
            distances = distances.clone();
            deltaDiffs.put(vertexId, distances);
        }

        int newDist = (int) distance;
        long newDiff = (((long) iterationNo) << 32) | (newDist & 0xffffffffL);

        /**
         * We cannot relay only on dropped diffs to check if a vertex/iteration exist.
         * We should also check in the real diffs because
         * unlike Bloom, dropped diffs in hash does not include the real one
         *
         */
        int indexToAdd = 1;
        int len = (int) distances[0];
        while (indexToAdd <= len) {
            long diff = distances[indexToAdd];
            short iter = (short) (diff >> 32);
            if (iter == iterationNo) {
                if (debug(vertexId)) {
                    Report.INSTANCE
                            .error("***** Vertex " + vertexId + " with iteration " + iterationNo + " is found? " +
                                    true);
                }
                distances[indexToAdd] = newDiff;
                updateMinFrontierDistancesIfNecessary(vertexId, iter, distance, false); // not a deletion

                if (debug(vertexId)) {
                    Report.INSTANCE.error("---- Distances of v=" + vertexId + " = " +
                            Arrays.toString(getMergedDiffs(vertexId)) + " vs OLD = " +
                            Arrays.toString(getOldDiffs(vertexId)));
                }

                return;
            } else if (iter > iterationNo) {
                break;
            } else {
                indexToAdd++;
            }
        }

        /**
         * Here we decide if we should store distance or Not!
         */

        if (skip(vertexId)) {

            if (debug(vertexId)) {
                Report.INSTANCE.error("******** Vertex " + vertexId + " will be added to dropped vertices");
            }

            droppedDiffs.addDiffValues(queryId, vertexId, iterationNo, true);
        } else {

            if (debug(vertexId)) {
                Report.INSTANCE.error("******** Vertex " + vertexId + " will not be dropped");
            }

            if (len + 2 >= distances.length) {
                var distances2 = new long[Math.max(len + 2, (int) (distances.length * 1.2))];
                distances2[0] = len + 1;
                System.arraycopy(distances, 1, distances2, 1, indexToAdd - 1);
                distances2[indexToAdd] = newDiff;
                System.arraycopy(distances, indexToAdd, distances2, indexToAdd + 1, len - indexToAdd + 1);
                deltaDiffs.put(vertexId, distances2);
            } else {
                if (indexToAdd <= len) {
                    System.arraycopy(distances, indexToAdd, distances, indexToAdd + 1, len - indexToAdd + 1);
                }
                distances[0]++;
                distances[indexToAdd] = newDiff;
            }
        }

        //if(distances.size()>0 )
        //    deltaDiffs.put(vertexId,distances);
        //else
        //    distances = null; // mark it for GC

        updateMinFrontierDistancesIfNecessary(vertexId, iterationNo, distance, false /* is not deletion */);

        if (debug(vertexId)) {
            Report.INSTANCE
                    .error("---- Distances of v=" + vertexId + " = " + Arrays.toString(getMergedDiffs(vertexId)));
            Report.INSTANCE.error("---- Distance  of v=" + vertexId + " @ " + iterationNo + " = " +
                    this.getDistance(vertexId, iterationNo, true));
        }
    }

    double getDistance(int vertexId, short iterationNo) {

        if (vertexId == destination) {
            return getDestinationDistance(iterationNo, true);
        } else {

            System.out.println("===========================================");
            System.out.println("===========================================");
            System.out.println("===========================================");
            System.out.println("===========================================");
            System.out.println("  ERROR : this function should not be used!");
            System.out.println("===========================================");
            System.out.println(Thread.getAllStackTraces());

            System.exit(-1);
            return -1;
        }
    }

    /**
     * Clears the distances that are greater than t but not including t.
     *
     * @param t iteration number. Distances that vertices took in later
     *          iterations than t will be removed.
     */
    void clearDistancesAfterT(short t) {
        super.clearDistancesAfterT(t);
        List<Integer> verticesToRemove = new ArrayList<>();

        Diff diff = null;
        for (Integer vertex : this.getVerticesWithDiff()) {
            List<Short> iterations = getAllIterations(vertex);
            for (Short itr : iterations) {
                if (itr > t) {
                    droppedDiffs.removeIteration(queryId, vertex, itr);
                }
            }
        }
    }

    /**
     * @param iteration iteration number to set the latestIteration to.
     */
    void setLatestIterationNumber(short iterationNo) {

        Report.INSTANCE.debug("**set Iteration number to " + iterationNo);

        this.latestIteration = iterationNo;
        clearDistancesAfterT(iterationNo);
        frontier.clear();
        nextFrontierSize = 0;
        // get all vertices that have diffs
        for (Integer vertex : getVerticesWithDiff()) {
            if (isDiffExist(queryId, vertex, iterationNo) || isDiffUseful(vertex, iterationNo)) {
                frontier.add(vertex);
                nextFrontierSize += Graph.getInstance().getVertexDegree(vertex, direction);
            }
        }
    }


    /**
     * This is where I need to drop diffs before I can copy to real diffs!
     *
     * @param from
     * @param to
     */
    void prepareRealDiffs(int from) {
        var arr = realDiff.getDiffs(from);
        var arrNew = new HashMap<Integer, long[]>();
        for (var entry : arr.entrySet()) {
            var vertex = entry.getKey();
            var distances = entry.getValue();
            var distancesNew = new long[distances.length];
            var newLen = 0;
            int len = (int) distances[0];
            for (int j = 1; j <= len; j++) {
                long diff = distances[j];
                short iter = (short) (diff >> 32);
                if (skip(vertex)) {
                    droppedDiffs.addDiffValues(queryId, vertex, iter, true);
                } else {
                    newLen++;
                    distancesNew[newLen] = diff;
                }
            }
            if (newLen > 0) {
                /**
                 * This code creates an array that is exactly similar to the diff size then
                 * copies the real (not dropped) diffs from distancesNew to the shrank version:
                 *
                 */
                var distanceShrink = new long[newLen+1];
                distanceShrink[0] = newLen;
                for (int i=0;i<newLen;i++)
                    distanceShrink[i + 1] = distancesNew[i + 1];
                arrNew.put(vertex, distanceShrink);
            }
        }
        realDiff.setExactDiffs(queryId, arrNew);
    }

    public Set<Integer> getVerticesWithDiff() {
        // this one takes care of deleted vertices
        Set<Integer> allV = super.getVerticesWithDiff();
        //System.out.println("real = "+allV.size() + " dropped = "+ droppedDiffs.getVerticesWithDiff(queryId).size());
        //System.out.println("droppedV="+Arrays.toString(droppedDiffs.getVerticesWithDiff(queryId).toArray()));
        allV.addAll(droppedDiffs.getVerticesWithDiff(queryId));
        return allV;
    }

    public void reInitialize() {
        super.reInitialize();
        droppedDiffs.clear(queryId);
    }

    public void removeDiff(int v) {
        super.removeDiff(v);

        droppedDiffs.removeVertex(queryId, v);
    }

    /**
     * Very similar to getAllDistances but it returns a list of iterations instead
     *
     * @param vertexId ID of a vertex.
     * @return
     */
    List<Short> getAllIterations(int vertexId) {

        var results = super.getAllIterations(vertexId);
        if (results.isEmpty()) {
            return droppedDiffs.getIterationsWithDiff(queryId, vertexId);
        }
        return results;
    }


    /**
     * Very similar to getAllDistances but it returns a list of iterations instead
     *
     * @param vertexId ID of a vertex.
     * @return
     */
    List<Short> getAllIterations(int vertexId, long[] distances) {

        Set<Short> result = new HashSet<>();
        int len = (int) distances[0];
        for (int i = 1; i <= len; i++) {
            long diff = distances[i];
            short iter = (short) (diff >> 32);
            result.add(iter);
        }
        result.addAll(droppedDiffs.getIterationsWithDiff(queryId, vertexId));
            /*
        // for destinations, we do not drop distances
        if(vertexId!=destination)
            for(int iter = 1; iter<= droppedDiffs.getMaxIteration(queryId);iter ++){
                if (!result.contains(iter)){
                    if(droppedDiffs.checkVertexIteration(queryId,vertexId,iter))
                        result.add((short) iter);
                }
            }
*/
        return new ArrayList<>(result);
    }


    double getDestinationDistance(short iterationNo, boolean newBatches) {
        int vertexId = destination;
        long[] distances;

        if (newBatches) {
            distances = this.getMergedDiffs(vertexId);
            if (DistancesWithDropBloom2.debug(vertexId)) {
                Report.INSTANCE.error("NewBatches Diff: v= " + vertexId + " distances = " + Arrays.toString(distances));
            }
        } else {
            distances = this.getOldDiffs(vertexId); //vIterDistPairMap.get(vertexId);
            if (DistancesWithDropBloom2.debug(vertexId)) {
                Report.INSTANCE.error("OldBatches Diff: v= " + vertexId + " distances = " + Arrays.toString(distances));
            }
        }

        if (distances == null) {
            return Double.MAX_VALUE;
        } else {
            double latestDistance = Double.MAX_VALUE;

            List<Short> iterList = getAllIterations(vertexId, distances);
            Collections.sort(iterList);

            for (int j = iterList.size() - 1; j >= 0; j--) {
                if (iterList.get(j) > iterationNo) {
                    continue;
                }

                if (iterList.get(j) <= iterationNo) {
                    int maxUsefulIteration = iterList.get(j);
                    int len = (int) distances[0];
                    for (int i = 1; i <= len; i++) {
                        long diff = distances[i];
                        short iter = (short) (diff >> 32);
                        if (iter == maxUsefulIteration) {
                            latestDistance = (int) diff;
                            return latestDistance;
                        }
                    }
                }
            }

            if (DistancesWithDropBloom2.debug(vertexId)) {
                Report.INSTANCE.error("** FAKE v = " + vertexId + " distances = " + Arrays.toString(distances) +
                        " distance @ " + iterationNo + " = " + latestDistance);
            }

            //System.out.println("####### distance for "+vertexId+" is "+latestDistance);
            return latestDistance;
        }
    }

    /*
This function is only useful when we drop vertices. When we check if a diff exist, it is possible that Bloom says yes while no diff found yet.
In this case, the getDistance will call recalculateDistance and will use the vertex (withoutVertexId) to find the current distance.
Then, it will not report it because it think that it already exist but it was dropped.
 */
    double getNewDistance(int vertexId, short iterationNo,
                          boolean newBatches /*get distances from old batches or the current one*/,
                          int withoutVertexId) {

        if (DistancesWithDropBloom2.debug(vertexId)) {
            Report.INSTANCE
                    .error("----1-- FAKE getNewDistance - v= " + vertexId + " i= " + iterationNo + " newBatch? " +
                            newBatches);
        }

        if (vertexId == source) {
            return 0;
        }

        if (iterationNo <= 0) {
            return Double.MAX_VALUE;
        }

        long[] distances;
        if (newBatches) {
            distances = this.getMergedDiffs(vertexId); //vIterDistPairMap.get(vertexId);
            if (DistancesWithDropBloom2.debug(vertexId)) {
                Report.INSTANCE.error("NewBatches Diff: v= " + vertexId + " distances = " + Arrays.toString(distances));
            }
        } else {
            distances = this.getOldDiffs(vertexId); //vIterDistPairMap.get(vertexId);
            if (DistancesWithDropBloom2.debug(vertexId)) {
                Report.INSTANCE.error("OldBatches Diff: v= " + vertexId + " distances = " + Arrays.toString(distances));
            }
        }

        double latestDistance = Double.MAX_VALUE;

        int distances_index = (int) distances[0];

        while (distances_index >= 1) {
            long diff = distances[distances_index];
            short iter = (short) (diff >> 32);
            if (iter == iterationNo) {
                return (int) diff;
            } else if (iter > iterationNo) {
                distances_index--;
            } else {
                break;
            }
        }

        for (int i = iterationNo; i >= 0; i--) {
            if (distances_index >= 1 && ((short) (distances[distances_index] >> 32)) == (short) i) {
                latestDistance = (int) distances[distances_index];
                break;
            } else {
                if (droppedDiffs.checkVertexIteration(queryId, vertexId, (short) i)) {
                    latestDistance = recalculateDistance(vertexId, (short) i, newBatches);
                    break;
                }
            }
        }

        return latestDistance;


/*
            List<Short> iterList = getAllIterations(vertexId, distances);
            Collections.sort(iterList);

            if(DistancesWithDropHash.debug(vertexId))
                Report.INSTANCE.error("**** Available Iterations for v "+vertexId+" = "+Arrays.toString(iterList.toArray()));


            for(int j=iterList.size()-1;j>=0;j--){
                if(DistancesWithDropHash.debug(vertexId))
                    Report.INSTANCE.error("****** checking "+iterList.get(j)+" vs needed iteration =  "+iterationNo);

                if(iterList.get(j) > iterationNo)
                    continue;

                if(iterList.get(j) <= iterationNo){
                    // this is not a nested loop, because the following loop will run once
                    for(int i=distances.size()-1; i>=0;i--){
                        if(DistancesWithDropHash.debug(vertexId))
                            Report.INSTANCE.error("check-3 : v= "+vertexId+" compare with iteration = "+distances.get(i).iterationNo);

                        // There was a bug here - I have to get the exact iteration found not any other smaller iterations
                        if(distances.get(i).iterationNo==iterList.get(j)) {
                            latestDistance = ((IterationDistancePair) distances.get(i)).distance;
                            break;
                        }
                    }

                    if(latestDistance == Double.MAX_VALUE) {
                        latestDistance = recalculateDistance(vertexId, iterList.get(j), newBatches, withoutVertexId);

                        if(DistancesWithDropHash.debug(vertexId))
                            Report.INSTANCE.error("check-4 : v= "+vertexId+" distance not found, recalculate answer = "+latestDistance);

                    }



                    break;
                }
            }

            if(DistancesWithDropHash.debug(vertexId))
                Report.INSTANCE.error("** FAKE v = "+vertexId+ " distances = "+Arrays.toString(distances.toArray())+ " distance @ "+iterationNo+ " = "+latestDistance);


            //System.out.println("####### distance for "+vertexId+" is "+latestDistance);
            return latestDistance;
        }

 */
    }

    /***
     * Confirm that a diff exist, or should exist.
     *
     * @param q
     * @param vertex
     * @param iteration
     * @return
     */
    boolean isDiffExist(int q, int vertex, short iteration) {
        if (super.isDiffExist(q, vertex, iteration)) {
            return true;
        } else {
            return isDiffUseful(vertex, iteration);
        }
    }


    /**
     * @param vertexId    ID of a vertex.
     * @param iterationNo iteration at which the distance of the given vertex should be returned.
     * @return Distance of the vertex at the given iteration number. Note that if the vertex's
     * distance was not updated in the given iterationNo, the distance in the latest iteration less
     * than iterationNo is returned.
     */
    double getDistance(int vertexId, short iterationNo,
                       boolean newBatches/*get distances from old batches or the current one*/) {

        if (DistancesWithDropBloom2.debug(vertexId)) {
            Report.INSTANCE.error("----1-- FAKE getDistance - v= " + vertexId + " i= " + iterationNo + " newBatch? " +
                    newBatches);
        }

        return getNewDistance(vertexId, iterationNo, newBatches, -1);
    }

    public void print() {
        if (Report.INSTANCE.appReportingLevel == Report.Level.DEBUG) {
            super.print();
            Report.INSTANCE.debug("Dropped Vertices:");
            Report.INSTANCE.debug(droppedDiffs.visitedVertices_fast.toString());
        }
    }
}
