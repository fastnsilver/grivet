package com.fns.grivet.service;

import java.util.function.Consumer;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.MessageRejectedException;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.fns.grivet.model.Op;

import io.micrometer.core.instrument.MeterRegistry;


@Service
@Profile("pipeline")
public class PersistenceService {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PersistenceService.class);

	private final EntityService entityService;
	private final MeterRegistry meterRegistry;

	@Autowired
	public PersistenceService(EntityService entityService, MeterRegistry meterRegistry) {
		this.entityService = entityService;
		this.meterRegistry = meterRegistry;
	}

	@Bean
	public Consumer<Message<JSONObject>> store() {
		return message -> {
			log.trace("Received message.  Headers - {}.  Payload - {}", message.getHeaders().toString(),
					message.getPayload().toString());
			Message<JSONObject> result = null;
			try {
				Assert.notNull(message.getHeaders(), "No message headers!");
				Assert.notNull(message.getPayload(), "Message must have non-null payload!");

				Op op = Op.fromValue(message.getHeaders().get("op", String.class));
				Assert.notNull(op, "Message header must contain an op code!");

				String type = null;
				Long oid = null;

				switch (op) {
					case CREATE:
						type = message.getHeaders().get("type", String.class);
						Assert.hasText(type, "Message header must contain a type for create requests!");
						entityService.create(type, message.getPayload());
						meterRegistry.counter(String.join("store", "create", type)).increment();
						log.info("Successfully created type [{}]", type);
						break;
					case UPDATE:
						oid = message.getHeaders().get("oid", Long.class);
						Assert.notNull(oid, "Message header must contain an oid for update requests!");
						type = entityService.update(oid, message.getPayload());
						meterRegistry.counter(String.join("store", "update", type)).increment();
						log.info("Successfully updated type [{}]", type);
						break;
					case DELETE:
						oid = message.getHeaders().get("oid", Long.class);
						Assert.notNull(oid, "Message header must contain an oid for delete requests!");
						type = entityService.delete(oid);
						meterRegistry.counter(String.join("store", "delete", type)).increment();
						log.info("Successfully deleted type [{}]", type);
						break;
					default:
						throw new MessageRejectedException(message, "Bad payload! Invalid op [%s].".formatted(op.name()));
				}

			} catch (Exception e) {
				log.error("Problem processing message.  Headers - {}.  Payload - {}", message.getHeaders().toString(),
						message.getPayload().toString(), e);
				result = MessageBuilder.fromMessage(message)
							.setHeader("processed", false)
							.setHeader("exception", e.getMessage()).build();
				throw new MessageHandlingException(result, "Could not process message!");
			}
		};
	}
}
