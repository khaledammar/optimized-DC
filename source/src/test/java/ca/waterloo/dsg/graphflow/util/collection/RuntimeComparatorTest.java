package ca.waterloo.dsg.graphflow.util.collection;

import ca.waterloo.dsg.graphflow.util.datatype.RuntimeComparator;
import ca.waterloo.dsg.graphflow.util.datatype.RuntimeComparator.ComparisonOperator;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the static methods of {@link RuntimeComparator} with each supported comparison
 * operator and operands with runtime types of {@code Integer}, {@code Double}, {@code String},
 * and {@code Boolean}.
 */
public class RuntimeComparatorTest {

    @Test
    public void testBooleanOperandsEquals() {
        Assert.assertTrue(RuntimeComparator.resolveTypesAndCompare(true, true, ComparisonOperator.
            EQUALS));
    }

    @Test
    public void testBooleanOperandsGreaterThan() {
        Assert.assertTrue(RuntimeComparator.resolveTypesAndCompare(true, false, ComparisonOperator.
            GREATER_THAN));
    }

    @Test
    public void testIntegerOperandsGreaterThan() {
        Assert.assertTrue(RuntimeComparator.resolveTypesAndCompare(20, 10, ComparisonOperator.
            GREATER_THAN));
        Assert.assertFalse(RuntimeComparator.resolveTypesAndCompare(10, 20, ComparisonOperator.
            GREATER_THAN));
    }

    @Test
    public void testIntegerOperandsLessThan() {
        Assert.assertTrue(RuntimeComparator.resolveTypesAndCompare(10, 20, ComparisonOperator.
            LESS_THAN));
        Assert.assertFalse(RuntimeComparator.resolveTypesAndCompare(20, 10, ComparisonOperator.
            LESS_THAN));
    }

    @Test
    public void testIntegerOperandsGreaterThanOrEqual() {
        Assert.assertTrue(RuntimeComparator.resolveTypesAndCompare(10, 10, ComparisonOperator.
            GREATER_THAN_OR_EQUAL));
        Assert.assertFalse(RuntimeComparator.resolveTypesAndCompare(10, 20, ComparisonOperator.
            GREATER_THAN_OR_EQUAL));
    }

    @Test
    public void testIntegerOperandsLessThanOrEqual() {
        Assert.assertTrue(RuntimeComparator.resolveTypesAndCompare(10, 20, ComparisonOperator.
            LESS_THAN_OR_EQUAL));
        Assert.assertFalse(RuntimeComparator.resolveTypesAndCompare(20, 10, ComparisonOperator.
            LESS_THAN_OR_EQUAL));
    }

    @Test
    public void testIntegerOperandsNotEquals() {
        Assert.assertTrue(RuntimeComparator.resolveTypesAndCompare(20, 10, ComparisonOperator.
            NOT_EQUALS));
        Assert.assertFalse(RuntimeComparator.resolveTypesAndCompare(10, 10, ComparisonOperator.
            NOT_EQUALS));
    }

    @Test
    public void testDoubleOperandsGreaterThan() {
        Assert.assertTrue(RuntimeComparator.resolveTypesAndCompare(20.5, 10.8, ComparisonOperator.
            GREATER_THAN));
    }

    @Test
    public void testStringOperandsGreaterThan() {
        Assert.assertTrue(RuntimeComparator.resolveTypesAndCompare("zxc", "abcdzx",
            ComparisonOperator.GREATER_THAN));
        Assert.assertTrue(RuntimeComparator.resolveTypesAndCompare("abs", "abc", ComparisonOperator.
            GREATER_THAN));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidStringAndInteger() {
        RuntimeComparator.resolveTypesAndCompare("zxc", 10, ComparisonOperator.GREATER_THAN);
    }
}

