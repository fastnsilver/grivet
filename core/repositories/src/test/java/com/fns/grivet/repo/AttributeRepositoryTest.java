package com.fns.grivet.repo;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fns.grivet.model.Attribute;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AttributeRepositoryTest {

    @Autowired
    private AttributeRepository repo;
    
    @Test
    public void testFindByName() {
        Attribute expected = Attribute.builder().name("eyeColor").build();
        repo.save(expected);
        Attribute actual = repo.findByName("eyeColor");
        Assert.assertNotNull("Expected a matching attribute!", actual);
        Assert.assertEquals(expected, actual);
    }
    
    @After
    public void tearDown() {
        repo.deleteAll();
    }

}
