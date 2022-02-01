package ca.waterloo.dsg.graphflow.query.plans;

import ca.waterloo.dsg.graphflow.exceptions.IncorrectDataTypeException;
import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.TypeAndPropertyKeyStore;
import ca.waterloo.dsg.graphflow.query.executors.csp.ContinuousShortestPathsExecutor;
import ca.waterloo.dsg.graphflow.query.operator.AbstractDBOperator;
import ca.waterloo.dsg.graphflow.query.structuredquery.QueryRelation;
import ca.waterloo.dsg.graphflow.query.structuredquery.QueryVariable;
import ca.waterloo.dsg.graphflow.query.structuredquery.StructuredQuery;
import ca.waterloo.dsg.graphflow.util.DataType;
import org.antlr.v4.runtime.misc.Pair;

import java.util.Map;

/**
 * Class representing plan for a CREATE operation.
 */
public class CreateQueryPlan implements QueryPlan {

    private StructuredQuery structuredQuery;

    public CreateQueryPlan(StructuredQuery structuredQuery) {
        this.structuredQuery = structuredQuery;
    }

    /**
     * Executes the {@link CreateQueryPlan}.
     *
     * @param graph      the {@link Graph} instance to use during the plan execution.
     * @param outputSink the {@link AbstractDBOperator} to which the execution output is written.
     * @throws IncorrectDataTypeException if there are two new properties in the query with the
     *                                    same key but different {@link DataType} or if the {@link DataType} of a property key K is
     *                                    not the same as the {@link DataType} that has been stored previously for K.
     */
    public void execute(Graph graph, AbstractDBOperator outputSink) {
        if (structuredQuery.getQueryRelations().size() > 0) {
            createEdges(graph, outputSink);
        } else {
            createVertices(graph, outputSink);
        }
    }

    private void createVertices(Graph graph, AbstractDBOperator outputSink) {
        TypeAndPropertyKeyStore typeAndPropertyKeyStore = TypeAndPropertyKeyStore.getInstance();
        try {
            for (QueryVariable queryVariable : structuredQuery.getQueryVariables()) {
                Map<String, Pair<String, String>> stringvertexProperties = queryVariable.getVariableProperties();
                typeAndPropertyKeyStore.assertExistingKeyDataTypesMatchPreviousDeclarations(stringvertexProperties);

                int vertexId = Integer.parseInt(queryVariable.getVariableName());
                short vertexType =
                        typeAndPropertyKeyStore.mapStringTypeToShortOrInsert(queryVariable.getVariableType());
                Map<Short, Pair<DataType, String>> vertexProperties =
                        typeAndPropertyKeyStore.mapStringPropertiesToShortAndDataTypeOrInsert(stringvertexProperties);

                graph.addVertex(vertexId, vertexType, vertexProperties);
            }
            // TODO(amine): bug, count the actual number of vertices created to append to sink.
            outputSink.append(structuredQuery.getQueryVariables().size() + " vertices created.");
        } catch (UnsupportedOperationException e) {
            outputSink.append("ERROR: " + e.getMessage());
        }
    }

    private void createEdges(Graph graph, AbstractDBOperator outputSink) {
        TypeAndPropertyKeyStore typeAndPropertyKeyStore = TypeAndPropertyKeyStore.getInstance();
        try {
            for (QueryRelation queryRelation : structuredQuery.getQueryRelations()) {
                Map<String, Pair<String, String>> stringFromVertexProperties =
                        queryRelation.getFromQueryVariable().getVariableProperties();
                Map<String, Pair<String, String>> stringToVertexProperties =
                        queryRelation.getToQueryVariable().getVariableProperties();
                Map<String, Pair<String, String>> stringEdgeProperties = queryRelation.getRelationProperties();

                assertDataTypesAreConsistent(stringFromVertexProperties, stringToVertexProperties);
                assertDataTypesAreConsistent(stringEdgeProperties, stringFromVertexProperties);
                assertDataTypesAreConsistent(stringEdgeProperties, stringToVertexProperties);

                typeAndPropertyKeyStore.assertExistingKeyDataTypesMatchPreviousDeclarations(stringFromVertexProperties);
                typeAndPropertyKeyStore.assertExistingKeyDataTypesMatchPreviousDeclarations(stringToVertexProperties);
                typeAndPropertyKeyStore.assertExistingKeyDataTypesMatchPreviousDeclarations(stringEdgeProperties);

                int fromVertex = Integer.parseInt(queryRelation.getFromQueryVariable().getVariableName());
                int toVertex = Integer.parseInt(queryRelation.getToQueryVariable().getVariableName());

                short fromVertexType = typeAndPropertyKeyStore
                        .mapStringTypeToShortOrInsert(queryRelation.getFromQueryVariable().getVariableType());
                short toVertexType = typeAndPropertyKeyStore
                        .mapStringTypeToShortOrInsert(queryRelation.getToQueryVariable().getVariableType());
                short edgeType = typeAndPropertyKeyStore.mapStringTypeToShortOrInsert(queryRelation.getRelationType());

                graph.addEdgeTemporarily(fromVertex, toVertex, fromVertexType, toVertexType, queryRelation.weight,
                        edgeType);
            }
            ContinuousShortestPathsExecutor.getInstance().execute();
            graph.finalizeChanges();
            // TODO(amine): bug, count the actual number of edges created to append to sink.
            outputSink.append(structuredQuery.getQueryRelations().size() + " edges created.");
        } catch (UnsupportedOperationException e) {
            outputSink.append("ERROR: " + e.getMessage());
        }
    }

    private void assertDataTypesAreConsistent(Map<String, Pair<String, String>> thisPropertiesCollection,
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
                throw new IncorrectDataTypeException("Inconsistent DataType usage - property " + "key " + propertyKey +
                        " is used with two different data types: " + thisDataType + " and " + thatDataType + ".");
            }
        }
    }
}
