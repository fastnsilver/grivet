package com.fns.grivet.controller;

import static org.junit.jupiter.api.Assertions.fail;
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
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fns.grivet.IngestInit;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

@ExtendWith(value = { SpringExtension.class, RestDocumentationExtension.class })
@SpringBootTest(classes=IngestInit.class)
public class IngestDocumentationTest {
    
    @Autowired
    private ResourceLoader resolver;

    @Autowired
    private WebApplicationContext context;
        
    private MockMvc mockMvc;
    
    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        RestDocumentationResultHandler document = document("{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()));
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(document)
                .build();
    }
    
    @Test
    public void ingestCreateTypeRequest() {
        try {
            mockMvc.perform(
                    post("/type")
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
                    post("/types")
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
                    patch("/type")
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
                    delete("/type")
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
        Resource r = resolver.getResource("classpath:%s.json".formatted(data));
        return IOUtils.toString(r.getInputStream(), Charset.defaultCharset());
    }
}
