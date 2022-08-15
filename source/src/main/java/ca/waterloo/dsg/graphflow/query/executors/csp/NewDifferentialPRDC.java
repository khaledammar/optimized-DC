package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.Graph.Direction;
import ca.waterloo.dsg.graphflow.graph.SortedAdjacencyList;

import java.util.Map;

import static ca.waterloo.dsg.graphflow.query.executors.csp.Distances.ONEM;
import static ca.waterloo.dsg.graphflow.query.executors.csp.Distances.SIXM;

/**
 * An implementation of Unidirectional unweighted differential BFS. The class takes
 * as input a direction in which to do the BFS.
 */
public class NewDifferentialPRDC extends NewUnidirectionalDifferentialBFSDC {
    short maxHop;

    public NewDifferentialPRDC(int queryId, int source, int maxHop, Direction direction) {
        super(queryId, source, -1, direction);
        this.maxHop = (short) maxHop;
    }

    @Override
    protected void initFrontierAndSourceDistance(int queryId) {
        distancesJ = new DistancesDC(queryId, source, destination, direction, NewUnidirectionalDifferentialBFS.Queries.PR, "Join");
        distancesR = new DistancesDC(queryId, source, destination, direction, NewUnidirectionalDifferentialBFS.Queries.PR,"Reduce");
    }


    @Override
    boolean shouldStopEarly(short iteration) {
        return iteration >= maxHop;
    }

    @Override
    void getNeighborsData(DistancesDC distancesJ, int currentVertexId, short iteration, long currentDistance,
                          short diff, boolean shouldRetract) {
        SortedAdjacencyList adjList = Graph.INSTANCE.getForwardMergedAdjacencyList(currentVertexId);
        if (SortedAdjacencyList.isNullOrEmpty(adjList)) {
            return;
        }

        if (iteration == 2 && shouldRetract) {
            currentDistance -= SIXM;
        }

        var fd = Graph.INSTANCE.getVertexFWDDegree(currentVertexId);
        if (fd == 0) {
            return;
        }
        var newDist = (5 * currentDistance) / (fd * 6L);
        if (newDist == 0) {
            return;
        }
        for (int j = 0; j < adjList.getSize(); j++) {
            // If the vertex is not the destination, has no outgoing neighbors that may help this query/direction, then no need to it to frontier!
            int neighbourVertexId = adjList.getNeighbourId(j);
            if (currentVertexId != neighbourVertexId) {
                distancesJ.addTmpDiff(neighbourVertexId, newDist, diff);
            }
        }
    }

    @Override
    protected void specialOperation(DistancesDC distancesJ, short iteration) {
        if (iteration == 1) {
            for (int vertex=0; vertex <= Graph.INSTANCE.getHighestVertexId(); vertex++) {
                distancesJ.addTmpDiff(vertex, ONEM, (short) 1);
            }
        }
    }

    protected long aggregateFunction(int vertex, short iteration, DistancesDC distancesJ){
        var jOutput = distancesJ.lastDiffs.get(vertex);
        var newValue = 0L;
        if (jOutput != null) {
            for (var entry: jOutput.entrySet()) {
                newValue += entry.getKey() * entry.getValue();
            }
        }
        if (newValue == 0) {
            newValue = getVertexDefaultValue(vertex);
        }
        return newValue;
    }

    @Override
    protected void processUpdate(short iter,
                                 Map.Entry<Integer, Map<Integer, DistancesDC.Diff2>> eEntries) {
        var eDiffSource = eEntries.getKey();

        // For each update, check if `src` had a diff output from `reduce` in previous iteration. If not, we can exit.
        var rDiffs = distancesR.getDiffsAt(eDiffSource, (short) (iter - 1), false);
        if (rDiffs[0] == 0) {
            return;
        }
        var prevPR = Distances.getDistanceFromArray(rDiffs, 1); // Assuming only one output
        if (iter == 2) {
            prevPR -= SIXM;
        }

        // Remove old contributions
        var oldAdjList = Graph.INSTANCE.getForwardUnMergedAdjacencyList(eDiffSource);
        if (oldAdjList != null && oldAdjList.getSize() != 0) {
            var oldContrib = (5 * prevPR) / (oldAdjList.getSize() * 6L);
            if (oldContrib != 0) {
                for (int j = 0; j < oldAdjList.getSize(); j++) {
                    // If the vertex is not the destination, has no outgoing neighbors that may help this query/direction, then no need to it to frontier!
                    int neighbourVertexId = oldAdjList.getNeighbourId(j);
                    if (eDiffSource != neighbourVertexId) {
                        distancesJ.addTmpDiff(neighbourVertexId, -oldContrib, (short) 1);
                    }
                }
            }
        }
        // Add new contributions
        var newAdjList = Graph.INSTANCE.getForwardMergedAdjacencyList(eDiffSource);
        if (newAdjList != null && newAdjList.getSize() != 0) {
            var newContrib = (5 * prevPR) / (newAdjList.getSize() * 6L);
            if (newContrib != 0) {
                for (int j = 0; j < newAdjList.getSize(); j++) {
                    // If the vertex is not the destination, has no outgoing neighbors that may help this query/direction, then no need to it to frontier!
                    int neighbourVertexId = newAdjList.getNeighbourId(j);
                    if (eDiffSource != neighbourVertexId) {
                        distancesJ.addTmpDiff(neighbourVertexId, newContrib, (short) 1);
                    }
                }
            }
        }
    }

    protected boolean shouldNegateDiffs(){
        return false;
    }

    @Override
    protected boolean shouldSkipEdge(short iteration, short eType) {
        return false;
    }

    public long getSrcDstDistance() {
        return distancesR.getVerticesWithDiff().size();
    }
}
