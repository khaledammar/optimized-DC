package ca.waterloo.dsg.graphflow.graph;

import ca.waterloo.dsg.graphflow.exception.IndexAlreadyExistsException;
import ca.waterloo.dsg.graphflow.exception.SerializationDeserializationException;
import ca.waterloo.dsg.graphflow.exception.UnindexableDataTypeException;
import ca.waterloo.dsg.graphflow.graph.serde.GraphflowSerializable;
import ca.waterloo.dsg.graphflow.graph.serde.MainFileSerDeHelper;
import ca.waterloo.dsg.graphflow.util.annotation.UsedOnlyByTests;
import ca.waterloo.dsg.graphflow.util.annotation.VisibleForTesting;
import ca.waterloo.dsg.graphflow.util.datatype.DataType;
import org.antlr.v4.runtime.misc.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Stores the indices maintained by the graph. An index is held on a specific type & property
 * pair.
 */
public class IndexStore implements GraphflowSerializable {

    private static IndexStore INSTANCE = new IndexStore();
    private static final String SERDE_FILE_NAME_PREFIX = "index_store";

    /**
     * A map of index key to an index. Each index is a map from a string to a set of vertex IDs.
     * The index key is an index constructed by concatenating the type and property shorts (@see
     * IndexStore#getTypePropertyIndexKey(Short, Short)).
     * The String key of each index is a token based on splitting the indexed property values on
     * white space. The properties are specified for a given property of vertices of a given type.
     *
     * When a property is indexed on Type.ANY type, a seperate index will be maintained for all
     * separate types. If a node of a new type is added that has a property that is indexed on
     * Type.ANY type a new index will be created for that type.
     */
    @VisibleForTesting
    Map<Integer, Map<String, Set<Integer>>> indices = new HashMap<>();

    /**
     * Stores the properties that are being indexed and maps to the set of types that is indexed on.
     * If the property is indexed on all types, it cannot be indexed on another more specific
     * type. Therefore the set of the types will have only 1 element: the Type.ANY value.
      */
    @VisibleForTesting
    Map<Short, Set<Short>> indexedPropertiesAndTypes = new HashMap<>();

    /**
     * Returns the singleton instance {@link #INSTANCE}.
     */
    public static IndexStore getInstance() {
        return INSTANCE;
    }

    /**
     * Resets the {@link IndexStore} state by creating a new {@code INSTANCE}.
     */
    static void reset() {
        INSTANCE = new IndexStore();
    }

    /**
     * Create an index on vertices currently in the database which are of given type and have the
     * given property {@code String} value. If the given type is Type.ANY, an index will be
     * created for all types and the index will remember to index any future types on this property.
     *
     * @param typeKey The type of vertices to index (possibly Type.ANY).
     * @param propertyKey The property to index on as a {@code Short}.
     */
    public void createIndex(Short typeKey, Short propertyKey) {
        indexedPropertiesAndTypes.putIfAbsent(propertyKey, new HashSet<>());
        Set<Short> currentIndexedTypes = indexedPropertiesAndTypes.get(propertyKey);
        validateIndex(currentIndexedTypes, typeKey, propertyKey);

        // If we're indexing on all types, the indexedPropertiesAndTypes should not maintain a list
        // of all types in the graph, just store that an index is being maintained on all types.
        if (TypeAndPropertyKeyStore.ANY == typeKey) {
            currentIndexedTypes.clear();
        }
        currentIndexedTypes.add(typeKey);

        if (TypeAndPropertyKeyStore.ANY == typeKey) {
            short typeCount = TypeAndPropertyKeyStore.getInstance().getTypeKeyCount();
            for (short type = 1; type <= typeCount; type += 1) {
                Integer typeAndPropertyToIndex = getTypePropertyIndexKey(type, propertyKey);
                indices.putIfAbsent(typeAndPropertyToIndex, new HashMap<>());
            }
        } else {
            Integer typeAndPropertyToIndex = getTypePropertyIndexKey(typeKey, propertyKey);
            indices.put(typeAndPropertyToIndex, new HashMap<>());
        }

        for (int vertexId = 0; vertexId <= Graph.getInstance().getHighestVertexId(); vertexId++) {
            short vertexType = Graph.getInstance().getVertexTypes().get(vertexId);
            Integer indexKey = getTypePropertyIndexKey(vertexType, propertyKey);
            if (TypeAndPropertyKeyStore.ANY != typeKey && vertexType != typeKey) {
                continue;
            }
            validatePropertyType(propertyKey);
            String propertyValue = (String) VertexPropertyStore.getInstance().getProperty(vertexId,
                propertyKey);
            updatedIndexWithVertex(vertexId, indexKey, propertyValue, true);
        }
    }

