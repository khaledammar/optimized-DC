package ca.waterloo.dsg.graphflow.exception;

import ca.waterloo.dsg.graphflow.util.datatype.DataType;

/**
 * Thrown to indicate that there is an inconsistent or incorrect use of a {@link DataType} with a
 * given property key.
 */
public class IncorrectDataTypeException extends GraphflowException {

    public IncorrectDataTypeException(String message) {
        super(message);
    }
}
