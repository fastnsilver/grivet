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
import java.time.LocalDateTime;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringRunner;

import com.fns.grivet.TestInit;

import net.javacrumbs.jsonunit.JsonAssert;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestInit.class)
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
		Resource r = resolver.getResource(String.format("classpath:%s.json", type));
		String json = IOUtils.toString(r.getInputStream());
		JSONObject payload = new JSONObject(json);
		classRegistryService.register(payload);
	}

	@Test
	public void testCreateThenFindByType() throws IOException {
		registerType("TestType");
		Resource r = resolver.getResource("classpath:TestTypeData.json");
		String json = IOUtils.toString(r.getInputStream());
		JSONObject payload = new JSONObject(json);

		entityService.create("TestType", payload);

		String result = entityService.findByCreatedTime("TestType", LocalDateTime.now().minusSeconds(3), LocalDateTime.now(), null);
		JSONArray resultAsJsonArray = new JSONArray(result);
		JsonAssert.assertJsonEquals(payload.toString(), resultAsJsonArray.get(0).toString());
	}

	@Test
	public void testCreateThenFindByTypeVariant() throws IOException {
		registerType("TestType2");
		Resource r = resolver.getResource("classpath:TestTypeData2.json");
		String json = IOUtils.toString(r.getInputStream());
		JSONObject payload = new JSONObject(json);

		entityService.create("TestType2", payload);

		String result = entityService.findByCreatedTime("TestType2", LocalDateTime.now().minusSeconds(3), LocalDateTime.now(), null);
		JSONArray resultAsJsonArray = new JSONArray(result);
		JsonAssert.assertJsonEquals(payload.toString(), resultAsJsonArray.get(0).toString());
	}

	@Test
	public void testSchemaLinkAndValidationSuccessThenUnlink() throws IOException {
		registerType("TestType");
		Resource r = resolver.getResource("classpath:TestTypeSchema.json");
		String jsonSchema = IOUtils.toString(r.getInputStream());
		JSONObject schemaObj = new JSONObject(jsonSchema);
		com.fns.grivet.model.Class c = schemaService.linkSchema(schemaObj);
		String type = c.getName();
		Assert.assertEquals("TestType", type);
		Assert.assertTrue(c.isValidatable());
		JsonAssert.assertJsonEquals(c.getJsonSchema(), jsonSchema);

		r = resolver.getResource("classpath:TestTypeData.json");
		String json = IOUtils.toString(r.getInputStream());
		JSONObject payload = new JSONObject(json);

		entityService.create("TestType", payload);

		String result = entityService.findByCreatedTime("TestType", LocalDateTime.now().minusSeconds(3), LocalDateTime.now(), null);
		JSONArray resultAsJsonArray = new JSONArray(result);
		JsonAssert.assertJsonEquals(payload.toString(), resultAsJsonArray.get(0).toString());

		c = schemaService.unlinkSchema(type);
		Assert.assertFalse(c.isValidatable());
		Assert.assertNull(c.getJsonSchema());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testTypeNotRegistered() throws IOException {
		JSONObject payload = new JSONObject();
		entityService.create("TestType", payload);
	}

	@Test(expected = NullPointerException.class)
	public void testTypePayloadIsNull() throws IOException {
		registerType("TestType");
		entityService.create("TestType", null);
	}

	@After
	public void tearDown() {
		String[] types = { "TestType", "TestType2" };
		Arrays.stream(types).forEach(type -> classRegistryService.deregister(type));
	}

}
