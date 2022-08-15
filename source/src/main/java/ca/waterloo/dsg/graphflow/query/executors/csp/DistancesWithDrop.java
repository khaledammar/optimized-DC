package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.SortedAdjacencyList;
import ca.waterloo.dsg.graphflow.util.ArrayUtils;
import ca.waterloo.dsg.graphflow.util.Report;

import java.util.*;
import java.util.Map.Entry;

/**
 * Represents a data structure that keeps track of the distances that each vertex took in different
 * iterations. Contains utility methods to add new distances or clear distances after some iteration
 * and keep track of the size of distances.
 */
public class DistancesWithDrop extends Distances {

    /**
     * This is only added here because I removed it from Distances.
     * This class is abandoned anyway, but if it is going to be used again then we should replace vIterDistPairMap references by realDiff.
     */
    protected Map<Integer, List<Diff>> vIterDistPairMap;
    protected Map<Integer, DroppedDiff> droppedVertices;
    float dropProbability;
    private Random droppingRandom;

    /**
     * Default constructor with no source.
     */
    public DistancesWithDrop(float probability) {
        super();
        droppedVertices = new HashMap<>(1);
        setRandomVariable(probability);
    }

    /**
     * Default public constructor.
     */
    public DistancesWithDrop(int queryId, int source, int destenation, float probability
            , NewUnidirectionalDifferentialBFS.Queries queryType) {
        super(queryId, source, destenation, queryType);
        droppedVertices = new HashMap<>(1);
        setRandomVariable(probability);
    }

    public DistancesWithDrop(int queryId, int source, int destination, Graph.Direction d, float probability
            , NewUnidirectionalDifferentialBFS.Queries queryType) {
        super(queryId, source, destination, d, queryType);

        droppedVertices = new HashMap<>(1);
        setRandomVariable(probability);

        System.out.println("******* " + droppedVertices.size());
    }

    public void setRandomVariable(float probability) {

        System.out.println("************ Set probability values " + probability);

        int dropSeed = 83101461;
        dropProbability = probability;
        droppingRandom = new Random(dropSeed);
    }

    private boolean shouldDrop() {

        float prob = droppingRandom.nextFloat();
        System.out.println("**** Probability is " + prob + " vs dropProb. " + dropProbability);

        if (prob < dropProbability) {
            return true;
        } else {
            return false;
        }
    }

    void vertexToFixCount(int vertexId) {
        VertexStats stats = vertexHistory.get(vertexId);
        if (null == stats) {
            stats = new VertexStats();
        }
        stats.addedToFix++;
        return;
    }

    /**
     * TODO, handle dropped vertices
     *
     * @param iterationNo iteration number to set the latestIteration to.
     */
    void setLatestIterationNumber(int iterationNo) {

        Report.INSTANCE.debug("**DROP**set Iteration number to " + iterationNo);

        this.latestIteration = (short) iterationNo;
        clearDistancesAfterT(iterationNo);
        frontier.clear();
        nextFrontierSize = 0;

        // Fill the frontier with regular vertices
        for (Entry<Integer, List<Diff>> vertexDistancesEntry : vIterDistPairMap.entrySet()) {
            List<Diff> distancePairs = vertexDistancesEntry.getValue();
            if (iterationNo == distancePairs.get(distancePairs.size() - 1).iterationNo) {
                frontier.add(vertexDistancesEntry.getKey());
                nextFrontierSize += Graph.getInstance().getVertexDegree(vertexDistancesEntry.getKey(), direction);
            }
        }

        // Fill the frontier with dropped vertices
        for (Entry<Integer, DroppedDiff> vertexDistancesEntry : droppedVertices.entrySet()) {
            List<Short> distances = vertexDistancesEntry.getValue().iterations;
            if (iterationNo == distances.get(distances.size() - 1)) {
                frontier.add(vertexDistancesEntry.getKey()); // vertex
                nextFrontierSize += Graph.getInstance().getVertexDegree(vertexDistancesEntry.getKey(), direction);
            }
        }
    }

    /*
     *
     * Check if a vertex is a dropped vertex which means it does not have all its diffs
     * */

    boolean isDropped(int vertexId) {

        if (droppedVertices == null) {
            droppedVertices = new HashMap<>(1);
        }

        System.out.println("Vertex " + vertexId + " is Dropped ? " + droppedVertices.containsKey(vertexId));

        return droppedVertices.containsKey(vertexId);
    }


