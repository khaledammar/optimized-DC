package ca.waterloo.dsg.graphflow.query.operator.descriptor;

import ca.waterloo.dsg.graphflow.util.json.JsonOutputable;
import com.google.gson.JsonObject;

/**
 * Stores the sourceIndex, destinationIndex, type, and edgeId for an edge in the prefix.
 */
public class EdgeDescriptor implements JsonOutputable {

    public int sourceIndex;
    public int destinationIndex;
    public short edgeTypeInQuery;
    /* Stores the type obtained by searching for the edge in the {@link Graph}. This is always a
    specific edge type, in contrast to {@code edgeTypeInQuery} which can be of {@code ANY} type. */
    public short determinedEdgeType;
    public long edgeId = -1;

    /**
     * @param sourceIndex index in vertexIds array to read the ID of the source vertex.
     * @param destinationIndex index in vertexIds array to read the ID of the destination vertex.
     * @param type of the edge between vertexIds[sourceIndex] and vertexIds[destinationIndex].
     */
    public EdgeDescriptor(int sourceIndex, int destinationIndex, short type) {
        this.sourceIndex = sourceIndex;
        this.destinationIndex = destinationIndex;
        this.edgeTypeInQuery = type;
    }

    @Override
    public String toString() {
        return "(sourceIndex: " + sourceIndex + ", destinationIndex: " + destinationIndex
            + ", type: " + edgeTypeInQuery + ")";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonSrcDstIndexAndType = new JsonObject();
        jsonSrcDstIndexAndType.addProperty("Src Index", sourceIndex);
        jsonSrcDstIndexAndType.addProperty("Dest Index", destinationIndex);
        jsonSrcDstIndexAndType.addProperty("Type", edgeTypeInQuery);
        return jsonSrcDstIndexAndType;
    }
}
