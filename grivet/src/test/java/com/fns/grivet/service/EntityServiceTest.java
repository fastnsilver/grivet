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
import org.junit.Assert;
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
public class EntityServiceTest {

    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    
    @Autowired
    private ClassRegistryService classRegistryService;
    
    @Autowired
    private EntityService entityService;
    
    @Before
    public void setUp() throws IOException {
        Resource r = resolver.getResource("classpath:TestType.json");
        String json = FileUtils.readFileToString(r.getFile());
        JSONObject payload = new JSONObject(json);
        classRegistryService.register(payload);
    }
    
    @Test
    public void testCreateThenFindByType() throws IOException {
        Resource r = resolver.getResource("classpath:TestTypeData.json");
        String json = FileUtils.readFileToString(r.getFile());
        JSONObject payload = new JSONObject(json);
        
        entityService.create("TestType", payload);
        
        String result = entityService.findByCreatedTime("TestType", LocalDateTime.now().minusSeconds(3), LocalDateTime.now(), null);
        JSONArray resultAsJsonArray = new JSONArray(result);
        JsonAssert.assertJsonEquals(payload.toString(), resultAsJsonArray.get(0).toString());
    }
    
    @Test
    public void testSchemaLinkAndValidationSuccessThenUnlink() throws IOException {
        Resource r = resolver.getResource("classpath:TestTypeSchema.json");
        String jsonSchema = FileUtils.readFileToString(r.getFile());
        JSONObject schemaObj = new JSONObject(jsonSchema);
        com.fns.grivet.model.Class c = classRegistryService.linkSchema(schemaObj);
        String type = c.getName();
        Assert.assertEquals("TestType", type);
        Assert.assertTrue(c.isValidatable());
        JsonAssert.assertJsonEquals(c.getJsonSchema(), jsonSchema);
        
        r = resolver.getResource("classpath:TestTypeData.json");
        String json = FileUtils.readFileToString(r.getFile());
        JSONObject payload = new JSONObject(json);
        
        entityService.create("TestType", payload);
        
        String result = entityService.findByCreatedTime("TestType", LocalDateTime.now().minusSeconds(3), LocalDateTime.now(), null);
        JSONArray resultAsJsonArray = new JSONArray(result);
        JsonAssert.assertJsonEquals(payload.toString(), resultAsJsonArray.get(0).toString());
        
        c = classRegistryService.unlinkSchema(type);
        Assert.assertFalse(c.isValidatable());
        Assert.assertNull(c.getJsonSchema());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testTypeNotRegistered() throws IOException {
        JSONObject payload = new JSONObject();
        entityService.create("TestType2", payload);
    }
    
    @Test(expected = NullPointerException.class) 
    public void testTypePayloadIsNull() {
        entityService.create("TestType", null);
    }
    
    @After
    public void tearDown() {
        classRegistryService.deregister("TestType");
    }

    // TODO Need to author tests for types with iso_date and iso_date_time member variables
}
