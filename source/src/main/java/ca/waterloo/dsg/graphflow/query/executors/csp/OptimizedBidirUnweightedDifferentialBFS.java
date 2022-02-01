package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph.Direction;
import ca.waterloo.dsg.graphflow.util.ArrayUtils;
import ca.waterloo.dsg.graphflow.util.Report;
import ca.waterloo.dsg.graphflow.util.VisibleForTesting;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of bidirectional differential BFS. Maintains two BFS's differentially.
 * One of them goes in the forward direction and the other in the backward direction.
 * <p>
 * Keeps the shortest path in two parts. Part of it will be stored in the forward BFS's
 * {@link ShortestPath} object. The other part will be stored in the backward BFS's
 * {@link ShortestPath} object.
 */
public class OptimizedBidirUnweightedDifferentialBFS implements DifferentialBFS {

    @VisibleForTesting
    NewUnidirectionalUnweightedDifferentialBFS fwDiffBFS;
    @VisibleForTesting
    NewUnidirectionalUnweightedDifferentialBFS bwDiffBFS;
    @VisibleForTesting
    Set<Integer> intersection;
    DirectionIterationPair[] iterationDirections;
    // Whether we should backtrack or not
    private boolean backtrack;

    public OptimizedBidirUnweightedDifferentialBFS(int queryId, int source, int destination, boolean backtrack, NewUnidirectionalDifferentialBFS.Queries queryType) {

        //    System.out.println("\nNewBidirUnweightedDifferentialBFS - constructor");

        this.backtrack = backtrack;
        this.fwDiffBFS = new NewUnidirectionalUnweightedDifferentialBFS(queryId, source, destination, Direction.FORWARD,
                backtrack, queryType);
        this.bwDiffBFS =
                new NewUnidirectionalUnweightedDifferentialBFS(queryId, destination, source, Direction.BACKWARD,
                        backtrack, queryType);
        this.intersection = new HashSet<>();

        this.iterationDirections = new DirectionIterationPair[100];
    }

    public int getQueryId() {
        return -1;
    }

    public void copyDiffs(DifferentialBFS initDiff) {
        return;
    }

    public void mergeDeltaDiff() {
        fwDiffBFS.mergeDeltaDiff();
        bwDiffBFS.mergeDeltaDiff();
    }

    // dummy function required by interface and used by Landmark
    public void preProcessing() {
    }


    public void printStats() {
        fwDiffBFS.printStats();
        bwDiffBFS.printStats();
    }


    public void printDiffs() {
        fwDiffBFS.printDiffs();
        bwDiffBFS.printDiffs();
    }

    public void printDiffs(Report.Level l) {
        fwDiffBFS.printDiffs(l);
        bwDiffBFS.printDiffs(l);
    }

    void addIterationDirection(Direction d) {
        int nextIterationNumber = fwDiffBFS.distances.latestIteration + bwDiffBFS.distances.latestIteration + 1;

        short localIterationNumber =
                (d == Direction.FORWARD) ? fwDiffBFS.distances.latestIteration : bwDiffBFS.distances.latestIteration;
        localIterationNumber++;
        // resize if necessary
        if (nextIterationNumber >= iterationDirections.length) {
            iterationDirections = (DirectionIterationPair[]) ArrayUtils
                    .resizeIfNecessary(iterationDirections, nextIterationNumber + 10);
        }

        iterationDirections[nextIterationNumber] = new DirectionIterationPair(d, localIterationNumber);

        Report.INSTANCE.debug("******** Added global iteration # " + nextIterationNumber + " with direction " + d +
                " and local iteration is " + localIterationNumber);
    }

