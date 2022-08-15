package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.SortedAdjacencyList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implements Weighted BFS and executes the same algorithm continuously
 */
public class BaselineWCC {

    private static final double INF = Double.MAX_VALUE;

    private static final BaselineWCC INSTANCE = new BaselineWCC();

    private static final Logger logger = LogManager.getLogger(BaselineWCC.class);

    int source;
    int destination;
    protected Map<Integer, Long> tempFrontier = new HashMap<>();
    protected Set<Integer> frontier = new HashSet<>();
    protected Map<Integer, Long> distance = new HashMap<>();

    public BaselineWCC() {

    }

    public static BaselineWCC getInstance() {
        return INSTANCE;
    }

    protected void init(int source, int destination) {
        this.source = source;
        this.destination = destination;
        this.distance.clear();
        this.distance.put(this.source, 0L);
        this.frontier.clear();
    }

    public long execute(int source, int destination) {

        //System.out.println("\n\n\nStart Execute");

        init(source, destination);

        frontier.add(this.source);

        while (!frontier.isEmpty()) {
            //System.out.println("\nFrontier size = "+frontier.size());

            for (int currVertexId : frontier) {
                SortedAdjacencyList adjacencyList = Graph.INSTANCE.getForwardMergedAdjacencyList(currVertexId);
                if (SortedAdjacencyList.isNullOrEmpty((adjacencyList))) {
                    continue;
                }
                for (int i = 0; i < adjacencyList.getSize(); i++) {
                    updateNbrDistances(currVertexId, adjacencyList, i, tempFrontier);
                }
            }

            frontier.clear();

            //System.out.println("---Frontier size = "+frontier.size());
            //System.out.println("---Frontier-temp size = "+tempFrontier.size());

            for (Map.Entry<Integer, Long> entry : tempFrontier.entrySet()) {
                frontier.add(entry.getKey());
                distance.put(entry.getKey(), entry.getValue());
            }
            tempFrontier.clear();

            //System.out.println("---Frontier-temp size = "+tempFrontier.size());
        }

        //System.out.println("Finished Execute\n\n\n");

        if (distance.containsKey(destination)) {
            return distance.get(destination);
        } else {
            return Long.MAX_VALUE;
        }
    }

    protected void updateNbrDistances(int currVertex, SortedAdjacencyList adjList, int nbrIndex,
                                    Map<Integer, Long> newFrontier) {

        int nbrId = adjList.neighbourIds[nbrIndex];
        long edgeWeight = (long) adjList.weights[nbrIndex];
        long nbrWeight = distance.get(currVertex) + edgeWeight;
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
}
