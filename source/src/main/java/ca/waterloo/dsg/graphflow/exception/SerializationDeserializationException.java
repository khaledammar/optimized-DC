package ca.waterloo.dsg.graphflow.exception;

/**
 * Thrown to indicate an error when serializing or deserializing the graph state.
 */
public class SerializationDeserializationException extends GraphflowException {

    public SerializationDeserializationException(String message) {
        super(message);
    }
}
