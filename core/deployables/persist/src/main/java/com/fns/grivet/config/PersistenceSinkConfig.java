package com.fns.grivet.config;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.fns.grivet.service.EntityService;
import com.fns.grivet.service.PersistenceService;

import io.micrometer.core.instrument.MeterRegistry;

@Profile("pipeline")
@Configuration
public class PersistenceSinkConfig {

	@EnableBinding(Sink.class)
	static class EventSink {

		@Bean
		public PersistenceService persistenceService(
		        EntityService entityService, MeterRegistry meterRegistry) {
			return new PersistenceService(entityService, meterRegistry);
		}

	}

}
