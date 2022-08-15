package ca.waterloo.dsg.graphflow.query.operator.resolver;

import ca.waterloo.dsg.graphflow.graph.EdgeStore;
import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.Graph.GraphVersion;
import ca.waterloo.dsg.graphflow.graph.TypeAndPropertyKeyStore;
import ca.waterloo.dsg.graphflow.graph.VertexPropertyStore;
import ca.waterloo.dsg.graphflow.query.operator.AbstractOperator;
import ca.waterloo.dsg.graphflow.query.operator.descriptor.EdgeDescriptor;
import ca.waterloo.dsg.graphflow.query.operator.sink.AbstractOutputSink;
import ca.waterloo.dsg.graphflow.query.result.MatchQueryOutput;
import ca.waterloo.dsg.graphflow.query.result.subgraph.Edge;
import ca.waterloo.dsg.graphflow.query.result.subgraph.Edge.EdgeState;
import ca.waterloo.dsg.graphflow.query.result.subgraph.Subgraph;
import ca.waterloo.dsg.graphflow.query.result.subgraph.SubgraphType;
import ca.waterloo.dsg.graphflow.query.result.Subgraphs;
import ca.waterloo.dsg.graphflow.query.result.subgraph.Vertex;
import ca.waterloo.dsg.graphflow.util.json.JsonKeyConstants;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

/**
 * An operator that resolves the types and properties of the vertices and edges present in the
 * incoming {@link MatchQueryOutput}, and constructs a {@link Subgraph} object representing the
 * output tuple.
 */
public class SubgraphsResolver extends AbstractOperator {

    private Subgraphs subgraphs;
    private AbstractOutputSink outputSink;
    private Map<String, Integer> vertexIndices;

    /**
     * Default constructor.
     *
     * @param outputSink the output sink to append the query result to.
     * @param vertexIndices the array index of a vertex given its {@code String} id in the query.
     */
    public SubgraphsResolver(AbstractOutputSink outputSink, Map<String, Integer> vertexIndices) {
        super(null);
        this.outputSink = outputSink;
        this.vertexIndices = vertexIndices;
        subgraphs = new Subgraphs(vertexIndices);
    }

    @Override
    public void append(MatchQueryOutput matchQueryOutput) {
        addVertices(matchQueryOutput);
        addEdges(matchQueryOutput);
    }

    @Override
    public void notifyDone() {
        outputSink.append(subgraphs);
        subgraphs = new Subgraphs(vertexIndices);
    }

    @Override
    public String getHumanReadableOperator() {
        return "SubgraphsResolver";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonOperator = new JsonObject();
        jsonOperator.addProperty(JsonKeyConstants.NAME.toString(), "Subgraphs Resolver");
        return jsonOperator;
    }

    /**
     * Add the vertices in a {@link MatchQueryOutput} to the output {@code subgraphs}.
     *
     * @param matchQueryOutput the output appended from the previous operator.
     */
    private void addVertices(MatchQueryOutput matchQueryOutput) {
        for (int vertexId : matchQueryOutput.vertexIds) {
            if (!subgraphs.getVertices().containsKey(vertexId)) {
                subgraphs.getVertices().put(vertexId, resolveVertex(vertexId));
            }
        }
    }

    /**
     * Add the edges in a {@link MatchQueryOutput} to the output {@code subgraphs}.
     *
     * @param matchQueryOutput the output appended from the previous operator.
     */
    private void addEdges(MatchQueryOutput matchQueryOutput) {
        long[] edgeIds = new long[matchQueryOutput.edgeDescriptors.size()];
        for (int i = 0; i < matchQueryOutput.edgeDescriptors.size(); i++) {
            Long edgeId = matchQueryOutput.edgeDescriptors.get(i).edgeId;
            if (!subgraphs.getEdges().containsKey(edgeId)) {
                subgraphs.getEdges().put(edgeId, resolveEdge(matchQueryOutput, i));
            }
            edgeIds[i] = edgeId;
        }
        subgraphs.addSubgraph(new Subgraph(matchQueryOutput.subgraphType, matchQueryOutput.
            vertexIds, edgeIds));
    }

    /**
     * @param vertexId the vertex id to resolve its type and properties.
     *
     * @return a {@link Vertex} object given the {@code vertexId}.
     */
    public static Vertex resolveVertex(int vertexId) {
        return new Vertex(vertexId,
            TypeAndPropertyKeyStore.getInstance().mapShortToStringType(Graph.getInstance()
                .getVertexTypes().get(vertexId)),
            VertexPropertyStore.getInstance().getPropertiesAsStrings(vertexId));
    }

    /**
     * @param matchQueryOutput the output appended from the previous operator.
     * @param index the index of the edge descriptor in the match query output descriptors.
     *
     * @return an {@link Edge} object given the {@code edgeDescriptor}.
     */
    public static Edge resolveEdge(MatchQueryOutput matchQueryOutput, int index) {
        EdgeDescriptor edgeDescriptor = matchQueryOutput.edgeDescriptors.get(index);
        int fromVertexId = matchQueryOutput.vertexIds[edgeDescriptor.sourceIndex];
        int toVertexId = matchQueryOutput.vertexIds[edgeDescriptor.destinationIndex];
        String type = null;
        if (TypeAndPropertyKeyStore.ANY != edgeDescriptor.determinedEdgeType) {
            type = TypeAndPropertyKeyStore.getInstance().mapShortToStringType(edgeDescriptor.
                determinedEdgeType);
        }
        Map<String, String> properties = EdgeStore.getInstance().getPropertiesAsStrings(
            matchQueryOutput.edgeDescriptors.get(index).edgeId);
        EdgeState edgeState = getEdgeState(fromVertexId, toVertexId, matchQueryOutput.subgraphType);
        return new Edge(fromVertexId, toVertexId, type, properties, edgeState);
    }

    /**
     * @param fromVertexId the from vertex id of the edge.
     * @param toVertexId the to vertex id of the edge.
     * @param type the {@link SubgraphType} of the subgraph the edge is part of.
     *
     * @return the {@link EdgeState} of the edge given the {@code fromVertexId}, the {@code
     * toVertexId} and the {@code subgraphType}.
     */
    public static EdgeState getEdgeState(int fromVertexId, int toVertexId, SubgraphType type) {
        List<int[]> diffEdges = Graph.getInstance().getDiffEdges(SubgraphType.EMERGED == type ?
            GraphVersion.DIFF_PLUS : GraphVersion.DIFF_MINUS);
        for (int[] edge : diffEdges) {
            if (edge[0] == fromVertexId && edge[1] == toVertexId) {
                if (SubgraphType.EMERGED == type) {
                    return EdgeState.INSERTED;
                } else {
                    return EdgeState.DELETED;
                }
            }
        }
        return EdgeState.MATCHED;
    }
}
