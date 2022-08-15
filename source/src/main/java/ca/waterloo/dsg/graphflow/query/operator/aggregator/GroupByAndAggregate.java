package ca.waterloo.dsg.graphflow.query.operator.aggregator;

import ca.waterloo.dsg.graphflow.query.operator.descriptor.EdgeOrVertexPropertyDescriptor;
import ca.waterloo.dsg.graphflow.query.operator.resolver.PropertyReadingOperator;
import ca.waterloo.dsg.graphflow.query.operator.resolver.SubgraphsResolver;
import ca.waterloo.dsg.graphflow.query.operator.sink.AbstractOutputSink;
import ca.waterloo.dsg.graphflow.query.result.MatchQueryOutput;
import ca.waterloo.dsg.graphflow.query.result.Tuples;
import ca.waterloo.dsg.graphflow.query.result.Tuples.ColumnType;
import ca.waterloo.dsg.graphflow.util.collection.StringToIntKeyMap;
import ca.waterloo.dsg.graphflow.util.json.JsonKeyConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Operator for grouping MATCH query outputs by zero more keys and aggregating each group by one
 * or more values.
 */
public class GroupByAndAggregate extends PropertyReadingOperator {

    private final static String GROUP_BY_KEY_DELIMITER = "-";

    private List<EdgeOrVertexPropertyDescriptor> valuesToGroupBy;
    private List<Pair<EdgeOrVertexPropertyDescriptor, AbstractAggregator>> valueAggregatorPairs;
    private StringToIntKeyMap groupByKeys;
    private String[] columnNames;
    private ColumnType[] columnTypes;
    private List<Integer> vertexIndices = new ArrayList<>();
    private List<Integer> edgeIndices = new ArrayList<>();
    private Tuples tuples;

    /**
     * Default constructor.
     *
     * @param nextOperator next operator to append outputs to.
     * @param valuesToGroupBy descriptions of the list of values to group by.
     * @param valueAggregatorPairs descriptions of the values to aggregate and the aggregator to use
     * for these values.
     */
    public GroupByAndAggregate(AbstractOutputSink nextOperator, String[] columnNames,
        List<EdgeOrVertexPropertyDescriptor> valuesToGroupBy,
        List<Pair<EdgeOrVertexPropertyDescriptor, AbstractAggregator>> valueAggregatorPairs) {
        super(nextOperator, valuesToGroupBy);
        this.valuesToGroupBy = valuesToGroupBy;
        this.valueAggregatorPairs = valueAggregatorPairs;
        this.columnNames = columnNames;
        this.groupByKeys = new StringToIntKeyMap();
        this.columnTypes = new ColumnType[columnNames.length];
        for (int i = 0; i < valuesToGroupBy.size(); ++i) {
            EdgeOrVertexPropertyDescriptor edgeOrVertexPropertyDescriptor = valuesToGroupBy.get(i);
            switch (edgeOrVertexPropertyDescriptor.descriptorType) {
                case EDGE_ID:
                    columnTypes[i] = ColumnType.EDGE;
                    edgeIndices.add(edgeOrVertexPropertyDescriptor.index);
                    break;
                case VERTEX_ID:
                    columnTypes[i] = ColumnType.VERTEX;
                    vertexIndices.add(edgeOrVertexPropertyDescriptor.index);
                    break;
                default:
                    columnTypes[i] = ColumnType.PRIMITIVE_TYPE;
            }
        }
        Arrays.fill(columnTypes, valuesToGroupBy.size(), columnTypes.length, ColumnType.
            PRIMITIVE_TYPE);
        this.tuples = new Tuples(columnTypes, columnNames);
    }

    @Override
    public void append(MatchQueryOutput matchQueryOutput) {
        clearAndFillStringBuilder(matchQueryOutput, GROUP_BY_KEY_DELIMITER);
        String groupByKey = stringBuilder.toString();
        int index = groupByKeys.getKeyAsIntOrInsert(groupByKey);
        for (Pair<EdgeOrVertexPropertyDescriptor, AbstractAggregator> valueAggregatorPair :
            valueAggregatorPairs) {
            if (valueAggregatorPair.b instanceof CountStar) {
                valueAggregatorPair.b.aggregate(index, 1 /* we aggregate count(*) by 1 */);
            } else {
                Object propertyOrId = getPropertyOrId(matchQueryOutput, valueAggregatorPair.a);
                valueAggregatorPair.b.aggregate(index, propertyOrId);
            }
        }
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
        List<String> keys = groupByKeys.getSortedKeys();
        for (String groupByKey : keys) {
            String[] variables = groupByKey.isEmpty() ? new String[0] : groupByKey.split(
                GROUP_BY_KEY_DELIMITER);
            Object[] tuple = new Object[variables.length + valueAggregatorPairs.size()];
            System.arraycopy(variables, 0, tuple, 0, variables.length);
            int i = variables.length;
            int index = groupByKeys.mapStringKeyToInt(groupByKey);
            for (Pair<EdgeOrVertexPropertyDescriptor, AbstractAggregator> valueAggregatorPair :
                valueAggregatorPairs) {
                tuple[i++] = valueAggregatorPair.b.getValue(index);
            }
            tuples.addTuple(tuple);
        }
        ((AbstractOutputSink) nextOperator).append(tuples);
        this.tuples = new Tuples(columnTypes, columnNames);
        this.vertexIndices = new ArrayList<>();
        this.edgeIndices = new ArrayList<>();
        super.notifyDone();
    }

    @Override
    public String getHumanReadableOperator() {
        StringBuilder stringBuilder = new StringBuilder("GroupByAndAggregate:\n");
        appendListAsCommaSeparatedString(stringBuilder, valuesToGroupBy, "valuesToGroupBy");
        appendListAsCommaSeparatedString(stringBuilder, valueAggregatorPairs,
            "valueAggregatorPairs");
        return stringBuilder.toString();
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonOperator = new JsonObject();
        JsonArray jsonArguments = new JsonArray();

        JsonObject jsonArgument = new JsonObject();
        jsonArgument.addProperty(JsonKeyConstants.NAME.toString(), "Values to Group-By");
        JsonArray jsonValuesToGroupBy = new JsonArray();
        for (EdgeOrVertexPropertyDescriptor descriptor : valuesToGroupBy) {
            jsonValuesToGroupBy.add(descriptor.toJson());
        }
        jsonArgument.add(JsonKeyConstants.VALUE.toString(), jsonValuesToGroupBy);
        jsonArguments.add(jsonArgument);

        jsonArgument = new JsonObject();
        jsonArgument.addProperty(JsonKeyConstants.NAME.toString(), "Aggregator Pairs");
        JsonArray jsonAggregatorPairs = new JsonArray();
        for (Pair<EdgeOrVertexPropertyDescriptor, AbstractAggregator> aggregatorPair :
            valueAggregatorPairs) {
            if (aggregatorPair.a.toJson().getAsJsonPrimitive(JsonKeyConstants.TYPE.toString()).
                getAsString().equals(JsonKeyConstants.COUNT_STAR_DESCRIPTOR.toString())) {
                jsonAggregatorPairs.add(aggregatorPair.b.toString());
            } else {
                jsonAggregatorPairs.add(aggregatorPair.toString());
            }
        }
        jsonArgument.add(JsonKeyConstants.VALUE.toString(), jsonAggregatorPairs);
        jsonArguments.add(jsonArgument);

        jsonOperator.addProperty(JsonKeyConstants.NAME.toString(),
            "Group-By & Aggregate (&Gamma;)");
        jsonOperator.add(JsonKeyConstants.ARGS.toString(), jsonArguments);
        return jsonOperator;
    }
}
