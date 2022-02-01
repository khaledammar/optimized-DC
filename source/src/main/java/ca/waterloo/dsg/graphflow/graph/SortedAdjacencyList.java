package ca.waterloo.dsg.graphflow.graph;

import ca.waterloo.dsg.graphflow.util.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.StringJoiner;

/**
 * Represents the adjacency list of a vertex. Stores the IDs of the vertex's
 * neighbours, the types, and the IDs of edges that the vertex has to these
 * neighbours in sorted arrays. Arrays are sorted first by neighbour IDs and
 * then by edge {@code short} type values.
 */
public class SortedAdjacencyList {

    // if the initial capacity is more than 0, then some vertices will have a vertex 0 as in or out Neighbour.
    // This is not correct, specially if vertex 0 exist in the graph!
    private static final int INITIAL_CAPACITY = 0;
    @VisibleForTesting
    public int[] neighbourIds = null;
    @VisibleForTesting
    public short[] edgeTypes = null;
    public double[] weights = null;
    private int size;

    public SortedAdjacencyList() {
        this(false /* no weights */);
    }

    /**
     * Default constructor for {@link SortedAdjacencyList}. Initializes the
     * arrays holding neighbour data to default initial capacity.
     */
    public SortedAdjacencyList(boolean isWeighted) {
        this(INITIAL_CAPACITY, isWeighted);
    }

    /**
     * Initializes the arrays holding neighbour IDs and edge types to the given
     * capacity.
     *
     * @param initialCapacity The initial capacity of the arrays holding neighbour IDs and
     *                        edge types.
     */
    public SortedAdjacencyList(int initialCapacity, boolean isWeighted) {
        neighbourIds = new int[initialCapacity];
        edgeTypes = new short[initialCapacity];
        if (isWeighted) {
            weights = new double[initialCapacity];
        } else {
            weights = null;
        }
    }

    /**
     * A static helper function to test whether a {@link SortedAdjacencyList} is
     * null or empty
     *
     * @param list the {@link SortedAdjacencyList} to test for null or emptiness
     * @return a boolean to indicate if the given {@code list} is empty or null
     */
    public static boolean isNullOrEmpty(SortedAdjacencyList list) {
        return null == list || list.getSize() == 0;
    }

    /**
     * Used during unit testing to check the equality of objects. This is used
     * instead of overriding the standard {@code equals()} and
     * {@code hashCode()} methods.
     * <p>
     * Warning: The below code does not take into account the weights or the edgeIDs being null.
     *
     * @param a One of the objects.
     * @param b The other object.
     * @return {@code true} if {@code a}'s values are the same as {@code b}'s.
     */
    @UsedOnlyByTests
    public static boolean isSameAs(SortedAdjacencyList a, SortedAdjacencyList b) {
        if (a == b) {
            return true;
        }
        if (null == a || null == b) {
            return false;
        }
        if (a.size != b.size) {
            return false;
        }
        for (int i = 0; i < a.size; i++) {
            if ((a.getNeighbourId(i) != b.getNeighbourId(i)) || (a.getEdgeType(i) != b.getEdgeType(i))) {
                return false;
            }
        }
        return true;
    }

    public void add(int neighbourId, short edgeType, long edgeId) {
        add(neighbourId, edgeType, edgeId, null /* no weight */, true /* sort */);
    }

    /**
     * Adds a new neighbour with the given ID, type, and edgeId.
     * <p>
     * Warning (Semih): We changed the logic of this add method to
     * "update or add". If the given edge exists, it updates the weight
     * of that edge.
     *
     * @param neighbourId The ID of the neighbour.
     * @param edgeType    The type of the edge to the neighbour.
     * @param edgeId      The ID of the edge to the neighbour.
     */
    public void add(int neighbourId, short edgeType, long edgeId, Double weight, boolean sort) {
        int searchIndex = search(neighbourId, edgeType);
        if (0 <= searchIndex) {
            // TODO(semih): Looks like there is a bug here.
            if (weights != null) {
                if (weight == null) {
                    weight = 1.0;
                } else {
                    weights[searchIndex] = weight;
                }
            }
        } else {
            ensureCapacity(size + 1);
            neighbourIds[size] = neighbourId;
            edgeTypes[size] = edgeType;
            if (weights != null) {
                if (weight == null) {
                    weight = 1.0;
                } else {
                    weights[size] = weight;
                }
            }
            size++;
            if (sort) {
                sort();
            }
        }
    }

