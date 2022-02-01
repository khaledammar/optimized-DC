package ca.waterloo.dsg.graphflow;

import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.TypeAndPropertyKeyStore;
import ca.waterloo.dsg.graphflow.query.executors.MatchQueryResultType;
import ca.waterloo.dsg.graphflow.query.operator.AbstractDBOperator;
import ca.waterloo.dsg.graphflow.query.operator.InMemoryOutputSink;
import ca.waterloo.dsg.graphflow.query.parser.StructuredQueryParser;
import ca.waterloo.dsg.graphflow.query.planner.CreateQueryPlanner;
import ca.waterloo.dsg.graphflow.query.plans.CreateQueryPlan;
import ca.waterloo.dsg.graphflow.query.structuredquery.QueryPropertyPredicate;
import ca.waterloo.dsg.graphflow.query.structuredquery.QueryPropertyPredicate.PredicateType;
import ca.waterloo.dsg.graphflow.query.structuredquery.QueryRelation;
import ca.waterloo.dsg.graphflow.query.structuredquery.StructuredQuery;
import ca.waterloo.dsg.graphflow.util.RuntimeTypeBasedComparator.ComparisonOperator;
import org.antlr.v4.runtime.misc.Pair;

import java.util.Arrays;
import java.util.StringJoiner;

/**
 * Provides utility functions for tests.
 */
public class TestUtils {

    /**
     * Creates and returns a graph initialized with the given {@code edges}, {@code edgeTypes} and
     * {@code vertexTypes}.
     *
     * @param edges       The edges {e=(u,v)} of the graph.
     * @param edgeTypes   The type of each edge e.
     * @param vertexTypes The types {@code (t1, t2)} where t1 is the type of source vertex u and t2
     *                    is the type of destination vertex v.
     * @return Graph The initialized graph.
     */
    public static Graph initializeGraphPermanently(int[][] edges, short[] edgeTypes, short[][] vertexTypes) {
        Graph graph = initializeGraphTemporarily(edges, edgeTypes, vertexTypes);
        graph.finalizeChanges();
        return graph;
    }

    public static void initializeGraphPermanently(int[][] edges, double[] weights, short[] edgeTypes,
                                                  short[][] vertexTypes) {
        Graph graph = Graph.getInstance();
        for (int i = 0; i < edges.length; i++) {
            graph.addEdgeTemporarily(edges[i][0], edges[i][1], vertexTypes[i][0], vertexTypes[i][1], weights[i],
                    edgeTypes[i]);
        }
        graph.finalizeChanges();
    }

    /**
     * Creates and returns a graph with with the given {@code edges}, {@code edgeTypes} and {@code
     * vertexTypes} added temporarily.
     *
     * @param edges       The edges {e=(u,v)} of the graph.
     * @param edgeTypes   The type of each edge e.
     * @param vertexTypes The types {@code (t1, t2)} where t1 is the type of source vertex u and t2
     *                    is the type of destination vertex v.
     * @return Graph The graph initialized with temporary edges.
     */
    public static Graph initializeGraphTemporarily(int[][] edges, short[] edgeTypes, short[][] vertexTypes) {
        Graph graph = Graph.getInstance();
        for (int i = 0; i < edges.length; i++) {
            graph.addEdgeTemporarily(edges[i][0], edges[i][1], vertexTypes[i][0], vertexTypes[i][1], edgeTypes[i]);
        }
        return graph;
    }

    /**
     * Returns the CREATE EDGES query string given a set of edges to insert that are given
     * in an array (see below) and optionally weights on the edges.
     *
     * @param edges   list of edges to add represented as a double array, where each array contains two
     *                integers, the source and the destination in that order.
     * @param weights optional weights on the edges.
     * @return CREATE EDGES string query that can be executed against the database.
     */
    public static String getCreateQueryString(int[][] edges, double[] weights) {
        StringBuilder sb = new StringBuilder("CREATE ");
        for (int i = 0; i < edges.length; ++i) {
            sb.append("(" + edges[i][0] + ":Person)-[:FOLLOWS");
            if (null != weights) {
                sb.append(" WGHT:" + weights[i]);
            }
            sb.append("]->(" + edges[i][1] + ":Person)");
            if (i < (edges.length - 1)) {
                sb.append(", ");
            }
        }
        sb.append(";");
        System.out.println("Create query: \n" + sb.toString());
        return sb.toString();
    }

