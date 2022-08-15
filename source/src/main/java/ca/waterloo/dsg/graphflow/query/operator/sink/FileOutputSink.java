package ca.waterloo.dsg.graphflow.query.operator.sink;

import ca.waterloo.dsg.graphflow.query.result.AbstractQueryResult;
import ca.waterloo.dsg.graphflow.util.json.JsonKeyConstants;
import com.google.gson.JsonObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Outputs query results to a file.
 */
public class FileOutputSink extends AbstractOutputSink {

    private File location;
    private PrintWriter writer;

    /**
     * @param location the full path to the file the sink writes the output to.
     *
     * @throws IOException if there is a problem with opening or writing to the file.
     */
    public FileOutputSink(File location) throws IOException {
        this.location = location;
        this.writer = new PrintWriter(new BufferedWriter(new FileWriter(location, true)));
    }

    @Override
    public void append(AbstractQueryResult queryResult) {
        String output = queryResult.toString();
        if (!output.isEmpty()) {
            writer.println(output);
            writer.flush();
        }
    }

    @Override
    public String getHumanReadableOperator() {
        return String.format("FileOutputSink('%s')\n", location.getAbsolutePath());
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
