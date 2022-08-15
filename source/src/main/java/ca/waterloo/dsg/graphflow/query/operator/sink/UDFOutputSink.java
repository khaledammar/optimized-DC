package ca.waterloo.dsg.graphflow.query.operator.sink;

import ca.waterloo.dsg.graphflow.graph.EdgeStore;
import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.TypeAndPropertyKeyStore;
import ca.waterloo.dsg.graphflow.graph.VertexPropertyStore;
import ca.waterloo.dsg.graphflow.query.operator.descriptor.EdgeDescriptor;
import ca.waterloo.dsg.graphflow.query.operator.resolver.SubgraphsResolver;
import ca.waterloo.dsg.graphflow.query.operator.udf.UDFAction;
import ca.waterloo.dsg.graphflow.query.operator.udf.UDFResolver;
import ca.waterloo.dsg.graphflow.query.operator.udf.subgraph.Subgraph;
import ca.waterloo.dsg.graphflow.query.operator.udf.subgraph.SubgraphFactory;
import ca.waterloo.dsg.graphflow.query.result.MatchQueryOutput;
import ca.waterloo.dsg.graphflow.query.result.AbstractQueryResult;
import ca.waterloo.dsg.graphflow.query.result.subgraph.Edge;
import ca.waterloo.dsg.graphflow.query.result.subgraph.Edge.EdgeState;
import ca.waterloo.dsg.graphflow.query.result.subgraph.SubgraphType;
import ca.waterloo.dsg.graphflow.query.result.subgraph.Vertex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Acts as an output sink. Operators append {@link MatchQueryOutput} one at a time. For each
 * match output tuple, the udf method resolved by the {@link UDFResolver} is executed.
 */
public class UDFOutputSink extends AbstractOutputSink {

    private static final Logger logger = LogManager.getLogger(UDFOutputSink.class);

    private UDFAction udfObject;
    private List<Subgraph> subgraphList = new ArrayList<>();

    /**
     * @param udfObject The {@link UDFAction} to execute.
     */
    public UDFOutputSink(UDFAction udfObject) {
        this.udfObject = udfObject;
    }

    @Override
    public void append(MatchQueryOutput matchQueryOutput) {
        List<Vertex> vertices = getVertices(matchQueryOutput);
        List<Edge> edges = getEdges(matchQueryOutput);
        SubgraphType subgraphType = getSubgraphType(matchQueryOutput);
        subgraphList.add(SubgraphFactory.getSubgraph(vertices, edges, subgraphType,
            matchQueryOutput.vertexIndices));
    }

    @Override
    public void append(AbstractQueryResult queryResult) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not " +
            "support the append(QueryResult) method.");
    }

    /**
     * @return a String human readable representation of an operator excluding its next operator.
     */
    public String getHumanReadableOperator() {
        return "UDFSink: " + udfObject.getClass().getCanonicalName() + ".evaluate(Subgraph)";
    }

    public void executeUDF() {
        if (!subgraphList.isEmpty()) {
            try {
                udfObject.evaluate(subgraphList);
            } catch (Exception e) {
                logger.error("The UDF " + udfObject.getClass().getSimpleName() + " did not " +
                    "execute correctly and threw an exception.");
            }
        }
        subgraphList = new ArrayList<>();
    }

    private List<Vertex> getVertices(MatchQueryOutput matchQueryOutput) {
        List<Vertex> vertices = new ArrayList<>();
        for (int vertexId : matchQueryOutput.vertexIds) {
            vertices.add(SubgraphFactory.getVertex(vertexId, TypeAndPropertyKeyStore.getInstance().
                    mapShortToStringType(Graph.getInstance().getVertexTypes().get(vertexId)),
                VertexPropertyStore.getInstance().getPropertiesAsStrings(vertexId)));
        }
        return vertices;
    }

    private List<Edge> getEdges(MatchQueryOutput matchQueryOutput) {
        List<Edge> edges = new ArrayList<>();
        for (EdgeDescriptor edgeInfo : matchQueryOutput.edgeDescriptors) {
            int fromVertexId = matchQueryOutput.vertexIds[edgeInfo.sourceIndex];
            int toVertexId = matchQueryOutput.vertexIds[edgeInfo.destinationIndex];
            String type = null;
            if (TypeAndPropertyKeyStore.ANY != edgeInfo.edgeTypeInQuery) {
                type = TypeAndPropertyKeyStore.getInstance().mapShortToStringType(edgeInfo.
                    edgeTypeInQuery);
            }
            Map<String, String> properties = EdgeStore.getInstance().getPropertiesAsStrings(
                edgeInfo.edgeId);
            EdgeState edgeState = SubgraphsResolver.getEdgeState(fromVertexId, toVertexId,
                matchQueryOutput.subgraphType);
            edges.add(SubgraphFactory.getEdge(fromVertexId, toVertexId, type, properties,
                edgeState));
        }
        return edges;
    }

    private SubgraphType getSubgraphType(MatchQueryOutput matchQueryOutput) {
        if (SubgraphType.EMERGED == matchQueryOutput.subgraphType) {
            return SubgraphType.EMERGED;
        } else {
            return SubgraphType.DELETED;
        }
    }
}
