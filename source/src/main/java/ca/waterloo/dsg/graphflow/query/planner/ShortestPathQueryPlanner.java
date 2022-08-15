package ca.waterloo.dsg.graphflow.query.planner;

import ca.waterloo.dsg.graphflow.query.plan.QueryPlan;
import ca.waterloo.dsg.graphflow.query.plan.ShortestPathPlan;
import ca.waterloo.dsg.graphflow.query.structuredquery.QueryRelation;
import ca.waterloo.dsg.graphflow.query.structuredquery.StructuredQuery;

/**
 * Creates the execution plan for a shortest path query.
 */
public class ShortestPathQueryPlanner extends AbstractQueryPlanner {

    public ShortestPathQueryPlanner(StructuredQuery structuredQuery) {
        super(structuredQuery);
    }

    @Override
    public QueryPlan plan() {
        QueryRelation shortestPathEdge = structuredQuery.getQueryRelations().get(0);
        return new ShortestPathPlan(
            Integer.parseInt(shortestPathEdge.getFromQueryVariable().getVariableName()),
            Integer.parseInt(shortestPathEdge.getToQueryVariable().getVariableName()));
    }
}
