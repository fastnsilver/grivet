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
import com.fns.grivet.TestInit;
import com.fns.grivet.query.NamedQuery;
import com.fns.grivet.service.NamedQueryService;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestInit.class)
public class NamedQueryControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
    @MockBean
    private NamedQueryService service;
        
    private ObjectMapper mapper = new ObjectMapper();
    
    
    @Test
    public void testThatCreateSucceeds_sprocWithParams() throws Exception {
        InputStream is = ClassLoader.class.getResourceAsStream("/TestSprocQuery.json");
        String json = IOUtils.toString(is);
        NamedQuery query = mapper.readValue(json, NamedQuery.class);
        doCallRealMethod().when(service).create(query);
        mockMvc.perform(
                post("/namedQuery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isNoContent());
    }
    
    @Test
    public void testThatCreateSucceeds_selectWithParams() throws Exception {
        InputStream is = ClassLoader.class.getResourceAsStream("/TestSelectQuery.json");
        String json = IOUtils.toString(is);
        NamedQuery query = mapper.readValue(json, NamedQuery.class);
        doCallRealMethod().when(service).create(query);
        mockMvc.perform(
                post("/namedQuery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isNoContent());
    }
    
    @Test
    public void testThatCreateSucceeds_selectNoParams() throws Exception {
        InputStream is = ClassLoader.class.getResourceAsStream("/TestSelectQuery2.json");
        String json = IOUtils.toString(is);
        NamedQuery query = mapper.readValue(json, NamedQuery.class);
        doCallRealMethod().when(service).create(query);
        mockMvc.perform(
                post("/namedQuery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/namedQuery/getAttributesCreatedToday"));
    }
    
    @Test
    public void testThatCreateSucceeds_forSelect_withQueryTypeSupplied() throws Exception {
        InputStream is = ClassLoader.class.getResourceAsStream("/TestSelectQuery3.json");
        String json = IOUtils.toString(is);
        NamedQuery query = mapper.readValue(json, NamedQuery.class);
        doCallRealMethod().when(service).create(query);
        mockMvc.perform(
                post("/namedQuery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/namedQuery/getClassesCreatedToday"));
    }
    
    @Test
    public void testThatCreateSucceeds_forSproc_withQueryTypeSupplied() throws Exception {
        InputStream is = ClassLoader.class.getResourceAsStream("/TestSprocQuery2.json");
        String json = IOUtils.toString(is);
        NamedQuery query = mapper.readValue(json, NamedQuery.class);
        doCallRealMethod().when(service).create(query);
        mockMvc.perform(
                post("/namedQuery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isNoContent());
    }
    
    @Test
    public void testThatGetSucceeds() throws Exception {
        InputStream is = ClassLoader.class.getResourceAsStream("/TestSelectQuery2-sample-response.json");
        String response = IOUtils.toString(is);
        when(service.get("getAttributesCreatedToday", new LinkedMultiValueMap<String, String>())).thenReturn(response);
        mockMvc.perform(
                get("/namedQuery/getAttributesCreatedToday")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(content().json(response));
    }
    

    @Test
    public void testThatAllSucceeds() throws Exception {
        String json = IOUtils.toString(ClassLoader.class.getResourceAsStream("/TestSelectQuery2.json"));
        JSONArray response = new JSONArray();
        response.put(new JSONObject(json));
        when(service.all()).thenReturn(response);
        mockMvc.perform(
                get("/namedQuery?showAll=true")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
                .andExpect(content().json(response.toString()));
    }

    @Test
    public void testThatDeleteSucceeds() throws Exception {
        mockMvc.perform(
                delete("/namedQuery/TestSelectQuery")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());
    }

}
