package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.query.plans.ContinuousDiffBFSShortestPathPlan;
import ca.waterloo.dsg.graphflow.query.plans.ContinuousShortestPathPlan;
import ca.waterloo.dsg.graphflow.util.Report;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zaidshennar on 2017-06-07.
 */
public class ContinuousBaselineShortestPathExecutor {
    private static final ContinuousBaselineShortestPathExecutor INSTANCE = new ContinuousBaselineShortestPathExecutor();
    private List<ContinuousShortestPathPlan> continuousShortestPathPlans = new ArrayList<>();
    public static int batch_number = 0;

    /**
     * Empty private constructor enforces usage of the singleton object {@link #INSTANCE} for this
     * class.
     */
    private ContinuousBaselineShortestPathExecutor() {
    }

    /**
     * Returns the singleton instance {@link #INSTANCE} of {@link ContinuousShortestPathsExecutor}.
     */
    public static ContinuousBaselineShortestPathExecutor getInstance() {
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
        batch_number++;
        Report.INSTANCE.debug(" ** Execute  ContinuousBaselineShortestPathExecutor all queries for batch ###  **");

        ContinuousShortestPathPlan.newBatch = true;
        continuousShortestPathPlans.forEach(plan -> plan.execute(batch_number));
    }
}