    /**
     * @see {@link DifferentialBFS#continueBFS()}.
     */
    public void continueBFS() {
/*
        if (Report.INSTANCE.appReportingLevel == Report.Level.DEBUG) {
            Report.INSTANCE.debug("\n------- Continue BFS   BiDir");
            Report.INSTANCE
                    .debug("** FWD frontier=" + Arrays.toString(fwDiffBFS.distances.getCurrentFrontier().toArray()));
            Report.INSTANCE
                    .debug("** BWD frontier=" + Arrays.toString(bwDiffBFS.distances.getCurrentFrontier().toArray()));
            Report.INSTANCE.debug("** FWD frontier=" + Arrays.toString(intersection.toArray()));
        }

 */

        while (!fwDiffBFS.distances.isFrontierEmpty(fwDiffBFS.distances.latestIteration) &&
                !bwDiffBFS.distances.isFrontierEmpty(bwDiffBFS.distances.latestIteration) && intersection.isEmpty()) {

            NewUnidirectionalUnweightedDifferentialBFS diffBFSToContinue;
            NewUnidirectionalUnweightedDifferentialBFS otherDiffBFS;

            // Existing policy, alternate between FWD and BWD frontiers!
            if (getDirection() == Direction.FORWARD) {
                addIterationDirection(Direction.FORWARD);
                diffBFSToContinue = fwDiffBFS;
                otherDiffBFS = bwDiffBFS;
            } else {
                addIterationDirection(Direction.BACKWARD);
                diffBFSToContinue = bwDiffBFS;
                otherDiffBFS = fwDiffBFS;
            }

            Report.INSTANCE.debug("Next step is going to be " + getDirection());

            diffBFSToContinue.takeNewBFSStep();
            computeIntersection(diffBFSToContinue, otherDiffBFS);
        }

        if (!intersection.isEmpty()) {
            backtrack();
        }
    }

    // NEW policy, pick smallest frontier!
    public DirectionIterationPair getDirection(int iteration) {

        if (iterationDirections[iteration] == null) {
            Report.INSTANCE.debug("No direction stored for this iteration yet --> " + iteration);

            // add another direction
            addIterationDirection(getDirection());
        }

        return iterationDirections[iteration];
    }

/*
    // Existing policy, alternate between FWD and BWD frontiers!
    public Direction getDirection(int iteration){
        if (iteration%2 == 1)
            return Direction.FORWARD;
        else
            return Direction.BACKWARD;
    }

    public Direction getDirection(){
        if (fwDiffBFS.distances.latestIteration <= bwDiffBFS.distances.latestIteration)
            return Direction.FORWARD;
        else
            return Direction.BACKWARD;
    }
*/

    public Direction getDirection() {

        int fwd_forcast = fwDiffBFS.distances.getNextFrontierSize();
        int bwd_forcast = bwDiffBFS.distances.getNextFrontierSize();

        //if (fwDiffBFS.distances.latestIteration==0)
        //    fwd_forcast = fwDiffBFS.distances.g

        Report.INSTANCE.debug(" FWD_direction= " + fwDiffBFS.distances.direction + " vs BWD_direction= " +
                bwDiffBFS.distances.direction);
        Report.INSTANCE.debug(" FWD_forcast= " + fwd_forcast + " vs BWD_forcast= " + bwd_forcast);

        if (fwd_forcast < bwd_forcast) {
            return Direction.FORWARD;
        } else {
            return Direction.BACKWARD;
        }
    }

    private void computeIntersection(NewUnidirectionalUnweightedDifferentialBFS diffBFS,
                                     NewUnidirectionalUnweightedDifferentialBFS otherDiffBFS) {

        //    System.out.println("\nCompute Intersection");

        intersection.clear();

        // it is better to start by the smaller set when we do intersection
        int size = diffBFS.distances.getCurrentFrintierSize();
        int otherSize = otherDiffBFS.distances.getCurrentFrintierSize();

        Set<Integer> currentFrontier;
        Distances otherDistances;

        if (size < otherSize) {
            currentFrontier = diffBFS.distances.getCurrentFrontier();
            otherDistances = otherDiffBFS.distances;
        } else {
            currentFrontier = otherDiffBFS.distances.getCurrentFrontier();
            otherDistances = diffBFS.distances;
        }

        for (int vertexInFrontier : currentFrontier) {
            if (Double.MAX_VALUE != otherDistances.getLatestDistance(vertexInFrontier)) {
                intersection.add(vertexInFrontier);
            }
        }
/*
        if (Report.INSTANCE.appReportingLevel == Report.Level.DEBUG) {
            Report.INSTANCE.debug("*** Intersections are " + Arrays.toString(intersection.toArray()));
        }

 */
    }

    String getDirectionName(Direction d) {
        return (d == Direction.FORWARD) ? "Forward" : "Backward";
    }

