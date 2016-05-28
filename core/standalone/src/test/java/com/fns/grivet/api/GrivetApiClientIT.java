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

import com.fns.grivet.TestInit;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDateTime;
import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import javax.annotation.PostConstruct;

import net.javacrumbs.jsonunit.JsonAssert;

@WebIntegrationTest({"server.port=0", "management.port=0"})
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { TestInit.class } )
public class GrivetApiClientIT {

    @Autowired
    private ResourceLoader resolver;
    
    
    @Value("${local.server.port}")
    private int serverPort;
    
    @PostConstruct
    public void init() {
      RestAssured.port = serverPort;
    }
    
    // individual tests are responsible for setup and tear-down!
    
    private String registerTestType() throws IOException {
        Resource r = resolver.getResource("classpath:TestType.json");
        String json = IOUtils.toString(r.getInputStream());
        given().contentType("application/json").request().body(json).then().expect().statusCode(equalTo(201)).when().post("/type/register");
        return json;
    }
    
    private void deregisterType() {
        given().contentType("application/json").request().then().expect().statusCode(equalTo(204)).when().delete("/type/register/TestType");
    }
    
    
    private String registerMultipleTypes() throws IOException {
        Resource r = resolver.getResource("classpath:TestMultipleTypes.json");
        String json = IOUtils.toString(r.getInputStream());
        given().contentType("application/json").request().body(json).then().expect().statusCode(equalTo(201)).when().post("/type/register/batch");
        return json;

    }

    private void deregisterTypes() {
        given().contentType("application/json").request().then().expect().statusCode(equalTo(204)).when().delete("/type/register/Contact");
        given().contentType("application/json").request().then().expect().statusCode(equalTo(204)).when().delete("/type/register/Course");
    }

    @Test
    public void testRegisterType_happyPath() throws IOException {
        registerTestType();
        deregisterType();
    }
    
    @Test
    public void testRegisterMultipleTypes_happyPath() throws IOException {
        registerMultipleTypes();
        deregisterTypes();
    }

    @Test
    public void testRegisterType_emptyBody() throws IOException {
        given().contentType("application/json").request().body("").then().expect().statusCode(equalTo(400)).when().post("/type/register");
    }
    
    @Test
    public void testRegisterType_badRequest() throws Exception {
        Resource r = resolver.getResource("classpath:BadTestType.json");
        String json = IOUtils.toString(r.getInputStream());
        given().contentType("application/json").request().body(json).then().expect().statusCode(equalTo(400)).when().post("/type/register");
    }
    
    @Test
    public void testGetRegisteredType_happyPath() throws IOException {
        String json = registerTestType();
        Response response = given().contentType("application/json").request().then().expect().statusCode(equalTo(200)).when().get("/type/register/TestType");
        JsonAssert.assertJsonEquals(json, response.body().asString());
        deregisterType();
    }
    
    @Test
    public void testAllRegisteredTypes_happyPath() throws IOException {
        String json = registerTestType();
        Response response = given().contentType("application/json").request().then().expect().statusCode(equalTo(200)).when().get("/type/register?showAll");
        JSONArray result = new JSONArray(response.body().asString());
        JsonAssert.assertJsonEquals(json, result.get(0).toString());
        deregisterType();
    }

    @Test
    public void testLinkAndUnlinkJsonSchema_happyPath() throws IOException {
        registerTestType();
        Resource r = resolver.getResource("classpath:TestTypeSchema.json");
        String schema = IOUtils.toString(r.getInputStream());
        given().contentType("application/json").request().body(schema).then().expect().statusCode(equalTo(200)).when().post("/type/schema/link");
        given().contentType("application/json").request().then().expect().statusCode(equalTo(200)).when().put("/type/schema/unlink/TestType");
        deregisterType();
    }
    
