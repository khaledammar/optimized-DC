package ca.waterloo.dsg.graphflow.query.planner;

import ca.waterloo.dsg.graphflow.exception.SerializationDeserializationException;
import ca.waterloo.dsg.graphflow.graph.GraphDBState;
import ca.waterloo.dsg.graphflow.query.plan.QueryPlan;
import ca.waterloo.dsg.graphflow.query.result.Message;
import ca.waterloo.dsg.graphflow.query.result.AbstractQueryResult;
import ca.waterloo.dsg.graphflow.query.structuredquery.StructuredQuery;

/**
 * Creates a {@code QueryPlan} for the LOAD GRAPH operation.
 */
public class LoadGraphFromDirQueryPlanner extends AbstractQueryPlanner {

    public LoadGraphFromDirQueryPlanner(StructuredQuery structuredQuery) {
        super(structuredQuery);
    }

    @Override
    public QueryPlan plan() {
        return new QueryPlan() {
            @Override
            public AbstractQueryResult execute() {
                try {
                    GraphDBState.deserialize(structuredQuery.getFilePath());
                    return new Message(String.format("Graph loaded from directory '%s'.",
                        structuredQuery.getFilePath()));
                } catch (SerializationDeserializationException e) {
                    return new Message(String.format("Error loading graph state from '%s'. " +
                            "Please check the Graphflow server logs for details.",
                        structuredQuery.getFilePath()), true /* isError */);
                }
            }
        };
    }
}