    /**
     * @see {@link DifferentialBFS#executeDifferentialBFS()}.
     */
    public void executeDifferentialBFS() {

        //    System.out.println("\nExecute Differential");

        fwDiffBFS.addVerticesToFixFromDiffEdges();
        bwDiffBFS.addVerticesToFixFromDiffEdges();
        if (fwDiffBFS.verticesToFix.isEmpty && bwDiffBFS.verticesToFix.isEmpty) {
            return;
        }

        //   System.out.println("There are vertices to Fix");

        int totalTimeToFix = fwDiffBFS.distances.latestIteration + bwDiffBFS.distances.latestIteration;

        //   System.out.println("totalTimeToFix = "+totalTimeToFix);

        int t = 1;
        NewUnidirectionalUnweightedDifferentialBFS diffBFSToFix = null;
        NewUnidirectionalUnweightedDifferentialBFS otherDiffBFS = null;
        short timeToFixForDiffBFS = -1;
        short lastTimeFixedForOtherDiffBFS = -1;
        boolean foundIntersectionWhileFixing = false;

        short FWDlastFixedIteration = 0;
        short BWDlastFixedIteration = 0;

        Report.INSTANCE.debug("** #iterations to be checked is " + totalTimeToFix);
        while (t <= totalTimeToFix) {

            //    myDirection = (t % 2 == 1) ? "Forward" : "Backward";
            /*
            if (Report.INSTANCE.appReportingLevel == Report.Level.DEBUG) {
                Report.INSTANCE.debug("===> iteration " + t + " direction = " + getDirectionName(getDirection(t).dir));
                Report.INSTANCE
                        .debug("FWD frontier : " + Arrays.toString(fwDiffBFS.distances.getCurrentFrontier().toArray()));
                Report.INSTANCE
                        .debug("BWD frontier : " + Arrays.toString(bwDiffBFS.distances.getCurrentFrontier().toArray()));
            }

             */

            // Existing policy, alternate between FWD and BWD frontiers!

            DirectionIterationPair myDirection = getDirection(t);
            timeToFixForDiffBFS = myDirection.localIteration;

            FWDlastFixedIteration += (myDirection.dir == Direction.FORWARD) ? 1 : 0;
            BWDlastFixedIteration += (myDirection.dir == Direction.FORWARD) ? 0 : 1;
            diffBFSToFix = (myDirection.dir == Direction.FORWARD) ? fwDiffBFS : bwDiffBFS;
            otherDiffBFS = (myDirection.dir == Direction.FORWARD) ? bwDiffBFS : fwDiffBFS;

            // TODO: Check there is no issues here
            // previous code!
            //lastTimeFixedForOtherDiffBFS = (t % 2 == 1) ? (t+1)/2 - 1: t/2;
            lastTimeFixedForOtherDiffBFS =
                    (myDirection.dir == Direction.FORWARD) ? BWDlastFixedIteration : FWDlastFixedIteration;

            Report.INSTANCE
                    .debug("FWD lastFixed= " + FWDlastFixedIteration + " , BWD lastFixed= " + BWDlastFixedIteration);

            if (myDirection.dir == Direction.FORWARD) {
                assert timeToFixForDiffBFS == FWDlastFixedIteration : "local iteration and my count should match";
            } else {
                assert timeToFixForDiffBFS == BWDlastFixedIteration : "local iteration and my count should match";
            }

            diffBFSToFix.fixOneBFSStep(timeToFixForDiffBFS);

            //    System.out.println("fixOneBFSStep - "+timeToFixForDiffBFS);

            // diffBFSToFix.distances.isFrontierEmpty(timeToFixForDiffBFS) ||
            if (t < totalTimeToFix && (diffBFSToFix.distances.isFrontierEmpty(timeToFixForDiffBFS) ||
                    (foundIntersectionWhileFixing =
                            foundIntersectionAtTime(diffBFSToFix, otherDiffBFS, timeToFixForDiffBFS,
                                    lastTimeFixedForOtherDiffBFS)))) {
                break;
            } else if (timeToFixForDiffBFS == diffBFSToFix.distances.latestIteration) {

                //       System.out.println("Update intersection : "+diffBFSToFix+ " " + otherDiffBFS);

                updateIntersection(diffBFSToFix, otherDiffBFS);
                /*
                if (Report.INSTANCE.appReportingLevel == Report.Level.DEBUG) {
                    Report.INSTANCE.debug("** @ " + t + " , Intersection = " + Arrays.toString(intersection.toArray()));
                }

                 */
            }
            t++;
        }
/*
        if (Report.INSTANCE.appReportingLevel == Report.Level.DEBUG) {
            Report.INSTANCE.debug("********************************************************");
            Report.INSTANCE.debug("********************************************************");
            Report.INSTANCE.debug("********************************************************");
            Report.INSTANCE.debug("********************************************************");

            Report.INSTANCE.debug("***********  Fixes are done what about continue BFS ***********");
            Report.INSTANCE
                    .debug("FWD frontier : " + Arrays.toString(fwDiffBFS.distances.getCurrentFrontier().toArray()));
            Report.INSTANCE
                    .debug("BWD frontier : " + Arrays.toString(bwDiffBFS.distances.getCurrentFrontier().toArray()));

            printDiffs(Report.Level.DEBUG);
            Report.INSTANCE.debug("********************************************************");
            Report.INSTANCE.debug("********************************************************");
            Report.INSTANCE.debug("********************************************************");
            Report.INSTANCE.debug("********************************************************");
        }

 */

        boolean intersectionIsNotEmpty = t > totalTimeToFix ? !intersection.isEmpty() : foundIntersectionWhileFixing;

        Report.INSTANCE.debug("** Intersection = " + Arrays.toString(intersection.toArray()));

        fwDiffBFS.verticesToFix.clear();
        bwDiffBFS.verticesToFix.clear();
        // If either the last frontier is empty or there is a non-empty intersection, we don't have to continue BFS.
        if (diffBFSToFix.distances.isFrontierEmpty(timeToFixForDiffBFS) || intersectionIsNotEmpty) {

            Report.INSTANCE.debug("** We stopped earlier than the previous version of the BFS -- t=" + t + " vs " +
                    (fwDiffBFS.distances.latestIteration + bwDiffBFS.distances.latestIteration));
            // We stopped earlier than the previous version of the BFS.
            if (t < fwDiffBFS.distances.latestIteration + bwDiffBFS.distances.latestIteration) {
                diffBFSToFix.distances.setLatestIterationNumber(timeToFixForDiffBFS);
                otherDiffBFS.distances.setLatestIterationNumber(lastTimeFixedForOtherDiffBFS);
                if (intersectionIsNotEmpty) {
                    computeIntersection(diffBFSToFix, otherDiffBFS);
                    fwDiffBFS.didShortestPathChange = true;
                    assert !intersection.isEmpty() : "If we stopped earlier during differential fixing and " +
                            "intersectionIsNotEmpty is true, then the intersection has to be non-null.";
                }
            }
            if (diffBFSToFix.distances.isFrontierEmpty(timeToFixForDiffBFS)) {
                fwDiffBFS.shortestPath.clear();
                bwDiffBFS.shortestPath.clear();
            } else if (backtrack && (fwDiffBFS.didShortestPathChange || bwDiffBFS.didShortestPathChange)) {
                backtrack();
            }
        } else {

            Report.INSTANCE.debug("** We continue BFS");
            intersection.clear();
            continueBFS();
        }

        // System.out.println("\n\n\n");
    }