    /**
     * Return whether or not a type-property pair is indexed.
     *
     * @param type The type to query.
     * @param property The property to query.
     * @return Whether or not the type-property pair is indexed.
     */
    public boolean isIndexed(short type, short property) {
        Set<Short> typesIndexedForProperty = indexedPropertiesAndTypes.get(property);
        if (null == typesIndexedForProperty) {
            return false;
        }
        return typesIndexedForProperty.contains(TypeAndPropertyKeyStore.ANY) ||
            typesIndexedForProperty.contains(type);
    }

    /**
     * Get the vertices that match a certain index query that have an indexed property that
     * matches the given {@code String} value.
     * If there is no type specified, it will return the vertices with that property, if that
     * property was indexed for all types.
     *
     * @param startVertexType The type that is being queried. Can be Type.ANY.
     * @param property The property that is being queried.
     * @param value The value of the property to match.
     * @return The vertices of that type with the specified value in the specified property.
     */
    Set<Integer> getVertices(short startVertexType, short property, String value) {
        if (TypeAndPropertyKeyStore.ANY != startVertexType) {
            return getVerticesGivenTypeIsNotANY(startVertexType, property, value);
        }

        Set<Integer> vertices = new HashSet<>();
        short numberOfTypes = TypeAndPropertyKeyStore.getInstance().getTypeKeyCount();
        for (short type = 1; type <= numberOfTypes; type += 1) {
            vertices.addAll(getVerticesGivenTypeIsNotANY(type, property, value));
        }
        return vertices;
    }

    /**
     * Get the list of vertices that are indexed for a given property and type and match the
     * given property {@code String} value.
     *
     * @param type The type of the vertex to search for. Guaranteed to _not_ be Type.ANY.
     * @param property The property that is being queried.
     * @param value The value of the property to match.
     * @return The vertices of the given type that have the given value on the indexed property.
     */
    private Set<Integer> getVerticesGivenTypeIsNotANY(short type, short property, String value) {
        Set<Integer> vertices = new HashSet<>();
        int indexKey = getTypePropertyIndexKey(type, property);
        if (indices.containsKey(indexKey)) {
            Map<String, Set<Integer>> tokenToVertexIDsMap = indices.get(indexKey);
            if (tokenToVertexIDsMap.containsKey(value)) {
                vertices.addAll(tokenToVertexIDsMap.get(value));
            }
        }
        return vertices;
    }

    /**
     * Update a vertex's properties and update the index store appropriately. If {@code
     * newVertexProperties} is null, will not update the vertex and acts as a NOOP.
     *
     * @param vertexId The ID of the vertex to update.
     * @param vertexType The type of the vertex that is changing its properties.
     * @param oldVertexProperties The old properties of the vertex to be removed.
     * @param newVertexProperties The new properties of the vertex to be added.
     */
    void updateIndexWithNewVertexProperties(int vertexId, short vertexType,
        Map<Short, Object> oldVertexProperties,
        Map<Short, Pair<DataType, String>> newVertexProperties) {
        if (null == newVertexProperties) {
            return;
        }
        removeVertexFromIndexedPropertyType(vertexId, vertexType, oldVertexProperties);
        indexVertex(vertexId, vertexType, newVertexProperties);
    }

