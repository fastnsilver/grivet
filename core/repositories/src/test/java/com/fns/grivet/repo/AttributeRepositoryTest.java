package com.fns.grivet.repo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fns.grivet.model.Attribute;

@ExtendWith(SpringExtension.class)
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
