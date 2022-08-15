package ca.waterloo.dsg.graphflow.query.executor;

import ca.waterloo.dsg.graphflow.TestUtils;
import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.GraphDBState;
import ca.waterloo.dsg.graphflow.query.parser.StructuredQueryParser;
import ca.waterloo.dsg.graphflow.query.plan.ContinuousMatchQueryPlan;
import ca.waterloo.dsg.graphflow.query.planner.ContinuousMatchQueryPlanner;
import ca.waterloo.dsg.graphflow.query.result.subgraph.SubgraphType;
import ca.waterloo.dsg.graphflow.query.structuredquery.StructuredQuery;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringJoiner;

/**
 * Tests {@link ContinuousMatchQueryExecutor}.
 */
public class ContinuousMatchQueryExecutorTest {

    // Special JUnit defined temporary folder used to test I/O operations on files. Requires
    // {@code public} visibility.
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() {
        GraphDBState.reset();
    }

    /**
     * Tests the execution of a triangle CONTINUOUSLY MATCH query.
     */
    @Test
    public void testProcessTriangles() throws IOException {
        // Register a triangle CONTINUOUSLY MATCH query.
        String continuousTriangleQuery = "CONTINUOUSLY MATCH (a)->(b),(b)->(c),(c)->(a)" +
            " FILE 'results';";
        StructuredQuery structuredQuery = new StructuredQueryParser().parse(
            continuousTriangleQuery);
        String fileName = "continuous_match_query_" + structuredQuery.getFilePath();
        File location = temporaryFolder.newFile(fileName);
        ContinuousMatchQueryPlanner planner = new ContinuousMatchQueryPlanner(structuredQuery,
            location);
        ContinuousMatchQueryExecutor.getInstance().addContinuousMatchQueryPlan(
            (ContinuousMatchQueryPlan) planner.plan());

        // Initialize a graph.
        Graph graph = Graph.getInstance();
        TestUtils.createEdgesPermanently(graph, "CREATE (0:Person)-[:FOLLOWS]->" +
            "(1:Person),(1:Person)-[:FOLLOWS]->(2:Person), (1:Person)-[:FOLLOWS]->(3:Person)," +
            "(2:Person)-[:FOLLOWS]->(3:Person), (3:Person)-[:FOLLOWS]->(4:Person)," +
            "(3:Person)-[:FOLLOWS]->(0:Person), (4:Person)-[:FOLLOWS]->(1:Person);");

        // Create a diff graph by temporarily adding and deleting edges.
        TestUtils.createEdgesTemporarily(graph, "CREATE (2:Person)-[:FOLLOWS]->(0:Person)");
        TestUtils.deleteEdgesTemporarily(graph, "DELETE (3)->(4)");
        TestUtils.deleteEdgesTemporarily(graph, "DELETE (1)->(2)");

        // Execute the registered CONTINUOUSLY MATCH query.
        ContinuousMatchQueryExecutor.getInstance().execute();

        Object[][][] expectedResults = {
            {{'a', 'b', 'c'}},
            {{2, 0, 1}, {SubgraphType.EMERGED}},
            {{'a', 'b', 'c'}},
            {{3, 4, 1}, {SubgraphType.DELETED}},
            {{'b', 'c', 'a'}},
            {{'b', 'c', 'a'}},
            {{3, 4, 1}, {SubgraphType.DELETED}},
            {{'c', 'a', 'b'}},
            {{'c', 'a', 'b'}},
            {{3, 4, 1}, {SubgraphType.DELETED}},
            {{1, 2, 0}, {SubgraphType.DELETED}}};

        // Test the output of the registered CONTINUOUSLY MATCH query.
        BufferedReader br = new BufferedReader(new FileReader(location));
        StringJoiner actualOutput = new StringJoiner(System.lineSeparator());
        String line;
        while ((line = br.readLine()) != null) {
            actualOutput.add(line);
        }
        Assert.assertEquals(getExpectedContentOfOutputFileSink(expectedResults),
            actualOutput.toString());
    }

    private String getExpectedContentOfOutputFileSink(Object[][][] expectedResults) {
        StringJoiner stringJoiner = new StringJoiner(System.lineSeparator());
        for (Object[][] expectedResult : expectedResults) {
            String output = Arrays.toString(expectedResult[0]);
            if (expectedResult.length > 1) {
                output += " " + expectedResult[1][0].toString();
            }
            stringJoiner.add(output);
        }
        return stringJoiner.toString();
    }

    /**
     * Removes all registered Continuous Match queries after the
     */
    @AfterClass
    public static void resetContinuousMatchQueries() {
        ContinuousMatchQueryExecutor.getInstance().reset();
    }
}
