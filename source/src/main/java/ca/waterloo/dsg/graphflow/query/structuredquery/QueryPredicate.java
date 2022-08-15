package ca.waterloo.dsg.graphflow.query.structuredquery;

import ca.waterloo.dsg.graphflow.exception.IncorrectDataTypeException;
import ca.waterloo.dsg.graphflow.exception.NoSuchPropertyKeyException;
import ca.waterloo.dsg.graphflow.graph.TypeAndPropertyKeyStore;
import ca.waterloo.dsg.graphflow.util.datatype.DataType;
import ca.waterloo.dsg.graphflow.util.json.JsonOutputable;
import com.google.gson.JsonObject;
import org.antlr.v4.runtime.misc.Pair;

import java.util.List;

/**
 * Base class for {@link ComparisonPredicate} and {@link InClausePredicate} classes.
 */
public abstract class QueryPredicate implements JsonOutputable {

    /**
     * Types of predicates supported.
     */
    public enum PredicateType {
        IN_CLAUSE_ONLY_LITERALS,
        IN_CLAUSE_VARIABLES_AND_LITERALS,
        COMPARATIVE_CLAUSE_TWO_PROPERTY_KEY_OPERANDS,
        COMPARATIVE_CLAUSE_PROPERTY_KEY_AND_LITERAL_OPERANDS,
    }

    protected Pair<String, String> leftOperand;
    protected PredicateType predicateType;

    /**
     * Returns the left operand of the predicate.
     *
     * @return a {@link Pair<String, String>} representing the variable name and property of the
     * left operand.
     */
    public Pair<String, String> getLeftOperand() {
        return leftOperand;
    }

    /**
     * Sets the left operand of the predicate.
     *
     * @param leftOperand a {@link Pair<String, String>} representing the variable name and
     * property of the left operand to be set.
     */
    public void setLeftOperand(Pair<String, String> leftOperand) {
        this.leftOperand = leftOperand;
    }

    /**
     * Returns the predicate type of the query.
     *
     * @return a {@link PredicateType} representing the type of the predicate.
     */
    public PredicateType getPredicateType() {
        return predicateType;
    }

    /**
     * Sets the predicate type.
     *
     * @param predicateType the {@link PredicateType} to be set.
     */
    public void setPredicateType(PredicateType predicateType) {
        this.predicateType = predicateType;
    }

    /**
     * Returns all variables associates with a particular predicate
     *
     * @return a list of {@link Pair<String, String>} representing a list of variables and their
     * respective properties
     */
    public abstract List<Pair<String, String>> getAllVariables();

    /**
     * Validates the types of the variables and literals present in the predicate.
     *
     * @throws {@link IncorrectDataTypeException} if there is a mismatch.
     */
    public abstract void validateTypes();

    /**
     * Helper function to return whether a certain variable is numeric or not
     *
     * @return a boolean
     */
    protected static boolean isNumeric(DataType operandDataType) {
        return (DataType.INT == operandDataType || DataType.DOUBLE == operandDataType);
    }

    /**
     * Helper function to return the key and {@link DataType} of a certain property.
     *
     * @param key the property whose key and data type wish to find.
     * @return a {@link Pair<Short, DataType>} containing the property's key and data type
     */
    protected static Pair<Short, DataType> getKeyAndDataTypePair(String key) {
        Short operandKey = TypeAndPropertyKeyStore.getInstance().mapStringPropertyKeyToShort(key);
        if (null == operandKey) {
            throw new NoSuchPropertyKeyException(key);
        }
        DataType leftOperandDataType = TypeAndPropertyKeyStore.getInstance().getPropertyDataType(
            operandKey);
        return new Pair<>(operandKey, leftOperandDataType);
    }

    /**
     * Returns the JSON representation of the predicate, needed for logging.
     *
     * @return a {@link JsonObject} representing the predicate's JSON.
     */
    @Override
    public JsonObject toJson() {
        JsonObject jsonPropertyPredicate = new JsonObject();
        jsonPropertyPredicate.addProperty("Predicate", toString());
        return jsonPropertyPredicate;
    }
}

