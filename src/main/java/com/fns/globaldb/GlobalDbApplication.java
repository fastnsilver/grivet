package com.fns.globaldb;

import org.h2.server.web.WebServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GlobalDbApplication {

    public static void main(String[] args) {
        SpringApplication.run(GlobalDbApplication.class, args);
    }

    @Bean
    @ConditionalOnProperty(name="spring.datasource.driver-class-name", havingValue="org.h2.Driver", matchIfMissing=false)
    ServletRegistrationBean h2servletRegistration(){
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(new WebServlet());
        registrationBean.addUrlMappings("/h2/console/*");
        return registrationBean;
    }
}
