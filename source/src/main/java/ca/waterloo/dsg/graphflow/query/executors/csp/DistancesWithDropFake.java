package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.SortedAdjacencyList;
import ca.waterloo.dsg.graphflow.util.ArrayUtils;
import ca.waterloo.dsg.graphflow.util.Report;
import ca.waterloo.dsg.graphflow.util.VisibleForTesting;
import java.util.Random;

import java.util.*;
import java.util.Map.Entry;

/**
 * Represents a data structure that keeps track of the distances that each vertex took in different
 * iterations. Contains utility methods to add new distances or clear distances after some iteration
 * and keep track of the size of distances.
 */
public class DistancesWithDropFake  extends Distances{

    static int generatorSeed = 7293857;
    static int generatorRange = 100;
    protected int skipDiff;

    protected Random dropRandomGenerator;
    protected float dropProbability;


    /**
     * Default constructor with no source.
     *
     */
    public DistancesWithDropFake() {

        super();

        skipDiff = 0;
        dropRandomGenerator = new Random(generatorSeed);
        dropProbability = 0;
        diffPool = DiffPool.createPool(poolSize);
    }


    /**
     * Default public constructor.
     */
    public DistancesWithDropFake(int source, float prob) {
        super(source);

        skipDiff = 0;
        dropRandomGenerator = new Random(generatorSeed);
        dropProbability = prob;
        diffPool = DiffPool.createPool(poolSize);
    }


    /**
     * Default public constructor.
     */
    public DistancesWithDropFake(int source) {
        super(source);

        skipDiff = 0;
        dropRandomGenerator = new Random(generatorSeed);
        dropProbability = 0;

        diffPool = DiffPool.createPool(poolSize);
    }


    public DistancesWithDropFake(int source, Graph.Direction d, float prob) {
        super(source,d);

        skipDiff = 0;
        dropRandomGenerator = new Random(generatorSeed);
        dropProbability = prob * 100;

        diffPool = DiffPool.createPool(poolSize);
    }

    public DistancesWithDropFake(int source, Graph.Direction d) {
        super(source,d);

        skipDiff = 0;
        dropRandomGenerator = new Random(generatorSeed);
        dropProbability = 0;

        diffPool = DiffPool.createPool(poolSize);
    }

    boolean skip(){
        if(dropRandomGenerator!= null && dropRandomGenerator.nextInt(generatorRange) >= dropProbability)
            return true;
        else
            return false;
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
    void clearVertexDistanceAtTifNecessary(int vertexId, int iterationNo){

        SortedAdjacencyList incomingPermanentAdjList = Graph.INSTANCE.backwardAdjLists[vertexId];

        if(debug(vertexId))
            Report.INSTANCE.print(Report.Level.ERROR,"----FAKE clearVertexDistanceAtTifNecessary v="+vertexId+ " iter= "+iterationNo + " nbrs = "+Arrays.toString(incomingPermanentAdjList.neighbourIds));

        if (!SortedAdjacencyList.isNullOrEmpty(incomingPermanentAdjList)) {
            for (int j = 0; j < incomingPermanentAdjList.getSize(); j++) {
                int nbrID = incomingPermanentAdjList.neighbourIds[j];
                for(Diff nbr_diff:getMergedDiffs(nbrID)){

                    short nbr_iteration = nbr_diff.iterationNo;
                    if(debug(vertexId) )
                        Report.INSTANCE.print(Report.Level.ERROR,"** in-nbr "+nbrID +" has iteration "+nbr_iteration);

                    if (nbr_iteration == (iterationNo-1))
                        return;
                }
            }
        }


        clearVertexDistanceAtT(vertexId, iterationNo);

    }


    /**
     * @param vertexId ID of a vertex.
     * @param iterationNo iteration number for which to update the distance
     * @param distance distance of the given vertex to the source in the given iteration number.
     */
    void setVertexDistance(int vertexId, int iterationNo, double distance) {


        if(debug(vertexId))
            Report.INSTANCE.print(Report.Level.ERROR,"----FAKE setVertexDistance v="+vertexId+ " iter= "+iterationNo + " distance= "+distance+ " ---- skipDiff "+ (skipDiff+1));

        if (Double.MAX_VALUE == distance) {
            clearVertexDistancesAtAndBeforeT(vertexId, iterationNo);
        }
        if (iterationNo == latestIteration) {
            frontier.add(vertexId);
            nextFrontierSize += Graph.getInstance().getVertexDegree(vertexId,direction);

            if(debug(vertexId))
                Report.INSTANCE.print(Report.Level.ERROR,"** Adding vertex "+vertexId+" with direction "+direction+" to make nextFrontier size = "+nextFrontierSize);
        }

        List<Diff> distances = getMergedDiffs(vertexId);//vIterDistPairMap.get(vertexId);
        boolean fromDelta = (deltaDiffs.containsKey(vertexId))? true:false;

        if(!fromDelta)
            deltaDiffs.put(vertexId,distances);

        int indexToAdd = 0;
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
                Diff newDiff = (iterationDistancePair.getClass() == IterationDistancePair.class)?  iterationDistancePairPool.getObject((short) iterationNo,distance) : diffPool.getObject((short) iterationNo);
                Diff toBeDeleted = distances.remove(indexToAdd);
                returnDiff(toBeDeleted);
                distances.add(indexToAdd,newDiff);
                updateMinFrontierDistancesIfNecessary(vertexId, iterationDistancePair.iterationNo, false /* not a deletion */);

                if(debug(vertexId))
                    Report.INSTANCE.print(Report.Level.ERROR,"---- Distances of v="+vertexId+ " = "+Arrays.toString(getMergedDiffs(vertexId).toArray())+ " vs OLD = "+Arrays.toString(getOldDiffs(vertexId).toArray()));

                return;
            } else if (iterationDistancePair.iterationNo > iterationNo) {
                break;
            }
            indexToAdd++;
        }

