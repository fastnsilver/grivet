/*
 * Copyright 2015 - Chris Phillipson
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fns.grivet.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fns.grivet.model.Attribute;
import com.fns.grivet.model.AttributeType;
import com.fns.grivet.model.ClassAttribute;
import com.fns.grivet.model.EntityAttributeValue;
import com.fns.grivet.query.DynamicQuery;
import com.fns.grivet.repo.AttributeRepository;
import com.fns.grivet.repo.AttributeTypeRepository;
import com.fns.grivet.repo.ClassAttributeRepository;
import com.fns.grivet.repo.ClassRepository;
import com.fns.grivet.repo.EntityRepository;
import com.fns.grivet.util.ValueHelper;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.google.common.collect.Lists;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;


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
        com.fns.grivet.model.Class c = classRepository.findByName(type);
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
    public String findByCreatedTime(String type, LocalDateTime createdTimeStart, LocalDateTime createdTimeEnd,
            Map<String, String[]> parameters) throws JsonProcessingException {
        com.fns.grivet.model.Class c = classRepository.findByName(type);
        Assert.notNull(c, "Type [%s] is not registered!");
        List<ClassAttribute> cas = classAttributeRepository.findByCid(c.getId());
        List<Attribute> attributes = Lists.newArrayList(attributeRepository.findAll());
        Map<Integer, Integer> attributeToAttributeTypeMap = cas.stream().collect(Collectors.toMap(ClassAttribute::getAid, ClassAttribute::getTid));
        Map<String, Integer> attributeNameToAttributeIdMap = attributes.stream().collect(Collectors.toMap(Attribute::getName, Attribute::getId));
        Set<Entry<String, String[]>> params = parameters == null ? null : parameters.entrySet();
        DynamicQuery query = new DynamicQuery(params, attributeToAttributeTypeMap, attributeNameToAttributeIdMap);
        List<EntityAttributeValue> rows = null;
        if (query.hasConstraints()) {
            rows = entityRepository.executeDynamicQuery(c.getId(), query);
        } else {
            rows = entityRepository.findByCreatedTime(c.getId(), createdTimeStart, createdTimeEnd);
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
            if (!current.equals(previous)) {
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
