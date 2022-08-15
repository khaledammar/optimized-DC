package ca.waterloo.dsg.graphflow.query.planner;

import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.GraphDBState;
import ca.waterloo.dsg.graphflow.query.parser.StructuredQueryParser;
import ca.waterloo.dsg.graphflow.query.result.AbstractQueryResult;
import ca.waterloo.dsg.graphflow.query.result.Tuples;
import ca.waterloo.dsg.graphflow.query.structuredquery.StructuredQuery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link LoadDataFromCSVQueryPlanner}.
 * <p>
 * Resource files used in these tests are from the train benchmark project.
 * https://github.com/FTSRG/trainbenchmark/tree/graphflow/models
 * <p>
 * Schema can be found in railway-containments.png, in the resources folder
 */
public class LoadDataFromCSVPlannerTest {

    private static final int EXPECTED_CONNECTS_TO_EDGES = 585;
    private static final int EXPECTED_ENTRY_EDGES = 5;
    private static final int EXPECTED_EXIT_EDGES = 5;
    private static final int EXPECTED_FOLLOWS_EDGES = 25;
    private static final int EXPECTED_MONITORED_BY_EDGES = 672;
    private static final int EXPECTED_REQUIRES_EDGES = 112;
    private static final int EXPETED_TARGET_EDGES = 25;
    private static final int EXPECTED_HIGHEST_VERTEX_ID = 737;

    private static final String[] edgeFiles = {
        "connectsTo",
        "entry",
        "exit",
        "follows",
        "monitoredBy",
        "requires",
        "target",
    };

    private static final String[] vertexFiles = {
        "Region",
        "Route",
        "Segment",
        "Semaphore",
        "Sensor",
        "Switch",
        "SwitchPosition",
    };

    @Before
    public void setUp() {
        GraphDBState.reset();
        for (int i = 0; i < vertexFiles.length; i++) {
            String filePath = this.getClass().getClassLoader().getResource("vertices_data_csv" +
                "/railway-" + vertexFiles[i] + ".csv").getPath();
            loadCSVIntoGraph("LOAD VERTICES WITH LABEL '" + vertexFiles[i] + "' FROM CSV '" +
                filePath + "' SEPARATOR ',';");
        }

        for (int i = 0; i < edgeFiles.length; i++) {
            String filePath = this.getClass().getClassLoader().getResource("edges_data_csv/" +
                "railway-" + edgeFiles[i] + ".csv").getPath();
            loadCSVIntoGraph("LOAD EDGES WITH TYPE '" + edgeFiles[i] + "' FROM CSV '" + filePath +
                "' SEPARATOR ',';");
        }
    }

    @Test
    public void testFindHighestVertexID() {
        int highestId = Graph.getInstance().getHighestPermanentVertexId();
        Assert.assertEquals(highestId, EXPECTED_HIGHEST_VERTEX_ID);
    }

    @Test
    public void testFindNumberOfConnections() {
        assertQueryResultCount("MATCH (a)-[:connectsTo]->(b) return COUNT(*);",
            EXPECTED_CONNECTS_TO_EDGES);
    }

    @Test
    public void testFindNumberOfEntries() {
        assertQueryResultCount("MATCH (a)-[:entry]->(b) return COUNT(*);", EXPECTED_ENTRY_EDGES);
    }

    @Test
    public void testFindNumberOfExits() {
        assertQueryResultCount("MATCH (a)-[:exit]->(b) return COUNT(*);", EXPECTED_EXIT_EDGES);
    }

    @Test
    public void testFindNumberOfFollows() {
        assertQueryResultCount("MATCH (a)-[:follows]->(b) return COUNT(*);",
            EXPECTED_FOLLOWS_EDGES);
    }

    @Test
    public void testFindNumberOfMonitoredBy() {
        assertQueryResultCount("MATCH (a)-[:monitoredBy]->(b) return COUNT(*);",
            EXPECTED_MONITORED_BY_EDGES);
    }

    @Test
    public void testFindNumberOfRequires() {
        assertQueryResultCount("MATCH (a)-[:requires]->(b) return COUNT(*);",
            EXPECTED_REQUIRES_EDGES);
    }

    @Test
    public void testFindNumberOfTargets() {
        assertQueryResultCount("MATCH (a)-[:target]->(b) return COUNT(*);", EXPETED_TARGET_EDGES);
    }

    private void assertQueryResultCount(String matchQuery, int expectedCount) {
        AbstractQueryResult queryResult = new OneTimeMatchQueryPlanner(new StructuredQueryParser().
            parse(matchQuery)).plan().execute();
        long edgeCount = (long) ((Tuples) queryResult).getTuples().get(0)[0];
        Assert.assertEquals(edgeCount, expectedCount);
    }

    private static void loadCSVIntoGraph(String loadQuery) {
        StructuredQuery structuredQuery = new StructuredQueryParser().parse(loadQuery);
        new LoadDataFromCSVQueryPlanner(structuredQuery).plan().execute();
    }
}
