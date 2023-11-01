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
        AttributeType expected = AttributeType.VARCHAR;
        AttributeType actual = repo.findByType("varchar");
        Assertions.assertNotNull(actual, "Expected matching attribute type!");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testfindById() {
        AttributeType expected = AttributeType.ISO_INSTANT;
        AttributeType actual = repo.findById(4);
        Assertions.assertNotNull(actual, "Expected matching attribute type!");
        Assertions.assertEquals(expected, actual);
    }

}
