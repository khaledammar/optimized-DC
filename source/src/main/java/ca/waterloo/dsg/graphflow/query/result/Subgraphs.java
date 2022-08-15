package ca.waterloo.dsg.graphflow.query.result;

import ca.waterloo.dsg.graphflow.query.result.subgraph.Edge;
import ca.waterloo.dsg.graphflow.query.result.subgraph.Subgraph;
import ca.waterloo.dsg.graphflow.query.result.subgraph.Vertex;
import ca.waterloo.dsg.graphflow.util.json.JsonKeyConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Represents the complete output from simple MATCH queries which do not have aggregations.
 */
public class Subgraphs extends AbstractQueryResult {

    private List<Subgraph> subgraphs = new ArrayList<>();
    private Map<Integer, Vertex> vertices = new HashMap<>();
    private Map<Long, Edge> edges = new HashMap<>();
    private Map<String, Integer> variableIndices;

    /**
     * @param variableIndices A map of {@code String} ids to {@code Integer} ids of the vertices.
     */
    public Subgraphs(Map<String, Integer> variableIndices) {
        this.variableIndices = variableIndices;
    }

    /**
     * @param subgraph The subgraph to be added to the list of subgraphs.
     */
    public void addSubgraph(Subgraph subgraph) {
        subgraphs.add(subgraph);
    }

    /**
     * @return A map of {@code Integer} vertex ids to {@code Vertex} objects in all subgraphs.
     */
    public Map<Integer, Vertex> getVertices() {
        return vertices;
    }

    /**
     * @return A map of {@code Long} edge ids to edge objects in all subgraphs.
     */
    public Map<Long, Edge> getEdges() {
        return edges;
    }

    @Override
    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(System.lineSeparator());
        String[] variables = new String[variableIndices.size()];
        for (String variable : variableIndices.keySet()) {
            variables[variableIndices.get(variable)] = variable;
        }
        stringJoiner.add(Arrays.toString(variables));
        for (Subgraph subgraph : subgraphs) {
            stringJoiner.add(subgraph.toString());
        }
        return stringJoiner.toString() + getExecutionTimeString();
    }

    @Override
    public JsonObject toJson() {
        JsonObject vertexMap = new JsonObject();
        for (String variable : variableIndices.keySet()) {
            vertexMap.addProperty(variable, variableIndices.get(variable));
        }

        JsonObject vertexData = new JsonObject();
        for (int vertexId : vertices.keySet()) {
            vertexData.add(String.valueOf(vertexId), vertices.get(vertexId).toJson());
        }

        JsonObject edgeData = new JsonObject();
        for (long edgeId : edges.keySet()) {
            edgeData.add(String.valueOf(edgeId), edges.get(edgeId).toJson());
        }

        JsonArray subgraphsResult = new JsonArray();
        for (Subgraph subgraph : subgraphs) {
            subgraphsResult.add(subgraph.toJson());
        }

        JsonObject result = new JsonObject();
        result.addProperty(JsonKeyConstants.RESPONSE_TYPE.toString(), ResultType.SUBGRAPHS.
            toString());
        result.add(JsonKeyConstants.VERTEX_MAP.toString(), vertexMap);
        result.add(JsonKeyConstants.VERTICES.toString(), vertexData);
        result.add(JsonKeyConstants.EDGES.toString(), edgeData);
        result.add(JsonKeyConstants.SUBGRAPHS.toString(), subgraphsResult);
        addExecutionTimeToJson(result);
        return result;
    }
}
