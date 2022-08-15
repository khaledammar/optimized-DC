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
    static int number_landmark;
    static int landmark_counter = 1;
    long landmark_distance = -1;
    static int lastProcessedBatch = -1;
    public LandmarkUnidirectionalUnweightedDifferentialBFS(int queryId, int source, int destination,
                                                           Graph.Direction direction, boolean backtrack, int k,
                                                           Queries queryType) {

        super(queryId+2*k, source, destination, direction, backtrack, queryType);
        number_landmark = k;
        this.mergeDeltaDiff();

        //Report.INSTANCE.debug("------ LandmarkUnidirectionalUnweightedDifferentialBFS - Landmark");

        if (landmarks.isEmpty()) {

            Report.INSTANCE.debug("** Landmark is empty");
            Report.INSTANCE.debug("** Find top-K vertices, K = "+k);
            HashMap<Integer, Integer> sources = Graph.getInstance().getTopK(k, direction);

            Report.INSTANCE.debug("** Potential Landmarks : " + Arrays.toString(sources.keySet().toArray()));

            for (Map.Entry<Integer, Integer> entry : sources.entrySet()) {
                Report.INSTANCE.debug("Landmark : "+entry.getKey()+ " - "+entry.getValue());
                Landmark l = new Landmark(entry.getKey(), entry.getValue(), direction);
                Report.INSTANCE.debug("Adding Landmark : "+l);
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

    public int[] sizeOfLandmarkDistances(){
        int[] distance = new int[number_landmark];
        int counter = 0;

        for (Map.Entry<Integer, LandmarkUnidirectionalUnweightedDifferentialBFS.Landmark> entry : landmarks.entrySet()) {
            distance[counter++] = entry.getValue().sptFWD.sizeOfDistances() + entry.getValue().sptBWD.sizeOfDistances();
        }
        return distance;
    }

    public static long getLandmarkDistance(int source, int destination) {
        long min_distance = Long.MAX_VALUE;
        long distance, srcDistance, dstDistance;
        for (Map.Entry<Integer, Landmark> entry : landmarks.entrySet()) {

            srcDistance = entry.getValue().sptBWD.distances.getLatestDistance(source);
            dstDistance = entry.getValue().sptFWD.distances.getLatestDistance(destination);

            if(srcDistance == Long.MAX_VALUE || dstDistance == Long.MAX_VALUE)
                continue;
            else
                distance = srcDistance + dstDistance;

            if (distance < min_distance) {
                min_distance = distance;
            }
        }
        return min_distance;
    }

    // if the source is one of the landmarks, then the answer should always be available in the landmark SPT
    // we add a dummy source vertex of -1 instead so that the frontier is always empty.
    private void initFrontierAndSourceDistance() {

        if (landmarks.keySet().contains(source)) {
            distances = new Distances();
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

            if (!landmarks.keySet().contains(neighbourId)
                    && vertexsCurrentDist + 1 < distances.getNewDistance(neighbourId, currentIterationNo, true, currentVertexId)
                    && vertexsCurrentDist + 1 < getLandmarkDistance(source,neighbourId)
            ) {
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

        return super.shouldStopBFSEarly(currentIterationNo) || currentIterationNo >= landmark_distance;
    }

    public void preProcessing(int batchNumber) {

        Report.INSTANCE.debug("------ preProcessing -- landmark");
        if(batchNumber > lastProcessedBatch) {
            for (Landmark l : landmarks.values()) {
                l.sptFWD.executeDifferentialBFS();
                l.sptBWD.executeDifferentialBFS();
            }
            lastProcessedBatch = batchNumber;
        }
        landmark_distance = getLandmarkDistance(source, destination);
    }

    public static boolean canAddVtoFrontier(int vertexId) {
        if(landmarks.keySet().contains(vertexId))
            return false;
        else
            return true;
    }

    boolean shouldSetNewValue(int vertexId, long newVal, long oldVal){
        if (newVal < getLandmarkDistance(source,vertexId))
            return super.shouldSetNewValue(vertexId,newVal,oldVal);
        else
            return false;
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


    public class Landmark {

        NewUnidirectionalUnweightedDifferentialBFS sptFWD;
        NewUnidirectionalUnweightedDifferentialBFS sptBWD;
        int vertex_id;
        int degree;
        int landmark_query_id;
        Graph.Direction direction;
        Graph.Direction reverseDirection;

        Landmark(int vertex_id, int degree, Graph.Direction d) {

            Report.INSTANCE.debug("Creating Landmark : "+vertex_id+ " - "+degree);
            this.landmark_query_id = landmark_counter;
            landmark_counter +=1;
            this.vertex_id = vertex_id;
            this.degree = degree;
            this.direction = d;

            Report.INSTANCE.debug("---- Forward query");
            sptFWD = new NewUnidirectionalUnweightedDifferentialBFS(landmark_query_id*2-1, vertex_id, -1, direction, false,Queries.SPSP);
            sptFWD.mergeDeltaDiff();
            sptFWD.distances.print();
            if (d == Graph.Direction.FORWARD) {
                reverseDirection = Graph.Direction.BACKWARD;
            } else {
                reverseDirection = Graph.Direction.FORWARD;
            }



            Report.INSTANCE.debug("---- Backward query");
            sptBWD = new NewUnidirectionalUnweightedDifferentialBFS(landmark_query_id*2, vertex_id, -1, reverseDirection, false, Queries.SPSP);
            sptBWD.mergeDeltaDiff();


            Report.INSTANCE.debug("*** Continue Forward query");
            sptFWD.continueBFS();
            sptFWD.mergeDeltaDiff();
            sptFWD.distances.print();

            Report.INSTANCE.debug("*** Continue Backward query");
            sptBWD.continueBFS();
            sptBWD.mergeDeltaDiff();


            sptFWD.distances.print();
        }
    }
}
