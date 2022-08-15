package ca.waterloo.dsg.graphflow.exception;

/**
 * Thrown to indicate that a vertexID does not exist in the system.
 */
public class NoSuchVertexIDException extends GraphflowException {

    public NoSuchVertexIDException(String message) {
        super(message);
    }
}
