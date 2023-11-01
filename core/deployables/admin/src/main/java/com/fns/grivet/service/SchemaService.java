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

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.fns.grivet.repo.ClassRepository;

@Service
public class SchemaService {

	private static final String ID = "id";
	private static final String SCHEMA = "$schema";
	private static final String TYPE = "type";
	private static final String PROPERTIES = "properties";

	private final ClassRepository classRepository;

	@Autowired
	public SchemaService(ClassRepository classRepository) {
		this.classRepository = classRepository;
	}

	public boolean isJsonSchema(JSONObject payload) {
		boolean result = false;
		String schema = payload.optString(SCHEMA);
		String type = payload.optString(TYPE);
		String id = payload.optString(ID);
		String props = payload.optString(PROPERTIES);
		if ("http://json-schema.org/draft-04/schema#".equals(schema)
				&& "object".equals(type)
				&& StringUtils.isNotBlank(id)
				&& StringUtils.isNotBlank(props)) {
			result = true;
		}
		return result;
	}

	@Transactional
	public com.fns.grivet.model.Class linkSchema(JSONObject payload) {
		String type = payload.getString(ID);
		Assert.notNull(type, "JSON Schema must declare an id!");
		com.fns.grivet.model.Class c = classRepository.findByName(type);
		Assert.notNull(c, "Type [%s] must be registered before linking a JSON Schema!".formatted(type));
		c.setValidatable(true);
		c.setJsonSchema(payload.toString());
		c.setUpdatedTime(LocalDateTime.now());
		classRepository.save(c);
		return c;
	}

	@Transactional
	public com.fns.grivet.model.Class unlinkSchema(String type) {
		com.fns.grivet.model.Class c = classRepository.findByName(type);
		Assert.notNull(c, "Type [%s] must be registered before unlinking a JSON Schema!".formatted(type));
		c.setValidatable(false);
		c.setJsonSchema(null);
		classRepository.save(c);
		return c;
	}

}
