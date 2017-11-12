package com.fns.grivet.query;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConjunctionTest {

	@Test
	public void testGetName() {
		String actual, expected;

		actual = Conjunction.AND.getName();
		expected = "AND";
		Assertions.assertEquals(expected, actual);

		actual = Conjunction.OR.getName();
		expected = "OR";
		Assertions.assertEquals(expected, actual);
	}

	@Test
	public void testThatFromValueIsGood() {
		String[] values = { "AND", "OR" };
		Arrays.stream(values).forEach(v -> Assertions.assertNotNull(Conjunction.fromValue(v)));
	}

	@Test
	public void testThatFromValueThrowsExceptionWhenNull() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->  { Conjunction.fromValue(null); } );
	}

	@Test
	public void testThatFromValueThrowsExceptionWhenUnknown() {
	    Assertions.assertThrows(IllegalArgumentException.class, () ->  { Conjunction.fromValue("foo"); } );
	}

}