    public double getDistance(int vertexId, int iteration) {
        double dist = Double.MAX_VALUE;

        Report.INSTANCE.debug("**** get distance of " + vertexId + " @ " + iteration);

        if (iteration < 0) {
            System.out.println("ERRORR***************** " + iteration);
            System.exit(1);
        }

        if (vertexId != source && iteration == 0) {

            Report.INSTANCE.debug("**** get distance of " + vertexId + " @ " + iteration + " = MAX");
            return Double.MAX_VALUE;
        }

        // if it is a regula vertex, just return its distance
        if (!isDropped(vertexId)) {
            return getRegularVertexDistance(vertexId, iteration);
        }

        Report.INSTANCE.debug("**** get Dropped distance of " + vertexId + " @ " + iteration);

        //Check if the iteration is more than the vertex's max iteration
        if (iteration >=
                droppedVertices.get(vertexId).iterations.get(droppedVertices.get(vertexId).iterations.size() - 1)) {

            Report.INSTANCE.debug("**** Found the latest distance of " + vertexId + " @ " + iteration + " = " +
                    droppedVertices.get(vertexId).lastDistance);

            return droppedVertices.get(vertexId).lastDistance;
        }

        short previousIteration = (short) (iteration - 1);
        //If not, then look for its neighbours
        //This is overhead of not keeping all diffs
        SortedAdjacencyList inNeighbours = getInNeighbours(vertexId, true, (short) iteration);
        Report.INSTANCE.debug("**** Neighbours are: " + Arrays.toString(inNeighbours.neighbourIds));



        // loop over inNeighbours to find the minimum distance
        for (int i = 0; i < inNeighbours.getSize(); i++) {

            Integer v = inNeighbours.neighbourIds[i];
            double w = inNeighbours.weights[i];

            double v_dist = 0;

            // there is no need to check the distance of v
            if (dist < w) {
                continue;
            }

            if (isDropped(v)) {
                v_dist = getDistance(v, previousIteration);
            } else {
                v_dist = getRegularVertexDistance(v, previousIteration);
            }

            if (dist > v_dist + w) {
                dist = v_dist + w;
            }
        }

        return dist;
    }

    /**
     * Clears the distances that are greater than t but not including t.
     *
     * @param t iteration number. Distances that vertices took in later
     *          iterations than t will be removed.
     */
    void clearDistancesAfterT(int t) {
        List<Integer> verticesToRemove = new ArrayList<>();

        // Regular vertices
        for (Entry<Integer, List<Diff>> entry : vIterDistPairMap.entrySet()) {
            List<Diff> iterDistPairs = entry.getValue();
            int indexToRemove = iterDistPairs.size();
            for (int j = iterDistPairs.size() - 1; j >= 0; --j) {
                if (iterDistPairs.get(j).iterationNo <= t) {
                    break;
                }
                indexToRemove--;
            }
            int size = iterDistPairs.size();
            for (int i = indexToRemove; i < size; ++i) {
                iterDistPairs.remove(indexToRemove);
            }
            if (iterDistPairs.isEmpty()) {
                verticesToRemove.add(entry.getKey());
            }
        }

        for (Entry<Integer, DroppedDiff> entry : droppedVertices.entrySet()) {
            List<Short> iterations = entry.getValue().iterations;

            int indexToRemove = iterations.size();

            for (int j = iterations.size() - 1; j >= 0; --j) {
                if (iterations.get(j) <= t) {
                    break;
                }
                indexToRemove--;
            }

            int size = iterations.size();

            for (int i = indexToRemove; i < size; ++i) {
                iterations.remove(indexToRemove);
            }
            if (iterations.isEmpty()) {
                verticesToRemove.add(entry.getKey());
            } else {
                // if we will not remove the vertex then we need to update its distance at iteration t
                entry.getValue().lastDistance = getDistance(entry.getKey(), t);
            }
        }

        for (int vertexToRemove : verticesToRemove) {
            if (isDropped(vertexToRemove)) {
                droppedVertices.remove(vertexToRemove);
            } else {
                vIterDistPairMap.remove(vertexToRemove);
            }
        }

        // This is an array that represents the minimum vertex distance at each iteration
        for (int i = t + 1; i < minFrontierDistances.length; ++i) {
            minFrontierDistances[i] = null;
        }
    }

