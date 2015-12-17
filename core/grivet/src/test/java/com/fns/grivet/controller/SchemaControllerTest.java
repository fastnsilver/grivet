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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fns.grivet.TestInit;
import com.fns.grivet.service.SchemaService;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestInit.class)
public class SchemaControllerTest {

    @Mock
    private SchemaService service;
    
    @InjectMocks
    private SchemaController controller;
    
    private MockMvc mockMvc;
    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testThatLinkSchemaSucceeds() throws Exception{
        Resource r = resolver.getResource("classpath:TestType.json");
        String json = FileUtils.readFileToString(r.getFile());
        when(service.isJsonSchema(any(JSONObject.class))).thenReturn(true);
        com.fns.grivet.model.Class clazz = new com.fns.grivet.model.Class("TestType", "A type for testing purposes", null);
        when(service.linkSchema(any(JSONObject.class))).thenReturn(clazz);
        mockMvc.perform(
                    post("/schema/link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("JSON Schema for type [TestType] linked!  Store requests for this type will be validated henceforth!"));
    }
    
    @Test
    public void testThatUnlinkSchemaSucceeds() throws Exception {
        com.fns.grivet.model.Class clazz = new com.fns.grivet.model.Class("TestType", "A type for testing purposes", null);
        when(service.unlinkSchema("TestType")).thenReturn(clazz);
        mockMvc.perform(
                    put("/schema/unlink/TestType")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("JSON Schema for type [TestType] unlinked!  Store requests for this type will no longer be validated!"));
    }
    
    // TODO More testing; unhappy path cases

}
