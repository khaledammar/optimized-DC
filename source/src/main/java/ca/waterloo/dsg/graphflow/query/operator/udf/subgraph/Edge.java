package ca.waterloo.dsg.graphflow.query.operator.udf.subgraph;

/**
 * An edge of the {@link Subgraph} containing a source vertex Id, a destination vertex Id, a
 * type, a properties map of {@code String} key to {@code String} values, and an {@link EdgeUpdate}
 * value indicating whether the edge was inserted, deleted, or already existed.
 * Warning: The resolution of both properties and {@link EdgeUpdate} is correct in simple graphs
 * (one edge at most between any two vertices). The bug described in this warning is filed under
 * issue #36.
 */
public class Edge extends VertexOrEdge {

    private int fromVertexId;
    private int toVertexId;

    /**
     * @param fromVertexId The source vertex Id.
     * @param toVertexId   The destination vertex Id.
     * @param type         The edge {@code String} type.
     * @param properties   The properties as a Map<String, String>.
     * @param edgeUpdate   The operation done on the edge triggering the continuous query. A value
     *                     of {@code None} indicates the edges was in the permanent graph and is not inserted or
     *                     deleted.
     */
    Edge(int fromVertexId, int toVertexId, String type) {
        super(type);
        this.fromVertexId = fromVertexId;
        this.toVertexId = toVertexId;
    }

    public int getFromVertexId() {
        return fromVertexId;
    }

    public int getToVertexId() {
        return toVertexId;
    }
}
