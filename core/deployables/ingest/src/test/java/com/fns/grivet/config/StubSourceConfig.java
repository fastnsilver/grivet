package com.fns.grivet.config;

import com.fns.grivet.service.IngestStub;
import com.fns.grivet.service.Ingester;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StubSourceConfig {

    @Bean
    public Ingester ingestService() {
        return new IngestStub();
    }
}