    /**
     * Clears each distance that a vertex took before and including a particular
     * iteration number.
     *
     * @param vertexId ID of the vertex whose distances should be cleared.
     * @param iterNo   iteration number. The vertex's value at iterations less than or equal t will be
     *                 removed.
     */
    void clearVertexDistancesAtAndBeforeT(int vertexId, int iterNo) {

        if (isDropped(vertexId)) {
            // dropped vertex

            List<Short> distances = droppedVertices.get(vertexId).iterations;

            while (!distances.isEmpty()) {
                if (distances.get(0) <= iterNo) {
                    int iter = distances.remove(0);
                    // the distance is zero because it is not used in the function
                    updateMinFrontierDistancesIfNecessary(vertexId, new IterationDistancePair((short) iter, 0),
                            true /* isDeletion */);
                } else {
                    break;
                }
            }

            if (distances.isEmpty()) {
                droppedVertices.remove(vertexId);
                frontier.remove(vertexId);
                nextFrontierSize -= Graph.getInstance().getVertexDegree(vertexId, direction);
            }
        } else {
            // regular vertex

            List<Diff> distances = vIterDistPairMap.get(vertexId);
            if (distances == null) {
                return;
            }

            while (!distances.isEmpty()) {
                if (distances.get(0).iterationNo <= iterNo) {
                    Diff iterDistPair = distances.remove(0);
                    updateMinFrontierDistancesIfNecessary(vertexId, iterDistPair, true /* isDeletion */);
                } else {
                    break;
                }
            }

            if (distances.isEmpty()) {
                vIterDistPairMap.remove(vertexId);
                frontier.remove(vertexId);
                nextFrontierSize -= Graph.getInstance().getVertexDegree(vertexId, direction);
            }
        }
    }


    void clearVertexDistanceAfterIterNo(int vertexId, int iterNo) {

        if (isDropped(vertexId)) {

            List<Short> distances = droppedVertices.get(vertexId).iterations;

            while (!distances.isEmpty()) {
                if (distances.get(distances.size() - 1) > iterNo) {
                    int iter = distances.remove(distances.size() - 1);

                    // added distance zero because the function does not use it
                    updateMinFrontierDistancesIfNecessary(vertexId, new IterationDistancePair((short) iter, 0),
                            true /* isDeletion */);
                } else {
                    break;
                }
            }
        } else {

            List<Diff> distances = vIterDistPairMap.get(vertexId);
            if (distances == null) {
                return;
            }
            while (!distances.isEmpty()) {
                if (distances.get(distances.size() - 1).iterationNo > iterNo) {
                    Diff iterDistPair = distances.remove(distances.size() - 1);
                    updateMinFrontierDistancesIfNecessary(vertexId, iterDistPair, true /* isDeletion */);
                } else {
                    break;
                }
            }
        }
    }


    void clearVertexDistanceAtT(int vertexId, int iterationNo) {

        if (isDropped(vertexId)) {

            List<Short> distances = droppedVertices.get(vertexId).iterations;

            int indexToRemove = -1;
            for (int i = 0; i < distances.size(); ++i) {
                int iter = distances.get(i);
                if (iter == iterationNo) {
                    indexToRemove = i;
                    break;
                } else if (iter > iterationNo) {
                    break;
                }
            }
            if (indexToRemove >= 0) {
                int iter = distances.remove(indexToRemove);
                // added distance zero because the function does not use it
                updateMinFrontierDistancesIfNecessary(vertexId, new IterationDistancePair((short) iter, 0),
                        true /* isDeletion */);
            }
        } else {
            List<Diff> distances = vIterDistPairMap.get(vertexId);
            if (distances == null) {
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
                Diff iterDistPair = distances.remove(indexToRemove);
                updateMinFrontierDistancesIfNecessary(vertexId, iterDistPair, true /* isDeletion */);
            }
        }
    }


