package ca.waterloo.dsg.graphflow.graph;

import ca.waterloo.dsg.graphflow.graph.serde.GraphflowSerializable;
import ca.waterloo.dsg.graphflow.util.datatype.DataType;
import org.antlr.v4.runtime.misc.Pair;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Abstract property store class for the {@link EdgeStore} and {@link VertexPropertyStore} classes.
 */
abstract class PropertyStore implements GraphflowSerializable {

    protected PropertyIterator propertyIterator = new PropertyIterator();

    @Override
    public void serializeAll(String outputDirectoryPath) throws IOException, InterruptedException {
        // Clean up property iterator when serializing because it does not need to be persisted.
        propertyIterator.reset(null /* data byte array */, 0 /* start index */, 0 /* end index */);
    }

    protected byte[] serializeProperties(Map<Short, Pair<DataType, String>> properties) {
        byte[] propertiesAsBytes = new byte[0];
        if (null != properties && !properties.isEmpty()) {
            int index = 0;
            int propertiesLength = 0;
            byte[][] keyValueByteArrays = new byte[properties.size()][];
            for (Short key : properties.keySet()) {
                keyValueByteArrays[index] = DataType.serialize(properties.get(key).a, key,
                    properties.get(key).b);
                propertiesLength += keyValueByteArrays[index].length;
                index++;
            }

            propertiesAsBytes = new byte[propertiesLength];
            propertiesLength = 0;
            for (byte[] keyValueAsBytes : keyValueByteArrays) {
                System.arraycopy(keyValueAsBytes, 0, propertiesAsBytes, propertiesLength,
                    keyValueAsBytes.length);
                propertiesLength += keyValueAsBytes.length;
            }
        }
        return propertiesAsBytes;
    }

    protected Object getPropertyFromIterator(short key) {
        Pair<Short, Object> keyValue;
        while (propertyIterator.hasNext()) {
            keyValue = propertyIterator.next();
            if (key == keyValue.a) {
                return keyValue.b;
            }
        }
        return null;
    }

    /**
     * An iterator to iterate over a set of properties that are serialized as a byte array.
     * Classes that use this iterator should create an instance of this iterator and then call the
     * {@link #reset(byte[], int, int)} method to use it again without constructing.
     */
    protected static class PropertyIterator implements Iterator<Pair<Short, Object>> {

        private byte[] data;
        private int endIndex;
        private int currentIndex;

        /**
         * Resets the iterator.
         *
         * @param data byte array containing the properties.
         * @param startIndex start index of properties in {@link #data}.
         * @param endIndex end index of properties in {@link #data}.
         */
        protected void reset(byte[] data, int startIndex, int endIndex) {
            this.data = data;
            this.currentIndex = startIndex;
            this.endIndex = endIndex;
        }

        @Override
        public boolean hasNext() {
            return currentIndex < endIndex;
        }

        @Override
        public Pair<Short, Object> next() {
            if (!hasNext()) {
                throw new NoSuchElementException("PropertyIterator has no more elements.");
            }
            short key = (short) (((((int) data[currentIndex]) << 8) & 0x0000FF00) |
                (data[currentIndex + 1] & 0x000000FF));
            DataType dataType = TypeAndPropertyKeyStore.getInstance().getPropertyDataType(key);

            int length;
            int valueOffset;
            if (DataType.STRING == dataType) {
                length = DataType.deserializeInteger(data, currentIndex + 2);
                // 2 bytes for short key + 4 for an int storing the length of
                // the String.
                valueOffset = 6;
            } else {
                length = DataType.getLength(dataType);
                // 2 bytes for short key. We do not store the lengths of data
                // types other than
                // Strings since they are fixed.
                valueOffset = 2;
            }

            Object value = DataType.deserialize(dataType, data, currentIndex + valueOffset, length);
            currentIndex += (valueOffset + length);
            return new Pair<>(key, value);
        }
    }
}
