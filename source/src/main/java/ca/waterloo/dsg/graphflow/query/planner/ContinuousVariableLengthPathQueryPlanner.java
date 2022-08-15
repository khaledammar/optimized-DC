package ca.waterloo.dsg.graphflow.query.planner;

import ca.waterloo.dsg.graphflow.query.executors.csp.NewUnidirectionalDifferentialBFS;
import ca.waterloo.dsg.graphflow.query.plans.ContinuousVariableLengthPathPlan;
import ca.waterloo.dsg.graphflow.query.plans.QueryPlan;

import java.io.IOException;

/**
 * Query planner for continuous variable length path queries.
 * <p>
 * Warning: Currently can only be used by tests.
 */
public class ContinuousVariableLengthPathQueryPlanner extends AbstractQueryPlanner {

    /**
     * Main constructor.
     */
    public ContinuousVariableLengthPathQueryPlanner() throws IOException {
        super(null /* no structured query */);
    }

    @Override
    QueryPlan plan() {
        throw new UnsupportedOperationException("For DiffBFS experiments, we should not be" +
                " calling this plan() method. Instead, we should be calling " +
                " ContinuousVariableLengthPathQueryPlanner.plan().");
    }

    public QueryPlan plan(int queryId, int source, int destination, int minLength, int maxLength, boolean backtrack, NewUnidirectionalDifferentialBFS.Queries queryType) {
        return new ContinuousVariableLengthPathPlan(queryId, source, destination, minLength, maxLength, backtrack, queryType);
    }
}
