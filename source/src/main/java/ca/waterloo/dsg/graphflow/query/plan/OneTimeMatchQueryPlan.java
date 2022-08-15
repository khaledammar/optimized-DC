package ca.waterloo.dsg.graphflow.query.plan;

import ca.waterloo.dsg.graphflow.graph.TypeAndPropertyKeyStore;
import ca.waterloo.dsg.graphflow.query.operator.AbstractOperator;
import ca.waterloo.dsg.graphflow.query.operator.join.EdgeIntersectionRule;
import ca.waterloo.dsg.graphflow.query.operator.join.Scan;
import ca.waterloo.dsg.graphflow.query.operator.join.StageOperator;
import ca.waterloo.dsg.graphflow.query.operator.sink.InMemoryOutputSink;
import ca.waterloo.dsg.graphflow.query.result.AbstractQueryResult;
import ca.waterloo.dsg.graphflow.util.json.JsonKeyConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing plan for a MATCH operation.
 */
public class OneTimeMatchQueryPlan extends QueryPlan {

    private List<String> orderedVariables;
    private Scan firstOperator;
    private InMemoryOutputSink outputSink = new InMemoryOutputSink();
    private StageOperator lastStageOperator;

    public OneTimeMatchQueryPlan(InMemoryOutputSink outputSink) {
        this.outputSink = outputSink;
    }

    public Scan getFirstOperator() {
        return firstOperator;
    }

    public void setFirstOperator(StageOperator firstOperator) {
        this.firstOperator = (Scan) firstOperator;
    }

    public void setLastStageOperator(StageOperator lastStageOperator) {
        this.lastStageOperator = lastStageOperator;
    }

    /**
     * Setter of {@code this.orderedVariables}.
     *
     * @param orderedVariables list of {@code String} variable symbols
     */
    public void setOrderedVariables(List<String> orderedVariables) {
        this.orderedVariables = orderedVariables;
    }

    /**
     * Executes the {@link OneTimeMatchQueryPlan}.
     */
    @Override
    public AbstractQueryResult execute() {
        firstOperator.execute();
        return null == outputSink ? null : outputSink.getQueryResult();
    }

