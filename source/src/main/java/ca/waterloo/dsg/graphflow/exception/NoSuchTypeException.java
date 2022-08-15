package ca.waterloo.dsg.graphflow.exception;

import ca.waterloo.dsg.graphflow.graph.TypeAndPropertyKeyStore;

/**
 * Thrown to indicate a type does not exist in the {@link TypeAndPropertyKeyStore}.
 */
public class NoSuchTypeException extends GraphflowException {

    public NoSuchTypeException(String message) {
        super(message);
    }
}
