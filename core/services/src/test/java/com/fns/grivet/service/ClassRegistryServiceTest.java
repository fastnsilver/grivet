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

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringRunner;

import com.fns.grivet.TestInit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestInit.class)
public class ClassRegistryServiceTest {

	@Autowired
	private ResourceLoader resolver;

	@Autowired
	private ClassRegistryService classRegistryService;

	@Test
	public void testRegisterThenGetThenAll() throws IOException {
		Resource r = resolver.getResource("classpath:TestType.json");
		String json = IOUtils.toString(r.getInputStream());
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
