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
package com.fns.grivet.api;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.joda.time.LocalDateTime;
import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fns.grivet.TestInit;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

import net.javacrumbs.jsonunit.JsonAssert;

@WebIntegrationTest({"server.port=0", "management.port=0"})
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { TestInit.class } )
public class GrivetApiClientIT {

    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    
    
    @Value("${local.server.port}")
    private int serverPort;
    
    @PostConstruct
    public void init() {
      RestAssured.port = serverPort;
    }
    
    // individual tests are responsible for setup and tear-down!
    
    private String registerTestType() throws IOException {
        Resource r = resolver.getResource("classpath:TestType.json");
        String json = FileUtils.readFileToString(r.getFile());
        given().contentType("application/json").request().body(json).then().expect().statusCode(equalTo(201)).when().post("/register");
        return json;
    }
    
    private void deregisterType() {
        given().contentType("application/json").request().then().expect().statusCode(equalTo(204)).when().delete("/register/TestType");
    }
    
    
    @Test
    public void testRegisterType_happyPath() throws IOException {
        registerTestType();
        deregisterType();
    }
    
    @Test
    public void testRegisterType_emptyBody() throws IOException {
        given().contentType("application/json").request().body("").then().expect().statusCode(equalTo(400)).when().post("/register");
    }
    
    @Test
    public void testGetRegisteredType_happyPath() throws IOException {
        String json = registerTestType();
        Response response = given().contentType("application/json").request().then().expect().statusCode(equalTo(200)).when().get("/register/TestType");
        JsonAssert.assertJsonEquals(json, response.body().asString());
        deregisterType();
    }
    
    @Test
    public void testAllRegisteredTypes_happyPath() throws IOException {
        String json = registerTestType();
        Response response = given().contentType("application/json").request().then().expect().statusCode(equalTo(200)).when().get("/register?showAll");
        JSONArray result = new JSONArray(response.body().asString());
        JsonAssert.assertJsonEquals(json, result.get(0).toString());
        deregisterType();
    }

    @Test
    public void testLinkAndUnlinkJsonSchema_happyPath() throws IOException {
        registerTestType();
        Resource r = resolver.getResource("classpath:TestTypeSchema.json");
        String schema = FileUtils.readFileToString(r.getFile());
        given().contentType("application/json").request().body(schema).then().expect().statusCode(equalTo(200)).when().post("/register?linkSchema");
        given().contentType("application/json").request().then().expect().statusCode(equalTo(200)).when().put("/register/TestType?unlinkSchema");
        deregisterType();
    }
    
    @Test
    public void testRegisterAndLinkAndStoreAndGetType_happyPath() throws IOException {
        registerTestType();
        Resource r = resolver.getResource("classpath:TestTypeSchema.json");
        String schema = FileUtils.readFileToString(r.getFile());
        given().contentType("application/json").request().body(schema).then().expect().statusCode(equalTo(200)).when().post("/register?linkSchema");
        r = resolver.getResource("classpath:TestTypeData.json");
        String type = FileUtils.readFileToString(r.getFile());
        given().contentType("application/json").request().body(type).then().expect().statusCode(equalTo(204)).when().post("/store/TestType");
        Response response = given().contentType("application/json").request().then().expect().statusCode(equalTo(200)).when().get("/store/TestType");
        JSONArray result = new JSONArray(response.body().asString());
        JsonAssert.assertJsonEquals(type, result.get(0).toString());
        deregisterType();
    }
    
    @Test
    public void testAllRegisteredNamedQueries_happyPath() throws IOException {
        Resource r = resolver.getResource("classpath:TestSelectQuery.json");
        String select = FileUtils.readFileToString(r.getFile());
        given().contentType("application/json").request().body(select).then().expect().statusCode(equalTo(204)).when().post("/query"); 
        Response response = given().contentType("application/json").request().then().expect().statusCode(equalTo(200)).when().get("/query?showAll");
        JSONArray result = new JSONArray(response.body().asString());
        Assert.assertEquals(1, result.length()); 
        given().contentType("application/json").request().then().expect().statusCode(equalTo(204)).when().delete("/query/getAttributesCreatedBefore");
    }
    
    @Test
    public void testNamedQueryRegistrationAndRetrieval_select_happyPath() throws IOException {
        registerTestType();
        Resource r = resolver.getResource("classpath:TestSelectQuery.json");
        String select = FileUtils.readFileToString(r.getFile());
        given().contentType("application/json").request().body(select).then().expect().statusCode(equalTo(204)).when().post("/query");
        LocalDateTime now = LocalDateTime.now();
        Response response = given().contentType("application/json").request().then().expect().statusCode(equalTo(200)).when().get("/query/getAttributesCreatedBefore?createdTime=" + now.toString());
        JSONArray result = new JSONArray(response.body().asString());
        Assert.assertEquals(7, result.length());
        given().contentType("application/json").request().then().expect().statusCode(equalTo(204)).when().delete("/query/getAttributesCreatedBefore");
        deregisterType();
    }
    
    
    @Test
    @Ignore("Cannot test w/ H2")
    public void testNamedQueryRegistrationAndRetrieval_sproc_happyPath() throws IOException {
        registerTestType();
        Resource r = resolver.getResource("classpath:TestSprocQuery.json");
        String sproc = FileUtils.readFileToString(r.getFile());
        given().contentType("application/json").request().body(sproc).then().expect().statusCode(equalTo(204)).when().post("/query");
        LocalDateTime now = LocalDateTime.now();
        Response response = given().contentType("application/json").request().then().expect().statusCode(equalTo(200)).when().get("/query/sproc.getAttributesCreatedBefore?createdTime=" + now.toString());
        JSONArray result = new JSONArray(response.body().asString());
        Assert.assertEquals(7, result.length());
        given().contentType("application/json").request().then().expect().statusCode(equalTo(204)).when().delete("/query/sproc.getAttributesCreatedBefore");
        deregisterType();
    }
    
    // TODO More testing; unhappy path cases
}
