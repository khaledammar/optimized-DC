package ca.waterloo.dsg.graphflow.query.planner;

import ca.waterloo.dsg.graphflow.exception.SerializationDeserializationException;
import ca.waterloo.dsg.graphflow.graph.GraphDBState;
import ca.waterloo.dsg.graphflow.query.plan.QueryPlan;
import ca.waterloo.dsg.graphflow.query.result.Message;
import ca.waterloo.dsg.graphflow.query.result.AbstractQueryResult;
import ca.waterloo.dsg.graphflow.query.structuredquery.StructuredQuery;

/**
 * Creates a {@code QueryPlan} for the SAVE GRAPH operation.
 */
public class SaveGraphToDirQueryPlanner extends AbstractQueryPlanner {

    public SaveGraphToDirQueryPlanner(StructuredQuery structuredQuery) {
        super(structuredQuery);
    }

    @Override
    public QueryPlan plan() {
        return new QueryPlan() {
            @Override
            public AbstractQueryResult execute() {
                try {
                    GraphDBState.serialize(structuredQuery.getFilePath());
                    return new Message(String.format("Graph saved to directory '%s'.",
                        structuredQuery.getFilePath()));
                } catch (SerializationDeserializationException e) {
                    return new Message(String.format("Error saving graph state to '%s'. " +
                            "Please check the Graphflow server logs for details.",
                        structuredQuery.getFilePath()), true /* isError */);
                }
            }
        };
    }
}
