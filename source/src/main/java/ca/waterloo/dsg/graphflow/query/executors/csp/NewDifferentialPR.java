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
public class NewDifferentialPR extends NewUnidirectionalUnweightedDifferentialBFS {
    int maxHop;

    /**
     * @see {@link NewUnidirectionalDifferentialBFS}.
     */

    public NewDifferentialPR(int queryId, int rounds, Direction direction, boolean backtrack, Queries queryType) {
        super(queryId, -1, -1, direction, backtrack, queryType);

        maxHop = rounds;
    }

    public NewDifferentialPR(int queryId, int rounds, Direction direction, boolean backtrack,
                             DropIndex dropIndex, float prob, DistancesWithDropBloom.DropType dropType, String bloomType, int minimumDegree,
                             int maxDegree, Queries queryType) {
        super(queryId, -1, -1, direction, backtrack, dropIndex, prob, dropType, bloomType, minimumDegree,
                maxDegree, queryType);

        maxHop = rounds;
    }

    /**
     * PR should stop as soon as the maximum number of hops is reached.
     *
     * @see {@link NewUnidirectionalDifferentialBFS#shouldStopBFSEarly(short)}.
     */
    @Override
    public boolean shouldStopBFSEarly(short currentIterationNo) {
        return currentIterationNo >= maxHop;
    }

    public long getSrcDstDistance() {

        /*
        System.out.println(distances.getDistance(0,(short) 0));
        System.out.println(distances.getDistance(1000,(short) 0));
        System.out.println(distances.getDistance(1234,(short) 0));
        System.out.println(distances.getDistance(146314,(short) 0));
*/
        var x = distances.getVerticesWithDiff();
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

        /*
        if (vertexsCurrentDist + neighbourWeight <
                distances.getNewDistance(neighbourId, currentIterationNo, true, currentVertexId)) {
            distances.clearAndSetOnlyVertexDistance(neighbourId, currentIterationNo ,
                    currentIterationNo );
        }
        */

        Report.INSTANCE.error("This updateNbrsDistance2 Function should not be called in PR!");
        System.exit(1);
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
        while (!distances.isFrontierEmpty(distances.latestIteration) &&
                !shouldStopBFSEarly(distances.latestIteration)) {

            distances.prepareFrontier(distances.latestIteration);
            /*
            if (Report.INSTANCE.appReportingLevel == Report.Level.DEBUG) {
                Report.INSTANCE.print("---- Iteration = " + distances.latestIteration + " #fixedVertices = " +
                        distances.setVertexDistanceCounter + " Frontier Size = " + distances.frontier.size() +
                        " - Distance " + sizeOfDistances() + " - Delta " + sizeOfDeltaDistances() +
                        " - Recalculate = " + getRecalculateNumbers());
                distances.print();
            }
             */
            takeNewBFSStep();
        }
        //We will keep the frontier, because there is only 1 PR query running

        executeDifferentialBFS();

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


    /**
     * Fixes the BFS differentially.
     * This is called from ContinuousDiffBFSShortestPathPlan.execute()
     */
    public void executeDifferentialBFS() {

        // Prepare a list of vertices that may impact the shortest path
        // based on added/deleted edges
        //verticesToFix.clear();
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
        // 2- Frontier is empty (based on threshold)
        // 3- last iteration is reached
        short t = 1;

        while (t <= distances.latestIteration) {
            fixOneBFSStep(t);

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
            Report.INSTANCE.debug("^^ continueBFS for more iterations ");
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

        // If the difference between PR values is less than threshold (1K) then do not fix our neighbours.
        if (distances.samePR(vertexNewDistance,vertexOldDistance))
            return;

        SortedAdjacencyList outComingAdjList = getOutNeighbours(vertexToFix, true, direction, (short) (currentFixedIter+1));
        List<Short> vertexIterations = distances.getAllIterations(vertexToFix);



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

        if (!distances.samePR(oldDistanceAtFixedIter, newValue) && !distances.samePR(distanceAtPreviousIter, newValue)) {
            distances.setVertexDistance(vertexToFix, currentFixedIter, newValue);
            fixSelfIfNecessary(vertexToFix, currentFixedIter);
            fixSelfInFutureIterations(vertexToFix, currentFixedIter);
            // Fix out neighbours
            fixOutNeighbours(vertexToFix, currentFixedIter, oldDistanceAtFixedIter, newValue);
        }
    }


    public void takeNewBFSStep() {

        Set<Integer> lastFrontier = distances.incrementIterationNoAndGetPreviousFrontier();
        // This loop ensures that we can only break after completely finishing traversing the level.


        for (int currentVertexId : lastFrontier) {
            // For every vertex in Frontier, compute PR again and add its out neighbours to frontier too!
                fixVertexAndAddNewVerticesToFix(currentVertexId,(short) (distances.latestIteration));
        }
    }

}
