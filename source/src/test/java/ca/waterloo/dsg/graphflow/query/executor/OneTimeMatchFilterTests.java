package ca.waterloo.dsg.graphflow.query.executor;

import ca.waterloo.dsg.graphflow.TestUtils;
import ca.waterloo.dsg.graphflow.graph.GraphDBState;
import ca.waterloo.dsg.graphflow.query.parser.StructuredQueryParser;
import ca.waterloo.dsg.graphflow.query.planner.OneTimeMatchQueryPlanner;
import ca.waterloo.dsg.graphflow.query.result.AbstractQueryResult;
import ca.waterloo.dsg.graphflow.query.result.Tuples;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * End-to-end tests of the different types of filter queries. Each query matches a triangle
 * pattern against the following graph.
 * <ul>
 * <li>Vertex IDs: 0, 1, 3, 4 , 5</li>
 * <li>Each vertex is of type PERSON and has properties name(string), age(int), views(int).</li>
 * <li>Each vertex is of type FOLLOWS and has the property views(int).</li>
 * </ul>
 * Edges: Form 3 interconnected triangles
 * <ul>
 * <li>0 -> 1, 1 -> 3, 3 -> 0</li>
 * <li>3 -> 4, 4 -> 1, 1 -> 3</li>
 * <li>4 -> 1, 1 -> 5, 5 -> 4</li>
 * </ul>
 */
public class OneTimeMatchFilterTests {

    @Before
    public void setUp() {
        GraphDBState.reset();
        TestUtils.initializeGraphPermanentlyWithProperties("CREATE " +
            "(0:Person { name: 'name0', age: 20, views: 120 })" +
            "-[:FOLLOWS { views: 250, is_friends: true }]->" +
            "(1:Person { name: 'name1', age: 25, views: 70 })," +
            "(1:Person)-[:FOLLOWS { views: 12, is_friends: true }]->(0:Person)," +
            "(1:Person)-[:FOLLOWS { views: 40, is_friends: false }]->" +
            "(3:Person { name: 'name3', age: 22, views: 250 })," +
            "(3:Person)-[:FOLLOWS { views: 70, is_friends: true }]->(0:Person), " +
            "(4:Person { name: 'name4', age: 40, views: 20 })-" +
            "[:FOLLOWS { views: 45, is_friends: true }]->(1:Person)," +
            "(3:Person)-[:FOLLOWS { views: 50, is_friends: true }]->(4:Person)," +
            "(5:Person { name: 'name5', age: 30, views: 120 })-" +
            "[:FOLLOWS { views: 35, is_friends: true }]->(4:Person)," +
            "(1:Person)-[:FOLLOWS { views: 250, is_friends: false }]->(5:Person);");
    }

    @Test
    public void testTwoVertexFilterVariableQuery() {
        String matchQuery = "MATCH (v1)-[e1:FOLLOWS]->(v2),(v2)-[e2:FOLLOWS]->(v3),(v3)" +
            "-[:FOLLOWS]->(v1) WHERE v1.views > v2.views RETURN v1, v2, v3;";
        Object[][] expectedResults = {{0, 1, 3}, {3, 0, 1}, {3, 4, 1}, {5, 4, 1}};
        runTest(matchQuery, expectedResults, new String[]{"v1", "v2", "v3"});
    }

    @Test
    public void testTwoVertexFilterLiteralQuery() {
        String matchQuery = "MATCH (v1)-[e1:FOLLOWS]->(v2) WHERE v1.views = 120 RETURN v1, v2;";
        Object[][] expectedResults = {{0, 1}, {5, 4}};
        runTest(matchQuery, expectedResults, new String[]{"v1", "v2"});
    }


    @Test
    public void testThreeVertexFilterLiteralQuery() {
        String matchQuery = "MATCH (v1)-[e1:FOLLOWS]->(v2),(v2)-[e2:FOLLOWS]->(v3) WHERE v3.views" +
            " = 120 RETURN v1, v2, v3;";
        Object[][] expectedResults = {{0, 1, 0}, {0, 1, 5}, {4, 1, 0}, {4, 1, 5}, {1, 3, 0}};
        runTest(matchQuery, expectedResults, new String[]{"v1", "v2", "v3"});

    }
    @Test
    public void testTwoEdgeFilterQuery() {
        String matchQuery = "MATCH (v1)-[e1:FOLLOWS]->(v2),(v2)-[e2:FOLLOWS]->(v3),(v3)" +
            "-[:FOLLOWS]->(v1) WHERE e1.views > e2.views RETURN v1, v2, v3;";
        Object[][] expectedResults = {{0, 1, 3}, {1, 5, 4}, {3, 4, 1}, {4, 1, 3}};
        runTest(matchQuery, expectedResults, new String[]{"v1", "v2", "v3"});
    }

