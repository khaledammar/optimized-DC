package ca.waterloo.dsg.graphflow.query.executor;

import ca.waterloo.dsg.graphflow.TestUtils;
import ca.waterloo.dsg.graphflow.graph.GraphDBState;
import ca.waterloo.dsg.graphflow.query.parser.StructuredQueryParser;
import ca.waterloo.dsg.graphflow.query.planner.OneTimeMatchQueryPlanner;
import ca.waterloo.dsg.graphflow.query.result.AbstractQueryResult;
import ca.waterloo.dsg.graphflow.query.structuredquery.StructuredQuery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * End to end tests for group by and aggregation. The tests in this class are executed on a
 * "wheel" graph that consists of 6 vertices with IDs from 0 to 5.The vertices are as follows:
 * <ul>
 * <li> Vertex i has type: VType{i}.
 * <li> Vertices 0, 2, and 4 have {@code String} property: strVP:strVPValueE (for "e"ven).
 * <li> Vertices 1, 3, and 5 have {@code String} property: strVP:strVPValueO (for "o"dd).
 * <li> Vertex i has intVP:i and doubleVP:{i}.0.
 * </ul>
 * The edges are as follows:
 * <ul>
 * <li> Vertex 0 has 5 edges to each other vertex. edge(0, i) has:
 * <ul>
 * <li> type: StarEdge.
 * <li> strEP: strEPValue0{i}.
 * <li> intEP: {i}.
 * <li> doubleEP: {i}.0.
 * </ul>
 * <li> There is the following cycle edges: (1, 2), (2, 3), (3, 4), (4, 5), and (5, 0). Edge
 * (i, j) has:
 * <ul>
 * <li> type: CycleEdge.
 * <li> strEP: strEPValueO (for "o"dd) if i + j is odd or strEPValueE (for "e"ven) if i + j is even.
 * <li> intEP: {i + j}.
 * <li> doubleEP: {i.0 + j.0}.
 * </ul>
 * </ul>
 */
public class OneTimeMatchQueryGroupByTests {

    @Before
    public void setUp() throws Exception {
        GraphDBState.reset();
        constructTestGraph();
    }

    private void constructTestGraph() {
        String[] verticesInQuery = {
            "(0:VType0{strVP: 'strVPValueE', intVP: 0, doubleVP: 0.0})",
            "(1:VType1{strVP: 'strVPValueO', intVP: 1, doubleVP: 1.0})",
            "(2:VType2{strVP: 'strVPValueE', intVP: 2, doubleVP: 2.0})",
            "(3:VType3{strVP: 'strVPValueO', intVP: 3, doubleVP: 3.0})",
            "(4:VType4{strVP: 'strVPValueE', intVP: 4, doubleVP: 4.0})",
            "(5:VType5{strVP: 'strVPValueO', intVP: 5, doubleVP: 5.0})"};

        String createQuery = "CREATE " +
            verticesInQuery[0] + "-[:StarEdge { strEP: 'strEPValueO', intEP: 1, " +
            "doubleEP: 1.0}]->" + verticesInQuery[1] + "," + verticesInQuery[0] +
            "-[:StarEdge{strEP: 'strEPValueE', intEP: 2, doubleEP: 2.0}]->" +
            verticesInQuery[2] + "," + verticesInQuery[0] +
            "-[:StarEdge{strEP: 'strEPValueO', intEP: 3, doubleEP: 3.0}]->" +
            verticesInQuery[3] + "," + verticesInQuery[0] +
            "-[:StarEdge{strEP: 'strEPValueE', intEP: 4, doubleEP: 4.0}]->" +
            verticesInQuery[4] + "," + verticesInQuery[0] +
            "-[:StarEdge{strEP: 'strEPValueO', intEP: 5, doubleEP: 5.0}]->" +
            verticesInQuery[5] + "," + verticesInQuery[1] +
            "-[:CycleEdge{strEP: 'strEPValueO', intEP: 3, doubleEP: 3.0}]->" +
            verticesInQuery[2] + "," + verticesInQuery[2] +
            "-[:CycleEdge{strEP: 'strEPValueO', intEP: 5, doubleEP: 5.0}]->" +
            verticesInQuery[3] + "," + verticesInQuery[3] +
            "-[:CycleEdge{strEP: 'strEPValueO', intEP: 7, doubleEP: 7.0}]->" +
            verticesInQuery[4] + "," + verticesInQuery[4] +
            "-[:CycleEdge{strEP: 'strEPValueO', intEP: 9, doubleEP: 9.0}]->" +
            verticesInQuery[5] + "," + verticesInQuery[5] +
            "-[:CycleEdge{strEP: 'strEPValueE', intEP: 6, doubleEP: 6.0}]->" +
            verticesInQuery[1];
        TestUtils.initializeGraphPermanentlyWithProperties(createQuery);
    }