    public static String getDeleteQueryString(int[][] edges) {
        StringBuilder sb = new StringBuilder("DELETE ");
        for (int i = 0; i < edges.length; ++i) {
            sb.append("(" + edges[i][0] + ")->(" + edges[i][1] + ")");
            if (i < (edges.length - 1)) {
                sb.append(", ");
            }
        }
        sb.append(";");
        System.out.println("Delete query: \n" + sb.toString());
        return sb.toString();
    }

    /**
     * @param graph The {@link Graph} instance to which the edges should be added.
     * @param edges edges to add.
     */
    public static void createEdgesPermanently(Graph graph, int[][] edges) {
        createEdgesPermanently(graph, edges, null /* weights */);
    }

    /**
     * @param graph The {@link Graph} instance to which the edges should be added.
     * @param edges edges to add.
     */
    public static void createEdgesPermanently(Graph graph, int[][] edges, double[] weights) {
        createEdgesTemporarily(graph, edges, weights);
        graph.finalizeChanges();
    }

    /**
     * Adds a set of unweighted edges to the given {@code graph}.
     *
     * @param graph The {@link Graph} instance to which the edges should be added.
     * @param edges edges to add.
     */
    public static void createEdgesTemporarily(Graph graph, int[][] edges) {
        createEdgesTemporarily(graph, edges, null /* no weights */);
    }

    /**
     * Adds a set of weighted edges to the given {@code graph}.
     *
     * @param graph   The {@link Graph} instance to which the edges should be added.
     * @param edges   edges to add.
     * @param weights weights of the given edges.
     */
    public static void createEdgesTemporarily(Graph graph, int[][] edges, double[] weights) {
        createEdgesTemporarily(graph, getCreateQueryString(edges, weights));
    }

    /**
     * Adds a set of edges to the given {@code graph} temporarily by executing the given {@code
     * createQuery}.
     *
     * @param graph       The {@link Graph} instance to which the edges should be added.
     * @param createQuery The {@code String} create query to be executed.
     */
    public static void createEdgesTemporarily(Graph graph, String createQuery) {
        StructuredQuery structuredQuery = new StructuredQueryParser().parse(createQuery);
        for (QueryRelation queryRelation : structuredQuery.getQueryRelations()) {
            int fromVertex = Integer.parseInt(queryRelation.getFromQueryVariable().getVariableName());
            int toVertex = Integer.parseInt(queryRelation.getToQueryVariable().getVariableName());
            // Insert the types into the {@code TypeStore} if they do not already exist, and
            // get their {@code short} IDs. An exception in the above {@code parseInt()} calls
            // will prevent the insertion of any new type to the {@code TypeStore}.
            short fromVertexTypeId = TypeAndPropertyKeyStore.getInstance()
                    .mapStringTypeToShortOrInsert(queryRelation.getFromQueryVariable().getVariableType());
            short toVertexTypeId = TypeAndPropertyKeyStore.getInstance()
                    .mapStringTypeToShortOrInsert(queryRelation.getToQueryVariable().getVariableType());
            short edgeTypeId =
                    TypeAndPropertyKeyStore.getInstance().mapStringTypeToShortOrInsert(queryRelation.getRelationType());
            // Add the new edge to the graph.
            graph.addEdgeTemporarily(fromVertex, toVertex, fromVertexTypeId, toVertexTypeId, queryRelation.weight,
                    edgeTypeId);
        }
    }

    /**
     * Deletes a set of edges from the given {@code graph} permanently by executing the given {@code
     * deleteQuery}.
     *
     * @param graph The {@link Graph} instance from which the edges should be deleted.
     * @param edges The edges to delete.
     */
    public static void deleteEdgesPermanently(Graph graph, int[][] edges) {
        deleteEdgesTemporarily(graph, edges);
        graph.finalizeChanges();
    }

    /**
     * Deletes a set of edges from the given {@code graph} temporarily by executing the given {@code
     * deleteQuery}.
     *
     * @param graph The {@link Graph} instance from which the edges should be deleted.
     * @param edges The edges to delete.
     */
    public static void deleteEdgesTemporarily(Graph graph, int[][] edges) {
        StructuredQuery structuredQuery = new StructuredQueryParser().parse(getDeleteQueryString(edges));
        for (QueryRelation queryRelation : structuredQuery.getQueryRelations()) {
            graph.deleteEdgeTemporarily(Integer.parseInt(queryRelation.getFromQueryVariable().getVariableName()),
                    Integer.parseInt(queryRelation.getToQueryVariable().getVariableName()),
                    TypeAndPropertyKeyStore.getInstance().mapStringTypeToShort(queryRelation.getRelationType()));
        }
    }

