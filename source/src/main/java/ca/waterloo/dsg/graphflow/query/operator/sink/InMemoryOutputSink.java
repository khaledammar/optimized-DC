package ca.waterloo.dsg.graphflow.query.operator.sink;

import ca.waterloo.dsg.graphflow.query.result.Message;
import ca.waterloo.dsg.graphflow.query.result.AbstractQueryResult;
import ca.waterloo.dsg.graphflow.util.json.JsonKeyConstants;
import com.google.gson.JsonObject;

/**
 * Stores the output as an in memory data structure in the form of a list of {@code Strings}s.
 */
public class InMemoryOutputSink extends AbstractOutputSink {

    private AbstractQueryResult queryResult;

    /**
     * @return The stored {@link AbstractQueryResult} representing the complete output.
     */
    public AbstractQueryResult getQueryResult() {
        if (null == queryResult) {
            return new Message(this.toString());
        }
        return queryResult;
    }

    @Override
    public void append(AbstractQueryResult queryResult) {
        this.queryResult = queryResult;
    }

    @Override
    public String getHumanReadableOperator() {
        return "InMemoryOutputSink\n";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonOperator = new JsonObject();
        jsonOperator.addProperty(JsonKeyConstants.TYPE.toString(), JsonKeyConstants.SINK.
            toString());
        jsonOperator.addProperty(JsonKeyConstants.NAME.toString(), this.getClass().getSimpleName());
        return jsonOperator;
    }
}
