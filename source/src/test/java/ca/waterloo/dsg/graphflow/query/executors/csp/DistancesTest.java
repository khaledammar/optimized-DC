package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.TestUtils;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class DistancesTest {

    @Test
    public void testDistances() throws Exception {
        var v1 = 0;
        var v2 = 1;
        var v3 = 2;
        var v4 = 3;
        var v5 = 4;
        int[][] edges = {{v1, v2}, {v3, v4}, {v4, v5}};
        short[] edgeTypes = new short[edges.length];
        short[][] vertexTypes = new short[edges.length][2];
        for (int i = 0; i < vertexTypes.length; i++) {
            vertexTypes[i] = new short[]{0, 0};
        }
        TestUtils.initializeGraphPermanently(edges, edgeTypes, vertexTypes);

        Distances.numQueries = 3;
        Distances distances = new Distances(1, v1, v2);

        distances.setVertexDistance(v2, (short) 1, 23);
        distances.setVertexDistance(v2, (short) 3, 12);
        distances.setVertexDistance(v2, (short) 4, 9);

        distances.setVertexDistance(v3, (short) 2, 244);
        distances.setVertexDistance(v3, (short) 3, 19);

        var vertices = Arrays.asList(v1, v2, v3, v4, v5);
        var iterations = Arrays.asList((short) 0, (short) 1, (short) 2, (short) 3, (short) 4, (short) 5);
        var expectedDistDelta = Arrays.asList(new long[]{0, 0, 0, 0, 0, 0},
                new long[]{Long.MAX_VALUE, 23, 23, 12, 9, 9},
                new long[]{Long.MAX_VALUE, Long.MAX_VALUE, 244, 19, 19, 19},
                new long[]{Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE,
                        Long.MAX_VALUE},
                new long[]{Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE,
                        Long.MAX_VALUE});
        for (var vertex : vertices) {
            for (var iter : iterations) {
                assertEquals("Delta: v=" + vertex + ",iter=" + iter, expectedDistDelta.get(vertex)[iter],
                        distances.getDistance(vertex, iter, true));
                assertEquals("Real: v=" + vertex + ",iter=" + iter, Long.MAX_VALUE,
                        distances.getDistance(vertex, iter, false));
            }
        }

        distances.mergeDeltaDiffs();

        for (var vertex : vertices) {
            for (var iter : iterations) {
                assertEquals("Delta: v=" + vertex + ",iter=" + iter, expectedDistDelta.get(vertex)[iter],
                        distances.getDistance(vertex, iter, true));
                assertEquals("Real: v=" + vertex + ",iter=" + iter, expectedDistDelta.get(vertex)[iter],
                        distances.getDistance(vertex, iter, false));
            }
        }

        distances.setVertexDistance(v2, (short) 2, 22);
        distances.setVertexDistance(v2, (short) 3, 10);

        distances.setVertexDistance(v4, (short) 2, 199);

        var expectedDistDelta2 = Arrays.asList(new long[]{0, 0, 0, 0, 0, 0},
                new long[]{Long.MAX_VALUE, 23, 22, 10, 9, 9},
                new long[]{Long.MAX_VALUE, Long.MAX_VALUE, 244, 19, 19, 19},
                new long[]{Long.MAX_VALUE, Long.MAX_VALUE, 199, 199, 199, 199},
                new long[]{Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE,
                        Long.MAX_VALUE});

        for (var vertex : vertices) {
            for (var iter : iterations) {
                assertEquals("Delta: v=" + vertex + ",iter=" + iter, expectedDistDelta2.get(vertex)[iter],
                        distances.getDistance(vertex, iter, true));
                assertEquals("Real: v=" + vertex + ",iter=" + iter, expectedDistDelta.get(vertex)[iter],
                        distances.getDistance(vertex, iter, false));
            }
        }
    }
}
