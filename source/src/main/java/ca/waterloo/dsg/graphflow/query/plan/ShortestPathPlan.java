package ca.waterloo.dsg.graphflow.query.plan;

import ca.waterloo.dsg.graphflow.exception.NoSuchVertexIDException;
import ca.waterloo.dsg.graphflow.query.executor.ShortestPathExecutor;
import ca.waterloo.dsg.graphflow.query.operator.sink.InMemoryOutputSink;
import ca.waterloo.dsg.graphflow.query.result.Message;
import ca.waterloo.dsg.graphflow.query.result.AbstractQueryResult;

/**
 * Represents the execution plan for a shortest path query.
 */
public class ShortestPathPlan extends QueryPlan {

    private int source = -1;
    private int destination = -1;

    public ShortestPathPlan(int source, int destination) {
        this.source = source;
        this.destination = destination;
    }

    /**
     * Executes the {@link ShortestPathPlan}.
     */
    @Override
    public AbstractQueryResult execute() {
        InMemoryOutputSink queryResultSink = new InMemoryOutputSink();
        try {
            ShortestPathExecutor.getInstance().execute(source, destination, queryResultSink);
        } catch (NoSuchVertexIDException e) {
            return new Message(e.getMessage(), true /* isError */);
        }
        return new Message(queryResultSink.toString());
    }
}
