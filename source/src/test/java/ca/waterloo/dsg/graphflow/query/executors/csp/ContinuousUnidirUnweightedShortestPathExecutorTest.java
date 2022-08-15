package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.ExecutorType;
import ca.waterloo.dsg.graphflow.TestUtils;
import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.query.executors.csp.Distances.VertexIterationDistancePair;
import ca.waterloo.dsg.graphflow.query.plans.ContinuousDiffBFSShortestPathPlan;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Tests for continuous undirected and unweighted differential BFS.
 */
public class ContinuousUnidirUnweightedShortestPathExecutorTest
        extends BaseContinuousUnidirBidirUnweightedShortestPathExecutorTest {

    /**
     * Tests that the min distance vertex of any frontier until the latest iteration is set.
     */
    @Test
    public void testMinDistanceFrontierIsSetUntilLatestIteration() throws IOException {
        Graph graph = Graph.getInstance();
        TestUtils.createEdgesPermanently(graph,
                new int[][]{new int[]{1, 2}, new int[]{2, 3}, new int[]{3, 4}, new int[]{4, 5}, new int[]{5, 6},
                        new int[]{6, 7}, new int[]{7, 8}});
        ContinuousDiffBFSShortestPathPlan plan = registerContinuousSPPlan(1, 7);
        executor.addShortestPathPlan(plan);

        Distances distances = ((NewUnidirectionalDifferentialBFS) plan.diffBFS).distances;
        assertEquals(6, distances.latestIteration);
        VertexIterationDistancePair[] minFrontierDistances = distances.minFrontierDistances;
        for (int i = 0; i <= 7; ++i) {
            VertexIterationDistancePair vertexIterDistPair = minFrontierDistances[i];
            if (i <= 6) {
                assertNotNull(vertexIterDistPair);
                assertEquals(i + 1, vertexIterDistPair.vertexId);
                assertEquals(i, vertexIterDistPair.iterDistPair.iterationNo);
                assertEquals((double) i, vertexIterDistPair.iterDistPair.distance, 0.0 /* delta */);
            } else {
                assertNull(vertexIterDistPair);
            }
        }

        TestUtils.deleteEdgesTemporarily(graph, new int[][]{new int[]{3, 4}});
        executor.execute();
        graph.finalizeChanges();
        assertEquals(3, distances.latestIteration);

        for (int i = 0; i <= 7; ++i) {
            VertexIterationDistancePair vertexIterDistPair = minFrontierDistances[i];
            if (i <= 2) {
                assertNotNull(vertexIterDistPair);
                assertEquals(i + 1, vertexIterDistPair.vertexId);
                assertEquals(i, vertexIterDistPair.iterDistPair.iterationNo);
                assertEquals((double) i, vertexIterDistPair.iterDistPair.distance, 0.0 /* delta */);
            } else {
                assertNull(vertexIterDistPair);
            }
        }

        TestUtils.createEdgesTemporarily(graph, new int[][]{new int[]{3, 4}});
        TestUtils.deleteEdgesTemporarily(graph, new int[][]{new int[]{4, 5}});
        executor.execute();
        graph.finalizeChanges();
        assertEquals(4, distances.latestIteration);

        for (int i = 0; i <= 7; ++i) {
            VertexIterationDistancePair vertexIterDistPair = minFrontierDistances[i];
            if (i <= 3) {
                assertNotNull(vertexIterDistPair);
                assertEquals(i + 1, vertexIterDistPair.vertexId);
                assertEquals(i, vertexIterDistPair.iterDistPair.iterationNo);
                assertEquals((double) i, vertexIterDistPair.iterDistPair.distance, 0.0 /* delta */);
            } else {
                assertNull(vertexIterDistPair);
            }
        }

        TestUtils.createEdgesTemporarily(graph, new int[][]{new int[]{4, 5}});
        executor.execute();
        graph.finalizeChanges();

        assertEquals(6, distances.latestIteration);
        for (int i = 0; i <= 7; ++i) {
            VertexIterationDistancePair vertexIterDistPair = minFrontierDistances[i];
            if (i <= 6) {
                assertNotNull(vertexIterDistPair);
                assertEquals(i + 1, vertexIterDistPair.vertexId);
                assertEquals(i, vertexIterDistPair.iterDistPair.iterationNo);
                assertEquals((double) i, vertexIterDistPair.iterDistPair.distance, 0.0 /* delta */);
            } else {
                assertNull(vertexIterDistPair);
            }
        }
    }

    /**
     * Test that the distance of each vertex is set correctly.
     */
    @Test
    public void testDistances() throws IOException {
        Graph graph = Graph.getInstance();
        TestUtils.createEdgesPermanently(graph,
                new int[][]{new int[]{1, 2}, new int[]{2, 3}, new int[]{2, 6}, new int[]{2, 8}, new int[]{3, 4},
                        new int[]{4, 5}, new int[]{6, 7}});
        ContinuousDiffBFSShortestPathPlan plan = registerContinuousSPPlan(2, 4);
        executor.addShortestPathPlan(plan);

        Distances distances = ((NewUnidirectionalDifferentialBFS) plan.diffBFS).distances;
        // Assert for each vertex u each method m of Distances that returns the distance of u.
        // v1:
        short expectedLatestIterNo = 2;
        assertEquals(expectedLatestIterNo, distances.latestIteration);
        checkDistancesIsCorrect(distances, 1 /* vId */, INFTY, expectedLatestIterNo);
        checkDistancesIsCorrect(distances, 2 /* vId */, 0.0, expectedLatestIterNo);
        // v3, v6, v8:
        for (int vertexId : new int[]{3, 6, 8}) {
            checkDistancesIsCorrect(distances, vertexId /* vId */, 1.0, expectedLatestIterNo);
        }
        // v4, v7:
        for (int vertexId : new int[]{4, 7}) {
            checkDistancesIsCorrect(distances, vertexId /* vId */, 2.0, expectedLatestIterNo);
        }
        checkDistancesIsCorrect(distances, 5 /* vId */, 3.0, expectedLatestIterNo);

        TestUtils.deleteEdgesTemporarily(graph, new int[][]{new int[]{3, 4}});
        executor.execute();
        graph.finalizeChanges();
        expectedLatestIterNo = 3;
        assertEquals(expectedLatestIterNo, distances.latestIteration);
        for (int vertexId : new int[]{1, 4, 5}) {
            checkDistancesIsCorrect(distances, vertexId /* vId */, INFTY, expectedLatestIterNo);
        }
        checkDistancesIsCorrect(distances, 2 /* vId */, 0.0, expectedLatestIterNo);
        // v3, v6, v8:
        for (int vertexId : new int[]{3, 6, 8}) {
            checkDistancesIsCorrect(distances, vertexId /* vId */, 1.0, expectedLatestIterNo);
        }
        checkDistancesIsCorrect(distances, 7 /* vId */, 2.0, expectedLatestIterNo);

        TestUtils.createEdgesTemporarily(graph, new int[][]{new int[]{7, 4}});
        executor.execute();
        graph.finalizeChanges();
        expectedLatestIterNo = 3;
        assertEquals(expectedLatestIterNo, distances.latestIteration);
        checkDistancesIsCorrect(distances, 1 /* vId */, INFTY, expectedLatestIterNo);
        checkDistancesIsCorrect(distances, 2 /* vId */, 0.0, expectedLatestIterNo);
        // v3, v6, v8:
        for (int vertexId : new int[]{3, 6, 8}) {
            checkDistancesIsCorrect(distances, vertexId /* vId */, 1.0, expectedLatestIterNo);
        }
        checkDistancesIsCorrect(distances, 7 /* vId */, 2.0, expectedLatestIterNo);
        checkDistancesIsCorrect(distances, 4 /* vId */, 3.0, expectedLatestIterNo);
        checkDistancesIsCorrect(distances, 5 /* vId */, 4.0, expectedLatestIterNo);

        TestUtils.deleteEdgesTemporarily(graph, new int[][]{new int[]{2, 6}});
        TestUtils.createEdgesTemporarily(graph, new int[][]{new int[]{8, 9}});
        TestUtils.createEdgesTemporarily(graph, new int[][]{new int[]{9, 7}});
        executor.execute();
        graph.finalizeChanges();
        expectedLatestIterNo = 4;
        assertEquals(expectedLatestIterNo, distances.latestIteration);
        for (int vertexId : new int[]{1, 6}) {
            checkDistancesIsCorrect(distances, vertexId /* vId */, INFTY, expectedLatestIterNo);
        }
        checkDistancesIsCorrect(distances, 2 /* vId */, 0.0, expectedLatestIterNo);
        // v3, v6, v8:
        for (int vertexId : new int[]{3, 8}) {
            checkDistancesIsCorrect(distances, vertexId /* vId */, 1.0, expectedLatestIterNo);
        }
        checkDistancesIsCorrect(distances, 9 /* vId */, 2.0, expectedLatestIterNo);
        checkDistancesIsCorrect(distances, 7 /* vId */, 3.0, expectedLatestIterNo);
        checkDistancesIsCorrect(distances, 4 /* vId */, 4.0, expectedLatestIterNo);
        checkDistancesIsCorrect(distances, 5 /* vId */, 5.0, expectedLatestIterNo);

        TestUtils.createEdgesTemporarily(graph, new int[][]{new int[]{10, 5}});
        executor.execute();
        graph.finalizeChanges();
        expectedLatestIterNo = 4;
        assertEquals(expectedLatestIterNo, distances.latestIteration);
        for (int vertexId : new int[]{1, 6, 10}) {
            checkDistancesIsCorrect(distances, vertexId /* vId */, INFTY, expectedLatestIterNo);
        }
        checkDistancesIsCorrect(distances, 2 /* vId */, 0.0, expectedLatestIterNo);
        // v3, v6, v8:
        for (int vertexId : new int[]{3, 8}) {
            checkDistancesIsCorrect(distances, vertexId /* vId */, 1.0, expectedLatestIterNo);
        }
        checkDistancesIsCorrect(distances, 9 /* vId */, 2.0, expectedLatestIterNo);
        checkDistancesIsCorrect(distances, 7 /* vId */, 3.0, expectedLatestIterNo);
        checkDistancesIsCorrect(distances, 4 /* vId */, 4.0, expectedLatestIterNo);
        checkDistancesIsCorrect(distances, 5 /* vId */, 5.0, expectedLatestIterNo);

        TestUtils.createEdgesTemporarily(graph, new int[][]{new int[]{2, 4}, new int[]{3, 4}});
        executor.execute();
        graph.finalizeChanges();
        expectedLatestIterNo = 1;
        assertEquals(expectedLatestIterNo, distances.latestIteration);
        for (int vertexId : new int[]{1, 6, 10}) {
            checkDistancesIsCorrect(distances, vertexId /* vId */, INFTY, expectedLatestIterNo);
        }
        checkDistancesIsCorrect(distances, 2 /* vId */, 0.0, expectedLatestIterNo);
        // v3, v6, v8:
        for (int vertexId : new int[]{3, 4, 8}) {
            checkDistancesIsCorrect(distances, vertexId /* vId */, 1.0, expectedLatestIterNo);
        }
        for (int vertexId : new int[]{5, 9}) {
            checkDistancesIsCorrect(distances, vertexId /* vId */, 2.0, expectedLatestIterNo);
        }
        checkDistancesIsCorrect(distances, 7 /* vId */, 3.0, expectedLatestIterNo);
    }

    private void checkDistancesIsCorrect(Distances distances, int vertexId, Double vertexDistInGraph,
                                         short latestIteration) {
        if (INFTY == vertexDistInGraph || vertexDistInGraph > latestIteration) {
            assertEquals(0, distances.getAllDistances(vertexId).size());
        } else {
            assertEquals(1, distances.getAllDistances(vertexId).size());
        }
        for (short i = 0; i <= latestIteration; ++i) {
            if (i < vertexDistInGraph) {
                assertEquals(INFTY, distances.getDistance(vertexId, i), 0.0 /* delta */);
            } else {
                assertEquals(vertexDistInGraph, distances.getDistance(vertexId, i), 0.0 /* delta */);
            }
        }
        Double expectedDistAfterLatestIter = vertexDistInGraph <= latestIteration ? vertexDistInGraph : INFTY;
        for (short i = latestIteration; i <= latestIteration + 2; ++i) {
            assertEquals(expectedDistAfterLatestIter, distances.getDistance(vertexId, i), 0.0 /* delta */);
        }
    }

    /**
     * Tests that the min distance vertex of any frontier until the latest iteration is set.
     * The graph we use below is a chain graph that starts from vertex 3 and goes until vertex 10,
     * where from vertex 5 another chain that is 5->11->12. There is also an edge from 6 to 5.
     * The registered continuous query is from 5 to 6. Later we add other vertices to the 5->12
     * chain and connect it to the main chain.
     */
    @Test
    public void testMinDistFrontierIsSetCorrectly() throws IOException {
        Graph graph = Graph.getInstance();
        TestUtils.createEdgesPermanently(graph,
                new int[][]{new int[]{3, 4}, new int[]{4, 5}, new int[]{5, 6}, new int[]{6, 5}, new int[]{6, 7},
                        new int[]{5, 11}, new int[]{11, 12}, new int[]{7, 8}, new int[]{8, 9}, new int[]{9, 10}});
        ContinuousDiffBFSShortestPathPlan plan = registerContinuousSPPlan(5, 6);
        executor.addShortestPathPlan(plan);
        Distances distances = ((NewUnidirectionalDifferentialBFS) plan.diffBFS).distances;
        VertexIterationDistancePair[] minFrontierDistances = distances.minFrontierDistances;
        verifyMinDistFrontierAndLatestIterationInTheBeginning(distances, minFrontierDistances);

        TestUtils.deleteEdgesTemporarily(graph, new int[][]{new int[]{5, 6}});
        executor.execute();
        graph.finalizeChanges();
        assertEquals(3, distances.latestIteration);

        for (int i = 0; i <= 7; ++i) {
            VertexIterationDistancePair vertexIterDistPair = minFrontierDistances[i];
            if (i <= 2) {
                assertNotNull(vertexIterDistPair);
                if (i == 0) {
                    assertEquals(5, vertexIterDistPair.vertexId);
                } else if (i == 1) {
                    assertEquals(11, vertexIterDistPair.vertexId);
                } else if (i == 2) {
                    assertEquals(12, vertexIterDistPair.vertexId);
                }
                assertEquals(i, vertexIterDistPair.iterDistPair.iterationNo);
                assertEquals((double) i, vertexIterDistPair.iterDistPair.distance, 0.0 /* delta */);
            } else {
                assertNull(vertexIterDistPair);
            }
        }

        TestUtils.createEdgesTemporarily(graph, new int[][]{new int[]{5, 6}});
        executor.execute();
        graph.finalizeChanges();
        // We are back to the original graph, so we assert the same conditions we did
        // initially in this test.
        verifyMinDistFrontierAndLatestIterationInTheBeginning(distances, minFrontierDistances);

        TestUtils.deleteEdgesTemporarily(graph, new int[][]{new int[]{5, 6}});
        TestUtils.createEdgesTemporarily(graph, new int[][]{new int[]{12, 8}});
        TestUtils.createEdgesTemporarily(graph, new int[][]{new int[]{8, 7}});
        TestUtils.createEdgesTemporarily(graph, new int[][]{new int[]{7, 6}});
        executor.execute();
        graph.finalizeChanges();
        assertEquals(5, distances.latestIteration);

        for (int i = 0; i <= 7; ++i) {
            VertexIterationDistancePair vertexIterDistPair = minFrontierDistances[i];
            if (i <= 5) {
                assertNotNull(vertexIterDistPair);
                if (1 == i) {
                    assertEquals(11, vertexIterDistPair.vertexId);
                } else if (2 == i) {
                    assertEquals(12, vertexIterDistPair.vertexId);
                } else if (3 == i) {
                    assertEquals(8, vertexIterDistPair.vertexId);
                } else if (4 == i) {
                    assertEquals(7, vertexIterDistPair.vertexId);
                } else if (5 == i) {
                    assertEquals(6, vertexIterDistPair.vertexId);
                }
                assertEquals(i, vertexIterDistPair.iterDistPair.iterationNo);
                assertEquals((double) i, vertexIterDistPair.iterDistPair.distance, 0.0 /* delta */);
            } else {
                assertNull(vertexIterDistPair);
            }
        }
    }

    private void verifyMinDistFrontierAndLatestIterationInTheBeginning(Distances distances,
                                                                       VertexIterationDistancePair[] minFrontierDistances) {
        assertEquals(1, distances.latestIteration);
        for (int i = 0; i <= 7; ++i) {
            VertexIterationDistancePair vertexIterDistPair = minFrontierDistances[i];
            if (i <= 1) {
                assertNotNull(vertexIterDistPair);
                if (i == 1) {
                    // There are 2 vertices that have distance 1: 6 and 11. We verify that one
                    // of them is set as the minimim vertex in the first frontier.
                    assertTrue(6 == vertexIterDistPair.vertexId || 11 == vertexIterDistPair.vertexId);
                } else {
                    assertEquals(5 + i, vertexIterDistPair.vertexId);
                }
                assertEquals(i, vertexIterDistPair.iterDistPair.iterationNo);
                assertEquals((double) i, vertexIterDistPair.iterDistPair.distance, 0.0 /* delta */);
            } else {
                assertNull(vertexIterDistPair);
            }
        }
    }

    @Override
    ExecutorType getExecutorType() {
        return ExecutorType.UNI_UNWEIGHTED_DIFF_BFS;
    }
}
