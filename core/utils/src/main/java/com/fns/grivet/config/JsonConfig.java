package com.fns.grivet.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonConfig {

    @Bean
    public Module orgJsonModule() {
        return new JsonOrgModule();
    }
}
