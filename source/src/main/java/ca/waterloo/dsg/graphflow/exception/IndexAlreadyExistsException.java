package ca.waterloo.dsg.graphflow.exception;

/**
 * Thrown to indicate that the index that is trying to be created already exists.
 */
public class IndexAlreadyExistsException extends GraphflowException {

    public IndexAlreadyExistsException(String message) {
        super(message);
    }
}
