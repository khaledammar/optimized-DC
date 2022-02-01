package ca.waterloo.dsg.graphflow.query.operator.udf.subgraph;

/**
 * A vertex of the {@link Subgraph} containing a vertex Id, a type, and properties map of {@code
 * String} key to {@code String} values.
 */
public class Vertex extends VertexOrEdge {

    private int id;

    /**
     * @param id   The vertex Id.
     * @param type The {@code String} vertex type.
     **/
    Vertex(int id, String type) {
        super(type);
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
