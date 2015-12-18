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

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fns.grivet.query.NamedQuery;
import com.fns.grivet.service.NamedQueryService;

public class NamedQueryControllerTest {

    @Mock
    private NamedQueryService service;
        
    @InjectMocks
    private NamedQueryController controller;
    
    private ObjectMapper mapper = new ObjectMapper();
    
    private MockMvc mockMvc;
    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }
    
    @Test
    public void testThatCreateSucceeds_sprocWithParams() throws Exception {
        Resource r = resolver.getResource("classpath:TestSprocQuery.json");
        String json = FileUtils.readFileToString(r.getFile());
        NamedQuery query = mapper.readValue(r.getFile(), NamedQuery.class);
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
        Resource r = resolver.getResource("classpath:TestSelectQuery.json");
        String json = FileUtils.readFileToString(r.getFile());
        NamedQuery query = mapper.readValue(r.getFile(), NamedQuery.class);
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
        Resource r = resolver.getResource("classpath:TestSelectQuery2.json");
        String json = FileUtils.readFileToString(r.getFile());
        NamedQuery query = mapper.readValue(r.getFile(), NamedQuery.class);
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
    public void testGet() throws Exception {
        Resource r = resolver.getResource("classpath:TestSelectQuery2-sample-response.json");
        String response = FileUtils.readFileToString(r.getFile());
        when(service.get("getAttributesCreatedToday", new LinkedMultiValueMap<String, String>())).thenReturn(response);
        mockMvc.perform(
                get("/query/getAttributesCreatedToday")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(content().json(response));
    }

    @Test
    public void testAll() throws Exception {
        Resource r = resolver.getResource("classpath:TestSelectQuery2.json");
        String response = String.format("[%s]", FileUtils.readFileToString(r.getFile()));
        when(service.all()).thenReturn(new JSONArray(response));
        mockMvc.perform(
                get("/query?showAll=true")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(content().json(response));
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(
                delete("/query/TestSelectQuery")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());
    }

}
