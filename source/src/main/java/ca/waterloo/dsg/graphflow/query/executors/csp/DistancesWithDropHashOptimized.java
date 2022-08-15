package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.SortedAdjacencyList;
import ca.waterloo.dsg.graphflow.util.Report;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;

import java.util.*;
import java.util.Map.Entry;

/**
 * Represents a data structure that keeps track of the distances that each vertex took in different
 * iterations. Contains utility methods to add new distances or clear distances after some iteration
 * and keep track of the size of distances.
 *
 * We keep diffs in Hash table instead of Bloom!
 *
 */
public class DistancesWithDropHashOptimized extends Distances{

    static int generatorSeed = 7293857;
    static int generatorRange = 100;
    protected int skipDiff;

    protected Random dropRandomGenerator;
    protected float dropProbability;
    protected static MultiQueryDiffHash droppedDiffs;
    String type;
    String dropType;
    protected static boolean droppedDiffsReady = false;

    int minimumDegree; // any vertex has less or equal degree will be dropped
    int maximumDegree; // any vertex has more or equal degree will be kept

    static int[] debug_list = {};//, 3809302


    void initializeDroppedDiff(String type){
        if(!droppedDiffsReady) {
            if(type.equals("Iteration")) {
                System.out.println("We do not support iteration Hash.");
                System.exit(1);
            }
            else {
                System.out.println("Query Hash");
                droppedDiffs = new MultiQueryDiffHashPerQuery2(numQueries);
            }

            droppedDiffsReady = true;
        }
        else return;
    }

    /**
     * Default constructor with no source.
     *
     */
    public DistancesWithDropHashOptimized() {

        super();
        initializeDroppedDiff();
        skipDiff = 0;
        dropRandomGenerator = new Random(generatorSeed);
        dropProbability = 0;
    }


    /**
     * merge two lists - this is only valid when we use bloom  filters
     */


    public void mergeDeltaDiffs(){
        // merge two lists
        realDiff.mergeDeltaDiffs(queryId, deltaDiffs, toBeDeleted);

        // Create vertex stats when needed
        if(Report.INSTANCE.appReportingLevel == Report.Level.INFO){
            /*
            for(Integer v:deltaDiffs.keySet()){
                VertexStats stats = vertexHistory.get(v);

                if (null == stats) {
                    stats = new VertexStats();
                    vertexHistory.put(v, stats);
                }
            }

             */
        }

        deltaDiffs.clear();
        toBeDeleted.clear();
        //newCache.clear();
        //oldCache.clear();
    }

    /**
     * Default public constructor.
     */
    public DistancesWithDropHashOptimized(int queryId, int source, int destination, float prob) {
        super(queryId, source, destination); // includes a call to init Dropped and Real Diff

        skipDiff = 0;
        dropRandomGenerator = new Random(generatorSeed);
        dropProbability = prob;
    }


    /**
     * Default public constructor.
     */
    public DistancesWithDropHashOptimized(int queryId, int source, int destination) {
        super(queryId, source, destination); // includes a call to init Dropped and Real Diff
        skipDiff = 0;
        dropRandomGenerator = new Random(generatorSeed);
        dropProbability = 0;
    }


    public DistancesWithDropHashOptimized(int queryId, int source, int destination, Graph.Direction d, float prob) {
        super(queryId,source,destination, d); // includes a call to init Dropped and Real Diff
        skipDiff = 0;
        dropRandomGenerator = new Random(generatorSeed);
        dropProbability = prob * 100;
    }

    public DistancesWithDropHashOptimized(int queryId, int source, int destination, Graph.Direction d, float prob, String dropType, String type, int minimumDegree, int maximumDegree) {
        super(queryId,source,destination, d, type); // includes a call to init Dropped and Real Diff
        skipDiff = 0;
        dropRandomGenerator = new Random(generatorSeed);
        dropProbability = prob * 100;
        this.type = type;
        this.dropType = dropType;
        this.minimumDegree = minimumDegree;
        this.maximumDegree = maximumDegree;
    }

