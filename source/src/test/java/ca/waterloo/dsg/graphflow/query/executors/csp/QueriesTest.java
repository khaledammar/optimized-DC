package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.ExecutorType;
import ca.waterloo.dsg.graphflow.Experiment;
import ca.waterloo.dsg.graphflow.graph.TypeAndPropertyKeyStore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static ca.waterloo.dsg.graphflow.query.executors.csp.BaseContinuousShortestPathExecutorTest.runQueriesAndAssert;

public class QueriesTest {

    @Test
    public void testQueries() throws Exception {
        testKHOP();
        testSPSP();
        testQ1();
        testQ2();
    }

    void testKHOP() throws Exception {
        int[][] edges =
                {{0, 10}, {10, 11}, {11, 12}, {12, 13}, {13, 12}, {10, 13}, {10, 14}, {13, 14}, {12, 15}, {15, 16},
                        {16, 17}, {10, 20}, {20, 30}, {30, 40}, {40, 50}, {50, 60}, {60, 70}, {70, 11}};
        double[] weights = null;
        short[] edgeTypes = new short[edges.length];
        short[][] vertexTypes = new short[edges.length][2];
        for (int i = 0; i < vertexTypes.length; i++) {
            vertexTypes[i] = new short[]{0, 0};
        }

        List<Experiment.EdgeBatch> data = new ArrayList<>();

        Experiment.EdgeBatch nextBatch = new Experiment.EdgeBatch(1);
        nextBatch.insertEdge(0, 10, 1, (short) 0, Experiment.BatchType.DELETION);
        data.add(nextBatch);

        nextBatch = new Experiment.EdgeBatch(3);
        nextBatch.insertEdge(10, 13, 1, (short) 0, Experiment.BatchType.DELETION);
        nextBatch.insertEdge(0, 10, 1, (short) 0, Experiment.BatchType.ADDITION);
        data.add(nextBatch);

        nextBatch = new Experiment.EdgeBatch(2);
        nextBatch.insertEdge(10, 11, 1, (short) 0, Experiment.BatchType.DELETION);
        data.add(nextBatch);

        int[][] queries = new int[][]{{0, 1}, {0, 2}, {0, 3}, {0, 4}, {0, 5}, {0, 6}, {0, 7}};

        List<List<Long>> srcDstDistancesExpected = new ArrayList<>();
        srcDstDistancesExpected.add(new ArrayList<>(List.of(2L, 6L, 8L, 10L, 12L, 14L, 15L)));
        srcDstDistancesExpected.add(new ArrayList<>(List.of(1L, 1L, 1L, 1L, 1L, 1L, 1L)));
        srcDstDistancesExpected.add(new ArrayList<>(List.of(2L, 5L, 7L, 10L, 12L, 14L, 15L)));
        srcDstDistancesExpected.add(new ArrayList<>(List.of(2L, 4L, 5L, 6L, 7L, 8L, 9L)));

        runQueriesAndAssert(edges, weights, edgeTypes, vertexTypes, false, data, queries,
                new ExecutorType[]{ExecutorType.KHOP_CDD, ExecutorType.KHOP_DC, ExecutorType.KHOP_CDD_DET,
                        ExecutorType.KHOP_CDD_PROB}, srcDstDistancesExpected);
    }


    void testSPSP() throws Exception {
        int[][] edges =
                {{10, 20}, {20, 30}, {30, 40}, {10, 40}, {40, 50}, {50, 60}, {60, 70}, {70, 80}, {80, 90}, {90, 100},
                        {20, 40}, {10, 30}, {30, 50}};
        double[] weights = new double[]{20, 5, 10, 520, 10, 1, 1, 1, 1, 1, 50, 500, 30};
        short[] edgeTypes = new short[edges.length];
        short[][] vertexTypes = new short[edges.length][2];
        for (int i = 0; i < vertexTypes.length; i++) {
            vertexTypes[i] = new short[]{0, 0};
        }

        List<Experiment.EdgeBatch> data = new ArrayList<>();

        Experiment.EdgeBatch nextBatch = new Experiment.EdgeBatch(1);
        nextBatch.insertEdge(60, 70, 1, (short) 0, Experiment.BatchType.DELETION);
        data.add(nextBatch);

        nextBatch = new Experiment.EdgeBatch(3);
        nextBatch.insertEdge(10, 30, 500, (short) 0, Experiment.BatchType.DELETION);
        nextBatch.insertEdge(10, 30, 10, (short) 0, Experiment.BatchType.ADDITION);
        nextBatch.insertEdge(60, 70, 1, (short) 0, Experiment.BatchType.ADDITION);
        data.add(nextBatch);

        nextBatch = new Experiment.EdgeBatch(2);
        nextBatch.insertEdge(50, 60, 1, (short) 0, Experiment.BatchType.DELETION);
        nextBatch.insertEdge(50, 60, 10, (short) 0, Experiment.BatchType.ADDITION);
        data.add(nextBatch);

        int[][] queries = new int[][]{{10, 70}};

        List<List<Long>> srcDstDistancesExpected = new ArrayList<>();
        srcDstDistancesExpected.add(new ArrayList<>(List.of(47L)));
        srcDstDistancesExpected.add(new ArrayList<>(List.of(Long.MAX_VALUE)));
        srcDstDistancesExpected.add(new ArrayList<>(List.of(32L)));
        srcDstDistancesExpected.add(new ArrayList<>(List.of(41L)));

        runQueriesAndAssert(edges, weights, edgeTypes, vertexTypes, true, data, queries,
                new ExecutorType[]{ExecutorType.SPSP_W_CDD, ExecutorType.SPSP_W_DC, ExecutorType.SPSP_W_CDD_DET,
                        ExecutorType.SPSP_W_CDD_PROB}, srcDstDistancesExpected);
    }


