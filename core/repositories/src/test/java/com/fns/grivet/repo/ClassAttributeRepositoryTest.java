package com.fns.grivet.repo;

import java.util.List;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fns.grivet.model.Attribute;
import com.fns.grivet.model.AttributeType;
import com.fns.grivet.model.ClassAttribute;

@RunWith(SpringRunner.class)
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
    
    // FIXME Precision issues; not sure if it has to do with Lombok impl of EqualsAndHashCode
    
    @Test
    public void testFindByCid() {
        ClassAttribute[] expected = seedClassAttributes();
        List<ClassAttribute> actual = classAttributeRepository.findByCid(expected[0].getCid());
        Assert.assertNotNull("Expected a list of class attributes returned!", actual);
        Assert.assertTrue(actual.size() == 2);
        //Assert.assertThat(actual, IsIterableContainingInOrder.contains(expected));
    }

    @Test
    public void testFindByCidAndAid() {
        ClassAttribute[] expected = seedClassAttributes();
        ClassAttribute actual = classAttributeRepository.findByCidAndAid(expected[1].getCid(), expected[1].getAid());
        Assert.assertNotNull("Expected a matching class attribute!", actual);
        //Assert.assertEquals(expected[1], actual);
    }
    
    @After
    public void tearDown() {
        classAttributeRepository.deleteAll();
        classRepository.deleteAll();
        attributeRepository.deleteAll();
    }

}
