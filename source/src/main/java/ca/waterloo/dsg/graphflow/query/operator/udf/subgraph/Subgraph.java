package ca.waterloo.dsg.graphflow.query.operator.udf.subgraph;

import java.util.List;
import java.util.Map;

/**
 * A wrapper to the list of vertices and list of edges with the following getter functionality:
 * <ul>
 * <li> Vertex type and properties using the {@code String} variable name from the query graph.
 * <li> Edge type and properties using the {@code String} fromVertex variable name and the {@code
 * String} toVertex variable name.</li>
 * </ul>
 * <p>
 * Note: The edge type is that specified by the user in the query. If the user didn't specify a
 * type, the type is set to {@code null}.
 * Warning: The resolution of both properties and {@link EdgeUpdate} is guaranteed to be correct
 * only in simple graphs (one edge at most between any two vertices). The bug described in this
 * warning is filed under issue #36.
 * </p>
 */
public class Subgraph {

    public enum SubgraphType {
        EMERGED,
        DELETED
    }
    private Map<String, Integer> vertexIndices;
    private List<Vertex> vertices;
    private SubgraphType subgraphType;

    /**
     * @param vertices      The list of vertices in the subgraph.
     * @param vertexIndices A map of the {@code String} vertex variable as provided by the user
     *                      in the query to its index in the list of vertices.
     */
    Subgraph(List<Vertex> vertices, SubgraphType subgraphType, Map<String, Integer> vertexIndices) {
        this.vertices = vertices;
        this.vertexIndices = vertexIndices;
        this.subgraphType = subgraphType;
    }

    public SubgraphType getSubgraphType() {
        return subgraphType;
    }

    public Vertex getVertex(String vertexVariable) {
        return vertices.get(vertexIndices.get(vertexVariable));
    }

    private int getVertexId(String vertexVariable) {
        return vertices.get(vertexIndices.get(vertexVariable)).getId();
    }
}
