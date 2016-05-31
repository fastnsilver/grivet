package com.fns.grivet.service;

import org.json.JSONObject;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.Message;

public class PersistenceService {

    static class PersistenceSink { }
    
    private final EntityService entityService;

    public PersistenceService(EntityService entityService) {
        this.entityService = entityService;
    }

    @StreamListener(Sink.INPUT)
    public void store(Message<JSONObject> message) {
        // TODO Log receipt of message
        entityService.create(message.getHeaders().get("type", String.class), message.getPayload());
    }
}
