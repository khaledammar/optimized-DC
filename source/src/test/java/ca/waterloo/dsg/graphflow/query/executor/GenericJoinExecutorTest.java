package ca.waterloo.dsg.graphflow.query.executor;

import ca.waterloo.dsg.graphflow.TestUtils;
import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.GraphDBState;
import ca.waterloo.dsg.graphflow.graph.TypeAndPropertyKeyStore;
import ca.waterloo.dsg.graphflow.query.QueryProcessor;
import ca.waterloo.dsg.graphflow.query.parser.StructuredQueryParser;
import ca.waterloo.dsg.graphflow.query.planner.OneTimeMatchQueryPlanner;
import ca.waterloo.dsg.graphflow.query.result.AbstractQueryResult;
import ca.waterloo.dsg.graphflow.query.result.Tuples;
import ca.waterloo.dsg.graphflow.query.result.subgraph.Subgraph;
import ca.waterloo.dsg.graphflow.query.result.subgraph.SubgraphType;
import ca.waterloo.dsg.graphflow.query.result.Subgraphs;
import ca.waterloo.dsg.graphflow.query.structuredquery.StructuredQuery;
import ca.waterloo.dsg.graphflow.server.ServerQueryString;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests {@code GenericJoinExecutor}.
 */
public class GenericJoinExecutorTest {

    @Before
    public void setUp() throws Exception {
        GraphDBState.reset();
    }

    /**
     * Tests the execution of a simple path query with no types.
     */
    @Test
    public void testPathQueryWithoutTypes() throws Exception {
        StructuredQuery pathQueryPlan = new StructuredQueryParser().parse("MATCH (a)->(b)");
        Integer[][] expectedMotifsAfterAddition = {{0, 1}, {1, 2}, {1, 3}, {2, 3}, {3, 0}, {3, 4},
            {4, 1}, {4, 4}};
        Integer[][] expectedMotifsAfterDeletion = {{0, 1}, {1, 2}, {1, 3}, {2, 3}, {3, 0}, {3, 4},
            {4, 4}};
        String[] headers = {"a", "b"};
        assertSimpleMatchQueryOutput(pathQueryPlan, constructSubgraphsQueryResult(
            expectedMotifsAfterAddition, headers), constructSubgraphsQueryResult(
            expectedMotifsAfterDeletion, headers));
    }

    /**
     * Tests the execution of a simple path query with types.
     */
    @Test
    public void testPathQueryWithTypes() throws Exception {
        StructuredQuery pathQueryPlan = new StructuredQueryParser().parse("MATCH (a)-[:FOLLOWS]->" +
            "(b)");
        Integer[][] expectedMotifsAfterAddition = {{0, 1}, {3, 0}, {3, 4}, {4, 1}};
        Integer[][] expectedMotifsAfterDeletion = {{3, 0}, {3, 4}, {4, 1}};
        String[] headers = {"a", "b"};
        assertComplexMatchQueryOutput(pathQueryPlan, constructSubgraphsQueryResult(
            expectedMotifsAfterAddition, headers), constructSubgraphsQueryResult(
            expectedMotifsAfterDeletion, headers));
    }

    /**
     * Tests the execution of a triangle query with no types.
     */
    @Test
    public void testTriangleQueryWithoutTypes() throws Exception {
        // Create a one time MATCH query plan for a simple triangle query with no types.
        StructuredQuery triangleStructuredQuery = new StructuredQueryParser().parse("MATCH " +
            "(a)->(b),(b)->(c),(c)->(a)");
        Integer[][] expectedMotifsAfterAdditions = {{0, 1, 3}, {1, 3, 0}, {1, 3, 4}, {3, 0, 1},
            {3, 4, 1}, {4, 1, 3}, {4, 4, 4}};
        Integer[][] expectedMotifsAfterDeletion = {{0, 1, 3}, {1, 3, 0}, {3, 0, 1}, {4, 4, 4}};
        String[] headers = {"a", "b", "c"};
        assertSimpleMatchQueryOutput(triangleStructuredQuery, constructSubgraphsQueryResult(
            expectedMotifsAfterAdditions, headers), constructSubgraphsQueryResult(
            expectedMotifsAfterDeletion, headers));
    }

