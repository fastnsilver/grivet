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

import org.springframework.context.annotation.Profile;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;

import com.fns.grivet.model.Op;

@Profile("!pipeline")
public class IngestStub implements Ingester {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(IngestStub.class);

	@Override
	public void ingest(Message<JSONObject> message) {
		Assert.notNull(message.getHeaders(), "No message headers!");
		if (message.getHeaders().get("op").equals(Op.CREATE.name())) {
			Assert.hasText(message.getHeaders().get("type", String.class), "Type must not be null or empty!");
		}
		Assert.notNull(message.getPayload(), "Message must have non-null payload!");
		log.info("Received message.  Headers - {}.  Payload - {}", message.getHeaders().toString(),
				message.getPayload().toString());
	}

}
