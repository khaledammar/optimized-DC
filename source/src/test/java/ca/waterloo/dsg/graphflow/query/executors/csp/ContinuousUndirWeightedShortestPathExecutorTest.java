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
 * Tests for {@link NewUnidirectionalWeightedDifferentialBFS}.
 */
public class ContinuousUndirWeightedShortestPathExecutorTest extends BaseContinuousShortestPathExecutorTest {

    @Before
    public void setUp() {
        Graph.IS_WEIGHTED = true;
        GraphDBState.reset();
    }

    /**
     * Tests the latest distances are correct in an acyclic graph under a workload
     * of mixed edge addition and deletion.
     */
    @Test
    public void testContinuousShortestPathsAcyclicMixed() throws IOException {
        Graph graph = Graph.getInstance();
        ContinuousDiffBFSShortestPathPlan plan =
                constructSampleGraphAndRegisterContSPQuery(graph, 1 /* src */, 7 /* dst */);
        executor.addShortestPathPlan(plan);
        assertArrayEquals(new double[]{INFTY, 0.0, 2.5, 3.5, 1.0, 4.0, 5.0, 3.0, 4.0},
                ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
        double[][] expectedDistancesArray = new double[9][];
        expectedDistancesArray[0] = new double[]{INFTY, INFTY, INFTY, INFTY, INFTY, INFTY, INFTY};
        expectedDistancesArray[1] = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        expectedDistancesArray[2] = new double[]{INFTY, 2.5, 2.5, 2.5, 2.5, 2.5, 2.5};
        expectedDistancesArray[3] = new double[]{INFTY, 3.5, 3.5, 3.5, 3.5, 3.5, 3.5};
        expectedDistancesArray[4] = new double[]{INFTY, INFTY, 1.0, 1.0, 1.0, 1.0, 1.0};
        expectedDistancesArray[5] = new double[]{INFTY, INFTY, 4.0, 4.0, 4.0, 4.0, 4.0};
        expectedDistancesArray[6] = new double[]{INFTY, INFTY, INFTY, 5.0, 5.0, 5.0, 5.0};
        expectedDistancesArray[7] = new double[]{INFTY, INFTY, 4.5, 4.5, 3.0, 3.0, 3.0};
        expectedDistancesArray[8] = new double[]{INFTY, INFTY, INFTY, 5.5, 5.5, 4.0, 4.0};
        double[] minFrontierDistances = new double[]{0.0, 2.5, 1.0, 5.0, 3.0, 4.0};
        int[][] minFrontierVertices =
                new int[][]{new int[]{1}, new int[]{2}, new int[]{4}, new int[]{6}, new int[]{7}, new int[]{8}};
        assertDistances(expectedDistancesArray, ((NewUnidirectionalDifferentialBFS) plan.diffBFS).distances,
                minFrontierVertices, minFrontierDistances, false);

        TestUtils.createEdgesTemporarily(graph, new int[][]{new int[]{4, 6}}, new double[]{1.0});
        executor.execute();
        graph.finalizeChanges();
        assertArrayEquals(new double[]{INFTY, 0.0, 2.5, 3.5, 1.0, 4.0, 2.0, 0.0, 1.0},
                ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
        expectedDistancesArray[6] = new double[]{INFTY, INFTY, INFTY, 2.0, 2.0, 2.0, 2.0};
        expectedDistancesArray[7] = new double[]{INFTY, INFTY, 4.5, 4.5, 0.0, 0.0, 0.0};
        expectedDistancesArray[8] = new double[]{INFTY, INFTY, INFTY, 5.5, 5.5, 1.0, 1.0};
        minFrontierDistances = new double[]{0.0, 2.5, 1.0, 2.0, 0.0, 1.0};
        minFrontierVertices =
                new int[][]{new int[]{1}, new int[]{2}, new int[]{4}, new int[]{6}, new int[]{7}, new int[]{8}};
        assertDistances(expectedDistancesArray, ((NewUnidirectionalDifferentialBFS) plan.diffBFS).distances,
                minFrontierVertices, minFrontierDistances, false);

        // WARNING: The way we update the weight of an edge is we add the same
        // edge with a new weight of -3.0.
        TestUtils.createEdgesTemporarily(graph, new int[][]{new int[]{2, 7}}, new double[]{-3.0});
        executor.execute();
        graph.finalizeChanges();
        assertArrayEquals(new double[]{INFTY, 0.0, 2.5, 3.5, 1.0, 4.0, 2.0, -0.5, 0.5},
                ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
        expectedDistancesArray[0] = new double[]{INFTY, INFTY, INFTY, INFTY, INFTY};
        expectedDistancesArray[1] = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
        expectedDistancesArray[2] = new double[]{INFTY, 2.5, 2.5, 2.5, 2.5};
        expectedDistancesArray[3] = new double[]{INFTY, 3.5, 3.5, 3.5, 3.5};
        expectedDistancesArray[4] = new double[]{INFTY, INFTY, 1.0, 1.0, 1.0};
        expectedDistancesArray[5] = new double[]{INFTY, INFTY, 4.0, 4.0, 4.0};
        expectedDistancesArray[6] = new double[]{INFTY, INFTY, INFTY, 2.0, 2.0};
        expectedDistancesArray[7] = new double[]{INFTY, INFTY, -0.5, -0.5, -0.5};
        expectedDistancesArray[8] = new double[]{INFTY, INFTY, INFTY, 0.5, 0.5};
        minFrontierDistances = new double[]{0.0, 2.5, -0.5, 0.5};
        minFrontierVertices = new int[][]{new int[]{1}, new int[]{2}, new int[]{7}, new int[]{8}};
        assertDistances(expectedDistancesArray, ((NewUnidirectionalDifferentialBFS) plan.diffBFS).distances,
                minFrontierVertices, minFrontierDistances, false);
    }

    /**
     * Tests the latest distances are correct in a cyclic graph under a workload
     * of mixed edge addition and deletion.
     */
    @Test
    public void testContinuousShortestPathsCyclicMixed() throws IOException {
        Graph graph = Graph.getInstance();
        ContinuousDiffBFSShortestPathPlan plan = constructSampleGraphAndRegisterContSPQuery(graph, 1, 7);
        TestUtils.createEdgesPermanently(graph, new int[][]{new int[]{8, 4}}, new double[]{1.5});
        executor.addShortestPathPlan(plan);

        assertArrayEquals(new double[]{INFTY, 0.0, 2.5, 3.5, 1.0, 4.0, 5.0, 3.0, 4.0},
                ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);

        // This edge creates a cycle (but not a negative-weight cycle). So the distances should
        // not be affected.
        TestUtils.createEdgesTemporarily(graph, new int[][]{new int[]{4, 7}}, new double[]{2.0});
        executor.execute();
        graph.finalizeChanges();
        assertArrayEquals(new double[]{INFTY, 0.0, 2.5, 3.5, 1.0, 4.0, 5.0, 3.0, 4.0},
                ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);

        // Deleting 2->4 will increase 4's distance from 1.0 to 5.0
        TestUtils.deleteEdgesTemporarily(graph, new int[][]{new int[]{2, 4}});
        executor.execute();
        graph.finalizeChanges();
        assertArrayEquals(new double[]{INFTY, 0.0, 2.5, 3.5, 5.0, 4.0, 5.0, 3.0, 4.0},
                ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
    }

    /**
     * Tests the latest distances are correct in a cyclic graph under a workload
     * of mixed edge addition and deletion.
     */
    @Test
    public void testContinuousShortestPathsNegativeCycle() throws IOException {
        Graph graph = Graph.getInstance();
        ContinuousDiffBFSShortestPathPlan plan = constructSampleGraphAndRegisterContSPQuery(graph, 1, 7);
        TestUtils.createEdgesPermanently(graph, new int[][]{new int[]{8, 4}}, new double[]{1.5});
        executor.addShortestPathPlan(plan);

        assertArrayEquals(new double[]{INFTY, 0.0, 2.5, 3.5, 1.0, 4.0, 5.0, 3.0, 4.0},
                ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);

        // This edge creates a negative weight cycle. So the
        // distances should not be affected.
        TestUtils.createEdgesTemporarily(graph, new int[][]{new int[]{4, 1}}, new double[]{-3.0});
        executor.execute();
        graph.finalizeChanges();
        assertArrayEquals(new double[]{INFTY, 0.0, 2.5, 3.5, 1.0, 4.0, 5.0, 3.0, 4.0},
                ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);

        // Deleting 2->4 will increase 4's distance from 1.0 to 5.0
        TestUtils.deleteEdgesTemporarily(graph, new int[][]{new int[]{2, 4}});
        executor.execute();
        graph.finalizeChanges();
        assertArrayEquals(new double[]{INFTY, 0.0, 2.5, 3.5, 5.0, 4.0, 5.0, 3.0, 4.0},
                ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
    }

    private ContinuousDiffBFSShortestPathPlan constructSampleGraphAndRegisterContSPQuery(Graph graph, int source,
                                                                                         int destination)
            throws IOException {
        TestUtils.createEdgesPermanently(graph,
                new int[][]{new int[]{0, 1}, new int[]{1, 2}, new int[]{1, 3}, new int[]{2, 4}, new int[]{2, 7},
                        new int[]{3, 4}, new int[]{3, 5}, new int[]{5, 6}, new int[]{6, 7}, new int[]{7, 8}},
                new double[]{500.3, 2.5, 3.5, -1.5, 2.0, 1.5, 0.5, 1.0, -2.0, 1.0});
        ContinuousDiffBFSShortestPathPlan plan = registerContinuousSPPlan(source, destination);
        return plan;
    }

    public ContinuousDiffBFSShortestPathPlan registerContinuousSPPlan(int src, int dst) throws IOException {
        return registerContinuousSPPlan(src, dst, ExecutorType.SPSP_W_CDD);
    }
}
