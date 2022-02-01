package ca.waterloo.dsg.graphflow.query.plans;

import ca.waterloo.dsg.graphflow.ExecutorType;
import ca.waterloo.dsg.graphflow.query.executors.ShortestPathExecutor;
import ca.waterloo.dsg.graphflow.query.executors.csp.DijkstraBaseline;
import ca.waterloo.dsg.graphflow.query.executors.csp.OptimizedDijkstraBaseline_1;
import ca.waterloo.dsg.graphflow.query.executors.csp.WeightedBaselineBFS;
import ca.waterloo.dsg.graphflow.query.operator.AbstractDBOperator;
import ca.waterloo.dsg.graphflow.util.Report;

/**
 * Created by zaidshennar on 2017-06-07.
 */
public class ContinuousBaselineShortestPathPlan extends ContinuousShortestPathPlan {

    ExecutorType executorType;
    boolean backtrack;

    private long distance = Long.MAX_VALUE;

    public ContinuousBaselineShortestPathPlan(int queryId, int source, int destination, AbstractDBOperator outputSink,
                                              ExecutorType executorType, boolean backtrack) {
        super(queryId, source, destination, outputSink);
        this.executorType = executorType;
        this.backtrack = backtrack;
        ShortestPathExecutor.backtrack = backtrack;
        DijkstraBaseline.backtrack = backtrack;
    }


    /**
     * Executes the Unidirectional Baseline-BFS
     */
    public void execute() {

        // Run queries!
        long startTime = System.nanoTime();

        /**
         * Undirected Un-weighted Baseline
         */
        if (executorType == ExecutorType.UNW_BASELINE) {
            distance = ShortestPathExecutor.getInstance().execute(source, destination, outputSink);
        } else if (executorType == ExecutorType.KHOP_BASELINE) {
            distance = ShortestPathExecutor.getInstance().execute_Khop(source, destination, outputSink);
        } else if (executorType == ExecutorType.Q1_BASELINE) {
            distance = ShortestPathExecutor.getInstance().execute_Q1_knows(source, outputSink);
        } else if (executorType == ExecutorType.Q2_BASELINE) {
            distance = ShortestPathExecutor.getInstance().execute_Q2_hasModerator_knows(source, outputSink);
        }
        /**
         * Weighted Baseline
         */
        else if (executorType == ExecutorType.SPSP_W_BASELINE) {
            distance = WeightedBaselineBFS.getInstance().execute(source, destination);

            /**
             * Dijkstra Baseline
             */
        } else if (executorType == ExecutorType.UNW_DIJKSTRA) {
            distance = DijkstraBaseline.getInstance()
                    .execute(source, destination, false); // False means edges are not weighted
        } else if (executorType == ExecutorType.W_DIJKSTRA) {
            distance =
                    DijkstraBaseline.getInstance().execute(source, destination, true); // True means edges are weighted
        } else if (executorType == ExecutorType.OPT_DIJKSTRA) {
            distance = OptimizedDijkstraBaseline_1.getInstance()
                    .execute(source, destination, false); // True means edges are weighted
        } else if (executorType == ExecutorType.NEW_DIJKSTRA) {
            distance = DijkstraBaseline.getInstance()
                    .execute(source, destination, false); // False means edges are not weighted
        } else {
            throw new RuntimeException("Executor not found");
        }

        long endTime = System.nanoTime();

        //Report.INSTANCE.error("Query "+source+"-"+destination+" "+(endTime - startTime));
        if (Report.INSTANCE.appReportingLevel == Report.Level.INFO) {
            queryTime.add(endTime - startTime);
        }

        if (distance == Integer.MAX_VALUE) {
            distance = Long.MAX_VALUE;
        }
    }

    @Override
    public long getSrcDstDistance() {
        return distance;
    }
}
