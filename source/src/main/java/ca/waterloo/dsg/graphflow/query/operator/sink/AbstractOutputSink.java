package ca.waterloo.dsg.graphflow.query.operator.sink;

import ca.waterloo.dsg.graphflow.query.operator.AbstractOperator;
import ca.waterloo.dsg.graphflow.query.result.AbstractQueryResult;

/**
 * This operator encapsulates common functionality between the {@link InMemoryOutputSink},
 * {@link FileOutputSink}, and {@link UDFOutputSink} operators.
 */
public abstract class AbstractOutputSink extends AbstractOperator {

    AbstractOutputSink() {
        super(null); /* an output sink is always the last operator */
    }

    /**
     * Sets the {@link AbstractQueryResult} representing the complete output.
     *
     * @param queryResult The {@link AbstractQueryResult} output.
     */
    public abstract void append(AbstractQueryResult queryResult);
}
