package ca.waterloo.dsg.graphflow.query.planner;

import ca.waterloo.dsg.graphflow.query.plan.IndexCreationQueryPlan;
import ca.waterloo.dsg.graphflow.query.plan.QueryPlan;
import ca.waterloo.dsg.graphflow.query.structuredquery.StructuredQuery;

/**
 * Create a {@code QueryPlan} for the CREATE INDEX operation.
 */
public class IndexCreationQueryPlanner extends AbstractQueryPlanner {

    /**
     * @param structuredQuery The index creation structured query.
     */
    public IndexCreationQueryPlanner(StructuredQuery structuredQuery) {
        super(structuredQuery);
    }

    /**
     * @see AbstractQueryPlanner#plan().
     */
    @Override
    public QueryPlan plan() {
        return new IndexCreationQueryPlan(structuredQuery);
    }
}