    @Test
    public void testAggregateNoGroupByKeyOneAggregation() {
        String queryString = "MATCH (a)->(b) return sum(b.intVP)";
        // There are 10 edges a->b. Each vertex in the outside has 2 edges: So the sum should be
        // 2*(1 + 2 + 3 + 4 + 5) = 30
        runTest(queryString, "[SUM(b.intVP)]", "30");
    }

    @Test
    public void testAggregateNoGroupByKeyMultipleAggregations() {
        String queryString = "MATCH (a)->(b) return sum(b.intVP), avg(a.doubleVP)";
        // There are 10 edges a->b. Sum is 30. For average: 0 matches 5 times and contributes 0.0
        // each time. Every other vertex i contributes once ands a value of i.0, totaling 15.
        // 15.0/10 = 1.5.
        runTest(queryString, "[SUM(b.intVP), AVG(a.doubleVP)]", "30, 1.5");
    }

    @Test
    public void testAggregateSingleGroupByKeySingleAggregation() {
        String queryString = "MATCH (a)-[e:CycleEdge]->(b) return b.strVP, avg(e.intEP)";
        // There are 5 star edges a-[e]->b.
        // (1, 2) has groupByKey: strVPValueE and value 3
        // (2, 3) has groupByKey: strVPValueO and value 5
        // (3, 4) has groupByKey: strVPValueE and value 7
        // (4, 5) has groupByKey: strVPValueO and value 9
        // (5, 1) has groupByKey: strVPValueO and value 6
        double strOAvg = (double) (5 + 9 + 6) / 3;
        double strEAvg = (double) (3 + 7) / 2;
        runTest(queryString, "[b.strVP, AVG(e.intEP)]", "strVPValueE, " + strEAvg, "strVPValueO, "
            + strOAvg);
    }

    @Test
    public void testAggregateSingleGroupByKeyMultipleAggregation() {
        String queryString = "MATCH (a)-[e:CycleEdge]->(b) return b.strVP, avg(e.intEP), count(*)";
        // see testAggregateSingleGroupByKeySingleAggregation
        double strVPValueOAvg = (double) (5 + 9 + 6) / 3;
        double strVPValueEAvg = (double) (3 + 7) / 2;
        runTest(queryString, "[b.strVP, AVG(e.intEP), COUNT(*)]", "strVPValueE, " + strVPValueEAvg
            + ", 2", "strVPValueO, " + strVPValueOAvg + ", 3");
    }

    @Test
    public void testAggregateMultipleGroupByKeySingleAggregation() {
        String queryString = "MATCH (a)-[e:CycleEdge]->(b) return a.strVP, e.strEP, " +
            "sum(b.doubleVP)";
        // There are 5 cycle edges a-[e]->b.
        // (1, 2) has groupByKey: strVPValueO-strEPValueO and value 2.0
        // (2, 3) has groupByKey: strVPValueE-strEPValueO and value 3.0
        // (3, 4) has groupByKey: strVPValueO-strEPValueO and value 4.0
        // (4, 5) has groupByKey: strVPValueE-strEPValueO and value 5.0
        // (5, 1) has groupByKey: strVPValueO-strEPValueE and value 1.0
        runTest(queryString, "[a.strVP, e.strEP, SUM(b.doubleVP)]", "strVPValueE, strEPValueO, 8.0",
            "strVPValueO, strEPValueE, 1.0", "strVPValueO, strEPValueO, 6.0");
    }

    @Test
    public void testAggregateMultipleGroupByKeyMultipleAggregations() {
        String queryString = "MATCH (a)-[e:CycleEdge]->(b) return a.strVP, e.strEP, sum(b" +
            ".doubleVP), count(*)";
        // see testAggregateMultipleGroupByKeySingleAggregation for the matching edges.
        runTest(queryString, "[a.strVP, e.strEP, SUM(b.doubleVP), COUNT(*)]", "strVPValueE, " +
            "strEPValueO, 8.0, 2", "strVPValueO, strEPValueE, 1.0, 1", "strVPValueO, " +
            "strEPValueO, 6.0, 2");
    }

    @Test
    public void testCountStar() {
        String queryString = "MATCH (a)->(b) return count(*)";
        runTest(queryString, "[COUNT(*)]", "10");
    }

    @Test
    public void testAverageInt() {
        String queryString = "MATCH (a)-[e]->(b) return e.strEP, avg(b.intVP);";
        // There are 7 strEPValueO edges (a, b) with b values: (0,1): 1, (0,3): 3, (0, 5): 5,
        // (1, 2): 2, (2, 3): 3, (3,4): 4, (4, 5): 5
        // There are 3 strEPValueE edges (a, b) with b values: (0,2): 2, (0,4): 4, (5,1): 1
        double strEPValueOAvg = (1 + 3 + 5 + 2 + 3 + 4 + 5) / 7.0;
        double strEPValueEAvg = (2 + 4 + 1) / 3.0;
        runTest(queryString, "[e.strEP, AVG(b.intVP)]", "strEPValueE, " + strEPValueEAvg,
            "strEPValueO, " + strEPValueOAvg);
    }

