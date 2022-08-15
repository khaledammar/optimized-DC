package ca.waterloo.dsg.graphflow.query.executors.csp;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;

import java.util.*;

/**
 * This is a class that represent a data structure to store diffs permanently as we process
 * multiple batches. We do not want to interact with the map directly because we want to encapsulate the details of
 * how the data structure of diffs are implemented inside this class.
 */
public class MultiQueryDiffHashPerQuery extends MultiQueryDiffHash {

    static int numQueries = 0;
    ArrayList<Map<Integer, ShortOpenHashSet>> sharedDiffs;
    static Map<Integer,Set<Short>> deletedDiffs;
    static Set<Integer> deletedVertices = new HashSet<>(1);

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
    public MultiQueryDiffHashPerQuery() {

    }

    /**
     * @param q = number of queries
     */
    public MultiQueryDiffHashPerQuery(int q) {
        numQueries = q;

        sharedDiffs = new ArrayList<>(numQueries);
        for (int i = 0; i < numQueries; i++) {
            sharedDiffs.add(new HashMap<Integer, ShortOpenHashSet>(1));
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
     * This function is accurate, not like bloom one
     * However, it only checks for dropped ones.
     *
     * @param queryId
     * @param vertex
     * @param iterationNo
     * @return
     */
    public boolean checkVertexIteration(int queryId, int vertex, short iterationNo, boolean newBatch) {

        if (newBatch && deletedDiffs.containsKey(vertex)){
            for (Short iter:deletedDiffs.get(vertex))
                if (iter == iterationNo)
                    return false;
        }

        if (sharedDiffs.get(queryId).containsKey(vertex)) {
            return sharedDiffs.get(queryId).get(vertex).contains(iterationNo);
        } else {
            return false;
        }
    }

    public void clear(int q) {
        sharedDiffs.get(q).clear();
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
        return sharedDiffs.get(q).keySet();
    }

    public void addDiffValues(int q, int vertex, short iteration, boolean dropped) {

        if (deletedDiffs.containsKey(vertex)){
            if(deletedDiffs.get(vertex).contains(iteration))
                deletedDiffs.get(vertex).remove(iteration);
        }

        if (sharedDiffs.get(q).containsKey(vertex)) {

            sharedDiffs.get(q).get(vertex).add(iteration);
/*
            ShortOpenHashSet currentSet = sharedDiffs.get(q).get(vertex);
            if (!currentSet.contains(iteration)) {
                currentSet.add(iteration);
                sharedDiffs.get(q).put(vertex, currentSet);
            }

 */
        } else {
            // initiate set for given vertex
            ShortOpenHashSet currentSet = new ShortOpenHashSet(1);
            currentSet.add(iteration);
            sharedDiffs.get(q).put(vertex, currentSet);
        }

        if (DistancesWithDropBloom.debug(vertex)) {
            System.out.println("vertex " + vertex + "exist in Hash? " + checkVertexIteration(q, vertex, iteration, true));
            System.out.println(
                    "vertex " + vertex + "exist in visitedVertices? " + sharedDiffs.get(q).containsKey(vertex));
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
            Set deleted= deletedDiffs.get(vertex);
            deleted.add(iter);
        }
        else {
            Set deleted= new HashSet(5);
            deleted.add(iter);
            deletedDiffs.put(vertex, deleted);
        }
    }

    public void removeIterationPermenant(int q) {

        for (Integer vertex:deletedDiffs.keySet()){
            for (Short iter: deletedDiffs.get(vertex)){
                sharedDiffs.get(q).get(vertex).remove(iter);
            }
        }


        for (Integer vertex: deletedVertices)
            if (sharedDiffs.get(q).containsKey(vertex)) {
                sharedDiffs.get(q).remove(vertex);
            }



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
        List<Short> iterList = new ArrayList<>();
        Set<Short> iterSet = new HashSet<>();


        if (DistancesWithDropBloom.debug(vertex)) {
            System.out.println(vertex + " shared diffs = " + Arrays.toString(sharedDiffs.get(q).entrySet().toArray()));
        }

        if (sharedDiffs.get(q).containsKey(vertex)) {
            if (DistancesWithDropBloom.debug(vertex)) {
                System.out
                        .println(vertex + " iterations = " + Arrays.toString(sharedDiffs.get(q).get(vertex).toArray()));
            }
            iterSet.addAll(sharedDiffs.get(q).get(vertex));

            if (DistancesWithDropBloom.debug(vertex)) {
                System.out.println(vertex + " reported = " + Arrays.toString(iterSet.toArray()));
            }
        }

        for (Short iter:deletedDiffs.get(vertex))
            iterSet.remove(iter);

        for (Short iter:iterSet)
            iterList.add(iter);

        return iterList;
    }

    public void removeVertex(int queryId, int vertex) {
        deletedVertices.add(vertex);
    }
}