    /**
     * Adds the given {@link SortedAdjacencyList} to the current
     * {@link SortedAdjacencyList}.
     *
     * @param otherList The {@link SortedAdjacencyList} to merge.
     */
    public void addAll(SortedAdjacencyList otherList) {
        ensureCapacity(size + otherList.getSize());
        for (int i = 0; i < otherList.getSize(); i++) {
            neighbourIds[size + i] = otherList.getNeighbourId(i);
            edgeTypes[size + i] = otherList.getEdgeType(i);
            if (null != weights) {
                weights[size + i] = otherList.weights[i];
            }
        }
        size += otherList.getSize();
        sort();
    }

    /**
     * Returns the neighbour ID at the given {@code index}.
     *
     * @param index The index of the neighbour ID.
     * @return The neighbour ID at the given index.
     * @throws ArrayIndexOutOfBoundsException If {@code index} is greater than the size of this
     *                                        {@code SortedAdjacencyList}.
     */
    public int getNeighbourId(int index) {
        if (index >= size) {
            throw new ArrayIndexOutOfBoundsException(
                    "No edge at index " + index + ". Therefore " + "cannot return the neighbour ID.");
        }
        return neighbourIds[index];
    }

    /**
     * Returns the neighbour Weight at the given {@code index}.
     *
     * @param index The index of the neighbour ID.
     * @return The neighbour weight at the given index - if weight is null (unWeighted graph) return 1
     * @throws ArrayIndexOutOfBoundsException If {@code index} is greater than the size of this
     *                                        {@code SortedAdjacencyList}.
     */
    public double getNeighbourWeight(int index) {
        if (index >= size) {
            throw new ArrayIndexOutOfBoundsException(
                    "No edge at index " + index + ". Therefore " + "cannot return the neighbour ID.");
        }
        if (weights == null || weights.length <= index) {
            return 1;
        } else {
            return weights[index];
        }
    }

    /**
     * Returns the edge type at the given {@code index}.
     *
     * @param index The index of the edge type.
     * @return The edge type at the given index.
     * @throws ArrayIndexOutOfBoundsException If {@code index} is greater than the size of this
     *                                        {@code SortedAdjacencyList}.
     */
    public short getEdgeType(int index) {
        if (index >= size) {
            throw new ArrayIndexOutOfBoundsException(
                    "No edge at index " + index + ". Therefore " + "cannot return the edge type.");
        }
        return edgeTypes[index];
    }

    /**
     * Returns the subset of the neighbour IDs whose type matches the given
     * {@code edgeTypeFilter}.
     *
     * @param toVertexTypeFilter The to vertex type for filtering.
     * @param edgeTypeFilter     The edge type for filtering.
     * @return IntArrayList The subset of neighbour IDs matching
     * {@code toVertexTypeFilter} and {@code edgeTypeFilter}
     */
    public IntArrayList getFilteredNeighbourIds(short toVertexTypeFilter, short edgeTypeFilter,
                                                ShortArrayList vertexTypes) {
        IntArrayList filteredList = new IntArrayList(size);
        if (TypeAndPropertyKeyStore.ANY == edgeTypeFilter && TypeAndPropertyKeyStore.ANY == toVertexTypeFilter) {
            filteredList.addAll(Arrays.copyOf(neighbourIds, size));
        } else {
            for (int i = 0; i < size; i++) {
                if ((TypeAndPropertyKeyStore.ANY == toVertexTypeFilter ||
                        vertexTypes.get(neighbourIds[i]) == toVertexTypeFilter) &&
                        (TypeAndPropertyKeyStore.ANY == edgeTypeFilter || edgeTypes[i] == edgeTypeFilter)) {
                    filteredList.add(neighbourIds[i]);
                }
            }
        }
        return filteredList;
    }

    /**
     * Returns the subset of the neighbour IDs whose type matches the given
     * {@code edgeTypeFilter}.
     *
     * @param edgeTypeFilter The edge type for filtering.
     * @return IntArrayList The subset of neighbour IDs matching
     * {@code toVertexTypeFilter} and {@code edgeTypeFilter}
     */
    public IntArrayList getFilteredNeighbourIds(short edgeTypeFilter) {
        IntArrayList filteredList = new IntArrayList(size);
        if (TypeAndPropertyKeyStore.ANY == edgeTypeFilter) {
            filteredList.addAll(Arrays.copyOf(neighbourIds, size));
        } else {
            for (int i = 0; i < size; i++) {
                if (edgeTypes[i] == edgeTypeFilter) {
                    filteredList.add(neighbourIds[i]);
                }
            }
        }
        return filteredList;
    }

