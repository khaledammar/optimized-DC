package ca.waterloo.dsg.graphflow.query.result;

import ca.waterloo.dsg.graphflow.query.operator.AbstractOperator;
import ca.waterloo.dsg.graphflow.query.operator.descriptor.EdgeDescriptor;
import ca.waterloo.dsg.graphflow.query.result.subgraph.SubgraphType;

import java.util.List;
import java.util.Map;

/**
 * Represents the output of a MATCH or CONTINUOUSLY MATCH query in the absence of aggregations.
 * Note: The fields of this class are public to enable instances of {@link AbstractOperator} to
 * easily reuse instances of this class by directly setting these fields. Doing so avoids
 * constructing new instances.
 */
public class MatchQueryOutput {

    public Map<String, Integer> vertexIndices;
    public int[] vertexIds;
    public List<EdgeDescriptor> edgeDescriptors;
    public SubgraphType subgraphType;
}
