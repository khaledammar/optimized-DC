package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.util.Report;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

import java.util.*;

public class DistancesWithLocalDiff extends Distances {

    protected Map<Integer, List<Diff>> realDiff;
    protected Map<Integer, List<Diff>> deltaDiffs;


    /**
     * Default constructor with no source.
     */
    public DistancesWithLocalDiff() {
        super();
        realDiff = new HashMap<>();
        deltaDiffs = new HashMap<>();
    }

    /**
     * Default public constructor.
     */
    public DistancesWithLocalDiff(int queryId, int source, int destination, NewUnidirectionalDifferentialBFS.Queries queryType) {
        super(queryId, source, destination, queryType);
        realDiff = new HashMap<>();
        deltaDiffs = new HashMap<>();
    }


    public DistancesWithLocalDiff(int queryId, int source, int destination, Graph.Direction d,
                                  NewUnidirectionalDifferentialBFS.Queries queryType) {
        super(queryId, source, destination, d, queryType);
        realDiff = new HashMap<>();
        deltaDiffs = new HashMap<>();
    }

    public Set<Integer> getVerticesWithDiff() {

        IntOpenHashSet allV = new IntOpenHashSet();
        allV.addAll(realDiff.keySet());
        allV.addAll(deltaDiffs.keySet());

        return allV;
    }

    public void mergeDeltaDiffs() {
        // merge two lists

        realDiff.putAll(deltaDiffs);

        // Create vertex stats when needed
        if (Report.INSTANCE.appReportingLevel == Report.Level.INFO) {
            for (Integer v : deltaDiffs.keySet()) {
                VertexStats stats = vertexHistory.get(v);

                if (null == stats) {
                    stats = new VertexStats();
                    vertexHistory.put(v, stats);
                }
            }
        }

        deltaDiffs.clear();
    }


    public void reInitialize() {
        this.realDiff.clear();
        this.deltaDiffs.clear();
        this.frontier.clear();
        this.previousFrontier.clear();
        this.vertexHistory.clear();
        this.latestIteration = 0;
        minFrontierDistances = new VertexIterationDistancePair[100];
        nextFrontierSize = 0;
        setVertexDistance(source, (short) 0 /* iteration number */, 0.0 /* distance */);
        frontierReady = true;
    }

    public void removeDiff(int v) {
        deltaDiffs.remove(v);
        realDiff.remove(v);
    }

    public List<Diff> getMergedDiffs(Integer v) {
        if (deltaDiffs.containsKey(v)) {
            return deltaDiffs.get(v);
        } else if (realDiff.containsKey(v)) {
            return realDiff.get(v);
        } else {
            return new ArrayList<>();
        }
    }

    public List<Diff> getOldDiffs(int v) {

        if (DistancesWithDropBloom.debug(v)) {
            if (realDiff.containsKey(v)) {
                Report.INSTANCE.error("---- getOldDiffs v=" + v + " distances = " +
                        Arrays.toString(realDiff.get(v).toArray()));
            }
        }

        if (realDiff.containsKey(v)) {
            return realDiff.get(v);
        } else {
            return new ArrayList<>();
        }
    }

    public void print() {
        System.out.println("Not printing!");
    }

    public int size() {
        int size = 0;
        for (Map.Entry<Integer, List<Diff>> entry : realDiff.entrySet()) {
            size += entry.getValue().size();
        }
        return size;
    }


    void initializeRealDiff() {
        initializeDroppedDiff();

        realDiff = new HashMap<>();
        deltaDiffs = new HashMap<>();
        realDiffInitialized = true;

        return;
    }


    void clearVertexDistanceAfterIterNo(int vertexId, int iteration) {

        short iterNo = (short) iteration;

        List<Diff> distances = this.getMergedDiffs(vertexId);  //vIterDistPairMap.get(vertexId);
        if (distances == null) {
            return;
        }
        if (!deltaDiffs.containsKey(vertexId)) {
            distances = new ArrayList<>(distances);
        }

        while (!distances.isEmpty()) {
            if ((distances.get(distances.size() - 1)).iterationNo > iterNo) {
                Diff iterDistPair = distances.remove(distances.size() - 1);
                updateMinFrontierDistancesIfNecessary(vertexId, iterDistPair, true /* isDeletion */);
            } else {
                break;
            }
        }
        deltaDiffs.put(vertexId, distances);
    }

