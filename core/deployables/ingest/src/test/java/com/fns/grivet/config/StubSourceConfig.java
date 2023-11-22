package com.fns.grivet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fns.grivet.service.IngestStub;
import com.fns.grivet.service.Ingester;

@Configuration
public class StubSourceConfig {

	@Bean
	public Ingester ingestService() {
		return new IngestStub();
	}

}
