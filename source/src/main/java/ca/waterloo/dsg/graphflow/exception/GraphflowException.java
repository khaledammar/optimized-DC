package ca.waterloo.dsg.graphflow.exception;

/**
 * The base class for all Graphflow exceptions.
 */
public class GraphflowException extends RuntimeException {

    public GraphflowException(String message) {
        super(message);
    }
}
