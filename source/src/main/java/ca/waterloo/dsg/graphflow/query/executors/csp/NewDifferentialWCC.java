package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph.Direction;
import ca.waterloo.dsg.graphflow.graph.SortedAdjacencyList;
import ca.waterloo.dsg.graphflow.util.Report;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An implementation of Unidirectional unweighted differential BFS. The class takes
 * as input a direction in which to do the BFS.
 */
public class NewDifferentialWCC extends NewUnidirectionalUnweightedDifferentialBFS {

    /**
     * @see {@link NewUnidirectionalDifferentialBFS}.
     */

    public NewDifferentialWCC(int queryId, int rounds, Direction direction, boolean backtrack, Queries queryType) {
        super(queryId, -1, -1, direction, backtrack, queryType);

    }

    public NewDifferentialWCC(int queryId, int rounds, Direction direction, boolean backtrack,
                              DropIndex dropIndex, float prob, DistancesWithDropBloom.DropType dropType, String bloomType, int minimumDegree,
                              int maxDegree, Queries queryType) {
        super(queryId, -1, -1, direction, backtrack, dropIndex, prob, dropType, bloomType, minimumDegree,
                maxDegree, queryType);
    }

    /**
     * PR should stop as soon as the maximum number of hops is reached.
     *
     * @see {@link NewUnidirectionalDifferentialBFS#shouldStopBFSEarly(short)}.
     */
    @Override
    public boolean shouldStopBFSEarly(short currentIterationNo) {return false; }

    public long getSrcDstDistance() {
        return distances.getVerticesWithDiff().size();
    }

    public void backtrack() { }

    public void backtrack(Set<Integer> verticesToStartFrom) { }

    @Override
    /**
     * We can update the out-neighbour PR value every time each vertex change its PR, but this is very inefficient.
     * Vertices need to wait for all neighbours to finish its computation in previous iterations anyway,
     * before computing its PR value
     *
     */
    public void updateNbrsDistance(int currentVertexId, SortedAdjacencyList currentVsAdjList, int neighborIdIndex,
                                   short currentIterationNo) {
        Report.INSTANCE.error("This updateNbrsDistance1 Function should not be called in PR!");
        System.exit(1);
    }


    @Override
    public void updateNbrsDistance(int currentVertexId, long vertexsCurrentDist, int neighbourId,
                                   long currentNbrEdgeWeight, short currentIterationNo) {

        if (vertexsCurrentDist <
                distances.getNewDistance(neighbourId, currentIterationNo, true, currentVertexId)) {
            addVFORFix(neighbourId,(short) (currentIterationNo+1));
        }
    }

    /**
     * Runs a unidirectional IFE starting from the frontier. This method is called twice. First,
     * when the continuous query is first registered and an initial IFE from scratch runs. Second,
     * each time the differentially maintained IFE is stuck and needs to extend the existing
     * frontier.
     * <p>
     * This is called when we want to run further IFE steps
     */
    public void continueBFS() {

        //System.out.println("Starting a typical WCC query");

        while (!distances.isFrontierEmpty(distances.latestIteration) &&
                !shouldStopBFSEarly(distances.latestIteration)) {


            distances.prepareFrontier(distances.latestIteration);
            //System.out.println("distances.latestIteration = "+distances.latestIteration+ " --- Frontier size = "+distances.frontier.size());
            //System.out.println("Vertex 5813826 is in Frontier for iteration "+distances.latestIteration+" ? "+distances.frontier.contains(5813826));
            takeNewBFSStep();
        }

        //System.out.println("Done with typical WCC query");

        /*
        System.out.println("Vertices to fix size after processing iterations : "+verticesToFix.iterVPairsList.get(0).size()+ " , "+
                verticesToFix.iterVPairsList.get(1).size()+ " , "+
                verticesToFix.iterVPairsList.get(2).size()+ " , "+
                verticesToFix.iterVPairsList.get(3).size()+ " , "+
                verticesToFix.iterVPairsList.get(4).size()+ " , ");

         */

        executeDifferentialBFS();
        verticesToFix.clear();

        //We will keep the frontier, because there is only 1 PR query running

        /*
        //distances.frontier=null;
        if (distances.frontierReady == true) {
            distances.frontier.clear();
            distances.previousFrontier.clear();
            distances.frontierReady = false;
            //Report.INSTANCE.debug("Frontier = " + distances.frontier.size() + " ---- previous = " +
            //        distances.previousFrontier.size());
        }

         */
    }

