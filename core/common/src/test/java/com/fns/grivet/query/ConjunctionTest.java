package com.fns.grivet.query;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

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
	public void testThatFromValueIsGood() {
		String[] values = { "AND", "OR" };
		Arrays.stream(values).forEach(v -> Assert.assertNotNull(Conjunction.fromValue(v)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testThatFromValueThrowsExceptionWhenNull() {
		Conjunction.fromValue(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testThatFromValueThrowsExceptionWhenUnknown() {
		Conjunction.fromValue("foo");
	}

}
