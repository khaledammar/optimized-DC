package ca.waterloo.dsg.graphflow.server;

import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.GraphDBState;

import java.io.IOException;

/**
 * Instantiates {@code GraphflowServer}.
 */
public class GraphflowServerRunner {

    public static void main(String[] args) throws IOException, InterruptedException {
        // TODO(semih): This is a quick hack to implement weighted graph support
        // for weighted continuous shortest paths research we do.
        if (args.length > 0) {
            if ("is-weighted-graph".equals(args[0])) {
                Graph.IS_WEIGHTED = true;
                GraphDBState.reset();
            }
        }
        final GraphflowServer graphflowServer = new GraphflowServer();
        graphflowServer.start();
        graphflowServer.blockUntilShutdown();
    }
}
