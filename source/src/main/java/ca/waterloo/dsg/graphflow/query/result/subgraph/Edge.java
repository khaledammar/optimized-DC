package ca.waterloo.dsg.graphflow.query.result.subgraph;

import ca.waterloo.dsg.graphflow.util.json.JsonKeyConstants;
import com.google.gson.JsonObject;

import java.util.Map;

/**
 * An edge of the {@link Subgraph} containing a source vertex Id, a destination vertex Id, a
 * type, a properties map of {@code String} key to {@code String} values, and an {@link EdgeState}
 * value indicating whether the edge was inserted, deleted, or already existed.
 * <p>
 * Warning: The resolution of both properties and {@link EdgeState} is correct in simple graphs
 * (one edge at most between any two vertices). The bug described in this warning is filed under
 * issue #36.
 * </p>
 */
public class Edge extends VertexOrEdge {

    /**
     * Used to represent whether an edge was added, deleted, or matched.
     */
    public enum EdgeState {
        INSERTED,
        DELETED,
        MATCHED
    }

    private int fromVertexId;
    private int toVertexId;
    private EdgeState edgeState;

    /**
     * @param fromVertexId The source vertex Id.
     * @param toVertexId The destination vertex Id.
     * @param type The edge {@code String} type.
     * @param properties The properties as a Map<String, String>.
     * @param edgeState The operation done on the edge triggering the query. A value of {@code
     * MATCHED} indicates the edges was in the permanent graph and is not inserted or deleted.
     */
    public Edge(int fromVertexId, int toVertexId, String type, Map<String, String> properties,
        EdgeState edgeState) {
        super(type, properties);
        this.fromVertexId = fromVertexId;
        this.toVertexId = toVertexId;
        this.edgeState = edgeState;
    }

    public int getFromVertexId() {
        return fromVertexId;
    }

    public int getToVertexId() {
        return toVertexId;
    }

    public EdgeState getEdgeState() {
        return edgeState;
    }

    @Override
    public JsonObject toJson() {
        JsonObject result = super.toJson();
        result.addProperty(JsonKeyConstants.FROM_VERTEX_ID.toString(), fromVertexId);
        result.addProperty(JsonKeyConstants.TO_VERTEX_ID.toString(), toVertexId);
        result.addProperty(JsonKeyConstants.EDGE_STATE.toString(), edgeState.toString());
        return result;
    }
}
