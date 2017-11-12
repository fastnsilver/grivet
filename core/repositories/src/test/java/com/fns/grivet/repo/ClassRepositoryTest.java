package com.fns.grivet.repo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fns.grivet.model.Class;

@ExtendWith(SpringExtension.class)
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
        Assertions.assertNotNull(actual, "Expected a matching Class!");
        Assertions.assertEquals(expected, actual);
    }
    
    @AfterEach
    public void tearDown() {
        repo.deleteAll();
    }

}
