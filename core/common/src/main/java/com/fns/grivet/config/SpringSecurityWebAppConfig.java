package com.fns.grivet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import com.auth0.Auth0;
import com.auth0.authentication.AuthenticationAPIClient;
import com.auth0.authentication.result.UserProfile;
import com.auth0.request.Request;
import com.auth0.spring.security.api.Auth0JWTToken;
import com.auth0.spring.security.api.Auth0SecurityConfig;

@Profile("secure")
@Configuration
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(Integer.MAX_VALUE - 7)
public class SpringSecurityWebAppConfig extends Auth0SecurityConfig {

    public static class Auth0Client {

        private final Auth0 auth0;
        private final AuthenticationAPIClient client;

        public Auth0Client(String clientid, String domain) {
            this.auth0 = new Auth0(clientid, domain);
            this.client = this.auth0.newAuthenticationAPIClient();
        }

        public String getUsername(Auth0JWTToken token) {
            final Request<UserProfile> request = client.tokenInfo(token.getJwt());
            final UserProfile profile = request.execute();
            return profile.getEmail();
        }

    }
    
    /**
     * Not required for the Spring Security implementation, but offers Auth0 API access
     */
    @Bean
    public Auth0Client auth0Client() {
        return new Auth0Client(clientId, issuer);
    }

    /**
     * Override this function in subclass to apply custom authentication / authorization
     * strategies to your application endpoints
     */
    @Override
    protected void authorizeRequests(final HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(securedRoute).authenticated();
    }

    
    
}