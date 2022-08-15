package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.ExecutorType;
import ca.waterloo.dsg.graphflow.TestUtils;
import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.GraphDBState;
import ca.waterloo.dsg.graphflow.query.plans.ContinuousDiffBFSShortestPathPlan;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Common tests for {@link NewUnidirectionalUnweightedDifferentialBFS} and
 * {@link NewBidirUnweightedDifferentialBFS}. These tests are written to verify
 * the behavior of both implementations depending on the value of {@link #getExecutorType()},
 * which should be {@link ExecutorType#UNI_UNWEIGHTED_DIFF_BFS} for
 * {@link NewUnidirectionalUnweightedDifferentialBFS} and {@link ExecutorType#BIDIR_UNWEIGHTED_DIFF_BFS}
 * for {@link NewBidirUnweightedDifferentialBFS}.
 */
public abstract class BaseContinuousUnidirBidirUnweightedShortestPathExecutorTest
        extends BaseContinuousShortestPathExecutorTest {

    @Before
    public void setUp() {
        GraphDBState.reset();
    }

    /**
     * Tests the execution of mixed operations mutation in CONTINUOUSLY SHORTEST PATH query.
     */
    @Test
    public void testContinuousShortestPathsMixed() throws IOException {
        Graph graph = Graph.getInstance();
        TestUtils.createEdgesPermanently(graph,
                new int[][]{new int[]{1, 2}, new int[]{2, 3}, new int[]{2, 4}, new int[]{3, 2}});
        ContinuousDiffBFSShortestPathPlan plan = registerContinuousSPPlan(1, 4);
        executor.addShortestPathPlan(plan);

        if (ExecutorType.UNI_UNWEIGHTED_DIFF_BFS == getExecutorType()) {
            assertArrayEquals(new double[]{INFTY, 0, 1, 2, 2},
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
            Map<Integer, Set<Integer>> expectedSP = new HashMap<>();
            expectedSP.put(1, new HashSet<>());
            expectedSP.put(2, new HashSet<>(Arrays.asList(1)));
            expectedSP.put(4, new HashSet<>(Arrays.asList(2)));
            assertEquals(expectedSP, ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        } else {
            assertFwAndBwDiffBFSDistanceArrays(plan, new double[]{INFTY, 0, 1, INFTY, INFTY},
                    new double[]{INFTY, INFTY, 1, INFTY, 0});
            assertEquals(new HashSet<>(Arrays.asList(2)),
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).intersection);
            Map<Integer, Set<Integer>> expectedFWSP = new HashMap<>();
            expectedFWSP.put(1, new HashSet<>());
            expectedFWSP.put(2, new HashSet<>(Arrays.asList(1)));
            assertEquals(expectedFWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).fwDiffBFS.shortestPath.shortestPath);
            Map<Integer, Set<Integer>> expectedBWSP = new HashMap<>();
            expectedBWSP.put(4, new HashSet<>());
            expectedBWSP.put(2, new HashSet<>(Arrays.asList(4)));
            assertEquals(expectedBWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).bwDiffBFS.shortestPath.shortestPath);
        }
        assertEquals(2, (int) plan.diffBFS.getSrcDstDistance());

        TestUtils.createEdgesTemporarily(graph, new int[][]{new int[]{1, 3}});
        TestUtils.deleteEdgesTemporarily(graph, new int[][]{new int[]{1, 2}});

        executor.execute();
        graph.finalizeChanges();

        if (ExecutorType.UNI_UNWEIGHTED_DIFF_BFS == getExecutorType()) {
            assertArrayEquals(new double[]{INFTY, 0, 2, 1, 3},
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0);
            Map<Integer, Set<Integer>> expectedShortestPath = new HashMap<>();
            expectedShortestPath.put(1, new HashSet<>());
            expectedShortestPath.put(3, new HashSet<>(Arrays.asList(1)));
            expectedShortestPath.put(2, new HashSet<>(Arrays.asList(3)));
            expectedShortestPath.put(4, new HashSet<>(Arrays.asList(2)));
            assertEquals(expectedShortestPath,
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        } else {
            assertFwAndBwDiffBFSDistanceArrays(plan, new double[]{INFTY, 0, 2, 1, INFTY},
                    new double[]{INFTY, INFTY, 1, INFTY, 0});
            assertEquals(new HashSet<>(Arrays.asList(2)),
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).intersection);
            Map<Integer, Set<Integer>> expectedFWSP = new HashMap<>();
            expectedFWSP.put(1, new HashSet<>());
            expectedFWSP.put(3, new HashSet<>(Arrays.asList(1)));
            expectedFWSP.put(2, new HashSet<>(Arrays.asList(3)));
            assertEquals(expectedFWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).fwDiffBFS.shortestPath.shortestPath);
            Map<Integer, Set<Integer>> expectedBWSP = new HashMap<>();
            expectedBWSP.put(4, new HashSet<>());
            expectedBWSP.put(2, new HashSet<>(Arrays.asList(4)));
            assertEquals(expectedBWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).bwDiffBFS.shortestPath.shortestPath);
        }
        assertEquals(3, (int) plan.diffBFS.getSrcDstDistance());
    }

    /**
     * Tests the execution where the shortest path keeps getting shorter each time
     */
    @Test
    public void testContinuousShortestPathsAddition() throws IOException {
        Graph graph = Graph.getInstance();
        TestUtils.createEdgesPermanently(graph,
                new int[][]{new int[]{1, 5}, new int[]{2, 5}, new int[]{2, 8}, new int[]{3, 5}, new int[]{3, 7},
                        new int[]{4, 2}, new int[]{5, 1}, new int[]{5, 3}, new int[]{6, 4}, new int[]{6, 1},
                        new int[]{7, 4}, new int[]{7, 6}, new int[]{8, 2}});
        ContinuousDiffBFSShortestPathPlan plan = registerContinuousSPPlan(1, 2);
        executor.addShortestPathPlan(plan);

        if (ExecutorType.UNI_UNWEIGHTED_DIFF_BFS == getExecutorType()) {
            assertArrayEquals(new double[]{INFTY, 0, 5, 2, 4, 1, 4, 3, INFTY},
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
            Map<Integer, Set<Integer>> expectedShortestPath = new HashMap<>();
            expectedShortestPath.put(1, new HashSet<>());
            expectedShortestPath.put(5, new HashSet<>(Arrays.asList(1)));
            expectedShortestPath.put(3, new HashSet<>(Arrays.asList(5)));
            expectedShortestPath.put(7, new HashSet<>(Arrays.asList(3)));
            expectedShortestPath.put(4, new HashSet<>(Arrays.asList(7)));
            expectedShortestPath.put(2, new HashSet<>(Arrays.asList(4)));
            assertEquals(expectedShortestPath,
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        } else {
            assertFwAndBwDiffBFSDistanceArrays(plan, new double[]{INFTY, 0, INFTY, 2, INFTY, 1, INFTY, 3, INFTY},
                    new double[]{INFTY, INFTY, 0, INFTY, 1, INFTY, 2, 2, 1});
            assertEquals(new HashSet<>(Arrays.asList(7)),
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).intersection);
            Map<Integer, Set<Integer>> expectedFWSP = new HashMap<>();
            expectedFWSP.put(1, new HashSet<>());
            expectedFWSP.put(5, new HashSet<>(Arrays.asList(1)));
            expectedFWSP.put(3, new HashSet<>(Arrays.asList(5)));
            expectedFWSP.put(7, new HashSet<>(Arrays.asList(3)));
            assertEquals(expectedFWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).fwDiffBFS.shortestPath.shortestPath);
            Map<Integer, Set<Integer>> expectedBWSP = new HashMap<>();
            expectedBWSP.put(2, new HashSet<>());
            expectedBWSP.put(4, new HashSet<>(Arrays.asList(2)));
            expectedBWSP.put(7, new HashSet<>(Arrays.asList(4)));
            assertEquals(expectedBWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).bwDiffBFS.shortestPath.shortestPath);
        }
        assertEquals(5, (int) plan.diffBFS.getSrcDstDistance());
        TestUtils.createEdgesTemporarily(graph, new int[][]{new int[]{1, 7}});
        executor.execute();
        graph.finalizeChanges();

        if (ExecutorType.UNI_UNWEIGHTED_DIFF_BFS == getExecutorType()) {
            assertArrayEquals(new double[]{INFTY, 0, 3, 2, 2, 1, 2, 1, INFTY},
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
            Map<Integer, Set<Integer>> expectedShortestPath = new HashMap<>();
            expectedShortestPath.put(1, new HashSet<>());
            expectedShortestPath.put(7, new HashSet<>(Arrays.asList(1)));
            expectedShortestPath.put(4, new HashSet<>(Arrays.asList(7)));
            expectedShortestPath.put(2, new HashSet<>(Arrays.asList(4)));
            assertEquals(expectedShortestPath,
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        } else {
            assertFwAndBwDiffBFSDistanceArrays(plan, new double[]{INFTY, 0, INFTY, 2, 2, 1, 2, 1, INFTY},
                    new double[]{INFTY, INFTY, 0, INFTY, 1, INFTY, INFTY, INFTY, 1});
            assertEquals(new HashSet<>(Arrays.asList(4)),
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).intersection);
            Map<Integer, Set<Integer>> expectedFWSP = new HashMap<>();
            expectedFWSP.put(1, new HashSet<>());
            expectedFWSP.put(7, new HashSet<>(Arrays.asList(1)));
            expectedFWSP.put(4, new HashSet<>(Arrays.asList(7)));
            assertEquals(expectedFWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).fwDiffBFS.shortestPath.shortestPath);
            Map<Integer, Set<Integer>> expectedBWSP = new HashMap<>();
            expectedBWSP.put(2, new HashSet<>());
            expectedBWSP.put(4, new HashSet<>(Arrays.asList(2)));
            assertEquals(expectedBWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).bwDiffBFS.shortestPath.shortestPath);
        }
        assertEquals(3, (int) plan.diffBFS.getSrcDstDistance());

        TestUtils.createEdgesTemporarily(graph, new int[][]{new int[]{5, 2}});
        executor.execute();
        graph.finalizeChanges();

        if (ExecutorType.UNI_UNWEIGHTED_DIFF_BFS == getExecutorType()) {
            assertArrayEquals(new double[]{INFTY, 0, 2, 2, 2, 1, 2, 1, INFTY},
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
            Map<Integer, Set<Integer>> expectedShortestPath = new HashMap<>();
            expectedShortestPath.put(1, new HashSet<>());
            expectedShortestPath.put(5, new HashSet<>(Arrays.asList(1)));
            expectedShortestPath.put(2, new HashSet<>(Arrays.asList(5)));
            assertEquals(expectedShortestPath,
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        } else {
            assertFwAndBwDiffBFSDistanceArrays(plan, new double[]{INFTY, 0, INFTY, INFTY, INFTY, 1, INFTY, 1, INFTY},
                    new double[]{INFTY, INFTY, 0, INFTY, 1, 1, INFTY, INFTY, 1});
            assertEquals(new HashSet<>(Arrays.asList(5)),
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).intersection);
            Map<Integer, Set<Integer>> expectedFWSP = new HashMap<>();
            expectedFWSP.put(1, new HashSet<>());
            expectedFWSP.put(5, new HashSet<>(Arrays.asList(1)));
            assertEquals(expectedFWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).fwDiffBFS.shortestPath.shortestPath);
            Map<Integer, Set<Integer>> expectedBWSP = new HashMap<>();
            expectedBWSP.put(2, new HashSet<>());
            expectedBWSP.put(5, new HashSet<>(Arrays.asList(2)));
            assertEquals(expectedBWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).bwDiffBFS.shortestPath.shortestPath);
        }
        assertEquals(2, (int) plan.diffBFS.getSrcDstDistance());
    }

    /**
     * Tests adding a new vertex with a new higher vertex id.
     */
    @Test
    public void testContinuousShortestPathsNewPath() throws IOException {
        Graph graph = Graph.getInstance();
        TestUtils.createEdgesPermanently(graph, new int[][]{new int[]{1, 2}, new int[]{2, 3}, new int[]{3, 4}});
        ContinuousDiffBFSShortestPathPlan plan = registerContinuousSPPlan(1, 4);
        executor.addShortestPathPlan(plan);

        if (ExecutorType.UNI_UNWEIGHTED_DIFF_BFS == getExecutorType()) {
            assertArrayEquals(new double[]{INFTY, 0, 1, 2, 3},
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
            Map<Integer, Set<Integer>> expectedShortestPath = new HashMap<>();
            expectedShortestPath.put(1, new HashSet<>());
            expectedShortestPath.put(2, new HashSet<>(Arrays.asList(1)));
            expectedShortestPath.put(3, new HashSet<>(Arrays.asList(2)));
            expectedShortestPath.put(4, new HashSet<>(Arrays.asList(3)));
            assertEquals(expectedShortestPath,
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        } else {
            assertFwAndBwDiffBFSDistanceArrays(plan, new double[]{INFTY, 0, 1, 2, INFTY},
                    new double[]{INFTY, INFTY, INFTY, 1, 0});
            assertEquals(new HashSet<>(Arrays.asList(3)),
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).intersection);
            Map<Integer, Set<Integer>> expectedFWSP = new HashMap<>();
            expectedFWSP.put(1, new HashSet<>());
            expectedFWSP.put(2, new HashSet<>(Arrays.asList(1)));
            expectedFWSP.put(3, new HashSet<>(Arrays.asList(2)));
            assertEquals(expectedFWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).fwDiffBFS.shortestPath.shortestPath);
            Map<Integer, Set<Integer>> expectedBWSP = new HashMap<>();
            expectedBWSP.put(4, new HashSet<>());
            expectedBWSP.put(3, new HashSet<>(Arrays.asList(4)));
            assertEquals(expectedBWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).bwDiffBFS.shortestPath.shortestPath);
        }
        assertEquals(3, (int) plan.diffBFS.getSrcDstDistance());

        TestUtils.createEdgesTemporarily(graph, new int[][]{new int[]{1, 8}, new int[]{8, 4}});

        executor.execute();
        graph.finalizeChanges();
        if (ExecutorType.UNI_UNWEIGHTED_DIFF_BFS == getExecutorType()) {
            assertArrayEquals(new double[]{INFTY, 0, 1, 2, 2, INFTY, INFTY, INFTY, 1},
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
            Map<Integer, Set<Integer>> expectedShortestPath = new HashMap<>();
            expectedShortestPath.put(1, new HashSet<>());
            expectedShortestPath.put(8, new HashSet<>(Arrays.asList(1)));
            expectedShortestPath.put(4, new HashSet<>(Arrays.asList(8)));
            assertEquals(expectedShortestPath,
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        } else {
            assertFwAndBwDiffBFSDistanceArrays(plan, new double[]{INFTY, 0, 1, INFTY, INFTY, INFTY, INFTY, INFTY, 1},
                    new double[]{INFTY, INFTY, INFTY, 1, 0, INFTY, INFTY, INFTY, 1});
            assertEquals(new HashSet<>(Arrays.asList(8)),
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).intersection);
            Map<Integer, Set<Integer>> expectedFWSP = new HashMap<>();
            expectedFWSP.put(1, new HashSet<>());
            expectedFWSP.put(8, new HashSet<>(Arrays.asList(1)));
            assertEquals(expectedFWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).fwDiffBFS.shortestPath.shortestPath);
            Map<Integer, Set<Integer>> expectedBWSP = new HashMap<>();
            expectedBWSP.put(4, new HashSet<>());
            expectedBWSP.put(8, new HashSet<>(Arrays.asList(4)));
            assertEquals(expectedBWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).bwDiffBFS.shortestPath.shortestPath);
        }
        assertEquals(2, (int) plan.diffBFS.getSrcDstDistance());
    }

    /**
     * Tests deleting the current shortest and choosing the next longest path -- has to redo the
     * bfs again
     */
    @Test
    public void testContinuousShortestPathsLongerPath() throws IOException {
        Graph graph = Graph.getInstance();
        TestUtils.createEdgesPermanently(graph,
                new int[][]{new int[]{1, 2}, new int[]{1, 8}, new int[]{2, 3}, new int[]{3, 4}, new int[]{4, 5},
                        new int[]{5, 6}, new int[]{8, 6}});
        ContinuousDiffBFSShortestPathPlan plan = registerContinuousSPPlan(1, 6);
        executor.addShortestPathPlan(plan);

        if (ExecutorType.UNI_UNWEIGHTED_DIFF_BFS == getExecutorType()) {
            assertArrayEquals(new double[]{INFTY, 0, 1, 2, INFTY, INFTY, 2, INFTY, 1},
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
            Map<Integer, Set<Integer>> expectedShortestPath = new HashMap<>();
            expectedShortestPath.put(1, new HashSet<>());
            expectedShortestPath.put(8, new HashSet<>(Arrays.asList(1)));
            expectedShortestPath.put(6, new HashSet<>(Arrays.asList(8)));
            assertEquals(expectedShortestPath,
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        } else {
            assertFwAndBwDiffBFSDistanceArrays(plan, new double[]{INFTY, 0, 1, INFTY, INFTY, INFTY, INFTY, INFTY, 1},
                    new double[]{INFTY, INFTY, INFTY, INFTY, INFTY, 1, 0, INFTY, 1});
            assertEquals(new HashSet<>(Arrays.asList(8)),
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).intersection);
            Map<Integer, Set<Integer>> expectedFWSP = new HashMap<>();
            expectedFWSP.put(1, new HashSet<>());
            expectedFWSP.put(8, new HashSet<>(Arrays.asList(1)));
            assertEquals(expectedFWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).fwDiffBFS.shortestPath.shortestPath);
            Map<Integer, Set<Integer>> expectedBWSP = new HashMap<>();
            expectedBWSP.put(6, new HashSet<>());
            expectedBWSP.put(8, new HashSet<>(Arrays.asList(6)));
            assertEquals(expectedBWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).bwDiffBFS.shortestPath.shortestPath);
        }
        assertEquals(2, (int) plan.diffBFS.getSrcDstDistance());

        TestUtils.deleteEdgesTemporarily(graph, new int[][]{new int[]{1, 8}});

        executor.execute();
        graph.finalizeChanges();

        if (ExecutorType.UNI_UNWEIGHTED_DIFF_BFS == getExecutorType()) {
            assertArrayEquals(new double[]{INFTY, 0, 1, 2, 3, 4, 5, INFTY, INFTY},
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
            Map<Integer, Set<Integer>> expectedShortestPath = new HashMap<>();
            expectedShortestPath.put(1, new HashSet<>());
            expectedShortestPath.put(2, new HashSet<>(Arrays.asList(1)));
            expectedShortestPath.put(3, new HashSet<>(Arrays.asList(2)));
            expectedShortestPath.put(4, new HashSet<>(Arrays.asList(3)));
            expectedShortestPath.put(5, new HashSet<>(Arrays.asList(4)));
            expectedShortestPath.put(6, new HashSet<>(Arrays.asList(5)));
            assertEquals(expectedShortestPath,
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        } else {
            assertFwAndBwDiffBFSDistanceArrays(plan, new double[]{INFTY, 0, 1, 2, 3, INFTY, INFTY, INFTY, INFTY},
                    new double[]{INFTY, INFTY, INFTY, INFTY, 2, 1, 0, INFTY, 1});
            assertEquals(new HashSet<>(Arrays.asList(4)),
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).intersection);
            Map<Integer, Set<Integer>> expectedFWSP = new HashMap<>();
            expectedFWSP.put(1, new HashSet<>());
            expectedFWSP.put(2, new HashSet<>(Arrays.asList(1)));
            expectedFWSP.put(3, new HashSet<>(Arrays.asList(2)));
            expectedFWSP.put(4, new HashSet<>(Arrays.asList(3)));
            assertEquals(expectedFWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).fwDiffBFS.shortestPath.shortestPath);
            Map<Integer, Set<Integer>> expectedBWSP = new HashMap<>();
            expectedBWSP.put(6, new HashSet<>());
            expectedBWSP.put(5, new HashSet<>(Arrays.asList(6)));
            expectedBWSP.put(4, new HashSet<>(Arrays.asList(5)));
            assertEquals(expectedBWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).bwDiffBFS.shortestPath.shortestPath);
        }
        assertEquals(5, (int) plan.diffBFS.getSrcDstDistance());
    }

    /**
     * Tests the execution of shortest paths when we add new vertices.
     */
    @Test
    public void testContinuousShortestPathsAddVertices() throws IOException {
        Graph graph = Graph.getInstance();
        TestUtils.createEdgesPermanently(graph,
                new int[][]{new int[]{1, 5}, new int[]{2, 5}, new int[]{3, 5}, new int[]{3, 4}, new int[]{4, 2},
                        new int[]{4, 3}, new int[]{4, 5}, new int[]{5, 1}, new int[]{5, 3}});
        ContinuousDiffBFSShortestPathPlan plan = registerContinuousSPPlan(1, 2);
        executor.addShortestPathPlan(plan);

        if (ExecutorType.UNI_UNWEIGHTED_DIFF_BFS == getExecutorType()) {
            assertArrayEquals(new double[]{INFTY, 0, 4, 2, 3, 1},
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
            Map<Integer, Set<Integer>> expectedShortestPath = new HashMap<>();
            expectedShortestPath.put(1, new HashSet<>());
            expectedShortestPath.put(5, new HashSet<>(Arrays.asList(1)));
            expectedShortestPath.put(3, new HashSet<>(Arrays.asList(5)));
            expectedShortestPath.put(4, new HashSet<>(Arrays.asList(3)));
            expectedShortestPath.put(2, new HashSet<>(Arrays.asList(4)));
            assertEquals(expectedShortestPath,
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        } else {
            assertFwAndBwDiffBFSDistanceArrays(plan, new double[]{INFTY, 0, INFTY, 2, INFTY, 1},
                    new double[]{INFTY, INFTY, 0, 2, 1, INFTY});
            assertEquals(new HashSet<>(Arrays.asList(3)),
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).intersection);
            Map<Integer, Set<Integer>> expectedFWSP = new HashMap<>();
            expectedFWSP.put(1, new HashSet<>());
            expectedFWSP.put(5, new HashSet<>(Arrays.asList(1)));
            expectedFWSP.put(3, new HashSet<>(Arrays.asList(5)));
            assertEquals(expectedFWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).fwDiffBFS.shortestPath.shortestPath);
            Map<Integer, Set<Integer>> expectedBWSP = new HashMap<>();
            expectedBWSP.put(2, new HashSet<>());
            expectedBWSP.put(4, new HashSet<>(Arrays.asList(2)));
            expectedBWSP.put(3, new HashSet<>(Arrays.asList(4)));
            assertEquals(expectedBWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).bwDiffBFS.shortestPath.shortestPath);
        }
        assertEquals(4, (int) plan.diffBFS.getSrcDstDistance());

        TestUtils.createEdgesTemporarily(graph,
                new int[][]{new int[]{1, 7}, new int[]{6, 4}, new int[]{6, 1}, new int[]{7, 4}, new int[]{7, 6},
                        new int[]{8, 2}});
        executor.execute();
        graph.finalizeChanges();

        if (ExecutorType.UNI_UNWEIGHTED_DIFF_BFS == getExecutorType()) {
            assertArrayEquals(new double[]{INFTY, 0, 3, 2, 2, 1, 2, 1, INFTY},
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
            Map<Integer, Set<Integer>> expectedShortestPath = new HashMap<>();
            expectedShortestPath.put(1, new HashSet<>());
            expectedShortestPath.put(7, new HashSet<>(Arrays.asList(1)));
            expectedShortestPath.put(4, new HashSet<>(Arrays.asList(7)));
            expectedShortestPath.put(2, new HashSet<>(Arrays.asList(4)));
            assertEquals(expectedShortestPath,
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        } else {
            assertFwAndBwDiffBFSDistanceArrays(plan, new double[]{INFTY, 0, INFTY, 2, 2, 1, 2, 1, INFTY},
                    new double[]{INFTY, INFTY, 0, INFTY, 1, INFTY, INFTY, INFTY, 1});
            assertEquals(new HashSet<>(Arrays.asList(4)),
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).intersection);
            Map<Integer, Set<Integer>> expectedFWSP = new HashMap<>();
            expectedFWSP.put(1, new HashSet<>());
            expectedFWSP.put(7, new HashSet<>(Arrays.asList(1)));
            expectedFWSP.put(4, new HashSet<>(Arrays.asList(7)));
            assertEquals(expectedFWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).fwDiffBFS.shortestPath.shortestPath);
            Map<Integer, Set<Integer>> expectedBWSP = new HashMap<>();
            expectedBWSP.put(2, new HashSet<>());
            expectedBWSP.put(4, new HashSet<>(Arrays.asList(2)));
            assertEquals(expectedBWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).bwDiffBFS.shortestPath.shortestPath);
        }
        assertEquals(3, (int) plan.diffBFS.getSrcDstDistance());
    }

    /**
     * Test that adds another path that is equal to the shortest path but different traversal.
     */
    @Test
    public void testContinuousShortestPathsAddEquilengthNewPath() throws IOException {
        Graph graph = Graph.getInstance();
        TestUtils.createEdgesPermanently(graph,
                new int[][]{new int[]{1, 5}, new int[]{2, 5}, new int[]{3, 5}, new int[]{3, 4}, new int[]{4, 2},
                        new int[]{4, 3}, new int[]{4, 5}, new int[]{5, 1}, new int[]{5, 3}});
        ContinuousDiffBFSShortestPathPlan plan = registerContinuousSPPlan(1, 2);
        executor.addShortestPathPlan(plan);

        // We omit testing anything here because this initial configuration is already tested in the
        // testContinuousShortestPathsAddVertices test above, which uses the same initial graph.

        TestUtils.createEdgesTemporarily(graph,
                new int[][]{new int[]{1, 7}, new int[]{6, 4}, new int[]{6, 1}, new int[]{7, 6}, new int[]{8, 2}});

        executor.execute();
        graph.finalizeChanges();

        if (ExecutorType.UNI_UNWEIGHTED_DIFF_BFS == getExecutorType()) {
            assertArrayEquals(new double[]{INFTY, 0, 4, 2, 3, 1, 2, 1, INFTY},
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
            Map<Integer, Set<Integer>> expectedShortestPath = new HashMap<>();
            expectedShortestPath.put(1, new HashSet<>());
            expectedShortestPath.put(7, new HashSet<>(Arrays.asList(1)));
            expectedShortestPath.put(5, new HashSet<>(Arrays.asList(1)));
            expectedShortestPath.put(6, new HashSet<>(Arrays.asList(7)));
            expectedShortestPath.put(3, new HashSet<>(Arrays.asList(5)));
            expectedShortestPath.put(4, new HashSet<>(Arrays.asList(3, 6)));
            expectedShortestPath.put(2, new HashSet<>(Arrays.asList(4)));
            assertEquals(expectedShortestPath,
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        } else {
            assertFwAndBwDiffBFSDistanceArrays(plan, new double[]{INFTY, 0, INFTY, 2, INFTY, 1, 2, 1, INFTY},
                    new double[]{INFTY, INFTY, 0, 2, 1, INFTY, 2, INFTY, 1});
            assertEquals(new HashSet<>(Arrays.asList(3, 6)),
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).intersection);
            Map<Integer, Set<Integer>> expectedFWSP = new HashMap<>();
            expectedFWSP.put(1, new HashSet<>());
            expectedFWSP.put(7, new HashSet<>(Arrays.asList(1)));
            expectedFWSP.put(5, new HashSet<>(Arrays.asList(1)));
            expectedFWSP.put(6, new HashSet<>(Arrays.asList(7)));
            expectedFWSP.put(3, new HashSet<>(Arrays.asList(5)));
            assertEquals(expectedFWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).fwDiffBFS.shortestPath.shortestPath);
            Map<Integer, Set<Integer>> expectedBWSP = new HashMap<>();
            expectedBWSP.put(2, new HashSet<>());
            expectedBWSP.put(4, new HashSet<>(Arrays.asList(2)));
            expectedBWSP.put(3, new HashSet<>(Arrays.asList(4)));
            expectedBWSP.put(6, new HashSet<>(Arrays.asList(4)));
            assertEquals(expectedBWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).bwDiffBFS.shortestPath.shortestPath);
        }
        assertEquals(4, (int) plan.diffBFS.getSrcDstDistance());
    }

    /**
     * Removes the only path that existed between source and destination
     */
    @Test
    public void testContinuousShortestPathsRemovePath() throws IOException {
        Graph graph = Graph.getInstance();
        TestUtils.createEdgesPermanently(graph, new int[][]{new int[]{5, 3}});
        ContinuousDiffBFSShortestPathPlan plan = registerContinuousSPPlan(5, 3);
        executor.addShortestPathPlan(plan);

        if (ExecutorType.UNI_UNWEIGHTED_DIFF_BFS == getExecutorType()) {
            assertArrayEquals(new double[]{INFTY, INFTY, INFTY, 1, INFTY, 0},
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
            Map<Integer, Set<Integer>> expectedShortestPath = new HashMap<>();
            expectedShortestPath.put(5, new HashSet<>());
            expectedShortestPath.put(3, new HashSet<>(Arrays.asList(5)));
            assertEquals(expectedShortestPath,
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        } else {
            assertFwAndBwDiffBFSDistanceArrays(plan, new double[]{INFTY, INFTY, INFTY, 1, INFTY, 0},
                    new double[]{INFTY, INFTY, INFTY, 0, INFTY, INFTY});
            assertEquals(new HashSet<>(Arrays.asList(3)),
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).intersection);
            Map<Integer, Set<Integer>> expectedFWSP = new HashMap<>();
            expectedFWSP.put(5, new HashSet<>());
            expectedFWSP.put(3, new HashSet<>(Arrays.asList(5)));
            assertEquals(expectedFWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).fwDiffBFS.shortestPath.shortestPath);
            Map<Integer, Set<Integer>> expectedBWSP = new HashMap<>();
            expectedBWSP.put(3, new HashSet<>());
            assertEquals(expectedBWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).bwDiffBFS.shortestPath.shortestPath);
        }
        assertEquals(1, (int) plan.diffBFS.getSrcDstDistance());

        TestUtils.deleteEdgesTemporarily(graph, new int[][]{new int[]{5, 3}});
        executor.execute();
        graph.finalizeChanges();

        if (ExecutorType.UNI_UNWEIGHTED_DIFF_BFS == getExecutorType()) {
            assertArrayEquals(new double[]{INFTY, INFTY, INFTY, INFTY, INFTY, 0},
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
            Map<Integer, Set<Integer>> expectedShortestPath = new HashMap<>();
            assertEquals(expectedShortestPath,
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        } else {
            assertFwAndBwDiffBFSDistanceArrays(plan, new double[]{INFTY, INFTY, INFTY, INFTY, INFTY, 0},
                    new double[]{INFTY, INFTY, INFTY, 0, INFTY, INFTY});
            assertEquals(new HashSet<>(Arrays.asList()),
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).intersection);
            Map<Integer, Set<Integer>> expectedFWSP = new HashMap<>();
            assertEquals(expectedFWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).fwDiffBFS.shortestPath.shortestPath);
            Map<Integer, Set<Integer>> expectedBWSP = new HashMap<>();
            assertEquals(expectedBWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).bwDiffBFS.shortestPath.shortestPath);
        }
        assertEquals(Integer.MAX_VALUE, (int) plan.diffBFS.getSrcDstDistance());
    }

    /**
     * Test that creates a path that initially didn't exist
     */
    @Test
    public void testContinuousShortestPathsNoPath() throws IOException {
        Graph graph = Graph.getInstance();
        TestUtils.createEdgesPermanently(graph,
                new int[][]{new int[]{1, 5}, new int[]{2, 3}, new int[]{3, 4}, new int[]{4, 2}, new int[]{4, 3},
                        new int[]{4, 5}});
        ContinuousDiffBFSShortestPathPlan plan = registerContinuousSPPlan(1, 4);
        executor.addShortestPathPlan(plan);

        if (ExecutorType.UNI_UNWEIGHTED_DIFF_BFS == getExecutorType()) {
            assertArrayEquals(new double[]{INFTY, 0, INFTY, INFTY, INFTY, 1},
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
            Map<Integer, Set<Integer>> expectedShortestPath = new HashMap<>();
            assertEquals(expectedShortestPath,
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        } else {
            assertFwAndBwDiffBFSDistanceArrays(plan, new double[]{INFTY, 0, INFTY, INFTY, INFTY, 1},
                    new double[]{INFTY, INFTY, INFTY, 1, 0, INFTY});
            assertEquals(new HashSet<>(Arrays.asList()),
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).intersection);
            Map<Integer, Set<Integer>> expectedFWSP = new HashMap<>();
            assertEquals(expectedFWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).fwDiffBFS.shortestPath.shortestPath);
            Map<Integer, Set<Integer>> expectedBWSP = new HashMap<>();
            assertEquals(expectedBWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).bwDiffBFS.shortestPath.shortestPath);
        }
        assertEquals(Integer.MAX_VALUE, (int) plan.diffBFS.getSrcDstDistance());

        TestUtils.createEdgesTemporarily(graph, new int[][]{new int[]{5, 2}, new int[]{1, 6}});
        executor.execute();
        graph.finalizeChanges();

        if (ExecutorType.UNI_UNWEIGHTED_DIFF_BFS == getExecutorType()) {
            assertArrayEquals(new double[]{INFTY, 0, 2, 3, 4, 1, 1},
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
            Map<Integer, Set<Integer>> expectedShortestPath = new HashMap<>();
            expectedShortestPath.put(1, new HashSet<>());
            expectedShortestPath.put(5, new HashSet<>(Arrays.asList(1)));
            expectedShortestPath.put(2, new HashSet<>(Arrays.asList(5)));
            expectedShortestPath.put(3, new HashSet<>(Arrays.asList(2)));
            expectedShortestPath.put(4, new HashSet<>(Arrays.asList(3)));
            assertEquals(expectedShortestPath,
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        } else {
            assertFwAndBwDiffBFSDistanceArrays(plan, new double[]{INFTY, 0, 2, INFTY, INFTY, 1, 1},
                    new double[]{INFTY, INFTY, 2, 1, 0, INFTY, INFTY});
            assertEquals(new HashSet<>(Arrays.asList(2)),
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).intersection);
            Map<Integer, Set<Integer>> expectedFWSP = new HashMap<>();
            expectedFWSP.put(1, new HashSet<>());
            expectedFWSP.put(5, new HashSet<>(Arrays.asList(1)));
            expectedFWSP.put(2, new HashSet<>(Arrays.asList(5)));
            assertEquals(expectedFWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).fwDiffBFS.shortestPath.shortestPath);
            Map<Integer, Set<Integer>> expectedBWSP = new HashMap<>();
            expectedBWSP.put(4, new HashSet<>());
            expectedBWSP.put(3, new HashSet<>(Arrays.asList(4)));
            expectedBWSP.put(2, new HashSet<>(Arrays.asList(3)));
            assertEquals(expectedBWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).bwDiffBFS.shortestPath.shortestPath);
        }
        assertEquals(4, (int) plan.diffBFS.getSrcDstDistance());
    }

    /**
     * Test that reruns a BFS fully again.
     */
    @Test
    public void testLongerPathBFS() throws IOException {
        Graph graph = Graph.getInstance();
        TestUtils.createEdgesPermanently(graph,
                new int[][]{new int[]{1, 2}, new int[]{1, 5}, new int[]{2, 3}, new int[]{3, 4}, new int[]{4, 5},
                        new int[]{5, 6}});
        ContinuousDiffBFSShortestPathPlan plan = registerContinuousSPPlan(1, 6);
        executor.addShortestPathPlan(plan);

        if (ExecutorType.UNI_UNWEIGHTED_DIFF_BFS == getExecutorType()) {
            assertArrayEquals(new double[]{INFTY, 0, 1, 2, INFTY, 1, 2},
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
            Map<Integer, Set<Integer>> expectedShortestPath = new HashMap<>();
            expectedShortestPath.put(1, new HashSet<>());
            expectedShortestPath.put(5, new HashSet<>(Arrays.asList(1)));
            expectedShortestPath.put(6, new HashSet<>(Arrays.asList(5)));
            assertEquals(expectedShortestPath,
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        } else {
            assertFwAndBwDiffBFSDistanceArrays(plan, new double[]{INFTY, 0, 1, INFTY, INFTY, 1, INFTY},
                    new double[]{INFTY, INFTY, INFTY, INFTY, INFTY, 1, 0});
            assertEquals(new HashSet<>(Arrays.asList(5)),
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).intersection);
            Map<Integer, Set<Integer>> expectedFWSP = new HashMap<>();
            expectedFWSP.put(1, new HashSet<>());
            expectedFWSP.put(5, new HashSet<>(Arrays.asList(1)));
            assertEquals(expectedFWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).fwDiffBFS.shortestPath.shortestPath);
            Map<Integer, Set<Integer>> expectedBWSP = new HashMap<>();
            expectedBWSP.put(6, new HashSet<>());
            expectedBWSP.put(5, new HashSet<>(Arrays.asList(6)));
            assertEquals(expectedBWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).bwDiffBFS.shortestPath.shortestPath);
        }
        assertEquals(2, (int) plan.diffBFS.getSrcDstDistance());

        TestUtils.createEdgesTemporarily(graph, new int[][]{new int[]{1, 3}});
        TestUtils.deleteEdgesTemporarily(graph, new int[][]{new int[]{1, 5}});

        executor.execute();
        graph.finalizeChanges();

        if (ExecutorType.UNI_UNWEIGHTED_DIFF_BFS == getExecutorType()) {
            assertArrayEquals(new double[]{INFTY, 0, 1, 1, 2, 3, 4},
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
            Map<Integer, Set<Integer>> expectedShortestPath = new HashMap<>();
            expectedShortestPath.put(1, new HashSet<>());
            expectedShortestPath.put(3, new HashSet<>(Arrays.asList(1)));
            expectedShortestPath.put(4, new HashSet<>(Arrays.asList(3)));
            expectedShortestPath.put(5, new HashSet<>(Arrays.asList(4)));
            expectedShortestPath.put(6, new HashSet<>(Arrays.asList(5)));
            assertEquals(expectedShortestPath,
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        } else {
            assertFwAndBwDiffBFSDistanceArrays(plan, new double[]{INFTY, 0, 1, 1, 2, INFTY, INFTY},
                    new double[]{INFTY, INFTY, INFTY, INFTY, 2, 1, 0});
            assertEquals(new HashSet<>(Arrays.asList(4)),
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).intersection);
            Map<Integer, Set<Integer>> expectedFWSP = new HashMap<>();
            expectedFWSP.put(1, new HashSet<>());
            expectedFWSP.put(3, new HashSet<>(Arrays.asList(1)));
            expectedFWSP.put(4, new HashSet<>(Arrays.asList(3)));
            assertEquals(expectedFWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).fwDiffBFS.shortestPath.shortestPath);
            Map<Integer, Set<Integer>> expectedBWSP = new HashMap<>();
            expectedBWSP.put(6, new HashSet<>());
            expectedBWSP.put(5, new HashSet<>(Arrays.asList(6)));
            expectedBWSP.put(4, new HashSet<>(Arrays.asList(5)));
            assertEquals(expectedBWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).bwDiffBFS.shortestPath.shortestPath);
        }
        assertEquals(4, (int) plan.diffBFS.getSrcDstDistance());
    }

    /**
     * Test that reruns a BFS fully again
     */
    @Test
    public void testDeletion() throws IOException {
        Graph graph = Graph.getInstance();
        TestUtils.createEdgesPermanently(graph,
                new int[][]{new int[]{1, 2}, new int[]{1, 5}, new int[]{2, 3}, new int[]{3, 4}, new int[]{4, 5},
                        new int[]{5, 6}, new int[]{6, 7}, new int[]{7, 8}, new int[]{8, 9}, new int[]{9, 10}});
        ContinuousDiffBFSShortestPathPlan plan = registerContinuousSPPlan(1, 10);
        executor.addShortestPathPlan(plan);

        if (ExecutorType.UNI_UNWEIGHTED_DIFF_BFS == getExecutorType()) {
            assertArrayEquals(new double[]{INFTY, 0, 1, 2, 3, 1, 2, 3, 4, 5, 6},
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
            Map<Integer, Set<Integer>> expectedShortestPath = new HashMap<>();
            expectedShortestPath.put(1, new HashSet<>());
            expectedShortestPath.put(5, new HashSet<>(Arrays.asList(1)));
            expectedShortestPath.put(6, new HashSet<>(Arrays.asList(5)));
            expectedShortestPath.put(7, new HashSet<>(Arrays.asList(6)));
            expectedShortestPath.put(8, new HashSet<>(Arrays.asList(7)));
            expectedShortestPath.put(9, new HashSet<>(Arrays.asList(8)));
            expectedShortestPath.put(10, new HashSet<>(Arrays.asList(9)));
            assertEquals(expectedShortestPath,
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        } else {
            assertFwAndBwDiffBFSDistanceArrays(plan, new double[]{INFTY, 0, 1, 2, 3, 1, 2, 3, INFTY, INFTY, INFTY},
                    new double[]{INFTY, INFTY, INFTY, INFTY, INFTY, INFTY, INFTY, 3, 2, 1, 0});
            assertEquals(new HashSet<>(Arrays.asList(7)),
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).intersection);
            Map<Integer, Set<Integer>> expectedFWSP = new HashMap<>();
            expectedFWSP.put(1, new HashSet<>());
            expectedFWSP.put(5, new HashSet<>(Arrays.asList(1)));
            expectedFWSP.put(6, new HashSet<>(Arrays.asList(5)));
            expectedFWSP.put(7, new HashSet<>(Arrays.asList(6)));
            assertEquals(expectedFWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).fwDiffBFS.shortestPath.shortestPath);
            Map<Integer, Set<Integer>> expectedBWSP = new HashMap<>();
            expectedBWSP.put(10, new HashSet<>());
            expectedBWSP.put(9, new HashSet<>(Arrays.asList(10)));
            expectedBWSP.put(8, new HashSet<>(Arrays.asList(9)));
            expectedBWSP.put(7, new HashSet<>(Arrays.asList(8)));
            assertEquals(expectedBWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).bwDiffBFS.shortestPath.shortestPath);
        }
        assertEquals(6, (int) plan.diffBFS.getSrcDstDistance());

        TestUtils.createEdgesTemporarily(graph, new int[][]{new int[]{6, 10}});
        TestUtils.deleteEdgesTemporarily(graph, new int[][]{new int[]{1, 5}});

        executor.execute();
        graph.finalizeChanges();
        if (ExecutorType.UNI_UNWEIGHTED_DIFF_BFS == getExecutorType()) {
            assertArrayEquals(new double[]{INFTY, 0, 1, 2, 3, 4, 5, 6, INFTY, INFTY, 6},
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).getDistancesAsArray(), 0.0 /* delta */);
            Map<Integer, Set<Integer>> expectedShortestPath = new HashMap<>();
            expectedShortestPath.put(1, new HashSet<>());
            expectedShortestPath.put(2, new HashSet<>(Arrays.asList(1)));
            expectedShortestPath.put(3, new HashSet<>(Arrays.asList(2)));
            expectedShortestPath.put(4, new HashSet<>(Arrays.asList(3)));
            expectedShortestPath.put(5, new HashSet<>(Arrays.asList(4)));
            expectedShortestPath.put(6, new HashSet<>(Arrays.asList(5)));
            expectedShortestPath.put(10, new HashSet<>(Arrays.asList(6)));
            assertEquals(expectedShortestPath,
                    ((NewUnidirectionalDifferentialBFS) plan.diffBFS).shortestPath.shortestPath);
        } else {
            assertFwAndBwDiffBFSDistanceArrays(plan,
                    new double[]{INFTY, 0, 1, 2, 3, INFTY, INFTY, INFTY, INFTY, INFTY, INFTY},
                    new double[]{INFTY, INFTY, INFTY, INFTY, 3, 2, 1, 3, 2, 1, 0});
            assertEquals(new HashSet<>(Arrays.asList(4)),
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).intersection);
            Map<Integer, Set<Integer>> expectedFWSP = new HashMap<>();
            expectedFWSP.put(1, new HashSet<>());
            expectedFWSP.put(2, new HashSet<>(Arrays.asList(1)));
            expectedFWSP.put(3, new HashSet<>(Arrays.asList(2)));
            expectedFWSP.put(4, new HashSet<>(Arrays.asList(3)));
            assertEquals(expectedFWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).fwDiffBFS.shortestPath.shortestPath);
            Map<Integer, Set<Integer>> expectedBWSP = new HashMap<>();
            expectedBWSP.put(10, new HashSet<>());
            expectedBWSP.put(6, new HashSet<>(Arrays.asList(10)));
            expectedBWSP.put(5, new HashSet<>(Arrays.asList(6)));
            expectedBWSP.put(4, new HashSet<>(Arrays.asList(5)));
            assertEquals(expectedBWSP,
                    ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).bwDiffBFS.shortestPath.shortestPath);
        }
        assertEquals(6, (int) plan.diffBFS.getSrcDstDistance());
    }

    public ContinuousDiffBFSShortestPathPlan registerContinuousSPPlan(int src, int dst) throws IOException {
        return registerContinuousSPPlan(src, dst, getExecutorType());
    }

    abstract ExecutorType getExecutorType();

    private void assertFwAndBwDiffBFSDistanceArrays(ContinuousDiffBFSShortestPathPlan plan, double[] fwDistanceArray,
                                                    double[] bwDistanceArray) {
        assertArrayEquals(fwDistanceArray,
                ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).fwDiffBFS.getDistancesAsArray(), 0.0 /* delta */);
        assertArrayEquals(bwDistanceArray,
                ((NewBidirUnweightedDifferentialBFS) plan.diffBFS).bwDiffBFS.getDistancesAsArray(), 0.0 /* delta */);
    }
}
