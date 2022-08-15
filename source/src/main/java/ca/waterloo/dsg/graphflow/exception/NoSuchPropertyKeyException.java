package ca.waterloo.dsg.graphflow.exception;

import ca.waterloo.dsg.graphflow.graph.TypeAndPropertyKeyStore;

/**
 * Thrown to indicate a property key does not exist in the {@link TypeAndPropertyKeyStore}.
 */
public class NoSuchPropertyKeyException extends GraphflowException {

    /**
     * Constructor that takes the key not found and sets a standard error message.
     *
     * @param stringKey A property key not found in the database.
     */
    public NoSuchPropertyKeyException(String stringKey) {
        super("String property key " + stringKey + " is not found in the database.");
    }

    /**
     * Constructor that takes the key not found and sets a standard error message.
     *
     * @param shortKey A property key not found in the database.
     */
    public NoSuchPropertyKeyException(short shortKey) {
        super("Short property key " + shortKey + " is not found in the database.");
    }
}
