package ca.waterloo.dsg.graphflow.query.result.subgraph;

import ca.waterloo.dsg.graphflow.util.json.JsonKeyConstants;
import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.Map;
import java.util.StringJoiner;

/**
 * A base class for the {@link Edge} and {@link Vertex} .
 */
public abstract class VertexOrEdge {

    private String type;
    private Map<String, String> properties;

    VertexOrEdge(String type, Map<String, String> properties) {
        this.type = type;
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public String getProperty(String key) {
        return properties.get(key);
    }

    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    public String toString() {
        StringJoiner propertiesStr = new StringJoiner(", ");
        for (String key : properties.keySet()) {
            propertiesStr.add(String.format("%s: %s", key, properties.get(key)));
        }
        return String.format(":%s {%s}", type, propertiesStr.toString());
    }

    public JsonObject toJson() {
        JsonObject propertiesResult = new JsonObject();
        for (String key : properties.keySet()) {
            propertiesResult.addProperty(key, properties.get(key));
        }

        JsonObject result = new JsonObject();
        result.addProperty(JsonKeyConstants.TYPE.toString(), type);
        result.add(JsonKeyConstants.PROPERTIES.toString(), propertiesResult);
        return result;
    }
}
