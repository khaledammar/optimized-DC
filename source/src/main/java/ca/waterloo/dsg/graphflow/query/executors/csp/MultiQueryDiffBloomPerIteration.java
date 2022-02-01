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
public class MultiQueryDiffBloomPerIteration extends MultiQueryDiffBloom {

    static int maxIteration = 0;
    static int numIteration = 20;
    static int expectedElementPerFilter = 100000;
    static int cacheSize = 1000;
    static int numQueries = 0;
    ArrayList<BloomFilter<Long>> sharedDiffs;

/* Removing caching because it adds errors during build
    // this data structure is helpful to keep most important diffs cached so that we do not keep re-computing it
    ArrayList<DiffCache<Pair<Integer,Short>, Double>> cachedDiffs;
*/

    /**
     * Default constructor
     */
    public MultiQueryDiffBloomPerIteration() {

    }


    /**
     * @param q = number of queries
     */
    public MultiQueryDiffBloomPerIteration(int q) {
        numQueries = q;
        sharedDiffs = new ArrayList<>(numIteration + 1);
        for (int i = 0; i <= numIteration; i++) {
            sharedDiffs.add(new BloomFilter<Long>(0.2, numQueries * getFilterSize(i), 1));
        }

        //visitedVertices = new ArrayList<>(numQueries);
        visitedVertices_fast = new ArrayList<>(numQueries);
        for (int i = 0; i < numQueries; i++) {
            //visitedVertices.add(new HashSet<>(1));
            visitedVertices_fast.add(new IntOpenHashSet());
        }
/*
        cachedDiffs = new ArrayList<>(numQueries);
        for(int i=0;i<numQueries;i++)
            cachedDiffs.add(new DiffCache<>(cacheSize));

 */
    }

    public boolean checkVertexIteration(int queryId, int vertex, int iterationNo) {
        if (iterationNo > numIteration) {
            return false;
        } else {
            return sharedDiffs.get(iterationNo).contains(createElement(queryId, vertex));
        }
    }

    int getFilterSize(int iteration) {

        //return expectedElementPerFilter;

        if (iteration == 0) {
            return 10;
        }

        if (iteration <= 10) {
            return expectedElementPerFilter;
        }

        if (iteration <= 20) {
            return expectedElementPerFilter / 10;
        }

        double iteration_position = Math.ceil(iteration / 10.0);

        return (int) Math.ceil(expectedElementPerFilter / 10 / (Math.pow(2, iteration_position)));
    }

    public void clear(int q) {
        sharedDiffs.get(q).clear();
    }

    //public int getvisitedVertices_fastsize(){
    //    return visitedVertices_fast.size();
    //}

    /**
     * A function that returns a set of all vertices with one or more diffs.
     * This query is very expensive when using bloom filter. It is only needed for reporting or
     * to "findNewMinDistanceVertexInFrontier". The latter is needed when a vertex increase its distance because of a delete or an edge update.
     * For now, I will not support those and return an empty set.
     *
     * @return
     */
    public Set<Integer> getVerticesWithDiff(int q) {
        return visitedVertices_fast.get(q);
    }

    public void addDiffValues(int q, int vertex, short iteration, boolean dropped) {
        if (iteration > maxIteration) {
            maxIteration = iteration;
            System.out.println("max iteration is " + maxIteration);
        }
        if (iteration > numIteration) {
            for (int i = numIteration + 1; i <= iteration; i++) {
                sharedDiffs.add(new BloomFilter<Long>(0.2, numQueries * getFilterSize(i), 1));
            }

            numIteration = iteration;
        }
        sharedDiffs.get(iteration).add(createElement(q, vertex));
        if (dropped) {
            visitedVertices_fast.get(q).add(vertex);
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
        Long element = this.createElement(q, vertex);
        Short iterIndex = 0;
        for (BloomFilter<Long> blm : sharedDiffs) {
            if (blm.contains(element)) {
                iterList.add(iterIndex);
            }
            iterIndex++;
        }
        return iterList;
    }


    public int getMaxIteration(int queryId) {
        return maxIteration;
    }
}