    /**
     * Tests the execution of a triangle query with types.
     */
    @Test
    public void testTriangleQueryWithTypes() throws Exception {
        // Create a one time MATCH query plan for a complex triangle query with multiple
        // relations between variable having different edge types.
        StructuredQuery triangleStructuredQuery = new StructuredQueryParser().parse("MATCH " +
            "(a)-[:FOLLOWS]->(b),(a)-[:LIKES]->(b),(b)-[:LIKES]->(a),(b)->(c),(c)->(b)," +
            "(c)-[:FOLLOWS]->(a)");
        Integer[][] expectedMotifsAfterAdditions = {{1, 0, 3}, {1, 4, 3}};
        Integer[][] expectedMotifsAfterDeletion = {{1, 4, 3}};
        String[] headers = {"b", "a", "c"};
        assertComplexMatchQueryOutput(triangleStructuredQuery, constructSubgraphsQueryResult(
            expectedMotifsAfterAdditions, headers), constructSubgraphsQueryResult(
            expectedMotifsAfterDeletion, headers));
    }

    @Test
    public void testTriangleQueryWithProjection() {
        StructuredQuery triangleStructuredQuery = new StructuredQueryParser().parse("MATCH " +
            "(a)-[:FOLLOWS]->(b),(b)->(c),(c)-[:FOLLOWS]->(a) RETURN a, b;");
        Object[][] expectedMotifsAfterAdditions = {{0, 1}, {4, 1}};
        Object[][] expectedMotifsAfterDeletion = {{4, 1}};
        String[] headers = {"a", "b"};
        assertComplexMatchQueryOutput(triangleStructuredQuery, constructTuplesQueryResult(
            expectedMotifsAfterAdditions, headers), constructTuplesQueryResult(
            expectedMotifsAfterDeletion, headers));
    }

    @Test
    public void testTriangleQueryWithPropertyProjections() {
        StructuredQuery triangleStructuredQuery = new StructuredQueryParser().parse("MATCH " +
            "(a)-[d:FOLLOWS]->(b),(b)->(c),(c)-[:FOLLOWS]->(a) " +
            "RETURN a.age, b.views, c.name, d.views;");
        Object[][] expectedMotifsAfterAdditions = {{20, 70, "name3", 60}, {40, 70, "name3", 4}};
        Object[][] expectedMotifsAfterDeletion = {{40, 70, "name3", 4}};
        String[] headers = {"a.age", "b.views", "c.name", "d.views"};
        assertComplexMatchQueryOutput(triangleStructuredQuery, constructTuplesQueryResult(
            expectedMotifsAfterAdditions, headers), constructTuplesQueryResult(
            expectedMotifsAfterDeletion, headers));
    }

    /**
     * Tests the execution of a square query with no types.
     */
    @Test
    public void testSquareQueryWithoutTypes() throws Exception {
        // Create a one time MATCH query plan for a simple square query with no types.
        StructuredQuery squareStructuredQuery = new StructuredQueryParser().parse("MATCH " +
            "(a)->(b),(b)->(c),(c)->(d),(d)->(a)");
        Integer[][] expectedMotifsAfterAdditions = {{0, 1, 2, 3}, {1, 2, 3, 0}, {1, 2, 3, 4},
            {1, 3, 4, 4}, {2, 3, 0, 1}, {2, 3, 4, 1}, {3, 0, 1, 2}, {3, 4, 1, 2}, {3, 4, 4, 1},
            {4, 1, 2, 3}, {4, 1, 3, 4}, {4, 4, 1, 3}, {4, 4, 4, 4}};
        Integer[][] expectedMotifsAfterDeletion = {{0, 1, 2, 3}, {1, 2, 3, 0}, {2, 3, 0, 1},
            {3, 0, 1, 2}, {4, 4, 4, 4}};
        String[] headers = {"a", "b", "c", "d"};
        assertSimpleMatchQueryOutput(squareStructuredQuery, constructSubgraphsQueryResult(
            expectedMotifsAfterAdditions, headers), constructSubgraphsQueryResult(
            expectedMotifsAfterDeletion, headers));
    }

    /**
     * Tests the execution of a square query with types.
     */
    @Test
    public void testSquareQueryWithTypes() {
        // Initialize the {@code TypeStore} with types used in the MATCH query.
        TypeAndPropertyKeyStore.getInstance().mapStringTypeToShortOrInsert("FOLLOWS");
        TypeAndPropertyKeyStore.getInstance().mapStringTypeToShortOrInsert("LIKES");
        //Create a one time MATCH query plan for a square pattern with types.
        StructuredQuery triangleStructuredQuery = new StructuredQueryParser().parse("MATCH " +
            "(a)-[:FOLLOWS]->(b),(b)-[:LIKES]->(c),(c)-[:LIKES]->(d),(d)-[:FOLLOWS]->(a)");
        Integer[][] expectedMotifsAfterAdditions = {{0, 1, 4, 3}, {4, 1, 4, 3}};
        Integer[][] expectedMotifsAfterDeletion = {{4, 1, 4, 3}};
        String[] headers = {"a", "b", "c", "d"};
        assertComplexMatchQueryOutput(triangleStructuredQuery, constructSubgraphsQueryResult(
            expectedMotifsAfterAdditions, headers), constructSubgraphsQueryResult(
            expectedMotifsAfterDeletion, headers));
    }

