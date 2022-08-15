package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph.Direction;
import ca.waterloo.dsg.graphflow.util.Report;
import ca.waterloo.dsg.graphflow.util.VisibleForTesting;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of bidirectional differential BFS. Maintains two BFS's differentially.
 * One of them goes in the forward direction and the other in the backward direction.
 * <p>
 * Keeps the shortest path in two parts. Part of it will be stored in the forward BFS's
 * {@link ShortestPath} object. The other part will be stored in the backward BFS's
 * {@link ShortestPath} object.
 */
public class NewBidirUnweightedDifferentialBFS implements DifferentialBFS {

    @VisibleForTesting
    NewUnidirectionalUnweightedDifferentialBFSlocalDiff fwDiffBFS;
    @VisibleForTesting
    NewUnidirectionalUnweightedDifferentialBFSlocalDiff bwDiffBFS;
    @VisibleForTesting
    Set<Integer> intersection;
    // Whether we should backtrack or not
    private boolean backtrack;

    public NewBidirUnweightedDifferentialBFS(int queryId, int source, int destination, boolean backtrack, NewUnidirectionalDifferentialBFS.Queries queryType) {

        //    System.out.println("\nNewBidirUnweightedDifferentialBFS - constructor");

        this.backtrack = backtrack;
        this.fwDiffBFS =
                new NewUnidirectionalUnweightedDifferentialBFSlocalDiff(queryId, source, destination, Direction.FORWARD,
                        backtrack, queryType);
        this.bwDiffBFS = new NewUnidirectionalUnweightedDifferentialBFSlocalDiff(queryId, destination, source,
                Direction.BACKWARD, backtrack, queryType);
        this.intersection = new HashSet<>();
    }

