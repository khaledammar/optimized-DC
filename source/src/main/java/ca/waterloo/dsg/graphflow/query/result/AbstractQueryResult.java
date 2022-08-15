package ca.waterloo.dsg.graphflow.query.result;

import ca.waterloo.dsg.graphflow.util.json.JsonKeyConstants;
import ca.waterloo.dsg.graphflow.util.json.JsonOutputable;
import com.google.gson.JsonObject;

/**
 * Abstract class used to represent the output from all queries.
 */
public abstract class AbstractQueryResult implements JsonOutputable {

    public enum ResultType {
        MESSAGE,
        SUBGRAPHS,
        TUPLES,
        EXPLAIN_PLAN
    }

    private Double executionTimeInMillis;

    /**
     * Sets the total execution time for inclusion in the {@link String} or {@code JSON} output.
     *
     * @param executionTimeInMillis The execution time in milliseconds.
     */
    public void setExecutionTimeInMillis(double executionTimeInMillis) {
        this.executionTimeInMillis = executionTimeInMillis;
    }

    /**
     * @return the execution time of the query in a {@code String}.
     */
    String getExecutionTimeString() {
        if (null != executionTimeInMillis) {
            return String.format("\n\nQuery executed in %.3f ms.", executionTimeInMillis);
        }
        return "";
    }

    /**
     * @param jsonObject the json object to which the execution time key value
     */
    void addExecutionTimeToJson(JsonObject jsonObject) {
        if (null != executionTimeInMillis) {
            jsonObject.addProperty(JsonKeyConstants.EXECUTION_TIME.toString(),
                executionTimeInMillis);
        }
    }

    @Override
    public JsonObject toJson() {
        return null;
    }

    public String toString() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not " +
            "support the toString() method.");
    }
}
