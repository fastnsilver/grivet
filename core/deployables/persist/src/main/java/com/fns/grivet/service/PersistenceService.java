package com.fns.grivet.service;

import java.util.UUID;

import org.json.JSONObject;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.integration.MessageRejectedException;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.util.Assert;

import com.fns.grivet.model.Op;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PersistenceService {

	private final EntityService entityService;
	private final MeterRegistry meterRegistry;

	public PersistenceService(EntityService entityService, MeterRegistry meterRegistry) {
		this.entityService = entityService;
		this.meterRegistry = meterRegistry;
	}

	@StreamListener(Sink.INPUT)
	public void store(Message<JSONObject> message) {
		log.trace("Received message.  Headers - {}.  Payload - {}", message.getHeaders().toString(),
				message.getPayload().toString());
		Message<JSONObject> result = null;
		try {
			Assert.notNull(message.getHeaders(), "No message headers!");
			Assert.notNull(message.getPayload(), "Message must have non-null payload!");
	
			Op op = Op.fromValue(message.getHeaders().get("op", String.class));
			Assert.notNull(op, "Message header must contain an op code!");
	
			String type = null;
			UUID oid = null;
		
			switch (op) {
				case CREATE:
					type = message.getHeaders().get("type", String.class);
					Assert.hasText(type, "Message header must contain a type for create requests!");
					entityService.create(type, message.getPayload());
					meterRegistry.counter(String.join("store", "create", type)).increment();
					log.info("Successfully created type [{}]", type);
					break;
				case UPDATE:
					oid = message.getHeaders().get("oid", UUID.class);
					Assert.notNull(oid, "Message header must contain an oid for update requests!");
					type = entityService.update(oid, message.getPayload());
					meterRegistry.counter(String.join("store", "update", type)).increment();
					log.info("Successfully updated type [{}]", type);
					break;
				case DELETE:
					oid = message.getHeaders().get("oid", UUID.class);
					Assert.notNull(oid, "Message header must contain an oid for delete requests!");
					type = entityService.delete(oid);
					meterRegistry.counter(String.join("store", "delete", type)).increment();
					log.info("Successfully deleted type [{}]", type);
					break;
				default:
					throw new MessageRejectedException(message, String.format("Bad payload! Invalid op [%s].", op.name()));
			}
			
		} catch (Exception e) {
			log.error("Problem processing message.  Headers - {}.  Payload - {}", message.getHeaders().toString(),
					message.getPayload().toString(), e);
			result = MessageBuilder.fromMessage(message)
						.setHeader("processed", false)
						.setHeader("exception", e.getMessage()).build();
			throw new MessageHandlingException(result, "Could not process message!");
		}
	}
}
