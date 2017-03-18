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
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.commons.io.IOUtils;
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

import com.fns.grivet.PersistInit;
import com.fns.grivet.service.EntityService;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PersistInit.class)
public class EntityControllerTest2 {

	@Autowired
	private MockMvc mockMvc;
    
	@MockBean
    private EntityService service;
    
    
    @Test
    public void testThatStoringASingleTestTypeSucceeds() {
    	try {
	        String json = IOUtils.toString(ClassLoader.class.getResourceAsStream("/TestTypeData.json"));
	        doNothing().when(service).create("TestType", new JSONObject(json));
	        mockMvc.perform(
	                    post("/api/v1/type")
	                        .header("Type", "TestType")
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .content(json)
	                )
	                .andExpect(status().isCreated());
    	} catch (Exception e) {
        	fail(e.getMessage());
        }
    }

    // TODO More testing; unhappy path cases
}
