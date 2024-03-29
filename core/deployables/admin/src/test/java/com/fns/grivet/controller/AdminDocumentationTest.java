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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.charset.Charset;

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

import com.fns.grivet.AdminInit;
import com.fns.grivet.repo.AttributeRepository;
import com.fns.grivet.repo.ClassAttributeRepository;
import com.fns.grivet.repo.ClassRepository;
import com.fns.grivet.service.ClassRegistryService;
import com.fns.grivet.service.SchemaService;

@ExtendWith(value = { RestDocumentationExtension.class })
@SpringBootTest(classes = AdminInit.class)
public class AdminDocumentationTest {

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
		context.getBean(ClassAttributeRepository.class).deleteAll();
		context.getBean(ClassRepository.class).deleteAll();
		context.getBean(AttributeRepository.class).deleteAll();
	}

	@Test
	public void defineType() {
		try {
			mockMvc.perform(post("/definition").contentType(MediaType.APPLICATION_JSON).content(payload("TestType")))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", "/definition/TestType"));
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void defineTypes() {
		try {
			mockMvc
				.perform(post("/definitions").contentType(MediaType.APPLICATION_JSON)
					.content(payload("TestMultipleTypes")))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location[1]", "/definition/Contact"))
				.andExpect(header().string("Location[2]", "/definition/Course"));
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void undefineType() {
		try {
			defineType("TestType");
			mockMvc.perform(delete("/definition/TestType").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void getTypeDefinition() {
		try {
			defineType("TestType");
			mockMvc.perform(get("/definition/TestType").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(payload("TestType")));
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void allTypeDefinitions() {
		try {
			defineTypes("TestMultipleTypes");
			mockMvc.perform(get("/definitions").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(payload("TestMultipleTypes")));
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void linkSchema() {
		try {
			defineType("TestType");
			mockMvc.perform(post("/schema").contentType(MediaType.APPLICATION_JSON).content(payload("TestTypeSchema")))
				.andExpect(status().isOk())
				.andExpect(content().string(
						"JSON Schema for type [TestType] linked!  Store requests for this type will be validated henceforth!"));
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void unlinkSchema() {
		try {
			defineType("TestType");
			linkSchema("TestTypeSchema");
			mockMvc.perform(delete("/schema/TestType").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string(
						"JSON Schema for type [TestType] unlinked!  Store requests for this type will no longer be validated!"));
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
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

}
