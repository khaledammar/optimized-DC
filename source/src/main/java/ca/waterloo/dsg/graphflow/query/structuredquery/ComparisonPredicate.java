package ca.waterloo.dsg.graphflow.query.structuredquery;

import ca.waterloo.dsg.graphflow.exception.IncorrectDataTypeException;
import ca.waterloo.dsg.graphflow.util.datatype.DataType;
import ca.waterloo.dsg.graphflow.util.datatype.RuntimeComparator.ComparisonOperator;
import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * A class representing a filter on the results of a MATCH query. Consists of two operands (a, b)
 * and a comparison operator(op) where the operands are a literal or alternatively a relation or
 * variable name used in the MATCH query followed by a property key. The results of the MATCH
 * query satisfy a {@link Predicate} encapsulating the predicate (a op b).
 */
public class ComparisonPredicate extends QueryPredicate {
    private Pair<String, String> rightOperand;
    private String literal;
    private ComparisonOperator comparisonOperator;

    public Pair<String, String> getRightOperand() {
        return rightOperand;
    }

    public void setRightOperand(Pair<String, String> rightOperand) {
        this.rightOperand = rightOperand;
    }

    public String getLiteral() {
        return literal;
    }

    public void setLiteral(String constant) {
        this.literal = constant;
    }

    public ComparisonOperator getComparisonOperator() {
        return comparisonOperator;
    }

    public void setComparisonOperator(ComparisonOperator comparisonOperator) {
        this.comparisonOperator = comparisonOperator;
    }

    public void invertComparisonOperator() {
        switch (comparisonOperator) {
            case GREATER_THAN:
                comparisonOperator = ComparisonOperator.LESS_THAN;
                break;
            case LESS_THAN:
                comparisonOperator = ComparisonOperator.GREATER_THAN;
                break;
            case GREATER_THAN_OR_EQUAL:
                comparisonOperator = ComparisonOperator.LESS_THAN_OR_EQUAL;
                break;
            case LESS_THAN_OR_EQUAL:
                comparisonOperator = ComparisonOperator.GREATER_THAN_OR_EQUAL;
                break;
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder()
            .append("{ " + leftOperand.a + "." + leftOperand.b)
            .append(" " + comparisonOperator.name() + " ")
            .append(" ");
        if (null != rightOperand) {
            stringBuilder.append(rightOperand.a + "." + rightOperand.b + "}");
        } else {
            stringBuilder.append(literal + "}");
        }
        return stringBuilder.toString();
    }

    /**
     * See {@link QueryPredicate#getAllVariables()}.
     */
    @Override
    public List<Pair<String, String>> getAllVariables() {
        List<Pair<String, String>> variables = new ArrayList<>();
        variables.add(leftOperand);
        if (null != rightOperand) {
            variables.add(rightOperand);
        }
        return variables;
    }

    /**
     * See {@link QueryPredicate#validateTypes()}.
     */
    @Override
    public void validateTypes() {
        Pair<Short, DataType> leftOperandKeyAndDataType = getKeyAndDataTypePair(leftOperand.b);
        if (PredicateType.COMPARATIVE_CLAUSE_TWO_PROPERTY_KEY_OPERANDS == predicateType) {
            Pair<Short, DataType> rightOperandKeyAndDataType = getKeyAndDataTypePair(
                rightOperand.b);
            if ((!isNumeric(leftOperandKeyAndDataType.b) || !isNumeric(rightOperandKeyAndDataType.
                b)) && leftOperandKeyAndDataType.b != rightOperandKeyAndDataType.b) {
                throw new IncorrectDataTypeException("DataType Mismatch - The left operand " +
                    leftOperand.a + "." + leftOperand.b + " is of data type " +
                    leftOperandKeyAndDataType.b + " and the right operand " + rightOperand.a + "." +
                    rightOperand.b + " is of data type " + rightOperandKeyAndDataType.b + ".");
            }
        } else {
            DataType.assertValueCanBeCastToDataType(leftOperandKeyAndDataType.b, literal);
        }
    }
}
