package ca.waterloo.dsg.graphflow.query.operator.projection;

import ca.waterloo.dsg.graphflow.query.operator.AbstractOperator;
import ca.waterloo.dsg.graphflow.query.result.MatchQueryOutput;
import ca.waterloo.dsg.graphflow.util.json.JsonKeyConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Projection operator for projecting MATCH query outputs onto a subset of the variables.
 */
public class Projection extends AbstractOperator {

    private int[] projectedVertexIds;
    private List<Integer> vertexIndicesToProject;

    /**
     * Default constructor that sets the vertex and indices to project and the next operator for
     * this operator.
     *
     * @param nextOperator next operator that the projected outputs should be appended to.
     * @param vertexIndicesToProject indices that specify which of the vertices in the vertexIds
     * array of {@link MatchQueryOutput}s should be in the output of the projection.
     */
    public Projection(AbstractOperator nextOperator, List<Integer> vertexIndicesToProject) {
        super(nextOperator);
        this.vertexIndicesToProject = null != vertexIndicesToProject ? vertexIndicesToProject :
            new ArrayList<>();
        projectedVertexIds = new int[vertexIndicesToProject.size()];
    }

    @Override
    public void append(MatchQueryOutput matchQueryOutput) {
        for (int i = 0; i < vertexIndicesToProject.size(); ++i) {
            projectedVertexIds[i] = matchQueryOutput.vertexIds[vertexIndicesToProject.get(i)];
        }
        matchQueryOutput.vertexIds = projectedVertexIds;
        nextOperator.append(matchQueryOutput);
    }

    @Override
    public String getHumanReadableOperator() {
        StringBuilder stringBuilder = new StringBuilder("Projection:\n");
        appendListAsCommaSeparatedString(stringBuilder, vertexIndicesToProject,
            "VertexIndicesToProject");
        return stringBuilder.toString();
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonOperator = new JsonObject();

        JsonArray jsonVertexIndicesToProject = new JsonArray();
        for (Integer vertexIndexToProject : vertexIndicesToProject) {
            jsonVertexIndicesToProject.add(vertexIndexToProject);
        }
        JsonArray jsonArguments = new JsonArray();
        JsonObject jsonArgument = new JsonObject();
        jsonArgument.addProperty(JsonKeyConstants.NAME.toString(), "Vertex Indices");
        jsonArgument.add(JsonKeyConstants.VALUE.toString(), jsonVertexIndicesToProject);
        jsonArguments.add(jsonArgument);
        jsonOperator.addProperty(JsonKeyConstants.NAME.toString(), "Projection (&Pi;)");
        jsonOperator.add(JsonKeyConstants.ARGS.toString(), jsonArguments);
        return jsonOperator;
    }
}