    @Override
    public void addVerticesToFix(List<int[]> diffEdges) {

        int edgeSource, edgeDest;
        for (int[] edge : diffEdges) {
            edgeSource = direction == Direction.FORWARD ? edge[0] : edge[1];
            edgeDest = direction == Direction.FORWARD ? edge[1] : edge[0];

            for (Short distanceIter : distances.getAllIterations(edgeSource)) {
                //Report.INSTANCE.error("------ addVToFix v="+edgeDest+" added edge from "+edgeSource+ " with iteration:"+(distanceIter+1));
                addVFORFix(edgeDest, (short) (distanceIter + 1));
            }
            for (Short distanceIter : distances.getAllIterations(edgeDest)) {
                //Report.INSTANCE.error("------ addVToFix v="+edgeSource+" added edge from "+edgeDest+ " with iteration:"+(distanceIter+1));
                addVFORFix(edgeSource, (short) (distanceIter + 1));
            }
        }
    }
    /**
     * Fixes the BFS differentially.
     * This is called from ContinuousDiffBFSShortestPathPlan.execute()
     */
    public void executeDifferentialBFS() {
        // Prepare a list of vertices that may impact the shortest path
        // based on added/deleted edges
        /*
        System.out.println("Vertices to fix size Before adding vertices to fix : "+verticesToFix.iterVPairsList.get(0).size()+ " , "+
                verticesToFix.iterVPairsList.get(1).size()+ " , "+
                verticesToFix.iterVPairsList.get(2).size()+ " , "+
                verticesToFix.iterVPairsList.get(3).size()+ " , "+
                verticesToFix.iterVPairsList.get(4).size()+ " , ");

         */
        addVerticesToFixFromDiffEdges();

        /*
        System.out.println("Vertices to fix size After adding vertices to fix : "+verticesToFix.iterVPairsList.get(0).size()+ " , "+
                verticesToFix.iterVPairsList.get(1).size()+ " , "+
                verticesToFix.iterVPairsList.get(2).size()+ " , "+
                verticesToFix.iterVPairsList.get(3).size()+ " , "+
                verticesToFix.iterVPairsList.get(4).size()+ " , ");

         */

        // if there is no changes in SP, return!
        if (verticesToFix.isEmpty) {
            distances.mergeDeltaDiffs();
            return;
        }

        // go through all iterations, and fix one step at a time
        // Stop when:
        // 2- Frontier is empty (based on threshold)
        // 3- last iteration is reached
        short t = 1;


        while (t <= distances.latestIteration) {
            //System.out.println("Fixing step "+t + " out of "+distances.latestIteration);
            fixOneBFSStep(t);
            distances.print();

            if (t < distances.latestIteration) {
                ++t;
            } else {
                break;
            }
        }

        // clear list of vertices
        verticesToFix.clear();

        // Empty frontier or stop early?
        if (distances.isFrontierEmpty(t) || shouldStopBFSEarly(t)) {

            // change latest iteration if we finish early
            if (t < distances.latestIteration) {
                distances.setLatestIterationNumber(t);
            }
        } else {
            // continue BFS if more iteration needed
            continueBFS();
        }

        distances.mergeDeltaDiffs();
    }

    public void fixOneBFSStep(short t) {
        // loop over vertices to be fixed in this iteration and fix them one by one
        if (verticesToFix.iterVPairsList.size() > t && !verticesToFix.getItemFromIterVPairsList(t).isEmpty()) {
            for (int vertexToFixAtT : verticesToFix.getItemFromIterVPairsList(t)) {
                fixVertexAndAddNewVerticesToFix(vertexToFixAtT, t);
            }
        }
        verticesToFix.clear(t);
    }

    public void fixOutNeighbours(int vertexToFix, short currentFixedIter, long vertexOldDistance,
                                 long vertexNewDistance) {


        List<Short> vertexIterations = distances.getAllIterations(vertexToFix);

        SortedAdjacencyList outComingAdjList = getOutNeighbours(vertexToFix, true, direction, (short) (currentFixedIter+1));
        for (int i = 0; i < outComingAdjList.getSize(); i++) {

            int out = outComingAdjList.getNeighbourId(i);
            addVFORFix(out, (short) (currentFixedIter + 1));
            distances.addVtoFrontier(out);

            for (Short diffIter : vertexIterations) {
                if (diffIter > currentFixedIter) {
                    addVFORFix(out, (short) (diffIter + 1));
                }
            }
        }

        // we want to look at in and out neighbours
        outComingAdjList = getInNeighbours(vertexToFix, true, direction, (short) (currentFixedIter+1));
        for (int i = 0; i < outComingAdjList.getSize(); i++) {

            int out = outComingAdjList.getNeighbourId(i);
            addVFORFix(out, (short) (currentFixedIter + 1));
            distances.addVtoFrontier(out);

            for (Short diffIter : vertexIterations) {
                if (diffIter > currentFixedIter) {
                    addVFORFix(out, (short) (diffIter + 1));
                }
            }
        }
    }



