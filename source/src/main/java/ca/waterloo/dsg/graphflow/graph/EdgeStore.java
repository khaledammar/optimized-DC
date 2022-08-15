package ca.waterloo.dsg.graphflow.graph;

import ca.waterloo.dsg.graphflow.graph.serde.EdgeStoreParallelSerDeUtils;
import ca.waterloo.dsg.graphflow.graph.serde.MainFileSerDeHelper;
import ca.waterloo.dsg.graphflow.util.annotation.UsedOnlyByTests;
import ca.waterloo.dsg.graphflow.util.annotation.VisibleForTesting;
import ca.waterloo.dsg.graphflow.util.collection.ArrayUtils;
import ca.waterloo.dsg.graphflow.util.datatype.DataType;
import org.antlr.v4.runtime.misc.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Stores the IDs and properties of the edges in the Graph.
 * Warning: The properties of a deleted edge are not deleted. The ID of the deleted edge is recycled
 * and the properties are overwritten by those of the edge that gets assigned the recycled ID next.
 */
public class EdgeStore extends PropertyStore {

    @VisibleForTesting
    static final int MAX_EDGES_PER_BUCKET = 8;
    private static final int INITIAL_CAPACITY = 2;
    private static final int MAX_BUCKETS_PER_PARTITION = 1000000;
    private static EdgeStore INSTANCE = new EdgeStore();
    @VisibleForTesting
    byte[][][] data = new byte[INITIAL_CAPACITY][][];
    @VisibleForTesting
    int[][][] dataOffsets = new int[INITIAL_CAPACITY][][];
    private long nextIDNeverYetAssigned = 0;
    private byte nextBucketOffset = 0;
    private int nextBucketId = 0;
    private int nextPartitionId = 0;
    private long[] recycledIds = new long[INITIAL_CAPACITY];
    private int recycledIdsSize = 0;

    /**
     * Empty private constructor enforces usage of the singleton object {@link #INSTANCE} for this
     * class.
     */
    private EdgeStore() {}

    /**
     * Adds a new edge and sets its properties to the given properties.
     *
     * @param properties The properties of the edge as <key, <DataType, value>> pairs.
     *
     * @return The ID of the added edge.
     */
    public long addEdge(Map<Short, Pair<DataType, String>> properties) {
        long edgeId = getNextIdToAssign();
        setProperties(edgeId, properties);
        return edgeId;
    }

    /**
     * Returns the next ID to assign to an edge. If there are recycled IDs, which is an ID
     * previously assigned to an edge that was deleted, the last added ID to the recycled IDs array
     * is returned. Otherwise, the 8 byte {@code long} ID is assigned as follows:
     * <ul>
     * <li>The 3 most significant bytes are the partition ID. There are up to {@link
     * EdgeStore#MAX_BUCKETS_PER_PARTITION} buckets in each partition.</li>
     * <li> The next 4 bytes are the bucket ID. There are up to
     * {@link EdgeStore#MAX_EDGES_PER_BUCKET} edges in each bucket.</li>
     * <li> The last byte is the index of the edge in the bucket.</li>
     * </ul>
     */
    @VisibleForTesting
    long getNextIdToAssign() {
        long nextIDToAssign;
        if (recycledIdsSize > 0) {
            nextIDToAssign = recycledIds[--recycledIdsSize];
        } else {
            nextIDToAssign = nextIDNeverYetAssigned;
            incrementNextIDNeverYetAssigned();
        }
        return nextIDToAssign;
    }

    /**
     * Returns the {@code Short} key, and {@code Object} value pair properties of the edge with the
     * given ID.
     * Warning: If the ID provided is an ID of a deleted edge, the properties of the deleted edge
     * are returned.
     *
     * @param edgeId The ID of the edge.
     *
     * @return The properties of the edge as a Map<Short, Object>.
     *
     * @throws NoSuchElementException if the {@code edgeId} has never been assigned before.
     */
    Map<Short, Object> getProperties(long edgeId) {
        verifyEdgeIdAndResetPropertyIterator(edgeId);
        Map<Short, Object> edgeProperties = new HashMap<>();
        Pair<Short, Object> keyValue;
        while (propertyIterator.hasNext()) {
            keyValue = propertyIterator.next();
            edgeProperties.put(keyValue.a, keyValue.b);
        }
        return edgeProperties;
    }

    /**
     * Returns the {@code String} key, and {@code String} value pair properties of the edge with the
     * given ID.
     * Warning: If the ID provided is an ID of a deleted edge, the properties of the deleted edge
     * are returned.
     *
     * @param edgeId The ID of the edge.
     *
     * @return The properties of the edge as a Map<String, String>.
     *
     * @throws NoSuchElementException if the {@code edgeId} has never been assigned before.
     */
    public Map<String, String> getPropertiesAsStrings(long edgeId) {
        verifyEdgeIdAndResetPropertyIterator(edgeId);
        Map<String, String> edgeProperties = new HashMap<>();
        Pair<Short, Object> keyValue;
        TypeAndPropertyKeyStore typeAndPropertyKeyStore = TypeAndPropertyKeyStore.getInstance();
        while (propertyIterator.hasNext()) {
            keyValue = propertyIterator.next();
            edgeProperties.put(typeAndPropertyKeyStore.mapShortPropertyKeyToString(keyValue.a),
                keyValue.b.toString());
        }
        return edgeProperties;
    }

