package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.SortedAdjacencyList;
import ca.waterloo.dsg.graphflow.util.Report;

import java.util.*;

/**
 * This class can be used to create an index of the Shortest-Path Tree (SPT) of one vertex (source).
 * Typically, this vertex is going to have a significantly large degree in comparison to the
 * remaining vertices in the graph. The idea is to create and update these kind of vertices once,
 * for all queries instead of updating them for each registered query in the system. That is why most
 * data and function members in the class is going to be static!
 */
public class LandmarkUnidirectionalUnweightedDifferentialBFS extends NewUnidirectionalUnweightedDifferentialBFS {


    static HashMap<Integer, Landmark> landmarks = new LinkedHashMap<>(10);
    long landmark_distance = -1;
    public LandmarkUnidirectionalUnweightedDifferentialBFS(int queryId, int source, int destination,
                                                           Graph.Direction direction, boolean backtrack, int k, Queries queryType) {

        super(queryId, source, destination, direction, backtrack, queryType);

        //Report.INSTANCE.debug("------ LandmarkUnidirectionalUnweightedDifferentialBFS - Landmark");

        if (landmarks.isEmpty()) {

            Report.INSTANCE.debug("** Landmark is empty");

            HashMap<Integer, Integer> sources = Graph.getInstance().getTopK(k, direction);

            Report.INSTANCE.debug("** Potential Landmarks : " + Arrays.toString(sources.keySet().toArray()));

            for (Map.Entry<Integer, Integer> entry : sources.entrySet()) {
                Landmark l = new Landmark(entry.getKey(), entry.getValue(), direction);
                landmarks.putIfAbsent(entry.getKey(), l);
            }

            Report.INSTANCE.debug("--------------------------------------------------------------------------");
            Report.INSTANCE.debug("----------------------- Finished computing Landmarks ---------------------");
            Report.INSTANCE.debug("--------------------------------------------------------------------------");
        }

        landmark_distance = getLandmarkDistance(this.source, this.destination);

        Report.INSTANCE.error("** Landmarks : " + Arrays.toString(landmarks.keySet().toArray()) + " distance = " +
                landmark_distance);

        // We need to rerun this becasue we want to handel a special case when source or destination is a landmark
        initFrontierAndSourceDistance();
    }

    public static long getLandmarkDistance(int source, int destination) {
        long min_distance = Long.MAX_VALUE;
        for (Map.Entry<Integer, Landmark> entry : landmarks.entrySet()) {

            long distance = entry.getValue().sptBWD.distances.getLatestDistance(source) +
                    entry.getValue().sptFWD.distances.getLatestDistance(destination);

            if (distance < min_distance) {
                min_distance = distance;
            }
        }

        //Report.INSTANCE.debug("** Landmark distance "+min_distance);

        return min_distance;
    }

    // if the source is one of the landmarks, then the answer should always be available in the landmark SPT
    // we add a dummy source vertex of -1 instead so that the frontier is always empty.
    private void initFrontierAndSourceDistance() {

        if (landmarks.keySet().contains(source)) {
            distances = new Distances();
        }
    }

    @Override
    public void updateNbrsDistance(int currentVertexId, SortedAdjacencyList currentVsAdjList, int neighborIdIndex,
                                   short currentIterationNo) {
        int neighbourId = currentVsAdjList.neighbourIds[neighborIdIndex];
        if (Long.MAX_VALUE == distances.getLatestDistance(neighbourId) && !landmarks.keySet().contains(neighbourId)) {
            distances.clearAndSetOnlyVertexDistance(neighbourId, currentIterationNo /* iteration no */,
                    currentIterationNo /* distance */);
        }
    }

    /**
     * This function only runs if the neighbor distance is infinity, otherwise it does nothing!
     *
     * @param currentVertexId
     * @param neighbourId
     * @param neighbourWeight
     * @param currentIterationNo
     */
    @Override
    public void updateNbrsDistance(int currentVertexId, long vertexsCurrentDist, int neighbourId,
                                   long neighbourWeight, short currentIterationNo) {

        Report.INSTANCE.debug("------ updateNbrsDistance - Landmark - " + neighbourId);

        // ignore landmark distances!
        if (landmarks.keySet().contains(neighbourId)) {
            return;
        }

        Report.INSTANCE.debug("== Neighbor distance is " + distances.getLatestDistance(neighbourId) + " vs " +
                Double.MAX_VALUE + " ? " + (Double.MAX_VALUE == distances.getLatestDistance(neighbourId)));
        if (Double.MAX_VALUE == distances.getLatestDistance(neighbourId)) {
            distances.clearAndSetOnlyVertexDistance(neighbourId, currentIterationNo /* iteration no */,
                    currentIterationNo /* distance */);
        }
    }

    boolean doesPathExist() {

        if (landmark_distance == Double.MAX_VALUE) {
            return Double.MAX_VALUE != distances.getLatestDistance(destination);
        } else {
            return true;
        }
    }

