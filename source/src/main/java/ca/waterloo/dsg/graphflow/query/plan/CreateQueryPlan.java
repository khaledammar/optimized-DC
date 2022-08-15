package ca.waterloo.dsg.graphflow.query.plan;

import ca.waterloo.dsg.graphflow.exception.IncorrectDataTypeException;
import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.TypeAndPropertyKeyStore;
import ca.waterloo.dsg.graphflow.query.executor.ContinuousMatchQueryExecutor;
import ca.waterloo.dsg.graphflow.query.result.Message;
import ca.waterloo.dsg.graphflow.query.result.AbstractQueryResult;
import ca.waterloo.dsg.graphflow.query.structuredquery.QueryRelation;
import ca.waterloo.dsg.graphflow.query.structuredquery.QueryVariable;
import ca.waterloo.dsg.graphflow.query.structuredquery.StructuredQuery;
import ca.waterloo.dsg.graphflow.util.datatype.DataType;
import org.antlr.v4.runtime.misc.Pair;

import java.util.Map;

/**
 * Class representing plan for a CREATE operation.
 */
public class CreateQueryPlan extends QueryPlan {

    private StructuredQuery structuredQuery;

    public CreateQueryPlan(StructuredQuery structuredQuery) {
        this.structuredQuery = structuredQuery;
    }

    /**
     * Executes the {@link CreateQueryPlan}.
     * An IncorrectDataTypeException is reported if there are two new properties in the query
     * with the same key but different {@link DataType} or if the {@link DataType} of a property
     * key K is not the same as the {@link DataType} that has been stored previously for K.
     */
    @Override
    public AbstractQueryResult execute() {
        String output;
        if (!structuredQuery.getQueryRelations().isEmpty()) {
            output = createEdges();
        } else {
            output = createVertices();
        }
        return new Message(output);
    }

    private String createVertices() {
        TypeAndPropertyKeyStore typeAndPropertyKeyStore = TypeAndPropertyKeyStore.getInstance();
        for (QueryVariable queryVariable : structuredQuery.getQueryVariables()) {
            Map<String, Pair<String, String>> stringVertexProperties = queryVariable.
                getVariableProperties();
            typeAndPropertyKeyStore.assertExistingKeyDataTypesMatchPreviousDeclarations(
                stringVertexProperties);

            int vertexId = Integer.parseInt(queryVariable.getVariableName());
            short vertexType = typeAndPropertyKeyStore.mapStringTypeToShortOrInsert(queryVariable.
                getVariableType());
            Map<Short, Pair<DataType, String>> vertexProperties = typeAndPropertyKeyStore.
                mapStringPropertiesToShortAndDataTypeOrInsert(stringVertexProperties);

            Graph.getInstance().addVertex(vertexId, vertexType, vertexProperties);
        }
        // TODO(amine): bug, count the actual number of vertices created to append to sink.
        return structuredQuery.getQueryVariables().size() + " vertices created.";
    }

    private String createEdges() {
        TypeAndPropertyKeyStore typeAndPropertyKeyStore = TypeAndPropertyKeyStore.getInstance();
        for (QueryRelation queryRelation : structuredQuery.getQueryRelations()) {
            Map<String, Pair<String, String>> stringFromVertexProperties = queryRelation.
                getFromQueryVariable().getVariableProperties();
            Map<String, Pair<String, String>> stringToVertexProperties = queryRelation.
                getToQueryVariable().getVariableProperties();
            Map<String, Pair<String, String>> stringEdgeProperties = queryRelation.
                getRelationProperties();

            assertDataTypesAreConsistent(stringFromVertexProperties, stringToVertexProperties);
            assertDataTypesAreConsistent(stringEdgeProperties, stringFromVertexProperties);
            assertDataTypesAreConsistent(stringEdgeProperties, stringToVertexProperties);

            typeAndPropertyKeyStore.assertExistingKeyDataTypesMatchPreviousDeclarations(
                stringFromVertexProperties);
            typeAndPropertyKeyStore.assertExistingKeyDataTypesMatchPreviousDeclarations(
                stringToVertexProperties);
            typeAndPropertyKeyStore.assertExistingKeyDataTypesMatchPreviousDeclarations(
                stringEdgeProperties);

            int fromVertex = Integer.parseInt(queryRelation.getFromQueryVariable().
                getVariableName());
            int toVertex = Integer.parseInt(queryRelation.getToQueryVariable().getVariableName());

            short fromVertexType = typeAndPropertyKeyStore.mapStringTypeToShortOrInsert(
                queryRelation.getFromQueryVariable().getVariableType());
            short toVertexType = typeAndPropertyKeyStore.mapStringTypeToShortOrInsert(queryRelation.
                getToQueryVariable().getVariableType());
            short edgeType = typeAndPropertyKeyStore.mapStringTypeToShortOrInsert(
                queryRelation.getRelationType());

            Map<Short, Pair<DataType, String>> fromVertexProperties = typeAndPropertyKeyStore.
                mapStringPropertiesToShortAndDataTypeOrInsert(stringFromVertexProperties);
            Map<Short, Pair<DataType, String>> toVertexProperties = typeAndPropertyKeyStore.
                mapStringPropertiesToShortAndDataTypeOrInsert(stringToVertexProperties);
            Map<Short, Pair<DataType, String>> edgeProperties = typeAndPropertyKeyStore.
                mapStringPropertiesToShortAndDataTypeOrInsert(stringEdgeProperties);

            Graph.getInstance().addEdgeTemporarily(fromVertex, toVertex, fromVertexType,
                toVertexType, fromVertexProperties, toVertexProperties, edgeType, edgeProperties);
        }
        ContinuousMatchQueryExecutor.getInstance().execute();
        Graph.getInstance().finalizeChanges();
        // TODO(amine): bug, count the actual number of edges created to append to sink.
        return structuredQuery.getQueryRelations().size() + " edges created.";
    }

    private void assertDataTypesAreConsistent(
        Map<String, Pair<String, String>> thisPropertiesCollection,
        Map<String, Pair<String, String>> thatPropertiesCollection) {
        if (null == thisPropertiesCollection || null == thatPropertiesCollection) {
            return;
        }
        for (String propertyKey : thisPropertiesCollection.keySet()) {
            String thisDataType = thisPropertiesCollection.get(propertyKey).a.toUpperCase();
            String thatDataType = null;
            if (null != thatPropertiesCollection.get(propertyKey)) {
                thatDataType = thatPropertiesCollection.get(propertyKey).a.toUpperCase();
            }
            if (null != thatDataType && !thisDataType.equals(thatDataType)) {
                throw new IncorrectDataTypeException("Inconsistent DataType usage - property key " +
                    propertyKey + " is used with two different data types: " + thisDataType +
                    " and " + thatDataType + ".");
            }
        }
    }
}
