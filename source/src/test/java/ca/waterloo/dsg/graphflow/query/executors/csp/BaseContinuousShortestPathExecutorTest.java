package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.ExecutorType;
import ca.waterloo.dsg.graphflow.Experiment;
import ca.waterloo.dsg.graphflow.TestUtils;
import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.GraphDBState;
import ca.waterloo.dsg.graphflow.query.executors.csp.Distances.VertexIterationDistancePair;
import ca.waterloo.dsg.graphflow.query.parser.StructuredQueryParser;
import ca.waterloo.dsg.graphflow.query.planner.ContinuousShortestPathPlanner;
import ca.waterloo.dsg.graphflow.query.plans.ContinuousDiffBFSShortestPathPlan;
import ca.waterloo.dsg.graphflow.query.plans.ContinuousShortestPathPlan;
import ca.waterloo.dsg.graphflow.query.structuredquery.StructuredQuery;
import ca.waterloo.dsg.graphflow.util.Report;
import org.junit.After;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static ca.waterloo.dsg.graphflow.ExecutorType.isDC;
import static org.junit.Assert.*;

/**
 * Base class for continuous shortest paths tests.
 *
 * @author ssalihog
 */
public class BaseContinuousShortestPathExecutorTest {
    protected static double INFTY = Double.MAX_VALUE;
    // Special JUnit defined temporary folder used to test I/O operations on files. Requires
    // {@code public} visibility.
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    protected ContinuousShortestPathsExecutor executor = ContinuousShortestPathsExecutor.getInstance();

    public static void runQueriesAndAssert(int[][] edges, double[] weights, short[] edgeTypes, short[][] vertexTypes,
                                           boolean isWeighted, List<Experiment.EdgeBatch> data, int[][] queries,
                                           ExecutorType[] executorTypes, List<List<Long>> srcDstDistancesExpected)
            throws IOException {
        for (ExecutorType executorType : executorTypes) {
            DistancesWithDropBloom.DropType[] dropTypes = new DistancesWithDropBloom.DropType[]{DistancesWithDropBloom.DropType.SELECTIVE};
            if (ExecutorType.isDetOrProb(executorType)) {
                dropTypes = new DistancesWithDropBloom.DropType[]{DistancesWithDropBloom.DropType.SELECTIVE, DistancesWithDropBloom.DropType.RANDOM};
            }
            for (var dropType : dropTypes) {
                runQueryAndAssert(edges, weights, edgeTypes, vertexTypes, isWeighted, data, queries, executorType,
                        dropType, srcDstDistancesExpected);
            }
        }
    }

    public static void runQueryAndAssert(int[][] edges, double[] weights, short[] edgeTypes, short[][] vertexTypes,
                                         boolean isWeighted, List<Experiment.EdgeBatch> data, int[][] queries,
                                         ExecutorType executorType, DistancesWithDropBloom.DropType dropType,
                                         List<List<Long>> srcDstDistancesExpected) throws IOException {
        GraphDBState.reset2();
        Report.INSTANCE.setLevel(Report.Level.ERROR);
        Graph.IS_WEIGHTED = isWeighted;
        if (isWeighted) {
            TestUtils.initializeGraphPermanently(edges, weights, edgeTypes, vertexTypes);
        } else {
            TestUtils.initializeGraphPermanently(edges, edgeTypes, vertexTypes);
        }

        Distances.numQueries = queries.length;
        for (int i = 0; i < queries.length; i++) {
            var query = "CONTINUOUSLY SHORTEST PATH (" + queries[i][0] + ", " + queries[i][1] +
                    ") WEIGHTS ON WEIGHT FILE '/dev/null'";
            var structuredQuery = new StructuredQueryParser().parse(query);
            ContinuousShortestPathsExecutor.getInstance().addShortestPathPlan(
                    (ContinuousShortestPathPlan) new ContinuousShortestPathPlanner(structuredQuery)
                            .plan(i + 1, executorType, false, 0.5F, dropType, "Query", 2, 13,10));
        }

        List<List<Long>> srcDstDistances = new ArrayList<>();

        List<Long> distances = new ArrayList<>();
        for (ContinuousShortestPathPlan shortestPathPlan : ContinuousShortestPathsExecutor.getShortestPathPlans()) {
            distances.add(shortestPathPlan.getSrcDstDistance());
        }
        srcDstDistances.add(distances);

        for (Experiment.EdgeBatch edgeBatch : data) {
            Graph.INSTANCE.edgeDiffs.clear();
            for (int j = 0; j < edgeBatch.currentEdgeIndex; j++) {
                var source = edgeBatch.fromVertexId[j];
                var dest = edgeBatch.toVertexId[j];
                double weight = edgeBatch.weight[j];
                short type = 0;
                short diff;
                if (edgeBatch.type[j] == Experiment.BatchType.ADDITION) {
                    if (isWeighted) {
                        Graph.INSTANCE.addEdgeTemporarily(source, dest, type, type, weight, type);
                    } else {

                        Graph.INSTANCE.addEdgeTemporarily(source, dest, type, type, type);
                    }
                    diff = 1;
                } else {
                    Graph.INSTANCE
                            .deleteEdgeTemporarily(edgeBatch.fromVertexId[j], edgeBatch.toVertexId[j], (short) -1);
                    diff = -1;
                }
                if (isDC(executorType)) {
                    var entry = Graph.INSTANCE.edgeDiffs.computeIfAbsent(source, k -> new HashMap<>());
                    var diffs = entry.computeIfAbsent(dest, k -> new DistancesDC.Diff2((short) 0));
                    diffs.addDiff(weight, diff, type);
                }
            }
            ContinuousShortestPathsExecutor.getInstance().execute();
            Graph.INSTANCE.finalizeChanges();

            distances = new ArrayList<>();
            for (ContinuousShortestPathPlan shortestPathPlan : ContinuousShortestPathsExecutor.getShortestPathPlans()) {
                distances.add(shortestPathPlan.getSrcDstDistance());
            }
            srcDstDistances.add(distances);
        }

        for (int i = 0; i < srcDstDistances.size(); i++) {
            assertEquals("Query=" + i + ", et=" + executorType + ", dt=" + dropType + ": results failed:",
                    srcDstDistancesExpected.get(i), srcDstDistances.get(i));
        }
    }

