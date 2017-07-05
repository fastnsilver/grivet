package com.fns.grivet.repo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fns.grivet.model.AttributeType;


@RunWith(SpringRunner.class)
@SpringBootTest
public class AttributeTypeRepositoryTest {

    @Autowired
    private AttributeTypeRepository repo;
    
    @Test
    public void testFindByType() {
        AttributeType expected = AttributeType.VARCHAR;
        AttributeType actual = repo.findByType("varchar");
        Assert.assertNotNull("Expected matching attribute type!", actual);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testfindById() {
        AttributeType expected = AttributeType.ISO_INSTANT;
        AttributeType actual = repo.findById(4);
        Assert.assertNotNull("Expected matching attribute type!", actual);
        Assert.assertEquals(expected, actual);
    }

}
