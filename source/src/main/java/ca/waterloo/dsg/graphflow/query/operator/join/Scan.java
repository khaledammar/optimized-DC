package ca.waterloo.dsg.graphflow.query.operator.join;

import ca.waterloo.dsg.graphflow.graph.Graph.GraphVersion;
import ca.waterloo.dsg.graphflow.graph.TypeAndPropertyKeyStore;
import ca.waterloo.dsg.graphflow.query.result.subgraph.SubgraphType;
import ca.waterloo.dsg.graphflow.query.structuredquery.QueryRelation;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * The scan operator gets all edges based on the {@link QueryRelation}'s from vertex, to vertex,
 * and edge type filters, and appends each set of prefixes to the next operator in batches.
 */
public class Scan extends StageOperator {

    private short fromVertexTypeFilter;
    private Predicate<Integer> firstVertexLiteralPredicate;
    private Predicate<Integer> secondVertexLiteralPredicate;

    /**
     * @param intersectionRules the {@link EdgeIntersectionRule}s the edges scanned need to follow.
     * @param fromVertexTypeFilter Filters the edges that do not have the given from vertex type. If
     * the value of {@code fromVertexTypeFilter} is {@link TypeAndPropertyKeyStore#ANY}, this
     * parameter is ignored.
     * @param toVertexTypeFilter Filters the edges that do not have the given to vertex type. If the
     * value of {@code fromVertexTypeFilter} is {@link TypeAndPropertyKeyStore#ANY}, this parameter
     * is ignored.
     */
    public Scan(List<EdgeIntersectionRule> intersectionRules, short fromVertexTypeFilter,
        short toVertexTypeFilter, Predicate<Integer> firstVertexLiteralPredicate,
        Predicate<Integer> secondVertexLiteralPredicate) {
        super(intersectionRules, toVertexTypeFilter);
        this.fromVertexTypeFilter = fromVertexTypeFilter;
        this.firstVertexLiteralPredicate = firstVertexLiteralPredicate;
        this.secondVertexLiteralPredicate = secondVertexLiteralPredicate;
        this.newPrefixes = new int[BATCH_SIZE][];
    }

    /**
     * Scans a set of edges according to the {@link EdgeIntersectionRule}s of the operator
     * and appends them to the next operator in batches of size {@link StageOperator#BATCH_SIZE}.
     */
    public void execute() {
        EdgeIntersectionRule firstGJIntersectionRule = intersectionRules.get(0);
        // Get the initial set of edges filtered by the {@code GraphVersion}, the {@code
        // Direction}, the edge type filter and the property equality filters using the {@code
        // firstGJIntersectionRule} of the first stage.
        Iterator<int[]> iterator = graph.getEdgesIterator(firstGJIntersectionRule.getGraphVersion(),
            firstGJIntersectionRule.getDirection(), fromVertexTypeFilter, toVertexTypeFilter,
            firstGJIntersectionRule.getEdgeTypeFilter());
        if (!iterator.hasNext()) {
            // Obtained empty set of edges, nothing to execute.
            super.notifyDone();
        }

        int index = 0;
        while (iterator.hasNext()) {
            int[] prefix = iterator.next();
            boolean isPrefixPresentForAllRules = true;
            for (int i = 1; i < intersectionRules.size(); i++) {
                // For each additional {@code EdgeIntersectionRule} present in the first
                // stage, check if the edge ({@code prefix[0]}, {@code prefix[1]}) satisfies the
                // {@code GraphVersion}, the {@code Direction}, and has the types and properties of
                // the rule.
                EdgeIntersectionRule rule = intersectionRules.get(i);
                if (!graph.isEdgePresent(prefix[0], prefix[1], rule.getDirection(), rule.
                    getGraphVersion(), rule.getEdgeTypeFilter())) {
                    // The {@code prefix} did not satisfy the rule {@code i} of the first stage.
                    isPrefixPresentForAllRules = false;
                    break;
                }
            }
            if (!isPrefixPresentForAllRules) {
                // Skip adding {@code prefix} to the list of {@code initialPrefixes}, because it
                // does not satisfy one of the {@code EdgeIntersectionRule}s of the first
                // stage.
                continue;
            }
            if ((null == firstVertexLiteralPredicate || firstVertexLiteralPredicate.test(prefix[0]))
                && (null == secondVertexLiteralPredicate || secondVertexLiteralPredicate.test(
                    prefix[1]))) {
                newPrefixes[index++] = prefix;
                if (BATCH_SIZE == index) {
                    // Append the prefixes to the next operator in batches of size BATCH_SIZE.
                    append(newPrefixes, index);
                    index = 0;
                }
            }
        }

        if (0 < index) {
            // Append the prefixes which did not reach size of BATCH_SIZE.
            append(newPrefixes, index);
        }
        super.notifyDone();
    }

    /**
     * @return the {@link SubgraphType} of the output prefixes of this query.
     */
    public SubgraphType getMatchQueryResultType() {
        EdgeIntersectionRule firstGJIntersectionRule = intersectionRules.get(0);
        if (GraphVersion.DIFF_PLUS == firstGJIntersectionRule.getGraphVersion()) {
            return SubgraphType.EMERGED;
        } else if (GraphVersion.DIFF_MINUS == firstGJIntersectionRule.getGraphVersion()) {
            return SubgraphType.DELETED;
        } else {
            return SubgraphType.MATCHED;
        }
    }

    /**
     * @return the {@code short} type filter for the from vertex in the intersection rules.
     */
    public short getFromVertexTypeFilter() {
        return fromVertexTypeFilter;
    }
}