    /**
     * Tests the execution of a diamond query with no types.
     */
    @Test
    public void testDiamondQueryWithoutTypes() throws Exception {
        //Create a a one time MATCH query plan for a simple diamond pattern with no types.
        StructuredQuery diamondStructuredQuery = new StructuredQueryParser().parse("MATCH (a)->" +
            "(b),(a)->(c),(b)->(d),(c)->(d)");
        Integer[][] expectedMotifsAfterAdditions = {{0, 1, 1, 2}, {0, 1, 1, 3}, {1, 2, 2, 3},
            {1, 3, 3, 0}, {1, 3, 3, 4}, {2, 3, 3, 0}, {2, 3, 3, 4}, {3, 0, 0, 1}, {3, 0, 4, 1},
            {3, 4, 0, 1}, {3, 4, 4, 1}, {3, 4, 4, 4}, {4, 1, 1, 2}, {4, 1, 1, 3}, {4, 4, 4, 1},
            {4, 4, 4, 4}};
        Integer[][] expectedMotifsAfterDeletion = {{0, 1, 1, 2}, {0, 1, 1, 3}, {1, 2, 2, 3},
            {1, 3, 3, 0}, {1, 3, 3, 4}, {2, 3, 3, 0}, {2, 3, 3, 4}, {3, 0, 0, 1}, {3, 4, 4, 4},
            {4, 4, 4, 4}};
        String[] headers = {"a", "b", "c", "d"};
        assertSimpleMatchQueryOutput(diamondStructuredQuery, constructSubgraphsQueryResult(
            expectedMotifsAfterAdditions, headers), constructSubgraphsQueryResult(
            expectedMotifsAfterDeletion, headers));
    }

    /**
     * Tests the execution of a diamond query with types.
     */
    @Test
    public void testDiamondQueryWithTypes() throws Exception {
        // Initialize the {@code TypeStore} with types used in the MATCH query.
        TypeAndPropertyKeyStore.getInstance().mapStringTypeToShortOrInsert("FOLLOWS");
        TypeAndPropertyKeyStore.getInstance().mapStringTypeToShortOrInsert("LIKES");
        //Create a one time MATCH query plan for a simple diamond pattern with types.
        StructuredQuery diamondStructuredQuery = new StructuredQueryParser().parse("MATCH (a)" +
            "-[:FOLLOWS]->(b),(a)-[:FOLLOWS]->(c),(b)-[:LIKES]->(d),(c)-[:LIKES]->(d)");
        Integer[][] expectedMotifsAfterAdditions = {{0, 1, 1, 0}, {0, 1, 1, 4}, {3, 0, 0, 1},
            {3, 0, 4, 1}, {3, 4, 0, 1}, {3, 4, 4, 1}, {3, 4, 4, 3}, {4, 1, 1, 0}, {4, 1, 1, 4}};
        Integer[][] expectedMotifsAfterDeletion = {{3, 0, 0, 1}, {3, 0, 4, 1}, {3, 4, 0, 1},
            {3, 4, 4, 1}, {3, 4, 4, 3}, {4, 1, 1, 0}, {4, 1, 1, 4}};
        String[] headers = {"a", "b", "c", "d"};
        assertComplexMatchQueryOutput(diamondStructuredQuery, constructSubgraphsQueryResult(
            expectedMotifsAfterAdditions, headers), constructSubgraphsQueryResult(
            expectedMotifsAfterDeletion, headers));
    }

    @Test
    public void testMatchQueryWithVertexTypesDifferentOrders() {
        GraphDBState.reset();
        String createQuery = "CREATE (0:t1)-[:t]->(1:t2), (2:t1)-[:t]->(3:t2);";
        QueryProcessor processor = new QueryProcessor();
        processor.process(ServerQueryString.newBuilder().setQuery(createQuery).build());

        String matchQuery = "MATCH (a:t1)-[:t]->(b:t2) return a, b;";
        Tuples queryResult = (Tuples) processor.process(ServerQueryString.newBuilder().setQuery(
            matchQuery).build());
        Assert.assertEquals(queryResult.getTuples().size(), 2);
        matchQuery = "MATCH (b:t1)-[:t]->(a:t2) return a, b;";
        queryResult = (Tuples) processor.process(ServerQueryString.newBuilder().setQuery(
            matchQuery).build());
        Assert.assertEquals(queryResult.getTuples().size(), 2);
    }

