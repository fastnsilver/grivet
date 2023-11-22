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
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fns.grivet.QueryInit;
import com.fns.grivet.query.NamedQuery;

import io.restassured.path.json.JsonPath;

@SpringBootTest(classes = QueryInit.class)
public class NamedQueryServiceSelectTest {

	@Autowired
	private ResourceLoader resolver;

	@Autowired
	private ClassRegistryService classRegistryService;

	@Autowired
	private NamedQueryService namedQueryService;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	public void setUp() throws IOException {
		var r = resolver.getResource("classpath:TestType.json");
		var json = IOUtils.toString(r.getInputStream(), Charset.defaultCharset());
		var payload = new JSONObject(json);
		classRegistryService.register(payload);
	}

	@Test
	public void testCreateThenGetHappyPath() throws IOException {
		var r = resolver.getResource("classpath:TestSelectQuery.json");
		var json = IOUtils.toString(r.getInputStream(), Charset.defaultCharset());
		var namedQuery = objectMapper.readValue(json, NamedQuery.class);
		namedQueryService.create(namedQuery);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("createdTime", LocalDateTime.now().plusDays(1).toString());
		var result = namedQueryService.get("getAttributesCreatedBefore", params);
		String[] expected = { "bigint", "varchar", "decimal", "datetime", "int", "text", "json", "boolean" };
		List<String> actual = JsonPath.given(result).getList("name");
		Assertions.assertTrue(actual.containsAll(Arrays.asList(expected)));
	}

	@Test
	public void testGetQueryNotFound() throws IOException {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("createdTime", LocalDateTime.now().plusDays(1).toString());
			namedQueryService.get("getAttributesCreatedBefore", params);
		});
	}

	@Test
	public void testCreateThenGetParamsNotSupplied() throws IOException {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			var r = resolver.getResource("classpath:TestSelectQuery.json");
			var json = IOUtils.toString(r.getInputStream(), Charset.defaultCharset());
			var namedQuery = objectMapper.readValue(json, NamedQuery.class);
			namedQueryService.create(namedQuery);

			namedQueryService.get("getAttributesCreatedBefore", null);
		});
	}

	@Test
	public void testCreateThenGetIncorrectParamsSupplied() throws IOException {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			var r = resolver.getResource("classpath:TestSelectQuery.json");
			var json = IOUtils.toString(r.getInputStream(), Charset.defaultCharset());
			var namedQuery = objectMapper.readValue(json, NamedQuery.class);
			namedQueryService.create(namedQuery);

			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("timedCreated", LocalDateTime.now().plusDays(1).toString());
			namedQueryService.get("getAttributesCreatedBefore", params);
		});
	}

	@AfterEach
	public void tearDown() {
		classRegistryService.deregister("TestType");
		namedQueryService.delete("getAttributesCreatedBefore");
	}

}
