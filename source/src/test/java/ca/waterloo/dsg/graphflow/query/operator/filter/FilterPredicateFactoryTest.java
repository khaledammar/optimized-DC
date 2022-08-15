package ca.waterloo.dsg.graphflow.query.operator.filter;

import ca.waterloo.dsg.graphflow.TestUtils;
import ca.waterloo.dsg.graphflow.graph.GraphDBState;
import ca.waterloo.dsg.graphflow.query.structuredquery.ComparisonPredicate;
import ca.waterloo.dsg.graphflow.query.structuredquery.InClausePredicate;
import ca.waterloo.dsg.graphflow.query.structuredquery.QueryPredicate;
import ca.waterloo.dsg.graphflow.util.datatype.RuntimeComparator.ComparisonOperator;
import org.antlr.v4.runtime.misc.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Tests the static functions of the {@link PredicateFactory} class for generating
 * {@link Predicate} lambda expressions.
 */
public class FilterPredicateFactoryTest {

    @Before
    public void setUp() throws Exception {
        GraphDBState.reset();
        String createQuery = "CREATE (0:Person {name: 'name0', age: 20, views: 120 })" +
            "-[:FOLLOWS { isRelated: true, views: 100 }]->(1:Person { name: 'name1', age: 10, " +
            "views: 50 }),(0:Person)-[:LIKES]->(1:Person),(1:Person)-[:LIKES]->" +
            "(0:Person),(1:Person)-[:TAGGED]->(3:Person),(3:Person)-[:LIKES{ rating: 4.1, " +
            "views: 300}]->(1:Person);";
        TestUtils.initializeGraphPermanentlyWithProperties(createQuery);
    }

    @Test
    public void testTwoVertexPropertyPredicate() {
        String propertyKey = "age";
        ComparisonPredicate comparisonPredicate = TestUtils.createComparisonPredicate(
            new Pair<>("a", propertyKey), new Pair<>("b", propertyKey), null, ComparisonOperator.
                GREATER_THAN);
        List<QueryPredicate> queryPredicates = new ArrayList<>();
        queryPredicates.add(comparisonPredicate);

        Map<String, Integer> descriptorIndexMap = new HashMap<>();
        descriptorIndexMap.put("a." + propertyKey, 0);
        descriptorIndexMap.put("b." + propertyKey, 2);
        Predicate<String[]> predicate = PredicateFactory.getFilterPredicate(queryPredicates,
            descriptorIndexMap);
        String[] resolvedProperties = {"15", "20", "10"};
        Assert.assertTrue(predicate.test(resolvedProperties));
    }

    @Test
    public void testEdgeAndLiteralPropertyPredicate() {
        String propertyKey = "views";
        ComparisonPredicate comparisonPredicate = TestUtils.createComparisonPredicate(
            new Pair<>("a", propertyKey), null, "74", ComparisonOperator.GREATER_THAN_OR_EQUAL);
        List<QueryPredicate> queryPredicates = new ArrayList<>();
        queryPredicates.add(comparisonPredicate);

        Map<String, Integer> descriptorIndexMap = new HashMap<>();
        descriptorIndexMap.put("a." + propertyKey, 0);
        Predicate<String[]> predicate = PredicateFactory.getFilterPredicate(queryPredicates,
            descriptorIndexMap);
        String[] resolvedProperties = {"75", "20", "10"};
        Assert.assertTrue(predicate.test(resolvedProperties));
    }

    @Test
    public void testInClausePredicateMatchVariable() {
        String propertyKey = "views";
        List<Pair<String, String>> variables = new ArrayList<>();
        variables.add(new Pair<>("b", propertyKey));

        List<String> literals = new ArrayList<>();
        literals.add("40");
        InClausePredicate inClausePredicate = TestUtils.createInClausePredicate(
            new Pair<>("a", propertyKey), variables, literals);
        List<QueryPredicate> queryPredicates = new ArrayList<>();
        queryPredicates.add(inClausePredicate);

        Map<String, Integer> descriptorIndexMap = new HashMap<>();
        descriptorIndexMap.put("a." + propertyKey, 0);
        descriptorIndexMap.put("b." + propertyKey, 1);
        Predicate<String[]> predicate = PredicateFactory.getFilterPredicate(queryPredicates,
            descriptorIndexMap);
        String[] resolvedProperties = {"20", "20"};
        Assert.assertTrue(predicate.test(resolvedProperties));
    }

    @Test
    public void testInClausePredicateMatchLiteral() {
        String propertyKey = "views";
        List<Pair<String, String>> variables = new ArrayList<>();
        variables.add(new Pair<>("b", propertyKey));

        List<String> literals = new ArrayList<>();
        literals.add("40");
        InClausePredicate inClausePredicate = TestUtils.createInClausePredicate(
            new Pair<>("a", propertyKey), variables, literals);
        List<QueryPredicate> queryPredicates = new ArrayList<>();
        queryPredicates.add(inClausePredicate);

        Map<String, Integer> descriptorIndexMap = new HashMap<>();
        descriptorIndexMap.put("a." + propertyKey, 0);
        descriptorIndexMap.put("b." + propertyKey, 1);
        Predicate<String[]> predicate = PredicateFactory.getFilterPredicate(queryPredicates,
            descriptorIndexMap);
        String[] resolvedProperties = {"40", "20"};
        Assert.assertTrue(predicate.test(resolvedProperties));
    }

    @Test
    public void testInClausePredicateMatchNone() {
        String propertyKey = "age";
        List<Pair<String, String>> variables = new ArrayList<>();
        variables.add(new Pair<>("b", propertyKey));

        List<String> literals = new ArrayList<>();
        literals.add("50");
        InClausePredicate inClausePredicate = TestUtils.createInClausePredicate(
            new Pair<>("a", propertyKey), variables, literals);
        List<QueryPredicate> queryPredicates = new ArrayList<>();
        queryPredicates.add(inClausePredicate);

        Map<String, Integer> descriptorIndexMap = new HashMap<>();
        descriptorIndexMap.put("a." + propertyKey, 0);
        descriptorIndexMap.put("b." + propertyKey, 1);
        Predicate<String[]> predicate = PredicateFactory.getFilterPredicate(queryPredicates,
            descriptorIndexMap);
        String[] resolvedProperties = {"29", "20"};
        Assert.assertFalse(predicate.test(resolvedProperties));

    }
}