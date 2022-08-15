package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.ExecutorType;
import ca.waterloo.dsg.graphflow.TestUtils;
import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.GraphDBState;
import ca.waterloo.dsg.graphflow.query.plans.ContinuousDiffBFSShortestPathPlan;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;

/**
 * Tests for{@link NewWeightedDifferentialBFSWithPositiveEdges}
 */
public class ContinuousUndirWeightedShortestPathPositiveEdgesExecutorTest
        extends BaseContinuousShortestPathExecutorTest {

    @Before
    public void setUp() {
        Graph.IS_WEIGHTED = true;
        GraphDBState.reset();
    }

    /**
     * Tests the latest distances are correct in an cyclic graph under a workload
     * of mixed edge addition and deletion.
     */
    @Test
    public void testContinuousShortestPathsNewCyclicMixed() throws IOException {
        Graph graph = Graph.getInstance();
        ContinuousDiffBFSShortestPathPlan plan =
                constructSampleGraphAndRegisterContSPQuery(graph, 5 /* src */, 4 /* dst */);
        executor.addShortestPathPlan(plan);
        assertArrayEquals(new double[]{INFTY, 5.5, 2.5, 3.0, 9.5, 0.0, 5.5, 7.0},
                ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
        double[][] expectedDistancesArray = new double[8][];
        expectedDistancesArray[0] = new double[]{INFTY, INFTY, INFTY, INFTY};
        expectedDistancesArray[1] = new double[]{INFTY, INFTY, 5.5, 5.5};
        expectedDistancesArray[2] = new double[]{INFTY, 2.5, 2.5, 2.5};
        expectedDistancesArray[3] = new double[]{INFTY, 3.0, 3.0, 3.0};
        expectedDistancesArray[4] = new double[]{INFTY, INFTY, INFTY, 1.0};
        expectedDistancesArray[5] = new double[]{0.0, 0.0, 0.0, 0.0};
        expectedDistancesArray[6] = new double[]{INFTY, 5.5, 5.5, 5.0};
        expectedDistancesArray[7] = new double[]{INFTY, INFTY, 7.0, 4.5};
        double[] minFrontierDistances = new double[]{0.0, 2.5, 5.5, 5.0, 3.0, 4.0};
        int[][] minFrontierVertices = new int[][]{new int[]{5}, new int[]{2, 3, 6}, new int[]{5, 7}, new int[]{7}};

        /* TODO: Will continue this today */
    }

    /**
     * Tests the latest distances are correct in an cyclic graph under a workload
     * of mixed edge addition and deletion.
     */
    @Test
    public void testContinuousShortestPathsPositiveBasic() throws IOException {
        Graph graph = Graph.getInstance();
        TestUtils.createEdgesPermanently(graph, new int[][]{new int[]{1, 2}, new int[]{2, 3}, new int[]{3, 4}},
                new double[]{5.0, 3.0, 4.5});
        ContinuousDiffBFSShortestPathPlan plan = registerContinuousSPPlan(1, 4);

        executor.addShortestPathPlan(plan);
        assertArrayEquals(new double[]{INFTY, 0.0, 5.0, 8.0, 12.5},
                ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
        double[][] expectedDistancesArray = new double[5][];
        expectedDistancesArray[0] = new double[]{INFTY, INFTY, INFTY, INFTY};
        expectedDistancesArray[1] = new double[]{0.0, 0.0, 0.0, 0.0};
        expectedDistancesArray[2] = new double[]{INFTY, 5.0, 5.0, 5.0};
        expectedDistancesArray[3] = new double[]{INFTY, INFTY, 8.0, 8.0};
        expectedDistancesArray[4] = new double[]{INFTY, INFTY, INFTY, 12.5};
        double[] minFrontierDistances = new double[]{0.0, 5.0, 8.0, 12.5};
        int[][] minFrontierVertices = new int[][]{new int[]{1}, new int[]{2}, new int[]{3}, new int[]{4}};
        assertDistances(expectedDistancesArray, ((NewUnidirectionalDifferentialBFS) plan.diffBFS).distances,
                minFrontierVertices, minFrontierDistances, false);

        TestUtils.createEdgesTemporarily(graph, new int[][]{new int[]{1, 4}}, new double[]{1.0});

        executor.execute();
        graph.finalizeChanges();
        assertArrayEquals(new double[]{INFTY, 0.0, 5.0, INFTY, 1.0},
                ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
    }

    private ContinuousDiffBFSShortestPathPlan constructSampleGraphAndRegisterContSPQuery(Graph graph, int source,
                                                                                         int destination)
            throws IOException {
        TestUtils.createEdgesPermanently(graph,
                new int[][]{new int[]{1, 4}, new int[]{2, 1}, new int[]{2, 7}, new int[]{3, 1}, new int[]{4, 5},
                        new int[]{4, 2}, new int[]{5, 6}, new int[]{5, 2}, new int[]{5, 3}, new int[]{6, 5},
                        new int[]{6, 7}, new int[]{7, 4}, new int[]{7, 3}},
                new double[]{4.0, 3.0, 4.5, 7.0, 2.0, 6.0, 5.5, 2.5, 3.0, 1.0, 3.0, 10.0, 2.0});
        ContinuousDiffBFSShortestPathPlan plan = registerContinuousSPPlan(source, destination);
        return plan;
    }

    public ContinuousDiffBFSShortestPathPlan registerContinuousSPPlan(int src, int dst) throws IOException {
        return registerContinuousSPPlan(src, dst, ExecutorType.UNIDIR_POSITIVE_WEIGHTED_DIFF_BFS);
    }
}
