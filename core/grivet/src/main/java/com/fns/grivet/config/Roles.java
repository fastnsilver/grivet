package com.fns.grivet.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Profile("secure")
@Component
public class Roles {

    public final String USER;
    public final String ADMIN;

    @Autowired
    public Roles(Environment env) {
        USER = env.getProperty("stormpath.authorized.group.user");
        ADMIN = env.getProperty("stormpath.authorized.group.admin");
    }
}