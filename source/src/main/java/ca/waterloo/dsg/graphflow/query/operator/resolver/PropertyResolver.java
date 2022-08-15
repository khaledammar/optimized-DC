package ca.waterloo.dsg.graphflow.query.operator.resolver;

import ca.waterloo.dsg.graphflow.graph.EdgeStore;
import ca.waterloo.dsg.graphflow.graph.VertexPropertyStore;
import ca.waterloo.dsg.graphflow.query.operator.AbstractOperator;
import ca.waterloo.dsg.graphflow.query.operator.descriptor.EdgeOrVertexPropertyDescriptor;
import ca.waterloo.dsg.graphflow.query.operator.sink.AbstractOutputSink;
import ca.waterloo.dsg.graphflow.query.result.MatchQueryOutput;
import ca.waterloo.dsg.graphflow.query.result.Tuples;
import ca.waterloo.dsg.graphflow.query.result.Tuples.ColumnType;
import ca.waterloo.dsg.graphflow.util.json.JsonKeyConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * An operator that takes a list of vertex and edge properties to look up in the
 * {@link VertexPropertyStore} and {@link EdgeStore}. When it is appended {@link MatchQueryOutput}s,
 * it resolves the properties and appends them to the next operator. Other vertices and edges
 * that do not have properties to resolve are also appended to the next operator with only their
 * IDs.
 * Note: For now this operator only appends String outputs to the next operator.
 */
public class PropertyResolver extends PropertyReadingOperator {

    private String[] columnNames;
    private ColumnType[] columnTypes;
    private List<Integer> vertexIndices = new ArrayList<>();
    private List<Integer> edgeIndices = new ArrayList<>();
    private Tuples tuples;

    /**
     * @see PropertyReadingOperator#PropertyReadingOperator(AbstractOperator, List).
     */
    public PropertyResolver(AbstractOutputSink nextOperator, String[] columnNames,
        List<EdgeOrVertexPropertyDescriptor> propertyDescriptors) {
        super(nextOperator, propertyDescriptors);
        this.columnNames = columnNames;
        this.columnTypes = new ColumnType[columnNames.length];
        for (int i = 0; i < propertyDescriptors.size(); i++) {
            EdgeOrVertexPropertyDescriptor descriptor = propertyDescriptors.get(i);
            switch (descriptor.descriptorType) {
                case EDGE_ID:
                    columnTypes[i] = ColumnType.EDGE;
                    edgeIndices.add(descriptor.index);
                    break;
                case VERTEX_ID:
                    columnTypes[i] = ColumnType.VERTEX;
                    vertexIndices.add(descriptor.index);
                    break;
                default:
                    columnTypes[i] = ColumnType.PRIMITIVE_TYPE;
            }
        }
        this.tuples = new Tuples(columnTypes, columnNames);
    }

    @Override
    public void append(MatchQueryOutput matchQueryOutput) {
        Object[] tuple = new Object[propertyDescriptors.size()];
        for (int i = 0; i < propertyDescriptors.size(); i++) {
            tuple[i] = getPropertyOrId(matchQueryOutput, propertyDescriptors.get(i));
        }
        tuples.addTuple(tuple);
        for (int vertexIndex : vertexIndices) {
            int vertexId = matchQueryOutput.vertexIds[vertexIndex];
            if (!tuples.getVertices().containsKey(vertexId)) {
                tuples.getVertices().put(vertexId, SubgraphsResolver.resolveVertex(vertexId));
            }
        }
        for (int edgeIndex : edgeIndices) {
            long edgeId = matchQueryOutput.edgeDescriptors.get(edgeIndex).edgeId;
            if (!tuples.getEdges().containsKey(edgeId)) {
                tuples.getEdges().put(edgeId, SubgraphsResolver.resolveEdge(matchQueryOutput,
                    edgeIndex));
            }
        }
    }

    @Override
    public void notifyDone() {
        ((AbstractOutputSink) nextOperator).append(tuples);
        this.tuples = new Tuples(columnTypes, columnNames);
        this.vertexIndices = new ArrayList<>();
        this.edgeIndices = new ArrayList<>();
        super.notifyDone();
    }

    @Override
    public String getHumanReadableOperator() {
        StringBuilder stringBuilder = new StringBuilder("PropertyResolver:\n");
        appendListAsCommaSeparatedString(stringBuilder, propertyDescriptors,
            "EdgeOrVertexPropertyDescriptors");
        return stringBuilder.toString();
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonOperator = new JsonObject();

        JsonArray jsonArguments = new JsonArray();
        JsonObject jsonArgument = new JsonObject();
        jsonArgument.addProperty(JsonKeyConstants.NAME.toString(), "Descriptors");
        JsonArray jsonDescriptors = new JsonArray();
        for (EdgeOrVertexPropertyDescriptor descriptor : propertyDescriptors) {
            jsonDescriptors.add(descriptor.toJson());
        }
        jsonArgument.add(JsonKeyConstants.VALUE.toString(), jsonDescriptors);
        jsonArguments.add(jsonArgument);

        jsonOperator.addProperty(JsonKeyConstants.NAME.toString(), "Property Resolver");
        jsonOperator.add(JsonKeyConstants.ARGS.toString(), jsonArguments);
        return jsonOperator;
    }
}
