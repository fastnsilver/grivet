package com.fns.globaldb.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fns.globaldb.model.Attribute;
import com.fns.globaldb.model.AttributeType;
import com.fns.globaldb.model.ClassAttribute;
import com.fns.globaldb.model.EntityAttributeValue;
import com.fns.globaldb.model.ValueHelper;
import com.fns.globaldb.query.DynamicQuery;
import com.fns.globaldb.repo.AttributeRepository;
import com.fns.globaldb.repo.AttributeTypeRepository;
import com.fns.globaldb.repo.ClassAttributeRepository;
import com.fns.globaldb.repo.ClassRepository;
import com.fns.globaldb.repo.EntityRepository;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.google.common.collect.Lists;


@Service
public class EntityService {

    private final ClassRepository classRepository;
    private final ClassAttributeRepository classAttributeRepository;
    private final AttributeRepository attributeRepository;
    private final AttributeTypeRepository attributeTypeRepository;
    private final EntityRepository entityRepository;
    private final SchemaValidator schemaValidator;
    
    @Autowired
    public EntityService(ClassRepository classRepository, ClassAttributeRepository classAttributeRepository,
            AttributeRepository attributeRepository, AttributeTypeRepository attributeTypeRepository,
            EntityRepository entityRepository, SchemaValidator schemaValidator) {
        this.classRepository = classRepository;
        this.classAttributeRepository = classAttributeRepository;
        this.attributeRepository = attributeRepository;
        this.attributeTypeRepository = attributeTypeRepository;
        this.entityRepository = entityRepository;
        this.schemaValidator = schemaValidator;
    }

    @Transactional
    public void create(String type, JSONObject payload) {
        com.fns.globaldb.model.Class c = classRepository.findByName(type);
        Assert.notNull(c, String.format("Type [%s] is not registered!", type));
        if (c.isValidatable()) {
            ProcessingReport report = schemaValidator.validate(type, payload);
            Assert.isTrue(report.isSuccess(), String.format("Cannot store [%s]! Type does not conform to JSON Schema definition.\n %s", type, report.toString()));
        }
        Set<String> keys = payload.keySet();
        Assert.notEmpty(keys, String.format("Type [%s] must declare at least one attribute in create request!", type));
        Object val = null;
        Attribute a = null;
        ClassAttribute ca = null;
        AttributeType at = null;
        Long eid = entityRepository.id(c.getId());
        // TODO create an expiring cache of attribute names for faster lookups (friendlier database use)
        for (String key: keys) {
            val = payload.get(key);
            a = attributeRepository.findByName(key);
            Assert.notNull(a, String.format("Attribute [%s] is not registered!", key));
            ca = classAttributeRepository.findByCidAndAid(c.getId(), a.getId());
            Assert.notNull(ca, String.format("[%s] is not a valid attribute of [%s]", key, type));
            at = attributeTypeRepository.findOne(ca.getTid());
            Assert.notNull(at, String.format("Attribute type [%s] is not supported!", at));
            entityRepository.save(eid, a, at, val);
        }
    }
    
    @Transactional(readOnly=true)
    public String get(String type, LocalDateTime createdTimeStart, LocalDateTime createdTimeEnd, Set<Entry<String, String[]>> requestParameters) throws JsonProcessingException {
        com.fns.globaldb.model.Class c = classRepository.findByName(type);
        Assert.notNull(c, "Type [%s] is not registered!");
        List<ClassAttribute> cas = classAttributeRepository.findByCid(c.getId());
        List<Attribute> attributes = Lists.newArrayList(attributeRepository.findAll());
        Map<Integer, Integer> attributeToAttributeTypeMap = cas.stream().collect(Collectors.toMap(ClassAttribute::getAid, ClassAttribute::getTid));
        Map<String, Integer> attributeNameToAttributeIdMap = attributes.stream().collect(Collectors.toMap(Attribute::getName, Attribute::getId));
        DynamicQuery query = new DynamicQuery(requestParameters, attributeToAttributeTypeMap, attributeNameToAttributeIdMap);
        List<EntityAttributeValue> rows = null;
        if (query.hasConstraints()) {
            rows = entityRepository.executeDynamicQuery(c.getId(), query);
        } else {
            rows = entityRepository.find(c.getId(), createdTimeStart, createdTimeEnd);
        }
        return mapRows(attributeToAttributeTypeMap, rows);
    }

    protected String mapRows(Map<Integer, Integer> attributeToAttributeTypeMap, List<EntityAttributeValue> rows) {
        JSONArray jsonArray = new JSONArray();
        Long current = null; 
        Long previous = null;
        JSONObject jsonObject = null;
        for (EntityAttributeValue row: rows) {
            current = row.getId();
            if (current != previous) {
                jsonObject = new JSONObject();
                jsonArray.put(jsonObject);
            }
            Integer tid = attributeToAttributeTypeMap.get(row.getAttributeId());
            jsonObject.put(row.getAttributeName(), ValueHelper.getValue(attributeTypeRepository.findOne(tid), row.getAttributeValue()));
            previous = current;
        }
        return jsonArray.toString();
    }
}