    @Test
    public void testAverageDouble() {
        String queryString = "MATCH (a)-[e]->(b) return e.strEP, avg(a.doubleVP);";
        // There are 7 strEPValueO edges (a, b) with a values: (0,1): 0.0, (0,3): 0.0,
        // (0, 5): 0.0, (1,2): 1.0, (2, 3): 2.0, (3,4): 3.0, (4, 5): 4.0
        // There are 3 strEPValueE edges (a, b) with a values: (0,2): 0.0, (0,4): 0.0, (5,1): 5.0
        double strEPValueOAvg = (0.0 + 0.0 + 0.0 + 1.0 + 2.0 + 3.0 + 4.0) / 7;
        double strEPValueEAvg = (0.0 + 0.0 + 5.0) / 3;
        runTest(queryString, "[e.strEP, AVG(a.doubleVP)]", "strEPValueE, " + strEPValueEAvg,
            "strEPValueO, " + strEPValueOAvg);
    }

    @Test
    public void testSumInt() {
        String queryString = "MATCH (a)-[e]->(b) return e.strEP, sum(b.intVP);";
        // see testAverageInt
        long strEPValueOSum = 1 + 3 + 5 + 2 + 3 + 4 + 5;
        long strEPValueESum = 2 + 4 + 1;
        runTest(queryString, "[e.strEP, SUM(b.intVP)]", "strEPValueE, " + strEPValueESum,
            "strEPValueO, " + strEPValueOSum);
    }

    @Test
    public void testSumDouble() {
        String queryString = "MATCH (a)-[e]->(b) return e.strEP, sum(a.doubleVP);";
        // see testAverageDouble
        double strEPValueOSum = 0.0 + 0.0 + 0.0 + 1.0 + 2.0 + 3.0 + 4.0;
        double strEPValueESum = 0.0 + 0.0 + 5.0;
        runTest(queryString, "[e.strEP, SUM(a.doubleVP)]", "strEPValueE, " + strEPValueESum,
            "strEPValueO, " + strEPValueOSum);
    }

    @Test
    public void testMaxInt() {
        String queryString = "MATCH (a)-[e]->(b) return a.strVP, max(e.intEP);";
        // There are 3 strVPValueO edges (a, b) with a values: (1,2): 3, (3,4): 7, (5,1): 6
        // There are 7 strVPValueE edges (a, b) with e values: (0,1): 1, (0,2): 2, (0,3): 3,
        // (0,4): 4, (0,5): 5, (2, 3): 5, (4,5): 9
        long strVPValueOMax = 7;
        long strVPValueEMax = 9;
        runTest(queryString, "[a.strVP, MAX(e.intEP)]", "strVPValueE, " + strVPValueEMax,
            "strVPValueO, " + strVPValueOMax);
    }

    @Test
    public void testMaxDouble() {
        String queryString = "MATCH (a)-[e]->(b) return e.strEP, max(a.doubleVP);";
        // see testAverageDouble
        double strEPValueOMax = 4.0;
        double strEPValueEMax = 5.0;
        runTest(queryString, "[e.strEP, MAX(a.doubleVP)]", "strEPValueE, " + strEPValueEMax,
            "strEPValueO, " + strEPValueOMax);
    }

    @Test
    public void testMinInt() {
        String queryString = "MATCH (a)-[e]->(b) return a.strVP, min(e.intEP);";
        // see testMaxInt
        long strVPValueOMin = 3;
        long strVPValueEMin = 1;
        runTest(queryString, "[a.strVP, MIN(e.intEP)]", "strVPValueE, " + strVPValueEMin,
            "strVPValueO, " + strVPValueOMin);
    }

    @Test
    public void testMinDouble() {
        String queryString = "MATCH (a)-[e]->(b) return e.strEP, min(a.doubleVP);";
        // see testAverageDouble
        double strEPValueOMax = 0.0;
        double strEPValueEMax = 0.0;
        runTest(queryString, "[e.strEP, MIN(a.doubleVP)]", "strEPValueE, " + strEPValueEMax,
            "strEPValueO, " + strEPValueOMax);
    }

    private void runTest(String queryString, String... expectedResultsList) {
        StructuredQuery query = new StructuredQueryParser().parse(queryString);
        AbstractQueryResult queryResult = new OneTimeMatchQueryPlanner(query).plan().execute();

        List<String> expectedResults = Arrays.asList(expectedResultsList);

        Assert.assertEquals(expectedResults.stream().collect(Collectors.joining(
            System.lineSeparator())), queryResult.toString());
    }
}
