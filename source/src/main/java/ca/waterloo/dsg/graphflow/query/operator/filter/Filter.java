package ca.waterloo.dsg.graphflow.query.operator.filter;

import ca.waterloo.dsg.graphflow.query.operator.AbstractOperator;
import ca.waterloo.dsg.graphflow.query.operator.descriptor.EdgeOrVertexPropertyDescriptor;
import ca.waterloo.dsg.graphflow.query.operator.resolver.PropertyReadingOperator;
import ca.waterloo.dsg.graphflow.query.result.MatchQueryOutput;
import ca.waterloo.dsg.graphflow.query.structuredquery.QueryPredicate;
import ca.waterloo.dsg.graphflow.util.json.JsonKeyConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.function.Predicate;

/**
 * Operator for filtering the output from a MATCH query based on a set of comparison predicates.
 * The comparisons are specified in the WHERE clause of the MATCH query and their conjunction is
 * used for filtering. The comparisons from the query are used to construct a {@link Predicate}
 * which tests each {@link MatchQueryOutput}.
 */
public class Filter extends PropertyReadingOperator {

    private static final Logger logger = LogManager.getLogger(Filter.class);
    private static final String FILTER_DELIMITER = "%%";
    private final Predicate<String[]> filterPredicate;
    private final List<QueryPredicate> queryPredicates;

    /**
     * Default constructor.
     *
     * @param nextOperator Next operator to append outputs to.
     * @param filterPredicate A composite {@link Predicate<String[]>} representing all the filter
     * predicates for a MATCH query ANDed together.
     * @param edgeOrVertexPropertyDescriptors A {@link EdgeOrVertexPropertyDescriptor} list
     * specifying parameters for retrieving the list of properties used by the {@link Predicate}s.
     * @param queryPredicates The predicates used for filtering the MATCH output.
     */
    public Filter(AbstractOperator nextOperator, Predicate<String[]> filterPredicate,
        List<EdgeOrVertexPropertyDescriptor> edgeOrVertexPropertyDescriptors,
        List<QueryPredicate> queryPredicates) {
        super(nextOperator, edgeOrVertexPropertyDescriptors);
        this.filterPredicate = filterPredicate;
        this.queryPredicates = queryPredicates;
        logger.info(queryPredicates.toString());
    }

    @Override
    public void append(MatchQueryOutput matchQueryOutput) {
        clearAndFillStringBuilder(matchQueryOutput, FILTER_DELIMITER);
        String[] properties = stringBuilder.toString().split(FILTER_DELIMITER);
        for (String property : properties) {
            if (property.equals("null")) {
                return;
            }
        }
        if (filterPredicate.test(properties)) {
            nextOperator.append(matchQueryOutput);
        }
    }

    @Override
    public String getHumanReadableOperator() {
        StringBuilder stringBuilder = new StringBuilder("Filter:\n");
        appendListAsCommaSeparatedString(stringBuilder, queryPredicates, "filterPredicates");
        return stringBuilder.toString();
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonFilter = new JsonObject();

        JsonArray jsonPropertyPredicates = new JsonArray();
        for (QueryPredicate queryPredicate : queryPredicates) {
            jsonPropertyPredicates.add(queryPredicate.toJson());
        }
        JsonArray jsonArguments = new JsonArray();
        JsonObject jsonArgument = new JsonObject();
        jsonArgument.addProperty(JsonKeyConstants.NAME.toString(), "Vertex Indices");
        jsonArgument.add(JsonKeyConstants.VALUE.toString(), jsonPropertyPredicates);
        jsonArguments.add(jsonArgument);
        jsonFilter.addProperty(JsonKeyConstants.NAME.toString(), "Filter (&sigma;)");
        jsonFilter.add(JsonKeyConstants.ARGS.toString(), jsonArguments);

        return jsonFilter;
    }
}