    /**
     * Add a vertex to a current index if it matches an index that is currently being stored.
     *
     * @param vertexId The ID of the vertex to potentially add to indices.
     * @param vertexType The type of the vertex specified by `vertexId`.
     * @param vertexProperties The properties of the vertex specified by `vertexId`.
     */
    private void indexVertex(int vertexId, Short vertexType,
        Map<Short, Pair<DataType, String>> vertexProperties) {
        for (short propertyKey : vertexProperties.keySet()) {
            if (!isPropertyTypeIndexed(vertexType, propertyKey)) {
                continue;
            }

            Integer indexKey = getTypePropertyIndexKey(vertexType, propertyKey);
            indices.putIfAbsent(indexKey, new HashMap<>());

            Pair<DataType, String> property = vertexProperties.get(propertyKey);
            DataType dataType = property.a;
            String propertyValue = property.b;

            validatePropertyType(propertyKey, dataType);
            updatedIndexWithVertex(vertexId, indexKey, propertyValue, true);
        }
    }

    private void updatedIndexWithVertex(int vertexId, Integer indexKey, String propertyValue,
        boolean addIndex) {
        String[] valuesToIndex = tokenizePropertyValue(propertyValue);
        for (String valueToIndex : valuesToIndex) {
            Map<String, Set<Integer>> index = indices.get(indexKey);
            index.putIfAbsent(valueToIndex, new HashSet<>());
            if (addIndex) {
                index.get(valueToIndex).add(vertexId);
            } else {
                index.get(valueToIndex).remove(vertexId);
            }
        }
    }

    /**
     * Update the index knowing the the specified properties were removed from this vertex.
     *
     * @param vertexId The vertex whose properties were deleted.
     * @param vertexType The type of the vertex whose properties were deleted.
     * @param properties The properties removed from the vertex.
     */
    private void removeVertexFromIndexedPropertyType(int vertexId, short vertexType,
        Map<Short, Object> properties) {
        if (null == properties) {
            return;
        }
        for (short propertyKey : properties.keySet()) {
            int indexKey = getTypePropertyIndexKey(vertexType, propertyKey);
            Map<String, Set<Integer>> index = indices.get(indexKey);
            if (null != index) {
                validatePropertyType(propertyKey);
                String propertyValue = (String) properties.get(propertyKey);
                updatedIndexWithVertex(vertexId, indexKey, propertyValue, false);
            }
        }
    }

    /**
     * Checks if a new index is allowed to be created on the given typeKey and propertyKey.
     * Otherwise throws a IndexAlreadyExistsException.
     *
     * @param currentIndexedTypes The set of types that are currently indexed.
     * @param typeKey The type that is going to be indexed.
     * @param propertyKey The property that is going to be indexed.
     */
    private void validateIndex(Set<Short> currentIndexedTypes, Short typeKey, Short propertyKey) {
        if (currentIndexedTypes.contains(TypeAndPropertyKeyStore.ANY)) {
            String propertyString = TypeAndPropertyKeyStore.getInstance().
                mapShortPropertyKeyToString(propertyKey);
            if (TypeAndPropertyKeyStore.ANY == typeKey) {
                throw new IndexAlreadyExistsException("The index on property " + propertyString +
                    " already exists on all types.");
            } else {
                String typeString = TypeAndPropertyKeyStore.getInstance().
                    mapShortToStringType(typeKey);
                throw new IndexAlreadyExistsException("The index on property " + propertyString +
                    " already exists on all types, so cannot create an index on a more specific " +
                    "type " + typeString + ".");
            }
        } else if (currentIndexedTypes.contains(typeKey)) {
            String propertyString = TypeAndPropertyKeyStore.getInstance().
                mapShortPropertyKeyToString(propertyKey);
            String typeString = TypeAndPropertyKeyStore.getInstance().mapShortToStringType(typeKey);
            throw new IndexAlreadyExistsException("An index on property " + propertyString +
                " for type " + typeString + " already exists.");
        }
    }

