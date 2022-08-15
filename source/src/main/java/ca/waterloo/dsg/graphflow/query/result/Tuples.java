package ca.waterloo.dsg.graphflow.query.result;

import ca.waterloo.dsg.graphflow.query.result.subgraph.Edge;
import ca.waterloo.dsg.graphflow.query.result.subgraph.Vertex;
import ca.waterloo.dsg.graphflow.util.json.JsonKeyConstants;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class Tuples extends AbstractQueryResult {

    public enum ColumnType {
        VERTEX,
        EDGE,
        PRIMITIVE_TYPE;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    private ColumnType[] columnTypes;
    private String[] columnNames;
    private Map<Integer, Vertex> vertices = new HashMap<>();
    private Map<Long, Edge> edges = new HashMap<>();
    private List<Object[]> tuples = new ArrayList<>();

    public Tuples(ColumnType[] columnTypes, String[] columnNames) {
        this.columnTypes = columnTypes;
        this.columnNames = columnNames;
    }

    public void addTuple(Object[] tuple) {
        this.tuples.add(tuple);
    }

    public Map<Integer, Vertex> getVertices() {
        return vertices;
    }

    public Map<Long, Edge> getEdges() {
        return edges;
    }

    public ColumnType[] getColumnTypes() {
        return columnTypes;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

   public List<Object[]> getTuples() {
        return tuples;
    }

    @Override
    public String toString() {
        StringJoiner result = new StringJoiner(System.lineSeparator());

        result.add(Arrays.toString(columnNames));

        for (Object[] objects : tuples) {
            result.add(Arrays.stream(objects).map(String::valueOf).collect(
                Collectors.joining(", ")));
        }

        return result.toString() + getExecutionTimeString();
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonOperator = new JsonObject();
        Gson gson = new Gson();

        jsonOperator.addProperty(JsonKeyConstants.RESPONSE_TYPE.toString(),
            ResultType.TUPLES.toString());

        JsonObject jsonObject = new JsonObject();
        for (int vertexId : vertices.keySet()) {
            jsonObject.add(String.valueOf(vertexId), vertices.get(vertexId).toJson());
        }
        jsonOperator.add(JsonKeyConstants.VERTICES.toString(), jsonObject);

        jsonObject = new JsonObject();
        for (long edgeId : edges.keySet()) {
            jsonObject.add(String.valueOf(edgeId), edges.get(edgeId).toJson());
        }
        jsonOperator.add(JsonKeyConstants.EDGES.toString(), jsonObject);

        jsonOperator.add(JsonKeyConstants.COLUMN_TYPES.toString(), gson.toJsonTree(columnTypes));
        jsonOperator.add(JsonKeyConstants.COLUMN_NAMES.toString(), gson.toJsonTree(columnNames));

        JsonArray jsonArray = new JsonArray();
        for (Object[] tuple : tuples) {
            jsonArray.add(gson.toJsonTree(tuple).getAsJsonArray());
        }
        jsonOperator.add(JsonKeyConstants.TUPLES.toString(), jsonArray);

        addExecutionTimeToJson(jsonOperator);
        return jsonOperator;
    }
}
