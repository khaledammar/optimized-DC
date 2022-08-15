package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.util.Report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a data structure that keeps track of the list of vertices to differentially fix at
 * different iterations.
 */
public class VerticesToFix {
    ArrayList<Set<Integer>> iterVPairsList;
    // This is only needed for one Weighted Algorithm - LastDiffOnly
    ArrayList<Set<Integer>> iterVPairsList_toBeDeleted;
    // Whether or not there are any vertices to fix in any iteration.
    boolean isEmpty = true;

    /**
     * Default constructor.
     */
    public VerticesToFix() {
        this.iterVPairsList = new ArrayList<>(100);
        for (int i = 0; i < 100; ++i) {
            this.iterVPairsList.add(new HashSet<>());
        }

        this.iterVPairsList_toBeDeleted = new ArrayList<>(0);
    }

    public Set<Integer> getItemFromIterVPairsList(int iter) {
        // check size first
        if (iterVPairsList.size() <= iter) {

            System.out.println("Old Size = " + iterVPairsList.size() + " while iter = " + iter);

            int newCapacity = iter + 100;

            ArrayList<Set<Integer>> temp = iterVPairsList;

            iterVPairsList = new ArrayList<>(newCapacity);
            iterVPairsList.addAll(temp);
            for (int i = 0; i < newCapacity - temp.size(); ++i) {
                iterVPairsList.add(new HashSet<>());
            }

            System.out.println("New Size = " + iterVPairsList.size());
        }

        return iterVPairsList.get(iter);
    }

    /**
     * Clears all of the vertices to fix.
     */
    public void clear() {
        for (Set<Integer> vsToFix : iterVPairsList) {
            vsToFix.clear();
        }
        isEmpty = true;
    }

    /**
     * Clear vertices from a specific iteration
     * @param iteration
     */
    public void clear(int iteration) {
        iterVPairsList.get(iteration).clear();
    }

    /**
     * Adds a vertex with the given ID to the list of vertices to fix in the given iteration number.
     *
     * @param vertexId    ID of the vertex to fix.
     * @param iterationNo iteration number in which the vertex should be fixed.
     */
    public void addVToFix(int vertexId, int iterationNo) {
        if (DistancesWithDropBloom.debug(vertexId)) {
            Report.INSTANCE.error("----FAKE2 addVToFix v=" + vertexId + " iter= " + iterationNo);
        }

        if (iterationNo >= iterVPairsList.size()) {
            for (int i = iterVPairsList.size(); i <= iterationNo; ++i) {
                iterVPairsList.add(new HashSet<>());
            }
        }
        getItemFromIterVPairsList(iterationNo).add(vertexId);
        isEmpty = false;
    }


    public void removeDeletedVertices() {

        Report.INSTANCE.debug("------ removeDeletedVertices ");

        for (int iterationNo = 0; iterationNo < iterVPairsList_toBeDeleted.size(); iterationNo++) {
/*
            if (iterVPairsList_toBeDeleted.get(iterationNo).size() > 0) {
                if (Report.INSTANCE.appReportingLevel == Report.Level.DEBUG) {
                    Report.INSTANCE.debug("** Removing " +
                            Arrays.toString(iterVPairsList_toBeDeleted.get(iterationNo).toArray()));
                }
            }

 */

            for (Integer vertexId : iterVPairsList_toBeDeleted.get(iterationNo)) {
                getItemFromIterVPairsList(iterationNo).remove(vertexId);
            }
            iterVPairsList_toBeDeleted.get(iterationNo).clear();
        }
        isEmpty = isEmptyHash();
    }

    /**
     * Adds a vertex with the given ID to the list of vertices to fix in the given iteration number.
     *
     * @param vertexId    ID of the vertex to fix.
     * @param iterationNo iteration number in which the vertex should be fixed.
     */
    public void removeV(int vertexId, int iterationNo) {

        Report.INSTANCE.debug("------ removeV v=" + vertexId + " itr=" + iterationNo);

        getItemFromIterVPairsList(iterationNo).remove(vertexId);
        isEmpty = isEmptyHash();
    }


    /**
     * Adds a vertex with the given ID to the list of vertices to fix in the given iteration number.
     *
     * @param vertexId    ID of the vertex to fix.
     * @param iterationNo iteration number in which the vertex should be fixed.
     */
    public void removeVLazy(int vertexId, int iterationNo) {

        Report.INSTANCE.debug("------ Lazy removeV v=" + vertexId + " itr=" + iterationNo);

        if (iterationNo >= iterVPairsList_toBeDeleted.size()) {
            for (int i = iterVPairsList_toBeDeleted.size(); i <= iterationNo; ++i) {
                iterVPairsList_toBeDeleted.add(new HashSet<>());
            }
        }
        iterVPairsList_toBeDeleted.get(iterationNo).add(vertexId);
    }


    boolean isEmptyHash() {
        isEmpty = true;
        int counter = 0;
        for (Set<Integer> i : iterVPairsList) {
            if (i.size() > 0) {
                /*
                if (Report.INSTANCE.appReportingLevel == Report.Level.DEBUG) {
                    Report.INSTANCE.debug("iterVPairsList of " + counter + " : " + Arrays.toString(i.toArray()));
                }
                 */
                isEmpty = false;
                return false;
            }
            counter++;
        }
        return isEmpty;
    }

    void print() {
/*
        if (Report.INSTANCE.appReportingLevel == Report.Level.DEBUG) {
            for (int i = 0; i < iterVPairsList.size(); i++) {
                Report.INSTANCE.debug("= iteration # " + i + " - vertices: " +
                        Arrays.toString(getItemFromIterVPairsList(i).toArray()));
            }
        }

 */
    }
}