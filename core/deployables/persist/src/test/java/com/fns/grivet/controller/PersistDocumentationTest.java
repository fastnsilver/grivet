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
package com.fns.grivet.controller;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fns.grivet.PersistInit;
import com.fns.grivet.repo.AttributeRepository;
import com.fns.grivet.repo.ClassAttributeRepository;
import com.fns.grivet.repo.ClassRepository;
import com.fns.grivet.repo.EntityRepository;
import com.fns.grivet.service.ClassRegistryService;
import com.fns.grivet.service.EntityService;
import com.fns.grivet.service.SchemaService;

@ExtendWith(value = { RestDocumentationExtension.class })
@SpringBootTest(classes = PersistInit.class)
public class PersistDocumentationTest {

	@Autowired
	private ResourceLoader resolver;

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	@BeforeEach
	public void setUp(RestDocumentationContextProvider restDocumentation) {
		var document = document("{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()));
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
			.apply(documentationConfiguration(restDocumentation))
			.alwaysDo(document)
			.build();
	}

	@AfterEach
	public void tearDown() {
		context.getBean(EntityRepository.class).deleteAll();
		context.getBean(ClassAttributeRepository.class).deleteAll();
		context.getBean(ClassRepository.class).deleteAll();
		context.getBean(AttributeRepository.class).deleteAll();
	}

	@Test
	public void createOne() {
		try {
			defineTypes("TestMultipleTypes");
			linkSchema("CourseSchema");
			mockMvc
				.perform(post("/type").header("Type", "Course")
					.contentType(MediaType.APPLICATION_JSON)
					.content(payload("CourseCreateData")))
				.andExpect(status().isCreated());
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void createMultiple() {
		try {
			defineTypes("TestMultipleTypes");
			mockMvc
				.perform(post("/types").contentType(MediaType.APPLICATION_JSON)
					.header("Type", "Contact")
					.content(payload("TestMultipleContactsData")))
				.andExpect(status().isCreated());
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void updateOne() {
		try {
			defineTypes("TestMultipleTypes");
			createTypes("Course", "CourseData");
			var oid = fetchAType("Course");
			mockMvc
				.perform(patch("/type").param("oid", String.valueOf(oid))
					.contentType(MediaType.APPLICATION_JSON)
					.content(payload("CourseUpdateData")))
				.andExpect(status().isOk());
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void deleteOne() {
		try {
			defineType("TestType2");
			var oid = createType("TestType2", "TestTypeData2");
			mockMvc.perform(delete("/type").param("oid", String.valueOf(oid)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	// GET (with default constraints)
	public void fetchWithDefaults() {
		try {
			defineType("TestType2");
			createType("TestType2", "TestTypeData2");
			mockMvc.perform(get("/type/TestType2").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(asArray(payload("TestTypeData2"))));
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	// GET (bounded by createdTimeStart and createdTimeEnd)
	public void fetchByTimeRange() {
		try {
			defineType("TestType2");
			createType("TestType2", "TestTypeData2");
			var createdTimeStart = LocalDateTime.now().minusMinutes(15).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			var createdTimeEnd = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			mockMvc
				.perform(get("/type/TestType2").param("createdTimeStart", createdTimeStart)
					.param("createdTimeEnd", createdTimeEnd)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(asArray(payload("TestTypeData2"))));
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	// GET (with startsWith constraint)
	public void fetchWithConstraints() {
		try {
			defineType("TestType2");
			createType("TestType2", "TestTypeData2");
			mockMvc
				.perform(get("/type/TestType2").param("c", "first-name|startsWith|J")
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(asArray(payload("TestTypeData2"))));
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	// GET (with noAudit flag set to true)
	public void fetchWithNoAudit() {
		try {
			defineTypes("TestMultipleTypes");
			createTypes("Course", "CourseData");
			mockMvc.perform(get("/type/Course").param("noAudit", "true").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(payload("CourseData")));
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void fetchOne() {
		try {
			defineType("TestType2");
			var oid = createType("TestType2", "TestTypeData2");
			mockMvc.perform(get("/type").param("oid", String.valueOf(oid)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(payload("TestTypeData2")));
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	private Long fetchAType(String type) throws JSONException, IOException {
		var classRepo = context.getBean(ClassRepository.class);
		var cid = classRepo.findByName(type).getId();
		var entityRepo = context.getBean(EntityRepository.class);
		return entityRepo.findAllEntitiesByCid(cid).iterator().next().getId();
	}

	private void createTypes(String type, String data) throws JSONException, IOException {
		var svc = context.getBean(EntityService.class);
		var json = payload(data);
		var array = new JSONArray(json);
		array.forEach(o -> svc.create(type, (JSONObject) o));
	}

	private Long createType(String type, String data) throws JSONException, IOException {
		var svc = context.getBean(EntityService.class);
		return svc.create(type, new JSONObject(payload(data)));
	}

	private void defineTypes(String definitions) throws JSONException, IOException {
		var svc = context.getBean(ClassRegistryService.class);
		var json = payload(definitions);
		var array = new JSONArray(json);
		array.forEach(o -> svc.register((JSONObject) o));
	}

	private void defineType(String definition) throws JSONException, IOException {
		var svc = context.getBean(ClassRegistryService.class);
		svc.register(new JSONObject(payload(definition)));
	}

	private void linkSchema(String schemaName) throws IOException {
		var svc = context.getBean(SchemaService.class);
		var schema = payload(schemaName);
		svc.linkSchema(new JSONObject(schema));
	}

	private String payload(String payload) throws IOException {
		var r = resolver.getResource("classpath:%s.json".formatted(payload));
		return IOUtils.toString(r.getInputStream(), Charset.defaultCharset());
	}

	private String asArray(String data) {
		var array = new JSONArray();
		var jo = new JSONObject(data);
		array.put(jo);
		return array.toString();
	}

}
