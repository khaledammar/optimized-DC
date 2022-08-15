package ca.waterloo.dsg.graphflow.util.json;

import com.google.gson.JsonObject;

/**
 * An interface for classes that can be outputted in JSON format
 */
public interface JsonOutputable {

    /**
     * @return a JSON representation of the class implementing the {@link JsonOutputable} interface.
     */
    JsonObject toJson();
}
