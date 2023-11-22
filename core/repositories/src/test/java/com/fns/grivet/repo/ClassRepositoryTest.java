package com.fns.grivet.repo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fns.grivet.model.Class;

@SpringBootTest
public class ClassRepositoryTest {

	@Autowired
	private ClassRepository repo;

	@Test
	public void testFindByName() {
		var detached = Class.builder()
			.name("PersonalDetails")
			.description("The personal details of an individual.")
			.build();
		var expected = repo.save(detached);
		var actual = repo.findByName("PersonalDetails");
		Assertions.assertNotNull(actual, "Expected a matching Class!");
		Assertions.assertEquals(expected, actual);
	}

	@AfterEach
	public void tearDown() {
		repo.deleteAll();
	}

}
