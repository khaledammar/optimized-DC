package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.SortedAdjacencyList;
import ca.waterloo.dsg.graphflow.util.Report;
import ca.waterloo.dsg.graphflow.util.Timer;

import java.util.*;

/**
 * This class can be used to create an index of the Shortest-Path Tree (SPT) of one vertex (source).
 * Typically, this vertex is going to have a significantly large degree in comparison to the
 * remaining vertices in the graph. The idea is to create and update these kind of vertices once,
 * for all queries instead of updating them for each registered query in the system. That is why most
 * data and function members in the class is going to be static!
 */
public class LandmarkUnidirectionalWeightedBaselineBFS extends WeightedBaselineBFS {


    static HashMap<Integer, Landmark> landmarks = new LinkedHashMap<>(10);
    static long[] landmark_source_distance;
    static long totalLandmarkTime=0;
    static int number_landmark;
    static int landmark_counter = 1;
    long landmark_distance = -1;
    long computed_distance = -1;
    long diffs_distance = -1;
    static int lastProcessedBatch = -1;
    static long totalGetDistanceTime=0;

    private LandmarkUnidirectionalWeightedBaselineBFS(){
        super();
    }

    private static final LandmarkUnidirectionalWeightedBaselineBFS INSTANCE = new LandmarkUnidirectionalWeightedBaselineBFS();

    public static LandmarkUnidirectionalWeightedBaselineBFS getInstance() {
        return INSTANCE;
    }

    protected void init(int source, int destination,int k) {

        this.source = source;
        this.destination = destination;
        this.distance.clear();
        this.distance.put(this.source, 0L);
        this.frontier.clear();
        number_landmark = k;

        Timer timer = new Timer();
        if (landmarks.isEmpty() && number_landmark > 0) {
            landmark_source_distance = new long[number_landmark];
            HashMap<Integer, Integer> sources = Graph.getInstance().getTopKnotNeighbours(k);
            System.out.println("** Potential Landmarks : " + Arrays.toString(sources.keySet().toArray()));

            Report.INSTANCE.debug("** Landmark is empty");
            Report.INSTANCE.debug("** Find top-K vertices, K = "+k);
            Report.INSTANCE.debug("** Potential Landmarks : " + Arrays.toString(sources.keySet().toArray()));

            for (Map.Entry<Integer, Integer> entry : sources.entrySet()) {
                Report.INSTANCE.debug("Landmark : "+entry.getKey()+ " - "+entry.getValue());
                Landmark l = new Landmark(entry.getKey(), entry.getValue(), Graph.Direction.FORWARD);
                Report.INSTANCE.debug("Adding Landmark : "+l);
                landmarks.putIfAbsent(entry.getKey(), l);
            }

            System.out.println("LandmarkTime = "+timer.elapsedDurationString());
            //totalLandmarkTime += timer.elapsedMicros();
            //System.out.println("total LandmarkTimeInit = "+Timer.elapsedDurationMicroString(totalLandmarkTime));
            Report.INSTANCE.debug("--------------------------------------------------------------------------");
            Report.INSTANCE.debug("----------------------- Finished computing Landmarks ---------------------");
            Report.INSTANCE.debug("--------------------------------------------------------------------------");
        }
        //this.mergeDeltaDiff();
    }

    public long execute(int source, int destination, int landmark_counter) {

        Timer timer= new Timer();
        init(source, destination, landmark_counter);

        int counter = 0;
        for (Landmark l : landmarks.values()) {
            landmark_source_distance[counter++] = l.sptBWD.distances.getLatestDistance(source);
        }
        landmark_distance = getLandmarkDistance(this.source, this.destination);
        frontier.add(this.source);

        int visitiedVetices=0;
        int iterations=0;
        while (!frontier.isEmpty()) {
            iterations++;
            //System.out.println("\nFrontier size = "+frontier.size());

            for (int currVertexId : frontier) {
                visitiedVetices++;
                SortedAdjacencyList adjacencyList = Graph.INSTANCE.getForwardMergedAdjacencyList(currVertexId);
                if (SortedAdjacencyList.isNullOrEmpty((adjacencyList))) {
                    continue;
                }
                for (int i = 0; i < adjacencyList.getSize(); i++) {
                    updateNbrDistances(currVertexId, adjacencyList, i, tempFrontier);
                }
            }

            frontier.clear();

            for (Map.Entry<Integer, Long> entry : tempFrontier.entrySet()) {
                frontier.add(entry.getKey());
                distance.put(entry.getKey(), entry.getValue());
            }
            tempFrontier.clear();
        }

//        System.out.println("Visited Vertices = "+visitiedVetices+ "\tIterations = "+iterations+" Time= "+timer.elapsedMillis()+" ms");
        //System.out.println("Checking Neighbors time = "+Timer.elapsedDurationMicroString(totalGetDistanceTime));
        return getSrcDstDistance();
    }

