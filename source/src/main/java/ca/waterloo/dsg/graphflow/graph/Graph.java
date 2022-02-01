package ca.waterloo.dsg.graphflow.graph;

import ca.waterloo.dsg.graphflow.graph.serde.GraphParallelSerDeUtils;
import ca.waterloo.dsg.graphflow.graph.serde.GraphflowSerializable;
import ca.waterloo.dsg.graphflow.graph.serde.MainFileSerDeHelper;
import ca.waterloo.dsg.graphflow.graph.serde.ParallelArraySerDeUtils;
import ca.waterloo.dsg.graphflow.query.executors.csp.DistancesDC;
import ca.waterloo.dsg.graphflow.util.*;
import org.antlr.v4.runtime.misc.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.Map.Entry;


/**
 * Encapsulates the Graph representation and provides utility methods.
 */
public class Graph implements GraphflowSerializable {

    // Used to represent different versions of the graph.
    public enum GraphVersion {
        // Graph formed after making all additions and deletions permanent.
        PERMANENT,
        // Graph of only the temporary additions.
        DIFF_PLUS,
        // Graph of only the temporary deletions.
        DIFF_MINUS,
        // Graph formed after merging the temporary additions and deletions with the permanent
        // graph to reflect the new state of the graph that will be formed after making the
        // changes permanent.
        MERGED
    }

    /**
     * This is used for both identifying the edge direction in adjacency lists in the graph
     * representation and the direction when evaluating MATCH queries using Generic Join and the
     * direction of traversals in SHORTEST PATH queries.
     */
    public enum Direction {
        FORWARD(true),
        BACKWARD(false);

        private final boolean isForward;

        Direction(boolean isForward) {
            this.isForward = isForward;
        }

        public boolean getBooleanValue() {
            return isForward;
        }
    }

    private static final Logger logger = LogManager.getLogger(Graph.class);
    private static final int DEFAULT_GRAPH_SIZE = 2;
    public static Graph INSTANCE = new Graph();
    // TODO(semih): This is a quick hack to support weighted graphs without going to the EdgeStore, which
    // is very slow. Callers can set this to true and the sorted adjacency lists will support weights.
    public static boolean IS_WEIGHTED = false;
    public static String FILE_SUFFIX = "";
    // get a random edge for Differential BFS
    Random randomGenerator = new Random();
    // Stores the highest vertex ID of the permanent graph.
    private int highestPermanentVertexId = -1;
    // Adjacency lists for the permanent graph, containing both the neighbour vertex IDs and edge
    // type IDs to those neighbours.
    private SortedAdjacencyList[] forwardAdjLists = new SortedAdjacencyList[DEFAULT_GRAPH_SIZE];
    private SortedAdjacencyList[] backwardAdjLists = new SortedAdjacencyList[DEFAULT_GRAPH_SIZE];
    // Stores the highest vertex ID present among all vertices in the permanent graph and the
    // temporary vertices to be added. This is used when permanently applying the temporary changes
    // to the graph to decide if the adjacency list arrays need resizing to accommodate higher
    // vertex IDs being added.
    private int highestMergedVertexId = -1;
    private ShortArrayList vertexTypes = new ShortArrayList();
    // Edges for additions and deletions.
    private List<int[]> diffPlusEdges = new ArrayList<>();
    private List<int[]> diffMinusEdges = new ArrayList<>();
    // Each edge at index i of {@link diffPlusEdges} or {@link diffMinusEdges} has a type at
    // index i of {@link diffPlusEdgeTypes} or {@link diffMinusEdgeTypes}.
    private ShortArrayList diffPlusEdgeTypes = new ShortArrayList();
    private ShortArrayList diffMinusEdgeTypes = new ShortArrayList();
    // Updated adjacency lists for the vertices affected by additions and deletions.
    private Map<Integer, SortedAdjacencyList> mergedForwardAdjLists = new HashMap<>();
    private Map<Integer, SortedAdjacencyList> mergedBackwardAdjLists = new HashMap<>();
    public Map<Integer, Map<Integer, DistancesDC.Diff2>> edgeDiffs = new HashMap<>();

    private Graph() {
        initializeSortedAdjacencyLists(0 /* starting index */, DEFAULT_GRAPH_SIZE);
    }

    /**
     * Resets the {@link Graph} state by creating a new {@code INSTANCE}.
     */
    static void reset() {
        INSTANCE = new Graph();
    }

    /**
     * Returns the singleton instance {@link #INSTANCE} of {@link Graph}.
     */
    public static Graph getInstance() {
        return INSTANCE;
    }

