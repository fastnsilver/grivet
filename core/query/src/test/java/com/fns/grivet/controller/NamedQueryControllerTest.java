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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fns.grivet.query.NamedQuery;
import com.fns.grivet.service.NamedQueryService;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;

import java.io.InputStream;

public class NamedQueryControllerTest {

    @Mock
    private NamedQueryService service;
        
    @InjectMocks
    private NamedQueryController controller;
    
    private ObjectMapper mapper = new ObjectMapper();
    
    private MockMvc mockMvc;
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }
    
    @Test
    public void testThatCreateSucceeds_sprocWithParams() throws Exception {
        InputStream is = ClassLoader.class.getResourceAsStream("/TestSprocQuery.json");
        String json = IOUtils.toString(is);
        NamedQuery query = mapper.readValue(json, NamedQuery.class);
        doCallRealMethod().when(service).create(query);
        mockMvc.perform(
                    post("/query")
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
                    post("/query")
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
                    post("/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/query/getAttributesCreatedToday"));
    }
    
    @Test
    public void testThatCreateSucceeds_forSelect_withQueryTypeSupplied() throws Exception {
        InputStream is = ClassLoader.class.getResourceAsStream("/TestSelectQuery3.json");
        String json = IOUtils.toString(is);
        NamedQuery query = mapper.readValue(json, NamedQuery.class);
        doCallRealMethod().when(service).create(query);
        mockMvc.perform(
                    post("/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/query/getClassesCreatedToday"));
    }
    
    @Test
    public void testThatCreateSucceeds_forSproc_withQueryTypeSupplied() throws Exception {
        InputStream is = ClassLoader.class.getResourceAsStream("/TestSprocQuery2.json");
        String json = IOUtils.toString(is);
        NamedQuery query = mapper.readValue(json, NamedQuery.class);
        doCallRealMethod().when(service).create(query);
        mockMvc.perform(
                    post("/query")
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
                get("/query/getAttributesCreatedToday")
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
                get("/query?showAll=true")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
                .andExpect(content().json(response.toString()));
    }

    @Test
    public void testThatDeleteSucceeds() throws Exception {
        mockMvc.perform(
                delete("/query/TestSelectQuery")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());
    }

}
