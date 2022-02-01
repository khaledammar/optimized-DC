package ca.waterloo.dsg.graphflow.query.executors;

import ca.waterloo.dsg.graphflow.TestUtils;
import ca.waterloo.dsg.graphflow.exceptions.NoSuchVertexIDException;
import ca.waterloo.dsg.graphflow.graph.Graph.Direction;
import ca.waterloo.dsg.graphflow.graph.GraphDBState;
import ca.waterloo.dsg.graphflow.query.operator.InMemoryOutputSink;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

/**
 * Tests {@link ShortestPathExecutor}.
 */
public class ShortestPathExecutorTest {

    private ShortestPathExecutor executor = ShortestPathExecutor.getInstance();

    @Before
    public void setUp() throws Exception {
        GraphDBState.reset();
        int[][] edges =
                {{0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 4}, {2, 5}, {3, 6}, {4, 6}, {4, 7}, {5, 7}, {6, 8}, {6, 9}, {7, 9},
                        {7, 10}, {8, 11}, {9, 11}, {10, 11}};
        short[] edgeTypes = {2, 4, 6, 8, 8, 10, 12, 12, 14, 14, 16, 18, 18, 20, 22, 22, 22};
        short[][] vertexTypes =
                {{0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 4}, {2, 5}, {3, 6}, {4, 6}, {4, 7}, {5, 7}, {6, 8}, {6, 9}, {7, 9},
                        {7, 10}, {8, 11}, {9, 11}, {10, 11}};
        TestUtils.initializeGraphPermanently(edges, edgeTypes, vertexTypes);
        executor = ShortestPathExecutor.getInstance();
    }

    @Test
    public void testEvaluateQuerySource0Target9() throws Exception {
        InMemoryOutputSink actualInMemoryOutputSink = new InMemoryOutputSink();
        executor.execute(0, 9, actualInMemoryOutputSink);

        Map<Integer, Set<Integer>> expectedResults = new HashMap<>();
        expectedResults.put(0, new HashSet<>(Arrays.asList(new Integer[]{1, 2})));
        expectedResults.put(1, new HashSet<>(Arrays.asList(new Integer[]{3, 4})));
        expectedResults.put(2, new HashSet<>(Arrays.asList(new Integer[]{4, 5})));
        expectedResults.put(3, new HashSet<>(Arrays.asList(new Integer[]{6})));
        expectedResults.put(4, new HashSet<>(Arrays.asList(new Integer[]{6, 7})));
        expectedResults.put(5, new HashSet<>(Arrays.asList(new Integer[]{7})));
        expectedResults.put(6, new HashSet<>(Arrays.asList(new Integer[]{9})));
        expectedResults.put(7, new HashSet<>(Arrays.asList(new Integer[]{9})));
        InMemoryOutputSink expectedInMemoryOutputSink = new InMemoryOutputSink();
        expectedInMemoryOutputSink.append(ShortestPathExecutor.getStringOutput(expectedResults));

        Assert.assertTrue(InMemoryOutputSink.isSameAs(actualInMemoryOutputSink, expectedInMemoryOutputSink));
    }

    @Test
    public void testBackTrackIntersectionSource0Target9() throws Exception {
        short[] visitedLevels = new short[]{1, 2, 2, 3, 3, 3, -2, -2, 0, -1, 0, 0};
        int[] visitedQueryId = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0};
        int queryId = 1;
        executor = new ShortestPathExecutor(visitedLevels, visitedQueryId, queryId);
        Map<Integer, Set<Integer>> actualResults = new HashMap<>();
        Set<Integer> intersectVertices = new HashSet<>();
        intersectVertices.add(6);
        intersectVertices.add(7);
        executor.backTrackIntersection(intersectVertices, Direction.BACKWARD, (short) 4, actualResults);

        Map<Integer, Set<Integer>> expectedResults = new HashMap<>();
        expectedResults.put(0, new HashSet<>(Arrays.asList(new Integer[]{1, 2})));
        expectedResults.put(1, new HashSet<>(Arrays.asList(new Integer[]{3, 4})));
        expectedResults.put(2, new HashSet<>(Arrays.asList(new Integer[]{4, 5})));
        expectedResults.put(3, new HashSet<>(Arrays.asList(new Integer[]{6})));
        expectedResults.put(4, new HashSet<>(Arrays.asList(new Integer[]{6, 7})));
        expectedResults.put(5, new HashSet<>(Arrays.asList(new Integer[]{7})));

        Assert.assertTrue(expectedResults.equals(actualResults));
    }

    @Test
    public void testEvaluateQueryWithNonExistentSource() throws Exception {
        int source = 100;
        int target = 0;
        try {
            executor.execute(source, target, new InMemoryOutputSink());
            Assert.fail("The specified vertexID " + source + " does not exist.");
        } catch (NoSuchVertexIDException e) {
            // Expected exception caught.
        }
    }
}
