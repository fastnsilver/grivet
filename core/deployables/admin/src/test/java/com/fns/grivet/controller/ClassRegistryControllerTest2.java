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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fns.grivet.TestInit;
import com.fns.grivet.service.ClassRegistryService;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestInit.class)
public class ClassRegistryControllerTest2 {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ClassRegistryService service;


	@Test
	public void testThatDeleteSucceeds() throws Exception {
		mockMvc.perform(
				delete("/register/TestType")
				.contentType(MediaType.APPLICATION_JSON)
				)
		.andExpect(status().isNoContent());
	}

	@Test
	public void testThatGetSucceeds() throws Exception {
		String json = IOUtils.toString(ClassLoader.class.getResourceAsStream("/TestType.json"));
		when(service.get("TestType")).thenReturn(new JSONObject(json));
		mockMvc.perform(
				get("/register/TestType")
				.contentType(MediaType.APPLICATION_JSON)
				)
		.andExpect(status().isOk())
		.andExpect(content().json(json));
	}

	@Test
	public void testThatAllSucceeds() throws Exception {
		String json = IOUtils.toString(ClassLoader.class.getResourceAsStream("/TestType.json"));
		String arr = String.format("[%s]", json);
		when(service.all()).thenReturn(new JSONArray(arr));
		mockMvc.perform(
				get("/register?showAll=true")
				.contentType(MediaType.APPLICATION_JSON)
				)
		.andExpect(status().isOk())
		.andExpect(content().json(arr));
	}

	// TODO More testing; unhappy path cases

}
