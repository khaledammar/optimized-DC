package ca.waterloo.dsg.graphflow.query.result.subgraph;

/**
 * Used to represent the type of the motifs that result from match queries. Result motifs from
 * {@code ContinuousMatchQueryExecutor} are of the type {@code EMERGED} or {@code DELETED},
 * while result motifs from {@code GenericJoinExecutor} are always of the type {@code MATCHED}.
 */
public enum SubgraphType {
    EMERGED,
    DELETED,
    MATCHED
}