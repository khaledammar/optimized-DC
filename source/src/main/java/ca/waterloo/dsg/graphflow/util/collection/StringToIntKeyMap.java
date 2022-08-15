package ca.waterloo.dsg.graphflow.util.collection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores a mapping of {@code String} keys to {@code int} keys. Each new {@code String} key
 * inserted gets a consecutively increasing integer key starting from 0.
 */
public class StringToIntKeyMap {

    protected Map<String, Integer> stringToIntMap = new HashMap<>();
    protected int nextKeyAsInt = 1;

    /**
     * @param key The {@code String} key.
     *
     * @return The {@code Integer} mapping of the given {@code String} key or {@code null} if the
     * {@code key} is not in the map.
     *
     * @throws IllegalArgumentException if {@code key} passed is {@code null}.
     */
    public Integer mapStringKeyToInt(String key) {
        if (null == key) {
            throw new IllegalArgumentException("The key parameter passed is null.");
        }
        return stringToIntMap.get(key);
    }

    /**
     * @param stringKey The {@code String} key.
     *
     * @return The {@code int} mapping of the given {@code String} key.
     *
     * @throws IllegalArgumentException if {@code stringKey} passed is {@code null}.
     */
    public int getKeyAsIntOrInsert(String stringKey) {
        if (null == stringKey) {
            throw new IllegalArgumentException("The stringKey parameter passed is null.");
        }
        Integer intKey = stringToIntMap.get(stringKey);
        if (null == intKey) {
            adjustOtherDataStructures(stringKey, nextKeyAsInt);
            stringToIntMap.put(stringKey, nextKeyAsInt);
            return nextKeyAsInt++;
        }
        return intKey;
    }

    /**
     * Adjusts other data structures that classes extending {@link StringToIntKeyMap} might have.
     * This is called when a new key is being inserted to the map.
     * Note: Should be implemented by classes extending {@link StringToIntKeyMap}.
     *
     * @param newStringKey new String key being inserted.
     * @param newIntKey the new integer key that corresponds to the newStringKey.
     */
    protected void adjustOtherDataStructures(String newStringKey, int newIntKey) { }

    public int size() {
        return stringToIntMap.size();
    }

    /**
     * @return the set of String key and int value entries in the map.
     */
    public List<String> getSortedKeys() {
        List<String> sortedKeys = new ArrayList<>(stringToIntMap.keySet());
        Collections.sort(sortedKeys);
        return sortedKeys;
    }

    /**
     * Serializes data to the given {@link ObjectOutputStream}.
     *
     * @param objectOutputStream The {@link ObjectOutputStream} to write serialized data to.
     */
    public void serialize(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeInt(nextKeyAsInt);
        objectOutputStream.writeObject(stringToIntMap);
    }

    /**
     * Deserializes data from the given {@link ObjectInputStream}.
     *
     * @param objectInputStream The {@link ObjectInputStream} to read serialized data from.
     */
    @SuppressWarnings("unchecked") // Ignore {@code HashMap<String, Integer>} cast warnings.
    public void deserialize(ObjectInputStream objectInputStream) throws IOException,
        ClassNotFoundException {
        nextKeyAsInt = objectInputStream.readInt();
        stringToIntMap = (HashMap<String, Integer>) objectInputStream.readObject();
    }
}
