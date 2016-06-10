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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;


@Service
public class IngestService {

    @EnableBinding(Source.class)
    static class EventSource { }
    
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Source source;
    
    @Autowired
    public IngestService(Source source) {
        this.source = source;
    }

    public void ingest(Message<JSONObject> message) {
        log.debug("Received message.  Headers - {}.  Payload - {}", message.getHeaders().toString(),
                message.getPayload().toString());
        String type = message.getHeaders().get("type", String.class);
        Assert.hasText(type, "Type must not be null or empty!");
        source.output().send(message);
    }
    
}
