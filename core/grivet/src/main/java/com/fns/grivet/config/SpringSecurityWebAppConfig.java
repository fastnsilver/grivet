package com.fns.grivet.config;

import static com.stormpath.spring.config.StormpathWebSecurityConfigurer.stormpath;

import com.stormpath.spring.config.EnableStormpathWebSecurity;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Profile("secure")
@Configuration
@EnableStormpathWebSecurity
// https://docs.stormpath.com/java/spring-boot-web/tutorial.html#spring-security-meet-stormpath 
// says we don't need to add the second annotation above, but if we don't add it we get a 
// org.springframework.beans.factory.BeanCreationException: Could not autowire field: 
// com.stormpath.spring.oauth.Oauth2AuthenticationSpringSecurityProcessingFilter
public class SpringSecurityWebAppConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .apply(stormpath()).and()
            .authorizeRequests()
            .antMatchers("/").permitAll();
    }
}