    /**
     * BFS should stop as soon as the destination is reached.
     *
     * @see {@link NewUnidirectionalDifferentialBFS#shouldStopBFSEarly(int)}.
     */
    @Override
    public boolean shouldStopBFSEarly(short currentIterationNo) {

        Report.INSTANCE.debug("------ shouldStopBFSEarly - Landmark");

        double diffs_distance = distances.getDistance(destination, currentIterationNo);

        Report.INSTANCE.debug("* current iteration " + currentIterationNo);
        Report.INSTANCE.debug("* diffs_distance = " + diffs_distance);
        Report.INSTANCE.debug("* landmark_distance = " + landmark_distance);
        Report.INSTANCE.debug("* Empty Frontier? = " + distances.isFrontierEmpty(currentIterationNo));
        Report.INSTANCE.debug("* Frontier: = " + Arrays.toString(distances.getCurrentFrontier().toArray()));

        if ((diffs_distance != Double.MAX_VALUE) ||
                (distances.isFrontierEmpty(currentIterationNo) && landmark_distance != Double.MAX_VALUE)) {
            return true;
        } else {
            return false;
        }
    }

    public void takeNewBFSStep() {

        Report.INSTANCE.debug("------ takeNewBFSStep -- Landmark");

        Set<Integer> lastFrontier = distances.incrementIterationNoAndGetPreviousFrontier();
        // This loop ensures that we can only break after completely finishing traversing the
        // level completely.
        for (int currentVertexId : lastFrontier) {

            Report.INSTANCE.debug("== Vertex in Frontier " + currentVertexId);

            SortedAdjacencyList adjList = getOutNeighbours(currentVertexId,true, direction, (short) (distances.latestIteration+1));
            if (SortedAdjacencyList.isNullOrEmpty(adjList)) {
                continue;
            }

            long vertexsCurrentDist = distances.getLatestDistance(currentVertexId);

            for (int i = 0; i < adjList.getSize(); i++) {
                // If the vertex is not the destination, has no outgoing neighbors that may help this query/direction, then no need to it to frontier!
                int neighbourVertexId = adjList.getNeighbourId(i);

                Report.INSTANCE.debug("== neighbor vertex " + neighbourVertexId + " -- ignore if part of landmarks!");

                if (!landmarks.keySet().contains(neighbourVertexId)) {
                    updateNbrsDistance(currentVertexId, vertexsCurrentDist, neighbourVertexId,
                            (long) adjList.getNeighbourWeight(i), distances.latestIteration);
                }
            }
        }
    }

    /**
     * Given a set of edges that have been updated, for each iteration i of the BFS, adds vertices
     * that should be fixed at i.
     * By default: For each diff edge (u, v), let t1,...,tk be the times u's distance was
     * updated. Then, we add v to be fixed for each iteration number t_i+1 because u may
     * have updated v's distance at t_i and v would be in the frontier at t_i+1.
     * <p>
     * <p>
     * Ignore changes to vertices in landmark!
     */
    public void addVerticesToFix(List<int[]> diffEdges) {

        Report.INSTANCE.debug("------ addVerticesToFix ");

        int edgeSource, edgeDest;
        for (int[] edge : diffEdges) {
            edgeSource = direction == Graph.Direction.FORWARD ? edge[0] : edge[1];
            edgeDest = direction == Graph.Direction.FORWARD ? edge[1] : edge[0];

            if (direction == Graph.Direction.FORWARD) {
                Report.INSTANCE.debug(edge[0] + " --> " + edge[1]);

                if (landmarks.keySet().contains(edge[0])) {
                    continue;
                }
            } else {
                Report.INSTANCE.debug(edge[0] + " <-- " + edge[1]);

                if (landmarks.keySet().contains(edge[1])) {
                    continue;
                }
            }

            for (Object object : distances.getAllDistances(edgeSource)) {

                Distances.Diff distanceIterPair = (Distances.Diff) object;
                //verticesToFix.addVToFix(edgeDest /* vToFix */,
                //        distanceIterPair.iterationNo + 1 /* iterationNo */);

                addVFORFix(edgeDest, (short) (distanceIterPair.iterationNo + 1));
            }
        }
    }

    public void preProcessing() {

        Report.INSTANCE.debug("------ preProcessing -- landmark");

        for (Landmark l : landmarks.values()) {
            l.sptFWD.executeDifferentialBFS();
            l.sptBWD.executeDifferentialBFS();
        }
        landmark_distance = getLandmarkDistance(source, destination);
    }

