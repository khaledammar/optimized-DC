package ca.waterloo.dsg.graphflow.exception;

/**
 * Thrown to indicate that the RETURN clause contains syntax and/or semantic errors.
 */
public class MalformedReturnClauseException extends GraphflowException {

    public MalformedReturnClauseException(String message) {
        super(message);
    }
}