    /**
     * @param vertexId    ID of a vertex.
     * @param iterationNo iteration number for which to update the distance
     * @param distance    distance of the given vertex to the source in the given iteration number.
     */
    void setVertexDistance(int vertexId, int iterationNo, double distance) {
        if (Double.MAX_VALUE == distance) {
            clearVertexDistancesAtAndBeforeT(vertexId, iterationNo);
        }

        if (iterationNo == latestIteration && Double.MAX_VALUE != distance) {
            frontier.add(vertexId);
            nextFrontierSize += Graph.getInstance().getVertexDegree(vertexId, direction);

            Report.INSTANCE.debug("**DROP** Adding vertex " + vertexId + " with direction " + direction +
                    " to make nextFrontier size = " + nextFrontierSize);
        }

        // Create vertex stats when needed
        VertexStats stats = vertexHistory.get(vertexId);
        if (Report.INSTANCE.appReportingLevel == Report.Level.DEBUG) {
            if (null == stats) {
                stats = new VertexStats();
                vertexHistory.put(vertexId, stats);
            }
        }

        if (isDropped(vertexId)) {

            int size = droppedVertices.get(vertexId).iterations.size();
            if (droppedVertices.get(vertexId).iterations.get(size - 1) < iterationNo) {
                // I need to add distance and update last distance
                droppedVertices.get(vertexId).iterations.add((short) iterationNo);
                droppedVertices.get(vertexId).lastDistance = distance;

                /*
                TODO, from statistics point of view, we only have one Diff in the dropped vertices, always

                if (stats.LargestDiffSize < droppedVertices.get(vertexId).iterations.size())
                    stats.LargestDiffSize = droppedVertices.get(vertexId).iterations.size();
                */

            } else if (droppedVertices.get(vertexId).iterations.get(size - 1) == iterationNo) {
                droppedVertices.get(vertexId).lastDistance = distance;
            }

            updateMinFrontierDistancesIfNecessary(vertexId, new IterationDistancePair((short) iterationNo, distance),
                    false /* is not deletion */);
            return;
        }

        // if not dropped
        List<Diff> distances = vIterDistPairMap.get(vertexId);

        // if does not have previous distances?
        if (null == distances) {

            // Check if we should drop the diffs of this vertex or not
            if (vertexId != this.source && shouldDrop()) {
                droppedVertices.put(vertexId, new DroppedDiff((short) iterationNo, distance));
                IterationDistancePair iterDistPair = new IterationDistancePair((short) iterationNo, distance);
                updateMinFrontierDistancesIfNecessary(vertexId, iterDistPair, false /* is not deletion */);

                if (Report.INSTANCE.appReportingLevel == Report.Level.DEBUG) {
                    stats.LargestDiffSize = 1;
                }

                return;
            } else {
                distances = new ArrayList<>(1);
                vIterDistPairMap.put(vertexId, distances);
            }
        }

        int indexToAdd = 0;
        while (indexToAdd < distances.size()) {
            IterationDistancePair iterationDistancePair = ((IterationDistancePair) distances.get(indexToAdd));

            if (iterationDistancePair.iterationNo == iterationNo) {
                iterationDistancePair.distance = distance;
                updateMinFrontierDistancesIfNecessary(vertexId, iterationDistancePair, false /* not a deletion */);
                return;
            } else if (iterationDistancePair.iterationNo > iterationNo) {
                break;
            }
            indexToAdd++;
        }

        IterationDistancePair iterDistPair = new IterationDistancePair((short) iterationNo, distance);
        distances.add(indexToAdd, iterDistPair);

        if (Report.INSTANCE.appReportingLevel == Report.Level.DEBUG) {
            if (stats.LargestDiffSize < distances.size()) {
                stats.LargestDiffSize = distances.size();
            }
        }

        updateMinFrontierDistancesIfNecessary(vertexId, iterDistPair, false /* is not deletion */);
    }

