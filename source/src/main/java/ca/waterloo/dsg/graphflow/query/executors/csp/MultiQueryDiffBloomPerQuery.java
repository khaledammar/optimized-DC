package ca.waterloo.dsg.graphflow.query.executors.csp;

import com.skjegstad.utils.BloomFilter;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Instead of representing a diff for each query, we want to share the overhead among queries.
 * In this class, the map key is the iteration, but the value is
 * This is a class that represent a data structure to store diffs permanently as we process
 * multiple batches. We do not want to interact with the map directly because we want to encapsulate the details of
 * how the data structure of diffs are implemented inside this class.
 * <p>
 * <p>
 * Instead of using hashtable to represent diffs, we are going to use a bloom filter.
 * This also means that sometime we will incorrectly assume that a vertex has a diff while it does not.
 * <p>
 * ---- Each element in the bloom filter will be a string that represent "query-vertex”
 * ---- We will use one bloom filter for each iteration
 */
public class MultiQueryDiffBloomPerQuery extends MultiQueryDiffBloom {

    public static int expectedElementPerFilter = 50000;
    static int numIteration = 20;
    static int cacheSize = 1000;
    static int numQueries = 0;
    ArrayList<Integer> maxIteration;
    //ArrayList<BloomFilter<String>> sharedDiffs;
    ArrayList<BloomFilter<Long>> sharedDiffs;


/* Removing caching because it adds errors during build
    // this data structure is helpful to keep most important diffs cached so that we do not keep re-computing it
    ArrayList<DiffCache<Pair<Integer,Short>, Double>> cachedDiffs;
*/

    /**
     * Default constructor
     */
    public MultiQueryDiffBloomPerQuery() {

    }

    /**
     * @param q = number of queries
     */
    public MultiQueryDiffBloomPerQuery(int q) {
        numQueries = q;

        maxIteration = new ArrayList<>(numQueries + 1);
        for (int i = 0; i <= numQueries; i++) {
            maxIteration.add(0);
        }

        sharedDiffs = new ArrayList<>(numQueries);
        // for dummy query zero, no need to create a bloom filter
        sharedDiffs.add(null);
        for (int i = 1; i <= numQueries; i++) {
            sharedDiffs.add(new BloomFilter<Long>(0.2, getFilterSize(i), 1));
        }
        System.out.println(getFilterSize(1));

        //visitedVertices = new ArrayList<>(numQueries);
        visitedVertices_fast = new ArrayList<>(numQueries);
        // for dummy query zero, no need to create an object
        visitedVertices_fast.add(null);
        for (int i = 1; i <= numQueries; i++) {
            //visitedVertices.add(new HashSet<>(1));
            visitedVertices_fast.add(new IntOpenHashSet());
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
     * @param queryId
     * @param vertex
     * @param iterationNo
     * @return
     */
    public boolean checkVertexIteration(int queryId, int vertex, int iterationNo) {
        return sharedDiffs.get(queryId).contains(createElement(iterationNo, vertex));
    }

    int getFilterSize(int iteration) {

        return expectedElementPerFilter;
/*
        if(iteration == 0)
            return 10;

        if(iteration<=10)
            return expectedElementPerFilter;

        if(iteration<=20)
            return expectedElementPerFilter/10;

        double iteration_position = Math.ceil(iteration / 10.0);

        return (int) Math.ceil(expectedElementPerFilter /10/ (Math.pow(2,iteration_position)));
*/

    }

    public void clear(int q) {
        sharedDiffs.get(q).clear();
        visitedVertices_fast.get(q).clear();
        maxIteration.set(q, 0);
    }


    public Set<Integer> getVerticesWithDiff(int q) {
        return visitedVertices_fast.get(q);
    }

    public void addDiffValues(int q, int vertex, short iteration, boolean dropped) {

        if (iteration > maxIteration.get(q)) {
            maxIteration.set(q, (int) iteration);
            //    System.out.println("max iteration is "+maxIteration.get(q));
        }

        sharedDiffs.get(q).add(createElement(iteration, vertex));
        visitedVertices_fast.get(q).add(vertex);

        if (DistancesWithDropBloom.debug(vertex)) {
            System.out.println("vertex " + vertex + "exist in Bloom? " +
                    sharedDiffs.get(q).contains(createElement(iteration, vertex)));
            System.out.println(
                    "vertex " + vertex + "exist in visitedVertices? " + visitedVertices_fast.get(q).contains(vertex));
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
     *
     * @param q      = the query context
     * @param vertex = the vertex in question
     * @return
     */
    public List<Short> getIterationsWithDiff(int q, int vertex) {
        List<Short> iterList = new ArrayList<>();

        Long element = 0L;

        for (short iterIndex = 1; iterIndex <= maxIteration.get(q); iterIndex++) {
            element = this.createElement(iterIndex, vertex);

            if (sharedDiffs.get(q).contains(element)) {
                iterList.add(iterIndex);
            }
        }
        return iterList;
    }

    public int getMaxIteration(int queryId) {
        return maxIteration.get(queryId);
    }


    public void removeVertex(int queryId, int vertex) {
        visitedVertices_fast.get(queryId).remove(vertex);
    }
}
