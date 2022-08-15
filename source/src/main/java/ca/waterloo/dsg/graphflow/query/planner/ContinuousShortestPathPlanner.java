package ca.waterloo.dsg.graphflow.query.planner;

import ca.waterloo.dsg.graphflow.ExecutorType;
import ca.waterloo.dsg.graphflow.query.executors.csp.DistancesWithDropBloom;
import ca.waterloo.dsg.graphflow.query.operator.AbstractDBOperator;
import ca.waterloo.dsg.graphflow.query.operator.FileOutputSink;
import ca.waterloo.dsg.graphflow.query.plans.ContinuousBaselineShortestPathPlan;
import ca.waterloo.dsg.graphflow.query.plans.ContinuousDiffBFSShortestPathPlan;
import ca.waterloo.dsg.graphflow.query.plans.QueryPlan;
import ca.waterloo.dsg.graphflow.query.structuredquery.QueryRelation;
import ca.waterloo.dsg.graphflow.query.structuredquery.StructuredQuery;

import java.io.File;
import java.io.IOException;

/**
 * Creates the execution plan for a continuous shortest path query.
 */
public class ContinuousShortestPathPlanner extends AbstractQueryPlanner {

    private AbstractDBOperator outputSink;

    public ContinuousShortestPathPlanner(StructuredQuery structuredQuery) throws IOException {
        super(structuredQuery);
        if (null != structuredQuery.getFilePath()) {
            outputSink = new FileOutputSink(new File(structuredQuery.getFilePath()));
        } else if (null != structuredQuery.getContinuousMatchAction()) {
            //TODO: handle UDF call
        }
    }

    @Override
    public QueryPlan plan() {
        throw new UnsupportedOperationException("For DiffBFS experiments, we should not be" +
                " calling this plan() method. Instead, we should be calling plan(ExecutorType).");
    }

    /**
     * This function set the execution plan for the query
     *
     * @param executorType
     * @param backtrack
     * @return
     */
    public QueryPlan plan(int queryId, ExecutorType executorType, boolean backtrack) {
        QueryRelation shortestPathEdge = structuredQuery.getQueryRelations().get(0);

        /**
         *
         * Two main executor types are used:
         * 1- For UNW_BASELINE, W_BASELINE, DIJKSTRA: --> ContinuousBaselineShortestPathPlan
         * 2- For all others: --> ContinuousDiffBFSShortestPathPlan
         */

        if (ExecutorType.isBaseLine(executorType)) {
            return new ContinuousBaselineShortestPathPlan(queryId,
                    Integer.parseInt(shortestPathEdge.getFromQueryVariable().getVariableName()),
                    Integer.parseInt(shortestPathEdge.getToQueryVariable().getVariableName()), outputSink, executorType,
                    backtrack);
        } else {
            return new ContinuousDiffBFSShortestPathPlan(queryId,
                    Integer.parseInt(shortestPathEdge.getFromQueryVariable().getVariableName()),
                    Integer.parseInt(shortestPathEdge.getToQueryVariable().getVariableName()), outputSink, executorType,
                    backtrack);
        }
    }

    /**
     * This function set the execution plan for the query
     *
     * @param executorType
     * @param backtrack
     * @return
     */
    public QueryPlan plan(int queryId, ExecutorType executorType, boolean backtrack, float dropProbability,
                          DistancesWithDropBloom.DropType dropType, String bloomType, int minimumDegree,
                          int maxDegree, int landmarkNumber) {
        QueryRelation shortestPathEdge = structuredQuery.getQueryRelations().get(0);

        /**
         *
         * Two main executor types are used:
         * 1- For UNW_BASELINE, W_BASELINE, DIJKSTRA: --> ContinuousBaselineShortestPathPlan
         * 2- For all others: --> ContinuousDiffBFSShortestPathPlan
         */

        if (ExecutorType.isBaseLine(executorType)) {
            return new ContinuousBaselineShortestPathPlan(queryId,
                    Integer.parseInt(shortestPathEdge.getFromQueryVariable().getVariableName()),
                    Integer.parseInt(shortestPathEdge.getToQueryVariable().getVariableName()), outputSink, executorType,
                    backtrack, landmarkNumber);
        } else {
            return new ContinuousDiffBFSShortestPathPlan(queryId,
                    Integer.parseInt(shortestPathEdge.getFromQueryVariable().getVariableName()),
                    Integer.parseInt(shortestPathEdge.getToQueryVariable().getVariableName()), outputSink, executorType,
                    backtrack, dropProbability, dropType, bloomType, minimumDegree, maxDegree, landmarkNumber);
        }
    }
}
