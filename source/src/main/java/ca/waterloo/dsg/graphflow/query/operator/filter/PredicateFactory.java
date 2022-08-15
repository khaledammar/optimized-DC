package ca.waterloo.dsg.graphflow.query.operator.filter;

import ca.waterloo.dsg.graphflow.graph.TypeAndPropertyKeyStore;
import ca.waterloo.dsg.graphflow.query.operator.descriptor.EdgeOrVertexPropertyDescriptor;
import ca.waterloo.dsg.graphflow.query.result.MatchQueryOutput;
import ca.waterloo.dsg.graphflow.query.structuredquery.ComparisonPredicate;
import ca.waterloo.dsg.graphflow.query.structuredquery.InClausePredicate;
import ca.waterloo.dsg.graphflow.query.structuredquery.QueryPredicate;
import ca.waterloo.dsg.graphflow.util.datatype.DataType;
import ca.waterloo.dsg.graphflow.util.datatype.RuntimeComparator;
import ca.waterloo.dsg.graphflow.util.datatype.RuntimeComparator.ComparisonOperator;
import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Contains static methods for creating {@link Predicate} objects that are used by {@link Filter}.
 */
public class PredicateFactory {

    /**
     * Returns a {@link Predicate<String[]>} which combines a list of {@link QueryPredicate}s
     *
     * @param queryPredicates a list of  {@link QueryPredicate}s which are to be converted to a
     * {@link Predicate<String[]>}
     * @param descriptorIndexMap a map of variables to their index in the
     * property result set created by the {@link Filter} operator. {@link Filter} uses its {@link
     * EdgeOrVertexPropertyDescriptor} list to create the property result set from the {@link
     * MatchQueryOutput}.
     * @return a {@link Predicate<String[]>} instance that will perform the comparisons specified
     * in the {@code queryPredicates}.
     */
    public static Predicate<String[]> getFilterPredicate(List<QueryPredicate> queryPredicates,
        Map<String, Integer> descriptorIndexMap) {
        Predicate<String[]> result = null;
        for (QueryPredicate queryPredicate : queryPredicates) {
            Predicate<String[]> predicate;
            switch (queryPredicate.getPredicateType()) {
                case IN_CLAUSE_ONLY_LITERALS:
                case IN_CLAUSE_VARIABLES_AND_LITERALS:
                    predicate = getInClausePredicate((InClausePredicate) queryPredicate,
                    descriptorIndexMap);
                    break;
                case COMPARATIVE_CLAUSE_TWO_PROPERTY_KEY_OPERANDS:
                case COMPARATIVE_CLAUSE_PROPERTY_KEY_AND_LITERAL_OPERANDS:
                     predicate = getComparativeClausePredicate((ComparisonPredicate) queryPredicate,
                        descriptorIndexMap);
                     break;
                default:
                    // Should never execute. Every predicate type introduced should be supported.
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

    private static Predicate<String[]> getInClausePredicate(InClausePredicate inClause,
        Map<String, Integer> descriptorIndexMap) {
        Pair<String, String> leftOperand = inClause.getLeftOperand();
        DataType dataType = TypeAndPropertyKeyStore.getInstance().
            getPropertyDataType(leftOperand.b);

        int leftOperandIndexInPropertyResults = descriptorIndexMap.get(leftOperand.a + '.' +
            leftOperand.b);

        List<Integer> rightOperandsIndexInPropertyResults = new ArrayList<>();
        for (Pair<String, String> variable : inClause.getVariablesWithProperty()) {
            rightOperandsIndexInPropertyResults.add(descriptorIndexMap.get(variable.a + '.' +
                variable.b));
        }
        return (String[] predicate) -> {
            for (String literal : inClause.getLiterals()) {
                if (RuntimeComparator.resolveTypesAndCompare(DataType.parseDataType(dataType,
                    predicate[leftOperandIndexInPropertyResults]), DataType.parseDataType(dataType,
                    literal), ComparisonOperator.EQUALS)) {
                    return true;
                }
            }
            for (int indexInPropertyResults : rightOperandsIndexInPropertyResults) {
                if (RuntimeComparator.resolveTypesAndCompare(DataType.parseDataType(dataType,
                    predicate[leftOperandIndexInPropertyResults]), DataType.parseDataType(dataType,
                    predicate[indexInPropertyResults]), ComparisonOperator.EQUALS)) {
                    return true;
                }
            }
            return false;
        };
    }

    private static Predicate<String[]> getComparativeClausePredicate(
        ComparisonPredicate comparisonPredicate, Map<String, Integer> descriptorIndexMap) {
        DataType dataType = getDataTypeToCastOperandsTo(comparisonPredicate);
        ComparisonOperator operator = comparisonPredicate.getComparisonOperator();
        Pair<String, String> leftOperand = comparisonPredicate.getLeftOperand();
        Pair<String, String> rightOperand = comparisonPredicate.getRightOperand();
        String literal = comparisonPredicate.getLiteral();

        int variable1IndexInPropertyResults = descriptorIndexMap.get(leftOperand.a + '.' +
            leftOperand.b);
        int variable2IndexInPropertyResults = (null != rightOperand) ? descriptorIndexMap.get(
            rightOperand.a + '.' + rightOperand.b) : -1;

        return (String[] predicate) -> {
            String rvalue = (variable2IndexInPropertyResults == -1) ? literal : predicate[
                variable2IndexInPropertyResults];
            return RuntimeComparator.resolveTypesAndCompare(DataType.parseDataType(dataType,
                predicate[variable1IndexInPropertyResults]), DataType.parseDataType(dataType,
                rvalue), operator);
        };
    }

    private static DataType getDataTypeToCastOperandsTo(ComparisonPredicate predicate) {
        DataType leftOperandDataType = TypeAndPropertyKeyStore.getInstance().getPropertyDataType(
            predicate.getLeftOperand().b);
        if (DataType.BOOLEAN == leftOperandDataType || DataType.STRING == leftOperandDataType ||
            DataType.DOUBLE == leftOperandDataType) {
            return leftOperandDataType;
        }

        DataType rightOperandDataType = DataType.INT;
        if (null != predicate.getRightOperand()) {
            rightOperandDataType = TypeAndPropertyKeyStore.getInstance().getPropertyDataType(
                predicate.getRightOperand().b);
        } else if (predicate.getLiteral().contains(".")) {
            // The numerical literal was written as a floating-point.
            rightOperandDataType = DataType.DOUBLE;
        }
        return rightOperandDataType;
    }
}