    /**
     * Removes the neighbour with the given {@code neighbourId} and
     * {@code edgeTypeFilter}. The properties of the edge are not deleted. The
     * ID of the edge is recycled and the properties are overwritten by those of
     * the edge that gets the recycled id next.
     *
     * @param neighbourId    The ID of the neighbour in the edge to remove.
     * @param edgeTypeFilter The type of the edge to the neighbour to remove.
     */
    public void removeNeighbour(int neighbourId, short edgeTypeFilter) {
        int index = search(neighbourId, edgeTypeFilter);
        if (index > -1) {
            int numElementsToShiftLeft = size - index - 1;
            if (numElementsToShiftLeft > 0) {
                System.arraycopy(neighbourIds, index + 1, neighbourIds, index, numElementsToShiftLeft);
                System.arraycopy(edgeTypes, index + 1, edgeTypes, index, numElementsToShiftLeft);
                if (weights != null) {
                    System.arraycopy(weights, index + 1, weights, index, numElementsToShiftLeft);
                }
            }
            --size;
        }
    }

    /**
     * Intersects the current {@link SortedAdjacencyList} with the given {@code
     * sortedListToIntersect}. If {@code edgeTypeFilter} equals
     * {@link TypeAndPropertyKeyStore#ANY} and
     * {@code edgePropertyEqualityFilters} is {@code null}, only the vertex ID
     * will be considered when intersecting. Otherwise, a valid intersection
     * will match both the vertex ID, the {@code edgeTypeFilter}, and the
     * {@code edgePropertyEqualityFilters}. Warning: We assume that the edges in
     * {@code sortedListToIntersect} already satisfy the {@code edgeTypeFilter}
     * and {@code edgePropertyEqualityFilters}. Also, we assume that it is
     * sorted in monotonically increasing order of neighbourIds first and then
     * types.
     *
     * @param sortedListToIntersect The {@link IntArrayList} to intersect.
     * @param edgeTypeFilter        The edge type for filtering the intersections.
     * @return The set of intersected vertices as an {@link IntArrayList}.
     */
    public IntArrayList getIntersection(IntArrayList sortedListToIntersect, short edgeTypeFilter) {
        IntArrayList intersection = new IntArrayList();
        int index = 0;
        for (int i = 0; i < sortedListToIntersect.size(); i++) {
            int currentElement = sortedListToIntersect.get(i);
            // We return only one neighbour vertex regardless of how many times
            // neighbour vertex
            // may be present in the adjacency list, with different edge types.
            int resultIndex = search(currentElement, edgeTypeFilter, index);
            if (resultIndex > -1) {
                intersection.add(currentElement);
            }
            if (resultIndex == Integer.MIN_VALUE) {
                index = 0;
            } else {
                index = (resultIndex) > -1 ? resultIndex : -resultIndex;
            }
        }
        return intersection;
    }

