package com.fns.grivet.repo;

import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fns.grivet.model.Attribute;
import com.fns.grivet.model.AttributeType;
import com.fns.grivet.model.Class;
import com.fns.grivet.model.ClassAttribute;
import com.fns.grivet.model.EntityAttributeValue;

@SpringBootTest
public class JdbcEntityRepositoryTest {

    private JdbcEntityRepository entityRepository;

    private AttributeRepository attributeRepository;

    private ClassRepository classRepository;

    private ClassAttributeRepository classAttributeRepository;

    @Autowired
    public JdbcEntityRepositoryTest(
            JdbcEntityRepository entityRepository,
            AttributeRepository attributeRepository,
            ClassRepository classRepository,
            ClassAttributeRepository classAttributeRepository
            ) {
        this.entityRepository = entityRepository;
        this.attributeRepository = attributeRepository;
        this.classRepository = classRepository;
        this.classAttributeRepository = classAttributeRepository;
    }

    @AfterEach
    public void tearDown() {
        entityRepository.deleteAll();
        classAttributeRepository.deleteAll();
        classRepository.deleteAll();
        attributeRepository.deleteAll();
    }

    @Test
    public void testNewId() {
        Class c = classRepository.save(Class.builder().name("foo").description("Foo type").build());
        LocalDateTime now = LocalDateTime.now();
        Long eid = entityRepository.newId(c.getId(), now);
        Assertions.assertTrue(eid != null && eid > 0, "Entity should have an identifier!");
    }

    @Test
    public void testSaveAndFindEntityById() {
        Attribute detachedAttribute = Attribute.builder().name("canSpeak").description("Is able to speak?").build();
        Attribute canSpeak = attributeRepository.save(detachedAttribute);
        Class c = classRepository.save(Class.builder().name("human").description("A human").build());
        classAttributeRepository.save(ClassAttribute.builder().aid(canSpeak.getId()).cid(c.getId()).tid(AttributeType.BOOLEAN.getId()).build());
        LocalDateTime now = LocalDateTime.now();
        Long eid = entityRepository.newId(c.getId(), now);
        entityRepository.save(eid, canSpeak, AttributeType.BOOLEAN, true, now);

        List<EntityAttributeValue> eavList = entityRepository.findByEntityId(eid);
        Assertions.assertTrue(eavList.size() == 1, "List of entity attribute values should contain only one item!");
        EntityAttributeValue item = eavList.get(0);
        Assertions.assertEquals(now, item.getCreatedTime());
        Assertions.assertEquals(eid, item.getId());
        Assertions.assertEquals("1", item.getAttributeValue());
        Assertions.assertEquals(canSpeak.getName(), item.getAttributeName());
    }

    @Test
    @Disabled
    public void testFindByCreatedTime() {
        Attribute detachedAttribute = Attribute.builder().name("maxRpm").description("Maximum rotations per minute.").build();
        Attribute maxRpm = attributeRepository.save(detachedAttribute);
        Class c = classRepository.save(Class.builder().name("engine").description("An engine").build());
        classAttributeRepository.save(ClassAttribute.builder().aid(maxRpm.getId()).cid(c.getId()).tid(AttributeType.INTEGER.getId()).build());
        LocalDateTime now = LocalDateTime.now();
        Long eid = entityRepository.newId(c.getId(), now);
        entityRepository.save(eid, maxRpm, AttributeType.INTEGER, 7500L, now);

        List<EntityAttributeValue> eavList = entityRepository.findByCreatedTime(c.getId(), now.minus(1L, ChronoUnit.SECONDS), now.plus(1L, ChronoUnit.SECONDS));
        Assertions.assertTrue(eavList.size() == 1, "List of entity attribute values should contain only one item!");
        EntityAttributeValue item = eavList.get(0);
        Assertions.assertEquals(now, item.getCreatedTime());
        Assertions.assertEquals(eid, item.getId());
        Assertions.assertEquals("7500", item.getAttributeValue());
        Assertions.assertEquals(maxRpm.getName(), item.getAttributeName());
    }

    @Test
    public void testGetClassIdForEntityId() {
        Attribute detachedAttribute = Attribute.builder().name("lotSize").description("Size of lot in square feet.").build();
        Attribute lotSize = attributeRepository.save(detachedAttribute);
        Class c = classRepository.save(Class.builder().name("home-details").description("Home details").build());
        classAttributeRepository.save(ClassAttribute.builder().aid(lotSize.getId()).cid(c.getId()).tid(AttributeType.INTEGER.getId()).build());
        LocalDateTime now = LocalDateTime.now();
        Long eid = entityRepository.newId(c.getId(), now);
        entityRepository.save(eid, lotSize, AttributeType.INTEGER, 2250, now);

        Integer cid = entityRepository.getClassIdForEntityId(eid);
        Assertions.assertEquals(c.getId(), cid);
    }

