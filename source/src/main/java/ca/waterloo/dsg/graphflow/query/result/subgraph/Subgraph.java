package ca.waterloo.dsg.graphflow.query.result.subgraph;

import ca.waterloo.dsg.graphflow.query.result.AbstractQueryResult;
import ca.waterloo.dsg.graphflow.util.json.JsonKeyConstants;
import ca.waterloo.dsg.graphflow.util.json.JsonOutputable;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Arrays;

/**
 * Represents one output tuple from a MATCH query in the {@link AbstractQueryResult}.
 */
public class Subgraph implements JsonOutputable {

    private int[] vertexIds;
    private long[] edgeIds;
    private SubgraphType subgraphType;

    /**
     * @param subgraphType The {@link SubgraphType} of the subgraph.
     * @param vertexIds The list of vertex ids in the subgraph.
     * @param edgeIds The list of edge ids in the subgraph.
     */
    public Subgraph(SubgraphType subgraphType, int[] vertexIds, long[] edgeIds) {
        this.subgraphType = subgraphType;
        this.vertexIds = vertexIds;
        this.edgeIds = edgeIds;
    }

    @Override
    public String toString() {
        String result = Arrays.toString(vertexIds);
        if (SubgraphType.MATCHED != subgraphType) {
            result += " " + subgraphType.toString();
        }
        return result;
    }

    @Override
    public JsonObject toJson() {
        JsonObject result = new JsonObject();
        Gson gson = new Gson();
        result.addProperty(JsonKeyConstants.SUBGRAPH_TYPE.toString(), subgraphType.toString());
        result.add(JsonKeyConstants.EDGES.toString(), gson.toJsonTree(edgeIds));
        result.add(JsonKeyConstants.VERTICES.toString(), gson.toJsonTree(vertexIds));
        return result;
    }
}
