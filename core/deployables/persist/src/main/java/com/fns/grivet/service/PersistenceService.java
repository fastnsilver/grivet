package com.fns.grivet.service;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;

import com.codahale.metrics.MetricRegistry;

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
		Assert.notNull(message.getHeaders(), "No message headers!");
		Assert.notNull(message.getPayload(), "Message must have non-null payload!");
		log.debug("Received message.  Headers - {}.  Payload - {}", message.getHeaders().toString(),
				message.getPayload().toString());

		String op = message.getHeaders().get("op", String.class);
		Assert.hasText(op, "Message header must contain an op code!");

		// TODO there must be a better way...
		if (op.equals("create")) {
			String type = message.getHeaders().get("type", String.class);
			Assert.hasText(type, "Message header must contain a type for create requests!");
			entityService.create(type, message.getPayload());
			metricRegistry.counter(MetricRegistry.name("store", "create", type, "count")).inc();
			log.info("Successfully created type [{}]", type);
		} else if (op.equals("update")) {
			Long oid = message.getHeaders().get("oid", Long.class);
			Assert.notNull(oid, "Message header must contain an oid for update requests!");
			String type = entityService.update(oid, message.getPayload());
			metricRegistry.counter(MetricRegistry.name("store", "update", type, "count")).inc();
			log.info("Successfully updated type [{}]", type);
		}
	}
}