    private void updateIntersection(NewUnidirectionalUnweightedDifferentialBFS diffBFSToFix,
                                    NewUnidirectionalUnweightedDifferentialBFS otherDiffBFS) {
/*
        if (Report.INSTANCE.appReportingLevel == Report.Level.DEBUG) {
            Report.INSTANCE.debug("---------- updateIntersection");
            Report.INSTANCE.debug("* diff = " + Arrays.toString(diffBFSToFix.distances.getCurrentFrontier().toArray()));
            Report.INSTANCE
                    .debug("* other = " + Arrays.toString(otherDiffBFS.distances.getCurrentFrontier().toArray()));
            Report.INSTANCE.debug("* original intersection = " + Arrays.toString(intersection.toArray()));
        }

 */
        // System.out.println("\nupdateIntersection");

        short latestIteration = diffBFSToFix.distances.latestIteration;
        for (int vertexFixed : diffBFSToFix.verticesToFix.getItemFromIterVPairsList(latestIteration)) {
            if (intersection.contains(vertexFixed)) {
                if (Double.MAX_VALUE == diffBFSToFix.distances.getDistance(vertexFixed, latestIteration)) {

                    //            System.out.println("Add vertex "+vertexFixed+" to the intersection!");

                    intersection.remove(vertexFixed);
                    // This is a hack but it avoids this class to maintain its own didShortestPathChange field.
                    fwDiffBFS.didShortestPathChange = true;
                }
            } else {
                if ((Double.MAX_VALUE != diffBFSToFix.distances.getLatestDistance(vertexFixed)) &&
                        (Double.MAX_VALUE != otherDiffBFS.distances.getLatestDistance(vertexFixed))) {

                    //            System.out.println("Add vertex "+vertexFixed+" to the intersection!");

                    intersection.add(vertexFixed);
                    // This is a hack but it avoids this class to maintain its own didShortestPathChange field.
                    fwDiffBFS.didShortestPathChange = true;
                }
            }
        }

        Report.INSTANCE.debug("* updated intersection = " + Arrays.toString(intersection.toArray()));
    }

