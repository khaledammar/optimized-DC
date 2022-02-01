package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.Graph.Direction;
import ca.waterloo.dsg.graphflow.graph.Graph.GraphVersion;
import ca.waterloo.dsg.graphflow.graph.SortedAdjacencyList;
import ca.waterloo.dsg.graphflow.util.Report;

import java.util.*;

/**
 * Base class for different implementations of a unidirectional differential BFS.
 */
public abstract class NewUnidirectionalDifferentialBFS implements DifferentialBFS {
    /**
     *
     */
    public enum DropIndex {
        NO_DROP,
        BLOOM,
        HASH_TABLE
    }

    /**
     * Supported Queries:
     * SPSP:
     * KHOP:
     * Q1: A*
     * Q2: A.B*
     * Q5: A.B*.C
     * Q7: A.B.C*
     * Q11: A1.A2.A3. .... Ak
     */
    public enum Queries {
        Q1,
        Q2,
        Q5,
        Q7,
        Q11,
        SPSP,
        KHOP,
        NOT_SUPPORTED
    }

    public static VerticesToFix verticesToFix;
    // query id
    protected int queryId;
    protected Queries queryType;
    // The source vertex.
    protected int source;
    // The destination vertex.
    protected int destination;
    // Distances different vertices took at different BFS iterations.
    protected Distances distances;
    // Direction of the BFS.
    protected Direction direction;
    // boolean that triggers backtracking
    protected boolean didShortestPathChange;
    // stores that actual shortest path
    protected ShortestPath shortestPath;
    // Whether we should backtrack or not. This is used to measure
    // how much time is spent backtracking vs fixing the BFS.
    boolean backtrack;

    ;
    int numTimesShortestPathWasNotFound = 0;

    ;

    /**
     * @param source      ID of the source vertex.
     * @param destination ID of the destination vertex.
     * @param direction   direction of the BFS.
     */
    public NewUnidirectionalDifferentialBFS(int queryId, int source, int destination, Direction direction,
                                            boolean backtrack, Queries queryType) {

        //Report.INSTANCE
        //        .debug("------ Initialize NewUnidirectionalDifferentialBFS query " + source + " -> " + direction);
        this.queryType = queryType;
        this.queryId = queryId;
        this.source = source;
        this.destination = destination;
        this.direction = direction;
        this.backtrack = backtrack;
        this.verticesToFix = new VerticesToFix();
        this.shortestPath = new ShortestPath(source, destination);
        this.didShortestPathChange = false;

        initFrontierAndSourceDistance(queryId);
    }

    // dummy function required by interface and used by Landmark
    public void preProcessing() {
    }

    public void printDiffs() {
        distances.print();
    }

    public int getQueryId() {
        return queryId;
    }

    /**
     * This function is designed to copy diffs from the initial diff query (0) to this query.
     * It will also take care of dropping as necessary.
     *
     * @param initDiff
     */
    public void copyDiffs(DifferentialBFS initDiff) {
        distances.copyDiffs(((NewUnidirectionalDifferentialBFS) initDiff).distances);
    }

    public void printStats() {

        if (backtrack) {
            shortestPath.toStringKhaled();
        }
        distances.printStats();

    }

    public void printDiffs(Report.Level l) { //distances.print(l);
        distances.printStats();
    }

    public int sizeOfDistances() {
        return distances.size();
    }

    public int getNumberOfVertices() {
        return distances.getVerticesWithDiff().size();
    }

    public int sizeOfDeltaDistances() {
        return distances.deltaSize();
    }

    public Map<Integer,Integer> getRecalculateStats(){
        return distances.recalculateState;
    }
    public int getRecalculateNumbers() {
        return distances.recalculateNumber;
    }

    public void initRecalculateNumbers() {
        //Report.INSTANCE.debug("---1----- initRecalculateNumbers : " + distances.recalculateNumber);
        distances.recalculateNumber = 0;
        distances.recalculateState = new HashMap<>(1);
    }

    public int minimumSizeOfDistances() {
        return distances.numberOfVerticesWithDiff();
    }


