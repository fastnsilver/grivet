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
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;

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
import org.springframework.util.LinkedMultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fns.grivet.QueryInit;
import com.fns.grivet.query.NamedQuery;
import com.fns.grivet.service.NamedQueryService;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(classes = QueryInit.class)
public class NamedQueryControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
    @MockBean
    private NamedQueryService service;
        
    private ObjectMapper mapper = new ObjectMapper();
    
    
    @Test
    public void testThatCreateSprocWithParamsSucceeds() {
    	try {
	        InputStream is = ClassLoader.class.getResourceAsStream("/TestSprocQuery.json");
	        String json = IOUtils.toString(is);
	        NamedQuery query = mapper.readValue(json, NamedQuery.class);
	        doCallRealMethod().when(service).create(query);
	        mockMvc.perform(
	                post("/api/v1/query")
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .content(json)
	                )
	                .andExpect(status().isNoContent());
    	} catch (Exception e) {
        	fail(e.getMessage());
        }
    }
    
    @Test
    public void testThatCreateSelectWithParamsSucceeds() {
    	try {
	        InputStream is = ClassLoader.class.getResourceAsStream("/TestSelectQuery.json");
	        String json = IOUtils.toString(is);
	        NamedQuery query = mapper.readValue(json, NamedQuery.class);
	        doCallRealMethod().when(service).create(query);
	        mockMvc.perform(
	                post("/api/v1/query")
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .content(json)
	                )
	                .andExpect(status().isNoContent());
    	} catch (Exception e) {
        	fail(e.getMessage());
        }
    }
    
    @Test
    public void testThatCreateSelectNoParamsSucceeds() {
    	try {
	        InputStream is = ClassLoader.class.getResourceAsStream("/TestSelectQuery2.json");
	        String json = IOUtils.toString(is);
	        NamedQuery query = mapper.readValue(json, NamedQuery.class);
	        doCallRealMethod().when(service).create(query);
	        mockMvc.perform(
	                post("/api/v1/query")
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .content(json)
	                )
	                .andExpect(status().isCreated())
	                .andExpect(header().string("Location", "/namedQuery/getAttributesCreatedToday"));
    	} catch (Exception e) {
        	fail(e.getMessage());
        }
    }
    
    @Test
    public void testThatCreateForSelectWithQueryTypeSuppliedSucceeds() {
    	try {
	        InputStream is = ClassLoader.class.getResourceAsStream("/TestSelectQuery3.json");
	        String json = IOUtils.toString(is);
	        NamedQuery query = mapper.readValue(json, NamedQuery.class);
	        doCallRealMethod().when(service).create(query);
	        mockMvc.perform(
	                post("/api/v1/query")
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .content(json)
	                )
	                .andExpect(status().isCreated())
	                .andExpect(header().string("Location", "/namedQuery/getClassesCreatedToday"));
    	} catch (Exception e) {
        	fail(e.getMessage());
        }
    }
    
    @Test
    public void testThatCreateForSprocWithQueryTypeSuppliedSucceeds() {
    	try {
	        InputStream is = ClassLoader.class.getResourceAsStream("/TestSprocQuery2.json");
	        String json = IOUtils.toString(is);
	        NamedQuery query = mapper.readValue(json, NamedQuery.class);
	        doCallRealMethod().when(service).create(query);
	        mockMvc.perform(
	                post("/api/v1/query")
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .content(json)
	                )
	                .andExpect(status().isNoContent());
    	} catch (Exception e) {
        	fail(e.getMessage());
        }
    }
    
    @Test
    public void testThatGetSucceeds() {
        try {
	    	InputStream is = ClassLoader.class.getResourceAsStream("/TestSelectQuery2-sample-response.json");
	        String response = IOUtils.toString(is);
	        when(service.get("getAttributesCreatedToday", new LinkedMultiValueMap<String, String>())).thenReturn(response);
	        mockMvc.perform(
	                get("/api/v1/query/getAttributesCreatedToday")
	                    .contentType(MediaType.APPLICATION_JSON)
	            )
	            .andExpect(status().isOk())
	            .andExpect(content().json(response));
        } catch (Exception e) {
        	fail(e.getMessage());
        }
    }
    

    @Test
    public void testThatAllSucceeds() {
    	try {
	        String json = IOUtils.toString(ClassLoader.class.getResourceAsStream("/TestSelectQuery2.json"));
	        JSONArray response = new JSONArray();
	        response.put(new JSONObject(json));
	        when(service.all()).thenReturn(response);
	        mockMvc.perform(
	                get("/api/v1/queries")
	                    .contentType(MediaType.APPLICATION_JSON)
	            )
	            .andExpect(status().isOk())
	                .andExpect(content().json(response.toString()));
    	} catch (Exception e) {
        	fail(e.getMessage());
        }
    }

    @Test
    public void testThatDeleteSucceeds() {
    	try {
	        mockMvc.perform(
	                delete("/api/v1/query/TestSelectQuery")
	                    .contentType(MediaType.APPLICATION_JSON)
	            )
	            .andExpect(status().isNoContent());
    	} catch (Exception e) {
        	fail(e.getMessage());
        }
    }

}
