package ca.waterloo.dsg.graphflow.query.plan;

import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.TypeAndPropertyKeyStore;
import ca.waterloo.dsg.graphflow.query.executor.ContinuousMatchQueryExecutor;
import ca.waterloo.dsg.graphflow.query.result.Message;
import ca.waterloo.dsg.graphflow.query.result.AbstractQueryResult;
import ca.waterloo.dsg.graphflow.query.structuredquery.QueryRelation;
import ca.waterloo.dsg.graphflow.query.structuredquery.StructuredQuery;

/**
 * Class representing plan for a DELETE operation.
 */
public class DeleteQueryPlan extends QueryPlan {

    private StructuredQuery structuredQuery;

    public DeleteQueryPlan(StructuredQuery structuredQuery) {
        this.structuredQuery = structuredQuery;
    }

    /**
     * Executes the {@link DeleteQueryPlan}.
     */
    @Override
    public AbstractQueryResult execute() {
        for (QueryRelation queryRelation : structuredQuery.getQueryRelations()) {
            TypeAndPropertyKeyStore.getInstance().mapStringTypeToShortAndAssertTypeExists(
                queryRelation.getRelationType());
            Graph.getInstance().deleteEdgeTemporarily(
                Integer.parseInt(queryRelation.getFromQueryVariable().getVariableName()),
                Integer.parseInt(queryRelation.getToQueryVariable().getVariableName()),
                TypeAndPropertyKeyStore.getInstance().mapStringTypeToShort(queryRelation.
                    getRelationType()));
        }
        ContinuousMatchQueryExecutor.getInstance().execute();
        Graph.getInstance().finalizeChanges();
        // TODO(amine): bug, count the actual num of edges deleted to append to sink.
        return new Message(structuredQuery.getQueryRelations().size() + " edges deleted.");
    }
}
