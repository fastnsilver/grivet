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

import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fns.grivet.SwaggerInit;

import io.github.robwin.markup.builder.MarkupLanguage;
import springfox.documentation.staticdocs.Swagger2MarkupResultHandler;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SwaggerInit.class)
public class AdminRestDocTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void convertSwaggerToAsciiDoc() {
		try {
			this.mockMvc.perform(get("/v2/api-docs").accept(MediaType.APPLICATION_JSON))
			.andDo(Swagger2MarkupResultHandler.outputDirectory("target/docs/asciidoc/generated").build())
			.andExpect(status().isOk());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void convertSwaggerToMarkdown() {
		try {
			this.mockMvc.perform(get("/v2/api-docs").accept(MediaType.APPLICATION_JSON))
			.andDo(Swagger2MarkupResultHandler.outputDirectory("target/docs/markdown/generated")
					.withMarkupLanguage(MarkupLanguage.MARKDOWN).build())
			.andExpect(status().isOk());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