        /**
         * Here we decide if we should store distance or Not!
         */
        Diff iterDistPair = null;

        if(skip() || vertexId==source)
            iterDistPair = iterationDistancePairPool.getObject((short) iterationNo ,  distance);
        else
            iterDistPair = diffPool.getObject((short) iterationNo);

        distances.add(indexToAdd, iterDistPair);

        updateMinFrontierDistancesIfNecessary(vertexId, iterDistPair.iterationNo, false /* is not deletion */);

        if(debug(vertexId))
            Report.INSTANCE.print(Report.Level.ERROR,"---- Distances of v="+vertexId+ " = "+Arrays.toString(getMergedDiffs(vertexId).toArray()));
    }

    public static boolean debug(int vertexId){


        //int[] debug_list = {3548303, 3916142, 3992819, 4172249, 4318075, 4378549, 4381441, 4906966, 5290991, 4172741, 4240094};
        //int[] debug_list = {3916212, 4670661, 5138142, 5490117, 5627416, 5627416};

        int[] debug_list = {};//, 3809302


        for (int v:debug_list){
            if (vertexId == v)
                return true;
        }


        return false;
    }

    double getDistance(int vertexId, int iterationNo){

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


    /**
     * @param vertexId ID of a vertex.
     * @param iterationNo iteration at which the distance of the given vertex should be returned.
     * @return Distance of the vertex at the given iteration number. Note that if the vertex's
     * distance was not updated in the given iterationNo, the distance in the latest iteration less
     * than iterationNo is returned.
     */
    double getDistance(int vertexId, int iterationNo, boolean newBatches/*get distances from old batches or the current one*/) {

        if(DistancesWithDropFake.debug(vertexId))
            Report.INSTANCE.print(Report.Level.ERROR,"------ FAKE getDistance - v= "+vertexId+" i= "+iterationNo+ " newBatch? "+newBatches);

        List<Diff> distances;
        if(newBatches)
            distances = this.getMergedDiffs(vertexId); //vIterDistPairMap.get(vertexId);
        else
            distances = this.getOldDiffs(vertexId); //vIterDistPairMap.get(vertexId);



        if (null == distances) {
            if(DistancesWithDropFake.debug(vertexId))
                Report.INSTANCE.print(Report.Level.ERROR,"------ FAKE getDistance - v= "+vertexId+" i= "+iterationNo+ " newBatch? "+newBatches + " = "+Double.MAX_VALUE);

            return Double.MAX_VALUE;
        } else {

            double latestDistance = Double.MAX_VALUE;
            for(int i=distances.size()-1; i>=0;i--){
                if(distances.get(i).iterationNo > iterationNo)
                    continue;

                // now, I should use the first iteration I C

                // if the distance is stored, then use it
                if(distances.get(i).getClass() == IterationDistancePair.class)
                    latestDistance = ((IterationDistancePair) distances.get(i)).distance;

                    // otherwise, when calculating the dropped distance, look for old/new computation
                else
                    latestDistance = recalculateDistance(vertexId,distances.get(i).iterationNo,newBatches);

                if(DistancesWithDropFake.debug(vertexId))
                    Report.INSTANCE.print(Report.Level.ERROR,"*** distance @ "+distances.get(i).iterationNo+ " = "+latestDistance);

                break;
            }

            if(DistancesWithDropFake.debug(vertexId))
                Report.INSTANCE.print(Report.Level.ERROR,"** FAKE v = "+vertexId+ " distances = "+Arrays.toString(distances.toArray())+ " distance @ "+iterationNo+ " = "+latestDistance);

            return latestDistance;
        }
    }


    IterationDistancePair getDistancePair(int vertexId, int iterationNo) {
        List<Diff> distances = getMergedDiffs(vertexId);//vIterDistPairMap.get(vertexId);

        Report.INSTANCE.print(Report.Level.DEBUG,"------- getDistancePair v = "+vertexId+ " @ "+iterationNo);

        resetPair1();

        if (null == distances) {
            pair1.iterationNo = (short) iterationNo;
            return pair1;

        } else {
            double latestDistance = Double.MAX_VALUE;
            for (Diff iterationDistance : distances) {
                if (iterationDistance.iterationNo > iterationNo) {
                    break;
                } else {

                    if(iterationDistance.getClass() == IterationDistancePair.class)
                        latestDistance = ((IterationDistancePair) iterationDistance).distance;
                    else
                        latestDistance = recalculateDistance(vertexId,iterationDistance.iterationNo, true);

                    pair1.distance = latestDistance;
                    pair1.iterationNo = iterationDistance.iterationNo;

                }
            }

            Report.INSTANCE.print(Report.Level.DEBUG,"**PAIR v = "+vertexId+ " distances = "+Arrays.toString(distances.toArray()));
            Report.INSTANCE.print(Report.Level.DEBUG,"**PAIR v = "+vertexId+ " @ "+iterationNo+ " = "+latestDistance);
            return pair1;
        }
    }

    public int size() {
        int size = 0;
        for(Integer vertex:getVerticesWithDiff()){
            for(Diff diff:getMergedDiffs(vertex))
                if(diff.getClass() == IterationDistancePair.class)
                    size ++;
        }
        return size;
    }

}
