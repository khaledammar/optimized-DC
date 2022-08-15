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
public class LandmarkUnidirectionalWeightedDifferentialBFS extends NewUnidirectionalWeightedDifferentialBFS {


    static HashMap<Integer, Landmark> landmarks = new LinkedHashMap<>(10);
    static int number_landmark;
    static int landmark_counter = 1;
    long landmark_distance = -1;
    long diffs_distance = -1;
    static int lastProcessedBatch = -1;
    public LandmarkUnidirectionalWeightedDifferentialBFS(int queryId, int source, int destination,
                                                         Graph.Direction direction, boolean backtrack, int k,
                                                         Queries queryType) {

        super(queryId+2*k, source, destination, backtrack, DropIndex.NO_DROP, queryType);
        number_landmark = k;
        this.mergeDeltaDiff();


        //Report.INSTANCE.debug("------ LandmarkUnidirectionalUnweightedDifferentialBFS - Landmark");

        if (landmarks.isEmpty()) {


            HashMap<Integer, Integer> sources = Graph.getInstance().getTopKnotNeighbours(k);
            System.out.println("** Potential Landmarks : " + Arrays.toString(sources.keySet().toArray()));


            Report.INSTANCE.debug("** Landmark is empty");
            Report.INSTANCE.debug("** Find top-K vertices, K = "+k);

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

        for (Map.Entry<Integer, Landmark> entry : landmarks.entrySet()) {
            distance[counter++] = entry.getValue().sptFWD.sizeOfDistances() + entry.getValue().sptBWD.sizeOfDistances();
        }
        return distance;
    }

    public static long getLandmarkDistance(int source, int destination) {
        long min_distance = Long.MAX_VALUE;
        long distance, srcDistance, dstDistance;
        for (Map.Entry<Integer, LandmarkUnidirectionalWeightedDifferentialBFS.Landmark> entry : landmarks.entrySet()) {

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
                && vertexsCurrentDist + neighbourWeight < distances.getNewDistance(neighbourId, currentIterationNo, true, currentVertexId)
                //&& vertexsCurrentDist + neighbourWeight < getLandmarkDistance(source,neighbourId)
                && vertexsCurrentDist + neighbourWeight < getLandmarkDistance(source,destination)
        ) {
            distances.setVertexDistance(neighbourId, currentIterationNo /* iteration no */, vertexsCurrentDist + neighbourWeight /* distance */);
        }
    }

    boolean doesPathExist() {

        if (landmark_distance == Double.MAX_VALUE) {
            return Double.MAX_VALUE != distances.getLatestDistance(destination);
        } else {
            return true;
        }
    }

    public static boolean canAddVtoFrontier(int vertexId) {
        if(landmarks.keySet().contains(vertexId))
            return false;
        else
            return true;
    }

    boolean shouldSetNewValue(int vertexId, long newVal, long oldVal){
        if (newVal < getLandmarkDistance(source,vertexId) && newVal < landmark_distance && newVal < diffs_distance)
            return super.shouldSetNewValue(vertexId,newVal,oldVal);
        else
            return false;
    }

    /**
     * Given a set of edges that have been updated, for each iteration i of the BFS, adds vertices
     * that should be fixed at i.
     * By default: For each diff edge (u, v), let t1,...,tk be the times u's distance was
     * updated. Then, we add v to be fixed for each iteration number t_i+1 because u may
     * have updated v's distance at t_i and v would be in the frontier at t_i+1.
     */
    public void addVerticesToFix(List<int[]> diffEdges) {

        int edgeSource, edgeDest;
        for (int[] edge : diffEdges) {
            edgeSource = direction == Graph.Direction.FORWARD ? edge[0] : edge[1];
            edgeDest = direction == Graph.Direction.FORWARD ? edge[1] : edge[0];

            for (Short distanceIter : distances.getAllIterations(edgeSource)) {
                //Report.INSTANCE.error("------ addVToFix v="+edgeDest+" added edge from "+edgeSource+ " with iteration:"+(distanceIter+1));
                addVFORFix(edgeDest, (short) (distanceIter + 1));
            }
        }
    }

    /**
     * BFS should stop as soon as the destination is reached.
     *
     * @see {@link NewUnidirectionalDifferentialBFS#shouldStopBFSEarly(int)}.
     */
    @Override
    public boolean shouldStopBFSEarly(short currentIterationNo) {


        diffs_distance = distances.getDistance(destination, currentIterationNo);

        Report.INSTANCE.debug("------ shouldStopBFSEarly - Landmark: iteration="+currentIterationNo+ " - minFrontier="+
                distances.minFrontierDistances[currentIterationNo].iterDistPair.distance+" - diffsDistance="+ diffs_distance + " - landmarkDistance="+landmark_distance);

        Report.INSTANCE.debug("* current iteration " + currentIterationNo);
        Report.INSTANCE.debug("* diffs_distance = " + diffs_distance);
        Report.INSTANCE.debug("* landmark_distance = " + landmark_distance);
        Report.INSTANCE.debug("* Empty Frontier? = " + distances.isFrontierEmpty(currentIterationNo));
        Report.INSTANCE.debug("* Frontier: = " + Arrays.toString(distances.getCurrentFrontier().toArray()));

        return super.shouldStopBFSEarly(currentIterationNo) ||
                distances.minFrontierDistances[currentIterationNo].iterDistPair.distance >= diffs_distance ||
                distances.minFrontierDistances[currentIterationNo].iterDistPair.distance >= landmark_distance;
    }

    public void fixVertexAndAddNewVerticesToFix(int vertexToFix, short currentFixedIter){
        if(!landmarks.keySet().contains(vertexToFix))
            super.fixVertexAndAddNewVerticesToFix(vertexToFix,currentFixedIter);
    }

    public void preProcessing(int batchNumber) {

        Report.INSTANCE.debug("------ preProcessing -- landmark");

        if(batchNumber > lastProcessedBatch){
            //System.out.println("Update Landmark queries");
            for (Landmark l : landmarks.values()) {
                l.sptFWD.executeDifferentialBFS();
                l.sptBWD.executeDifferentialBFS();
            }
            lastProcessedBatch = batchNumber;
        }

        landmark_distance = getLandmarkDistance(source, destination);
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

    public int getMaxIteration(){
        return distances.latestIteration;
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

        NewUnidirectionalWeightedDifferentialBFS sptFWD;
        NewUnidirectionalWeightedDifferentialBFS sptBWD;
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
            sptFWD = new NewUnidirectionalWeightedDifferentialBFS(landmark_query_id*2-1, vertex_id, -1, false, direction, DropIndex.NO_DROP, queryType);
            sptFWD.mergeDeltaDiff();
            sptFWD.distances.print();
            if (d == Graph.Direction.FORWARD) {
                reverseDirection = Graph.Direction.BACKWARD;
            } else {
                reverseDirection = Graph.Direction.FORWARD;
            }



            Report.INSTANCE.debug("---- Backward query");
            sptBWD = new NewUnidirectionalWeightedDifferentialBFS(landmark_query_id*2, vertex_id, -1, false, reverseDirection, DropIndex.NO_DROP,  queryType);
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
