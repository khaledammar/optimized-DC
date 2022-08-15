package ca.waterloo.dsg.graphflow.exception;

/**
 * Thrown to indicate that an unindexable data type is trying to be indexed.
 */
public class UnindexableDataTypeException extends GraphflowException {

    public UnindexableDataTypeException(String message) {
        super(message);
    }
}
