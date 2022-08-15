package ca.waterloo.dsg.graphflow.exception;

/**
 * Thrown to indicate that the WHERE clause contains syntax and/or semantic errors.
 */
public class MalformedWhereClauseException extends GraphflowException {

    public MalformedWhereClauseException(String message) {
        super(message);
    }
}