package ca.waterloo.dsg.graphflow.query.plan;

import ca.waterloo.dsg.graphflow.query.result.ExplainPlan;
import ca.waterloo.dsg.graphflow.query.result.AbstractQueryResult;
import ca.waterloo.dsg.graphflow.util.json.JsonOutputable;
import com.google.gson.JsonObject;

/**
 * Abstract class representing query plans.
 */
public abstract class QueryPlan implements JsonOutputable {

    public abstract AbstractQueryResult execute();

    /**
     * @return a {@link ExplainPlan} encapsulating the {@link QueryPlan} of the current class.
     */
    public ExplainPlan explain() {
        return new ExplainPlan(this);
    }

    @Override
    public JsonObject toJson() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not " +
            "support the explain() method. Explain is only supported for MATCH and CONTINUOUSLY " +
            "MATCH queries.");
    }

    public AbstractQueryResult register() {
        throw new UnsupportedOperationException("The method register() is only supported for " +
            "CONTINUOUSLY MATCH queries can be registered.");
    }
}
