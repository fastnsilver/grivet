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

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;

import com.fns.grivet.PersistInit;

import net.javacrumbs.jsonunit.JsonAssert;

@SpringBootTest(classes = PersistInit.class)
public class EntityServiceTest {

	@Autowired
	private ResourceLoader resolver;

	@Autowired
	private SchemaService schemaService;

	@Autowired
	private ClassRegistryService classRegistryService;

	@Autowired
	private EntityService entityService;

	protected void registerType(String type) throws IOException {
		var r = resolver.getResource("classpath:%s.json".formatted(type));
		var json = IOUtils.toString(r.getInputStream(), Charset.defaultCharset());
		var payload = new JSONObject(json);
		classRegistryService.register(payload);
	}

	@Test
	public void testCreateThenFindByType() throws IOException {
		registerType("TestType");
		var r = resolver.getResource("classpath:TestTypeData.json");
		var json = IOUtils.toString(r.getInputStream(), Charset.defaultCharset());
		var expected = new JSONObject(json);

		var eid = entityService.create("TestType", expected);

		var result = entityService.findById(eid);
		var actual = new JSONObject(result);
		JsonAssert.assertJsonEquals(expected.toString(), actual.toString());
	}

	@Test
	public void testCreateThenFindByTypeVariant() throws IOException {
		registerType("TestType2");
		var r = resolver.getResource("classpath:TestTypeData2.json");
		var json = IOUtils.toString(r.getInputStream(), Charset.defaultCharset());
		var payload = new JSONObject(json);

		entityService.create("TestType2", payload);

		var result = entityService.findByCreatedTime("TestType2", LocalDateTime.now().minusSeconds(3),
				LocalDateTime.now(), null);
		var resultAsJsonArray = new JSONArray(result);
		JsonAssert.assertJsonEquals(payload, resultAsJsonArray.get(0));
	}

	@Test
	public void testSchemaLinkAndValidationSuccessThenUnlink() throws IOException {
		registerType("TestType");
		var r = resolver.getResource("classpath:TestTypeSchema.json");
		var jsonSchema = IOUtils.toString(r.getInputStream(), Charset.defaultCharset());
		var schemaObj = new JSONObject(jsonSchema);
		var c = schemaService.linkSchema(schemaObj);
		var type = c.getName();
		Assertions.assertEquals("TestType", type);
		Assertions.assertTrue(c.isValidatable());
		JsonAssert.assertJsonEquals(c.getJsonSchema(), jsonSchema);

		r = resolver.getResource("classpath:TestTypeData.json");
		var json = IOUtils.toString(r.getInputStream(), Charset.defaultCharset());
		var payload = new JSONObject(json);

		entityService.create("TestType", payload);

		var result = entityService.findByCreatedTime("TestType", LocalDateTime.now().minusSeconds(3),
				LocalDateTime.now(), null);
		var resultAsJsonArray = new JSONArray(result);
		JsonAssert.assertJsonEquals(payload.toString(), resultAsJsonArray.get(0).toString());

		c = schemaService.unlinkSchema(type);
		Assertions.assertFalse(c.isValidatable());
		Assertions.assertNull(c.getJsonSchema());
	}

	@Test
	public void testTypeNotRegistered() throws IOException {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			var payload = new JSONObject();
			entityService.create("TestType", payload);
		});
	}

	@Test
	public void testTypePayloadIsNull() throws IOException {
		Assertions.assertThrows(NullPointerException.class, () -> {
			registerType("TestType");
			entityService.create("TestType", null);
		});
	}

	@AfterEach
	public void tearDown() {
		String[] types = { "TestType", "TestType2" };
		Arrays.stream(types).forEach(type -> classRegistryService.deregister(type));
	}

}
