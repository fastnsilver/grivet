package com.fns.grivet.query;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OperatorTest {

	@Test
	public void testAllVariationsONameForEnum() {
		String actual, expected;

		actual = Operator.STARTS_WITH.getName();
		expected = "startsWith";
		Assertions.assertEquals(expected, actual);

		actual = Operator.ENDS_WITH.getName();
		expected = "endsWith";
		Assertions.assertEquals(expected, actual);

		actual = Operator.CONTAINS.getName();
		expected = "contains";
		Assertions.assertEquals(expected, actual);

		actual = Operator.EQUALS.getName();
		expected = "equals";
		Assertions.assertEquals(expected, actual);

		actual = Operator.NOT_EQUAL_TO.getName();
		expected = "notEqualTo";
		Assertions.assertEquals(expected, actual);

		actual = Operator.GREATER_THAN.getName();
		expected = "greaterThan";
		Assertions.assertEquals(expected, actual);

		actual = Operator.LESS_THAN.getName();
		expected = "lessThan";
		Assertions.assertEquals(expected, actual);

		actual = Operator.GREATER_THAN_OR_EQUAL_TO.getName();
		expected = "greaterThanOrEqualTo";
		Assertions.assertEquals(expected, actual);

		actual = Operator.LESS_THAN_OR_EQUAL_TO.getName();
		expected = "lessThanOrEqualTo";
		Assertions.assertEquals(expected, actual);

		actual = Operator.BETWEEN.getName();
		expected = "between";
		Assertions.assertEquals(expected, actual);

		actual = Operator.IN.getName();
		expected = "in";
		Assertions.assertEquals(expected, actual);
	}

	@Test
	public void testAllVariationsOfSymbolForEnum() {
		String actual, expected;

		actual = Operator.STARTS_WITH.getSymbol();
		expected = "LIKE";
		Assertions.assertEquals(expected, actual);

		actual = Operator.ENDS_WITH.getSymbol();
		expected = "LIKE";
		Assertions.assertEquals(expected, actual);

		actual = Operator.CONTAINS.getSymbol();
		expected = "LIKE";
		Assertions.assertEquals(expected, actual);

		actual = Operator.EQUALS.getSymbol();
		expected = "=";
		Assertions.assertEquals(expected, actual);

		actual = Operator.NOT_EQUAL_TO.getSymbol();
		expected = "<>";
		Assertions.assertEquals(expected, actual);

		actual = Operator.GREATER_THAN.getSymbol();
		expected = ">";
		Assertions.assertEquals(expected, actual);

		actual = Operator.LESS_THAN.getSymbol();
		expected = "<";
		Assertions.assertEquals(expected, actual);

		actual = Operator.GREATER_THAN_OR_EQUAL_TO.getSymbol();
		expected = ">=";
		Assertions.assertEquals(expected, actual);

		actual = Operator.LESS_THAN_OR_EQUAL_TO.getSymbol();
		expected = "<=";
		Assertions.assertEquals(expected, actual);

		actual = Operator.BETWEEN.getSymbol();
		expected = "BETWEEN";
		Assertions.assertEquals(expected, actual);

		actual = Operator.IN.getSymbol();
		expected = "IN";
		Assertions.assertEquals(expected, actual);

	}

	@Test
	public void testThatFromValueSucceedsForAllKnownNames() {
		String[] values = { "startsWith", "endsWith", "contains", "equals", "notEqualTo", "greaterThan",
				"greaterThanOrEqualTo", "lessThan", "lessThanOrEqualTo", "between", "in" };
		Arrays.stream(values).forEach(v -> Assertions.assertNotNull(Operator.fromValue(v)));
	}

	@Test
	public void testThatFromValueThrowsExceptionWhenNullNameProvided() {
	    Assertions.assertThrows(IllegalArgumentException.class, () ->  { Operator.fromValue(null); } );
	}

	@Test
	public void testFromValueThrowsExceptionWhenUnknownNameProvided() {
	    Assertions.assertThrows(IllegalArgumentException.class, () ->  { Operator.fromValue("foo"); } );
	}

}
