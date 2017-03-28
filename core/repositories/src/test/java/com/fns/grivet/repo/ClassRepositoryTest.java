package com.fns.grivet.repo;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fns.grivet.model.Class;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ClassRepositoryTest {

    @Autowired
    private ClassRepository repo;
    
    @Test
    public void testFindByName() {
        Class detached = 
                Class.builder()
                    .name("PersonalDetails")
                    .description("The personal details of an individual.").build();
        Class expected = repo.save(detached);
        Class actual = repo.findByName("PersonalDetails");
        Assert.assertNotNull("Expected a matching Class!", actual);
        Assert.assertEquals(expected, actual);
    }
    
    @After
    public void tearDown() {
        repo.deleteAll();
    }

}
