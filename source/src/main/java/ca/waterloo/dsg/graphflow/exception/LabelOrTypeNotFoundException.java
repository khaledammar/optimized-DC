package ca.waterloo.dsg.graphflow.exception;

/**
 * Thrown to indicate that the label for the vertices or the type for the edges was not found
 * while executing a Load Data from CSV plan.
 */
public class LabelOrTypeNotFoundException extends GraphflowException {

    public LabelOrTypeNotFoundException(String message) {
        super(message);
    }
}
