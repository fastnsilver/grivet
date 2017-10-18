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
package com.fns.grivet.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fns.grivet.model.Op;
import com.fns.grivet.service.Ingester;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;


/**
 * Ingestion end-points
 *
 * @author Chris Phillipson
 */
@Slf4j
@RefreshScope
@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class IngestController {

	@Value("${grivet.ingest.batch-size:100}")
	private int batchSize;

	private final Ingester ingestService;
	private final MeterRegistry meterRegistry;

	@Autowired
	public IngestController(Ingester entityService, MeterRegistry meterRegistry) {
		this.ingestService = entityService;
		this.meterRegistry = meterRegistry;
	}

	@PreAuthorize("hasAuthority('write:type')")
	@PostMapping("/api/v1/type")
	public ResponseEntity<?> ingestCreateTypeRequest(@RequestHeader("Type") String type, @RequestBody JSONObject payload) {
	    ingestService
				.ingest(MessageBuilder.withPayload(payload).setHeader("type", type).setHeader("op", Op.CREATE.name()).build());
	    meterRegistry.counter(String.join("ingest", "create", type)).increment();
		log.info("Successfully ingested create request for type [{}]", type);
		return ResponseEntity.accepted().build();
	}
	
	@PreAuthorize("hasAuthority('write:type')")
	@PostMapping("/api/v1/types")
	public ResponseEntity<?> ingestCreateTypesRequest(@RequestHeader("Type") String type, @RequestBody JSONArray array) {
		int numberOfTypesToCreate = array.length();
		Assert.isTrue(numberOfTypesToCreate <= batchSize,
				String.format(
						"The total number of entries in a request must not exceed %d! Your ingest request contained [%d] entries.",
						batchSize, numberOfTypesToCreate));
		JSONObject payload = null;
		HttpHeaders headers = new HttpHeaders();
		// allow for all JSONObjects within JSONArray to be processed; capture and report errors during processing
		for (int i = 0; i < numberOfTypesToCreate; i++) {
			try {
				payload = array.getJSONObject(i);
				ingestService.ingest(MessageBuilder.withPayload(payload).setHeader("type", type)
						.setHeader("op", Op.CREATE.name()).build());
				meterRegistry.counter(String.join("ingest", "create", type)).increment();
				log.info("Successfully ingested create request for type [{}]", type);
			} catch (Exception e) {
				String message = LogUtil.toLog(payload,
						String.format("Problems ingesting type! Portion of payload @ index[%d]\n", i + 1));
				log.error(message, e);
				if (numberOfTypesToCreate == 1) {
					throw e;
				}
				headers.set(String.format("Error[%s]", String.valueOf(i+1)), e.getMessage());
			}
		}
		return ResponseEntity.accepted().headers(headers).build();
	}

	@PreAuthorize("hasAuthority('write:type')")
	@PatchMapping("/api/v1/type")
	public ResponseEntity<?> ingestUpdateTypeRequest(
		@RequestParam(value = "oid", required = true) Long oid,
		@RequestBody JSONObject payload) {
		ingestService.ingest(MessageBuilder.withPayload(payload).setHeader("oid", oid).setHeader("op", Op.UPDATE.name()).build());
		meterRegistry.counter(String.join("ingest", "update")).increment();
		log.info("Successfully ingested update request for type w/ oid = [{}]", oid);
		return ResponseEntity.accepted().build();
	}
	
	@PreAuthorize("hasAuthority('delete:type')")
	@DeleteMapping(value = "/api/v1/type")
	public ResponseEntity<?> ingestDeleteTypeRequest(
		@RequestParam(value = "oid", required = true) Long oid) {
		ingestService.ingest(MessageBuilder.withPayload(new JSONObject()).setHeader("oid", oid).setHeader("op", Op.DELETE.name()).build());
		meterRegistry.counter(String.join("ingest", "delete")).increment();
		log.info("Successfully ingested update request for type w/ oid = [{}]", oid);
		return ResponseEntity.accepted().build();
	}

}