    private void assertSimpleMatchQueryOutput(StructuredQuery structuredQuery,
        AbstractQueryResult expectedResultAfterAdditions, AbstractQueryResult expectedResultAfterDeletion) {

        // Initialize a graph.
        Graph graph = Graph.getInstance();
        TestUtils.createEdgesPermanently(graph, "CREATE (0:Person)-[:FOLLOWS]->" +
            "(1:Person),(1:Person)-[:FOLLOWS]->(2:Person), (1:Person)-[:FOLLOWS]->(3:Person)," +
            "(2:Person)-[:FOLLOWS]->(3:Person), (3:Person)-[:FOLLOWS]->(4:Person)," +
            "(3:Person)-[:FOLLOWS]->(0:Person), (4:Person)-[:FOLLOWS]->(1:Person)," +
            "(4:Person)-[:LIKES]->(4:Person);");

        // Execute the query and test.
        AbstractQueryResult actualResult = new OneTimeMatchQueryPlanner(structuredQuery).plan().execute();
        Assert.assertEquals(expectedResultAfterAdditions.toString(), actualResult.toString());

        // Delete one of the edges.
        TestUtils.deleteEdgesPermanently(graph, "DELETE (4)->(1);");

        // Execute the query again and test.
        actualResult = new OneTimeMatchQueryPlanner(structuredQuery).plan().execute();
        Assert.assertEquals(expectedResultAfterDeletion.toString(), actualResult.toString());
    }

    private void assertComplexMatchQueryOutput(StructuredQuery structuredQuery,
        AbstractQueryResult expectedResultAfterAdditions, AbstractQueryResult expectedResultAfterDeletion) {

        // Initialize a graph.
        Graph graph = Graph.getInstance();
        TestUtils.initializeGraphPermanentlyWithProperties("CREATE " +
            "(0:Person { name: 'name0', age: 20, views: 120 })-[:FOLLOWS { views: 60 }]" +
            "->(1:Person { name: 'name1', age: 25, views: 70 })," +
            "(0:Person)-[:LIKES { views: 2 }]->(1:Person)," +
            "(1:Person)-[:LIKES { views: 250 }]->(0:Person)," +
            "(1:Person)-[:TAGGED]->(3:Person { name: 'name3', age: 22, views: 250})," +
            "(3:Person)-[:LIKES { views: 44 }]->(1:Person)," +
            "(3:Person)-[:FOLLOWS { views: 234 }]->(0:Person)," +
            "(4:Person{ name: 'name4', age: 40, views: 20})-[:FOLLOWS {views: 4}]->" +
            "(1:Person),(4:Person)-[:LIKES { views: 56 }]->(1:Person)," +
            "(1:Person)-[:LIKES { views: 68 }]->(4:Person)," +
            "(3:Person)-[:FOLLOWS { views: 123 }]->(4:Person)," +
            "(4:Person)-[:LIKES { views: 2 }]->(3:Person);");

        // Execute the query and test.
        AbstractQueryResult actualResult = new OneTimeMatchQueryPlanner(structuredQuery).plan().execute();
        Assert.assertEquals(expectedResultAfterAdditions.toString(), actualResult.toString());

        // Delete one of the edges.
        TestUtils.deleteEdgesPermanently(graph, "DELETE (0)-[:FOLLOWS]->(1);");

        // Execute the query again and test.
        actualResult = new OneTimeMatchQueryPlanner(structuredQuery).plan().execute();
        Assert.assertEquals(expectedResultAfterDeletion.toString(), actualResult.toString());
    }

    private AbstractQueryResult constructSubgraphsQueryResult(Integer[][] subgraphsData, String[] headers) {
        Map<String, Integer> variableIndices = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            variableIndices.put(headers[i], i);
        }
        Subgraphs subgraphs = new Subgraphs(variableIndices);
        for (Integer[] subgraphData : subgraphsData) {
            Subgraph subgraph = new Subgraph(SubgraphType.MATCHED, Arrays.stream(subgraphData).
                mapToInt(Integer::intValue).toArray(), null);
            subgraphs.addSubgraph(subgraph);
        }
        return subgraphs;
    }

    private AbstractQueryResult constructTuplesQueryResult(Object[][] subgraphsData, String[] headers) {
        Tuples tuples = new Tuples(null, headers);
        for (Object[] tuple : subgraphsData) {
            tuples.addTuple(tuple);
        }
        return tuples;
    }
}