    /**
     * This function is needed if a vertex's distance increase due to changing in weight or a delete
     * At the moment, we will not support these cases using bloom filters
     */
    /**
     * Checks whether or not the min distance vertex needs to change in the frontier of the iteration,
     * in which the given update to the distance of a vertex is happening.
     *
     * @param vertexId     ID of the vertex that is being updated.
     * @param iterDistPair the iteration number and the distance of the vertex.
     * @param isDeletion   whether or not the iterDistPair is being deleted for the vertex.
     */
    protected void updateMinFrontierDistancesIfNecessary(int vertexId, Diff iterDistPair, boolean isDeletion) {
        int iterationNo = iterDistPair.iterationNo;
        if (isDeletion) {
            VertexIterationDistancePair currentMinVIterDistPair = minFrontierDistances[iterationNo];
            // Warning: We don't explicitly check it but currentMinVIterDistPair has to be non null
            // because if we are deleting the distance of a vertex in a particular iteration i,
            // there must be at least one vertex in the frontier for iteration i. Therefore there must
            // be a vertex with the minimum distance in that frontier.
            if (vertexId == currentMinVIterDistPair.vertexId) {
                findNewMinDistanceVertexInFrontier(iterDistPair.iterationNo);
            }
        } else {
            if (iterationNo + 1 > minFrontierDistances.length) {
                minFrontierDistances = (VertexIterationDistancePair[]) ArrayUtils
                        .resizeIfNecessary(minFrontierDistances, iterationNo + 1);
            }
            if (minFrontierDistances[iterationNo] == null || ((IterationDistancePair) iterDistPair).distance <
                    minFrontierDistances[iterationNo].iterDistPair.distance) {
                minFrontierDistances[iterationNo] =
                        new VertexIterationDistancePair(vertexId, (IterationDistancePair) iterDistPair);
            } else {
                VertexIterationDistancePair currentMinVIterDistPair = minFrontierDistances[iterationNo];
                if (vertexId == currentMinVIterDistPair.vertexId) {
                    if (((IterationDistancePair) iterDistPair).distance >
                            currentMinVIterDistPair.iterDistPair.distance) {
                        findNewMinDistanceVertexInFrontier(iterDistPair.iterationNo);
                    }
                }
            }
        }
    }

    protected void findNewMinDistanceVertexInFrontier(int iterationNo) {
        minFrontierDistances[iterationNo] = null;
        int minVertexId = -1;
        double minDistance = Double.MAX_VALUE;
        IterationDistancePair minIterDistPair = null;

        // search in regular vertices first
        for (Entry<Integer, List<Diff>> vertexDistancesEntry : vIterDistPairMap.entrySet()) {
            for (Diff iterDistPair : vertexDistancesEntry.getValue()) {
                if (iterDistPair.iterationNo == iterationNo &&
                        ((IterationDistancePair) iterDistPair).distance < minDistance) {
                    minVertexId = vertexDistancesEntry.getKey();
                    minIterDistPair = ((IterationDistancePair) iterDistPair);
                    minDistance = ((IterationDistancePair) iterDistPair).distance;
                }
            }
        }

        // search in dropped vertices
        for (Entry<Integer, DroppedDiff> vertex : droppedVertices.entrySet()) {
            double dist = getDistance(vertex.getKey(), iterationNo);
            if (dist < minDistance) {
                minVertexId = vertex.getKey();
                minDistance = dist;
                minIterDistPair = new IterationDistancePair((short) iterationNo, dist);
            }
        }

        if (null != minIterDistPair) {
            minFrontierDistances[iterationNo] = new VertexIterationDistancePair(minVertexId, minIterDistPair);
        }
    }

    /**
     * Clears all the distances of a vertex and adds a single {@link IterationDistancePair} for the
     * vertex with the given iterationNo and distance.
     * <p>
     * Note: This is a specialized method provided for unidirectional unweighted differential BFS.
     *
     * @param vertexId    ID of the vertex.
     * @param iterationNo iteration number.
     * @param distance    distance of the vertex from the source at the given iteration number.
     */
    void clearAndSetOnlyVertexDistance(int vertexId, int iterationNo, double distance) {

        Report.INSTANCE
                .debug("**DROP------ clearAndSetOnlyVertexDistance = Set " + vertexId + " @ " + iterationNo + " to " +
                        distance);

        if (isDropped(vertexId)) {
            List<Short> distances = droppedVertices.get(vertexId).iterations;

            Report.INSTANCE.debug("**DROP* Previous distances that is going to be deleted : " +
                    Arrays.toString(distances.toArray()));

            while (!distances.isEmpty()) {
                int deletedDist = distances.remove(0);
                // TODO, it is alright to send a random distance here because it is not needed by the function
                IterationDistancePair iterDistPair = new IterationDistancePair((short) deletedDist, 0);
                updateMinFrontierDistancesIfNecessary(vertexId, iterDistPair, true /* isDeletion */);
            }
        } else {
            // usual case!
            List<Diff> distances = vIterDistPairMap.get(vertexId);

            if (null != distances) {
                Report.INSTANCE.debug("**DROP* Previous distances that is going to be deleted : " +
                        Arrays.toString(distances.toArray()));

                while (!distances.isEmpty()) {
                    Diff iterDistPair = distances.remove(0);
                    updateMinFrontierDistancesIfNecessary(vertexId, iterDistPair, true /* isDeletion */);
                }
            } else {
                Report.INSTANCE.debug("**DROP* Previous distances that is going to be deleted : []");
            }
        }
        setVertexDistance(vertexId, iterationNo, distance);
    }