    public DistancesWithDropHashOptimized(int queryId, int source, int destination, Graph.Direction d) {
        super(queryId, source,destination, d); // includes a call to init Dropped and Real Diff
        skipDiff = 0;
        dropRandomGenerator = new Random(generatorSeed);
        dropProbability = 0;
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


    /**
     * This version is used for Random Drop
     *
     * @param vertexId
     * @return
     */
    static public int keepCuonter = 0;
    static public int dropoCuonter = 0;
    boolean skip(int vertexId){

        if (vertexId == destination || vertexId == source)
            return false;

        boolean doNotskip = (dropRandomGenerator== null || dropRandomGenerator.nextInt(generatorRange) >= dropProbability);

        if(doNotskip)
            keepCuonter++;
        else
            dropoCuonter++;

        if (vertexId == destination || vertexId == source)
            return false;

        if("Random".equals(dropType)) { // Random
            if (doNotskip)
                return false;
            else
                return true;
        }
        else { // Selective
            int vertexDegree = Graph.getInstance().getVertexDegree(vertexId, direction);
            if(vertexDegree>=maximumDegree || (vertexDegree>minimumDegree &&  doNotskip))
                return false;
            else
                return true;
        }
    }


    /**
     * Clears the distances that are greater than t but not including t.
     *
     * @param t iteration number. Distances that vertices took in later
     * iterations than t will be removed.
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

    /**
     * Check if there is any IN-neighbour that has a diff with iteration less than iterationNo
     * If not, then clear this iterationNo!
     *
     * @param vertexId
     * @param iterationNo
     */
    void clearVertexDistanceAtT(int vertexId, short iterationNo){

        if (vertexId == source) {
            System.out.println("This is an error at clearVertexDistanceAtT - you cannot clear distance from source vertex = "+vertexId);
            System.exit(1); // cannot clear a diff from source
        }

        removeVertexDistance(vertexId,iterationNo);

        // check in-neighbours to see if the vertex should be removed all together
        SortedAdjacencyList incomingPermanentAdjList = Graph.INSTANCE.getBackwardMergedAdjacencyList(vertexId);
        if (!SortedAdjacencyList.isNullOrEmpty(incomingPermanentAdjList)){
            ShortOpenHashSet iterations = getAllIterations(vertexId);
            for (short iter:iterations){
                if (isDiffUseful(vertexId,iter)){
                    // if any diff is useful then return
                    return;
                }
            }
            // if all diffs are not useful, then remove this vertex all together
            removeDiff(vertexId);
        }
        removeDiff(vertexId);

    }

    /**
     *
     *
     * @param queryId
     * @param vertexId
     * @param iteration
     * @return  1 if it found in real, 2 if found in dropped, 0 if not found
     */
    short checkVertexIteration(int queryId, int vertexId, short iteration){

        boolean real = realDiff.checkVertexIteration(queryId,vertexId,iteration);
        boolean dropped = droppedDiffs.checkVertexIteration(queryId,vertexId,iteration);

        if (real)
            return 1;
        else if (dropped)
            return 2;
        else
            return 0;
    }

    /**
     * @param vertexId ID of a vertex.
     * @param iterationNo iteration number for which to update the distance
     * @param distance distance of the given vertex to the source in the given iteration number.
     */
    void setVertexDistance(int vertexId, short iterationNo, double distance) {

        setVertexDistanceCuonter++;
        // if we are going to add a new diff for this distance, but previously remove it we should not remember that
        // any more because vertices in this set will be deleted during merge time.
        toBeDeleted.remove(vertexId);

        if(debug(vertexId))
            Report.INSTANCE.print(Report.Level.ERROR,"----FAKE setVertexDistance v="+vertexId+ " iter= "+iterationNo + " distance= "+distance+ " ---- skipDiff "+ (skipDiff+1));

        if (iterationNo == latestIteration && Double.MAX_VALUE != distance) {
            frontier.add(vertexId);
            nextFrontierSize += Graph.getInstance().getVertexDegree(vertexId,direction);

            if(debug(vertexId))
                Report.INSTANCE.print(Report.Level.ERROR,"** Adding vertex "+vertexId+" with direction "+direction+" to make nextFrontier size = "+nextFrontierSize);
        }

        List<Diff> distances = getMergedDiffs(vertexId);//vIterDistPairMap.get(vertexId);
        // TODO:This might be needed
        if(!deltaDiffs.containsKey(vertexId))
            distances = new ArrayList<>(distances);



        int indexToAdd = 0;

        short vertex_found = checkVertexIteration(queryId,vertexId,iterationNo);
        if(debug(vertexId))
            Report.INSTANCE.print(Report.Level.ERROR,"***** Vertex "+vertexId+" with iteration "+iterationNo+" is found? "+vertex_found);

        if(vertex_found==1){
            // found in real, we need to fix its distance

            if(debug(vertexId))
                Report.INSTANCE.print(Report.Level.ERROR,"***** Vertex "+vertexId+" with iteration "+iterationNo+" is found? "+vertex_found);

            while (indexToAdd < distances.size()) {
                Diff iterationDistancePair = distances.get(indexToAdd);
                if (iterationDistancePair.iterationNo == iterationNo) {

                    // I did not check or increase skipDiff because in this case we are editing an existing distance
                    // It will not save us to remove an existing distance

                    /**
                     * We need to create a new class for an updated Diff because the distances list we have was made
                     * using a shallow clone. This means it is a new list, but the objects in it are shared with the old-Diff.
                     *
                     * This is helpful to reduce memory consumption and class creation but would lead to problems when
                     * the old and new lists use the same object in the list.
                     *
                     */
                    IterationDistancePair newDiff = (IterationDistancePair) distances.remove(indexToAdd);
                    newDiff.iterationNo = iterationNo;
                    newDiff.distance = distance;
                    distances.add(indexToAdd,newDiff);

                    updateMinFrontierDistancesIfNecessary(vertexId, iterationNo, distance, false /* not a deletion */);

                    if(debug(vertexId))
                        Report.INSTANCE.print(Report.Level.ERROR,"---- Distances of v="+vertexId+ " = "+Arrays.toString(getMergedDiffs(vertexId).toArray())+ " vs OLD = "+Arrays.toString(getOldDiffs(vertexId).toArray()));

                    deltaDiffs.put(vertexId,distances);

                    return;
                } else if (iterationDistancePair.iterationNo > iterationNo) {

                    if(debug(vertexId))
                        Report.INSTANCE.print(Report.Level.ERROR,"** Vertex "+vertexId+" with iteration "+iterationNo+" reached a diff with large iteration @ "+iterationDistancePair.iterationNo);

                    break;
                }
                indexToAdd++;
            }
        }
        else
        {
            // not found at all
            if(debug(vertexId))
                Report.INSTANCE.print(Report.Level.ERROR,"***** Vertex "+vertexId+" with iteration "+iterationNo+" is Not found! ");

        }

        /**
         * Here we decide if we should store distance or Not!
         */


        if(skip(vertexId)){

            if(debug(vertexId))
                Report.INSTANCE.print(Report.Level.ERROR,"******** Vertex "+vertexId+" will be added to dropped vertices");

            distances = null;
            droppedDiffs.addDiffValues(queryId, vertexId,  iterationNo, true);
        }else{

            if(debug(vertexId))
                Report.INSTANCE.print(Report.Level.ERROR,"******** Vertex "+vertexId+" will not be dropped");

            Diff iterDistPair =  new IterationDistancePair( iterationNo, distance);

            if(indexToAdd==0){
                // There is a risk that we skipped updating indexToAdd using Bloom. Let us make sure it is correct here:
                // because we jumped to here quickly using bloom filter, we need to update the indexToAdd to match the correct position of this diff
                while(indexToAdd<distances.size() && distances.get(indexToAdd).iterationNo<iterationNo){
                    indexToAdd++;
                }

            }
            distances.add(indexToAdd, iterDistPair);
            deltaDiffs.put(vertexId,distances);
            // I want bloom filter to better represent all dropped and non-dropped diffs
            //droppedDiffs.addDiffValues(queryId,vertexId, iterationNo, false);
        }


        //if(distances.size()>0 )
        //    deltaDiffs.put(vertexId,distances);
        //else
        //    distances = null; // mark it for GC

        updateMinFrontierDistancesIfNecessary(vertexId,iterationNo, distance, false /* is not deletion */);

        if(debug(vertexId)) {
            Report.INSTANCE.print(Report.Level.ERROR, "---- Distances of v=" + vertexId + " = " + Arrays.toString(getMergedDiffs(vertexId).toArray()));
            Report.INSTANCE.print(Report.Level.ERROR, "---- Distance  of v=" + vertexId + " @ " + iterationNo + " = " + this.getDistance(vertexId, iterationNo,true));
        }
    }

    public static boolean debug(int vertexId){


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

    double getDistance(int vertexId, short iterationNo){


        if (vertexId==destination){
            return getDestinationDistance(iterationNo,true);
        }
        else {

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
     * @param iterationNo iteration number to set the latestIteration to.
     */
    void setLatestIterationNumber(short iterationNo) {

        Report.INSTANCE.print(Report.Level.DEBUG,"**set Iteration number to "+iterationNo);

        this.latestIteration = iterationNo;
        clearDistancesAfterT(iterationNo);
        frontier.clear();
        nextFrontierSize = 0;
        // get all vertices that have diffs
        for (Integer vertex : getVerticesWithDiff()) {
            if (checkVertexIteration(queryId,vertex,iterationNo) > 0) {
                frontier.add(vertex);
                nextFrontierSize += Graph.getInstance().getVertexDegree(vertex,direction);
            }
        }
    }


    public IntOpenHashSet getVerticesWithDiff(){
        IntOpenHashSet allV= super.getVerticesWithDiff();
        int[] droppedVertices = droppedDiffs.getVerticesWithDiff(queryId);
        for(int i=0;i<droppedDiffs.getNumberVerticesWithDiff(queryId);i++)
            allV.add(droppedVertices[i]);

        return allV;
    }
    public void reInitialize(){
        super.reInitialize();
        droppedDiffs.clear(queryId);
    }

    public void removeDiff(int v){
        super.removeDiff(v);

        droppedDiffs.removeVertex(queryId,v);
    }

    /**
     *
     * Very similar to getAllDistances but it returns a list of iterations instead
     *
     * @param vertexId ID of a vertex.
     * @return
     */
    ShortOpenHashSet getAllIterations(int vertexId) {

        List<Diff> retVal = getMergedDiffs(vertexId); //vIterDistPairMap.get(vertexId);
        if (null != retVal){
            return getAllIterations(vertexId,retVal);
        }
        else
            return droppedDiffs.getIterationsWithDiff(queryId,vertexId);
    }


    /**
     *
     * Very similar to getAllDistances but it returns a list of iterations instead
     *
     * @param vertexId ID of a vertex.
     * @return
     */
    ShortOpenHashSet getAllIterations(int vertexId, List<Diff> retVal) {
        ShortOpenHashSet result = new ShortOpenHashSet();
        for(Diff pair : retVal) result.add(pair.iterationNo);

        result.addAll(droppedDiffs.getIterationsWithDiff(queryId,vertexId));
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
        return result;
    }


    double getDestinationDistance(short iterationNo,boolean newBatches){
        int vertexId = destination;
        List<Diff> distances;

        if(newBatches) {
            distances = this.getMergedDiffs(vertexId);
            if(DistancesWithDropHash.debug(vertexId))
                Report.INSTANCE.print(Report.Level.ERROR,"NewBatches Diff: v= "+vertexId+" distances = "+Arrays.toString(distances.toArray()));
        }
        else {
            distances = this.getOldDiffs(vertexId); //vIterDistPairMap.get(vertexId);
            if(DistancesWithDropHash.debug(vertexId))
                Report.INSTANCE.print(Report.Level.ERROR,"OldBatches Diff: v= "+vertexId+" distances = "+Arrays.toString(distances.toArray()));
        }

        if (distances == null)
            return Double.MAX_VALUE;
        else {
            double latestDistance = Double.MAX_VALUE;

            short[] iterList = getAllIterations(vertexId, distances).toShortArray();
            Arrays.sort(iterList);


            for(int j=iterList.length-1;j>=0;j--){
                if(iterList[j] > iterationNo)
                    continue;

                if(iterList[j] <= iterationNo){
                    int maxUsefulIteration = iterList[j];

                    for (Diff diff : distances)
                        if (diff.iterationNo == maxUsefulIteration) {
                            latestDistance = ((IterationDistancePair) diff).distance;
                            return latestDistance;
                        }
                }
            }

            if(DistancesWithDropHash.debug(vertexId))
                Report.INSTANCE.print(Report.Level.ERROR,"** FAKE v = "+vertexId+ " distances = "+Arrays.toString(distances.toArray())+ " distance @ "+iterationNo+ " = "+latestDistance);


            //System.out.println("####### distance for "+vertexId+" is "+latestDistance);
            return latestDistance;

        }

    }

    /*
This function is only useful when we drop vertices using Bloom. When we check if a diff exist, it is possible that Bloom says yes while no diff found yet.
In this case, the getDistance will call recalculateDistance and will use the vertex (withoutVertexId) to find the current distance.
Then, it will not report it because it think that it already exist but it was dropped.
 */
    double getNewDistance (int vertexId, short iterationNo, boolean newBatches /*get distances from old batches or the current one*/, int withoutVertexId){

        if(DistancesWithDropHash.debug(vertexId))
            Report.INSTANCE.print(Report.Level.ERROR,"----1-- FAKE getNewDistance - v= "+vertexId+" i= "+iterationNo+ " newBatch? "+newBatches);

        if(vertexId==source)
            return 0;

        // if there is no entry in visitedVertices, then no diffs, and then distance is MAX
        // when we are looking for the current batch
        if(newBatches && !getVerticesWithDiff().contains(vertexId)){
            return Double.MAX_VALUE;
        }

        // for old batch, we should also make sure that it was *not* deleted this batch
        if(!newBatches && !getVerticesWithDiff().contains(vertexId) &&
                !this.toBeDeleted.contains(vertexId)){
            return Double.MAX_VALUE;
        }


        if(iterationNo<=0)
            return Double.MAX_VALUE;

        List<Diff> distances;
        if(newBatches) {
            distances = this.getMergedDiffs(vertexId); //vIterDistPairMap.get(vertexId);
            if(DistancesWithDropHash.debug(vertexId))
                Report.INSTANCE.print(Report.Level.ERROR,"NewBatches Diff: v= "+vertexId+" distances = "+Arrays.toString(distances.toArray()));
        }
        else {
            distances = this.getOldDiffs(vertexId); //vIterDistPairMap.get(vertexId);
            if(DistancesWithDropHash.debug(vertexId))
                Report.INSTANCE.print(Report.Level.ERROR,"OldBatches Diff: v= "+vertexId+" distances = "+Arrays.toString(distances.toArray()));
        }


        // if distances are empty, then I have to recalculate
        if (null == distances || distances.isEmpty()) {
            if(DistancesWithDropHash.debug(vertexId))
                Report.INSTANCE.print(Report.Level.ERROR,"----2-- FAKE getDistance - v= "+vertexId+" i= "+iterationNo+ " newBatch? "+newBatches + " = "+Double.MAX_VALUE);

            //Report.INSTANCE.print(Report.Level.ERROR,"----N-- FAKE Recalculate - v= "+vertexId+" i= "+iterationNo+ " newBatch? "+newBatches + " = "+withoutVertexId);
            return recalculateDistance(vertexId, iterationNo, newBatches, withoutVertexId);

        } else {


            double latestDistance = Double.MAX_VALUE;

            short[] iterList = getAllIterations(vertexId, distances).toShortArray();
            Arrays.sort(iterList);

            if(DistancesWithDropHash.debug(vertexId))
                Report.INSTANCE.print(Report.Level.ERROR,"**** Available Iterations for v "+vertexId+" = "+Arrays.toString(iterList));


            for(int j=iterList.length-1;j>=0;j--){
                if(DistancesWithDropHash.debug(vertexId))
                    Report.INSTANCE.print(Report.Level.ERROR,"****** checking "+iterList[j]+" vs needed iteration =  "+iterationNo);

                if(iterList[j] > iterationNo)
                    continue;

                if(iterList[j] <= iterationNo){
                    // this is not a nested loop, because the following loop will run once
                    for(int i=distances.size()-1; i>=0;i--){
                        if(DistancesWithDropHash.debug(vertexId))
                            Report.INSTANCE.print(Report.Level.ERROR,"check-3 : v= "+vertexId+" compare with iteration = "+distances.get(i).iterationNo);

                        // There was a bug here - I have to get the exact iteration found not any other smaller iterations
                        if(distances.get(i).iterationNo==iterList[j]) {
                            latestDistance = ((IterationDistancePair) distances.get(i)).distance;
                            break;
                        }
                    }

                    if(latestDistance == Double.MAX_VALUE) {
                        latestDistance = recalculateDistance(vertexId, iterList[j], newBatches, withoutVertexId);

                        if(DistancesWithDropHash.debug(vertexId))
                            Report.INSTANCE.print(Report.Level.ERROR,"check-4 : v= "+vertexId+" distance not found, recalculate answer = "+latestDistance);

                    }



                    break;
                }
            }

            if(DistancesWithDropHash.debug(vertexId))
                Report.INSTANCE.print(Report.Level.ERROR,"** FAKE v = "+vertexId+ " distances = "+Arrays.toString(distances.toArray())+ " distance @ "+iterationNo+ " = "+latestDistance);


            //System.out.println("####### distance for "+vertexId+" is "+latestDistance);
            return latestDistance;
        }
    }

    /***
     * Confirm that a diff exist, or should exist.
     *
     * @param q
     * @param vertex
     * @param iteration
     * @return
     */
    boolean isDiffExist(int q, int vertex, short iteration){
        if (checkVertexIteration(q,vertex,iteration) > 0)
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
    boolean isDiffUseful(int vertexId, short iterationNo){
        double distanceCurrent = getDistance(vertexId,iterationNo, true);
        double distancePrevious = getDistance(vertexId,(short) (iterationNo-1),true);

        // check if this diff has an impact
        if (distanceCurrent < distancePrevious &&  Math.abs(distanceCurrent - distancePrevious) > 0.0001)
            return true;
        else
            return false;
    }

    /**
     * @param vertexId ID of a vertex.
     * @param iterationNo iteration at which the distance of the given vertex should be returned.
     * @return Distance of the vertex at the given iteration number. Note that if the vertex's
     * distance was not updated in the given iterationNo, the distance in the latest iteration less
     * than iterationNo is returned.
     */
    double getDistance(int vertexId, short iterationNo, boolean newBatches/*get distances from old batches or the current one*/) {

        if(DistancesWithDropHash.debug(vertexId))
            Report.INSTANCE.print(Report.Level.ERROR,"----1-- FAKE getDistance - v= "+vertexId+" i= "+iterationNo+ " newBatch? "+newBatches);

        return getNewDistance(vertexId,iterationNo,newBatches,-1);
    }

    public int size() {
        int size = 0;
        for(Entry<Integer,List<Diff>> entry: realDiff.getDiffs(queryId).entrySet()){
            size += entry.getValue().size();
        }
        return size;
    }

}
