package com.fns.grivet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

import com.okta.spring.boot.oauth.Okta;

@Profile("secure")
@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                // allow anonymous access to the root page
                .requestMatchers("/").permitAll()
                // all other requests
                .anyRequest().authenticated()
                .and()
                .oauth2ResourceServer().jwt(); // replace .jwt() with .opaqueToken() for Opaque Token case

        // Send a 401 message to the browser (w/o this, you'll see a blank page)
        Okta.configureResourceServer401ResponseBody(http);
        return http.build();
    }
}