    private void initFrontierAndSourceDistance(int queryId) {
        distances = new Distances(queryId, source, destination, direction, queryType);
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

        /*
        Report.INSTANCE.debug("------ continueBFS ");
        if (Report.INSTANCE.appReportingLevel == Report.Level.DEBUG) {
            Report.INSTANCE.print("---- Iteration = " + distances.latestIteration + " #fixedVertices = " +
                    distances.setVertexDistanceCounter + " Frontier Size = " + distances.frontier.size() +
                    " - Distance " + sizeOfDistances() + " - Delta " + sizeOfDeltaDistances() + " - Recalculate = " +
                    getRecalculateNumbers());
        }

         */
        while (!distances.isFrontierEmpty(distances.latestIteration) &&
                !shouldStopBFSEarly(distances.latestIteration)) {

            distances.prepareFrontier(distances.latestIteration);
            takeNewBFSStep();
            /*
            if (Report.INSTANCE.appReportingLevel == Report.Level.DEBUG) {
                distances.print();
                Report.INSTANCE.print("---- Iteration = " + distances.latestIteration + " #fixedVertices = " +
                        distances.setVertexDistanceCounter + " Frontier Size = " + distances.frontier.size() +
                        " - Distance " + sizeOfDistances() + " - Delta " + sizeOfDeltaDistances() +
                        " - Recalculate = " + getRecalculateNumbers());
            }

             */
        }
        /*
        Report.INSTANCE.debug("Empty Frontier? " + distances.isFrontierEmpty(distances.latestIteration));
        if (!distances.isFrontierEmpty(distances.latestIteration)) {
            Report.INSTANCE.debug("Stop Early? " + shouldStopBFSEarly(distances.latestIteration));
        }

         */

        //System.out.println("DONE with ALL steps");

        /* we do not want to keep the frontier because its size might be huge*/
        //distances.frontier=null;
        if (distances.frontierReady == true) {
            distances.frontier.clear();
            distances.previousFrontier.clear();
            distances.frontierReady = false;
            //Report.INSTANCE.debug("Frontier = " + distances.frontier.size() + " ---- previous = " +
            //        distances.previousFrontier.size());
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

    public static SortedAdjacencyList staticFilterNeighbours(SortedAdjacencyList neighbours, short iteration){
        return neighbours;
    }

    public SortedAdjacencyList filterNeighbours(SortedAdjacencyList neighbours, short iteration){
        return staticFilterNeighbours(neighbours,iteration);
    }

    public static SortedAdjacencyList staticGetOutNeighbours(int vertex, boolean merged, Direction direction, short iteration){
        SortedAdjacencyList neighbours;

        if (merged)
            neighbours = direction == Direction.FORWARD ? Graph.INSTANCE.getForwardMergedAdjacencyList(vertex) :
                    Graph.INSTANCE.getBackwardMergedAdjacencyList(vertex);
        else
            neighbours = direction == Direction.FORWARD ? Graph.INSTANCE.getForwardUnMergedAdjacencyList(vertex) :
                    Graph.INSTANCE.getBackwardUnMergedAdjacencyList(vertex);

        return staticFilterNeighbours(neighbours, iteration);
    }

    public SortedAdjacencyList getOutNeighbours(int vertex, boolean merged, Direction direction, short iteration){
        return staticGetOutNeighbours(vertex,merged,direction,iteration);
    }

    public static SortedAdjacencyList staticGetInNeighbours(int vertex, boolean merged, Direction direction, short iteration){
        SortedAdjacencyList neighbours;
        if (merged)
            neighbours =  direction == Direction.FORWARD ? Graph.INSTANCE.getBackwardMergedAdjacencyList(vertex) :
                    Graph.INSTANCE.getForwardMergedAdjacencyList(vertex);
        else
            neighbours =  direction == Direction.FORWARD ? Graph.INSTANCE.getBackwardUnMergedAdjacencyList(vertex) :
                    Graph.INSTANCE.getForwardUnMergedAdjacencyList(vertex);

        return staticFilterNeighbours(neighbours, iteration);
    }

    public SortedAdjacencyList getInNeighbours(int vertex, boolean merged, Direction direction, short iteration){
        return staticGetInNeighbours(vertex,merged,direction,iteration);
    }

    boolean doesPathExist() {
        return Long.MAX_VALUE != distances.getLatestDistance(destination);
    }

    /**
     * This function is used to filter neighbours as we traverse them.
     * It is useful for RPQ queries
     *
     * @param edgeType
     * @param iterationNo
     * @return
     */
    boolean goodNeighbour(short edgeType, short iterationNo){
        return staticGoodNeighbour(edgeType,iterationNo);
    }

    static boolean staticGoodNeighbour(short edgeType, short iterationNo){
        return true;
    }

    public void takeNewBFSStep() {

        //Report.INSTANCE.debug("------ takeNewBFSStep @ iter = " + distances.latestIteration);
        distances.print();
        //Report.INSTANCE.debug("------------------------- ");

        Set<Integer> lastFrontier = distances.incrementIterationNoAndGetPreviousFrontier();
        // This loop ensures that we can only break after completely finishing traversing the
        // level completely.
        //System.out.println("Frontier size = "+lastFrontier.size());
        for (int currentVertexId : lastFrontier) {

            SortedAdjacencyList adjList = getOutNeighbours(currentVertexId, true, direction,distances.latestIteration);

            if (SortedAdjacencyList.isNullOrEmpty(adjList)) {
                continue;
            }

            long currentDistance =
                    distances.getDistance(currentVertexId, (short) (distances.latestIteration - 1), true);

            for (int i = 0; i < adjList.getSize(); i++) {
                // if this neighbour should not be considered for this iteration because of the edge type, ignore it!
                if(!goodNeighbour(adjList.getEdgeType(i),distances.latestIteration))
                    continue;

                // If the vertex is not the destination, has no outgoing neighbors that may help this query/direction, then no need to it to frontier!
                int neighbourVertexId = adjList.getNeighbourId(i);

/*

This should be removed or revisited!
For disconnected queries, it is possible that one of the leaf vertices is going to connect us to the destination, probably after adding an edge.
When we do not add them to distances, they cannot use their own distance to reach the destination!

                if (    neighbourVertexId != destination &&
                        ( direction == Direction.FORWARD && Graph.INSTANCE.getVertexFWDDegree(neighbourVertexId) == 0 ) ||
                        (direction == Direction.BACKWARD && Graph.INSTANCE.getVertexBWDDegree(neighbourVertexId) == 0 )) {

                    System.out.println("== neighbor is destination or has no potential neighbors");


                    continue;
                }

                else
                    */
                // we assume no negative edges, so source has a distance of zero and cannot go any less!
                // Also one vertex cannot update itself again (all self-loops are positive anyway)
                if (neighbourVertexId != source && currentVertexId != neighbourVertexId) {
                    updateNbrsDistance(currentVertexId, currentDistance, neighbourVertexId,
                            (long) adjList.getNeighbourWeight(i), distances.latestIteration);
                }
            }
        }
    }

    /**
     * Fixes the BFS differentially.
     * This is called from ContinuousDiffBFSShortestPathPlan.execute()
     */
    public void executeDifferentialBFS() {

        //Report.INSTANCE.debug("------ executeDifferentialBFS ");

        // Merge Diffs from previous batches
        //System.out.println("size-beginning = "+ distances.deltaDiffs.size());
        //distances.mergeDeltaDiffs();
        //System.out.println("size-beginning2 = "+ distances.deltaDiffs.size());
        //System.out.println("Delta Diff = "+distances.deltaDiffs.size());
        // Prepare a list of vertices that may impact the shortest path
        // based on added/deleted edges
        addVerticesToFixFromDiffEdges();

        // if there is no changes in SP, return!
        if (verticesToFix.isEmpty) {
            //System.out.println("size-end = "+ distances.deltaDiffs.size());
            distances.mergeDeltaDiffs();
            //System.out.println("size-end2 = "+ distances.deltaDiffs.size());
            return;
        }

        // go through all iterations, and fix one step at a time
        // Stop when:
        // 1- destination found
        // 2- Frontier is empty
        // 3- last iteration is reached
        short t = 1;

        //Report.INSTANCE.debug("== revisiting all iterations");
        while (t <= distances.latestIteration) {

            //Report.INSTANCE.debug("== iteration #" + t);

            fixOneBFSStep(t);
            distances.print();
            /**
             * This check should be removed!
             * It is possible that the destination has been found but we need to fix more itertions to get the shortest distance

            if (distances.isFrontierEmpty(t) || shouldStopBFSEarly(t)) {
                Report.INSTANCE.debug("** Frontier is empty or destination found");
                break;
            }
            */

            if (t < distances.latestIteration) {
                ++t;
            } else {
                break;
            }
        }

        // clear list of vertices
        //Report.INSTANCE.debug("== Clear list of vertices to fix");
        verticesToFix.clear();

        // Empty frontier or destination found?
        if (distances.isFrontierEmpty(t) || shouldStopBFSEarly(t)) {

            // change latest iteration
            if (t < distances.latestIteration) {
                distances.setLatestIterationNumber(t);
            }

            // update path when necessary!
            //            if (backtrack && doesPathExist() && (shortestPath.isEmpty() || didShortestPathChange)) {
            //                backtrack();
            //            } else if (!doesPathExist() && distances.isFrontierEmpty(t) && !shortestPath.isEmpty()) {
            //                shortestPath.clear();
            //            }
        } else {

            //Report.INSTANCE.debug("== more BFS is needed");
            // continue BFS if more iteration needed
            continueBFS();
        }

        //System.out.println("size-end = "+ distances.deltaDiffs.size());
        distances.mergeDeltaDiffs();
        //System.out.println("size-end2 = "+ distances.deltaDiffs.size());
    }

    public void addVerticesToFixFromDiffEdges() {

        Report.INSTANCE.debug("------ addVerticesToFixFromDiffEdges ");

        //Report.INSTANCE.debug("== PLUS ");
        addVerticesToFix(Graph.INSTANCE.getDiffEdges(GraphVersion.DIFF_PLUS));

        //Report.INSTANCE.debug("== MINUS ");
        addVerticesToFix(Graph.INSTANCE.getDiffEdges(GraphVersion.DIFF_MINUS));
    }

    public void fixOneBFSStep(short t) {

        //Report.INSTANCE.debug("------ fixOneBFSStep %d", t);

        // ShortestPath is only used if we are doing backtracking!
        // System.out.println("== "+shortestPath.toString());

        //Report.INSTANCE.debug("* Number of vertices to fix in this iteration = "+verticesToFix.iterVPairsList.get(t).size());

        if (verticesToFix.iterVPairsList.size() > t && !verticesToFix.getItemFromIterVPairsList(t).isEmpty()) {

            for (int vertexToFixAtT : verticesToFix.getItemFromIterVPairsList(t)) {

                //Report.INSTANCE.debug("** fix vertex %d and look for neighbors", vertexToFixAtT);

                // this is only used if we are doing backtracking!
                //                if (shortestPath.contains(vertexToFixAtT)) {
                //                    didShortestPathChange = true;
                //                }

                fixVertexAndAddNewVerticesToFix(vertexToFixAtT, t);
            }
        }
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
            edgeSource = direction == Direction.FORWARD ? edge[0] : edge[1];
            edgeDest = direction == Direction.FORWARD ? edge[1] : edge[0];

            for (Short distanceIter : distances.getAllIterations(edgeSource)) {
                //Report.INSTANCE.error("------ addVToFix v="+edgeDest+" added edge from "+edgeSource+ " with iteration:"+(distanceIter+1));
                addVFORFix(edgeDest, (short) (distanceIter + 1));
            }
        }
    }


    public void addVFORFix(int vertexForFix, short iterationForFix) {

        if (DistancesWithDropBloom.debug(vertexForFix)) {
            Report.INSTANCE.error(" ---- Add " + vertexForFix + " TO FIX @ " + iterationForFix);
        }

        if (vertexForFix == source) {
            return;
        }

        if (null != verticesToFix.getItemFromIterVPairsList(iterationForFix)) {
            if (!verticesToFix.getItemFromIterVPairsList(iterationForFix).contains(vertexForFix)) {
                verticesToFix.addVToFix(vertexForFix /* vToFix */, iterationForFix /* iterationNo */);


                if (Report.INSTANCE.appReportingLevel == Report.Level.DEBUG) {
                    distances.countVertexFix(vertexForFix);
                }
            }
        }
    }

    /**
     * Called at the end of each iteration of BFS to decide whether the BFS should stop or continue
     * another iteration. Each implementations should implement its own stopping criterion.
     *
     * @param currentIterationNo the iteration number that is being executed either as we continue
     *                           the bfs or differentially.
     * @return whether the BFS should stop or continue one more iteration.
     */
    public abstract boolean shouldStopBFSEarly(short currentIterationNo);

    /**
     * @param currentVertexId    ID of the current vertex that is being visited.
     * @param currentVsAdjList   current vertex's adjancecy list.
     * @param nbrIndex           the index of the neighborVertex in current vertex's adjacency list
     *                           index.
     * @param currentIterationNo iteration number in which the update is happening.
     */
    public abstract void updateNbrsDistance(int currentVertexId, SortedAdjacencyList currentVsAdjList, int nbrIndex,
                                            short currentIterationNo);

    /**
     * Similar to the above function but avoids sending the complete adjacency list
     *
     * @param currentVertexId
     * @param neighbourId
     * @param currentIterationNo
     */
    public abstract void updateNbrsDistance(int currentVertexId, long currentDistance, int neighbourId,
                                            long neighbourWeight, short currentIterationNo);


    public long getSrcDstDistance() {
        return distances.getLatestDistance(destination);
    }

    /**
     * Returns the latest distance of each vertex in the graph as a
     * double array.
     */
    public long[] getDistancesAsArray() {
        long[] temp = new long[Graph.INSTANCE.getHighestVertexId() + 1];
        for (int i = 0; i <= Graph.INSTANCE.getHighestVertexId(); i++) {
            temp[i] = distances.getLatestDistance(i);
        }
        return temp;
    }

    public abstract void backtrack();

    /**
     * If needed, fixes a vertex's distance at the given iteration t and possibly adds
     * other vertices to be fixed to {@link #verticesToFix} data structure.
     *
     * @param vertexToFix      ID of the vertex to fix.
     * @param currentFixedIter iteration number to fix the given vertex.
     */
    public void fixVertexAndAddNewVerticesToFix(int vertexToFix, short currentFixedIter) {
        /*
        if (DistancesWithDropBloom.debug(vertexToFix)) {
            Report.INSTANCE.error("------ fixVertexAndAddNewVerticesToFix - weighted - v= " + vertexToFix + " i= " +
                    currentFixedIter);
        }*/

        if (vertexToFix == this.source) {
            // We assume there are no negative weight cycles. So the source's
            // distance can never be updated.
            return;
        }

        long oldDistanceAtFixedIter =
                distances.getDistance(vertexToFix, currentFixedIter, false /* do not consider current batch*/);
        long distanceAtPreviousIter = distances.getDistance(vertexToFix, (short) (currentFixedIter - 1), true);
        long newValue = distances.recalculateDistance(vertexToFix, currentFixedIter, true);

/*        if (DistancesWithDropBloom.debug(vertexToFix)) {
            Report.INSTANCE.error("** old distance = " + oldDistanceAtFixedIter + " previous Dist = " +
                    distanceAtPreviousIter + " newValue = " + newValue);
        }
*/
        if (oldDistanceAtFixedIter != newValue) {

/*            if (DistancesWithDropBloom.debug(vertexToFix)) {
                // get new diffs
                var v_diffs = distances.getMergedDiffs(vertexToFix);  //distances.vIterDistPairMap.get(vertexToFix);
                String v_diff_string = "[]";
                if (null != v_diffs) {
                    v_diff_string = Arrays.toString(v_diffs);
                }
                Report.INSTANCE.error("**** FIX v= " + vertexToFix + " newValue = " + newValue + " while old= " +
                        oldDistanceAtFixedIter + " @ " + currentFixedIter + " Diff= " + v_diff_string);
            }
*/
            // Update self if in-neighbours were updated after currentIter
            // This is important because if the vertex became unreachable or worse, we need to revisit it again
            // in iterations after its in-neighbours were updated

            if (newValue < distanceAtPreviousIter) {
                // set the new distance
                distances.setVertexDistance(vertexToFix, currentFixedIter, newValue);
            }
            fixSelfIfNecessary(vertexToFix, currentFixedIter);
            fixSelfInFutureIterations(vertexToFix, currentFixedIter);
            // Fix out neighbours
            fixOutNeighbours(vertexToFix, currentFixedIter, oldDistanceAtFixedIter, newValue);
        }

        // if this iteration does not enhance the distance of this vertex
        // remove this iteration if exist
        if (newValue >= distanceAtPreviousIter) {
            // this might mean that vertex is now unreachable
            distances.clearVertexDistanceAtT(vertexToFix, currentFixedIter, newValue);
        }
    }

    public void fixOutNeighbours(int vertexToFix, short currentFixedIter, long vertexOldDistance,
                                 long vertexNewDistance) {
        SortedAdjacencyList outComingAdjList = getOutNeighbours(vertexToFix, true, direction, (short) (currentFixedIter+1));
/*
        if (DistancesWithDropBloom.debug(vertexToFix)) {
            Report.INSTANCE.error("**** Add out neighbours to be fixed after " + currentFixedIter);
        }
*/
        List<Short> vertexIterations = distances.getAllIterations(vertexToFix);

        for (int i = 0; i < outComingAdjList.getSize(); i++) {
            // if this neighbour should not be considered for this iteration because of the edge type, ignore it!
            if(!goodNeighbour(outComingAdjList.getEdgeType(i),(short) (currentFixedIter+1)))
                continue;

            int out = outComingAdjList.getNeighbourId(i);
            long outDistance = distances.getNewDistance(out, (short) (currentFixedIter + 1), false, -1);

            if (outDistance >= vertexOldDistance || outDistance >= vertexNewDistance) {
                addVFORFix(out, (short) (currentFixedIter + 1));
            }

            for (Short diffIter : vertexIterations) {
                if (diffIter > currentFixedIter) {
                    addVFORFix(out, (short) (diffIter + 1));
                }
            }
        }
    }

    public void fixSelfInFutureIterations(int vertexToFix, short currentFixedIter) {
        // check if in-neighbours were updated after current iteration
        // if yes, then we should add current vertex to be fixed too.

/*        if (DistancesWithDropBloom.debug(vertexToFix)) {
            Report.INSTANCE.error("**** Add self to fix for future iterations");
        }
*/
        for (Short iter : distances.getAllIterations(vertexToFix)) {
            if (iter > currentFixedIter) {
                //Report.INSTANCE.error("------ addVToFix v="+vertexToFix+" because it was fixed on a previous iteration :"+currentFixedIter);
                addVFORFix(vertexToFix, iter);
            }
        }
    }


    public void fixSelfIfNecessary(int vertexToFix, short currentFixedIter) {
        // check if in-neighbours were updated after current iteration
        // if yes, then we should add current vertex to be fixed too.

/*        if (DistancesWithDropBloom.debug(vertexToFix)) {
            Report.INSTANCE.error("**** Add self to fix based on in-nbrs ");
        }
*/
        Set<Short> toBeFixedIterations = new HashSet<>();

        SortedAdjacencyList incomingAdjList = getInNeighbours(vertexToFix,true, direction, (short) (-1));
/*        if (DistancesWithDropBloom.debug(vertexToFix)) {
            System.out.println(
                    "**** For " + vertexToFix + " in-neighbours are: " + Arrays.toString(incomingAdjList.neighbourIds) +
                            " but size is " + incomingAdjList.getSize());
        }
*/
        for (int in_nbr = 0; in_nbr < incomingAdjList.getSize(); in_nbr++) {
            // if this neighbour should not be considered for this iteration because of the edge type, ignore it!
            if(!goodNeighbour(incomingAdjList.getEdgeType(in_nbr),(short) (-1)))
                continue;

/*            if (DistancesWithDropBloom.debug(vertexToFix)) {
                System.out.println("** in-neighbour: " + incomingAdjList.neighbourIds[in_nbr]);
            }
*/
            if (incomingAdjList.neighbourIds[in_nbr] < 0) {
                continue;
            }

/*            if (DistancesWithDropBloom.debug(vertexToFix)) {
                System.out.println("** has iterations " +
                        Arrays.toString(distances.getAllIterations(incomingAdjList.neighbourIds[in_nbr]).toArray()));
            }
*/            for (Short iter : distances.getAllIterations(incomingAdjList.neighbourIds[in_nbr])) {
                if (iter >= currentFixedIter) {
                    //Report.INSTANCE.error("------ addVToFix v="+vertexToFix+" because its incoming neighbor was changed in the future org_v "+incomingAdjList.neighbourIds[in_nbr]+ " with iteration:"+iter);
                    toBeFixedIterations.add((short) (iter + 1));
                }
            }
        }

        for (Short iter : toBeFixedIterations) {
            addVFORFix(vertexToFix, iter);
        }
    }
}


