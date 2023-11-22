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
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

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
import com.fns.grivet.repo.AttributeRepository;
import com.fns.grivet.repo.AttributeTypeRepository;
import com.fns.grivet.repo.ClassAttributeRepository;
import com.fns.grivet.repo.ClassRepository;

@Service
public class ClassRegistryService {

	private static final String TYPE = "type";

	private static final String DESCRIPTION = "description";

	private static final String ATTRIBUTES = "attributes";

	private final AttributeRepository attributeRepository;

	private final AttributeTypeRepository attributeTypeRepository;

	private final ClassRepository classRepository;

	private final ClassAttributeRepository classAttributeRepository;

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
		var c = classRepository.findByName(type);
		if (c != null) {
			classRepository.delete(c);
		}
	}

	@Transactional
	public String register(JSONObject payload) {
		var type = payload.getString(TYPE);
		var description = payload.optString(DESCRIPTION);
		var persistentClass = classRepository.findByName(type);
		if (persistentClass == null) {
			var createdTime = LocalDateTime.now();
			var detachedClass = com.fns.grivet.model.Class.builder().name(type).description(description).build();
			detachedClass.setCreatedTime(createdTime);
			persistentClass = classRepository.save(detachedClass);
			var attributes = payload.getJSONObject(ATTRIBUTES);
			Set<String> attributeNames = attributes.keySet();
			Assert.notEmpty(attributeNames,
					"[%s] must declare at least one attribute in registration request!".formatted(type));
			Attribute persistentAttribute = null;
			String at = null;
			AttributeType attributeType = null;
			// TODO create an expiring cache of attribute names for faster lookups
			// (friendlier database use)
			for (String attributeName : attributeNames) {
				persistentAttribute = attributeRepository.findByName(attributeName);
				if (persistentAttribute == null) {
					var detachedAttribute = Attribute.builder().name(attributeName).build();
					detachedAttribute.setCreatedTime(createdTime);
					persistentAttribute = attributeRepository.save(detachedAttribute);
				}
				at = attributes.getString(attributeName);
				Assert.notNull(at, "[%s].[%s] must declare an attribute type in registration request!".formatted(type,
						attributeName));
				attributeType = attributeTypeRepository.findByType(at);
				Assert.notNull(attributeType, "Attribute type [%s] is not supported!".formatted(at));
				var detachedClassAttribute = ClassAttribute.builder()
					.cid(persistentClass.getId())
					.aid(persistentAttribute.getId())
					.tid(attributeType.getId())
					.build();
				detachedClassAttribute.setCreatedTime(createdTime);
				classAttributeRepository.save(detachedClassAttribute);
			}
		}
		else {
			throw new DuplicateKeyException("Type [%s] already registered!".formatted(type));
		}
		return type;
	}

	@Transactional(readOnly = true)
	public JSONObject get(String type) {
		var c = classRepository.findByName(type);
		Assert.notNull(c, "Type [%s] is not registered!".formatted(type));
		var result = new JSONObject();
		result.put("type", type);
		var description = c.getDescription();
		result.put("description", description != null ? description : "");
		List<ClassAttribute> cas = classAttributeRepository.findByCid(c.getId());
		Assert.notEmpty(cas, "Type [%s] does not have any attributes registered!".formatted(type));
		var attributes = new JSONObject();
		result.put("attributes", attributes);
		Attribute a = null;
		AttributeType at = null;
		for (ClassAttribute ca : cas) {
			a = attributeRepository.findById(ca.getAid()).get();
			Assert.notNull(a, "Attribute id [%s] is not registered!".formatted(ca.getAid()));
			at = attributeTypeRepository.findById(ca.getTid());
			Assert.notNull(a, "Attribute Type id [%s] is not registered!".formatted(ca.getTid()));
			attributes.put(a.getName(), at.getType());
		}
		return result;
	}

	@Transactional(readOnly = true)
	public JSONArray all() {
		var result = new JSONArray();
		Iterable<com.fns.grivet.model.Class> iterable = classRepository.findAll();
		Assert.notNull(iterable, "No types are registered!");
		StreamSupport.stream(iterable.spliterator(), false).forEach(c -> result.put(get(c.getName())));
		return result;
	}

}
