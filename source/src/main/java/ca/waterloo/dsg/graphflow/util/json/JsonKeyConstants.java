package ca.waterloo.dsg.graphflow.util.json;

/**
 * Class containing common keys used in JSON objects. Each enum value is used as a literal with a
 * toString() call.
 */
public enum JsonKeyConstants {
    NEXT_OPERATORS,

    ID,
    TYPE,
    PROPERTIES,
    INDEX,
    KEY,

    COUNT_STAR_DESCRIPTOR,

    NAME,
    VALUE,
    ARGS,

    MESSAGE,
    IS_ERROR,

    SINK,

    VAR_ORDERING,
    GRAPH_VERSION,
    VARIABLE,
    FROM_VERTEX_TYPE,
    TO_VERTEX_TYPE,
    DIRECTION,
    EDGE_TYPE,
    STAGES,

    RESPONSE_TYPE,
    SUBGRAPHS,
    SUBGRAPH_TYPE,

    VERTICES,
    VERTEX_MAP,

    EDGES,
    FROM_VERTEX_ID,
    TO_VERTEX_ID,
    EDGE_STATE,

    TUPLES,
    COLUMN_TYPES,
    COLUMN_NAMES,

    PLAN,
    EXECUTION_TIME;

    public String toString() {
        return super.toString().toLowerCase();
    }
}