    void testQ1() throws Exception {
        int[][] edges = {{10, 20}, {20, 30}, {40, 50}, {50, 60}, {10, 30}, {10, 30}, {30, 40}, {10, 50}, {50, 40}};
        double[] weights = null;
        short x = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShortOrInsert("knows");
        short y = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShortOrInsert("has");
        short[] edgeTypes = new short[]{x, x, x, x, x, y, y, x, x};
        short[][] vertexTypes = new short[edges.length][2];
        for (int i = 0; i < vertexTypes.length; i++) {
            vertexTypes[i] = new short[]{0, 0};
        }

        List<Experiment.EdgeBatch> data = new ArrayList<>();

        Experiment.EdgeBatch nextBatch = new Experiment.EdgeBatch(1);
        nextBatch.insertEdge(10, 50, 1, x, Experiment.BatchType.DELETION);
        data.add(nextBatch);

        nextBatch = new Experiment.EdgeBatch(1);
        nextBatch.insertEdge(30, 40, 1, x, Experiment.BatchType.ADDITION);
        data.add(nextBatch);

        int[][] queries = new int[][]{{10, 40}};

        List<List<Long>> srcDstDistancesExpected = new ArrayList<>();
        srcDstDistancesExpected.add(new ArrayList<>(List.of(6L)));
        srcDstDistancesExpected.add(new ArrayList<>(List.of(3L)));
        srcDstDistancesExpected.add(new ArrayList<>(List.of(6L)));

        runQueriesAndAssert(edges, weights, edgeTypes, vertexTypes, false, data, queries,
                new ExecutorType[]{ExecutorType.Q1_CDD, ExecutorType.Q1_DC, ExecutorType.Q1_CDD_DET,
                        ExecutorType.Q1_CDD_PROB}, srcDstDistancesExpected);
    }

    void testQ2() throws Exception {
        int[][] edges = {{10, 20}, {20, 30}, {40, 50}, {50, 60}, {10, 30}, {10, 30}, {30, 40}, {10, 50}, {50, 40}};
        double[] weights = null;
        short x = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShortOrInsert("knows");
        short y = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShortOrInsert("hasMember");
        short z = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShortOrInsert("has");
        short[] edgeTypes = new short[]{y, x, x, x, x, z, y, x, x};
        short[][] vertexTypes = new short[edges.length][2];
        for (int i = 0; i < vertexTypes.length; i++) {
            vertexTypes[i] = new short[]{0, 0};
        }

        List<Experiment.EdgeBatch> data = new ArrayList<>();

        Experiment.EdgeBatch nextBatch = new Experiment.EdgeBatch(1);
        nextBatch.insertEdge(10, 50, 1, x, Experiment.BatchType.DELETION);
        data.add(nextBatch);

        nextBatch = new Experiment.EdgeBatch(1);
        nextBatch.insertEdge(30, 40, 1, x, Experiment.BatchType.ADDITION);
        data.add(nextBatch);

        int[][] queries = new int[][]{{10, 40}};

        List<List<Long>> srcDstDistancesExpected = new ArrayList<>();
        srcDstDistancesExpected.add(new ArrayList<>(List.of(3L)));
        srcDstDistancesExpected.add(new ArrayList<>(List.of(3L)));
        srcDstDistancesExpected.add(new ArrayList<>(List.of(6L)));

        runQueriesAndAssert(edges, weights, edgeTypes, vertexTypes, false, data, queries,
                new ExecutorType[]{ExecutorType.Q2_CDD, ExecutorType.Q2_DC, ExecutorType.Q2_CDD_DET,
                        ExecutorType.Q2_CDD_PROB}, srcDstDistancesExpected);
    }
}
