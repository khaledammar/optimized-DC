package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.ExecutorType;

/**
 * Tests for {@link NewBidirUnweightedDifferentialBFS}.
 */
public class ContinuousBidirUnweightedShortestPathExecutorTest
        extends BaseContinuousUnidirBidirUnweightedShortestPathExecutorTest {

    @Override
    ExecutorType getExecutorType() {
        return ExecutorType.BIDIR_UNWEIGHTED_DIFF_BFS;
    }
}
