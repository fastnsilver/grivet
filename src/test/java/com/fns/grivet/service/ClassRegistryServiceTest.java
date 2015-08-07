package com.fns.grivet.service;

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fns.grivet.App;
import com.fns.grivet.service.ClassRegistryService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
public class ClassRegistryServiceTest {

    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    
    @Autowired
    private ClassRegistryService classRegistryService;
    
    @Test
    public void testRegisterThenGetThenAll() throws IOException {
        Resource r = resolver.getResource("classpath:TestType.json");
        String json = FileUtils.readFileToString(r.getFile());
        JSONObject payload = new JSONObject(json);
        
        String type = classRegistryService.register(payload);
        Assert.assertEquals("TestType", type);
        
        JSONObject jo = classRegistryService.get("TestType");
        Assert.assertEquals(payload.toString(), jo.toString());
        
        JSONArray ja = classRegistryService.all();
        Assert.assertEquals(payload.toString(), ja.get(0).toString());
    }
    
    @After
    public void tearDown() {
        classRegistryService.deregister("TestType");
    }

}
