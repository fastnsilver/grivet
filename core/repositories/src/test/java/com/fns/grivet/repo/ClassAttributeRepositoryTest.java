package com.fns.grivet.repo;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fns.grivet.model.Attribute;
import com.fns.grivet.model.AttributeType;
import com.fns.grivet.model.ClassAttribute;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ClassAttributeRepositoryTest {
    
    @Autowired
    private AttributeRepository attributeRepository;
    
    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private ClassAttributeRepository classAttributeRepository;
    
    private ClassAttribute[] seedClassAttributes() {
        Attribute a1 = attributeRepository.save(new Attribute("birthday", null));
        Attribute a2 = attributeRepository.save(new Attribute("socialSecurityNumber", null));
        com.fns.grivet.model.Class c1 = classRepository.save(new com.fns.grivet.model.Class("PersonDetails", "Sensitive details about an individual", null));
        ClassAttribute[] expected = { 
                new ClassAttribute(c1.getId(), a1.getId(), AttributeType.ISO_DATE.getId(), null),
                new ClassAttribute(c1.getId(), a2.getId(), AttributeType.VARCHAR.getId(), null)
                };
        classAttributeRepository.save(expected[0]);
        classAttributeRepository.save(expected[1]);
        return expected;
    }
    
    @Test
    public void testFindByCid() {
        ClassAttribute[] expected = seedClassAttributes();
        List<ClassAttribute> actual = classAttributeRepository.findByCid(1);
        Assert.assertNotNull("Expected a list of class attributes returned!", actual);
        Assert.assertTrue(actual.size() == 2);
        Assert.assertEquals(Arrays.asList(expected), actual);
    }

    @Test
    public void testFindByCidAndAid() {
        ClassAttribute[] expected = seedClassAttributes();
        ClassAttribute actual = classAttributeRepository.findByCidAndAid(expected[1].getCid(), expected[1].getAid());
        Assert.assertNotNull("Expected a matching class attribute!", actual);
        Assert.assertEquals(expected[1], actual);
    }

}
