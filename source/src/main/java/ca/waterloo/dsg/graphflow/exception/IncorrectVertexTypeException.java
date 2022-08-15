package ca.waterloo.dsg.graphflow.exception;

/**
 * Thrown to indicate that there is an inconsistent or incorrect use of a vertex type.
 */
public class IncorrectVertexTypeException extends GraphflowException {

    public IncorrectVertexTypeException(String message) {
        super(message);
    }
}
