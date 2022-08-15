package ca.waterloo.dsg.graphflow.query.operator.resolver;

import ca.waterloo.dsg.graphflow.graph.Graph.GraphVersion;
import ca.waterloo.dsg.graphflow.query.operator.AbstractOperator;
import ca.waterloo.dsg.graphflow.query.operator.descriptor.EdgeDescriptor;
import ca.waterloo.dsg.graphflow.query.result.MatchQueryOutput;
import ca.waterloo.dsg.graphflow.util.json.JsonKeyConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * Given a {@link MatchQueryOutput} whose only vertexIds are set, resolves the edge IDs that are
 * needed in the query between some of these vertices. The edge IDs are only searched in the
 * {@link GraphVersion#PERMANENT}.
 */
public class EdgeIdResolver extends AbstractOperator {

    private List<EdgeDescriptor> edgeDescriptors;

    /**
     * Default constructor.
     *
     * @param nextOperator next operator to append {@link MatchQueryOutput}s to.
     * @param edgeDescriptors a list of {@link EdgeDescriptor}s. For each (srcIndex, dstIndex,
     * type) triple in the list, this operator will resolve the ID of the edge between {@code
     * matchQueryOutput.vertexIds[srcIndex]} and {@code matchQueryOutput.vertexIds[srcIndex]} with
     * the given type in each {@link MatchQueryOutput} appended to this operator.
     */
    public EdgeIdResolver(AbstractOperator nextOperator, List<EdgeDescriptor> edgeDescriptors) {
        super(nextOperator);
        this.edgeDescriptors = edgeDescriptors;
    }

    @Override
    public void append(MatchQueryOutput matchQueryOutput) {
        int srcId, dstId;
        for (EdgeDescriptor edge : edgeDescriptors) {
            srcId = matchQueryOutput.vertexIds[edge.sourceIndex];
            dstId = matchQueryOutput.vertexIds[edge.destinationIndex];
            graph.resolveEdgeIdAndType(srcId, dstId, edge);
        }
        matchQueryOutput.edgeDescriptors = edgeDescriptors;
        nextOperator.append(matchQueryOutput);
    }

    @Override
    public String getHumanReadableOperator() {
        StringBuilder stringBuilder = new StringBuilder("EdgeIdResolver:\n");
        appendListAsCommaSeparatedString(stringBuilder, edgeDescriptors,
            "SourceDestinationIndexAndTypes");
        return stringBuilder.toString();
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonOperator = new JsonObject();

        JsonArray jsonArguments = new JsonArray();
        JsonObject jsonArgument = new JsonObject();
        jsonArgument.addProperty(JsonKeyConstants.NAME.toString(),
            "Src & Dst Vertex Indices & Types");
        JsonArray jsonIndicesAndTypes = new JsonArray();
        for (EdgeDescriptor indexAndType : edgeDescriptors) {
            jsonIndicesAndTypes.add(indexAndType.toJson());
        }
        jsonArgument.add(JsonKeyConstants.VALUE.toString(), jsonIndicesAndTypes);
        jsonArguments.add(jsonArgument);

        jsonOperator.addProperty(JsonKeyConstants.NAME.toString(), "Edge ID Resolver");
        jsonOperator.add(JsonKeyConstants.ARGS.toString(), jsonArguments);
        return jsonOperator;
    }
}