    void clearVertexDistanceAtT(int vertexId, int iterationNo) {

        if (DistancesWithDropBloom.debug(vertexId)) {
            Report.INSTANCE.error("=========---- clearVertexDistanceAtT v=" + vertexId + " iter= " + iterationNo);
        }

        List<Diff> distances = this.getMergedDiffs(vertexId);  //vIterDistPairMap.get(vertexId);
        if (distances == null || distances.size() == 0) {
            return;
        }

        int indexToRemove = -1;
        for (int i = 0; i < distances.size(); ++i) {
            Diff iterationDistPair = distances.get(i);
            if (iterationDistPair.iterationNo == iterationNo) {
                indexToRemove = i;
                break;
            } else if (iterationDistPair.iterationNo > iterationNo) {
                break;
            }
        }
        if (indexToRemove >= 0) {

            if (!deltaDiffs.containsKey(vertexId)) {
                distances = new ArrayList<>(distances);
            }

            Diff iterDistPair = distances.remove(indexToRemove);
            updateMinFrontierDistancesIfNecessary(vertexId, iterDistPair, true /* isDeletion */);

            deltaDiffs.put(vertexId, distances);
        }

        if (DistancesWithDropBloom.debug(vertexId)) {
            Report.INSTANCE.error("=========---- clearVertexDistanceAtT v=" + vertexId + " iter= " + iterationNo +
                    " NewDistances = " + Arrays.toString(getMergedDiffs(vertexId).toArray()) + " vs OLD : " +
                    Arrays.toString(getOldDiffs(vertexId).toArray()));
        }
    }


    /**
     * @param vertexId    ID of a vertex.
     * @param iterationNo iteration number for which to update the distance
     * @param distance    distance of the given vertex to the source in the given iteration number.
     */
    void setVertexDistance(int vertexId, int iteration, double distance) {

        short iterationNo = (short) iteration;

        if (DistancesWithDropBloom.debug(vertexId)) {
            Report.INSTANCE
                    .error("---- setVertexDistance v=" + vertexId + " iter= " + iterationNo + " distance= " + distance);
        }

        if (iterationNo == latestIteration && Double.MAX_VALUE != distance) {
            frontier.add(vertexId);

            nextFrontierSize += Graph.getInstance().getVertexDegree(vertexId, direction);

            Report.INSTANCE.debug("** Adding vertex " + vertexId + " with direction " + direction +
                    " to make nextFrontier size = " + nextFrontierSize);
        }

        List<Diff> distances = getMergedDiffs(vertexId);//vIterDistPairMap.get(vertexId);
        // TODO:This might be needed
        if (!deltaDiffs.containsKey(vertexId)) {
            distances = new ArrayList<>(distances);
        }
        deltaDiffs.put(vertexId, distances);

        int indexToAdd = 0;
        while (indexToAdd < distances.size()) {
            IterationDistancePair iterationDistancePair = (IterationDistancePair) distances.get(indexToAdd);

            if (iterationDistancePair.iterationNo == iterationNo) {

                /**
                 * We need to create a new class for an updated Diff because the distances list we have was made
                 * using a shallow clone. This means it is a new list, but the objects in it are shared with the old-Diff.
                 *
                 * This is helpful to reduce memory consumption and class creation but would lead to problems when
                 * the old and new lists use the same object in the list.
                 *
                 */
                Diff newDiff = new IterationDistancePair(iterationNo, distance);
                distances.remove(indexToAdd);
                distances.add(indexToAdd, newDiff);

                updateMinFrontierDistancesIfNecessary(vertexId, iterationDistancePair, false); // not a deletion
                return;
            } else if (iterationDistancePair.iterationNo > iterationNo) {
                break;
            }
            indexToAdd++;
        }

        Diff iterDistPair = new IterationDistancePair(iterationNo, distance);
        distances.add(indexToAdd, iterDistPair);
        updateMinFrontierDistancesIfNecessary(vertexId, iterationNo, distance, false /* is not deletion */);
    }
}