    private void verifyEdgeIdAndResetPropertyIterator(long edgeId) {
        if (edgeId >= nextIDNeverYetAssigned) {
            throw new NoSuchElementException("Edge with ID " + edgeId + " does not exist.");
        }
        int partitionId = (int) ((edgeId & 0xFFFFFF0000000000L) >> 40);
        int bucketId = (int) ((edgeId & 0x000000FFFFFFFF00L) >> 8);
        byte bucketOffset = (byte) (edgeId & 0x00000000000000FFL);

        int dataOffsetStart = dataOffsets[partitionId][bucketId][bucketOffset];
        int dataOffsetEnd;
        if (MAX_EDGES_PER_BUCKET - 1 == bucketOffset) {
            dataOffsetEnd = data[partitionId][bucketId].length;
        } else {
            dataOffsetEnd = dataOffsets[partitionId][bucketId][bucketOffset + 1];
        }
        propertyIterator.reset(data[partitionId][bucketId], dataOffsetStart, dataOffsetEnd);
    }

    /**
     * Given an edge ID, and a property key, returns the value of the property that is on the edge
     * with the given edge ID and that has the given key. If the edge does not contain a property
     * with the given key, returns null.
     *
     * @param edgeId ID of an edge.
     * @param key key of a property.
     *
     * @return the given edge's property with the given key or null if no such property exists.
     */
    public Object getProperty(long edgeId, short key) {
        verifyEdgeIdAndResetPropertyIterator(edgeId);
        return getPropertyFromIterator(key);
    }

    /**
     * Sets the properties of the given edge to the given properties serialized to bytes.
     *
     * @param edgeId The ID of the edge.
     * @param properties The properties of the edge. See {@link #addEdge(Map)}.
     */
    private void setProperties(long edgeId, Map<Short, Pair<DataType, String>> properties) {
        int partitionId = (int) ((edgeId & 0xFFFFFF0000000000L) >> 40);
        int bucketId = (int) ((edgeId & 0x000000FFFFFFFF00L) >> 8);
        byte bucketOffset = (byte) (edgeId & 0x00000000000000FFL);
        resizeIfNecessary(partitionId, bucketId);

        int dataOffsetStart = dataOffsets[partitionId][bucketId][bucketOffset];
        int dataOffsetEnd;
        if (MAX_EDGES_PER_BUCKET - 1 == bucketOffset) {
            dataOffsetEnd = data[partitionId][bucketId].length - 1;
        } else {
            dataOffsetEnd = dataOffsets[partitionId][bucketId][bucketOffset + 1] - 1;
        }

        byte[] propertiesAsBytes = serializeProperties(properties);
        int bucketLength = data[partitionId][bucketId].length;
        byte[] newPropertiesForTheBucket = new byte[dataOffsetStart /* length of 1st half */ +
            bucketLength - (dataOffsetEnd + 1) /* length of 2nd half */ + propertiesAsBytes.length];

        // copy the old data + new properties to the new array.
        System.arraycopy(data[partitionId][bucketId], 0, newPropertiesForTheBucket, 0,
            dataOffsetStart);
        System.arraycopy(propertiesAsBytes, 0, newPropertiesForTheBucket, dataOffsetStart,
            propertiesAsBytes.length);
        if (newPropertiesForTheBucket.length > propertiesAsBytes.length + dataOffsetStart) {
            System.arraycopy(data[partitionId][bucketId], dataOffsetEnd, newPropertiesForTheBucket,
                dataOffsetStart + propertiesAsBytes.length, bucketLength - (dataOffsetEnd + 1));
        }
        data[partitionId][bucketId] = newPropertiesForTheBucket;

        // update the offsets
        int shiftOffset = dataOffsetStart - (dataOffsetEnd + 1) + propertiesAsBytes.length;
        if (0 != shiftOffset) {
            for (int i = bucketOffset + 1; i < MAX_EDGES_PER_BUCKET; ++i) {
                dataOffsets[partitionId][bucketId][i] += shiftOffset;
            }
        }
    }

    /**
     * Deletes the edge with the given ID.
     * Warning: Internally adds the given ID to the recycled IDs array.
     *
     * @param edgeId The ID of the edge to delete.
     *
     * @throws NoSuchElementException if the {@code edgeId} has never been assigned before.
     */
    void deleteEdge(long edgeId) {
        if (edgeId >= nextIDNeverYetAssigned) {
            throw new NoSuchElementException("Edge with ID " + edgeId + " does not exist.");
        }
        recycledIds = ArrayUtils.resizeIfNecessary(recycledIds, recycledIdsSize + 1,
            -1 /* default value to fill new cells if resizing */);
        recycledIds[recycledIdsSize++] = edgeId;
    }

