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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fns.grivet.ApplicationTests;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

public class ClassRegistryControllerTest extends ApplicationTests {

    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private ResourceLoader resolver;

    private MockMvc mockMvc;
    

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    
    @Test
    public void testThatRegisteringASingleTypeResultsInABadRequest() throws Exception {
        Resource r = resolver.getResource("classpath:BadTestType.json");
        String json = IOUtils.toString(r.getInputStream());
        mockMvc.perform(
                post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest());
    }
    
    @Test
    public void testThatRegisteringASingleTypeSucceeds() throws Exception {
        Resource r = resolver.getResource("classpath:TestType.json");
        String json = IOUtils.toString(r.getInputStream());
        mockMvc.perform(
                post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/register/TestType"));
        
        mockMvc.perform(
                delete("/register/TestType")
                    .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());
    }

    @Test
    public void testThatRegisteringMultipleTypesSucceeds() throws Exception {
        Resource r = resolver.getResource("classpath:TestMultipleTypes.json");
        String json = IOUtils.toString(r.getInputStream());
        mockMvc.perform(
                post("/register/types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                    )
                    .andExpect(status().isCreated())
                .andExpect(header().string("Location[1]", "/register/Contact"))
                .andExpect(header().string("Location[2]", "/register/Course"));
        
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
    }
        
    // TODO More testing; unhappy path cases

}
