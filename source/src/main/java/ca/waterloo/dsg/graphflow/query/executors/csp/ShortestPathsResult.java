package ca.waterloo.dsg.graphflow.query.executors.csp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Represents the result of a shortest path query.
 */
public class ShortestPathsResult {

    private static final Logger logger = LogManager.getLogger(ShortestPathsResult.class);

    int source;
    int destination;
    Map<Integer, Map<Integer, Double>> edges;

    public ShortestPathsResult(int source, int destination) {
        this.source = source;
        this.destination = destination;
        edges = new HashMap<>();
    }

    public void addEdge(int parent, int child, double distance) {
        Map<Integer, Double> children = edges.get(parent);
        if (null == children) {
            children = new HashMap<>();
        }
        children.put(child, distance);
    }

    /**
     * @param vertexId ID of a vertex.
     * @return whether the vertex with the given ID is on the shortest paths or not.
     */
    public boolean containsVertex(int vertexId) {
        return (!edges.isEmpty() && (destination == vertexId || edges.containsKey(vertexId)));
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ShortestPathsResult)) {
            return true;
        }
        ShortestPathsResult otherSP = (ShortestPathsResult) other;
        if ((this.source != otherSP.source) || (this.destination != otherSP.destination)) {
            return false;
        }
        if (this.edges.size() != otherSP.edges.size()) {
            return false;
        }
        Map<Integer, Double> thisChildren, otherChildren;

        for (int parent : this.edges.keySet()) {
            thisChildren = this.edges.get(parent);
            if (null == thisChildren) {
                logger.error("In " + this.getClass().getName() + " instance the parent: " + parent +
                        " has null children. Every parent should have at least one child.");
            }

            otherChildren = otherSP.edges.get(parent);
            if ((null == thisChildren && null != otherChildren) || (null != thisChildren && null == otherChildren)) {
                return false;
            }

            if (null == thisChildren && null == otherChildren) {
                continue;
            }

            for (Entry<Integer, Double> thisChildDistancePair : thisChildren.entrySet()) {
                if (thisChildDistancePair.getValue() != otherChildren.get(thisChildDistancePair.getKey())) {
                    return false;
                }
            }
        }
        return true;
    }
}
