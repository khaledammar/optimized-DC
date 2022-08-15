package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.Graph.Direction;
import ca.waterloo.dsg.graphflow.graph.SortedAdjacencyList;

import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of Unidirectional unweighted differential BFS. The class takes
 * as input a direction in which to do the BFS.
 */
public class NewDifferentialWCCDC extends NewUnidirectionalDifferentialBFSDC {

    public NewDifferentialWCCDC(int queryId, int source, int maxHop, Direction direction) {
        super(queryId, source, -1, direction);
    }

    @Override
    protected void initFrontierAndSourceDistance(int queryId) {
        distancesJ = new DistancesDC(queryId, source, destination, direction, NewUnidirectionalDifferentialBFS.Queries.WCC, "Join");
        distancesR = new DistancesDC(queryId, source, destination, direction, NewUnidirectionalDifferentialBFS.Queries.WCC,"Reduce");
    }

    @Override
    boolean shouldStopEarly(short iteration) {
        return false;
    }

    @Override
    void getNeighborsData(DistancesDC distancesJ, int currentVertexId, short iteration, long currentDistance,
                          short diff, boolean shouldRetract) {
        // TODO(sid)
        SortedAdjacencyList adjList = Graph.INSTANCE.getForwardMergedAdjacencyList(currentVertexId);

        for (int j = 0; j < adjList.getSize(); j++) {
            // If the vertex is not the destination, has no outgoing neighbors that may help this query/direction, then no need to it to frontier!
            int neighbourVertexId = adjList.getNeighbourId(j);
            if (currentVertexId != neighbourVertexId) {
                distancesJ.addTmpDiff(neighbourVertexId, currentDistance, diff);
            }
        }

        adjList = Graph.INSTANCE.getBackwardMergedAdjacencyList(currentVertexId);

        for (int j = 0; j < adjList.getSize(); j++) {
            // If the vertex is not the destination, has no outgoing neighbors that may help this query/direction, then no need to it to frontier!
            int neighbourVertexId = adjList.getNeighbourId(j);
            if (currentVertexId != neighbourVertexId) {
                distancesJ.addTmpDiff(neighbourVertexId, currentDistance, diff);
            }
        }

    }

    protected void processUpdate(short iter, Map.Entry<Integer, Map<Integer, DistancesDC.Diff2>> eEntries) {
        var eDiffSource = eEntries.getKey();

        // For each update, check if `src` had a diff output from `reduce` in previous iteration.
        var rDiffs = distancesR.getDiffsAt(eDiffSource, (short) (iter - 1), false);

        // For each update, get diffs from previous iteration and compute new distances based on the update.
        for (var eEntry : eEntries.getValue().entrySet()) {
            var eDiffDest = eEntry.getKey();
            var eDestDiffs = distancesR.getDiffsAt(eDiffDest, (short) (iter - 1), false);
            var eDiffs = eEntry.getValue();
            var x = rDiffs[0] !=0 ? distancesJ.lastDiffs.computeIfAbsent(eDiffDest, l -> new HashMap<>()) : null;
            var y = eDestDiffs[0] !=0 ? distancesJ.lastDiffs.computeIfAbsent(eDiffSource, l -> new HashMap<>()) : null;

            for (int j = 0; j < eDiffs.count; j++) {
                if (shouldSkipEdge(iter, eDiffs.types[j])) {
                    continue;
                }
                var eDiff = eDiffs.diffs[j];
                for (int k = 1; k < rDiffs[0] * 5 + 1; k += 5) {
                    long rDistance = Distances.getDistanceFromArray(rDiffs, k);
                    var rDiff = rDiffs[k];
                    short newDiff = (short) (eDiff * rDiff);
                    DistancesDC.mergeDiff(x, rDistance, newDiff);
                }
                for (int k = 1; k < eDestDiffs[0] * 5 + 1; k += 5) {
                    long rDistance = Distances.getDistanceFromArray(eDestDiffs, k);
                    var rDiff = eDestDiffs[k];
                    short newDiff = (short) (eDiff * rDiff);
                    DistancesDC.mergeDiff(y, rDistance, newDiff);
                }
            }
        }
    }

    @Override
    protected boolean shouldSkipEdge(short iteration, short eType) {
        return false;
    }

    public long getSrcDstDistance() {
        return distancesR.getVerticesWithDiff().size();
    }
}
