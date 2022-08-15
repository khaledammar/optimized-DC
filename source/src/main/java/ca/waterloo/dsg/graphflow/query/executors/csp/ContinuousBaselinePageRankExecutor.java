package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.query.plans.ContinuousDiffBFSShortestPathPlan;
import ca.waterloo.dsg.graphflow.query.plans.ContinuousShortestPathPlan;
import ca.waterloo.dsg.graphflow.util.Report;

import java.util.ArrayList;
import java.util.List;


public class ContinuousBaselinePageRankExecutor {
    private static final ContinuousBaselinePageRankExecutor INSTANCE = new ContinuousBaselinePageRankExecutor();
    private List<ContinuousShortestPathPlan> continuousShortestPathPlans = new ArrayList<>();

    /**
     * Empty private constructor enforces usage of the singleton object {@link #INSTANCE} for this
     * class.
     */
    private ContinuousBaselinePageRankExecutor() {
    }

    /**
     * Returns the singleton instance {@link #INSTANCE} of {@link ContinuousShortestPathsExecutor}.
     */
    public static ContinuousBaselinePageRankExecutor getInstance() {
        return INSTANCE;
    }

    /**
     * adds a new {@link ContinuousDiffBFSShortestPathPlan} to the list of plans
     *
     * @param plan the new {@link ContinuousDiffBFSShortestPathPlan}
     */
    public void addShortestPathPlan(ContinuousDiffBFSShortestPathPlan plan) {
        this.continuousShortestPathPlans.add(plan);
    }

    /**
     * removes all current continuous shortest path queries
     */
    public void reset() {
        this.continuousShortestPathPlans.clear();
    }

    /**
     * Executes all the registered {@link ContinuousDiffBFSShortestPathPlan}s.
     */
    public void execute() {
        Report.INSTANCE.debug(" ** Execute  ContinuousBaselineShortestPathExecutor all queries for batch ###  **");

        ContinuousShortestPathPlan.newBatch = true;
        continuousShortestPathPlans.forEach(plan -> plan.execute());
    }
}
