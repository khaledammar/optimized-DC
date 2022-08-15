package ca.waterloo.dsg.graphflow.query.plans;

import ca.waterloo.dsg.graphflow.query.executors.csp.NewUnidirectionalDifferentialBFS;
import ca.waterloo.dsg.graphflow.query.executors.csp.VariableLengthPathDifferentialBFS;
import ca.waterloo.dsg.graphflow.util.VisibleForTesting;

/**
 * A {@link QueryPlan} for continuous variable length path queries. These
 * queries take a source, a destination, a minimum and maximum length of paths
 * interested and finds all paths from the source to the destination within the
 * given lengths.
 */
public class ContinuousVariableLengthPathPlan extends ContinuousShortestPathPlan {

    @VisibleForTesting
    public VariableLengthPathDifferentialBFS diffBFS;

    public ContinuousVariableLengthPathPlan(int queryId, int source, int destination, int minLength, int maxLength,
                                            boolean backtrack, NewUnidirectionalDifferentialBFS.Queries queryType) {
        super(queryId, source, destination, null /* no output sink */);
        diffBFS = new VariableLengthPathDifferentialBFS(queryId, source, destination, minLength, maxLength, backtrack, queryType);
        diffBFS.continueBFS();
    }

    @Override
    public double getSrcDstDistance() {
        return -1.0;
    }

    @Override
    public void execute() {
        diffBFS.executeDifferentialBFS();
    }
}
