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
package com.fns.grivet.config;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.jdbc.DataSourcePoolMetrics;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fns.grivet.repo.AuditorProvider;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;

@Configuration
@EnableTransactionManagement
@EnableJpaAuditing(auditorAwareRef="auditorProvider")
public class DataSourceConfig {
    
    @Autowired
    private DataSource dataSource;

    @Autowired
    private Collection<DataSourcePoolMetadataProvider> metadataProviders;

    @Autowired
    private MeterRegistry registry;

    @Autowired
    private Environment env;

    @PostConstruct
    private void instrumentDataSource() {
        new DataSourcePoolMetrics(
            dataSource,
            metadataProviders,
            "data.source",
            Tags.zip(
                    "stack", 
                    env.acceptsProfiles("h2") ? "in-memory" : "external-datasource")
        ).bindTo(registry);
    }
    
    
    
    
        
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorProvider();
    }
}
