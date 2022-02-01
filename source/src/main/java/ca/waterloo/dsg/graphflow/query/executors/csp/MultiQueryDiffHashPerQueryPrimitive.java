package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.util.ArrayUtils;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

import java.util.*;

/**
 * This is a class that represent a data structure to store diffs permanently as we process
 * multiple batches. We do not want to interact with the map directly because we want to encapsulate the details of
 * how the data structure of diffs are implemented inside this class.
 */
public class MultiQueryDiffHashPerQueryPrimitive extends MultiQueryDiffHash {

    static int numQueries = 0;
    static int INIT_DROPPED_VERTEX = 10000;
    static int SHORT_MASK = 0xffff;
    ArrayList<Int2IntOpenHashMap> sharedVertex;
    ArrayList<short[]> sharedBlock;
    Int2IntOpenHashMap lastUsedLocation;
    static Map<Integer,Set<Short>> deletedDiffs;
    static Set<Integer> deletedVertices;

    //    public ArrayList<Map<Integer,ShortOpenHashSet>> getSharedDiffs(){
    //        return sharedDiffs;
    //    }
/* Removing caching because it adds errors during build
    // this data structure is helpful to keep most important diffs cached so that we do not keep re-computing it
    ArrayList<DiffCache<Pair<Integer,Short>, Double>> cachedDiffs;
*/

    /**
     * Default constructor
     */
    public MultiQueryDiffHashPerQueryPrimitive() {

    }

    /**
     * @param q = number of queries
     */
    public MultiQueryDiffHashPerQueryPrimitive(int q) {
        numQueries = q;

        sharedBlock = new ArrayList<>(numQueries);
        sharedVertex = new ArrayList<>(numQueries);
        lastUsedLocation = new Int2IntOpenHashMap(numQueries);
        lastUsedLocation.defaultReturnValue(-1);
        // for dummy query 0, no need to create any thing
        sharedBlock.add(null);
        sharedVertex.add(null);
        deletedDiffs = new HashMap<>(1);
        deletedVertices = new HashSet<>(1);

        for (int i = 1; i <= numQueries; i++) {
            sharedBlock.add(new short[INIT_DROPPED_VERTEX]);
            sharedVertex.add(new Int2IntOpenHashMap(INIT_DROPPED_VERTEX));
            sharedVertex.get(i).defaultReturnValue(-1);
            lastUsedLocation.put(i, 0);
        }

        //System.out.println("Queries = "+ numQueries + " ---------- "+visitedVertices_fast.size());
        //System.out.println("Queries = "+ numQueries + " ---------- "+getvisitedVertices_fastsize());

/*
        cachedDiffs = new ArrayList<>(numQueries);
        for(int i=0;i<numQueries;i++)
            cachedDiffs.add(new DiffCache<>(cacheSize));

 */
    }

    /**
     * returns most significant short of an int value
     *
     * @param x
     * @return
     */
    static short getFirstShort(int x) {
        return (short) (x >> 16);
    }

    /**
     * returns least significant short of an int value
     *
     * @param x
     * @return
     */
    static short getSecondShort(int x) {
        return (short) x;
    }

    /**
     * Make an int value from two shorts
     *
     * @param first
     * @param second
     * @return
     */
    static int makeInt(short first, short second) {
        int x = first;
        x = (first << 16) | (second & 0xFFFF);
        return x;
    }

    /**
     * This function is accurate, not like bloom one
     * However, it only checks for dropped ones.
     *
     * @param queryId
     * @param vertex
     * @param iterationNo
     * @return
     */
    public boolean checkVertexIteration(int queryId, int vertex, short iterationNo, boolean newBatch) {

        if (!sharedVertex.get(queryId).containsKey(vertex)) {
            return false;
        }

        if (newBatch && deletedDiffs.containsKey(vertex)){
            for (Short iter:deletedDiffs.get(vertex))
                if (iter == iterationNo)
                    return false;
        }

        int vertexFirstLocation = sharedVertex.get(queryId).get(vertex);

        do {
            if (sharedBlock.get(queryId)[vertexFirstLocation] == iterationNo) {
                return true;
            } else {
                vertexFirstLocation = makeInt(sharedBlock.get(queryId)[vertexFirstLocation + 1],
                        sharedBlock.get(queryId)[vertexFirstLocation + 2]);
            }
        } while (vertexFirstLocation > 0);
        return false;
    }

    public void clear(int q) {
        sharedBlock.set(q, new short[INIT_DROPPED_VERTEX]);
        sharedVertex.set(q, new Int2IntOpenHashMap(INIT_DROPPED_VERTEX));
        lastUsedLocation.put(q, 0);
    }

    public void clear() {
        sharedBlock.clear();
        sharedVertex.clear();
        lastUsedLocation.clear();
    }

    /**
     * A function that returns a set of all vertices with one or more diffs.
     * This query is very expensive when using bloom filter. It is only needed for reporting or
     * to "findNewMinDistanceVertexInFrontier". The latter is needed when a vertex increase its distance because of a delete or an edge update.
     * For now, I will not support those and return an empty set.
     *
     * @return
     */
    public Set<Integer> getVerticesWithDiff(int q) {
        return sharedVertex.get(q).keySet();
    }

    private void ensureCapacity(int q, int minCapacity) {
        if (minCapacity > sharedBlock.get(q).length) {
            sharedBlock.set(q, ArrayUtils.resizeIfNecessary(sharedBlock.get(q), minCapacity));
        }
    }