    /**
     * @param vertexId ID of a vertex.
     * @return Distance of the vertex at the maximum known iteration.
     */
    double getLatestDistance(int vertexId) {

        if (isDropped(vertexId)) {
            return droppedVertices.get(vertexId).lastDistance;
        }

        List<Diff> distances = vIterDistPairMap.get(vertexId);
        if (null == distances || distances.isEmpty()) {
            return Double.MAX_VALUE;
        } else {
            return ((IterationDistancePair) distances.get(distances.size() - 1)).distance;
        }
    }

    /**
     * @param vertexId ID of a vertex.
     * @return The iteration with the best Distance of the vertex.
     */
    int getIterationWithBestDistance(int vertexId) {

        System.err.println("***** Warning ****** ");
        System.err.println("***** Do not use getIterationWithBestDistance with Dropping Diff execType ****** ");
        System.err.println("***** It is going to be buggy and produce errors ****** ");

        int i = 1;

        assert i != 1 : "ERROR, you cannot use getIterationWithBestDistance with dropped vertices!";

        List<Diff> distances = vIterDistPairMap.get(vertexId);
        if (null == distances || distances.isEmpty()) {
            return Integer.MAX_VALUE;
        } else {
            return distances.get(distances.size() - 1).iterationNo;
        }
    }


    /**
     * @param vertexId ID of a vertex.
     * @return Distances the vertex took at different iterations. In other words, for
     * each iteration the distance of the vertex changed, the returned list contains one
     * {@link IterationDistancePair}.
     */
    List<Diff> getAllDistances(int vertexId) {

        System.err.println("***** Warning ****** ");
        System.err.println("***** Do not use getAllDistances with Dropping Diff execType ****** ");
        System.err.println("***** It is going to be buggy and produce errors ****** ");

        int i = 1;
        assert i != 1 : "ERROR, you cannot use getAllDistances with dropped vertices!";

        List<Diff> retVal = vIterDistPairMap.get(vertexId);
        return (null == retVal) ? emptyList : retVal;
    }

    /**
     * Very similar to getAllDistances but it returns a list of iterations instead
     *
     * @param vertexId ID of a vertex.
     * @return
     */
    List<Short> getAllIterations(int vertexId) {

        if (isDropped(vertexId)) {
            return droppedVertices.get(vertexId).iterations;
        }

        List<Diff> retVal = vIterDistPairMap.get(vertexId);
        List<Short> result = new ArrayList<Short>(0);
        if (null != retVal) {
            for (Diff pair : retVal) {
                result.add(pair.iterationNo);
            }
        }

        return result;
    }


    /**
     * @param vertexId    ID of a vertex.
     * @param iterationNo iteration at which the distance of the given vertex should be returned.
     * @return Distance of the vertex at the given iteration number. Note that if the vertex's
     * distance was not updated in the given iterationNo, the distance in the latest iteration less
     * than iterationNo is returned.
     */
    double getRegularVertexDistance(int vertexId, int iterationNo) {

        Report.INSTANCE.debug("**** get Regular distance of " + vertexId + " @ " + iterationNo);

        List<Diff> distances = vIterDistPairMap.get(vertexId);
        if (null == distances) {
            Report.INSTANCE.debug("**** get Regular distance of " + vertexId + " @ " + iterationNo + " = MAX");

            return Double.MAX_VALUE;
        } else {
            double latestDistance = Double.MAX_VALUE;
            for (Diff iterationDistance : distances) {
                if (iterationDistance.iterationNo > iterationNo) {
                    break;
                } else {
                    latestDistance = ((IterationDistancePair) iterationDistance).distance;
                }
            }

            Report.INSTANCE.debug("**DROP** v = " + vertexId + " distances = " + Arrays.toString(distances.toArray()));
            Report.INSTANCE.debug("**DROP** v = " + vertexId + " @ " + iterationNo + " = " + latestDistance);

            Report.INSTANCE.debug("**** get distance of " + vertexId + " @ " + iterationNo + " = " + latestDistance);
            return latestDistance;
        }
    }


