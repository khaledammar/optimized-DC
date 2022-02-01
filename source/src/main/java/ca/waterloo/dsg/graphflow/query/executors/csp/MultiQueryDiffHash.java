package ca.waterloo.dsg.graphflow.query.executors.csp;

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
public abstract class MultiQueryDiffHash {

    //ArrayList<Map<Integer,IntOpenHashSet>> sharedDiffs;

/* Removing caching because it adds errors during build
    // this data structure is helpful to keep most important diffs cached so that we do not keep re-computing it
    ArrayList<DiffCache<Pair<Integer,Short>, Double>> cachedDiffs;
*/

    //public abstract ArrayList<Map<Integer,ShortOpenHashSet>> getSharedDiffs();
    public abstract void removeIteration(int q, int vertex, short iter);
    public abstract void removeIterationPermenant(int q);

    public abstract boolean checkVertexIteration(int queryId, int vertex, short iterationNo, boolean newBatch);

    public abstract void addDiffValues(int q, int vertex, short iteration, boolean dropped);

    /**
     * Return a list of iterations that a vertex might have diffs for
     *
     * @param q      = the query context
     * @param vertex = the vertex in question
     * @return
     */
    public abstract List<Short> getIterationsWithDiff(int q, int vertex);

    public abstract void clear(int q);
    public abstract void clear();

    /**
     * A function that returns a set of all vertices with one or more diffs.
     * This query is very expensive when using bloom filter. It is only needed for reporting or
     * to "findNewMinDistanceVertexInFrontier". The latter is needed when a vertex increase its distance because of a delete or an edge update.
     * For now, I will not support those and return an empty set.
     *
     * @return
     */
    public abstract Set<Integer> getVerticesWithDiff(int queryId);

    public abstract void removeVertex(int queryId, int vertex);
}
