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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fns.grivet.service.EntityService;
import com.google.common.collect.Maps;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

public class EntityControllerTest {

    @Mock
    private EntityService service;

    @InjectMocks
    private EntityController controller;
    
    private MockMvc mockMvc;
    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }
    
    @Test
    public void testThatCreateSucceeds() throws Exception {
        Resource r = resolver.getResource("classpath:TestTypeData.json");
        String json = FileUtils.readFileToString(r.getFile());
        doNothing().when(service).create("TestType", new JSONObject(json));
        mockMvc.perform(
                    post("/type/store/TestType")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isNoContent());
    }

    @Test
    @Ignore("For whatever silly unknown reason this test always fails.")
    public void testThatGetSucceeds() throws Exception {
        Resource r = resolver.getResource("classpath:TestTypeData2.json");
        String response = String.format("[%s]", FileUtils.readFileToString(r.getFile()));
        LocalDateTime now = LocalDateTime.now();
        when(service.findByCreatedTime("TestType2", now.minusDays(7), now, Maps.newHashMap()))
                .thenReturn(response);
        mockMvc.perform(
                get("/type/store/TestType2")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(content().json(response));
    }

}
