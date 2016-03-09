package com.fns.grivet.query;

import org.junit.Assert;
import org.junit.Test;

public class ConstraintTest {

    @Test(expected = IllegalArgumentException.class)
    public void testConstraint_null() {
        new Constraint(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstraint_empty() {
        new Constraint(new String[] {});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstraint_partsLessThan3() {
        new Constraint(new String[] { "foo", "equals" });
    }

    @Test
    public void testConstraint_3parts() {
        Constraint c = new Constraint(new String[] { "foo", "equals", "bar" });
        Assert.assertEquals("foo", c.getAttributeName());
        Assert.assertEquals(Operator.EQUALS, c.getOperator());
        Assert.assertArrayEquals(new String[] { "bar" }, c.getValues());
        Assert.assertNull(c.getConjunction());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstraint_betweenBad() {
        new Constraint(new String[] { "foo", "between", "bar" });
    }

    @Test
    public void testConstraint_betweenGood() {
        Constraint c = new Constraint(new String[] { "foo", "between", "bar,noogie" });
        Assert.assertEquals("foo", c.getAttributeName());
        Assert.assertEquals(Operator.BETWEEN, c.getOperator());
        Assert.assertArrayEquals(new String[] { "bar", "noogie" }, c.getValues());
        Assert.assertNull(c.getConjunction());
    }

    @Test
    public void testConstraint_betweenGoodWithConjunction() {
        Constraint c = new Constraint(new String[] { "foo", "between", "bar,noogie", "and" });
        Assert.assertEquals("foo", c.getAttributeName());
        Assert.assertEquals(Operator.BETWEEN, c.getOperator());
        Assert.assertArrayEquals(new String[] { "bar", "noogie" }, c.getValues());
        Assert.assertEquals(Conjunction.AND, c.getConjunction());
    }

    @Test
    public void testConstraint_withConjunction() {
        Constraint c = new Constraint(new String[] { "foo", "startsWith", "f", "or" });
        Assert.assertEquals("foo", c.getAttributeName());
        Assert.assertEquals(Operator.STARTS_WITH, c.getOperator());
        Assert.assertArrayEquals(new String[] { "f" }, c.getValues());
        Assert.assertEquals(Conjunction.OR, c.getConjunction());
    }
}
