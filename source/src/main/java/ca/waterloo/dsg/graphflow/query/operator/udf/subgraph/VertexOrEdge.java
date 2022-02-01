package ca.waterloo.dsg.graphflow.query.operator.udf.subgraph;

/**
 * A base class for the {@link Edge} and {@link Vertex} .
 */
public abstract class VertexOrEdge {

    private String type;

    VertexOrEdge(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
