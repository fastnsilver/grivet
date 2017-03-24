package com.fns.grivet.repo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fns.grivet.model.Class;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ClassRepositoryTest {

    @Autowired
    private ClassRepository repo;
    
    @Test
    public void testFindByName() {
        Class expected = new Class("PersonalDetails", "The personal details of an individual.", null);
        repo.save(expected);
        Class actual = repo.findByName("PersonalDetails");
        Assert.assertNotNull("Expected a matching Class!", actual);
        Assert.assertEquals(expected, actual);
    }

}
