package com.fns.grivet.repo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fns.grivet.model.Attribute;
import com.fns.grivet.model.AttributeType;
import com.fns.grivet.model.EntityAttributeValue;
import com.fns.grivet.query.DynamicQuery;

public interface EntityRepository {

	public void save(UUID eid, Attribute attribute, AttributeType attributeType, Object value,
			LocalDateTime createdTime);

	public List<EntityAttributeValue> findByCreatedTime(UUID cid, LocalDateTime createdTimeStart, LocalDateTime createdTimeEnd);

	public UUID getClassIdForEntityId(UUID eid);

	public List<EntityAttributeValue> findByIdEntity(UUID eid);

	public void delete(UUID eid);
	
	public void deleteAll();

	public List<EntityAttributeValue> findAllEntitiesByCid(UUID cid);

	public List<EntityAttributeValue> executeDynamicQuery(UUID cid, DynamicQuery query);

}
