package com.fns.globaldb.repo;

import java.time.LocalDateTime;
import java.util.List;

import com.fns.globaldb.model.Attribute;
import com.fns.globaldb.model.AttributeType;
import com.fns.globaldb.model.EntityAttributeValue;
import com.fns.globaldb.query.DynamicQuery;

public interface EntityRepository {

    public Long id(Integer cid);
    public void save(Long eid, Attribute attribute, AttributeType attributeType, Object value);
    public List<EntityAttributeValue> find(Integer cid, LocalDateTime createdTimeStart, LocalDateTime createdTimeEnd);
    public List<EntityAttributeValue> executeDynamicQuery(Integer cid, DynamicQuery query);
    
}
