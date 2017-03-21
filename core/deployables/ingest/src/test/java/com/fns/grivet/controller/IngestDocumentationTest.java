package com.fns.grivet.controller;

import static org.junit.Assert.fail;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fns.grivet.IngestInit;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=IngestInit.class)
public class IngestDocumentationTest {

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");
    
    @Autowired
    private ResourceLoader resolver;

    @Autowired
    private WebApplicationContext context;
        
    private MockMvc mockMvc;
    
    private RestDocumentationResultHandler document;

    @Before
    public void setUp() {
        this.document = document("{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()));
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration(this.restDocumentation))
                .alwaysDo(this.document)
                .build();
    }
    
    @Test
    public void ingestCreateTypeRequest() {
        try {
            mockMvc.perform(
                    post("/api/v1/type")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Type", "TestType2")
                            .content(payload("TestTypeData2"))
                    )
                    .andExpect(status().isAccepted());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void ingestCreateTypesRequest() {
        try {
            mockMvc.perform(
                    post("/api/v1/types")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Type", "Contact")
                            .content(payload("TestMultipleContactsData"))
                    )
                    .andExpect(status().isAccepted());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void ingestUpdateTypeRequest() {
        try {
            mockMvc.perform(
                    patch("/api/v1/type")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("oid", "123")
                            .content(mutz("TestTypeData2", ImmutableMap.of("age", 35, "high-school-graduation-year", 1997), ImmutableSet.of("iq", "is-minor")))
                    )
                    .andExpect(status().isAccepted());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void ingestDeleteTypeRequest() {
        try {
            mockMvc.perform(
                    delete("/api/v1/type")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("oid", "123")
                    )
                    .andExpect(status().isAccepted());
        } catch (Exception e) {
            fail(e.getMessage());
        } 
    }
    
    private String mutz(String data, Map<String, Object> properties, Set<String> removed) throws JSONException, IOException {
        String original = payload(data);
        JSONObject jo = new JSONObject(original);
        properties.forEach((k,v) -> jo.put(k, v));
        removed.forEach(k -> jo.remove(k));
        return jo.toString();
    }
    
    private String payload(String data) throws IOException{
        Resource r = resolver.getResource(String.format("classpath:%s.json", data));
        return IOUtils.toString(r.getInputStream());
    }
}
