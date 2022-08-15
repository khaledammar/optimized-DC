package ca.waterloo.dsg.graphflow.query.structuredquery;

import ca.waterloo.dsg.graphflow.exception.IncorrectDataTypeException;
import ca.waterloo.dsg.graphflow.util.datatype.DataType;
import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing an IN clause in a query. Consists of a left operand, which is a variable
 * with a property, a list of variables with their respective properties and a list of literals.
 * This is used when checking whether the left operand is equal to at least one of the right
 * operands.
 */
public class InClausePredicate extends QueryPredicate {

    private List<Pair<String, String>> variablesWithProperty = new ArrayList<>();
    private List<String> literals = new ArrayList<>();

    /**
     * Returns a list of {@link Pair<String, String>} representing the variables with properties
     * present in the right operand of the IN clause.
     *
     * @return the list of {@link Pair<String, String>}.
     */
    public List<Pair<String, String>> getVariablesWithProperty() {
        return variablesWithProperty;
    }

    /**
     * Adds a new variable with property to the list of variables with properties.
     *
     * @param variableWithProperty the {@link Pair<String, String>} to be added.
     */
    public void addVariableWithProperty(Pair<String, String> variableWithProperty) {
        this.variablesWithProperty.add(variableWithProperty);
    }

    /**
     * Returns a list of {@link String} representing the literals present in the right operand of
     * the IN clause.
     *
     * @return the list of {@link String}.
     */
    public List<String> getLiterals() {
        return literals;
    }

    /**
     * Adds a new literal to the list of literals.
     *
     * @param literal the {@link String} to be added.
     */
    public void addLiteral(String literal) {
        this.literals.add(literal);
    }

    /**
     * See {@link QueryPredicate#getAllVariables()}.
     */
    @Override
    public List<Pair<String, String>> getAllVariables() {
        ArrayList<Pair<String, String>> result = new ArrayList<>(this.variablesWithProperty);
        result.add(leftOperand);
        return result;
    }

    /**
     * See {@link QueryPredicate#validateTypes()}.
     */
    @Override
    public void validateTypes() {
        Pair<Short, DataType> leftOperandKeyAndDataType = getKeyAndDataTypePair(leftOperand.b);
        for (Pair<String, String> variable : variablesWithProperty) {
            Pair<Short, DataType> variableKeyAndDataType = getKeyAndDataTypePair(variable.b);
            if ((!isNumeric(leftOperandKeyAndDataType.b) || !isNumeric(variableKeyAndDataType.b))
                && leftOperandKeyAndDataType.b != variableKeyAndDataType.b) {
                throw new IncorrectDataTypeException("DataType Mismatch - The left operand " +
                    leftOperand.b + " is of data type " + leftOperandKeyAndDataType.b +
                    " and the value " + variable.a + " is of " + "data type " +
                    variableKeyAndDataType.b + ".");
            }
        }
        for (String literal : literals) {
            DataType.assertValueCanBeCastToDataType(leftOperandKeyAndDataType.b, literal);
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder()
            .append("{ " + leftOperand.a + "." + leftOperand.b)
            .append(" IN [");
        for (Pair<String, String> variable : variablesWithProperty) {
            stringBuilder.append(variable.a + "." + variable.b + ", ");
        }
        for (String literal : literals) {
            stringBuilder.append(literal + ", ");
        }
        stringBuilder.append("]}");
        return stringBuilder.toString();
    }
}
