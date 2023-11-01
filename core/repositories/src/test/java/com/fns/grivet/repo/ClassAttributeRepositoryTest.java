package com.fns.grivet.repo;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fns.grivet.model.Attribute;
import com.fns.grivet.model.AttributeType;
import com.fns.grivet.model.ClassAttribute;

@SpringBootTest
public class ClassAttributeRepositoryTest {

    @Autowired
    private AttributeRepository attributeRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private ClassAttributeRepository classAttributeRepository;

    private ClassAttribute[] seedClassAttributes() {
        Attribute a1 = attributeRepository.save(Attribute.builder().name("birthday").build());
        Attribute a2 = attributeRepository.save(Attribute.builder().name("socialSecurityNumber").build());
        com.fns.grivet.model.Class detached = 
                com.fns.grivet.model.Class.builder()
                    .name("PersonDetails")
                    .description("Sensitive details about an individual").build();
        com.fns.grivet.model.Class c1 = classRepository.save(detached);
        ClassAttribute[] expected = { 
                ClassAttribute.builder().cid(c1.getId()).aid(a1.getId()).tid(AttributeType.ISO_DATE.getId()).build(),
                ClassAttribute.builder().cid(c1.getId()).aid(a2.getId()).tid(AttributeType.VARCHAR.getId()).build()
                };
        expected[0] = classAttributeRepository.save(expected[0]);
        expected[1] = classAttributeRepository.save(expected[1]);
        return expected;
    }

    @Test
    public void testFindByCid() {
        ClassAttribute[] expected = seedClassAttributes();
        List<ClassAttribute> actual = classAttributeRepository.findByCid(expected[0].getCid());
        Assertions.assertNotNull(actual, "Expected a list of class attributes returned!");
        Assertions.assertTrue(actual.size() == 2);
        Assertions.assertEquals(Arrays.asList(expected), actual);
    }

    @Test
    public void testFindByCidAndAid() {
        ClassAttribute[] expected = seedClassAttributes();
        ClassAttribute actual = classAttributeRepository.findByCidAndAid(expected[1].getCid(), expected[1].getAid());
        Assertions.assertNotNull(actual, "Expected a matching class attribute!");
        Assertions.assertEquals(expected[1], actual);
    }

    @AfterEach
    public void tearDown() {
        classAttributeRepository.deleteAll();
        classRepository.deleteAll();
        attributeRepository.deleteAll();
    }

}
