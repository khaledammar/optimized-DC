package ca.waterloo.dsg.graphflow.query.result.subgraph;

import ca.waterloo.dsg.graphflow.util.json.JsonKeyConstants;
import com.google.gson.JsonObject;

import java.util.Map;

/**
 * A vertex of the {@link Subgraph} containing a vertex Id, a type, and properties map of {@code
 * String} key to {@code String} values.
 */
public class Vertex extends VertexOrEdge {

    private int id;

    /**
     * @param id The vertex Id.
     * @param type The {@code String} vertex type.
     * @param properties The properties as a {@code Map<String, String>}.
     **/
    public Vertex(int id, String type, Map<String, String> properties) {
        super(type, properties);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("(%s %s)", id, super.toString());
    }

    @Override
    public JsonObject toJson() {
        JsonObject result = super.toJson();
        result.addProperty(JsonKeyConstants.ID.toString(), id);
        return result;
    }
}
