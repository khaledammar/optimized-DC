package ca.waterloo.dsg.graphflow.exception;

/**
 * Thrown to indicate that the MATCH statement is not well formed.
 */
public class MalformedMatchQueryException extends GraphflowException {

    public MalformedMatchQueryException(String message) {
        super(message);
    }
}

