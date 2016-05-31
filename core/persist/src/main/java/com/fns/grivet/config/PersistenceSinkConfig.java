package com.fns.grivet.config;

import com.fns.grivet.service.EntityService;
import com.fns.grivet.service.PersistenceService;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;

@EnableBinding(Sink.class)
@ConditionalOnProperty(prefix = "app.persistence.sink", name = "enabled", havingValue = "true")
public class PersistenceSinkConfig {

    public PersistenceService persistenceService(EntityService entityService) {
        return new PersistenceService(entityService);
    }

}
