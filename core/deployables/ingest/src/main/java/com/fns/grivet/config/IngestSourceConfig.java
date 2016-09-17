package com.fns.grivet.config;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import com.fns.grivet.service.IngestService;
import com.fns.grivet.service.Ingester;

@Profile("pipeline")
@Configuration
public class IngestSourceConfig {

	@EnableBinding(Source.class)
	static class EventSource {

		@Primary
		@Bean
		public Ingester ingestService(Source source) {
			return new IngestService(source);
		}
	}

}
