package com.fns.grivet.repo;

import java.time.LocalDateTime;
import java.util.List;

import com.fns.grivet.model.Attribute;
import com.fns.grivet.model.AttributeType;
import com.fns.grivet.model.EntityAttributeValue;
import com.fns.grivet.query.DynamicQuery;

public interface EntityRepository {

	public Long newId(Integer cid, LocalDateTime createdTime);

	public void save(Long eid, Attribute attribute, AttributeType attributeType, Object value,
			LocalDateTime createdTime);

	public List<EntityAttributeValue> findByCreatedTime(Integer cid, LocalDateTime createdTimeStart, LocalDateTime createdTimeEnd);

	public Integer getClassIdForEntityId(Long eid);

	public List<EntityAttributeValue> findByIdEntity(Long eid);

	public void delete(Long eid);
	
	public void deleteAll();

	public List<EntityAttributeValue> findAllEntitiesByCid(Integer cid);

	public List<EntityAttributeValue> executeDynamicQuery(Integer cid, DynamicQuery query);

}
