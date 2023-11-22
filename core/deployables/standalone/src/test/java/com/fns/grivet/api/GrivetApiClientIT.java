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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;

import jakarta.annotation.PostConstruct;
import org.apache.commons.io.IOUtils;

import org.json.JSONArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.fns.grivet.StandaloneInit;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.javacrumbs.jsonunit.JsonAssert;

@SpringBootTest(classes = StandaloneInit.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class GrivetApiClientIT {

	@Autowired
	private ResourceLoader resolver;

	@LocalServerPort
	private int serverPort;

	@PostConstruct
	public void init() {
		RestAssured.port = serverPort;
	}

	// individual tests are responsible for setup and tear-down!

	private String registerTestType() {
		String json = null;
		Resource r = resolver.getResource("classpath:TestType.json");
		try {
			json = IOUtils.toString(r.getInputStream(), Charset.defaultCharset());
			given().contentType("application/json")
				.request()
				.body(json)
				.then()
				.expect()
				.statusCode(equalTo(201))
				.when()
				.post("/definition");
		}
		catch (IOException e) {
			fail(e.getMessage());
		}
		return json;
	}

	private void deregisterType() {
		given().contentType("application/json")
			.request()
			.then()
			.expect()
			.statusCode(equalTo(204))
			.when()
			.delete("/definition/TestType");
	}

	private String registerMultipleTypes() {
		String json = null;
		Resource r = resolver.getResource("classpath:TestMultipleTypes.json");
		try {
			json = IOUtils.toString(r.getInputStream(), Charset.defaultCharset());
			given().contentType("application/json")
				.request()
				.body(json)
				.then()
				.expect()
				.statusCode(equalTo(201))
				.when()
				.post("/definitions");
		}
		catch (IOException e) {
			fail(e.getMessage());
		}
		return json;

	}

	private void deregisterTypes() {
		given().contentType("application/json")
			.request()
			.then()
			.expect()
			.statusCode(equalTo(204))
			.when()
			.delete("/definition/Contact");
		given().contentType("application/json")
			.request()
			.then()
			.expect()
			.statusCode(equalTo(204))
			.when()
			.delete("/definition/Course");
	}

	@Test
	public void testRegisterTypeHappyPath() {
		registerTestType();
		deregisterType();
	}

	@Test
	public void testRegisterMultipleTypesHappyPath() {
		registerMultipleTypes();
		deregisterTypes();
	}

	@Test
	public void testRegisterTypeEmptyBody() {
		given().contentType("application/json")
			.request()
			.body("")
			.then()
			.expect()
			.statusCode(equalTo(400))
			.when()
			.post("/definition");
	}

	@Test
	public void testRegisterTypeBadRequest() {
		Resource r = resolver.getResource("classpath:BadTestType.json");
		try {
			String json = IOUtils.toString(r.getInputStream(), Charset.defaultCharset());
			given().contentType("application/json")
				.request()
				.body(json)
				.then()
				.expect()
				.statusCode(equalTo(400))
				.when()
				.post("/definition");
		}
		catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetRegisteredTypeHappyPath() {
		String json = registerTestType();
		Response response = given().contentType("application/json")
			.request()
			.then()
			.expect()
			.statusCode(equalTo(200))
			.when()
			.get("/definition/TestType");
		JsonAssert.assertJsonEquals(json, response.body().asString());
		deregisterType();
	}

	@Test
	public void testAllRegisteredTypesHappyPath() {
		String json = registerTestType();
		Response response = given().contentType("application/json")
			.request()
			.then()
			.expect()
			.statusCode(equalTo(200))
			.when()
			.get("/definitions");
		JSONArray result = new JSONArray(response.body().asString());
		JsonAssert.assertJsonEquals(json, result.get(0).toString());
		deregisterType();
	}

	@Test
	public void testLinkAndUnlinkJsonSchemaHappyPath() {
		registerTestType();
		Resource r = resolver.getResource("classpath:TestTypeSchema.json");
		try {
			String schema = IOUtils.toString(r.getInputStream(), Charset.defaultCharset());
			given().contentType("application/json")
				.request()
				.body(schema)
				.then()
				.expect()
				.statusCode(equalTo(200))
				.when()
				.post("/schema");
			given().contentType("application/json")
				.request()
				.then()
				.expect()
				.statusCode(equalTo(200))
				.when()
				.delete("/schema/TestType");
		}
		catch (IOException e) {
			fail(e.getMessage());
		}
		deregisterType();
	}

	@Test
	public void testRegisterAndLinkAndStoreAndGetTypeHappyPath() {
		registerTestType();
		Resource r = resolver.getResource("classpath:TestTypeSchema.json");
		try {
			String schema = IOUtils.toString(r.getInputStream(), Charset.defaultCharset());
			given().contentType("application/json")
				.request()
				.body(schema)
				.then()
				.expect()
				.statusCode(equalTo(200))
				.when()
				.post("/schema");
			r = resolver.getResource("classpath:TestTypeData.json");
			String type = IOUtils.toString(r.getInputStream(), Charset.defaultCharset());
			given().contentType("application/json")
				.request()
				.header("Type", "TestType")
				.body(type)
				.then()
				.expect()
				.statusCode(equalTo(201))
				.when()
				.post("/type");

			// GET (default)
			Response response = given().contentType("application/json")
				.request()
				.then()
				.expect()
				.statusCode(equalTo(200))
				.when()
				.get("/type/TestType");
			JSONArray result = new JSONArray(response.body().asString());
			JsonAssert.assertJsonEquals(type, result.get(0).toString());

			// GET (with equals constraint that is a boolean)
			response = given().param("c", "boolean|equals|false")
				.contentType("application/json")
				.request()
				.then()
				.expect()
				.statusCode(equalTo(200))
				.when()
				.get("/type/TestType");
			result = new JSONArray(response.body().asString());
			JsonAssert.assertJsonEquals(type, result.get(0).toString());

			// GET (with equals constraint that is a varchar)
			response = given().param("c", "varchar|equals|Rush")
				.contentType("application/json")
				.request()
				.then()
				.expect()
				.statusCode(equalTo(200))
				.when()
				.get("/type/TestType");
			result = new JSONArray(response.body().asString());
			JsonAssert.assertJsonEquals(type, result.get(0).toString());

			// GET (with startsWith constraint)
			response = given().param("c", "text|startsWith|Grim-faced")
				.contentType("application/json")
				.request()
				.then()
				.expect()
				.statusCode(equalTo(200))
				.when()
				.get("/type/TestType");
			result = new JSONArray(response.body().asString());
			JsonAssert.assertJsonEquals(type, result.get(0).toString());

			// GET (with lessThanOrEqualTo constraint)
			response = given().param("c", "datetime|lessThanOrEqualTo|2016-01-01T00:00:00Z")
				.contentType("application/json")
				.request()
				.then()
				.expect()
				.statusCode(equalTo(200))
				.when()
				.get("/type/TestType");
			result = new JSONArray(response.body().asString());
			JsonAssert.assertJsonEquals(type, result.get(0).toString());

			// GET (with multiple constraints)
			response = given().param("c", "datetime|lessThanOrEqualTo|2016-01-01T00:00:00Z")
				.param("c", "text|endsWith|city")
				.contentType("application/json")
				.request()
				.then()
				.expect()
				.statusCode(equalTo(200))
				.when()
				.get("/type/TestType");
			result = new JSONArray(response.body().asString());
			JsonAssert.assertJsonEquals(type, result.get(0).toString());
		}
		catch (IOException e) {
			fail(e.getMessage());
		}

		deregisterType();
	}

	@Test
	public void testRegisterAndStoreMultipleContactsHappyPath() {
		registerMultipleTypes();
		Resource r = resolver.getResource("classpath:TestMultipleContactsData.json");
		try {
			String contacts = IOUtils.toString(r.getInputStream(), Charset.defaultCharset());
			given().contentType("application/json")
				.request()
				.header("Type", "Contact")
				.body(contacts)
				.then()
				.expect()
				.statusCode(equalTo(201))
				.when()
				.post("/types");
		}
		catch (IOException e) {
			fail(e.getMessage());
		}
		deregisterTypes();
	}

	@Test
	public void testRegisterAndStoreMultipleCoursesAccepted() {
		registerMultipleTypes();
		Resource r = resolver.getResource("classpath:BadCourseData.json");
		try {
			String courses = IOUtils.toString(r.getInputStream(), Charset.defaultCharset());

			// link Course schema
			r = resolver.getResource("classpath:CourseSchema.json");
			String schema = IOUtils.toString(r.getInputStream(), Charset.defaultCharset());
			given().contentType("application/json")
				.request()
				.body(schema)
				.then()
				.expect()
				.statusCode(equalTo(200))
				.when()
				.post("/schema");

			given().contentType("application/json")
				.request()
				.header("Type", "Course")
				.body(courses)
				.then()
				.expect()
				.statusCode(equalTo(202))
				.when()
				.post("/types");
		}
		catch (IOException e) {
			fail(e.getMessage());
		}
		deregisterTypes();
	}

	@Test
	public void testAllRegisteredNamedQueriesHappyPath() {
		Resource r = resolver.getResource("classpath:TestSelectQuery.json");
		try {
			String select = IOUtils.toString(r.getInputStream(), Charset.defaultCharset());
			given().contentType("application/json")
				.request()
				.body(select)
				.then()
				.expect()
				.statusCode(equalTo(204))
				.when()
				.post("/query");
			Response response = given().contentType("application/json")
				.request()
				.then()
				.expect()
				.statusCode(equalTo(200))
				.when()
				.get("/queries");
			JSONArray result = new JSONArray(response.body().asString());
			Assertions.assertEquals(1, result.length());
			given().contentType("application/json")
				.request()
				.then()
				.expect()
				.statusCode(equalTo(204))
				.when()
				.delete("/query/getAttributesCreatedBefore");
		}
		catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testNamedQueryRegistrationAndRetrievalSelectHappyPath() {
		registerTestType();
		Resource r = resolver.getResource("classpath:TestSelectQuery3.json");
		try {
			String select = IOUtils.toString(r.getInputStream(), Charset.defaultCharset());
			given().contentType("application/json")
				.request()
				.body(select)
				.then()
				.expect()
				.statusCode(equalTo(201))
				.when()
				.post("/query");
			Response response = given().contentType("application/json")
				.request()
				.then()
				.expect()
				.statusCode(equalTo(200))
				.when()
				.get("/query/getClassesCreatedToday");
			JSONArray result = new JSONArray(response.body().asString());
			Assertions.assertEquals(1, result.length());
			given().contentType("application/json")
				.request()
				.then()
				.expect()
				.statusCode(equalTo(204))
				.when()
				.delete("/query/getClassesCreatedToday");
		}
		catch (IOException e) {
			fail(e.getMessage());
		}
		deregisterType();
	}

	@Test
	@Disabled("Cannot test w/ H2")
	public void testNamedQueryRegistrationAndRetrievalSprocHappyPath() {
		registerTestType();
		Resource r = resolver.getResource("classpath:TestSprocQuery.json");
		try {
			String sproc = IOUtils.toString(r.getInputStream(), Charset.defaultCharset());
			given().contentType("application/json")
				.request()
				.body(sproc)
				.then()
				.expect()
				.statusCode(equalTo(204))
				.when()
				.post("/query");
			LocalDateTime now = LocalDateTime.now();
			Response response = given().contentType("application/json")
				.request()
				.then()
				.expect()
				.statusCode(equalTo(200))
				.when()
				.get("/query/sproc.getAttributesCreatedBefore?createdTime=" + now.toString());
			JSONArray result = new JSONArray(response.body().asString());
			Assertions.assertEquals(7, result.length());
			given().contentType("application/json")
				.request()
				.then()
				.expect()
				.statusCode(equalTo(204))
				.when()
				.delete("/query/sproc.getAttributesCreatedBefore");
		}
		catch (IOException e) {
			fail(e.getMessage());
		}
		deregisterType();
	}

	// TODO More testing; unhappy path cases

}