    public void fixSelfInFutureIterations(int vertexToFix, short currentFixedIter) {
        // Almost no change to BFS logic
        // check if in-neighbours were updated after current iteration
        // if yes, then we should add current vertex to be fixed too.

        for (Short iter : distances.getAllIterations(vertexToFix)) {
            if (iter > currentFixedIter) {
                //Report.INSTANCE.error("------ addVToFix v="+vertexToFix+" because it was fixed on a previous iteration :"+currentFixedIter);
                addVFORFix(vertexToFix, iter);
            }
        }
    }


    public void fixSelfIfNecessary(int vertexToFix, short currentFixedIter) {
        // Almost no change to BFS logic

        // check if in-neighbours were updated after current iteration
        // if yes, then we should add current vertex to be fixed too.

        Set<Short> toBeFixedIterations = new HashSet<>();

        SortedAdjacencyList incomingAdjList = getInNeighbours(vertexToFix,true, direction, (short) (-1));
        for (int in_nbr = 0; in_nbr < incomingAdjList.getSize(); in_nbr++) {
            if (incomingAdjList.neighbourIds[in_nbr] < 0) {
                continue;
            }

            for (Short iter : distances.getAllIterations(incomingAdjList.neighbourIds[in_nbr])) {
                if (iter >= currentFixedIter) {
                    //Report.INSTANCE.error("------ addVToFix v="+vertexToFix+" because its incoming neighbor was changed in the future org_v "+incomingAdjList.neighbourIds[in_nbr]+ " with iteration:"+iter);
                    toBeFixedIterations.add((short) (iter + 1));
                }
            }
        }


        incomingAdjList = getOutNeighbours(vertexToFix,true, direction, (short) (-1));
        for (int in_nbr = 0; in_nbr < incomingAdjList.getSize(); in_nbr++) {
            if (incomingAdjList.neighbourIds[in_nbr] < 0) {
                continue;
            }

            for (Short iter : distances.getAllIterations(incomingAdjList.neighbourIds[in_nbr])) {
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

    /**
     * If needed, fixes a vertex's distance at the given iteration t and possibly adds
     * other vertices to be fixed to {@link #verticesToFix} data structure.
     *
     * @param vertexToFix      ID of the vertex to fix.
     * @param currentFixedIter iteration number to fix the given vertex.
     */
    public void fixVertexAndAddNewVerticesToFix(int vertexToFix, short currentFixedIter) {

        long oldDistanceAtFixedIter =
                distances.getDistance(vertexToFix, currentFixedIter, false /* do not consider current batch*/);
        long distanceAtPreviousIter = distances.getDistance(vertexToFix, (short) (currentFixedIter - 1), true);
        long newValue = distances.recalculateDistance(vertexToFix, currentFixedIter, true);


        if (oldDistanceAtFixedIter != newValue) {

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

    public void takeNewBFSStep() {

        Set<Integer> lastFrontier = distances.incrementIterationNoAndGetPreviousFrontier();
        // This loop ensures that we can only break after completely finishing traversing the level.

        //System.out.println("Visiting "+lastFrontier.size()+" vertices  @ "+distances.latestIteration);
        for (int currentVertexId : lastFrontier) {
            // For every vertex in Frontier, compute PR again and add its out neighbours to frontier too!
            //if(currentVertexId == 5813826 && distances.latestIteration==2)
            //    System.out.println("Time to check!");
            fixVertexAndAddNewVerticesToFix(currentVertexId,(short) (distances.latestIteration));
            /*
            long currentDistance = distances.getDistance(currentVertexId, (short) (distances.latestIteration - 1), true);

            SortedAdjacencyList adjList = getOutNeighbours(currentVertexId, true, direction,distances.latestIteration);
            for (int i = 0; i < adjList.getSize(); i++) {
                int neighbourVertexId = adjList.getNeighbourId(i);
                if (currentVertexId != neighbourVertexId)
                    updateNbrsDistance(currentVertexId, currentDistance, neighbourVertexId,
                            (long) adjList.getNeighbourWeight(i), distances.latestIteration);

            }

            adjList = getInNeighbours(currentVertexId, true, direction,distances.latestIteration);
            for (int i = 0; i < adjList.getSize(); i++) {
                int neighbourVertexId = adjList.getNeighbourId(i);
                if (currentVertexId != neighbourVertexId)
                    updateNbrsDistance(currentVertexId, currentDistance, neighbourVertexId,
                            (long) adjList.getNeighbourWeight(i), distances.latestIteration);
            }*/
        }
    }
}
