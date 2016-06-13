package com.fns.grivet.config;

import com.fns.grivet.service.IngestService;
import com.fns.grivet.service.Ingester;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class IngestSourceConfig {

    @ConditionalOnProperty(prefix = "app.ingest.source", name = "enabled", havingValue = "true")
    @EnableBinding(Source.class)
    static class EventSource {

        @Primary
        @Bean
        public Ingester ingestService(Source source) {
            return new IngestService(source);
        }
    }

}
