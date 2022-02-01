package ca.waterloo.dsg.graphflow.query.operator;

import ca.waterloo.dsg.graphflow.util.JsonUtils;
import ca.waterloo.dsg.graphflow.util.UsedOnlyByTests;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Stores the output as an in memory data structure in the form of a list of {@code Strings}s.
 */
public class InMemoryOutputSink extends AbstractDBOperator {

    private String header;
    private List<String> results = new ArrayList<>();

    public InMemoryOutputSink() {
        super(null /* no nextOperator, always last operator in the OneTimeMatchQueryPlan. */);
    }

    /**
     * Used during unit testing to check the equality of objects. This is used instead of
     * overriding the standard {@code equals()} and {@code hashCode()} methods.
     *
     * @param a One of the objects.
     * @param b The other object.
     * @return {@code true} if the {@code a} object values are the same as the
     * {@code b} object values, {@code false} otherwise.
     */
    @UsedOnlyByTests
    public static boolean isSameAs(InMemoryOutputSink a, InMemoryOutputSink b) {
        if (a == b) {
            return true;
        }
        if (null == a || null == b) {
            return false;
        }
        return Objects.equals(a.results, b.results);
    }

    /**
     * Adds {@code result} to the list of in-memory outputs.
     *
     * @param result the output {@code String}.
     */
    @Override
    public void append(String result) {
        results.add(result);
    }

    /**
     * Sets the header to be printed before all appended outputs.
     * Note: the header is only set for queries which do not have return statements.
     *
     * @param header the {@code String} header for the results.
     */
    public void setHeader(String header) {
        this.header = header;
    }

    @Override
    public String getHumanReadableOperator() {
        return "InMemoryOutputSink:\n";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonOperator = new JsonObject();
        jsonOperator.addProperty(JsonUtils.TYPE, JsonUtils.SINK);
        jsonOperator.addProperty(JsonUtils.NAME, this.getClass().getSimpleName());
        return jsonOperator;
    }

    @Override
    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(System.lineSeparator());
        if (null != header && results.size() > 0) {
            stringJoiner.add(header);
        }
        for (String result : results) {
            stringJoiner.add(result);
        }
        return stringJoiner.toString();
    }

    @UsedOnlyByTests
    public List<String> getResults() {
        return results;
    }
}
