package ca.waterloo.dsg.graphflow.query.result;

import ca.waterloo.dsg.graphflow.util.json.JsonKeyConstants;
import com.google.gson.JsonObject;

/**
 * A {@link AbstractQueryResult} used to encapsulate a {@link String} message or error.
 */
public class Message extends AbstractQueryResult {

    private String message;
    private boolean isError;

    public Message(String message) {
        this(message, false);
    }

    public Message(String message, boolean isError) {
        this.message = message;
        this.isError = isError;
    }

    @Override
    public String toString() {
        String result = "";
        if (isError) {
            result += "ERROR: ";
        }
        result += message;
        result += getExecutionTimeString();
        return result;
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonOperator = new JsonObject();
        jsonOperator.addProperty(JsonKeyConstants.RESPONSE_TYPE.toString(),
            ResultType.MESSAGE.toString());
        jsonOperator.addProperty(JsonKeyConstants.MESSAGE.toString(), message);
        jsonOperator.addProperty(JsonKeyConstants.IS_ERROR.toString(), isError);
        addExecutionTimeToJson(jsonOperator);
        return jsonOperator;
    }
}
