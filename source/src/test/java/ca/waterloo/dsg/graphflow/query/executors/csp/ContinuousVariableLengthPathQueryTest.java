package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.TestUtils;
import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.GraphDBState;
import ca.waterloo.dsg.graphflow.query.planner.ContinuousVariableLengthPathQueryPlanner;
import ca.waterloo.dsg.graphflow.query.plans.ContinuousVariableLengthPathPlan;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link ContinuousVariableLengthPathPlan}s.
 */
public class ContinuousVariableLengthPathQueryTest extends BaseContinuousShortestPathExecutorTest {

    @Before
    public void setUp() {
        GraphDBState.reset();
    }

    /**
     * Tests the case when we start with one path between source and destination, then
     * after additions and deletions we still have one path.
     */
    @Test
    public void testOnePathNoChange() throws IOException {
        Graph graph = Graph.getInstance();
        TestUtils.createEdgesPermanently(graph,
                new int[][]{new int[]{1, 2}, new int[]{2, 3}, new int[]{2, 4}, new int[]{3, 2}});
        int minLength = 1;
        int maxLength = 3;
        ContinuousVariableLengthPathPlan plan = registerContinuousSPPlan(1, 4, minLength, maxLength);
        executor.addShortestPathPlan(plan);
        assertArrayEquals(new double[]{INFTY, 0, 3, 2, 2},
                ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
        Map<Integer, Set<Integer>> expectedSP = new HashMap<>();
        expectedSP.put(1, new HashSet<>());
        expectedSP.put(2, new HashSet<>(Arrays.asList(1)));
        expectedSP.put(4, new HashSet<>(Arrays.asList(2)));
        assertEquals(expectedSP, ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        assertEquals(2, (int) plan.diffBFS.getSrcDstDistance());

        TestUtils.createEdgesTemporarily(graph, new int[][]{new int[]{3, 5}, new int[]{5, 6}});
        TestUtils.deleteEdgesTemporarily(graph, new int[][]{new int[]{3, 2}});
        executor.execute();
        graph.finalizeChanges();

        assertArrayEquals(new double[]{INFTY, 0, 1, 2, 2, 3, INFTY},
                ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
        // Shortest path should not have changed.
        assertEquals(expectedSP, ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        assertEquals(2, (int) plan.diffBFS.getSrcDstDistance());
    }

    /**
     * Tests the case when we start with one path between source and destination, then
     * after additions and deletions we add one more and then after another set of
     * additions and deletion we add multiple more paths.
     */
    @Test
    public void testOnePathAddOneAndMultiplePaths() throws IOException {
        Graph graph = Graph.getInstance();
        TestUtils.createEdgesPermanently(graph,
                new int[][]{new int[]{1, 2}, new int[]{2, 3}, new int[]{2, 4}, new int[]{3, 2}});
        int minLength = 1;
        int maxLength = 3;
        ContinuousVariableLengthPathPlan plan = registerContinuousSPPlan(1, 4, minLength, maxLength);
        executor.addShortestPathPlan(plan);
        // We don't test anything here because this case is tested
        // in the testOnePathNoChange test.

        TestUtils.createEdgesTemporarily(graph, new int[][]{new int[]{3, 4}, new int[]{3, 5}, new int[]{5, 6}});
        TestUtils.deleteEdgesTemporarily(graph, new int[][]{new int[]{3, 2}});
        executor.execute();
        graph.finalizeChanges();

        assertArrayEquals(new double[]{INFTY, 0, 1, 2, 3, 3, INFTY},
                ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
        Map<Integer, Set<Integer>> expectedSP = new HashMap<>();
        expectedSP.put(1, new HashSet<>());
        expectedSP.put(2, new HashSet<>(Arrays.asList(1)));
        expectedSP.put(3, new HashSet<>(Arrays.asList(2)));
        expectedSP.put(4, new HashSet<>(Arrays.asList(2, 3)));
        assertEquals(expectedSP, ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        assertEquals(3, (int) plan.diffBFS.getSrcDstDistance());

        TestUtils.createEdgesTemporarily(graph, new int[][]{new int[]{1, 4}, new int[]{2, 6}, new int[]{6, 4}});
        TestUtils.deleteEdgesTemporarily(graph, new int[][]{new int[]{5, 6}});
        executor.execute();
        graph.finalizeChanges();

        expectedSP = new HashMap<>();
        expectedSP.put(1, new HashSet<>());
        expectedSP.put(2, new HashSet<>(Arrays.asList(1)));
        expectedSP.put(3, new HashSet<>(Arrays.asList(2)));
        expectedSP.put(4, new HashSet<>(Arrays.asList(1, 2, 3, 6)));
        expectedSP.put(6, new HashSet<>(Arrays.asList(2)));
        assertEquals(expectedSP, ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        assertEquals(3, (int) plan.diffBFS.getSrcDstDistance());
    }

    /**
     * Tests the case when we start with one path between source and destination, then
     * after additions and deletions we remove that path and then after another set
     * of additions and deletions we still have no path.
     */
    @Test
    public void testOnePathDeleteOne() throws IOException {
        Graph graph = Graph.getInstance();
        TestUtils.createEdgesPermanently(graph,
                new int[][]{new int[]{1, 2}, new int[]{2, 3}, new int[]{2, 4}, new int[]{3, 2}});
        int minLength = 1;
        int maxLength = 3;
        ContinuousVariableLengthPathPlan plan = registerContinuousSPPlan(1, 4, minLength, maxLength);
        executor.addShortestPathPlan(plan);
        // We don't test anything here because this case is tested
        // in the testOnePathNoChange test.

        TestUtils.createEdgesTemporarily(graph, new int[][]{new int[]{3, 5}, new int[]{5, 6}});
        TestUtils.deleteEdgesTemporarily(graph, new int[][]{new int[]{1, 2}});
        executor.execute();
        graph.finalizeChanges();

        assertArrayEquals(new double[]{INFTY, 0, INFTY, INFTY, INFTY, INFTY, INFTY},
                ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
        Map<Integer, Set<Integer>> expectedSP = new HashMap<>();
        assertEquals(expectedSP, ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        assertEquals(Integer.MAX_VALUE, (int) plan.diffBFS.getSrcDstDistance());

        TestUtils.createEdgesTemporarily(graph, new int[][]{new int[]{1, 2}});
        TestUtils.deleteEdgesTemporarily(graph, new int[][]{new int[]{2, 4}});
        executor.execute();
        graph.finalizeChanges();
        assertArrayEquals(new double[]{INFTY, 0, 3, 2, INFTY, 3, INFTY},
                ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
        assertEquals(expectedSP, ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        assertEquals(Integer.MAX_VALUE, (int) plan.diffBFS.getSrcDstDistance());
    }

    /**
     * Tests the case when we start with multiple paths between source and destination, then
     * after additions and deletions we maintain the same paths.
     */
    @Test
    public void testMultiplePathsNoChange() throws IOException {
        Graph graph = Graph.getInstance();
        TestUtils.createEdgesPermanently(graph,
                new int[][]{new int[]{1, 2}, new int[]{1, 6}, new int[]{2, 3}, new int[]{3, 4}, new int[]{4, 5},
                        new int[]{6, 5}, new int[]{6, 7}, new int[]{0, 8}});
        int minLength = 2;
        int maxLength = 5;
        ContinuousVariableLengthPathPlan plan = registerContinuousSPPlan(1, 5, minLength, maxLength);
        executor.addShortestPathPlan(plan);
        assertArrayEquals(new double[]{INFTY, 0, 1, 2, 3, 4, 1, 2, INFTY},
                ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
        Map<Integer, Set<Integer>> expectedSP = new HashMap<>();
        expectedSP.put(1, new HashSet<>());
        expectedSP.put(2, new HashSet<>(Arrays.asList(1)));
        expectedSP.put(3, new HashSet<>(Arrays.asList(2)));
        expectedSP.put(4, new HashSet<>(Arrays.asList(3)));
        expectedSP.put(5, new HashSet<>(Arrays.asList(4, 6)));
        expectedSP.put(6, new HashSet<>(Arrays.asList(1)));
        assertEquals(expectedSP, ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        assertEquals(4, (int) plan.diffBFS.getSrcDstDistance());

        TestUtils.createEdgesTemporarily(graph, new int[][]{new int[]{1, 8}});
        TestUtils.deleteEdgesTemporarily(graph, new int[][]{new int[]{0, 8}, new int[]{6, 7}});
        executor.execute();
        graph.finalizeChanges();

        assertArrayEquals(new double[]{INFTY, 0, 1, 2, 3, 4, 1, INFTY, 1},
                ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
        assertEquals(expectedSP, ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        assertEquals(4, (int) plan.diffBFS.getSrcDstDistance());
    }

    /**
     * Tests the case when we start with multiple paths between source and destination, then
     * after additions and deletions we add one more and then after another set of
     * additions and deletion we add multiple more paths.
     */
    @Test
    public void testMultiplePathsAddOneAndMultiplePaths() throws IOException {
        Graph graph = Graph.getInstance();
        TestUtils.createEdgesPermanently(graph,
                new int[][]{new int[]{1, 2}, new int[]{1, 6}, new int[]{2, 3}, new int[]{3, 4}, new int[]{4, 5},
                        new int[]{6, 5}, new int[]{6, 7}, new int[]{0, 8}});
        int minLength = 2;
        int maxLength = 5;
        ContinuousVariableLengthPathPlan plan = registerContinuousSPPlan(1, 5, minLength, maxLength);
        executor.addShortestPathPlan(plan);
        // We don't test anything here because this case is tested in the
        // testMultiplePathsNoChange test.

        TestUtils.createEdgesTemporarily(graph, new int[][]{new int[]{7, 5}});
        executor.execute();
        graph.finalizeChanges();

        assertArrayEquals(new double[]{INFTY, 0, 1, 2, 3, 4, 1, 2, INFTY},
                ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
        Map<Integer, Set<Integer>> expectedSP = new HashMap<>();
        expectedSP.put(1, new HashSet<>());
        expectedSP.put(2, new HashSet<>(Arrays.asList(1)));
        expectedSP.put(3, new HashSet<>(Arrays.asList(2)));
        expectedSP.put(4, new HashSet<>(Arrays.asList(3)));
        expectedSP.put(5, new HashSet<>(Arrays.asList(4, 6, 7)));
        expectedSP.put(6, new HashSet<>(Arrays.asList(1)));
        expectedSP.put(7, new HashSet<>(Arrays.asList(6)));
        assertEquals(expectedSP, ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        assertEquals(4, (int) plan.diffBFS.getSrcDstDistance());

        TestUtils.createEdgesTemporarily(graph,
                new int[][]{new int[]{1, 0}, new int[]{8, 9}, new int[]{9, 5}, new int[]{3, 8}, new int[]{4, 10}});
        executor.execute();
        graph.finalizeChanges();

        assertArrayEquals(new double[]{1, 0, 1, 2, 3, 5, 1, 2, 3, 4, 4},
                ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
        expectedSP = new HashMap<>();
        expectedSP.put(0, new HashSet<>(Arrays.asList(1)));
        expectedSP.put(1, new HashSet<>());
        expectedSP.put(2, new HashSet<>(Arrays.asList(1)));
        expectedSP.put(3, new HashSet<>(Arrays.asList(2)));
        expectedSP.put(4, new HashSet<>(Arrays.asList(3)));
        expectedSP.put(5, new HashSet<>(Arrays.asList(4, 6, 7, 9)));
        expectedSP.put(6, new HashSet<>(Arrays.asList(1)));
        expectedSP.put(7, new HashSet<>(Arrays.asList(6)));
        expectedSP.put(8, new HashSet<>(Arrays.asList(0, 3)));
        expectedSP.put(9, new HashSet<>(Arrays.asList(8)));
        assertEquals(expectedSP, ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        assertEquals(5, (int) plan.diffBFS.getSrcDstDistance());
    }

    /**
     * Tests the case when we start with multiple paths between source and destination, then
     * after additions and deletions we remove one of those paths. Then recovers the original paths.
     * Then after another set of additions and deletions we remove multiple of those paths.
     * Then after another set of additions and deletions we remove all of those paths.
     */
    @Test
    public void testMultiplePathsDeleteOneAndMultipleAndAll() throws IOException {
        Graph graph = Graph.getInstance();
        TestUtils.createEdgesPermanently(graph,
                new int[][]{new int[]{1, 2}, new int[]{1, 3}, new int[]{1, 4}, new int[]{1, 5}, new int[]{1, 6},
                        new int[]{2, 3}, new int[]{3, 4}, new int[]{4, 5}, new int[]{5, 6}, new int[]{6, 7}});
        int minLength = 3;
        int maxLength = 5;
        ContinuousVariableLengthPathPlan plan = registerContinuousSPPlan(1, 6, minLength, maxLength);
        executor.addShortestPathPlan(plan);
        assertArrayEquals(new double[]{INFTY, 0, 1, 2, 3, 4, 5, 5},
                ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
        Map<Integer, Set<Integer>> expectedSP = new HashMap<>();
        expectedSP.put(1, new HashSet<>());
        expectedSP.put(2, new HashSet<>(Arrays.asList(1)));
        expectedSP.put(3, new HashSet<>(Arrays.asList(1, 2)));
        expectedSP.put(4, new HashSet<>(Arrays.asList(1, 3)));
        expectedSP.put(5, new HashSet<>(Arrays.asList(4)));
        expectedSP.put(6, new HashSet<>(Arrays.asList(5)));
        assertEquals(expectedSP, ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        assertEquals(5, (int) plan.diffBFS.getSrcDstDistance());

        TestUtils.deleteEdgesTemporarily(graph, new int[][]{new int[]{1, 2}});
        executor.execute();
        graph.finalizeChanges();

        assertArrayEquals(new double[]{INFTY, 0, INFTY, 1, 2, 3, 4, 5},
                ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
        expectedSP = new HashMap<>();
        expectedSP.put(1, new HashSet<>());
        expectedSP.put(3, new HashSet<>(Arrays.asList(1)));
        expectedSP.put(4, new HashSet<>(Arrays.asList(1, 3)));
        expectedSP.put(5, new HashSet<>(Arrays.asList(4)));
        expectedSP.put(6, new HashSet<>(Arrays.asList(5)));
        assertEquals(expectedSP, ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        assertEquals(4, (int) plan.diffBFS.getSrcDstDistance());

        TestUtils.createEdgesTemporarily(graph, new int[][]{new int[]{1, 2}});
        executor.execute();
        graph.finalizeChanges();
        assertArrayEquals(new double[]{INFTY, 0, 1, 2, 3, 4, 5, 5},
                ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
        expectedSP = new HashMap<>();
        expectedSP.put(1, new HashSet<>());
        expectedSP.put(2, new HashSet<>(Arrays.asList(1)));
        expectedSP.put(3, new HashSet<>(Arrays.asList(1, 2)));
        expectedSP.put(4, new HashSet<>(Arrays.asList(1, 3)));
        expectedSP.put(5, new HashSet<>(Arrays.asList(4)));
        expectedSP.put(6, new HashSet<>(Arrays.asList(5)));
        assertEquals(expectedSP, ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        assertEquals(5, (int) plan.diffBFS.getSrcDstDistance());

        TestUtils.deleteEdgesTemporarily(graph, new int[][]{new int[]{1, 3}, new int[]{1, 4}});
        executor.execute();
        graph.finalizeChanges();

        assertArrayEquals(new double[]{INFTY, 0, 1, 2, 3, 4, 5, 3},
                ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
        expectedSP = new HashMap<>();
        expectedSP.put(1, new HashSet<>());
        expectedSP.put(2, new HashSet<>(Arrays.asList(1)));
        expectedSP.put(3, new HashSet<>(Arrays.asList(2)));
        expectedSP.put(4, new HashSet<>(Arrays.asList(3)));
        expectedSP.put(5, new HashSet<>(Arrays.asList(4)));
        expectedSP.put(6, new HashSet<>(Arrays.asList(5)));
        assertEquals(expectedSP, ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        assertEquals(5, (int) plan.diffBFS.getSrcDstDistance());

        TestUtils.createEdgesTemporarily(graph, new int[][]{new int[]{1, 3}});
        TestUtils.deleteEdgesTemporarily(graph, new int[][]{new int[]{3, 4}, new int[]{6, 7}});
        executor.execute();
        graph.finalizeChanges();

        assertArrayEquals(new double[]{INFTY, 0, 1, 2, INFTY, 1, 2, INFTY},
                ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
        expectedSP = new HashMap<>();
        assertEquals(expectedSP, ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        assertEquals(2, (int) plan.diffBFS.getSrcDstDistance());
    }

    private ContinuousVariableLengthPathPlan registerContinuousSPPlan(int source, int destination, int minLength,
                                                                      int maxLength) throws IOException {
        ContinuousVariableLengthPathQueryPlanner planner = new ContinuousVariableLengthPathQueryPlanner();
        return (ContinuousVariableLengthPathPlan) planner
                .plan(0, source, destination, minLength, maxLength, true /* backtrack */);
    }
}
