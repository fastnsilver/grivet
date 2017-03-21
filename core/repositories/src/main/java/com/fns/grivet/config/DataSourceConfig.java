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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.codahale.metrics.MetricRegistry;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.data-source-class-name}")
    private String dataSourceClassName;
    
    @Autowired
    private DataSourceProperties dataSourceProperties;
    
    @Autowired
    private MetricRegistry metricRegistry;
    
    @Bean
    public HikariDataSource dataSource() {
        final HikariDataSource ds = new HikariDataSource();
        ds.setMaximumPoolSize(25); 
        ds.setDataSourceClassName(dataSourceClassName); 
        ds.addDataSourceProperty("url", dataSourceProperties.getUrl()); 
        ds.addDataSourceProperty("user", dataSourceProperties.getUsername());
        ds.addDataSourceProperty("password", dataSourceProperties.getPassword());
        ds.setMetricRegistry(metricRegistry);
        return ds;
    } 
}