    /**
     * @return a String human readable representation of an operator and all of its next operators.
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getHumanReadableGenericJoinOperators());
        int level = 1;
        AbstractOperator operator = lastStageOperator.nextOperator;
        while (null != operator) {
            stringBuilder.append(getIndentedString("nextOperator -> " + operator.
                getHumanReadableOperator(), level++));
            operator = operator.nextOperator;
        }
        return stringBuilder.toString();
    }

    private String getHumanReadableGenericJoinOperators() {
        StringBuilder stringBuilder = new StringBuilder("OneTimeMatchQueryPlan: \n");
        StageOperator currentOperator = firstOperator;
        int stageCount = 0;
        while (null != currentOperator) {
            List<EdgeIntersectionRule> stage = currentOperator.getIntersectionRules();
            stringBuilder.append("\tStage ").append(stageCount).append("\n");
            for (EdgeIntersectionRule intersectionRule : stage) {
                stringBuilder.append("\t\t").append(intersectionRule.toString()).append("\n");
            }
            if (currentOperator.nextOperator instanceof StageOperator) {
                currentOperator = (StageOperator) currentOperator.nextOperator;
                stageCount++;
            } else {
                currentOperator = null;
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Converts {@link OneTimeMatchQueryPlan} into JSON format
     *
     * @return {@code JsonArray} containing one or more {@code JsonObject}
     */
    public JsonObject toJson() {
        // Add "name", "variableOrdering", and "sink" to {@code planJson}
        JsonObject jsonPlan = new JsonObject();

        jsonPlan.addProperty(JsonKeyConstants.NAME.toString(), "Q");
        JsonArray variableOrdering = new JsonArray();
        orderedVariables.forEach(variableOrdering::add);
        jsonPlan.add(JsonKeyConstants.VAR_ORDERING.toString(), variableOrdering);

        // Construct "stages" and add it to {@code planJson}
        JsonArray stagesJson = new JsonArray();
        StageOperator currentOperator = this.firstOperator;
        while (null != currentOperator) {
            List<EdgeIntersectionRule> stage = currentOperator.getIntersectionRules();
            JsonArray stageJson = new JsonArray();
            for (EdgeIntersectionRule rule : stage) {
                JsonObject ruleJson = new JsonObject();
                ruleJson.addProperty(JsonKeyConstants.GRAPH_VERSION.toString(),
                    rule.getGraphVersion().toString());
                ruleJson.addProperty(JsonKeyConstants.VARIABLE.toString(),
                    orderedVariables.get(rule.getPrefixIndex()));
                ruleJson.addProperty(JsonKeyConstants.DIRECTION.toString(),
                    rule.getDirection().toString());
                Short edgeTypeFilter = rule.getEdgeTypeFilter();
                if (TypeAndPropertyKeyStore.ANY != edgeTypeFilter) {
                    ruleJson.addProperty(JsonKeyConstants.EDGE_TYPE.toString(),
                        TypeAndPropertyKeyStore.getInstance().mapShortToStringType(edgeTypeFilter));
                }
                if (currentOperator instanceof Scan) {
                    Short fromVertexTypeFilter = ((Scan) currentOperator).getFromVertexTypeFilter();
                    if (TypeAndPropertyKeyStore.ANY != fromVertexTypeFilter) {
                        ruleJson.addProperty(JsonKeyConstants.FROM_VERTEX_TYPE.toString(),
                            TypeAndPropertyKeyStore.getInstance().mapShortToStringType(
                                fromVertexTypeFilter));
                    }
                }
                Short toVertexTypeFilter = currentOperator.getToVertexTypeFilter();
                if (TypeAndPropertyKeyStore.ANY != toVertexTypeFilter) {
                    ruleJson.addProperty(JsonKeyConstants.TO_VERTEX_TYPE.toString(),
                        TypeAndPropertyKeyStore.getInstance().mapShortToStringType(
                            toVertexTypeFilter));
                }
                stageJson.add(ruleJson);
            }
            stagesJson.add(stageJson);
            if (currentOperator.nextOperator instanceof StageOperator) {
                currentOperator = (StageOperator) currentOperator.nextOperator;
            } else {
                currentOperator = null;
            }
        }
        jsonPlan.add(JsonKeyConstants.STAGES.toString(), stagesJson);

        JsonArray nextOperators = new JsonArray();
        AbstractOperator operator = lastStageOperator.nextOperator;
        while (null != operator) {
            nextOperators.add(operator.toJson());
            operator = operator.nextOperator;
        }
        jsonPlan.add(JsonKeyConstants.NEXT_OPERATORS.toString(), nextOperators);

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(jsonPlan);

        JsonObject jsonObject = new JsonObject();
        jsonObject.add(JsonKeyConstants.PLAN.toString(), jsonArray);
        return jsonObject;
    }

    /**
     * @param numTabsToIndent the number of tab indentations to have.
     *
     * @return a String that indents each line of the given unindented String by {@code
     * numTabsToIndent} tabs.
     */
    private String getIndentedString(String unindentedString, int numTabsToIndent) {
        StringBuilder indentationBuilder = new StringBuilder();
        for (int i = 0; i < numTabsToIndent; ++i) {
            indentationBuilder.append("\t");
        }
        StringBuilder stringBuilder = new StringBuilder();
        String[] lines = unindentedString.split("\n");
        for (String line : lines) {
            stringBuilder.append(indentationBuilder.toString()).append(line).append("\n");
        }
        return stringBuilder.toString();
    }

    private List<List<EdgeIntersectionRule>> getPlanStages() {
        List<List<EdgeIntersectionRule>> rules = new ArrayList<>();
        StageOperator currentOperator = firstOperator;
        rules.add(currentOperator.getIntersectionRules());
        while (null != currentOperator.nextOperator &&
            currentOperator.nextOperator instanceof StageOperator) {
            currentOperator = (StageOperator) currentOperator.nextOperator;
            rules.add(currentOperator.getIntersectionRules());
        }

        return rules;
    }
}