    @Test
    public void testVertexAndEdgeFilterQuery() {
        String matchQuery = "MATCH (v1)-[e1:FOLLOWS]->(v2),(v2)-[e2:FOLLOWS]->(v3),(v3)" +
            "-[:FOLLOWS]->(v1) WHERE v1.views > e1.views RETURN v1, v2, v3;";
        Object[][] expectedResults = {{1, 3, 0}, {1, 3, 4}, {3, 0, 1}, {3, 4, 1}, {5, 4, 1}};
        runTest(matchQuery, expectedResults, new String[]{"v1", "v2", "v3"});
    }

    @Test
    public void testEdgeAndLiteralFilterQuery() {
        String matchQuery = "MATCH (v1)-[e1:FOLLOWS]->(v2),(v2)-[e2:FOLLOWS]->(v3),(v3)" +
            "-[:FOLLOWS]->(v1) WHERE e1.views > 50 RETURN v1, v2, v3;";
        Object[][] expectedResults = {{0, 1, 3}, {1, 5, 4}, {3, 0, 1}};
        runTest(matchQuery, expectedResults, new String[]{"v1", "v2", "v3"});
    }

    @Test
    public void testVertexAndLiteralFilterQuery() {
        String matchQuery = "MATCH (v1)-[e1:FOLLOWS]->(v2),(v2)-[e2:FOLLOWS]->(v3),(v3)" +
            "-[:FOLLOWS]->(v1) WHERE v1.views > 100 RETURN v1, v2, v3;";
        Object[][] expectedResults = {{0, 1, 3}, {3, 0, 1}, {3, 4, 1}, {5, 4, 1}};
        runTest(matchQuery, expectedResults, new String[]{"v1", "v2", "v3"});
    }

    @Test
    public void testOneVariableExistsMultipleTimesInAFilter() {
        String matchQuery = "MATCH (v1)-[e1:FOLLOWS]->(v2),(v2)-[e2:FOLLOWS]->(v3),(v3)" +
            "-[:FOLLOWS]->(v1) WHERE v1.views > v2.views AND v1.views > v3.views " +
            "RETURN v1, v2, v3;";
        Object[][] expectedResults = {{3, 0, 1}, {3, 4, 1}, {5, 4, 1}};
        runTest(matchQuery, expectedResults, new String[]{"v1", "v2", "v3"});
    }

    @Test
    public void testInClauseMatchNothingQuery() {
        String matchQuery = "MATCH (v1)-[e1:FOLLOWS]->(v2) WHERE v1.age in [10, 11, v2.age] " +
            "RETURN v1, v2";
        Object[][] expectedResults = {};
        runTest(matchQuery, expectedResults, new String[]{"v1", "v2"});
    }

    @Test
    public void testInClauseMatchLiteralQuery() {
        String matchQuery = "MATCH (v1)-[e1:FOLLOWS]->(v2) WHERE v1.views IN [v2.age, v2.views, " +
            "120] RETURN v1, v2;";
        Object[][] expectedResults = {{0, 1}, {5, 4}};
        runTest(matchQuery, expectedResults, new String[]{"v1", "v2"});
    }

    @Test
    public void testInClauseOnlyLiteralsFilterQuery() {
        String matchQuery = "MATCH (v1)-[e1:FOLLOWS]->(v2) WHERE v1.views IN [118, 119, 120] " +
            "RETURN v1, v2;";
        Object[][] expectedResults = {{0, 1}, {5, 4}};
        runTest(matchQuery, expectedResults, new String[]{"v1", "v2"});
    }

    private void runTest(String query, Object[][] expectedResults, String[] columnNames) {
        Tuples tuples = new Tuples(null, columnNames);
        for (Object[] tuple : expectedResults) {
            tuples.addTuple(tuple);
        }
        AbstractQueryResult queryResult = new OneTimeMatchQueryPlanner(new StructuredQueryParser().parse(
            query)).plan().execute();
        Assert.assertEquals(tuples.toString(), queryResult.toString());
    }
}
