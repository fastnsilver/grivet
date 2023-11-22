package com.fns.grivet.repo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fns.grivet.model.Attribute;

@SpringBootTest
public class AttributeRepositoryTest {

	@Autowired
	private AttributeRepository repo;

	@Test
	public void testFindByName() {
		Attribute expected = Attribute.builder().name("eyeColor").build();
		repo.save(expected);
		Attribute actual = repo.findByName("eyeColor");
		Assertions.assertNotNull(actual, "Expected a matching attribute!");
		Assertions.assertEquals(expected, actual);
	}

	@AfterEach
	public void tearDown() {
		repo.deleteAll();
	}

}
