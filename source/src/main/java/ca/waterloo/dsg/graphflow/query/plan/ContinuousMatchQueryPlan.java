package ca.waterloo.dsg.graphflow.query.plan;

import ca.waterloo.dsg.graphflow.query.executor.ContinuousMatchQueryExecutor;
import ca.waterloo.dsg.graphflow.query.operator.AbstractOperator;
import ca.waterloo.dsg.graphflow.query.operator.sink.UDFOutputSink;
import ca.waterloo.dsg.graphflow.query.result.Message;
import ca.waterloo.dsg.graphflow.query.result.AbstractQueryResult;
import ca.waterloo.dsg.graphflow.util.json.JsonKeyConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Delta Generic Join query plan generated from a match query consisting of
 * relations between variables.
 */
public class ContinuousMatchQueryPlan extends QueryPlan {

    /**
     * The continuous match query plan is stored as a list of {@code OneTimeMatchQueryPlan}s,
     * which together produce the complete output for the Delta Generic Join query.
     */
    private List<OneTimeMatchQueryPlan> oneTimeMatchQueryPlans = new ArrayList<>();
    private AbstractOperator outputSink;

    public ContinuousMatchQueryPlan(AbstractOperator outputSink) {
        this.outputSink = outputSink;
    }

    /**
     * Adds a new {@link OneTimeMatchQueryPlan} to the {@link ContinuousMatchQueryPlan}.
     *
     * @param oneTimeMatchQueryPlan the {@link OneTimeMatchQueryPlan} to be added.
     */
    public void addOneTimeMatchQueryPlan(OneTimeMatchQueryPlan oneTimeMatchQueryPlan) {
        oneTimeMatchQueryPlans.add(oneTimeMatchQueryPlan);
    }

    /**
     * Executes the {@link OneTimeMatchQueryPlan}s that make up the
     * {@link ContinuousMatchQueryPlan}.
     */
    @Override
    public AbstractQueryResult execute() {
        for (OneTimeMatchQueryPlan oneTimeMatchQueryPlan : oneTimeMatchQueryPlans) {
            oneTimeMatchQueryPlan.execute();
        }
        if (outputSink instanceof UDFOutputSink) {
            ((UDFOutputSink) outputSink).executeUDF();
        }
        return null /* return value ignored */;
    }

    public AbstractQueryResult register() {
        ContinuousMatchQueryExecutor.getInstance().addContinuousMatchQueryPlan(this);
        return new Message("The CONTINUOUS MATCH query has been registered successfully");
    }

    /**
     * @return a String human readable representation of {@code ContinuousMatchQueryPlan}
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("ContinuousMatchQueryPlan: \n");
        for (int i = 0; i < oneTimeMatchQueryPlans.size(); i += 2) {
            stringBuilder.append("dQ").append(i / 2 + 1).append("\n");
            stringBuilder.append(oneTimeMatchQueryPlans.get(i).toString()).append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * Converts {@link ContinuousMatchQueryPlan} into JSON format
     *
     * @return {@code JsonArray} containing one or more {@code JsonObject}
     */
    @Override
    public JsonObject toJson() {
        JsonArray jsonArray = new JsonArray();
        JsonObject oneTimeMatchQueryPlanJson;
        for (int i = 0; i < oneTimeMatchQueryPlans.size(); i += 2) {
            oneTimeMatchQueryPlanJson = (JsonObject) oneTimeMatchQueryPlans.get(i).toJson().
                getAsJsonArray(JsonKeyConstants.PLAN.toString()).get(0);
            oneTimeMatchQueryPlanJson.remove("name");
            oneTimeMatchQueryPlanJson.addProperty("name", "dQ" + (i / 2 + 1));
            jsonArray.add(oneTimeMatchQueryPlanJson);
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(JsonKeyConstants.PLAN.toString(), jsonArray);
        return jsonObject;
    }
}
