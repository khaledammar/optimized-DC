package ca.waterloo.dsg.graphflow.util.collection;

import ca.waterloo.dsg.graphflow.util.annotation.VisibleForTesting;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

/**
 * A list of int primitives implemented using an array.
 */
public class ShortArrayList {

    private static final int INITIAL_CAPACITY = 2;
    private short[] data;
    private int size = 0;

    /**
     * Creates {@link ShortArrayList} with default capacity.
     */
    public ShortArrayList() {
        data = new short[INITIAL_CAPACITY];
    }

    /**
     * Adds a new short to the {@link ShortArrayList}.
     *
     * @param element The new short to be added to the collection.
     */
    public void add(short element) {
        data = ArrayUtils.resizeIfNecessary(data, size + 1);
        data[size++] = element;
    }

    /**
     * Sets the given value at the given {@code index}, resizing the underlying array if necessary
     * . If the
     * index is greater than the current array capacity, all values between the current array
     * size and {@code index} will be initialized to zero.
     *
     * @param index Index in the underlying array to be be updated with {@code newItem}.
     * @param newItem The value to be placed at {@code index}.
     */
    public void set(int index, short newItem) {
        if (index >= data.length) {
            data = ArrayUtils.resizeIfNecessary(data, index + 1); // Index is zero based.
        }
        data[index] = newItem;
        if (index >= size) {
            size = index + 1;
        }
    }

    /**
     * Returns the value at the specified index.
     *
     * @param index The index in the underlying array of the element to be returned.
     *
     * @return short The value at {@code index}.
     *
     * @throws ArrayIndexOutOfBoundsException Throws exception if index is greater than the size of
     * the {@link ShortArrayList} collection.
     */
    public short get(int index) {
        if (index >= size) {
            throw new ArrayIndexOutOfBoundsException("No element at index " + index);
        }
        return data[index];
    }

    public int getSize() {
        return size;
    }

    @VisibleForTesting
    short[] toArray() {
        return Arrays.copyOf(data, size);
    }

    /**
     * Empties data array by initializing it to a new {@code short[]} with
     * {@link ShortArrayList#INITIAL_CAPACITY} and sets its size to 0.
     */
    public void reset() {
        data = new short[INITIAL_CAPACITY];
        size = 0;
    }

    /**
     * Sets the size of the collection to zero.
     */
    public void clear() {
        size = 0;
    }

    /**
     * Serializes data to the given {@link ObjectOutputStream}.
     *
     * @param objectOutputStream The {@link ObjectOutputStream} to write serialized data to.
     */
    public void serialize(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeInt(size);
        objectOutputStream.writeObject(data);
    }

    /**
     * Deserializes data from the given {@link ObjectInputStream}.
     *
     * @param objectInputStream The {@link ObjectInputStream} to read serialized data from.
     */
    public void deserialize(ObjectInputStream objectInputStream) throws IOException,
        ClassNotFoundException {
        size = objectInputStream.readInt();
        data = (short[]) objectInputStream.readObject();
    }
}
