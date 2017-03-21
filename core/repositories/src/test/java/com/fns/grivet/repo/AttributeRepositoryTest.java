package com.fns.grivet.repo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fns.grivet.model.Attribute;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AttributeRepositoryTest {

    @Autowired
    private AttributeRepository repo;
    
    @Test
    public void testFindByName() {
        Attribute expected = new Attribute("eyeColor", null);
        repo.save(expected);
        Attribute actual = repo.findByName("eyeColor");
        Assert.assertNotNull("Expected a matching attribute!", actual);
        Assert.assertEquals(expected, actual);
    }

}
