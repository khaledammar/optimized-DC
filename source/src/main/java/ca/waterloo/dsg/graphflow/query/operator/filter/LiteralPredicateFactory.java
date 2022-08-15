package ca.waterloo.dsg.graphflow.query.operator.filter;

import ca.waterloo.dsg.graphflow.graph.TypeAndPropertyKeyStore;
import ca.waterloo.dsg.graphflow.graph.VertexPropertyStore;
import ca.waterloo.dsg.graphflow.query.operator.join.Extend;
import ca.waterloo.dsg.graphflow.query.operator.join.Scan;
import ca.waterloo.dsg.graphflow.query.structuredquery.ComparisonPredicate;
import ca.waterloo.dsg.graphflow.query.structuredquery.InClausePredicate;
import ca.waterloo.dsg.graphflow.query.structuredquery.QueryPredicate;
import ca.waterloo.dsg.graphflow.util.datatype.DataType;
import ca.waterloo.dsg.graphflow.util.datatype.RuntimeComparator;
import ca.waterloo.dsg.graphflow.util.datatype.RuntimeComparator.ComparisonOperator;

import java.util.List;
import java.util.function.Predicate;

/**
 * This class is a factory for predicates containing only literals on the right side of the
 * predicate. It is used to filter out edges during the {@link Scan} and {@link Extend} operations.
 */
public class LiteralPredicateFactory {

    /**
     * This function creates a conjunctive predicate from a list of literal {@link QueryPredicate}s.
     *
     * @param queryPredicates the list of literal {@link QueryPredicate}s to form a conjunctive
     * predicate from.
     * @return a {@link Predicate<Integer>} that checks whether a vertex satisfies the predicates.
     */
    public static Predicate<Integer> getLiteralFilterPredicate(
        List<QueryPredicate> queryPredicates) {
        Predicate<Integer> result = null;
        for (QueryPredicate queryPredicate : queryPredicates) {
            Predicate<Integer> predicate;
            switch (queryPredicate.getPredicateType()) {
                case IN_CLAUSE_ONLY_LITERALS:
                    predicate = getInClauseLiteralOnlyPredicate((InClausePredicate) queryPredicate);
                    break;
                case COMPARATIVE_CLAUSE_PROPERTY_KEY_AND_LITERAL_OPERANDS:
                    predicate = getComparativeClauseLiteralOnlyPredicate((ComparisonPredicate)
                        queryPredicate);
                    break;
                default:
                    throw new IllegalArgumentException("The predicate type " + queryPredicate.
                        getPredicateType().name() + " is not supported.");
            }
            if (null == result) {
                result = predicate;
            } else {
                result = result.and(predicate);
            }
        }
        return result;
    }

    /**
     * Converts an {@link InClausePredicate} into a {@link Predicate<Integer>}.
     *
     * @param inClause the {@link InClausePredicate} to be converted.
     * @return a {@link Predicate<Integer>} that represents the IN clause.
     */
    private static Predicate<Integer> getInClauseLiteralOnlyPredicate(InClausePredicate inClause) {
        short propertyKey = TypeAndPropertyKeyStore.getInstance().mapStringPropertyKeyToShort(
            inClause.getLeftOperand().b);
        DataType dataType = TypeAndPropertyKeyStore.getInstance().getPropertyDataType(inClause.
            getLeftOperand().b);

        return (Integer prefix) -> {
            String propertyValue = String.valueOf(VertexPropertyStore.getInstance().getProperty(
                prefix, propertyKey));
            for (String literal : inClause.getLiterals()) {
                if (RuntimeComparator.resolveTypesAndCompare(DataType.parseDataType(dataType,
                    propertyValue), DataType.parseDataType(dataType, literal), ComparisonOperator.
                    EQUALS)) {
                    return true;
                }
            }
            return false;
        };
    }

    /**
     * Converts a {@link ComparisonPredicate} into a {@link Predicate<Integer>}.
     *
     * @param predicate the {@link ComparisonPredicate} to convert.
     * @return a {@link Predicate<Integer>} that represents the comparison predicate.
     */
    private static Predicate<Integer> getComparativeClauseLiteralOnlyPredicate(
        ComparisonPredicate predicate) {
        short propertyKey = TypeAndPropertyKeyStore.getInstance().mapStringPropertyKeyToShort(
            predicate.getLeftOperand().b);
        DataType dataType = TypeAndPropertyKeyStore.getInstance().getPropertyDataType(predicate.
            getLeftOperand().b);
        ComparisonOperator operator = predicate.getComparisonOperator();
        String literal = predicate.getLiteral();

        return (Integer prefix) -> {
            String propertyValue = String.valueOf(VertexPropertyStore.getInstance().getProperty(
                prefix, propertyKey));
            return RuntimeComparator.resolveTypesAndCompare(DataType.parseDataType(dataType,
                propertyValue), DataType.parseDataType(dataType, literal), operator);
        };
    }
}