    @Test
    public void testDelete() {
        Attribute detachedAttribute1 = Attribute.builder().name("lotSize").description("Size of lot in square feet.").build();
        Attribute lotSize = attributeRepository.save(detachedAttribute1);
        Attribute detachedAttribute2 = Attribute.builder().name("bedrooms").description("Number of bedrooms.").build();
        Attribute bedrooms = attributeRepository.save(detachedAttribute2);
        Class c = classRepository.save(Class.builder().name("home-details").description("Home details").build());
        classAttributeRepository.save(ClassAttribute.builder().aid(lotSize.getId()).cid(c.getId()).tid(AttributeType.INTEGER.getId()).build());
        classAttributeRepository.save(ClassAttribute.builder().aid(bedrooms.getId()).cid(c.getId()).tid(AttributeType.INTEGER.getId()).build());
        LocalDateTime now = LocalDateTime.now();
        Long eid1 = entityRepository.newId(c.getId(), now);
        entityRepository.save(eid1, lotSize, AttributeType.INTEGER, 7500, now);
        entityRepository.save(eid1, bedrooms, AttributeType.INTEGER, 4, now);
        Long eid2 = entityRepository.newId(c.getId(), now);
        entityRepository.save(eid2, lotSize, AttributeType.INTEGER, 5200, now);
        entityRepository.save(eid2, bedrooms, AttributeType.INTEGER, 3, now);

        List<EntityAttributeValue> eavList1 = entityRepository.findByEntityId(eid1);
        Assertions.assertTrue(eavList1.size() == 2, "List of entity attribute values should contain two items!");

        List<EntityAttributeValue> eavList2 = entityRepository.findByEntityId(eid2);
        Assertions.assertTrue(eavList2.size() == 2, "List of entity attribute values should contain two items!");

        entityRepository.delete(eid1);
        eavList1 = entityRepository.findByEntityId(eid1);
        Assertions.assertTrue(eavList1.size() == 0, "No entity attribute values should exist!");
    }

    @Test
    public void testDeleteAll() {
        Attribute detachedAttribute1 = Attribute.builder().name("lotSize").description("Size of lot in square feet.").build();
        Attribute lotSize = attributeRepository.save(detachedAttribute1);
        Attribute detachedAttribute2 = Attribute.builder().name("bedrooms").description("Number of bedrooms.").build();
        Attribute bedrooms = attributeRepository.save(detachedAttribute2);
        Class c = classRepository.save(Class.builder().name("home-details").description("Home details").build());
        classAttributeRepository.save(ClassAttribute.builder().aid(lotSize.getId()).cid(c.getId()).tid(AttributeType.INTEGER.getId()).build());
        classAttributeRepository.save(ClassAttribute.builder().aid(bedrooms.getId()).cid(c.getId()).tid(AttributeType.INTEGER.getId()).build());
        LocalDateTime now = LocalDateTime.now();
        Long eid1 = entityRepository.newId(c.getId(), now);
        entityRepository.save(eid1, lotSize, AttributeType.INTEGER, 7500, now);
        entityRepository.save(eid1, bedrooms, AttributeType.INTEGER, 4, now);
        Long eid2 = entityRepository.newId(c.getId(), now);
        entityRepository.save(eid2, lotSize, AttributeType.INTEGER, 5200, now);
        entityRepository.save(eid2, bedrooms, AttributeType.INTEGER, 3, now);

        List<EntityAttributeValue> eavList1 = entityRepository.findByEntityId(eid1);
        Assertions.assertTrue(eavList1.size() == 2, "List of entity attribute values should contain two items!");

        List<EntityAttributeValue> eavList2 = entityRepository.findByEntityId(eid2);
        Assertions.assertTrue(eavList2.size() == 2, "List of entity attribute values should contain two items!");

        entityRepository.deleteAll();
        eavList1 = entityRepository.findByEntityId(eid1);
        Assertions.assertTrue(eavList1.size() == 0, "No entity attribute values should exist!");
        eavList2 = entityRepository.findByEntityId(eid2);
        Assertions.assertTrue(eavList2.size() == 0, "No entity attribute values should exist!");
    }

    @Test
    public void testFindAllEntitiesByCid() {
        Attribute detachedAttribute1 = Attribute.builder().name("orderQuantity").description("Quantity ordered.").build();
        Attribute orderQuantity = attributeRepository.save(detachedAttribute1);
        Attribute detachedAttribute2 = Attribute.builder().name("productId").description("Product SKU.").build();
        Attribute productId = attributeRepository.save(detachedAttribute2);
        Class c = classRepository.save(Class.builder().name("order-line-item").description("Order line item details.").build());
        classAttributeRepository.save(ClassAttribute.builder().aid(orderQuantity.getId()).cid(c.getId()).tid(AttributeType.INTEGER.getId()).build());
        classAttributeRepository.save(ClassAttribute.builder().aid(productId.getId()).cid(c.getId()).tid(AttributeType.INTEGER.getId()).build());
        LocalDateTime now = LocalDateTime.now();
        Long eid1 = entityRepository.newId(c.getId(), now);
        entityRepository.save(eid1, orderQuantity, AttributeType.INTEGER, 67, now);
        entityRepository.save(eid1, productId, AttributeType.INTEGER, 123, now);
        Long eid2 = entityRepository.newId(c.getId(), now);
        entityRepository.save(eid2, orderQuantity, AttributeType.INTEGER, 11, now);
        entityRepository.save(eid2, productId, AttributeType.INTEGER, 456, now);

        List<EntityAttributeValue> eavList = entityRepository.findAllEntitiesByCid(c.getId());
        Assertions.assertTrue(eavList.size() == 4, "List of entity attribute values should contain four items!");
    }

    @Test
    @Disabled
    public void testExecuteDynamicQuery() {
        fail("Not yet implemented");
    }

}
