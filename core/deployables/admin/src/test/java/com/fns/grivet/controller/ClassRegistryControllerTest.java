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
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fns.grivet.AdminInit;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(classes=AdminInit.class)
public class ClassRegistryControllerTest {

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");
    
    private MockMvc mockMvc;
    
    @Autowired
    private ResourceLoader resolver;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration(this.restDocumentation))
                .alwaysDo(document("{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
                .build();
    }
    
    
    @Test
    public void testThatRegisteringASingleTypeResultsInABadRequest() {
        try {
	    	Resource r = resolver.getResource("classpath:BadTestType.json");
	        String json = IOUtils.toString(r.getInputStream());
	        mockMvc.perform(
	                post("/api/v1/definition")
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .content(json)
	                )
	                .andExpect(status().isBadRequest());
        } catch (Exception e) {
        	fail(e.getMessage());
        }
    }
    
    @Test
    public void testThatRegisteringASingleTypeSucceeds() {
    	try {
	        Resource r = resolver.getResource("classpath:TestType.json");
	        String json = IOUtils.toString(r.getInputStream());
	        mockMvc.perform(
	                post("/api/v1/definition")
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .content(json)
	                )
	                .andExpect(status().isCreated())
	                .andExpect(header().string("Location", "/api/v1/definition/TestType"));
	        
	        mockMvc.perform(
	                delete("/api/v1/definition/TestType")
	                    .contentType(MediaType.APPLICATION_JSON)
	                )
	                .andExpect(status().isNoContent());
    	} catch (Exception e) {
        	fail(e.getMessage());
        }
    }

    @Test
    public void testThatRegisteringMultipleTypesSucceeds() {
    	try {
	    	Resource r = resolver.getResource("classpath:TestMultipleTypes.json");
	        String json = IOUtils.toString(r.getInputStream());
	        mockMvc.perform(
	                post("/api/v1/definitions")
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .content(json)
	                    )
	                    .andExpect(status().isCreated())
	                .andExpect(header().string("Location[1]", "/api/v1/definition/Contact"))
	                .andExpect(header().string("Location[2]", "/api/v1/definition/Course"));
	        
	        mockMvc.perform(
	                delete("/api/v1/definition/Contact")
	                    .contentType(MediaType.APPLICATION_JSON)
	                )
	                .andExpect(status().isNoContent());
	        mockMvc.perform(
	                delete("/api/v1/definition/Course")
	                    .contentType(MediaType.APPLICATION_JSON)
	                )
	                .andExpect(status().isNoContent());
    	} catch (Exception e) {
        	fail(e.getMessage());
        }
    }
    
    /*
    @Test
    public void testThatDeleteSucceeds() {
        try {
            mockMvc.perform(
                    delete("/api/v1/definition/TestType")
                    .contentType(MediaType.APPLICATION_JSON)
                    )
            .andExpect(status().isNoContent());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testThatGetSucceeds() {
        try {
            String json = IOUtils.toString(ClassLoader.class.getResourceAsStream("/TestType.json"));
            //when(service.get("TestType")).thenReturn(new JSONObject(json));
            mockMvc.perform(
                    get("/api/v1/definition/TestType")
                    .contentType(MediaType.APPLICATION_JSON)
                    )
            .andExpect(status().isOk())
            .andExpect(content().json(json));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testThatAllSucceeds() {
        try {
            String json = IOUtils.toString(ClassLoader.class.getResourceAsStream("/TestType.json"));
            String arr = String.format("[%s]", json);
            //when(service.all()).thenReturn(new JSONArray(arr));
            mockMvc.perform(
                    get("/api/v1/definitions")
                    .contentType(MediaType.APPLICATION_JSON)
                    )
            .andExpect(status().isOk())
            .andExpect(content().json(arr));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    */
        
    // TODO More testing; unhappy path cases

}