    private void incrementNextIDNeverYetAssigned() {
        if (nextBucketOffset < MAX_EDGES_PER_BUCKET - 1) {
            nextBucketOffset++;
        } else {
            nextBucketOffset = 0;
            if (nextBucketId < MAX_BUCKETS_PER_PARTITION - 1) {
                nextBucketId++;
            } else {
                nextBucketId = 0;
                nextPartitionId++;
            }
        }
        nextIDNeverYetAssigned = (((long) (nextPartitionId & 0x00FFFFFF) << 40) &
            0xFFFFFF0000000000L) | ((((long) nextBucketId) << 8) & 0x000000FFFFFFFF00L)
            | (((long) nextBucketOffset) & 0x00000000000000FFL);
    }

    private void resizeIfNecessary(int partitionId, int bucketId) {
        data = ArrayUtils.resizeIfNecessary(data, partitionId + 1);
        dataOffsets = ArrayUtils.resizeIfNecessary(dataOffsets, partitionId + 1);

        if (null == data[partitionId]) {
            data[partitionId] = new byte[0][];
        }
        if (null == dataOffsets[partitionId]) {
            dataOffsets[partitionId] = new int[0][];
        }

        data[partitionId] = ArrayUtils.resizeIfNecessary(data[partitionId], bucketId + 1);
        dataOffsets[partitionId] = ArrayUtils.resizeIfNecessary(dataOffsets[partitionId],
            bucketId + 1);

        if (null == data[partitionId][bucketId]) {
            data[partitionId][bucketId] = new byte[0];
        }
        if (null == dataOffsets[partitionId][bucketId]) {
            dataOffsets[partitionId][bucketId] = new int[MAX_EDGES_PER_BUCKET];
        }
    }

    @UsedOnlyByTests
    void setNextIDNeverYetAssigned(int partitionID, int bucketID, byte bucketOffset) {
        this.nextPartitionId = partitionID;
        this.nextBucketId = bucketID;
        this.nextBucketOffset = bucketOffset;
        this.nextIDNeverYetAssigned = (((long) (nextPartitionId & 0x00FFFFFF) << 40) &
            0xFFFFFF0000000000L) | ((((long) nextBucketId) << 8) & 0x000000FFFFFFFF00L)
            | (((long) nextBucketOffset) & 0x00000000000000FFL);
    }

    @Override
    public void serializeAll(String outputDirectoryPath) throws IOException, InterruptedException {
        super.serializeAll(outputDirectoryPath);
        EdgeStoreParallelSerDeUtils parallelArraySerDeHelper = new EdgeStoreParallelSerDeUtils(
            outputDirectoryPath, data, dataOffsets, nextPartitionId + 1);
        parallelArraySerDeHelper.startSerialization();
        MainFileSerDeHelper.serialize(this, outputDirectoryPath);
        parallelArraySerDeHelper.finishSerDe();
    }

    @Override
    public void deserializeAll(String inputDirectoryPath) throws IOException,
        ClassNotFoundException,
        InterruptedException {
        // Deserialize main file first to initialize arrays.
        MainFileSerDeHelper.deserialize(this, inputDirectoryPath);
        EdgeStoreParallelSerDeUtils parallelArraySerDeHelper = new EdgeStoreParallelSerDeUtils(
            inputDirectoryPath, data, dataOffsets, nextPartitionId + 1);
        parallelArraySerDeHelper.startDeserialization();
        parallelArraySerDeHelper.finishSerDe();
    }

    @Override
    public void serializeMainFile(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeInt(data.length);
        objectOutputStream.writeLong(nextIDNeverYetAssigned);
        objectOutputStream.writeByte(nextBucketOffset);
        objectOutputStream.writeInt(nextBucketId);
        objectOutputStream.writeInt(nextPartitionId);
        objectOutputStream.writeInt(recycledIdsSize);
        objectOutputStream.writeObject(recycledIds);
    }

    @Override
    public void deserializeMainFile(ObjectInputStream objectInputStream) throws IOException,
        ClassNotFoundException {
        int arrayLength = objectInputStream.readInt();
        this.data = new byte[arrayLength][][];
        this.dataOffsets = new int[arrayLength][][];
        this.nextIDNeverYetAssigned = objectInputStream.readLong();
        this.nextBucketOffset = objectInputStream.readByte();
        this.nextBucketId = objectInputStream.readInt();
        this.nextPartitionId = objectInputStream.readInt();
        this.recycledIdsSize = objectInputStream.readInt();
        this.recycledIds = (long[]) objectInputStream.readObject();
    }

    @Override
    public String getMainFileNamePrefix() {
        return EdgeStore.class.getName().toLowerCase();
    }

    /**
     * Resets {@link EdgeStore} by creating a new {@code INSTANCE}.
     */
    static void reset() {
        INSTANCE = new EdgeStore();
    }

    /**
     * Returns the singleton instance {@link #INSTANCE} of {@link EdgeStore}.
     */
    public static EdgeStore getInstance() {
        return INSTANCE;
    }
}
