package ca.waterloo.dsg.graphflow.graph;

import ca.waterloo.dsg.graphflow.exceptions.SerializationDeserializationException;
import ca.waterloo.dsg.graphflow.query.executors.csp.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Used to reset, serialize, or deserialize {@link Graph}, {@link EdgeStore},
 * {@link VertexPropertyStore}, and {@link TypeAndPropertyKeyStore}.
 */
public class GraphDBState {

    private static final Logger logger = LogManager.getLogger(GraphDBState.class);

    /**
     * Resets {@link Graph}, {@link EdgeStore}, {@link VertexPropertyStore}, and
     * {@link TypeAndPropertyKeyStore}.
     */
    public static void reset() {
        Graph.reset();
        TypeAndPropertyKeyStore.reset();
        Distances.reset();
        ContinuousShortestPathsExecutor.reset();
    }

    public static void reset2() {
        Graph.reset();
        Distances.reset();
        DistancesWithDropBloom.reset();
        DistancesWithDropBloom.reset();
        DistancesWithDropHash.reset();
        ContinuousShortestPathsExecutor.reset();
    }

    /**
     * Serializes {@link Graph}, {@link EdgeStore}, {@link VertexPropertyStore}, and
     * {@link TypeAndPropertyKeyStore} and writes it to files inside the given directory.
     *
     * @param outputDirectoryPath The directory path to write the serialized data to.
     */
    public static void serialize(String outputDirectoryPath) {
        try {
            Graph.getInstance().serializeAll(outputDirectoryPath);
            TypeAndPropertyKeyStore.getInstance().serializeAll(outputDirectoryPath);
        } catch (IOException | InterruptedException e) {
            logger.error("Error in serialization:", e);
            throw new SerializationDeserializationException("Error in serialization.");
        }
    }

    /**
     * Deserializes {@link Graph}, {@link EdgeStore}, {@link VertexPropertyStore}, and
     * {@link TypeAndPropertyKeyStore} from the files containing serialized data in the given
     * directory.
     *
     * @param inputDirectoryPath The input directory path to read serialized data from.
     */
    public static void deserialize(String inputDirectoryPath) {
        GraphDBState.reset();
        try {
            Graph.getInstance().deserializeAll(inputDirectoryPath);
            TypeAndPropertyKeyStore.getInstance().deserializeAll(inputDirectoryPath);
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            GraphDBState.reset();
            logger.error("Error in deserialization:", e);
            throw new SerializationDeserializationException("Error in deserialization.");
        }
    }
}
