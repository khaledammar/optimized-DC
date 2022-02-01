package ca.waterloo.dsg.graphflow.util;

import java.util.Arrays;
import java.util.Objects;

/**
 * A list of int primitives implemented using an array.
 */
public class IntArrayList {

    private static final int INITIAL_CAPACITY = 2;
    private int[] data;
    private int size = 0;

    /**
     * Creates {@link IntArrayList} with default capacity.
     */
    public IntArrayList() {
        data = new int[INITIAL_CAPACITY];
    }

    /**
     * Creates {@link IntArrayList} with the given {@code capacity}.
     *
     * @param capacity The initial capacity of the array underlying the {@link IntArrayList}.
     */
    public IntArrayList(int capacity) {
        data = new int[capacity];
    }

    /**
     * Adds a new integer to the list.
     *
     * @param element The new integer to be added.
     */
    public void add(int element) {
        data = ArrayUtils.resizeIfNecessary(data, size + 1);
        data[size++] = element;
    }

    /**
     * Appends the given array of integers to the list.
     *
     * @param elements The array of integers to be added.
     */
    public void addAll(int[] elements) {
        data = ArrayUtils.resizeIfNecessary(data, size + elements.length);
        System.arraycopy(elements, 0, data, size, elements.length);
        size += elements.length;
    }

    /**
     * Returns the value at the specified index.
     *
     * @param index The index in the underlying array of the element to be returned.
     * @return int The value at index {@code index}.
     * @throws ArrayIndexOutOfBoundsException Exception thrown when {@code index} is larger than
     *                                        the size of the collection.
     */
    public int get(int index) {
        if (index >= size) {
            throw new ArrayIndexOutOfBoundsException("No element at index " + index);
        }
        return data[index];
    }

    /**
     * Returns true if {@code data} has the value {@code element}
     *
     * @param element the value to be tested
     * @return true or false based on the existence of the value
     */
    public boolean contains(int element) {
        for (int i = 0; i < this.size(); i++) {
            if (this.data[i] == element) {
                return true;
            }
        }
        return false;
    }

    public void removeIfExists(int element) {
        int[] temp = new int[this.data.length];
        int currIndex = 0;
        for (int i = 0; i < size(); i++) {
            if (this.data[i] == element) {
                this.size--;
                continue;
            }
            temp[currIndex] = this.data[i];
            currIndex++;
        }
        this.data = temp;
    }

    /**
     * clears the list
     */
    public void clear() {
        this.size = 0;
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public int size() {
        return size;
    }

    public int[] toArray() {
        return Arrays.copyOf(data, size);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IntArrayList that = (IntArrayList) o;
        if (that.size != this.size) {
            return false;
        }
        for (int i = 0; i < that.data.length; i++) {
            if (!this.contains(that.data[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(size);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int eltIdx = 0; eltIdx < size(); eltIdx++) {
            sb.append(data[eltIdx]);
            if (eltIdx != size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