    @Test
    public void testRegisterAndLinkAndStoreAndGetType_happyPath() throws IOException {
        registerTestType();
        Resource r = resolver.getResource("classpath:TestTypeSchema.json");
        String schema = IOUtils.toString(r.getInputStream());
        given().contentType("application/json").request().body(schema).then().expect().statusCode(equalTo(200)).when().post("/type/schema/link");
        r = resolver.getResource("classpath:TestTypeData.json");
        String type = IOUtils.toString(r.getInputStream());
        given().contentType("application/json").request().body(type).then().expect().statusCode(equalTo(204)).when().post("/type/store/TestType");
        
        // GET (default)
        Response response = given().contentType("application/json").request().then().expect().statusCode(equalTo(200)).when().get("/type/store/TestType");
        JSONArray result = new JSONArray(response.body().asString());
        JsonAssert.assertJsonEquals(type, result.get(0).toString());
        
        // GET (with equals constraint)
        response = given().param("c", "varchar|equals|Rush").contentType("application/json").request().then().expect().statusCode(equalTo(200)).when().get("/type/store/TestType");
        result = new JSONArray(response.body().asString());
        JsonAssert.assertJsonEquals(type, result.get(0).toString());
        
        // GET (with startsWith constraint)
        response = given().param("c", "text|startsWith|Grim-faced").contentType("application/json").request().then().expect().statusCode(equalTo(200)).when().get("/type/store/TestType");
        result = new JSONArray(response.body().asString());
        JsonAssert.assertJsonEquals(type, result.get(0).toString());
        
        // GET (with lessThanOrEqualTo constraint)
        response = given().param("c", "datetime|lessThanOrEqualTo|2016-01-01T00:00:00Z").contentType("application/json").request().then().expect().statusCode(equalTo(200)).when().get("/type/store/TestType");
        result = new JSONArray(response.body().asString());
        JsonAssert.assertJsonEquals(type, result.get(0).toString());
        
        // GET (with multiple constraints)
        response = given()
                    .param("c", "datetime|lessThanOrEqualTo|2016-01-01T00:00:00Z")
                    .param("c", "text|endsWith|city")
                    .contentType("application/json").request().then().expect().statusCode(equalTo(200)).when().get("/type/store/TestType");
        result = new JSONArray(response.body().asString());
        JsonAssert.assertJsonEquals(type, result.get(0).toString());
        
        deregisterType();
    }
    
    @Test
    public void testRegisterAndStoreMultipleContacts_happyPath() throws IOException {
        registerMultipleTypes();
        Resource r = resolver.getResource("classpath:TestMultipleContactsData.json");
        String contacts = IOUtils.toString(r.getInputStream());
        given().contentType("application/json").request().body(contacts).then().expect().statusCode(equalTo(204)).when().post("/type/store/batch/Contact");
        deregisterTypes();
    }
    
    @Test
    public void testRegisterAndStoreMultipleCourses_accepted() throws IOException {
        registerMultipleTypes();
        Resource r = resolver.getResource("classpath:BadCourseData.json");
        String courses = IOUtils.toString(r.getInputStream());
        
        // link Course schema
        r = resolver.getResource("classpath:CourseSchema.json");
        String schema = IOUtils.toString(r.getInputStream());
        given().contentType("application/json").request().body(schema).then().expect().statusCode(equalTo(200)).when().post("/type/schema/link");
        
        given().contentType("application/json").request().body(courses).then().expect().statusCode(equalTo(202)).when().post("/type/store/batch/Course");
        deregisterTypes();
    }
    
    @Test
    public void testAllRegisteredNamedQueries_happyPath() throws IOException {
        Resource r = resolver.getResource("classpath:TestSelectQuery.json");
        String select = IOUtils.toString(r.getInputStream());
        given().contentType("application/json").request().body(select).then().expect().statusCode(equalTo(204)).when().post("/query"); 
        Response response = given().contentType("application/json").request().then().expect().statusCode(equalTo(200)).when().get("/query?showAll");
        JSONArray result = new JSONArray(response.body().asString());
        Assert.assertEquals(1, result.length()); 
        given().contentType("application/json").request().then().expect().statusCode(equalTo(204)).when().delete("/query/getAttributesCreatedBefore");
    }
    
    @Test
    public void testNamedQueryRegistrationAndRetrieval_select_happyPath() throws IOException {
        registerTestType();
        Resource r = resolver.getResource("classpath:TestSelectQuery3.json");
        String select = IOUtils.toString(r.getInputStream());
        given().contentType("application/json").request().body(select).then().expect().statusCode(equalTo(201)).when().post("/query");
        Response response = given().contentType("application/json").request().then().expect().statusCode(equalTo(200)).when().get("/query/getClassesCreatedToday");
        JSONArray result = new JSONArray(response.body().asString());
        Assert.assertEquals(1, result.length());
        given().contentType("application/json").request().then().expect().statusCode(equalTo(204)).when().delete("/query/getClassesCreatedToday");
        deregisterType();
    }
    
    
    @Test
    @Ignore("Cannot test w/ H2")
    public void testNamedQueryRegistrationAndRetrieval_sproc_happyPath() throws IOException {
        registerTestType();
        Resource r = resolver.getResource("classpath:TestSprocQuery.json");
        String sproc = IOUtils.toString(r.getInputStream());
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
