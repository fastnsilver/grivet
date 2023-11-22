package com.fns.grivet.repo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fns.grivet.model.AttributeType;

@SpringBootTest
public class AttributeTypeRepositoryTest {

	@Autowired
	private AttributeTypeRepository repo;

	@Test
	public void testFindByType() {
		var expected = AttributeType.VARCHAR;
		var actual = repo.findByType("varchar");
		Assertions.assertNotNull(actual, "Expected matching attribute type!");
		Assertions.assertEquals(expected, actual);
	}

	@Test
	public void testfindById() {
		var expected = AttributeType.ISO_INSTANT;
		var actual = repo.findById(4);
		Assertions.assertNotNull(actual, "Expected matching attribute type!");
		Assertions.assertEquals(expected, actual);
	}

}
