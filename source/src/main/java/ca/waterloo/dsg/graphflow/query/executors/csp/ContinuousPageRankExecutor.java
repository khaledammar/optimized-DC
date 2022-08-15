package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.query.plans.ContinuousShortestPathPlan;
import ca.waterloo.dsg.graphflow.util.Report;

import java.util.ArrayList;
import java.util.List;

/**
 * Finds the s-t shortest path between a given source s and destination t using uni-directional BFS.
 **/
public class ContinuousPageRankExecutor {

    private static final ContinuousPageRankExecutor INSTANCE = new ContinuousPageRankExecutor();
    public static List<ContinuousShortestPathPlan> continuousShortestPathPlans = new ArrayList<>();
    public static int batch_number = 0;

    /**
     * Empty private constructor enforces usage of the singleton object {@link #INSTANCE} for this
     * class.
     */
    private ContinuousPageRankExecutor() {
    }

    /**
     * Returns the singleton instance {@link #INSTANCE} of {@link ContinuousPageRankExecutor}.
     */
    public static ContinuousPageRankExecutor getInstance() {
        return INSTANCE;
    }

    public static List<ContinuousShortestPathPlan> getShortestPathPlans() {
        return continuousShortestPathPlans;
    }

    /**
     * adds a new {@link ContinuousShortestPathPlan} to the list of plans
     *
     * @param plan the new {@link ContinuousShortestPathPlan}
     */
    public void addShortestPathPlan(ContinuousShortestPathPlan plan) {
        this.continuousShortestPathPlans.add(plan);
    }

    /**
     * set the single new {@link ContinuousShortestPathPlan} to the list of plans
     *
     * @param plan the new {@link ContinuousShortestPathPlan}
     */
    public void setShortestPathPlan(ContinuousShortestPathPlan plan) {
        this.continuousShortestPathPlans.add(0, plan);
    }

    /**
     * removes all current continuous shortest path queries
     */
    public static void reset() {
        continuousShortestPathPlans.clear();
    }

    public void initializeStats() {
        for (ContinuousShortestPathPlan plan : continuousShortestPathPlans) {
            plan.initializeStats();
        }
    }

    /**
     * Executes all the registered {@link ContinuousShortestPathPlan}s.
     */
    public void execute() {

        //Report.INSTANCE.debug(" ** Execute  ContinuousShortestPathsExecutor all queries for batch #" + batch_number + "**");

        // increase batch id counter
        batch_number++;
        int queryNum = 1;
        for (ContinuousShortestPathPlan plan : continuousShortestPathPlans) {
            //System.out.println("Execute query "+queryNum );
            plan.execute(batch_number);
            queryNum++;
        }
    }

    public void printAllDiffs() {
        int i = 0;
        for (ContinuousShortestPathPlan plan : continuousShortestPathPlans) {
            Report.INSTANCE.debug("***** Query " + i + "******");
            plan.printDiffs();
            i++;
        }
    }

    public void printStats() {
        int i = 0;
        for (ContinuousShortestPathPlan plan : continuousShortestPathPlans) {
            Report.INSTANCE.debug("***** Query " + i + "******");
            plan.printStats();
            i++;
        }
    }

    public double[] getSourceDestDistances() {
        double[] srcDstDistancesArray = new double[continuousShortestPathPlans.size()];
        for (int i = 0; i < continuousShortestPathPlans.size(); i++) {
            srcDstDistancesArray[i] = continuousShortestPathPlans.get(i).getSrcDstDistance();
        }
        return srcDstDistancesArray;
    }
}
