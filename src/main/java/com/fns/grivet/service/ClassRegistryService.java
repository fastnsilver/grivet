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

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.fns.grivet.model.Attribute;
import com.fns.grivet.model.AttributeType;
import com.fns.grivet.model.ClassAttribute;
import com.fns.grivet.model.User;
import com.fns.grivet.repo.AttributeRepository;
import com.fns.grivet.repo.AttributeTypeRepository;
import com.fns.grivet.repo.ClassAttributeRepository;
import com.fns.grivet.repo.ClassRepository;

@Service
public class ClassRegistryService {

    private static final String ID = "id";
    private static final String SCHEMA = "$schema";
    private static final String TYPE = "type";
    private static final String DESCRIPTION = "description";
    private static final String ATTRIBUTES = "attributes";
    private static final String PROPERTIES = "properties";
    
    private final AttributeRepository attributeRepository;
    private final AttributeTypeRepository attributeTypeRepository;
    private final ClassRepository classRepository;
    private final ClassAttributeRepository classAttributeRepository;
    
    @Autowired(required=false)
    private SecurityFacade securityFacade;
    
    @Autowired
    public ClassRegistryService(AttributeRepository attributeRepository,
            AttributeTypeRepository attributeTypeRepository, ClassRepository classRepository,
            ClassAttributeRepository classAttributeRepository) {
        this.attributeRepository = attributeRepository;
        this.attributeTypeRepository = attributeTypeRepository;
        this.classRepository = classRepository;
        this.classAttributeRepository = classAttributeRepository;
    }

    @Transactional
    // USE WITH CAUTION! Consider that w/ FK, type and all related data will be destroyed
    public void deregister(String type) {
        com.fns.grivet.model.Class c = classRepository.findByName(type);
        if (c != null) {
            classRepository.delete(c);
        }
    }
    
    @Transactional
    public String register(JSONObject payload) {
        String type = payload.getString(TYPE);
        String description = payload.optString(DESCRIPTION);
        com.fns.grivet.model.Class c = classRepository.findByName(type);
        User user = securityFacade != null ? securityFacade.getCurrentUser(): null;
        if (c == null) {
            c = classRepository.save(new com.fns.grivet.model.Class(type, description, user));
            JSONObject attributes = payload.getJSONObject(ATTRIBUTES);
            Set<String> attributeNames = attributes.keySet();
            Assert.notEmpty(attributeNames, String.format("[%s] must declare at least one attribute in registration request!", type));
            Attribute a = null;
            String at = null;
            AttributeType attributeType = null;
            // TODO create an expiring cache of attribute names for faster lookups (friendlier database use)
            for (String attributeName: attributeNames) {
                a = attributeRepository.findByName(attributeName);
                if (a == null) {
                    a = attributeRepository.save(new Attribute(attributeName, user));
                }
                at = attributes.getString(attributeName);
                Assert.notNull(at, String.format("[%s].[%s] must declare an attribute type in registration request!", type, attributeName));
                attributeType = attributeTypeRepository.findByType(at);
                Assert.notNull(attributeType, String.format("Attribute type [%s] is not supported!", at));
                classAttributeRepository.save(new ClassAttribute(c.getId(), a.getId(), attributeType.getId(), user));
            }
        } else {
            throw new DuplicateKeyException(String.format("Type [%s] already registered!", type));
        }
        return type;
    }

    @Transactional(readOnly=true)
    public JSONObject get(String type) {
        com.fns.grivet.model.Class c = classRepository.findByName(type);
        Assert.notNull(c, String.format("Type [%s] is not registered!", type));
        JSONObject result = new JSONObject();
        result.put("type", type);
        String description = c.getDescription();
        result.put("description", description != null ? description: "");
        List<ClassAttribute> cas = classAttributeRepository.findByCid(c.getId());
        Assert.notEmpty(cas, String.format("Type [%s] does not have any attributes registered!", type));
        JSONObject attributes = new JSONObject();
        result.put("attributes", attributes);
        Attribute a = null;
        AttributeType at = null;
        for (ClassAttribute ca: cas) {
            a = attributeRepository.findOne(ca.getAid());
            Assert.notNull(a, String.format("Attribute id [%s] is not registered!", ca.getAid()));
            at = attributeTypeRepository.findOne(ca.getTid());
            Assert.notNull(a, String.format("Attribute Type id [%s] is not registered!", ca.getTid()));
            attributes.put(a.getName(), at.getType());
        }
        return result;
    }
    
    @Transactional(readOnly=true)
    public JSONArray all() {
        JSONArray result = new JSONArray();
        Iterable<com.fns.grivet.model.Class> iterable = classRepository.findAll();
        Assert.notNull(iterable, "No types are registered!");
        Iterator<com.fns.grivet.model.Class> it = iterable.iterator();
        com.fns.grivet.model.Class c = null;
        while(it.hasNext()) {
           c = it.next();
           result.put(get(c.getName()));
        }
        return result;
    }
    
    public boolean isJsonSchema(JSONObject payload) {
        boolean result = false;
        String schema = payload.optString(SCHEMA);
        String type = payload.optString(TYPE);
        String id = payload.optString(ID);
        String props = payload.optString(PROPERTIES);
        if (schema.equals("http://json-schema.org/draft-04/schema#") 
                && type.equals("object") 
                && StringUtils.isNotBlank(id) 
                && StringUtils.isNotBlank(props)) {
            result = true;
        }
        return result;
    }

    @Transactional
    public com.fns.grivet.model.Class linkSchema(JSONObject payload) {
        String type = payload.getString(ID);
        com.fns.grivet.model.Class c = classRepository.findByName(type);
        Assert.notNull(c, String.format("Type [%s] must be registered before linking a JSON Schema!", type));
        c.setValidatable(true);
        c.setJsonSchema(payload.toString());
        c.setUpdatedTime(LocalDateTime.now());
        classRepository.save(c);
        return c;
    }
    
    @Transactional
    public com.fns.grivet.model.Class unlinkSchema(String type) {
        com.fns.grivet.model.Class c = classRepository.findByName(type);
        Assert.notNull(c, String.format("Type [%s] must be registered before unlinking a JSON Schema!", type));
        c.setValidatable(false);
        c.setJsonSchema(null);
        c.setUpdatedTime(LocalDateTime.now());
        User user = securityFacade != null ? securityFacade.getCurrentUser(): null;
        c.setUpdater(user);
        classRepository.save(c);
        return c;
    }
    
}
