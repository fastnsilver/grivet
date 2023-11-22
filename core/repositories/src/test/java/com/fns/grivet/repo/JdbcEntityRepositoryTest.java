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
	public JdbcEntityRepositoryTest(JdbcEntityRepository entityRepository, AttributeRepository attributeRepository,
			ClassRepository classRepository, ClassAttributeRepository classAttributeRepository) {
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
		var c = classRepository.save(Class.builder().name("foo").description("Foo type").build());
		var now = LocalDateTime.now();
		var eid = entityRepository.newId(c.getId(), now);
		Assertions.assertTrue(eid != null && eid > 0, "Entity should have an identifier!");
	}

	@Test
	public void testSaveAndFindEntityById() {
		var detachedAttribute = Attribute.builder().name("canSpeak").description("Is able to speak?").build();
		var canSpeak = attributeRepository.save(detachedAttribute);
		var c = classRepository.save(Class.builder().name("human").description("A human").build());
		classAttributeRepository.save(ClassAttribute.builder()
			.aid(canSpeak.getId())
			.cid(c.getId())
			.tid(AttributeType.BOOLEAN.getId())
			.build());
		var now = LocalDateTime.now();
		var eid = entityRepository.newId(c.getId(), now);
		entityRepository.save(eid, canSpeak, AttributeType.BOOLEAN, true, now);

		List<EntityAttributeValue> eavList = entityRepository.findByEntityId(eid);
		Assertions.assertTrue(eavList.size() == 1, "List of entity attribute values should contain only one item!");
		var item = eavList.get(0);
		Assertions.assertEquals(now.truncatedTo(ChronoUnit.SECONDS),
				item.getCreatedTime().truncatedTo(ChronoUnit.SECONDS));
		Assertions.assertEquals(eid, item.getId());
		Assertions.assertEquals("1", item.getAttributeValue());
		Assertions.assertEquals(canSpeak.getName(), item.getAttributeName());
	}

	@Test
	public void testFindByCreatedTime() {
		var detachedAttribute = Attribute.builder().name("maxRpm").description("Maximum rotations per minute.").build();
		var maxRpm = attributeRepository.save(detachedAttribute);
		var c = classRepository.save(Class.builder().name("engine").description("An engine").build());
		classAttributeRepository.save(
				ClassAttribute.builder().aid(maxRpm.getId()).cid(c.getId()).tid(AttributeType.INTEGER.getId()).build());
		var now = LocalDateTime.now();
		var eid = entityRepository.newId(c.getId(), now);
		entityRepository.save(eid, maxRpm, AttributeType.INTEGER, 7500, now);

		List<EntityAttributeValue> eavList = entityRepository.findByCreatedTime(c.getId(),
				now.minus(1L, ChronoUnit.SECONDS), now.plus(1L, ChronoUnit.SECONDS));
		Assertions.assertTrue(eavList.size() == 1, "List of entity attribute values should contain only one item!");
		var item = eavList.get(0);
		Assertions.assertEquals(now.truncatedTo(ChronoUnit.SECONDS),
				item.getCreatedTime().truncatedTo(ChronoUnit.SECONDS));
		Assertions.assertEquals(eid, item.getId());
		Assertions.assertEquals("7500", item.getAttributeValue());
		Assertions.assertEquals(maxRpm.getName(), item.getAttributeName());
	}

	@Test
	public void testGetClassIdForEntityId() {
		var detachedAttribute = Attribute.builder().name("lotSize").description("Size of lot in square feet.").build();
		var lotSize = attributeRepository.save(detachedAttribute);
		var c = classRepository.save(Class.builder().name("home-details").description("Home details").build());
		classAttributeRepository.save(ClassAttribute.builder()
			.aid(lotSize.getId())
			.cid(c.getId())
			.tid(AttributeType.INTEGER.getId())
			.build());
		var now = LocalDateTime.now();
		var eid = entityRepository.newId(c.getId(), now);
		entityRepository.save(eid, lotSize, AttributeType.INTEGER, 2250, now);

		var cid = entityRepository.getClassIdForEntityId(eid);
		Assertions.assertEquals(c.getId(), cid);
	}

	@Test
	public void testDelete() {
		var detachedAttribute1 = Attribute.builder().name("lotSize").description("Size of lot in square feet.").build();
		var lotSize = attributeRepository.save(detachedAttribute1);
		var detachedAttribute2 = Attribute.builder().name("bedrooms").description("Number of bedrooms.").build();
		var bedrooms = attributeRepository.save(detachedAttribute2);
		var c = classRepository.save(Class.builder().name("home-details").description("Home details").build());
		classAttributeRepository.save(ClassAttribute.builder()
			.aid(lotSize.getId())
			.cid(c.getId())
			.tid(AttributeType.INTEGER.getId())
			.build());
		classAttributeRepository.save(ClassAttribute.builder()
			.aid(bedrooms.getId())
			.cid(c.getId())
			.tid(AttributeType.INTEGER.getId())
			.build());
		var now = LocalDateTime.now();
		var eid1 = entityRepository.newId(c.getId(), now);
		entityRepository.save(eid1, lotSize, AttributeType.INTEGER, 7500, now);
		entityRepository.save(eid1, bedrooms, AttributeType.INTEGER, 4, now);
		var eid2 = entityRepository.newId(c.getId(), now);
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
		var detachedAttribute1 = Attribute.builder().name("lotSize").description("Size of lot in square feet.").build();
		var lotSize = attributeRepository.save(detachedAttribute1);
		var detachedAttribute2 = Attribute.builder().name("bedrooms").description("Number of bedrooms.").build();
		var bedrooms = attributeRepository.save(detachedAttribute2);
		var c = classRepository.save(Class.builder().name("home-details").description("Home details").build());
		classAttributeRepository.save(ClassAttribute.builder()
			.aid(lotSize.getId())
			.cid(c.getId())
			.tid(AttributeType.INTEGER.getId())
			.build());
		classAttributeRepository.save(ClassAttribute.builder()
			.aid(bedrooms.getId())
			.cid(c.getId())
			.tid(AttributeType.INTEGER.getId())
			.build());
		var now = LocalDateTime.now();
		var eid1 = entityRepository.newId(c.getId(), now);
		entityRepository.save(eid1, lotSize, AttributeType.INTEGER, 7500, now);
		entityRepository.save(eid1, bedrooms, AttributeType.INTEGER, 4, now);
		var eid2 = entityRepository.newId(c.getId(), now);
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
		var detachedAttribute1 = Attribute.builder().name("orderQuantity").description("Quantity ordered.").build();
		var orderQuantity = attributeRepository.save(detachedAttribute1);
		var detachedAttribute2 = Attribute.builder().name("productId").description("Product SKU.").build();
		var productId = attributeRepository.save(detachedAttribute2);
		var c = classRepository
			.save(Class.builder().name("order-line-item").description("Order line item details.").build());
		classAttributeRepository.save(ClassAttribute.builder()
			.aid(orderQuantity.getId())
			.cid(c.getId())
			.tid(AttributeType.INTEGER.getId())
			.build());
		classAttributeRepository.save(ClassAttribute.builder()
			.aid(productId.getId())
			.cid(c.getId())
			.tid(AttributeType.INTEGER.getId())
			.build());
		var now = LocalDateTime.now();
		var eid1 = entityRepository.newId(c.getId(), now);
		entityRepository.save(eid1, orderQuantity, AttributeType.INTEGER, 67, now);
		entityRepository.save(eid1, productId, AttributeType.INTEGER, 123, now);
		var eid2 = entityRepository.newId(c.getId(), now);
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