    protected void updateNbrDistances(int currVertex, SortedAdjacencyList adjList, int nbrIndex,
                                    Map<Integer, Long> newFrontier) {

        int nbrId = adjList.neighbourIds[nbrIndex];
        long edgeWeight = (long) adjList.weights[nbrIndex];
        long nbrWeight = distance.get(currVertex) + edgeWeight;

        if(//landmarks.keySet().contains(nbrId) ||
                nbrWeight >= landmark_distance ||
                //nbrWeight >= getLandmarkDistance(source,nbrId) ||
                ( distance.get(destination)!= null && nbrWeight >= distance.get(destination) ) )
            return;
        else
            super.updateNbrDistances(currVertex,adjList,nbrIndex,newFrontier);
    }


    protected void updateNbrDistancesNew(int currVertex, SortedAdjacencyList adjList, int nbrIndex,
                                      Map<Integer, Long> newFrontier) {

        int nbrId = adjList.neighbourIds[nbrIndex];
        long edgeWeight = (long) adjList.weights[nbrIndex];
        long nbrWeight = distance.get(currVertex) + edgeWeight;
        nbrWeight = Math.min(nbrWeight,getLandmarkDistance(source,nbrId));

        if(nbrWeight >= landmark_distance || ( distance.get(destination)!= null && nbrWeight >= distance.get(destination) ) )
            return;
        else
            updateNbrDistances(nbrId,nbrWeight, newFrontier);
    }

    protected void updateNbrDistances(int nbrId,
                                      long nbrWeight, Map<Integer, Long> newFrontier) {

        if (newFrontier.containsKey(nbrId)) {
            if (nbrWeight < newFrontier.get(nbrId)) {
                newFrontier.put(nbrId, nbrWeight);
            }
            return;
        }
        if (!distance.containsKey(nbrId)) {
            newFrontier.put(nbrId, nbrWeight);
        } else if (nbrWeight < distance.get(nbrId)) {
            newFrontier.put(nbrId, nbrWeight);
        }
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
        int counter = 0;

        for (Map.Entry<Integer, LandmarkUnidirectionalWeightedBaselineBFS.Landmark> entry : landmarks.entrySet()) {

            //srcDistance = entry.getValue().sptBWD.distances.getLatestDistance(source);
            srcDistance = landmark_source_distance[counter++]; //entry.getValue().sptBWD.distances.getLatestDistance(source);
            if(srcDistance == Long.MAX_VALUE)
                continue;

            Timer timer = new Timer();
            dstDistance = entry.getValue().sptFWD.distances.getLatestDistance(destination);
            totalGetDistanceTime+=timer.elapsedMicros();
            if(dstDistance == Long.MAX_VALUE)
                continue;

            distance = srcDistance + dstDistance;

            if (distance < min_distance) {
                min_distance = distance;
            }
        }
        return min_distance;
    }


    public void preProcessing(int batchNumber) {



        if(batchNumber > lastProcessedBatch){
            Timer timer= new Timer();
            System.out.println("**** Update Landmark queries");
            for (Landmark l : landmarks.values()) {
                l.sptFWD.executeDifferentialBFS();
                l.sptBWD.executeDifferentialBFS();
            }
            lastProcessedBatch = batchNumber;

            if(batchNumber==1)
                totalLandmarkTime = 0;
            totalLandmarkTime += timer.elapsedMicros();
            System.out.println("LandmarkTime = "+timer.elapsedDurationString());
            System.out.println("total LandmarkTimePreprocess = "+Timer.elapsedMicroToMilliString(totalLandmarkTime));
            totalGetDistanceTime=0;
        }

        //Report.INSTANCE.error("------ preProcessing landmark: "+timer.elapsedMillis()+" ms");
    }


    public long getSrcDstDistance() {
        if(distance.containsKey(destination))
            return Long.min(landmark_distance, distance.get(destination));
        else
            return landmark_distance;
    }

    public int sizeOfLandmarkDistance() {
        int size = 0;

        for (Landmark l : landmarks.values()) {
            size += l.sptBWD.distances.size() + l.sptFWD.distances.size();
        }

        return size;
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
            sptFWD = new NewUnidirectionalWeightedDifferentialBFS(landmark_query_id*2-1, vertex_id, -1,
                    false, direction, NewUnidirectionalDifferentialBFS.DropIndex.NO_DROP,
                    NewUnidirectionalDifferentialBFS.Queries.SPSP);
            sptFWD.mergeDeltaDiff();
            sptFWD.distances.print();
            if (d == Graph.Direction.FORWARD) {
                reverseDirection = Graph.Direction.BACKWARD;
            } else {
                reverseDirection = Graph.Direction.FORWARD;
            }



            Report.INSTANCE.debug("---- Backward query");
            sptBWD = new NewUnidirectionalWeightedDifferentialBFS(landmark_query_id*2, vertex_id, -1,
                    false, reverseDirection, NewUnidirectionalDifferentialBFS.DropIndex.NO_DROP,
                    NewUnidirectionalDifferentialBFS.Queries.SPSP);
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
