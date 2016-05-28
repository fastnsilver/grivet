package com.fns.grivet.query;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class OperatorTest {

    @Test
    public void testGetName() {
        String actual, expected;

        actual = Operator.STARTS_WITH.getName();
        expected = "startsWith";
        Assert.assertEquals(expected, actual);

        actual = Operator.ENDS_WITH.getName();
        expected = "endsWith";
        Assert.assertEquals(expected, actual);

        actual = Operator.CONTAINS.getName();
        expected = "contains";
        Assert.assertEquals(expected, actual);

        actual = Operator.EQUALS.getName();
        expected = "equals";
        Assert.assertEquals(expected, actual);

        actual = Operator.NOT_EQUAL_TO.getName();
        expected = "notEqualTo";
        Assert.assertEquals(expected, actual);

        actual = Operator.GREATER_THAN.getName();
        expected = "greaterThan";
        Assert.assertEquals(expected, actual);

        actual = Operator.LESS_THAN.getName();
        expected = "lessThan";
        Assert.assertEquals(expected, actual);

        actual = Operator.GREATER_THAN_OR_EQUAL_TO.getName();
        expected = "greaterThanOrEqualTo";
        Assert.assertEquals(expected, actual);

        actual = Operator.LESS_THAN_OR_EQUAL_TO.getName();
        expected = "lessThanOrEqualTo";
        Assert.assertEquals(expected, actual);

        actual = Operator.BETWEEN.getName();
        expected = "between";
        Assert.assertEquals(expected, actual);

        actual = Operator.IN.getName();
        expected = "in";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetSymbol() {
        String actual, expected;

        actual = Operator.STARTS_WITH.getSymbol();
        expected = "LIKE";
        Assert.assertEquals(expected, actual);

        actual = Operator.ENDS_WITH.getSymbol();
        expected = "LIKE";
        Assert.assertEquals(expected, actual);

        actual = Operator.CONTAINS.getSymbol();
        expected = "LIKE";
        Assert.assertEquals(expected, actual);

        actual = Operator.EQUALS.getSymbol();
        expected = "=";
        Assert.assertEquals(expected, actual);

        actual = Operator.NOT_EQUAL_TO.getSymbol();
        expected = "<>";
        Assert.assertEquals(expected, actual);

        actual = Operator.GREATER_THAN.getSymbol();
        expected = ">";
        Assert.assertEquals(expected, actual);

        actual = Operator.LESS_THAN.getSymbol();
        expected = "<";
        Assert.assertEquals(expected, actual);

        actual = Operator.GREATER_THAN_OR_EQUAL_TO.getSymbol();
        expected = ">=";
        Assert.assertEquals(expected, actual);

        actual = Operator.LESS_THAN_OR_EQUAL_TO.getSymbol();
        expected = "<=";
        Assert.assertEquals(expected, actual);

        actual = Operator.BETWEEN.getSymbol();
        expected = "BETWEEN";
        Assert.assertEquals(expected, actual);

        actual = Operator.IN.getSymbol();
        expected = "IN";
        Assert.assertEquals(expected, actual);

    }

    @Test
    public void testFromValue_good() {
        String[] values = { "startsWith", "endsWith", "contains", "equals", "notEqualTo", "greaterThan",
                "greaterThanOrEqualTo", "lessThan", "lessThanOrEqualTo", "between", "in" };
        Arrays.stream(values).forEach(v -> Assert.assertNotNull(Operator.fromValue(v)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromValue_null() {
        Operator.fromValue(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromValue_unknown() {
        Operator.fromValue("foo");
    }

}
