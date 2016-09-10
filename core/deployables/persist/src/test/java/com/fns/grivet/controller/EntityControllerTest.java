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

import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fns.grivet.TestInit;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestInit.class)
public class EntityControllerTest {

	@Autowired
	private ResourceLoader resolver;

	@Autowired
	private MockMvc mockMvc;


	private void registerTestType2() {
		try {
			Resource r = resolver.getResource("classpath:TestType2.json");
			String json = IOUtils.toString(r.getInputStream());
			mockMvc.perform(
					post("/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(json)
					)
			.andExpect(status().isCreated());
		} catch (Exception e) {
	    	fail(e.getMessage());
	    }
	}

	private void storeTestType2() {
		try {
			Resource r = resolver.getResource("classpath:TestTypeData2.json");
			String json = IOUtils.toString(r.getInputStream());
			mockMvc.perform(
					post("/store/TestType2")
					.contentType(MediaType.APPLICATION_JSON)
					.content(json)
					)
			.andExpect(status().isNoContent());
		} catch (Exception e) {
        	fail(e.getMessage());
        }
	}

	private void unregisterTestType2() {
		try {
			mockMvc.perform(
					delete("/register/TestType2")
					.contentType(MediaType.APPLICATION_JSON)
					)
			.andExpect(status().isNoContent());
		} catch (Exception e) {
        	fail(e.getMessage());
        }
	}

	private void registerMultipleTypes() {
		try {
			Resource r = resolver.getResource("classpath:TestMultipleTypes.json");
			String json = IOUtils.toString(r.getInputStream());
			mockMvc.perform(
					post("/register/types")
					.contentType(MediaType.APPLICATION_JSON)
					.content(json)
					)
			.andExpect(status().isCreated());
		} catch (Exception e) {
        	fail(e.getMessage());
        }
	}

	private void storeMultipleContacts() {
		try {
			Resource r = resolver.getResource("classpath:TestMultipleContactsData.json");
			String json = IOUtils.toString(r.getInputStream());
			mockMvc.perform(
					post("/store/batch/Contact")
					.contentType(MediaType.APPLICATION_JSON)
					.content(json)
					)
			.andExpect(status().isNoContent());
		} catch (Exception e) {
        	fail(e.getMessage());
        }
	}

	private void unregisterMultipleTypes() {
		try {
			mockMvc.perform(
					delete("/register/Contact")
					.contentType(MediaType.APPLICATION_JSON)
					)
			.andExpect(status().isNoContent());
			mockMvc.perform(
					delete("/register/Course")
					.contentType(MediaType.APPLICATION_JSON)
					)
			.andExpect(status().isNoContent());
		} catch (Exception e) {
        	fail(e.getMessage());
        }
	}

	@Test
	public void testThatGetSucceeds() {
		// register, store, fetch, then unregister
		registerTestType2();
		storeTestType2();

		try {
			Resource r = resolver.getResource("classpath:TestTypeData2.json");
			String response = String.format("[%s]", IOUtils.toString(r.getInputStream()));
	
			// GET (with default constraints)
			mockMvc.perform(
					get("/store/TestType2")
					.contentType(MediaType.APPLICATION_JSON)
					)
			.andExpect(status().isOk())
			.andExpect(content().json(response));
	
			// GET (with startsWith constraint)
			mockMvc.perform(
					get("/store/TestType2?c=first-name|startsWith|J")
					.contentType(MediaType.APPLICATION_JSON)
					)
			.andExpect(status().isOk())
			.andExpect(content().json(response));
		} catch (Exception e) {
        	fail(e.getMessage());
        }

		unregisterTestType2();
	}

	@Test
	public void testThatStoringMultipleContactsSucceeds() {
		// register, then store
		registerMultipleTypes();
		storeMultipleContacts();
		unregisterMultipleTypes();
	}

	// TODO More testing; unhappy path cases

}
