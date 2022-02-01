package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.SortedAdjacencyList;
import ca.waterloo.dsg.graphflow.util.IntQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class DijkstraBaseline {

    private static final DijkstraBaseline INSTANCE = new DijkstraBaseline();

    private static final Logger logger = LogManager.getLogger(DijkstraBaseline.class);

    public static boolean backtrack = true;
    int source;
    int destination;
    private Map<Integer, Long> distance = new HashMap<>();
    private ShortestPath oldPath;
    private DijkstraBaseline() {
    }

    public static DijkstraBaseline getInstance() {
        return INSTANCE;
    }

    private void init(int source, int destination) {
        this.source = source;
        this.destination = destination;
        this.distance.clear();
        this.distance.put(this.source, 0L);
        this.distance.put(this.destination, Long.MAX_VALUE);
    }

    public long execute(int source, int destination, boolean is_weighted) {

        init(source, destination);
        PriorityQueue<Integer> pqueue =
                new PriorityQueue<>(Graph.getInstance().getVertexCount(), new NodeDistanceComparator());
        pqueue.add(this.source);

        init(source, destination);

        while (!pqueue.isEmpty()) {
            int currVertexId = pqueue.remove();
            if (currVertexId == this.destination) {
                //System.out.println("Found destintation at distance = "+ distance.get(currVertexId)+ " - STOP");
                break;
            }
            SortedAdjacencyList adjacencyList = Graph.INSTANCE.getForwardMergedAdjacencyList(currVertexId);

            //System.out.println(" Vertex "+currVertexId+" has "+adjacencyList.getSize()+" neighbors!");

            if (SortedAdjacencyList.isNullOrEmpty((adjacencyList))) {
                continue;
            }
            for (int i = 0; i < adjacencyList.getSize(); i++) {
                int nbrId = adjacencyList.neighbourIds[i];
                double edgeWeight = 1;
                if (is_weighted) {
                    edgeWeight = adjacencyList.weights[i];
                }

                long nbrWeight = (long) (distance.get(currVertexId) + edgeWeight);
                if (!distance.containsKey(nbrId) || nbrWeight < distance.get(nbrId)) {
                    distance.put(nbrId, nbrWeight);
                    //System.out.println(" Vertex "+nbrId+" has distance = "+nbrWeight);
                    pqueue.add(nbrId);
                }
            }
        }

        return distance.get(destination);

        // back tracking step
        //ShortestPath path = backtrack(source, destination, is_weighted);

        //        if(null != path){
        //            System.out.println(path.toString());
        //        }
    }

    private ShortestPath backtrack(int source, int destination, boolean is_weighted) {
        if (!distance.containsKey(destination) || distance.get(destination) == Double.MAX_VALUE) {
            return null;
        }
        ShortestPath path = new ShortestPath(source, destination);
        IntQueue nodesOnPath = new IntQueue();
        Set<Integer> visited = new HashSet<>();

        nodesOnPath.enqueue(this.destination);
        while (!nodesOnPath.isEmpty()) {
            int currNode = nodesOnPath.dequeue();
            if (visited.contains(currNode)) {
                continue;
            }
            visited.add(currNode);
            Set<Integer> parents = new HashSet<>();
            double vertexDistance = distance.get(currNode);
            SortedAdjacencyList incomingNodes = Graph.INSTANCE.getBackwardMergedAdjacencyList(currNode);

            for (int i = 0; i < incomingNodes.getSize(); i++) {
                int nbrId = incomingNodes.neighbourIds[i];
                double nbrWeight = 1;
                if (is_weighted) {
                    nbrWeight = incomingNodes.weights[i];
                }

                if (!distance.containsKey(nbrId)) {
                    continue;
                }
                if (distance.get(nbrId) + nbrWeight == vertexDistance) {
                    parents.add(nbrId);
                    nodesOnPath.enqueue(nbrId);
                }
            }

            path.add(currNode, parents);
            if (this.source == currNode) {
                break;
            }
        }
        return path;
    }

    private class NodeDistanceComparator implements Comparator<Integer> {

        @Override
        public int compare(Integer x, Integer y) {
            boolean xHasDistance = distance.containsKey(x);
            boolean yHasDistance = distance.containsKey(y);
            if (!xHasDistance && !yHasDistance) {
                return 0;
            }
            if (xHasDistance && !yHasDistance) {
                return -1;
            }
            if (yHasDistance && !xHasDistance) {
                return 1;
            }
            if (distance.get(x) < distance.get(y)) {
                return -1;
            }
            if (distance.get(x) > distance.get(y)) {
                return 1;
            }
            return 0;
        }
    }
}
