package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.util.ArrayUtils;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;



import java.util.ArrayList;

/**
 *
 * This is a class that represent a data structure to store diffs permanently as we process
 * multiple batches. We do not want to interact with the map directly because we want to encapsulate the details of
 * how the data structure of diffs are implemented inside this class.
 *
 */
public class MultiQueryDiffHashPerQuery2 extends MultiQueryDiffHash{

    static int numQueries = 0;
    ArrayList<Int2IntOpenHashMap> sharedVertex;
    ArrayList<int[]> sharedBlock;
    Int2IntOpenHashMap lastUsedLocation;
    static ShortOpenHashSet emptySet = new ShortOpenHashSet(0);
    /* Removing caching because it adds errors during build
    // this data structure is helpful to keep most important diffs cached so that we do not keep re-computing it
    ArrayList<DiffCache<Pair<Integer,Short>, Double>> cachedDiffs;
*/

    /**
     * Default constructor
     */
    public MultiQueryDiffHashPerQuery2(){

    }

    /**
     * Get the location of the first iteration of a vertex in the sharedBlock
     * @param q
     * @param vertex
     * @return vertex location on block or -1
     */
    public int getVertexLocation(int q, int vertex)
    {
        return sharedVertex.get(q).get(vertex);
    }

    public ShortOpenHashSet getVertexIterations(int q, int vertex){
        int location = getVertexLocation(q,vertex);

        if (location > 0){
            ShortOpenHashSet result = new ShortOpenHashSet();
            while (location>0){
                short iteration = (short) sharedBlock.get(q)[location];
                location = sharedBlock.get(q)[location+1];
                result.add(iteration);
            }
            return result;
        }
        else return emptySet;
    }

    public int getLatestVertexIterationsLocation(int q, int vertex){
        int location = getVertexLocation(q,vertex);
        while (location>0){
            short iteration = (short) sharedBlock.get(q)[location];
            if (sharedBlock.get(q)[location+1] > 0)
                location = sharedBlock.get(q)[location+1];
            else
                break;
        }

        return location;
    }


    /**
     * This function is accurate, not like bloom one
     *
     * @param queryId
     * @param vertex
     * @param iterationNo
     * @return
     */
    public boolean checkVertexIteration(int queryId, int vertex, short iterationNo){

        if (getVertexIterations(queryId,vertex).contains(iterationNo))
            return true;
        else
            return false;
    }


    /**
     *
     * @param q = number of queries
     */
    public MultiQueryDiffHashPerQuery2(int q){
        numQueries = q;

        sharedBlock = new ArrayList<>(q);
        sharedVertex = new ArrayList<>(q);
        lastUsedLocation = new Int2IntOpenHashMap(q);
        lastUsedLocation.defaultReturnValue(-1);
        for(int i=0;i<numQueries;i++) {
            sharedVertex.add(new Int2IntOpenHashMap());
            sharedVertex.get(i).defaultReturnValue(-1);
            sharedBlock.add(new int[1000000]);
            lastUsedLocation.put(i,0);
        }

        //System.out.println("Queries = "+ numQueries + " ---------- "+visitedVertices_fast.size());
        //System.out.println("Queries = "+ numQueries + " ---------- "+getvisitedVertices_fastsize());

/*
        cachedDiffs = new ArrayList<>(numQueries);
        for(int i=0;i<numQueries;i++)
            cachedDiffs.add(new DiffCache<>(cacheSize));

 */
    }

    public void clear(int q) {
        sharedVertex.set(q, new Int2IntOpenHashMap());
        sharedBlock.set(q,new int[1000000]);
    }

    /**
     * A function that returns a set of all vertices with one or more diffs.
     * This query is very expensive when using bloom filter. It is only needed for reporting or
     * to "findNewMinDistanceVertexInFrontier". The latter is needed when a vertex increase its distance because of a delete or an edge update.
     * For now, I will not support those and return an empty set.
     * @return
     */
    public int[] getVerticesWithDiff(int q){
        return sharedVertex.get(q).keySet().toIntArray();
    }

    public int getNumberVerticesWithDiff(int q){
        return sharedVertex.get(q).keySet().size();
    }

    private void ensureCapacity(int q, int minCapacity) {
        if (minCapacity > sharedBlock.get(q).length)
            sharedBlock.set(q, ArrayUtils.resizeIfNecessary(sharedBlock.get(q),minCapacity));
    }

    public void addDiffValues(int q, int vertex, short iteration, boolean dropped){

        // last location the vertex stored a diff in the block
        int lastLocation = getLatestVertexIterationsLocation(q,vertex);

        if (lastLocation > 0){

            // check that this iteration does not already exist
            ShortOpenHashSet iterations = getVertexIterations(q,vertex);
            if (iterations.contains(iteration))
                    return;


            // update last location in the block
            lastUsedLocation.addTo(q,2);
            int nextLocation = lastUsedLocation.get(q);
            ensureCapacity(q,nextLocation+2);

            // add a pointer to the next location
            sharedBlock.get(q)[lastLocation+1] = nextLocation;
            // add iteration information
            sharedBlock.get(q)[nextLocation] = iteration;
            //reset the pointer of the next location
            sharedBlock.get(q)[nextLocation+1] = -1;
        }
        else{// this the first time to add this vertex to the block

            // update last location in the block
            lastUsedLocation.addTo(q,2);
            int nextLocation = lastUsedLocation.get(q);

            // make sure there is still space in the array
            ensureCapacity(q,nextLocation+2);

            // add vertex to the hash table
            sharedVertex.get(q).put(vertex,nextLocation);

            // add iteration information
            sharedBlock.get(q)[nextLocation] = iteration;
            //reset the pointer of the next location
            sharedBlock.get(q)[nextLocation+1] = -1;
        }
    }

/*
Removing caching because it adds errors during build
    // add a vertex to the cache
    public void cacheDiff(int query, int vertex, short iteration, double diff){
        cachedDiffs.get(query).put(new Pair<>(vertex,iteration),diff);
    }

    public Double getCachedDiff(int query, int vertex, short iteration){
        return cachedDiffs.get(query).get( new Pair<>(vertex,iteration));
    }
*/
    /**
     * Return a list of iterations that a vertex might have diffs for
     * @param q = the query context
     * @param vertex = the vertex in question
     * @return
     */
    public ShortOpenHashSet getIterationsWithDiff(int q,int vertex){
        return getVertexIterations(q,vertex);
    }



    public void removeVertex(int queryId, int vertex){

        int location = getVertexLocation(queryId, vertex);
        if (location < 0)
            return;

        //TODO: This means we do not clean up the block, and it may contains diffs
        // that we can remove. However, it is expensive to do this cleanup every
        // time we remove a vertex and it should be done every several removes.
        sharedVertex.get(queryId).remove(vertex);

    }
}