    private void backtrack() {
        fwDiffBFS.didShortestPathChange = false;
        bwDiffBFS.didShortestPathChange = false;
        fwDiffBFS.shortestPath.clear();
        bwDiffBFS.shortestPath.clear();
        if (intersection.isEmpty()) {
            return;
        }
        fwDiffBFS.backtrack(intersection);
        bwDiffBFS.backtrack(intersection);
    }

    private boolean foundIntersectionAtTime(NewUnidirectionalUnweightedDifferentialBFS diffBFSToFix,
                                            NewUnidirectionalUnweightedDifferentialBFS otherDiffBFS,
                                            short timeToFixForDiffBFS, short lastTimeFixedForOtherDiffBFS) {

        //System.out.println("\nfoundIntersectionAtTime: timeToFixForDiffBFS= "+timeToFixForDiffBFS+
        //        " - lastTimeFixedForOtherDiffBFS="+lastTimeFixedForOtherDiffBFS);

        for (int vertexFixed : diffBFSToFix.verticesToFix.getItemFromIterVPairsList(timeToFixForDiffBFS)) {

            //    System.out.println("Vertex to fix = "+vertexFixed+ " // distance = " + diffBFSToFix.distances.getDistance(vertexFixed, timeToFixForDiffBFS) );
            //    System.out.println("Vertex to fix = "+vertexFixed+ " // distance = " + otherDiffBFS.distances.getDistance(vertexFixed, lastTimeFixedForOtherDiffBFS) );

            if (Double.MAX_VALUE != diffBFSToFix.distances.getDistance(vertexFixed, timeToFixForDiffBFS) &&
                    Double.MAX_VALUE != otherDiffBFS.distances.getDistance(vertexFixed, lastTimeFixedForOtherDiffBFS)) {

                //        System.out.println("Return True");
                return true;
            }
        }
        //System.out.println("Return False");
        return false;
    }

    @Override
    public int sizeOfDistances() {

        //fwDiffBFS.distances.print();
        //bwDiffBFS.distances.print();

        return fwDiffBFS.distances.size() + bwDiffBFS.distances.size();
    }
    public int getNumberOfVertices() {
        return fwDiffBFS.distances.getVerticesWithDiff().size() + bwDiffBFS.distances.getVerticesWithDiff().size();
    }

    @Override
    public int minimumSizeOfDistances() {

        //fwDiffBFS.distances.print();
        //bwDiffBFS.distances.print();

        return fwDiffBFS.distances.numberOfVerticesWithDiff() + bwDiffBFS.distances.numberOfVerticesWithDiff();
    }

    @Override
    public long getSrcDstDistance() {

        //System.out.println("Size of intersection = "+intersection.size());
        //System.out.println("Current distances = "+ (fwDiffBFS.distances.latestIteration + bwDiffBFS.distances.latestIteration) );
        if (intersection.isEmpty()) {
            return Long.MAX_VALUE;
        }

        // This is wrong!
        // Instead of picking the latest iteration, we should find the min (total iteration of each vertex in the intersection)
        //return fwDiffBFS.distances.latestIteration + bwDiffBFS.distances.latestIteration;

        long distance = Long.MAX_VALUE;
        for (Integer v : intersection) {
            long fwd_distance = fwDiffBFS.distances.getLatestDistance(v);
            long bwd_distance = bwDiffBFS.distances.getLatestDistance(v);
            if (fwd_distance + bwd_distance < distance) {
                distance = fwd_distance + bwd_distance;
            }
        }

        return distance;
    }

    public Map<Integer,Integer> getRecalculateStats () {return fwDiffBFS.getRecalculateStats();}
    public int getRecalculateNumbers() {
        return fwDiffBFS.getRecalculateNumbers() + bwDiffBFS.getRecalculateNumbers();
    }

    public void initRecalculateNumbers() {
        fwDiffBFS.initRecalculateNumbers();
        bwDiffBFS.initRecalculateNumbers();
    }

    /**
     * Represents a vertex and iteration number and a distance pair.
     */
    static class DirectionIterationPair {
        Direction dir;
        short localIteration;

        public DirectionIterationPair(Direction d, short i) {
            this.dir = d;
            this.localIteration = i;
        }
    }
}
