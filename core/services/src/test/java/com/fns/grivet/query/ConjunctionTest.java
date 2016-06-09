package com.fns.grivet.query;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class ConjunctionTest {

    @Test
    public void testGetName() {
        String actual, expected;

        actual = Conjunction.AND.getName();
        expected = "AND";
        Assert.assertEquals(expected, actual);

        actual = Conjunction.OR.getName();
        expected = "OR";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testFromValue_good() {
        String[] values = { "AND", "OR" };
        Arrays.stream(values).forEach(v -> Assert.assertNotNull(Conjunction.fromValue(v)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromValue_null() {
        Conjunction.fromValue(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromValue_unknown() {
        Conjunction.fromValue("foo");
    }

}
