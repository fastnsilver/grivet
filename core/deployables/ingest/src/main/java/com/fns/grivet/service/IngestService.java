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
package com.fns.grivet.service;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.fns.grivet.model.Op;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile("pipeline")
@Service
public class IngestService implements Ingester {

    public static final String DESTINATION = "message-out-0";

    private final StreamBridge bridge;

    @Autowired
    public IngestService(StreamBridge bridge) {
        this.bridge = bridge;
    }

    @Override
    public void ingest(Message<JSONObject> message) {
        Assert.notNull(message.getHeaders(), "No message headers!");
        Assert.notNull(message.getHeaders().get("op", String.class), "Op code must be specified!");
        if (message.getHeaders().get("op").equals(Op.CREATE.name())) {
            Assert.hasText(message.getHeaders().get("type", String.class), "Type must not be null or empty!");
        }
        log.debug("Received message.  Headers - {}.  Payload - {}", message.getHeaders().toString(),
            message.getPayload().toString());
        bridge.send(DESTINATION, message);
    }

}
