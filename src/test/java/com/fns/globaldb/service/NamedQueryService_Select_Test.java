package com.fns.globaldb.service;

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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fns.globaldb.GlobalDbApplication;
import com.fns.globaldb.query.NamedQuery;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = GlobalDbApplication.class)
public class NamedQueryService_Select_Test {

private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    
    @Autowired
    private ClassRegistryService classRegistryService;
    
    @Autowired
    private NamedQueryService namedQueryService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Before
    public void setUp() throws IOException {
        Resource r = resolver.getResource("classpath:TestType.json");
        String json = FileUtils.readFileToString(r.getFile());
        JSONObject payload = new JSONObject(json);
        classRegistryService.register(payload);
    }
    
    @Test
    public void testCreateThenGet_happyPath() throws IOException {
        Resource r = resolver.getResource("classpath:TestSelectQuery.json");
        String json = FileUtils.readFileToString(r.getFile());
        NamedQuery namedQuery = objectMapper.readValue(json, NamedQuery.class);
        namedQueryService.create(namedQuery);
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("createdTime", LocalDateTime.now().plusDays(1).toString());
        String result = namedQueryService.get("getAttributesCreatedBefore", params);
        JSONArray arrResult = new JSONArray(result);
        Assert.assertTrue("Result should contain 7 attributes", arrResult.length() == 7);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testGet_queryNotFound() throws IOException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("createdTime", LocalDateTime.now().plusDays(1).toString());
        namedQueryService.get("getAttributesCreatedBefore", params);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testCreateThenGet_paramsNotSupplied() throws IOException {
        Resource r = resolver.getResource("classpath:TestSelectQuery.json");
        String json = FileUtils.readFileToString(r.getFile());
        NamedQuery namedQuery = objectMapper.readValue(json, NamedQuery.class);
        namedQueryService.create(namedQuery);
        
        namedQueryService.get("getAttributesCreatedBefore", null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testCreateThenGet_incorrectParamsSupplied() throws IOException {
        Resource r = resolver.getResource("classpath:TestSelectQuery.json");
        String json = FileUtils.readFileToString(r.getFile());
        NamedQuery namedQuery = objectMapper.readValue(json, NamedQuery.class);
        namedQueryService.create(namedQuery);
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("timedCreated", LocalDateTime.now().plusDays(1).toString());
        namedQueryService.get("getAttributesCreatedBefore", params);
    }
    
    @After
    public void tearDown() {
        classRegistryService.deregister("TestType");
        namedQueryService.delete("getAttributesCreatedBefore");
    }
}
