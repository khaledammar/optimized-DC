package ca.waterloo.dsg.graphflow.exception;

import ca.waterloo.dsg.graphflow.query.plan.ContinuousMatchQueryPlan;

/**
 * Thrown to indicate that the UDF for the {@link ContinuousMatchQueryPlan} was not loaded.
 */
public class LoadUDFException extends GraphflowException {

    public LoadUDFException(String message) {
        super(message);
    }
}