    /**
     * Returns the size of the collections in {@code neighbourIds} and
     * {@code edgeTypes}.
     *
     * @return The size of the above mentioned collections.
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns a string representation of {@link SortedAdjacencyList}.
     *
     * @return String representation.
     */
    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(", ");
        for (int i = 0; i < size; i++) {
            sj.add("{" + neighbourIds[i] + ": " + edgeTypes[i] + "}");
        }
        return "[" + sj.toString() + "]";
    }

    /**
     * @see #search(int, short)
     */
    public boolean contains(int neighbourId, short edgeTypeFilter) {
        return search(neighbourId, edgeTypeFilter, 0 /* start index */) > -1;
    }

    /**
     * Searches for the given ({@code neighbourId},{@code edgeTypeFilter}) pair
     * in {@code neighbourIds} and {@code edgeTypes} starting from the given
     * {@code startIndex} and searching to the right. Returns the index of the
     * matching ({@code neighbourId}, {@code edgeTypeFilter}) pair if it also
     * satisfies the {@code edgePropertyEqualityFilters}. If no match is found,
     * returns the negative of the index one before the largest pair less than
     * ({@code neighbourId},{@code edgeTypeFilter}) or {@code Integer.MIN_VALUE}
     * if the index is 0.
     *
     * @param neighbourId    The neighbour ID to be searched.
     * @param edgeTypeFilter The type of the edge searched for.
     * @param startIndex     The index to start the search from.
     * @return Index of the neighbour if a match is found or a negative value as
     * described above.
     */
    public int search(int neighbourId, short edgeTypeFilter, int startIndex) {
        int i = startIndex;
        int stepSize = 1;
        // We iteratively double {@code stepSize} to move forward in the list
        // until ({@code
        // neighbourId}, {@code typeId}) exceeds (u, v) at index {@code i},
        // where u is the
        // neighbour ID and v is the type ID.
        while (i < size &&
                (neighbourIds[i] < neighbourId || (neighbourId == neighbourIds[i] && edgeTypes[i] < edgeTypeFilter))) {
            stepSize <<= 1;
            i += stepSize;
        }
        // We halve {@code stepSize} iteratively until {@code i} is at the index
        // just before
        // ({@code neighbourId},{@code edgeTypeFilter}) if they exist. If they
        // do not exist,
        // {@code i} will stop at the index before the largest pair which is
        // smaller than
        // ({@code neighbourId}, {@typeId}).
        i -= stepSize;
        stepSize >>= 1;
        while (stepSize > 0) {
            if ((i + stepSize) < size && (neighbourIds[i + stepSize] < neighbourId ||
                    (neighbourIds[i + stepSize] == neighbourId && edgeTypes[i + stepSize] < edgeTypeFilter))) {
                i += stepSize;
            }
            stepSize >>= 1;
        }
        if (((i + 1) < size) && neighbourIds[i + 1] == neighbourId &&
                (TypeAndPropertyKeyStore.ANY == edgeTypeFilter || edgeTypeFilter == edgeTypes[i + 1])) {
            return i + 1;
        }
        // If ({@code neighbourId},{@code edgeTypeFilter}) does not exist,
        // return the negative value
        // of the index before the largest pair which is smaller than ({@code
        // neighbourId},
        // {@code edgeTypeFilter}), or {@code Integer.MIN_VALUE} if index is 0.
        return (i > 0) ? -i : Integer.MIN_VALUE;
    }

    /**
     * @see #search(int, short, int)
     */
    public int search(int neighbourId, short edgeTypeFilter) {
        return search(neighbourId, edgeTypeFilter, 0 /* start index */);
    }

    /**
     * Sorts {@code neighbourIds} first in ascending order of their IDs and then
     * by edge type. The {@code edgeTypes} and {@code edgeIds} are also sorted
     * to match the neighbor ID ordering.
     */
    void sort() {
        for (int i = 1; i < size; i++) {
            int tempNeighbourId = neighbourIds[i];
            short tempNeighbourType = edgeTypes[i];
            double tempWeight = weights != null ? weights[i] : Double.MIN_VALUE;
            int j = i;
            while ((j > 0) && ((tempNeighbourId < neighbourIds[j - 1]) ||
                    ((tempNeighbourId == neighbourIds[j - 1]) && (tempNeighbourType < edgeTypes[j - 1])))) {
                neighbourIds[j] = neighbourIds[j - 1];
                edgeTypes[j] = edgeTypes[j - 1];
                if (null != weights) {
                    weights[j] = weights[j - 1];
                }
                j--;
            }
            neighbourIds[j] = tempNeighbourId;
            edgeTypes[j] = tempNeighbourType;
            if (null != weights) {
                weights[j] = tempWeight;
            }
        }
    }

    private void ensureCapacity(int minCapacity) {
        neighbourIds = ArrayUtils.resizeIfNecessary(neighbourIds, minCapacity, -1);
        edgeTypes = ArrayUtils.resizeIfNecessary(edgeTypes, minCapacity);
        if (weights != null) {
            weights = ArrayUtils.resizeIfNecessary(weights, minCapacity, Double.MAX_VALUE /*
             * default value to fill new cells if resizing
             */);
        }
    }

    /**
     * See {@link GraphDBState#serialize(String)}.
     */
    public void serialize(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeInt(size);
        objectOutputStream.writeInt(neighbourIds.length);
        objectOutputStream.writeInt(edgeTypes.length);
        objectOutputStream.writeBoolean(weights != null);
        if (weights != null) {
            objectOutputStream.writeInt(weights.length);
        }
        for (int i = 0; i < size; i++) {
            objectOutputStream.writeInt(neighbourIds[i]);
            objectOutputStream.writeShort(edgeTypes[i]);
            if (weights != null) {
                objectOutputStream.writeDouble(weights[i]);
            }
        }
    }

    /**
     * See {@link GraphDBState#deserialize(String)}.
     */
    public void deserialize(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        size = objectInputStream.readInt();
        neighbourIds = new int[objectInputStream.readInt()];
        edgeTypes = new short[objectInputStream.readInt()];
        boolean hasWeights = objectInputStream.readBoolean();
        if (hasWeights) {
            weights = new double[objectInputStream.readInt()];
        } else {
            weights = null;
        }
        for (int i = 0; i < size; i++) {
            neighbourIds[i] = objectInputStream.readInt();
            edgeTypes[i] = objectInputStream.readShort();
            if (hasWeights) {
                weights[i] = objectInputStream.readDouble();
            }
        }
    }
}
