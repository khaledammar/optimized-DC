package ca.waterloo.dsg.graphflow.query.validator;

import ca.waterloo.dsg.graphflow.exception.IncorrectVertexTypeException;
import ca.waterloo.dsg.graphflow.exception.MalformedMatchQueryException;
import ca.waterloo.dsg.graphflow.exception.MalformedReturnClauseException;
import ca.waterloo.dsg.graphflow.exception.NoSuchPropertyKeyException;
import ca.waterloo.dsg.graphflow.graph.TypeAndPropertyKeyStore;
import ca.waterloo.dsg.graphflow.query.structuredquery.QueryAggregation;
import ca.waterloo.dsg.graphflow.query.structuredquery.QueryGraph;
import ca.waterloo.dsg.graphflow.query.structuredquery.QueryPredicate;
import ca.waterloo.dsg.graphflow.query.structuredquery.QueryRelation;
import ca.waterloo.dsg.graphflow.query.structuredquery.QueryVariable;
import ca.waterloo.dsg.graphflow.query.structuredquery.StructuredQuery;
import org.antlr.v4.runtime.misc.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Given a MATCH {@link StructuredQuery}, the class constructs a {@link QueryGraph} for the MATCH
 * Query Planners and validates the syntax and semantics of the query.
 */
public class MatchQueryValidator {

    private static final String UNDEFINED_VARIABLE_ERROR_MESSAGE = "clause contains " +
        "variables that are not defined in the MATCH clause.";
    private static final String UNDEFINED_VARIABLE_IN_WHERE_CLAUSE_ERROR_MESSAGE = "WHERE " +
        UNDEFINED_VARIABLE_ERROR_MESSAGE;
    private static final String UNDEFINED_VARIABLE_IN_RETURN_CLAUSE_ERROR_MESSAGE = "RETURN " +
        UNDEFINED_VARIABLE_ERROR_MESSAGE;

    private QueryGraph queryGraph = new QueryGraph();
    private StructuredQuery structuredQuery;

    public MatchQueryValidator(StructuredQuery structuredQuery) {
        this.structuredQuery = structuredQuery;
    }

    public QueryGraph validateQueryAndGetQueryGraph() {
        Map<String, String> variableTypeMap = checkQueryVariableTypesAreConsistent();
        setMissingVariableTypes(variableTypeMap);
        for (QueryRelation queryRelation : structuredQuery.getQueryRelations()) {
            TypeAndPropertyKeyStore.getInstance().mapStringTypeToShortAndAssertTypeExists(
                queryRelation.getRelationType());
            TypeAndPropertyKeyStore.getInstance().mapStringTypeToShortAndAssertTypeExists(
                queryRelation.getFromQueryVariable().getVariableType());
            TypeAndPropertyKeyStore.getInstance().mapStringTypeToShortAndAssertTypeExists(
                queryRelation.getToQueryVariable().getVariableType());
            queryGraph.addRelation(queryRelation);
        }
        validateReturnClause();
        validateEdgeAndVertexVariablesAreDistinct();
        validatePredicates();
        return queryGraph;
    }

    private void validateEdgeAndVertexVariablesAreDistinct() {
        Set<String> variableNames = queryGraph.getAllVariableNames();
        for (String relationName : queryGraph.getAllRelationNames()) {
            if (variableNames.contains(relationName)) {
                throw new MalformedMatchQueryException("Edge variable: " + relationName +
                    " has also been defined as a vertex variable in the query.");
            }
        }
    }

    private void validateReturnClause() {
        for (String variable : structuredQuery.getReturnVariables()) {
            checkVariableIsDefined(variable, UNDEFINED_VARIABLE_IN_RETURN_CLAUSE_ERROR_MESSAGE);
        }
        for (Pair<String, String> variablePropertyPair :
            structuredQuery.getReturnVariablePropertyPairs()) {
            checkVariableIsDefinedAndPropertyExists(variablePropertyPair);
        }
        for (QueryAggregation queryAggregation : structuredQuery.getQueryAggregations()) {
            if (null != queryAggregation.getVariable()) {
                checkVariableIsDefined(queryAggregation.getVariable(),
                    UNDEFINED_VARIABLE_IN_RETURN_CLAUSE_ERROR_MESSAGE);
            } else if (null != queryAggregation.getVariablePropertyPair()) {
                checkVariableIsDefinedAndPropertyExists(queryAggregation.getVariablePropertyPair());
            }
        }
    }

    private void checkVariableIsDefinedAndPropertyExists(Pair<String, String>
        variablePropertyPair) {
        String variable = variablePropertyPair.a;
        checkVariableIsDefined(variable, UNDEFINED_VARIABLE_IN_RETURN_CLAUSE_ERROR_MESSAGE);
        String propertyKey = variablePropertyPair.b;
        if (!TypeAndPropertyKeyStore.getInstance().isPropertyDefined(propertyKey)) {
            throw new NoSuchPropertyKeyException(propertyKey);
        }
    }

    private void checkVariableIsDefined(String variable, String errorMessage) {
        if (!queryGraph.getAllVariableNames().contains(variable) &&
            !queryGraph.getAllRelationNames().contains(variable)) {
            throw new MalformedReturnClauseException(errorMessage);
        }
    }

    private void validatePredicates() {
        for (QueryPredicate predicate : structuredQuery.getAllQueryPredicates()) {
            for (Pair<String, String> variable : predicate.getAllVariables()) {
                checkVariableIsDefined(variable.a,
                    UNDEFINED_VARIABLE_IN_WHERE_CLAUSE_ERROR_MESSAGE);
                predicate.validateTypes();
            }
        }
    }

    private Map<String, String> checkQueryVariableTypesAreConsistent() {
        Map<String, String> variableTypeMap = new HashMap<>();
        for (QueryRelation queryRelation : structuredQuery.getQueryRelations()) {
            QueryVariable fromQueryVariable = queryRelation.getFromQueryVariable();
            QueryVariable toQueryVariable = queryRelation.getToQueryVariable();
            assetQueryVariableTypeIsConsistent(fromQueryVariable.getVariableName(),
                fromQueryVariable.getVariableType(), variableTypeMap);
            assetQueryVariableTypeIsConsistent(toQueryVariable.getVariableName(),
                toQueryVariable.getVariableType(), variableTypeMap);
        }
        return variableTypeMap;
    }

    private void setMissingVariableTypes(Map<String, String> variableTypeMap) {
        for (QueryRelation queryRelation : structuredQuery.getQueryRelations()) {
            String fromQueryVariable = queryRelation.getFromQueryVariable().getVariableName();
            String toQueryVariable = queryRelation.getToQueryVariable().getVariableName();
            String fromQueryType = variableTypeMap.get(fromQueryVariable);
            String toQueryType = variableTypeMap.get(toQueryVariable);
            queryRelation.getFromQueryVariable().setVariableType(fromQueryType);
            queryRelation.getToQueryVariable().setVariableType(toQueryType);
        }
    }

    private void assetQueryVariableTypeIsConsistent(String variableName, String variableType,
        Map<String, String> variableTypeMap) {
        if (!variableTypeMap.containsKey(variableName) ||
            null == variableTypeMap.get(variableName)) {
            variableTypeMap.put(variableName, variableType);
        } else if (null != variableType &&
            !variableType.equals(variableTypeMap.get(variableName))) {
            throw new IncorrectVertexTypeException("Incorrect type usage - The query variable '" +
                variableName + "' in the MATCH clause is used with two different types: '" +
                variableType + "' and '" + variableTypeMap.get(variableName) + "'.");
        }
    }
}
