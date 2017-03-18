package com.fns.grivet.query;

import org.junit.Assert;
import org.junit.Test;

public class ConstraintTest {

	@Test(expected = IllegalArgumentException.class)
	public void testThatConstraintThrowsExceptionWhenNull() {
		new Constraint(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testThatConstraintThrowsExceptionWhenEmpty() {
		new Constraint(new String[] {});
	}

	@Test(expected = IllegalArgumentException.class)
	public void testThatLessThan3PartConstraintThrowsException() {
		new Constraint(new String[] { "foo", "equals" });
	}

	@Test
	public void testThatThreePartConstraintDefinitionSucceeds() {
		Constraint c = new Constraint(new String[] { "foo", "equals", "bar" });
		Assert.assertEquals("foo", c.getAttributeName());
		Assert.assertEquals(Operator.EQUALS, c.getOperator());
		Assert.assertArrayEquals(new String[] { "bar" }, c.getValues());
		Assert.assertNull(c.getConjunction());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testThatImproperBetweenConstraintDefinitionFails() {
		new Constraint(new String[] { "foo", "between", "bar" });
	}

	@Test
	public void testThatProperBetweenConstraintDefinitionSucceeds() {
		Constraint c = new Constraint(new String[] { "foo", "between", "bar,noogie" });
		Assert.assertEquals("foo", c.getAttributeName());
		Assert.assertEquals(Operator.BETWEEN, c.getOperator());
		Assert.assertArrayEquals(new String[] { "bar", "noogie" }, c.getValues());
		Assert.assertNull(c.getConjunction());
	}

	@Test
	public void testThatProperBetweenConstraintDefinitionWithConjunctionSucceeds() {
		Constraint c = new Constraint(new String[] { "foo", "between", "bar,noogie", "and" });
		Assert.assertEquals("foo", c.getAttributeName());
		Assert.assertEquals(Operator.BETWEEN, c.getOperator());
		Assert.assertArrayEquals(new String[] { "bar", "noogie" }, c.getValues());
		Assert.assertEquals(Conjunction.AND, c.getConjunction());
	}

	@Test
	public void testThatConstraintWithProperOrConjunctionSucceeds() {
		Constraint c = new Constraint(new String[] { "foo", "startsWith", "f", "or" });
		Assert.assertEquals("foo", c.getAttributeName());
		Assert.assertEquals(Operator.STARTS_WITH, c.getOperator());
		Assert.assertArrayEquals(new String[] { "f" }, c.getValues());
		Assert.assertEquals(Conjunction.OR, c.getConjunction());
	}
}