    private void validatePropertyType(short propertyKey) {
        DataType dataType = TypeAndPropertyKeyStore.getInstance().getPropertyDataType(propertyKey);
        validatePropertyType(propertyKey, dataType);
    }

    private void validatePropertyType(short propertyKey, DataType dataType) {
        if (dataType != DataType.STRING) {
            throw new UnindexableDataTypeException("Property '" + Short.toString(propertyKey) +
                "' is of type '" + dataType.toString() + "'. Expected to index on properties of" +
                "type String only.");
        }
    }

    private String[] tokenizePropertyValue(Object propertyValue) {
        if (null == propertyValue) {
            return new String[]{};
        }
        return ((String) propertyValue).split("\\s+");
    }

    private boolean isPropertyTypeIndexed(short vertexType, short property) {
        Set<Short> indexedTypes = indexedPropertiesAndTypes.get(property);
        if (null == indexedTypes) {
            return false;
        }
        return indexedTypes.contains(TypeAndPropertyKeyStore.ANY) ||
            indexedTypes.contains(vertexType);
    }

    /**
     * Given a {@code Short} type and a {@code Short} property, the key is an {@code int} where the
     * type value is the two most significant bytes and the property value is the least two
     * significant bytes.
     *
     * @param type A {@code Short} specifying the type.
     * @param property A {@code Short} specifying the property.
     * @return The key in the index calculated as described.
     */
    private int getTypePropertyIndexKey(Short type, Short property) {
        return (type << 16) | property;
    }

    @UsedOnlyByTests
    int numberOfPropertyTypes() {
        int propertyTypeCount = 0;
        for (Short type : indexedPropertiesAndTypes.keySet()) {
            propertyTypeCount += indexedPropertiesAndTypes.get(type).size();
        }
        return propertyTypeCount;
    }

    @Override
    public void serializeAll(String outputDirectoryPath) throws IOException {
        MainFileSerDeHelper.serialize(this, outputDirectoryPath);
    }

    @Override
    public void deserializeAll(String inputDirectoryPath) throws IOException,
            ClassNotFoundException {
        MainFileSerDeHelper.deserialize(this, inputDirectoryPath);
    }

    @Override
    public void serializeMainFile(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeObject(indices);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void deserializeMainFile(ObjectInputStream objectInputStream) throws IOException,
            ClassNotFoundException {
        try {
            indices = (Map<Integer, Map<String, Set<Integer>>>) objectInputStream.readObject();
            createIndexedPropertiesAndTypesFromIndices();
        } catch (ClassCastException exc) {
            throw new SerializationDeserializationException("Index store data type does not match" +
                " current index store data type.");
        }
    }

    private void createIndexedPropertiesAndTypesFromIndices() {
        indexedPropertiesAndTypes = new HashMap<>();
        for (Integer indexKey : indices.keySet()) {
            short property = (short) (indexKey >> 16);
            short temp = (short) (property << 16);
            short type = (short) (indexKey - temp);
            Set<Short> properties = indexedPropertiesAndTypes.getOrDefault(type, new HashSet<>());
            properties.add(property);
            indexedPropertiesAndTypes.put(type, properties);
        }
        int numberOfTypes = TypeAndPropertyKeyStore.getInstance().getTypeKeyCount();
        for (Short property : indexedPropertiesAndTypes.keySet()) {
            Set<Short> anySet = new HashSet<Short>() {{ add(TypeAndPropertyKeyStore.ANY); }};
            if (indexedPropertiesAndTypes.get(property).size() == numberOfTypes) {
                indexedPropertiesAndTypes.put(property, anySet);
            }
        }
    }

    @Override
    public String getMainFileNamePrefix() {
        return SERDE_FILE_NAME_PREFIX;
    }
}
