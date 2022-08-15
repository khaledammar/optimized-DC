package ca.waterloo.dsg.graphflow.query.plan;

import ca.waterloo.dsg.graphflow.exception.NoSuchPropertyKeyException;
import ca.waterloo.dsg.graphflow.graph.IndexStore;
import ca.waterloo.dsg.graphflow.graph.TypeAndPropertyKeyStore;
import ca.waterloo.dsg.graphflow.query.result.Message;
import ca.waterloo.dsg.graphflow.query.result.AbstractQueryResult;
import ca.waterloo.dsg.graphflow.query.structuredquery.StructuredQuery;
import org.antlr.v4.runtime.misc.Pair;

/**
 * Class representing plan for a CREATE INDEX operation.
 */
public class IndexCreationQueryPlan extends QueryPlan {

    private StructuredQuery structuredQuery;

    /**
     * Creates a query planner object for index creation using given a {@link StructuredQuery}.
     *
     * @param structuredQuery parsed query object for current query.
     */
    public IndexCreationQueryPlan(StructuredQuery structuredQuery) {
        this.structuredQuery = structuredQuery;
    }

    /**
     * @see QueryPlan#execute()
     */
    @Override
    public AbstractQueryResult execute() {
        return new Message(createIndex());
    }

    /**
     * Creates an index depending on its {@link StructuredQuery}.
     *
     * @return Returns a string describing what it did.
     */
    private String createIndex() {
        Pair<String, String> typePropertyPair = structuredQuery.getTypeAndPropertyToIndex();
        String stringType = typePropertyPair.a;
        String stringProperty = typePropertyPair.b;
        Short type = TypeAndPropertyKeyStore.ANY;
        if (null != stringType) {
            type = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShort(stringType);
        }
        Short property = TypeAndPropertyKeyStore.getInstance().mapStringPropertyKeyToShort(
            stringProperty);
        if (null == property) {
            throw new NoSuchPropertyKeyException(stringProperty);
        }
        IndexStore.getInstance().createIndex(type, property);
        return "Index created for type: " + (null == stringType ? "<ALL_TYPES>" : stringType)  +
            " and on property: " + stringProperty;
    }
}
