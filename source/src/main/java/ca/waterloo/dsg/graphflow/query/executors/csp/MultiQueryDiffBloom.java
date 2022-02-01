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
public abstract class MultiQueryDiffBloom {

    static int maxIteration = 0;
    static int numIteration = 20;
    static int expectedElementPerFilter = 100000;
    static int cacheSize = 1000;
    static int numQueries = 0;
    ArrayList<BloomFilter<String>> sharedDiffs;
    //ArrayList<Set<Integer>> visitedVertices;
    ArrayList<IntOpenHashSet> visitedVertices_fast;

/* Removing caching because it adds errors during build
    // this data structure is helpful to keep most important diffs cached so that we do not keep re-computing it
    ArrayList<DiffCache<Pair<Integer,Short>, Double>> cachedDiffs;
*/

    /**
     * This is reasonable at the moment
     *
     * @param first
     * @param second
     * @return
     */
    static long makeLong(int first, int second) {
        long x = first;
        x = (x << 8) | first;
        x = (x << 8) | first;
        x = (x << 8) | first;
        x = (x << 32) | second;
        return x;
    }

    public int getNumberVertices(int q) {
        return visitedVertices_fast.get(q).size();
    }

    public void removeIteration(int query, int vertex, short iteration) {
        // we cannot remove iteration from a bloom filter
        return;
    }

    public abstract boolean checkVertexIteration(int queryId, int vertex, int iterationNo);

    public abstract void addDiffValues(int q, int vertex, short iteration, boolean dropped);

    abstract int getFilterSize(int iteration);

    /**
     * Return a list of iterations that a vertex might have diffs for
     *
     * @param q      = the query context
     * @param vertex = the vertex in question
     * @return
     */
    public abstract List<Short> getIterationsWithDiff(int q, int vertex);

/*    public String createElement(int id, int vertex){
        return id+"-"+vertex;
    }
*/

    public abstract int getMaxIteration(int queryId);

    public Long createElement(int id, int vertex) {

        return makeLong(id, vertex);
    }

    public void clear(int q) {
        sharedDiffs.get(q).clear();
    }

    public void clear() {
        if (sharedDiffs != null) {
            sharedDiffs.clear();
        }
    }

    /**
     * A function that returns a set of all vertices with one or more diffs.
     * This query is very expensive when using bloom filter. It is only needed for reporting or
     * to "findNewMinDistanceVertexInFrontier". The latter is needed when a vertex increase its distance because of a delete or an edge update.
     * For now, I will not support those and return an empty set.
     *
     * @return
     */
    public Set<Integer> getVerticesWithDiff(int queryId) {
        return visitedVertices_fast.get(queryId);
    }

    public void removeVertex(int queryId, int vertex) {
        visitedVertices_fast.get(queryId).remove(vertex);
    }
}
