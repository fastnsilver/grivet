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
package com.fns.grivet.service;

import java.io.IOException;
import java.time.LocalDateTime;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.fns.grivet.TestInit;

import net.javacrumbs.jsonunit.JsonAssert;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestInit.class)
public class ExtendedEntityServiceTest {

    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    
    @Autowired
    private ClassRegistryService classRegistryService;
    
    @Autowired
    private EntityService entityService;
    
    @Before
    public void setUp() throws IOException {
        Resource r = resolver.getResource("classpath:TestType2.json");
        String json = FileUtils.readFileToString(r.getFile());
        JSONObject payload = new JSONObject(json);
        classRegistryService.register(payload);
    }
    
    @Test
    public void testCreateThenFindByType() throws IOException {
        Resource r = resolver.getResource("classpath:TestTypeData2.json");
        String json = FileUtils.readFileToString(r.getFile());
        JSONObject payload = new JSONObject(json);
        
        entityService.create("TestType2", payload);
        
        String result = entityService.findByCreatedTime("TestType2", LocalDateTime.now().minusSeconds(3), LocalDateTime.now(), null);
        JSONArray resultAsJsonArray = new JSONArray(result);
        JsonAssert.assertJsonEquals(payload.toString(), resultAsJsonArray.get(0).toString());
    }
    
        
    @After
    public void tearDown() {
        classRegistryService.deregister("TestType2");
    }

}