    /**
     * Used during unit testing to check the equality of objects. This is used instead of
     * overriding the standard {@code equals()} and {@code hashCode()} methods.
     *
     * @param a One of the objects.
     * @param b The other object.
     * @return {@code true} if {@code a}'s values are the same as {@code b}'s.
     */
    @UsedOnlyByTests
    public static boolean isSamePermanentGraphAs(Graph a, Graph b) {
        if (a == b) {
            return true;
        }
        if (null == a || null == b) {
            return false;
        }
        if (a.highestPermanentVertexId != b.highestPermanentVertexId ||
                a.highestMergedVertexId != b.highestMergedVertexId ||
                !ShortArrayList.isSameAs(a.vertexTypes, b.vertexTypes)) {
            return false;
        }
        for (int i = 0; i < a.highestPermanentVertexId; i++) {
            if (!SortedAdjacencyList.isSameAs(a.forwardAdjLists[i], b.forwardAdjLists[i])) {
                return false;
            }
            if (!SortedAdjacencyList.isSameAs(a.backwardAdjLists[i], b.backwardAdjLists[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return The highest permanent vertex ID .
     */
    public int getVertexCount() {
        return highestPermanentVertexId + 1;
    }

    /***
     * @return The highest merged vertex ID in the graph.
     */
    public int getHighestVertexId() {
        return highestMergedVertexId;
    }

    /**
     * Adds a vertex to the graph.
     * Warning: Currently, as part of this call, we will overwrite the current vertex type and
     * properties of {@code vertexId} with {@code vertexType} and {@code vertexProperties},
     * respectively.
     * If the properties passed are {@code null}, no changes to the vertex properties occur.
     * If a vertex u has type T, callers should always call this method with type T for u to keep
     * u's type.
     *
     * @param vertexId         The vertex ID.
     * @param vertexType       The type of {@code vertexId}.
     * @param vertexProperties The properties of {@code vertexId}.
     */
    public void addVertex(int vertexId, short vertexType, Map<Short, Pair<DataType, String>> vertexProperties) {
        vertexTypes.set(vertexId, vertexType);
        highestPermanentVertexId = Integer.max(highestPermanentVertexId, vertexId);
        highestMergedVertexId = Integer.max(highestMergedVertexId, vertexId);
        ensureCapacity(highestMergedVertexId + 1);
    }

    public void addEdgeTemporarily(int fromVertex, int toVertex, short fromVertexType, short toVertexType,
                                   short edgeType) {
        addEdgeTemporarily(fromVertex, toVertex, fromVertexType, toVertexType, null /* no weight */, edgeType);
    }

    public int[] getRandomEdge() {
        int randSource = randomGenerator.nextInt(forwardAdjLists.length);
        while (forwardAdjLists[randSource].getSize() == 0) {
            randSource = randomGenerator.nextInt(forwardAdjLists.length);
        }
        int randDest = randomGenerator.nextInt(forwardAdjLists[randSource].getSize());
        return new int[]{randSource, forwardAdjLists[randSource].neighbourIds[randDest]};
    }

    public double getEdgeWeight(int source, int dest) {
        if (source > forwardAdjLists.length) {
            return -1.0;
        }
        SortedAdjacencyList list = forwardAdjLists[source];
        for (int i = 0; i < list.getSize(); i++) {
            if (list.neighbourIds[i] == dest) {
                return list.weights[i];
            }
        }
        return -1.0;
    }

    public void setSeed(int seed) {
        randomGenerator.setSeed(seed);
    }

    /**
     * Adds an edge temporarily to the graph. A call to {@link #finalizeChanges()} is required to
     * make the changes permanent.
     * Note: If an edge to {@code toVertex} with the given {@code edgeType} already exists, this
     * method returns without doing anything.
     * Warning: Currently, as part of this call, we will overwrite the current vertex types and
     * properties of {@code fromVertex} and {@code toVertex} with {@code fromVertexType}, {@code
     * toVertexType}, {@code toVertexProperties}, and {@code fromVertexProperties}, respectively.
     * If the properties passed are {@code null}, no changes to the vertex properties occur.
     * Otherwise, the vertex properties are overwritten.
     * Warning: This method makes the types and properties permanent.
     * If a vertex u has type T, callers should always call this method with type T for u to keep
     * u's type.
     *
     * @param fromVertex     The source vertex ID for the edge.
     * @param toVertex       The destination vertex ID for the edge.
     * @param fromVertexType The type of {@code fromVertex}.
     * @param toVertexType   The type of {@code toVertex}.
     * @param edgeType       The type of the edge being added.
     */
    public void addEdgeTemporarily(int fromVertex, int toVertex, short fromVertexType, short toVertexType,
                                   Double weight, short edgeType) {
        // Warning: This is a hack to allow updates in the graph for testing differential BFS.
        //        if ((fromVertex <= highestPermanentVertexId) && (null != forwardAdjLists[fromVertex]) &&
        //            (forwardAdjLists[fromVertex].contains(toVertex, edgeType))) {
        //            return; // Edge is already present. Skip.
        //        }
        vertexTypes.set(fromVertex, fromVertexType);
        vertexTypes.set(toVertex, toVertexType);
        addOrDeleteEdgeTemporarily(true /* addition */, fromVertex, toVertex, edgeType, weight);
        highestMergedVertexId = Integer.max(highestMergedVertexId, Integer.max(fromVertex, toVertex));
    }

    public void updateEdgeTemporarily(int fromVertex, int toVertex, short fromVertexType, short toVertexType,
                                      Double weight, short edgeType) {
        //        if ((fromVertex <= highestPermanentVertexId) && (null != forwardAdjLists[fromVertex]) &&
        //            (forwardAdjLists[fromVertex].contains(toVertex, edgeType))) {
        //            return; // Edge is already present. Skip.
        //        }
        vertexTypes.set(fromVertex, fromVertexType);
        vertexTypes.set(toVertex, toVertexType);
        addOrDeleteEdgeTemporarily(true /* addition */, fromVertex, toVertex, edgeType, weight);
        highestMergedVertexId = Integer.max(highestMergedVertexId, Integer.max(fromVertex, toVertex));
    }

    public void addSimpleEdge(int fromVertex, int toVertex) {
        // Saves the edge to the diffEdges list and the types to the diffEdgeTypes list.
        addSimpleVertexIfNecessary(fromVertex);
        addSimpleVertexIfNecessary(toVertex);
        SortedAdjacencyList fromVertexFwAdjList = forwardAdjLists[fromVertex];
        fromVertexFwAdjList
                .add(toVertex, TypeAndPropertyKeyStore.ANY, -1 /* no edgeID */, null, false /* do not sort */);
        SortedAdjacencyList toVertexBwAdjList = backwardAdjLists[fromVertex];
        toVertexBwAdjList
                .add(fromVertex, TypeAndPropertyKeyStore.ANY, -1 /* no edgeID */, null, false /* do not sort */);
    }

    public void sortAllAdjacencyLists() {
        for (int i = 0; i < highestMergedVertexId; ++i) {
            forwardAdjLists[i].sort();
            backwardAdjLists[i].sort();
        }
    }

    private void addSimpleVertexIfNecessary(int vertexId) {
        if (vertexId < highestMergedVertexId) {
            return;
        }
        vertexTypes.set(vertexId, TypeAndPropertyKeyStore.ANY);
        highestPermanentVertexId = Integer.max(highestPermanentVertexId, vertexId);
        highestMergedVertexId = Integer.max(highestMergedVertexId, vertexId);
        ensureCapacity(highestMergedVertexId + 1);
    }

    /**
     * Deletes an edge temporarily from the graph. A call to {@link #finalizeChanges()} is required
     * to make the changes permanent.
     *
     * @param fromVertex The starting vertex ID for the edge.
     * @param toVertex   The ending vertex ID for the edge.
     * @param edgeType   The type of the edge being deleted.
     * @throws NoSuchElementException Exception thrown when the specified edge does not exist.
     */
    public void deleteEdgeTemporarily(int fromVertex, int toVertex, short edgeType) {
        // Check whether the edge exists in either the MERGED or the PERMANENT graph.
        if (!((fromVertex <= highestMergedVertexId) &&
                ((toVertex <= highestMergedVertexId) && mergedForwardAdjLists.containsKey(fromVertex) &&
                        (-1 != mergedForwardAdjLists.get(fromVertex).search(toVertex, edgeType)))) &&
                !((fromVertex <= highestPermanentVertexId) && (toVertex <= highestPermanentVertexId) &&
                        (null != forwardAdjLists[fromVertex]) &&
                        (forwardAdjLists[fromVertex].contains(toVertex, edgeType)))) {
            // The edge does not exist.

            // TODO: Khaled, here we ignore the problem of deleting non-existing edge. This is useful when we handle real data
            System.err.println("Warning: The edge " + fromVertex + "->" + toVertex + " does not exist.");

            //throw new NoSuchElementException("Warning: The edge " + fromVertex + "->" + toVertex + " does not exist.");

        } else {
            addOrDeleteEdgeTemporarily(false /* deletion */, fromVertex, toVertex, edgeType, null /* no weights */);
        }
    }

    /**
     * Adds or deletes an edge temporarily from the graph.
     *
     * @param isAddition {@code true} for addition, {@code false} for deletion.
     * @param fromVertex The starting vertex ID for the edge.
     * @param toVertex   The ending vertex ID for the edge.
     * @param edgeType   The type of the edge being added or deleted.
     */
    private void addOrDeleteEdgeTemporarily(boolean isAddition, int fromVertex, int toVertex, short edgeType,
                                            Double weight) {
        List<int[]> diffEdges = isAddition ? diffPlusEdges : diffMinusEdges;
        ShortArrayList diffEdgeTypes = isAddition ? diffPlusEdgeTypes : diffMinusEdgeTypes;
        // Saves the edge to the diffEdges list and the types to the diffEdgeTypes list.
        diffEdges.add(new int[]{fromVertex, toVertex});
        diffEdgeTypes.add(edgeType);
        // Create the updated forward adjacency list for the vertex.
        updateMergedAdjLists(isAddition, fromVertex, toVertex, edgeType, -1, weight, mergedForwardAdjLists,
                forwardAdjLists);
        // Create the updated backward adjacency list for the vertex.
        updateMergedAdjLists(isAddition, toVertex, fromVertex, edgeType, -1, weight, mergedBackwardAdjLists,
                backwardAdjLists);
    }

    /**
     * Return a vertex degree
     */

    public int getVertexFWDDegree(int vertex) {
        return forwardAdjLists[vertex].getSize();
    }

    public int getVertexBWDDegree(int vertex) {
        return backwardAdjLists[vertex].getSize();
    }

    public int getVertexDegree(int vertex, Direction d) {
        int vertexDegree = (d == Graph.Direction.FORWARD) ? Graph.getInstance().getVertexFWDDegree(vertex) :
                Graph.getInstance().getVertexBWDDegree(vertex);

        return vertexDegree;
    }

    /**
     * Temporarily performs an addition or deletion of the edge {@code fromVertex}->{@code toVertex}
     * by updating the list {@code mergedAdjLists}, using {@code permanentAdjLists} if required.
     *
     * @param isAddition        {@code true} for addition, {@code false} for deletion.
     * @param fromVertex        The starting vertex ID for the edge.
     * @param toVertex          The ending vertex ID for the edge.
     * @param edgeType          The type of the edge being added or deleted.
     * @param edgeId            the ID generated by the edgeStore for the edge.
     * @param mergedAdjLists    The merged adjacency lists to modify.
     * @param permanentAdjLists The permanent adjacency list, used if {@code fromVertex} does not
     *                          already exist in {@code mergedAdjLists}.
     */
    private void updateMergedAdjLists(boolean isAddition, int fromVertex, int toVertex, short edgeType, long edgeId,
                                      Double weight, Map<Integer, SortedAdjacencyList> mergedAdjLists,
                                      SortedAdjacencyList[] permanentAdjLists) {
        if (mergedAdjLists.containsKey(fromVertex)) {
            if (isAddition) {
                mergedAdjLists.get(fromVertex).add(toVertex, edgeType, edgeId, weight, true /* sort */);
            } else {
                mergedAdjLists.get(fromVertex).removeNeighbour(toVertex, edgeType);
            }
        } else {
            SortedAdjacencyList updatedList = new SortedAdjacencyList(IS_WEIGHTED);
            if (fromVertex <= highestPermanentVertexId && null != permanentAdjLists[fromVertex]) {
                // Copy the adjacency list from the permanent graph to the merged graph.
                updatedList.addAll(permanentAdjLists[fromVertex]);
            }
            if (isAddition) {
                updatedList.add(toVertex, edgeType, edgeId, weight, true /* sort */);
            } else {
                updatedList.removeNeighbour(toVertex, edgeType);
            }
            mergedAdjLists.put(fromVertex, updatedList);
        }
    }

    /**
     * Permanently applies the temporary additions and deletions that have been applied using the
     * {@link #addEdgeTemporarily} and {@link #deleteEdgeTemporarily} methods since the previous
     * call to this method.
     */
    public void finalizeChanges() {

        //System.out.println("** GRAPH.finalizeChanges **");

        // Increase the size of the adjacency lists if newly added edges have a higher
        // vertex ID as captured by {@code highestMergedVertexId}.
        // TODO: handle very large vertex ids.
        ensureCapacity(highestMergedVertexId + 1);
        highestPermanentVertexId = highestMergedVertexId;
        // Replace the adjacency lists of permanent vertices with the merged ones.
        for (int vertex : mergedForwardAdjLists.keySet()) {
            forwardAdjLists[vertex] = mergedForwardAdjLists.get(vertex);
        }
        for (int vertex : mergedBackwardAdjLists.keySet()) {
            backwardAdjLists[vertex] = mergedBackwardAdjLists.get(vertex);
        }
        // delete edgeIds from the edge store.
        //        for (int i = 0; i < diffMinusEdgeIds.getSize(); ++i) {
        //            EdgeStore.getInstance().deleteEdge(diffMinusEdgeIds.get(i));
        //        }
        // Reset the diff and merged graph states.
        diffPlusEdges.clear();
        diffMinusEdges.clear();
        diffPlusEdgeTypes.clear();
        diffMinusEdgeTypes.clear();
        mergedForwardAdjLists.clear();
        mergedBackwardAdjLists.clear();
    }

    /**
     * Returns an iterator over the edges of the graph for the given {@code graphVersion} and
     * {@code direction}. The order of the returned edges is first in increasing order of source
     * vertex IDs and then, for the edges with the same source vertex ID, in increasing order of
     * destination IDs.
     *
     * @param graphVersion         The {@code GraphVersion} for which list of edges is required.
     * @param direction            The {@code Direction} of the edges.
     * @param fromVertexTypeFilter The type of the from vertex of the edge returned by the
     *                             iterator.
     * @param toVertexTypeFilter   The type of the to vertex of the edge returned by the iterator.
     * @param edgeTypeFilter       The type of the edges returned by the iterator.
     * @return An iterator to the list of edges for the given {@code graphVersion} and {@code
     * direction}.
     * @throws UnsupportedOperationException Exception thrown when {@code graphVersion} is {@link
     *                                       GraphVersion#DIFF_MINUS} or {@link GraphVersion#DIFF_PLUS} and direction is {@link
     *                                       Direction#BACKWARD}.
     */
    public Iterator<int[]> getEdgesIterator(GraphVersion graphVersion, Direction direction, short fromVertexTypeFilter,
                                            short toVertexTypeFilter, short edgeTypeFilter) {
        if ((GraphVersion.DIFF_PLUS == graphVersion || GraphVersion.DIFF_MINUS == graphVersion) &&
                Direction.BACKWARD == direction) {
            throw new UnsupportedOperationException("Getting edges for the DIFF_PLUS " +
                    "or DIFF_MINUS graph in the BACKWARD direction is not supported.");
        }
        if (GraphVersion.DIFF_PLUS == graphVersion) {
            return new DiffEdgesIterator(diffPlusEdges, diffPlusEdgeTypes, vertexTypes, fromVertexTypeFilter,
                    toVertexTypeFilter, edgeTypeFilter);
        } else if (GraphVersion.DIFF_MINUS == graphVersion) {
            return new DiffEdgesIterator(diffMinusEdges, diffMinusEdgeTypes, vertexTypes, fromVertexTypeFilter,
                    toVertexTypeFilter, edgeTypeFilter);
        } else {
            SortedAdjacencyList[] permanentAdjacencyLists;
            Map<Integer, SortedAdjacencyList> mergedAdjLists;
            if (Direction.FORWARD == direction) {
                permanentAdjacencyLists = forwardAdjLists;
                mergedAdjLists = mergedForwardAdjLists;
            } else {
                permanentAdjacencyLists = backwardAdjLists;
                mergedAdjLists = mergedBackwardAdjLists;
            }
            int lastVertexId = (GraphVersion.MERGED == graphVersion) ? highestMergedVertexId : highestPermanentVertexId;

            if (0 > lastVertexId) {
                // Handle the case when the graph is empty.
                logger.warn("A getEdgesIterator(" + graphVersion + "," + direction +
                        ") call received when the graph was empty.");
                return Collections.<int[]>emptyList().iterator();
            }
            return new PermanentAndMergedEdgesIterator(graphVersion, permanentAdjacencyLists, mergedAdjLists,
                    vertexTypes, fromVertexTypeFilter, toVertexTypeFilter, edgeTypeFilter, lastVertexId);
        }
    }

    /**
     * Checks if an edge is present between {@code fromVertexId} and {@code toVertexId} in the
     * given {@code graphVersion} of the graph, for the given {@code direction}, with a given
     * edge type and a set of properties.
     *
     * @param fromVertexId The from vertex ID.
     * @param toVertexId   The to vertex ID.
     * @param direction    The {@link Direction} of the edge.
     * @param graphVersion The {@link GraphVersion} where the edge's presence needs to be checked.
     * @param typeFilter   The type of the edge being searched for.
     * @return {@code true} if the edge is present, {@code false} otherwise.
     */
    public boolean isEdgePresent(int fromVertexId, int toVertexId, Direction direction, GraphVersion graphVersion,
                                 short typeFilter) {
        if (GraphVersion.DIFF_MINUS == graphVersion || GraphVersion.DIFF_PLUS == graphVersion) {
            throw new UnsupportedOperationException(
                    "Checking presence of an edge in the DIFF_PLUS " + "or DIFF_MINUS graph is not supported.");
        }
        if (fromVertexId < 0 || fromVertexId > highestMergedVertexId || toVertexId < 0 ||
                toVertexId > highestMergedVertexId) {
            return false;
        }
        if (GraphVersion.PERMANENT == graphVersion &&
                (fromVertexId > highestPermanentVertexId || toVertexId > highestPermanentVertexId)) {
            return false;
        }
        SortedAdjacencyList[] permanentAdjacencyLists;
        Map<Integer, SortedAdjacencyList> mergedAdjLists;
        if (Direction.FORWARD == direction) {
            permanentAdjacencyLists = forwardAdjLists;
            mergedAdjLists = mergedForwardAdjLists;
        } else {
            permanentAdjacencyLists = backwardAdjLists;
            mergedAdjLists = mergedBackwardAdjLists;
        }
        if (graphVersion == GraphVersion.MERGED && mergedAdjLists.containsKey(fromVertexId)) {
            return mergedAdjLists.get(fromVertexId).contains(toVertexId, typeFilter);
        }
        return permanentAdjacencyLists[fromVertexId].contains(toVertexId, typeFilter);
    }

    /**
     * Returns the {@link SortedAdjacencyList} for the given {@code vertexId}, {@code direction}
     * and {@code graphVersion}.
     *
     * @param vertexId     The vertex ID whose adjacency list is required.
     * @param direction    The {@code Direction} of the adjacency list.
     * @param graphVersion The {@code GraphVersion} to consider.
     * @return The adjacency list for the vertex with the given {@code vertexId}, for the given
     * {@code graphVersion} and {@code direction}.
     */
    public SortedAdjacencyList getSortedAdjacencyList(int vertexId, Direction direction, GraphVersion graphVersion) {
        if (vertexId < 0 || vertexId > highestMergedVertexId) {
            throw new NoSuchElementException(vertexId + " does not exist.");
        } else if (GraphVersion.PERMANENT == graphVersion && vertexId > highestPermanentVertexId) {
            // The vertexId's adj list has not been added yet. vertexId > highestPermanentVertexId
            return new SortedAdjacencyList(IS_WEIGHTED);
        } else if (GraphVersion.DIFF_MINUS == graphVersion || GraphVersion.DIFF_PLUS == graphVersion) {
            throw new UnsupportedOperationException(
                    "Getting adjacency lists from the DIFF_PLUS " + "or DIFF_MINUS graph is not supported.");
        }
        SortedAdjacencyList[] permanentAdjList;
        Map<Integer, SortedAdjacencyList> mergedAdjLists;
        if (Direction.FORWARD == direction) {
            permanentAdjList = forwardAdjLists;
            mergedAdjLists = mergedForwardAdjLists;
        } else {
            permanentAdjList = backwardAdjLists;
            mergedAdjLists = mergedBackwardAdjLists;
        }
        if (GraphVersion.MERGED == graphVersion && mergedAdjLists.containsKey(vertexId)) {
            // Use the adjacency list of the merged graph.
            return mergedAdjLists.get(vertexId);
        } else if (vertexId <= highestPermanentVertexId) {
            // Use the adjacency list of the permanent graph.
            return permanentAdjList[vertexId];
        } else {
            // The vertexId's adj list has not been added yet. vertexId > highestPermanentVertexId
            return new SortedAdjacencyList(IS_WEIGHTED);
        }
    }

    /**
     * Implemented for Differential-BFS experiments.
     */
    public SortedAdjacencyList getBackwardMergedAdjacencyList(int vertexId) {
        if (mergedBackwardAdjLists.containsKey(vertexId)) {
            return mergedBackwardAdjLists.get(vertexId);
        } else if (vertexId <= highestPermanentVertexId) {
            // Use the adjacency list of the permanent graph.
            return backwardAdjLists[vertexId];
        } else {
            // The vertexId's adj list has not been added yet. vertexId > highestPermanentVertexId
            return new SortedAdjacencyList(IS_WEIGHTED);
        }
    }

    /**
     * Implemented for Differential-BFS experiments.
     */
    public SortedAdjacencyList getForwardMergedAdjacencyList(int vertexId) {
        if (mergedForwardAdjLists.containsKey(vertexId)) {
            return mergedForwardAdjLists.get(vertexId);
        } else if (vertexId <= highestPermanentVertexId) {
            // Use the adjacency list of the permanent graph.
            return forwardAdjLists[vertexId];
        } else {
            // The vertexId's adj list has not been added yet. vertexId > highestPermanentVertexId
            return new SortedAdjacencyList(IS_WEIGHTED);
        }
    }

    /**
     * Implemented for Differential-BFS experiments.
     */
    public SortedAdjacencyList getForwardUnMergedAdjacencyList(int vertexId) {
        if (vertexId <= highestPermanentVertexId && null != forwardAdjLists) {
            return forwardAdjLists[vertexId];
        } else {
            return new SortedAdjacencyList(IS_WEIGHTED);
        }
    }

    /**
     * Implemented for Differential-BFS experiments.
     */
    public SortedAdjacencyList getBackwardUnMergedAdjacencyList(int vertexId) {
        if (vertexId <= highestPermanentVertexId && null != backwardAdjLists) {
            return backwardAdjLists[vertexId];
        } else {
            return new SortedAdjacencyList(IS_WEIGHTED);
        }
    }

    /**
     * Returns the {@link GraphVersion#DIFF_PLUS} or {@link GraphVersion#DIFF_MINUS} edges of the
     * graph.
     *
     * @param graphVersion The vertices in the diffPlus or diffMinus in the graph.
     */
    public List<int[]> getDiffEdges(GraphVersion graphVersion) {
        if (graphVersion != GraphVersion.DIFF_PLUS && graphVersion != GraphVersion.DIFF_MINUS) {
            throw new IllegalArgumentException("The graph version should be " + GraphVersion.DIFF_PLUS.name() + " or " +
                    GraphVersion.DIFF_MINUS.name() + ".");
        }
        return graphVersion == GraphVersion.DIFF_PLUS ? diffPlusEdges : diffMinusEdges;
    }

    /**
     * Returns the {@code vertexTypes} in the graph where the type of vertex i is in the ith
     * position of the returned {@link ShortArrayList}.
     */
    public ShortArrayList getVertexTypes() {
        return vertexTypes;
    }

    /**
     * Checks if the permanent capacity exceeds {@code minCapacity} and increases the capacity if it
     * doesn't.
     *
     * @param minCapacity The minimum required size of the arrays.
     */
    private void ensureCapacity(int minCapacity) {
        int oldCapacity = forwardAdjLists.length;
        forwardAdjLists = (SortedAdjacencyList[]) ArrayUtils.resizeIfNecessary(forwardAdjLists, minCapacity);
        backwardAdjLists = (SortedAdjacencyList[]) ArrayUtils.resizeIfNecessary(backwardAdjLists, minCapacity);
        initializeSortedAdjacencyLists(oldCapacity, forwardAdjLists.length);
    }

    /**
     * Initializes {@link Graph#forwardAdjLists} and {@link Graph#backwardAdjLists} with empty
     * {@link SortedAdjacencyList} in the range given by {@code startIndex} and {@code endIndex}.
     *
     * @param startIndex The start index for initializing {@link SortedAdjacencyList}, inclusive.
     * @param endIndex   The end index for initializing {@link SortedAdjacencyList}, exclusive.
     */
    private void initializeSortedAdjacencyLists(int startIndex, int endIndex) {
        for (int i = startIndex; i < endIndex; ++i) {
            forwardAdjLists[i] = new SortedAdjacencyList(IS_WEIGHTED);
            backwardAdjLists[i] = new SortedAdjacencyList(IS_WEIGHTED);
        }
    }

    @UsedOnlyByTests
    void setHighestMergedVertexId(int highestMergedVertexId) {
        this.highestMergedVertexId = highestMergedVertexId;
    }

    /**
     * Convert the graph to a {@code String}.
     *
     * @return The {@code String} representation of the graph.
     */
    @Override
    public String toString() {
        String graph =
                "Forward Adjacency Lists:" + System.lineSeparator() + convertPermanentAdjListsToString(forwardAdjLists);
        graph += "Backward Adjacency Lists:" + System.lineSeparator() +
                convertPermanentAdjListsToString(backwardAdjLists);
        graph += "Temporarily added edges: " + convertDiffEdgesToString(diffPlusEdges) + System.lineSeparator();
        graph += "Temporarily deleted edges: " + convertDiffEdgesToString(diffMinusEdges) + System.lineSeparator();
        graph += "Merged Forward Adjacency Lists: " + convertMergedAdjListsToString(mergedForwardAdjLists);
        graph += "Merged Backward Adjacency Lists: " + convertMergedAdjListsToString(mergedBackwardAdjLists);
        graph += "highestPermanentVertexId = " + highestPermanentVertexId + System.lineSeparator();
        graph += "highestMergedVertexId = " + highestMergedVertexId + System.lineSeparator();
        return graph;
    }

    /**
     * Converts the permanent adjacency lists {@code permanentAdjLists} to a {@code String}.
     *
     * @param permanentAdjLists The permanent adjacency list to convert to a {@code String}.
     * @return The{@code String} representation of {@code permanentAdjLists}.
     */
    private String convertPermanentAdjListsToString(SortedAdjacencyList[] permanentAdjLists) {
        StringBuilder adjString = new StringBuilder();
        for (int index = 0; index <= highestPermanentVertexId; index++) {
            SortedAdjacencyList adjList = permanentAdjLists[index];
            adjString.append(index).append(": ");
            adjString.append((null == adjList) ? "[]" : adjList.toString());
            adjString.append(System.lineSeparator());
        }
        return adjString.toString();
    }

    /**
     * Converts the merged adjacency lists {@code mergedAdjLists} to a {@code String}.
     *
     * @param mergedAdjLists The merged adjacency list to convert to a {@code String}.
     * @return The {@code String} representation of {@code permanentAdjLists}.
     */
    private String convertMergedAdjListsToString(Map<Integer, SortedAdjacencyList> mergedAdjLists) {
        StringBuilder adjString = new StringBuilder();
        for (int index : mergedAdjLists.keySet()) {
            SortedAdjacencyList adjList = mergedAdjLists.get(index);
            adjString.append(index).append(": ");
            adjString.append((null == adjList) ? "[]" : adjList.toString());
            adjString.append(System.lineSeparator());
        }
        return adjString.toString();
    }

    /**
     * Converts the list of diff edges {@code diffEdges} to a {@code String}.
     *
     * @param diffEdges The list of diff edges to convert to a {@code String}.
     * @return The {@code String} representation of {@code diffEdges}.
     */
    private String convertDiffEdgesToString(List<int[]> diffEdges) {
        StringJoiner stringJoiner = new StringJoiner(",");
        for (int[] edge : diffEdges) {
            stringJoiner.add(Arrays.toString(edge));
        }
        return "[" + stringJoiner.toString() + "]";
    }

    @Override
    public void serializeAll(String outputDirectoryPath) throws IOException, InterruptedException {
        finalizeChanges();
        ParallelArraySerDeUtils parallelArraySerDeHelper =
                new GraphParallelSerDeUtils(outputDirectoryPath, forwardAdjLists, backwardAdjLists,
                        highestPermanentVertexId + 1);
        parallelArraySerDeHelper.startSerialization();
        MainFileSerDeHelper.serialize(this, outputDirectoryPath);
        parallelArraySerDeHelper.finishSerDe();
    }

    @Override
    public void deserializeAll(String inputDirectoryPath)
            throws IOException, ClassNotFoundException, InterruptedException {
        MainFileSerDeHelper.deserialize(this, inputDirectoryPath);
        ParallelArraySerDeUtils parallelArraySerDeHelper =
                new GraphParallelSerDeUtils(inputDirectoryPath, forwardAdjLists, backwardAdjLists,
                        highestPermanentVertexId + 1);
        parallelArraySerDeHelper.startDeserialization();
        parallelArraySerDeHelper.finishSerDe();
    }

    @Override
    public void serializeMainFile(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeBoolean(IS_WEIGHTED);
        objectOutputStream.writeInt(highestPermanentVertexId);
        objectOutputStream.writeInt(forwardAdjLists.length);
        objectOutputStream.writeInt(backwardAdjLists.length);
        vertexTypes.serialize(objectOutputStream);
    }

    @Override
    public void deserializeMainFile(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        IS_WEIGHTED = objectInputStream.readBoolean();
        highestPermanentVertexId = objectInputStream.readInt();
        highestMergedVertexId = highestPermanentVertexId;
        int forwardAdjListsLength = objectInputStream.readInt();
        int backwardAdjListsLength = objectInputStream.readInt();
        forwardAdjLists = new SortedAdjacencyList[forwardAdjListsLength];
        backwardAdjLists = new SortedAdjacencyList[backwardAdjListsLength];
        initializeSortedAdjacencyLists(0, highestPermanentVertexId + 1);
        vertexTypes.deserialize(objectInputStream);
    }

    @Override
    public String getMainFileNamePrefix() {
        return Graph.class.getName().toLowerCase();
    }

    /**
     * @param k
     * @param direction
     * @return A hash table of vertex ids/degree with top-K degrees
     */
    public HashMap<Integer, Integer> getTopK(int k, Graph.Direction direction) {

        HashMap<Integer, Integer> topK = new LinkedHashMap<>(k);
        int minimumPickedDegree = -1;
        int minimumPickedVertex = -1;

        //Report.INSTANCE.debug("---- Looking for topK vertices ; k = " + k + " - direction = " + direction);

        for (int vertex = 0; vertex < forwardAdjLists.length; vertex++) {

            int degree = 0;
            degree = getVertexDegree(vertex, direction);

            //Report.INSTANCE.debug("*** vertex " + vertex + " degree = " + degree + " vs min " + minimumPickedDegree);

            // we need to replace the minimum vertex in topK by a new one
            if (degree > minimumPickedDegree) {

                // topK is full, we need to remove the minimum
                if (topK.keySet().size() == k) {
                    topK.remove(minimumPickedVertex);
                }

                // Add the new found vertex
                topK.putIfAbsent(vertex, degree);

                // update information about minimum vertex/degree
                minimumPickedDegree = Integer.MAX_VALUE;
                for (Entry<Integer, Integer> entry : topK.entrySet()) {
                    if (entry.getValue() < minimumPickedDegree) {
                        minimumPickedDegree = entry.getValue();
                        minimumPickedVertex = entry.getKey();
                    }
                }
            }
        }
        return topK;
    }


    public void printDistribution(Direction d){

        int minDegree = Integer.MAX_VALUE;
        int maxDegree = Integer.MIN_VALUE;
        int histogramSize = 1100;

        int[] range = new int[histogramSize];
        int[] histogram = new int[histogramSize];

        for (int v = 0; v < getHighestVertexId(); v++) {
            int degree = getVertexDegree(v, d);
            if (degree < minDegree) minDegree = degree;
            if (degree > maxDegree) maxDegree = degree;
        }

        double rangeStep =  maxDegree /histogramSize;
        range[0] = 0;
        range[histogramSize-1] = maxDegree+1;
        for(int i=1;i<(histogramSize-1);i++){
            range[i] = minDegree + (int) Math.floor(rangeStep * i);
            histogram[i] = 0;
        }

        // For smaller degrees, make the bins small
        for (int i=0;i<=1015;i++){
            range[i]=i;
        }


        // loop on all vertices
        for (int v = 0; v < getHighestVertexId(); v++) {
            double degree = getVertexDegree(v, d);
/*
            SortedAdjacencyList neighbours = getForwardUnMergedAdjacencyList(v);
            boolean skip = true;
            for (int n=0; n< neighbours.getSize();n++){
                if (neighbours.edgeTypes[n] == TypeAndPropertyKeyStore.getInstance().mapStringTypeToShort("replyOf")){
                    skip = false;
                    break;
                }
            }
            if(skip)
                continue;
*/
            for (int i = 1; i < histogramSize; i++) {
                if (range[i-1] < degree && degree <= range[i]) {
                    histogram[i]++;
                    break;
                }
            }
        }
            System.out.println("Graph Histogram with Direction = "+d.name());
            System.out.println("Direction "+d.name()+" Degree "+Arrays.toString(range));
            System.out.println("Direction "+d.name()+" Count "+Arrays.toString(histogram));
    }

    public double[] getDegreeStats(Direction d, int threshold) {

        double[] result = {Double.MAX_VALUE, 0.0, 0.0, 0.0, 0.0}; // Min, Max, Sum, count_non_zero, more than threshold
        for (int v = 0; v < getHighestVertexId(); v++) {
            double degree = getVertexDegree(v, d);
            if (degree > 0) {
                if (degree < result[0]) {
                    result[0] = degree;
                }
                if (degree > result[1]) {
                    result[1] = degree;
                }
                result[2] += degree;
                result[3]++;
                if (degree > threshold) {
                    result[4]++;
                }
            }
        }

        result[2] = result[2] / result[3];
        return result;
    }
}
