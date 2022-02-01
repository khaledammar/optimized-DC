package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.util.VisibleForTesting;

import java.util.*;

/**
 * This class contains a map of the shortest path from one source to a destination
 */
public class ShortestPath {
    @VisibleForTesting
    Map<Integer, Set<Integer>> shortestPath = new HashMap<>();
    private int source;
    private int destination;

    ShortestPath(int source, int destination) {
        this.source = source;
        this.destination = destination;
    }

    void add(int node, Set<Integer> parents) {
        shortestPath.put(node, parents);
    }

    boolean contains(int node) {
        return shortestPath.containsKey(node);
    }

    void clear() {
        this.shortestPath.clear();
    }

    boolean isEmpty() {
        return shortestPath.isEmpty();
    }

    @Override
    public String toString() {
        if (shortestPath.isEmpty()) {
            return "[]";
        }
        StringJoiner stringJoiner = new StringJoiner(", ");
        for (Map.Entry<Integer, Set<Integer>> entry : shortestPath.entrySet()) {
            stringJoiner.add(entry.getKey() + ": " + Arrays.toString(entry.getValue().toArray()));
        }
        return stringJoiner.toString();
    }

    public String toStringKhaled() {
        if (shortestPath.isEmpty()) {
            return "[]";
        }
        StringJoiner stringJoiner = new StringJoiner(" --> ");

        int vertex = destination;

        String parent =
                shortestPath.get(vertex).size() > 0 ? String.valueOf(shortestPath.get(vertex).toArray()[0]) : "";
        stringJoiner.add(String.valueOf(vertex));

        while (!parent.equals("")) {
            stringJoiner.add(parent);
            vertex = Integer.valueOf(parent);
            //System.out.println(vertex);
            parent = shortestPath.get(vertex).size() > 0 ? String.valueOf(shortestPath.get(vertex).toArray()[0]) : "";
            //System.out.println(Arrays.toString(shortestPath.get(vertex).toArray()));
        }

/*
        for (Map.Entry<Integer, Set<Integer>> entry : shortestPath.entrySet()) {
            int vertex = entry.getKey();
            String parent = entry.getValue().size() > 0 ? String.valueOf(entry.getValue().toArray()[0]) : "";
            stringJoiner.add(vertex + " " + parent);
        }
*/
        System.out.println("Path = " + stringJoiner.toString());
        return stringJoiner.toString();
    }


    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (!(other instanceof ShortestPath)) {
            return false;
        }
        ShortestPath otherPath = (ShortestPath) other;

        if (otherPath.source != this.source) {
            return false;
        }
        if (otherPath.destination != this.destination) {
            return false;
        }
        if (otherPath.shortestPath.size() != this.shortestPath.size()) {
            return false;
        }

        for (Map.Entry<Integer, Set<Integer>> entry : otherPath.shortestPath.entrySet()) {
            if (!this.shortestPath.containsKey(entry.getKey())) {
                return false;
            }
            if (!entry.getValue().equals(this.shortestPath.get(entry.getKey()))) {
                return false;
            }
        }

        return true;
    }
}
