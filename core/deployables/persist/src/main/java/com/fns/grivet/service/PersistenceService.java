package com.fns.grivet.service;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.integration.MessageRejectedException;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;

import com.codahale.metrics.MetricRegistry;
import com.fns.grivet.model.Op;

public class PersistenceService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final EntityService entityService;
	private final MetricRegistry metricRegistry;

	public PersistenceService(EntityService entityService, MetricRegistry metricRegistry) {
		this.entityService = entityService;
		this.metricRegistry = metricRegistry;
	}

	@ServiceActivator(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
	public Object store(Message<JSONObject> message) {
		log.trace("Received message.  Headers - {}.  Payload - {}", message.getHeaders().toString(),
				message.getPayload().toString());
		Object result = null;
		try {
			Assert.notNull(message.getHeaders(), "No message headers!");
			Assert.notNull(message.getPayload(), "Message must have non-null payload!");
	
			Op op = message.getHeaders().get("op", Op.class);
			Assert.notNull(op, "Message header must contain an op code!");
	
			String type = null;
			Long oid = null;
		
			switch (op) {
				case CREATE:
					type = message.getHeaders().get("type", String.class);
					Assert.hasText(type, "Message header must contain a type for create requests!");
					entityService.create(type, message.getPayload());
					metricRegistry.counter(MetricRegistry.name("store", "create", type, "count")).inc();
					log.info("Successfully created type [{}]", type);
					break;
				case UPDATE:
					oid = message.getHeaders().get("oid", Long.class);
					Assert.notNull(oid, "Message header must contain an oid for update requests!");
					type = entityService.update(oid, message.getPayload());
					metricRegistry.counter(MetricRegistry.name("store", "update", type, "count")).inc();
					log.info("Successfully updated type [{}]", type);
					break;
				case DELETE:
					oid = message.getHeaders().get("oid", Long.class);
					Assert.notNull(oid, "Message header must contain an oid for delete requests!");
					type = entityService.delete(oid);
					metricRegistry.counter(MetricRegistry.name("store", "delete", type, "count")).inc();
					log.info("Successfully deleted type [{}]", type);
					break;
				default:
					throw new MessageRejectedException(message, "Bad payload!");
			}
			result = MessageBuilder.fromMessage(message).setHeader("processed", true);
			
		} catch (Exception e) {
			log.error("Op not available.  Headers - {}.  Payload - {}", message.getHeaders().toString(),
					message.getPayload().toString());
			result = MessageBuilder.fromMessage(message)
						.setHeader("processed", false)
						.setHeader("exception", e.getMessage());
		}
		return result;
	}
}
