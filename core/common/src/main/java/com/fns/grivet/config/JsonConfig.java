package com.fns.grivet.config;

import tools.jackson.databind.JacksonModule;
import tools.jackson.datatype.jsonorg.JsonOrgModule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonConfig {

	@Bean
	public JacksonModule orgJsonModule() {
		return new JsonOrgModule();
	}

}
