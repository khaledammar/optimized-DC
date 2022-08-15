package ca.waterloo.dsg.graphflow.query.result;

import ca.waterloo.dsg.graphflow.query.plan.QueryPlan;
import com.google.gson.JsonObject;

/**
 * A {@link AbstractQueryResult} which encapsulates a {@link QueryPlan}.
 */
public class ExplainPlan extends AbstractQueryResult {

    private QueryPlan queryPlan;

    public ExplainPlan(QueryPlan queryPlan) {
        this.queryPlan = queryPlan;
    }

    @Override
    public String toString() {
        return queryPlan.toString() + getExecutionTimeString();
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = queryPlan.toJson();
        addExecutionTimeToJson(jsonObject);
        return jsonObject;
    }
}