    /**
     * Fixes the BFS differentially.
     * This is called from ContinuousDiffBFSShortestPathPlan.execute()
     * <p>
     * This is similar to to regular execution, but it needs to update the Landmark SPTs first!
     */
    public void executeDifferentialBFS() {

        //Report.INSTANCE.debug("------ executeDifferentialBFS -- landmark");

        // Prepare a list of vertices that may impact the shortest path
        // based on added/deleted edges
        addVerticesToFixFromDiffEdges();

        // if there is no changes in SP, return!
        if (verticesToFix.isEmpty) {
            return;
        }

        // go through all iterations, and fix one step at a time
        // Stop when:
        // 1- destination found
        // 2- Frontier is empty
        // 3- last iteration is reached
        short t = 1;

        Report.INSTANCE.debug("== revisiting all iterations");
        while (t <= distances.latestIteration && t <= landmark_distance) {

            Report.INSTANCE.debug("== iteration #" + t);

            fixOneBFSStep(t);
            if (distances.isFrontierEmpty(t) || shouldStopBFSEarly(t)) {
                Report.INSTANCE.debug("** Frontier is empty or destination found");
                break;
            }
            if (t < distances.latestIteration) {
                ++t;
            } else {
                break;
            }
        }

        // clear list of vertices
        Report.INSTANCE.debug("== Clear list of vertices to fix");
        verticesToFix.clear();

        // Empty frontier or destination found?
        if (distances.isFrontierEmpty(t) || shouldStopBFSEarly(t)) {

            // change latest iteration
            if (t < distances.latestIteration) {
                distances.setLatestIterationNumber(t);
            }

            // update path when necessary!
            if (backtrack && doesPathExist() && (shortestPath.isEmpty() || didShortestPathChange)) {
                backtrack();
            } else if (!doesPathExist() && distances.isFrontierEmpty(t) && !shortestPath.isEmpty()) {
                shortestPath.clear();
            }
        } else {

            Report.INSTANCE.debug("== more BFS is needed");
            // continue BFS if more iteration needed
            continueBFS();
        }
    }

    public long getSrcDstDistance() {
        return Long.min(landmark_distance, distances.getLatestDistance(destination));
    }

    public int sizeOfLandmarkDistance() {
        int size = 0;

        for (Landmark l : landmarks.values()) {
            size += l.sptBWD.distances.size() + l.sptFWD.distances.size();
        }

        return size;
    }

    public int sizeOfDistances() {

        int query_distance_size = distances.size();
        // int ladnmark_distance_size = sizeOfLandmarkDistance();

        return query_distance_size;
    }
    public int getNumberOfVertices() {
        return distances.getVerticesWithDiff().size();
    }

    public void printDiffs() {
        PrintLandmarkInfo();
        distances.print();
    }

    public void PrintLandmarkInfo() {

        Report.INSTANCE.error("** Landmarks = " + Arrays.toString(landmarks.keySet().toArray()));

        String degrees = "";
        for (int l : landmarks.keySet()) {
            degrees += " ( " + Graph.getInstance().getVertexFWDDegree(l) + " - " +
                    Graph.getInstance().getVertexBWDDegree(l) + " ) ";
        }

        String sizes = "";
        for (Landmark l : landmarks.values()) {
            sizes += " ( " + l.sptFWD.distances.size() + " - " + l.sptBWD.distances.size() + " ) ";
        }

        Report.INSTANCE.error("** Landmarks diffs size = " + sizes);
    }

    /**
     * Runs a unidirectional BFS starting from the frontier. This method is called twice. First,
     * when the continuous query is first registered and an initial BFS from scratch runs. Second,
     * each time the differentially maintained BFS is stuck and needs to extend the existing
     * frontier.
     * <p>
     * This is called when we want to run further BFS steps to find the destination
     */
    public void continueBFS() {
        // Khaled:
        // change the order, because if frontier is empty, then shouldStopBFSEarly will Fail with Null Pointer Exception

        Report.INSTANCE.debug("------ continueBFS ");

        while (!distances.isFrontierEmpty(distances.latestIteration) &&
                !shouldStopBFSEarly(distances.latestIteration) && distances.latestIteration < landmark_distance) {
            takeNewBFSStep();
        }
        if (backtrack) {
            if (doesPathExist()) {
                backtrack();
            } else {
                // Could not find the destination.
                numTimesShortestPathWasNotFound++;
            }
        }
    }

    public class Landmark {

        NewUnidirectionalUnweightedDifferentialBFS sptFWD;
        NewUnidirectionalUnweightedDifferentialBFS sptBWD;
        int vertex_id;
        int degree;
        Graph.Direction direction;
        Graph.Direction reverseDirection;

        Landmark(int vertex_id, int degree, Graph.Direction d) {
            this.vertex_id = vertex_id;
            this.degree = degree;
            this.direction = d;
            sptFWD = new NewUnidirectionalUnweightedDifferentialBFS(queryId, vertex_id, -1, direction, false,queryType);
            if (d == Graph.Direction.FORWARD) {
                reverseDirection = Graph.Direction.BACKWARD;
            } else {
                reverseDirection = Graph.Direction.FORWARD;
            }

            sptBWD = new NewUnidirectionalUnweightedDifferentialBFS(queryId, vertex_id, -1, reverseDirection, false, queryType);

            sptFWD.continueBFS();
            sptBWD.continueBFS();
        }
    }
}