    public void printStats() {
        Report.INSTANCE.error("=======================================");
        Report.INSTANCE.error("======== # vertices " + vertexHistory.keySet().size() + " ==========");
        Report.INSTANCE.error("======== Printing Statistics ===========**DROP\n");

        StringJoiner fixJoiner = new StringJoiner(",");
        StringJoiner sizeJoiner = new StringJoiner(",");
        int[] histogram_toFix = new int[1000000];
        int[] histogram_Diffs = new int[1000000];
        int max_fix = 0;
        int max_size = 0;

        for (Entry<Integer, VertexStats> iterDistPair : vertexHistory.entrySet()) {
            histogram_toFix[iterDistPair.getValue().addedToFix]++;
            histogram_Diffs[iterDistPair.getValue().LargestDiffSize]++;

            if (iterDistPair.getValue().addedToFix > 1000) {
                System.out.println("**** v= " + iterDistPair.getKey() + " " + iterDistPair.getValue().addedToFix);
            }

            if (iterDistPair.getValue().addedToFix > max_fix) {
                max_fix = iterDistPair.getValue().addedToFix;
            }
            if (iterDistPair.getValue().LargestDiffSize > max_size) {
                max_size = iterDistPair.getValue().LargestDiffSize;
            }
        }

        for (int i = 0; i <= max_fix; i++) {
            fixJoiner.add(String.valueOf(histogram_toFix[i]));
        }

        for (int i = 0; i <= max_size; i++) {
            sizeJoiner.add(String.valueOf(histogram_Diffs[i]));
        }

        System.out.println("#Fix-Histogram " + fixJoiner.toString());
        System.out.println("#DiffSize-Histogram " + sizeJoiner.toString());

        Report.INSTANCE.error("=======================================");
        //Report.INSTANCE.error("         Histogram (#fix, maxSize) ");
        //for(int i=0;i<100;i++)
        //    if(histogram_toFix[i] > 0 || histogram_Diffs[i] > 0)
        //        Report.INSTANCE.error( i +" : "+ histogram_toFix[i]+ " - "+histogram_Diffs[i]);
        //Report.INSTANCE.error("\n=======================================");
    }

    public void print() {

        Report.INSTANCE.debug("=======================================");
        Report.INSTANCE.debug("======== Printing distances ===========**DROP\n");
        for (Entry<Integer, List<Diff>> iterDistPair : vIterDistPairMap.entrySet()) {

            Report.INSTANCE
                    .debug(iterDistPair.getKey() + " --> " + vertexHistory.get(iterDistPair.getKey()).toString() +
                            "   (" + Arrays.toString(iterDistPair.getValue().toArray()) + ")");
        }

        Report.INSTANCE.debug("\n=======================================");
    }

    public int numberOfVertices() {
        int size = 0;
        for (Integer v : vIterDistPairMap.keySet()) {
            size++;
        }
        System.out.println("# Regular vertices = " + size);

        for (Integer v : droppedVertices.keySet()) {
            size++;
        }

        System.out.println("# total vertices = " + size);

        return size;
    }


    public int size() {
        int size = 0;
        for (Entry<Integer, List<Diff>> iterDistPair : vIterDistPairMap.entrySet()) {
            size += iterDistPair.getValue().size();
        }

        return size;
    }

    /**
     * @return whether the frontier is empty or not.
     */
    boolean isFrontierEmpty(int iterationNo) {
        return minFrontierDistances[iterationNo] == null;
    }


    /**
     * Keeps information about vertices for testing purposes
     */
    static class DroppedDiff {
        List<Short> iterations;
        double lastDistance;

        DroppedDiff(short iterationNo, double distance) {
            iterations = new ArrayList<>(1);
            iterations.add(iterationNo);
            lastDistance = distance;
        }

        @Override
        public String toString() {

            String str = "[";
            for (int i : iterations) {
                str = str + i + ",";
            }

            str += "] - " + lastDistance;
            return str;
        }
    }
}


