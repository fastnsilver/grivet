package com.fns.grivet.query;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConstraintTest {

	@Test
	public void testThatConstraintThrowsExceptionWhenNull() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Constraint(null);
		});
	}

	@Test
	public void testThatConstraintThrowsExceptionWhenEmpty() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Constraint(new String[] {});
		});
	}

	@Test
	public void testThatLessThan3PartConstraintThrowsException() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Constraint(new String[] { "foo", "equals" });
		});
	}

	@Test
	public void testThatThreePartConstraintDefinitionSucceeds() {
		Constraint c = new Constraint(new String[] { "foo", "equals", "bar" });
		Assertions.assertEquals("foo", c.getAttributeName());
		Assertions.assertEquals(Operator.EQUALS, c.getOperator());
		Assertions.assertArrayEquals(new String[] { "bar" }, c.getValues());
		Assertions.assertNull(c.getConjunction());
	}

	@Test
	public void testThatImproperBetweenConstraintDefinitionFails() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Constraint(new String[] { "foo", "between", "bar" });
		});
	}

	@Test
	public void testThatProperBetweenConstraintDefinitionSucceeds() {
		Constraint c = new Constraint(new String[] { "foo", "between", "bar,noogie" });
		Assertions.assertEquals("foo", c.getAttributeName());
		Assertions.assertEquals(Operator.BETWEEN, c.getOperator());
		Assertions.assertArrayEquals(new String[] { "bar", "noogie" }, c.getValues());
		Assertions.assertNull(c.getConjunction());
	}

	@Test
	public void testThatProperBetweenConstraintDefinitionWithConjunctionSucceeds() {
		Constraint c = new Constraint(new String[] { "foo", "between", "bar,noogie", "and" });
		Assertions.assertEquals("foo", c.getAttributeName());
		Assertions.assertEquals(Operator.BETWEEN, c.getOperator());
		Assertions.assertArrayEquals(new String[] { "bar", "noogie" }, c.getValues());
		Assertions.assertEquals(Conjunction.AND, c.getConjunction());
	}

	@Test
	public void testThatConstraintWithProperOrConjunctionSucceeds() {
		Constraint c = new Constraint(new String[] { "foo", "startsWith", "f", "or" });
		Assertions.assertEquals("foo", c.getAttributeName());
		Assertions.assertEquals(Operator.STARTS_WITH, c.getOperator());
		Assertions.assertArrayEquals(new String[] { "f" }, c.getValues());
		Assertions.assertEquals(Conjunction.OR, c.getConjunction());
	}

}
