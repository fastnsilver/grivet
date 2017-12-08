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

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fns.grivet.QueryInit;
import com.fns.grivet.query.NamedQuery;
import com.fns.grivet.repo.NamedQueryRepository;
import com.fns.grivet.service.NamedQueryService;

@ExtendWith(value = { SpringExtension.class, RestDocumentationExtension.class })
@SpringBootTest(classes = QueryInit.class)
public class QueryDocumentationTest {

    @Autowired
    private ResourceLoader resolver;

    @Autowired
    private WebApplicationContext context;
    
    @Autowired
    private ObjectMapper mapper;
        
    private MockMvc mockMvc;
    
    @BeforeEach
    public void setUp(RestDocumentationExtension restDocumentation) {
        RestDocumentationResultHandler document = document("{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()));
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(document)
                .build();
    }
    
    @AfterEach
    public void tearDown() {
        context.getBean(NamedQueryRepository.class).deleteAll();
    }
    
    @Test
    public void createNamedQueryAsSproc() {
        try {
            mockMvc.perform(
                    post("/api/v1/query")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payload("TestSprocQuery"))
                    )
                    .andExpect(status().isNoContent());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void createNamedQueryAsSelect() {
        try {
            mockMvc.perform(
                    post("/api/v1/query")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payload("TestSelectQuery"))
                    )
                    .andExpect(status().isNoContent());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void createNamedQueryAsSelectWithNoParams() {
        try {
            mockMvc.perform(
                    post("/api/v1/query")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payload("TestSelectQuery2"))
                    )
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/api/v1/query/getAttributesCreatedToday"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void createNamedQueryAsSelectWithQueryType() {
        try {
            mockMvc.perform(
                    post("/api/v1/query")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payload("TestSelectQuery3"))
                    )
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/api/v1/query/getClassesCreatedToday"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void createNamedQueryAsSprocWithQueryType() {
        try {
            mockMvc.perform(
                    post("/api/v1/query")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payload("TestSprocQuery2"))
                    )
                    .andExpect(status().isNoContent());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void executeNamedQueryNoParams() {
        try {
            createNamedQuery("TestSelectQuery2");
            mockMvc.perform(
                    get("/api/v1/query/getAttributesCreatedToday")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void listNamedQueries() {
        try {
            createNamedQuery("TestSelectQuery");
            createNamedQuery("TestSelectQuery2");
            mockMvc.perform(
                    get("/api/v1/queries")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void deleteNamedQuery() {
        try {
            createNamedQuery("TestSelectQuery");
            mockMvc.perform(
                    delete("/api/v1/query/TestSelectQuery")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    private void createNamedQuery(String payload) throws JsonParseException, JsonMappingException, IOException {
        NamedQueryService svc = context.getBean(NamedQueryService.class);
        svc.create(mapper.readValue(payload(payload), NamedQuery.class));
    }
    
    private String payload(String payload) throws IOException{
        Resource r = resolver.getResource(String.format("classpath:%s.json", payload));
        return IOUtils.toString(r.getInputStream(), Charset.defaultCharset());
    }
}
