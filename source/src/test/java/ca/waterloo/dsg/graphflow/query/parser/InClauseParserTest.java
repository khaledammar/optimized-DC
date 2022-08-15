package ca.waterloo.dsg.graphflow.query.parser;

import ca.waterloo.dsg.graphflow.TestUtils;
import ca.waterloo.dsg.graphflow.query.structuredquery.InClausePredicate;
import ca.waterloo.dsg.graphflow.query.structuredquery.QueryPredicate.PredicateType;
import ca.waterloo.dsg.graphflow.query.structuredquery.QueryRelation;
import ca.waterloo.dsg.graphflow.query.structuredquery.QueryVariable;
import ca.waterloo.dsg.graphflow.query.structuredquery.StructuredQuery;
import org.antlr.v4.runtime.misc.Pair;
import org.junit.Test;

/**
 * Tests {@link StructuredQueryParser} for the IN clause.
 */
public class InClauseParserTest {

    @Test
    public void testInClauseVariablesOnly() throws Exception {
        String query = "MATCH (a)->(b) WHERE a.name IN [b.age, b.name, b.value];";
        StructuredQuery actualStructuredQuery = new StructuredQueryParser().parse(query);

        StructuredQuery expectedStructuredQuery = new StructuredQuery();
        expectedStructuredQuery.addRelation(new QueryRelation(new QueryVariable("a"),
            new QueryVariable("b")));
        expectedStructuredQuery.setQueryOperation(StructuredQuery.QueryOperation.MATCH);

        InClausePredicate expectedPredicate = new InClausePredicate();
        expectedPredicate.setLeftOperand(new Pair<>("a", "name"));
        expectedPredicate.setPredicateType(PredicateType.IN_CLAUSE_VARIABLES_AND_LITERALS);
        expectedPredicate.addVariableWithProperty(new Pair<>("b", "age"));
        expectedPredicate.addVariableWithProperty(new Pair<>("b", "name"));
        expectedPredicate.addVariableWithProperty(new Pair<>("b", "value"));
        expectedStructuredQuery.addQueryPredicate(expectedPredicate);

        TestUtils.assertEquals(expectedStructuredQuery, actualStructuredQuery);
    }

    @Test
    public void testInClauseVariablesAndLiterals() throws Exception {
        String query = "MATCH (a)->(b) WHERE a.name IN [\"a\", b.name, \"b\"];";
        StructuredQuery actualStructuredQuery = new StructuredQueryParser().parse(query);

        StructuredQuery expectedStructuredQuery = new StructuredQuery();
        expectedStructuredQuery.addRelation(new QueryRelation(new QueryVariable("a"),
            new QueryVariable("b")));
        expectedStructuredQuery.setQueryOperation(StructuredQuery.QueryOperation.MATCH);

        InClausePredicate expectedPredicate = new InClausePredicate();
        expectedPredicate.setLeftOperand(new Pair<>("a", "name"));
        expectedPredicate.setPredicateType(PredicateType.IN_CLAUSE_VARIABLES_AND_LITERALS);
        expectedPredicate.addLiteral("a");
        expectedPredicate.addLiteral("b");
        expectedPredicate.addVariableWithProperty(new Pair<>("b", "name"));
        expectedStructuredQuery.addQueryPredicate(expectedPredicate);

        TestUtils.assertEquals(expectedStructuredQuery, actualStructuredQuery);
    }
}

