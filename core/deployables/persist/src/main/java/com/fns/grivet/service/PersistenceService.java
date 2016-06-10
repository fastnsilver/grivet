package com.fns.grivet.service;

import com.codahale.metrics.MetricRegistry;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.Message;

public class PersistenceService {
    
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final EntityService entityService;
    private final MetricRegistry metricRegistry;

    public PersistenceService(EntityService entityService, MetricRegistry metricRegistry) {
        this.entityService = entityService;
        this.metricRegistry = metricRegistry;
    }

    @StreamListener(Sink.INPUT)
    public void store(Message<JSONObject> message) {
        log.debug("Received message.  Headers - {}.  Payload - {}", message.getHeaders().toString(),
                message.getPayload().toString());
        String type = message.getHeaders().get("type", String.class);
        entityService.create(type, message.getPayload());
        metricRegistry.counter(MetricRegistry.name("store", type, "count")).inc();
        log.info("Successfully stored type [{}]", type);
    }
}