    public ContinuousDiffBFSShortestPathPlan registerContinuousSPPlan(int src, int dst, ExecutorType executorType)
            throws IOException {
        String continuousShortestPathQuery = "CONTINUOUSLY SHORTEST PATH (" + src + "," + dst + ") FILE '/dev/null';";
        StructuredQuery structuredQuery = new StructuredQueryParser().parse(continuousShortestPathQuery);
        ContinuousShortestPathPlanner planner = new ContinuousShortestPathPlanner(structuredQuery);
        return (ContinuousDiffBFSShortestPathPlan) planner.plan(0, executorType, true /* backtrack */);
    }

    /**
     * Note: This method assumes that the input expectdDistances given as a double array has the same size
     * for each double array in it.
     */
    protected void assertDistances(double[][] expectedDistances, Distances actualDistances, int[][] minFrontierVertices,
                                   double[] minFrontierDistances, boolean isLastFrontierEmpty) {
        assertEquals(expectedDistances[0].length, actualDistances.latestIteration + 1);
        for (int iterationNo = 0; iterationNo < expectedDistances[0].length; ++iterationNo) {
            for (int vertexId = 0; vertexId < expectedDistances.length; ++vertexId) {
                double expectedDistance = expectedDistances[vertexId][iterationNo];
                System.out.println("iterationNo: " + iterationNo + " vertexId: " + vertexId);
                assertEquals(expectedDistance, actualDistances.getDistance(vertexId, (short) iterationNo),
                        0.0 /* delta */);
            }
            if (iterationNo >= expectedDistances[0].length - 1) {
                continue;
            }
            assertFalse(actualDistances.isFrontierEmpty(iterationNo));
            VertexIterationDistancePair minFrontiervIterDistPair = actualDistances.minFrontierDistances[iterationNo];
            assertEquals(iterationNo, minFrontiervIterDistPair.iterDistPair.iterationNo);
            assertEquals(minFrontierDistances[iterationNo], minFrontiervIterDistPair.iterDistPair.distance,
                    0.0 /* delta */);
            boolean minDistVIsContained = false;
            System.out.println("minFrontiervIterDistPair.vertexId: " + minFrontiervIterDistPair.vertexId);
            for (int minDistanceVertex : minFrontierVertices[iterationNo]) {
                if (minDistanceVertex == minFrontiervIterDistPair.vertexId) {
                    minDistVIsContained = true;
                }
            }
            assertTrue(minDistVIsContained);
        }
        if (isLastFrontierEmpty) {
            assertTrue(actualDistances.isFrontierEmpty(expectedDistances[0].length));
        }
    }

    /**
     * Removes all registered CONTINUOUSLY shortest path queries queries after the test
     */
    @After
    public void resetContinuousMatchQueries() {
        executor.reset();
    }
}