    public void printDiffs() {
        fwDiffBFS.printDiffs();
        bwDiffBFS.printDiffs();
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

    public void printStats() {
        fwDiffBFS.printStats();
        bwDiffBFS.printStats();
    }

    // dummy function required by interface and used by Landmark
    public void preProcessing() {
    }

    public void printDiffs(Report.Level l) {
        fwDiffBFS.printDiffs(l);
        bwDiffBFS.printDiffs(l);
    }

    /**
     * @see {@link DifferentialBFS#continueBFS()}.
     */
    public void continueBFS() {

        Report.INSTANCE.debug("\n------- Continue BFS");

        while (!fwDiffBFS.distances.isFrontierEmpty(fwDiffBFS.distances.latestIteration) &&
                !bwDiffBFS.distances.isFrontierEmpty(bwDiffBFS.distances.latestIteration) && intersection.isEmpty()) {

            NewUnidirectionalUnweightedDifferentialBFSlocalDiff diffBFSToContinue;

            // Existing policy, alternate between FWD and BWD frontiers!
            if (fwDiffBFS.distances.latestIteration <= bwDiffBFS.distances.latestIteration) {
                diffBFSToContinue = fwDiffBFS;
            } else {
                diffBFSToContinue = bwDiffBFS;
            }

            diffBFSToContinue.takeNewBFSStep();
            computeIntersection(fwDiffBFS, bwDiffBFS);
        }

        if (!intersection.isEmpty()) {
            backtrack();
        }
    }

    private void computeIntersection(NewUnidirectionalUnweightedDifferentialBFSlocalDiff diffBFS,
                                     NewUnidirectionalUnweightedDifferentialBFSlocalDiff otherDiffBFS) {

        //    System.out.println("\nCompute Intersection");

        intersection.clear();
        Set<Integer> currentFrontier = diffBFS.distances.getCurrentFrontier();
        Distances otherDistances = otherDiffBFS.distances;
        for (int vertexInFrontier : currentFrontier) {
            if (Double.MAX_VALUE != otherDistances.getLatestDistance(vertexInFrontier)) {
                intersection.add(vertexInFrontier);
            }
        }
    }

    /**
     * @see {@link DifferentialBFS#executeDifferentialBFS()}.
     */
    public void executeDifferentialBFS() {

        Report.INSTANCE.debug("\nExecute Differential");

        fwDiffBFS.addVerticesToFixFromDiffEdges();
        bwDiffBFS.addVerticesToFixFromDiffEdges();
        if (fwDiffBFS.verticesToFix.isEmpty && bwDiffBFS.verticesToFix.isEmpty) {
            return;
        }

        //   System.out.println("There are vertices to Fix");

        int totalTimeToFix = fwDiffBFS.distances.latestIteration + bwDiffBFS.distances.latestIteration;

        //   System.out.println("totalTimeToFix = "+totalTimeToFix);

        int t = 1;
        NewUnidirectionalUnweightedDifferentialBFSlocalDiff diffBFSToFix = null;
        NewUnidirectionalUnweightedDifferentialBFSlocalDiff otherDiffBFS = null;
        short timeToFixForDiffBFS = -1;
        short lastTimeFixedForOtherDiffBFS = -1;
        boolean foundIntersectionWhileFixing = false;

        String myDirection = "";
        while (t <= totalTimeToFix) {

            //    myDirection = (t % 2 == 1) ? "Forward" : "Backward";
            //    System.out.println("===> Time "+t+" "+myDirection);

            // Existing policy, alternate between FWD and BWD frontiers!
            diffBFSToFix = (t % 2 == 1) ? fwDiffBFS : bwDiffBFS;
            otherDiffBFS = (t % 2 == 1) ? bwDiffBFS : fwDiffBFS;
            timeToFixForDiffBFS = (t % 2 == 1) ? (short) ((t + 1) / 2) : (short) (t / 2);
            lastTimeFixedForOtherDiffBFS = (t % 2 == 1) ? (short) ((t + 1) / 2 - 1) : (short) (t / 2);

            diffBFSToFix.fixOneBFSStep(timeToFixForDiffBFS);

            //    System.out.println("fixOneBFSStep - "+timeToFixForDiffBFS);

            // diffBFSToFix.distances.isFrontierEmpty(timeToFixForDiffBFS) ||
            if (t < totalTimeToFix && (diffBFSToFix.distances.isFrontierEmpty(timeToFixForDiffBFS) ||
                    (foundIntersectionWhileFixing =
                            foundIntersectionAtTime(diffBFSToFix, otherDiffBFS, timeToFixForDiffBFS,
                                    lastTimeFixedForOtherDiffBFS)))) {
        /*
                    System.out.println("BREAK the loop - WHY?");
                    System.out.println("--- empty frontier? "+ diffBFSToFix.distances.isFrontierEmpty(timeToFixForDiffBFS));
                    System.out.println("--- Found intersection while fixing? "+ foundIntersectionAtTime(diffBFSToFix, otherDiffBFS,
                            timeToFixForDiffBFS, lastTimeFixedForOtherDiffBFS));
                    System.out.println("--- ");
                    System.out.println("--- ");
        */
                break;
            } else if (timeToFixForDiffBFS == diffBFSToFix.distances.latestIteration) {

                //       System.out.println("Update intersection : "+diffBFSToFix+ " " + otherDiffBFS);
                updateIntersection(diffBFSToFix, otherDiffBFS);
            }
            t++;
        }

        boolean intersectionIsNotEmpty = t > totalTimeToFix ? !intersection.isEmpty() : foundIntersectionWhileFixing;
        fwDiffBFS.verticesToFix.clear();
        bwDiffBFS.verticesToFix.clear();
        // If either the last frontier is empty or there is a non-empty intersection, we don't have to continue BFS.
        if (diffBFSToFix.distances.isFrontierEmpty(timeToFixForDiffBFS) || intersectionIsNotEmpty) {
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
            intersection.clear();
            continueBFS();
        }

        // System.out.println("\n\n\n");
    }

    private void updateIntersection(NewUnidirectionalUnweightedDifferentialBFSlocalDiff diffBFSToFix,
                                    NewUnidirectionalUnweightedDifferentialBFSlocalDiff otherDiffBFS) {

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

    private boolean foundIntersectionAtTime(NewUnidirectionalUnweightedDifferentialBFSlocalDiff diffBFSToFix,
                                            NewUnidirectionalUnweightedDifferentialBFSlocalDiff otherDiffBFS,
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


    @Override
    public int minimumSizeOfDistances() {

        //fwDiffBFS.distances.print();
        //bwDiffBFS.distances.print();

        return fwDiffBFS.distances.numberOfVerticesWithDiff() + bwDiffBFS.distances.numberOfVerticesWithDiff();
    }


    @Override
    public double getSrcDstDistance() {

        //System.out.println("Size of intersection = "+intersection.size());
        //System.out.println("Current distances = "+ (fwDiffBFS.distances.latestIteration + bwDiffBFS.distances.latestIteration) );
        if (intersection.isEmpty()) {
            return Double.MAX_VALUE;
        }
        return fwDiffBFS.distances.latestIteration + bwDiffBFS.distances.latestIteration;
    }


    public int getRecalculateNumbers() {
        return fwDiffBFS.getRecalculateNumbers() + bwDiffBFS.getRecalculateNumbers();
    }

    public void initRecalculateNumbers() {
        fwDiffBFS.initRecalculateNumbers();
        bwDiffBFS.initRecalculateNumbers();
    }
}