    /**
     * Initializes the {@link Graph} with the given {@code createQuery}.
     *
     * @param createQuery a {@code String} representing a CREATE query which will be parsed and
     *                    used to initialize the {@link Graph}.
     */
    public static void initializeGraphPermanentlyWithProperties(String createQuery) {
        StructuredQuery structuredQuery = new StructuredQueryParser().parse(createQuery);
        AbstractDBOperator outputSink = new InMemoryOutputSink();
        ((CreateQueryPlan) new CreateQueryPlanner(structuredQuery).plan()).execute(Graph.getInstance(), outputSink);
    }

    /**
     * Creates a {@link QueryPropertyPredicate} using the given parameters.
     *
     * @param variable1          A {@code Pair<String, Short>} which will be the left operand in the
     *                           {@link QueryPropertyPredicate} to be created.
     * @param variable2          A {@code Pair<String, Short>} which will be the right operand in the
     *                           {@link QueryPropertyPredicate} to be created. Mutually exclusive with {@code literal}.
     * @param literal            A {@code String} which will be the right operand in the
     *                           {@link QueryPropertyPredicate} to be created. Mutually exclusive with {@code variable1}.
     * @param comparisonOperator A {@link ComparisonOperator} specifying the comparison operator
     *                           of the {@link QueryPropertyPredicate} to be created.
     * @return A {@link QueryPropertyPredicate} created using the given parameters.
     */
    public static QueryPropertyPredicate createQueryPropertyPredicate(Pair<String, String> variable1,
                                                                      Pair<String, String> variable2, String literal,
                                                                      ComparisonOperator comparisonOperator) {
        QueryPropertyPredicate queryPropertyPredicate = new QueryPropertyPredicate();
        queryPropertyPredicate.setLeftOperand(variable1);
        queryPropertyPredicate.setRightOperand(variable2);
        queryPropertyPredicate.setLiteral(literal);
        queryPropertyPredicate.setComparisonOperator(comparisonOperator);
        if (null == queryPropertyPredicate.getLiteral()) {
            queryPropertyPredicate.setPredicateType(PredicateType.TWO_PROPERTY_KEY_OPERANDS);
        } else {
            queryPropertyPredicate.setPredicateType(PredicateType.PROPERTY_KEY_AND_LITERAL_OPERANDS);
        }
        return queryPropertyPredicate;
    }

    /**
     * @see TestUtils#getInMemoryOutputSinkForMotifs(Object[][], MatchQueryResultType[])
     */
    public static InMemoryOutputSink getInMemoryOutputSinkForMotifs(Object[][] results) {
        MatchQueryResultType[] matchQueryResultTypes = new MatchQueryResultType[results.length];
        Arrays.fill(matchQueryResultTypes, MatchQueryResultType.MATCHED);
        return getInMemoryOutputSinkForMotifs(results, matchQueryResultTypes);
    }

    /**
     * Creates an {@link InMemoryOutputSink} containing the results given in {@code results}.
     *
     * @param results               a {@code Object[][]} where the outer array is a list of {@code Object[]}
     *                              result records.
     * @param matchQueryResultTypes an array of {@link MatchQueryResultType}s that will be
     *                              appended to the end of each record added to the {@link InMemoryOutputSink}.
     * @return an {@link InMemoryOutputSink} containing the given results.
     */
    public static InMemoryOutputSink getInMemoryOutputSinkForMotifs(Object[][] results,
                                                                    MatchQueryResultType[] matchQueryResultTypes) {
        InMemoryOutputSink inMemoryOutputSink = new InMemoryOutputSink();
        StringJoiner joiner;
        for (int i = 0; i < results.length; i++) {
            joiner = new StringJoiner(" ");
            for (Object element : results[i]) {
                joiner.add(element.toString());
            }
            inMemoryOutputSink.append(joiner.toString());
        }
        return inMemoryOutputSink;
    }

    public static String getExpectedContentOfOutputFileSink(int[][] results,
                                                            MatchQueryResultType[] matchQueryResultTypes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < results.length; ++i) {
            stringBuilder.append(Arrays.toString(results[i])).append(" ").append(matchQueryResultTypes[i].name());
            if (i < results.length - 1) {
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