    public void addDiffValues(int q, int vertex, short iteration, boolean dropped) {

        if (deletedDiffs.containsKey(vertex)){
            if(deletedDiffs.get(vertex).contains(iteration))
                deletedDiffs.get(vertex).remove(iteration);
        }

        int vertexFirstLocation = sharedVertex.get(q).get(vertex);
        int toBeCorrected = -1;
        int nextLocation = lastUsedLocation.get(q);

        if (vertexFirstLocation > 0) {
            // Vertex exist in the block

            // find what is the appropriate location in the block
            while (vertexFirstLocation > 0) {
                if (sharedBlock.get(q)[vertexFirstLocation] == iteration) {
                    // already added
                    return;
                } else if (sharedBlock.get(q)[vertexFirstLocation] == 0) {
                    // I found an empty spot, so let us reuse it
                    sharedBlock.get(q)[vertexFirstLocation] = iteration;
                    return;
                } else {
                    // this is location I should update its NEXT pointer
                    toBeCorrected = vertexFirstLocation;
                    vertexFirstLocation = makeInt(sharedBlock.get(q)[vertexFirstLocation + 1],
                            sharedBlock.get(q)[vertexFirstLocation + 2]);
                }
            }

            // add a pointer to the next location
            sharedBlock.get(q)[toBeCorrected + 1] = getFirstShort(nextLocation);
            sharedBlock.get(q)[toBeCorrected + 2] = getSecondShort(nextLocation);
        } else {
            // first time to add a diff for this vertex

            // add vertex to the hash table
            sharedVertex.get(q).put(vertex, nextLocation);
        }

        lastUsedLocation.addTo(q, 3);
        ensureCapacity(q, nextLocation + 3);

        // add iteration information
        sharedBlock.get(q)[nextLocation] = iteration;
        //reset the pointer of the next location
        sharedBlock.get(q)[nextLocation + 1] = getFirstShort(-1);
        sharedBlock.get(q)[nextLocation + 2] = getSecondShort(-1);

        if (DistancesWithDropHash.debug(vertex)) {
            System.out.println("first location of vertex " + vertex + " = " + sharedVertex.get(q).get(vertex));
            System.out
                    .println("current saved iteration of vertex " + vertex + " = " + sharedBlock.get(q)[nextLocation]);
            System.out.println("vertex " + vertex + "exist in Hash? " + checkVertexIteration(q, vertex, iteration, true));
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
     * When removing an iteration, keep it somewhere and remove it later to differentiate between new and old status
     *
     * @param q
     * @param vertex
     * @param iter
     */
    public void removeIteration(int q, int vertex, short iter) {


        if (deletedDiffs.containsKey(vertex)){
            var deleted= deletedDiffs.get(vertex);
            deleted.add(iter);
        }
        else {
            var deleted= new HashSet<Short>(5);
            deleted.add(iter);
            deletedDiffs.put(vertex, deleted);
        }
    }
    /**
     * When removing an iteration, instead of reshuffling the big array block...
     * we change the iteration id to be 0.
     *
     * @param q
     * @param vertex
     * @param iter
     */
    public void removeIterationPermenant(int q) {

        for (int vertex:deletedDiffs.keySet()){
            int vertexFirstLocation = sharedVertex.get(q).get(vertex);

            for (Short iter: deletedDiffs.get(vertex)){
                int vertexLocation = vertexFirstLocation;

                while (vertexLocation > 0) {
                    if (sharedBlock.get(q)[vertexLocation] == iter) {
                        sharedBlock.get(q)[vertexLocation] = 0;
                        break;
                    } else {
                        vertexLocation = makeInt(sharedBlock.get(q)[vertexLocation + 1],
                                sharedBlock.get(q)[vertexLocation + 2]);
                    }
                }
            }
        }

        for (int vertex: deletedVertices)
            sharedVertex.get(q).remove(vertex);

        // initialize delataDiffs to be empty again
        // reset deltaDeiffs to keep its capacity small
        deletedDiffs = new HashMap<>(1);
        deletedVertices = new HashSet<>(1);
    }

    /**
     * Return a list of iterations that a vertex might have diffs for
     *
     * @param q      = the query context
     * @param vertex = the vertex in question
     * @return
     */
    public List<Short> getIterationsWithDiff(int q, int vertex) {
        Set<Short> iterSet = new HashSet<>();
        List<Short> iterList = new ArrayList<>();

        int vertexFirstLocation = sharedVertex.get(q).get(vertex);

        while (vertexFirstLocation >= 0) {
            if (sharedBlock.get(q)[vertexFirstLocation] != 0) {
                iterSet.add(sharedBlock.get(q)[vertexFirstLocation]);
            }

            vertexFirstLocation =
                    makeInt(sharedBlock.get(q)[vertexFirstLocation + 1], sharedBlock.get(q)[vertexFirstLocation + 2]);
        }

        if (deletedDiffs.containsKey(vertex))
            for (Short iter:deletedDiffs.get(vertex))
                iterSet.remove(iter);

        for (Short iter:iterSet)
            iterList.add(iter);

        return iterList;

        /*
        *
        *
        * for (short iterIndex=1;iterIndex<=maxIteration.get(q);iterIndex++){
            element = this.createElement(iterIndex,vertex);

            if(sharedDiffs.get(q).contains(element)){
                iterList.add(iterIndex);
            }
        }
        return iterList;
        *
        *
        *
        * */

    }


    /**
     * TODO: This is causing us to use unnecessiry space in the block, but it is okay for now
     *
     * @param queryId
     * @param vertex
     */
    public void removeVertex(int queryId, int vertex) {
        deletedVertices.add(vertex);
    }
}
