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
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;

import com.fns.grivet.model.Op;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IngestService implements Ingester {

    private final Source source;
    
    public IngestService(Source source) {
        this.source = source;
    }

    @Override
    public void ingest(Message<JSONObject> message) {
        Assert.notNull(message.getHeaders(), "No message headers!");
        Assert.notNull(message.getHeaders().get("op", Op.class), "Op code must be specified!");
        if (message.getHeaders().get("op").equals(Op.CREATE)) {
            Assert.hasText(message.getHeaders().get("type", String.class), "Type must not be null or empty!");
        }
        log.debug("Received message.  Headers - {}.  Payload - {}", message.getHeaders().toString(),
                message.getPayload().toString());
        source.output().send(message);
    }
    
}
