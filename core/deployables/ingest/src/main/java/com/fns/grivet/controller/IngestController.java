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

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.MetricRegistry;
import com.fns.grivet.service.Ingester;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * Ingestion end-points
 *
 * @author Chris Phillipson
 */
@RestController
@RequestMapping("/ingester")
@Api(value = "ingester", produces = "application/json")
public class IngestController {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Value("${grivet.ingest.batch-size:100}")
	private int batchSize;

	private final Ingester ingestService;

	private final MetricRegistry metricRegistry;

	@Autowired
	public IngestController(Ingester entityService, MetricRegistry metricRegistry) {
		this.ingestService = entityService;
		this.metricRegistry = metricRegistry;
	}

	@PreAuthorize("hasRole(@roles.ADMIN) or hasRole(@roles.USER)")
	@RequestMapping(value = "/{type}", method = RequestMethod.POST,
	consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", notes = "Store a type.", value = "/ingester/{type}")
	@ApiResponses({ @ApiResponse(code = 204, message = "Successfully ingest type."),
		@ApiResponse(code = 202, message = "Partial success. Error details for type(s) that could not be registered."),
		@ApiResponse(code = 400, message = "Bad request."),
		@ApiResponse(code = 500, message = "Internal server error.") })
	public ResponseEntity<?> createSingle(@PathVariable("type") String type, @RequestBody JSONObject json)
			throws IOException {
		ingestService
				.ingest(MessageBuilder.withPayload(json).setHeader("type", type).setHeader("op", "create").build());
		metricRegistry.counter(MetricRegistry.name("ingest", type, "count")).inc();
		log.info("Successfully ingested create request for type [{}]", type);
		return ResponseEntity.accepted().build();
	}

	@PreAuthorize("hasRole(@roles.ADMIN) or hasRole(@roles.USER)")
	@RequestMapping(value = "", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PATCH", notes = "Update an existing type.", value = "/ingester?oid={oid}")
	@ApiResponses({ @ApiResponse(code = 200, message = "Successfully updated type."),
			@ApiResponse(code = 400, message = "Bad request."),
			@ApiResponse(code = 500, message = "Internal server error.") })
	public ResponseEntity<?> updateSingle(
			@ApiParam(value = "Object identifier", required = true) @RequestParam(value = "oid", required = true) Long oid,
			@RequestBody JSONObject json) throws IOException {
		ingestService.ingest(MessageBuilder.withPayload(json).setHeader("oid", oid).setHeader("op", "update").build());
		metricRegistry.counter(MetricRegistry.name("ingest", "update", "count")).inc();
		log.info("Successfully ingested update request for type w/ oid = [{}]", oid);
		return ResponseEntity.accepted().build();
	}

	@PreAuthorize("hasRole(@roles.ADMIN) or hasRole(@roles.USER)")
	@RequestMapping(value = "/{type}/batch", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", notes = "Store multiple types.", value = "/ingester/{type}/batch")
	@ApiResponses({ @ApiResponse(code = 204, message = "Successfully ingest types."),
		@ApiResponse(code = 202, message = "Partial success. Error details for type(s) that could not be registered."),
		@ApiResponse(code = 400, message = "Bad request."),
		@ApiResponse(code = 500, message = "Internal server error.") })
	public ResponseEntity<?> createMultiple(@PathVariable("type") String type, @RequestBody JSONArray json)
			throws IOException, JSONException {
		int numberOfTypesToCreate = json.length();
		Assert.isTrue(numberOfTypesToCreate <= batchSize,
				String.format(
						"The total number of entries in a request must not exceed %d! Your ingest request contained [%d] entries.",
						batchSize, numberOfTypesToCreate));
		JSONObject jsonObject = null;
		HttpHeaders headers = new HttpHeaders();
		// allow for all JSONObjects within JSONArray to be processed; capture and report errors during processing
		for (int i = 0; i < numberOfTypesToCreate; i++) {
			try {
				jsonObject = json.getJSONObject(i);
				ingestService.ingest(MessageBuilder.withPayload(jsonObject).setHeader("type", type)
						.setHeader("op", "create").build());
				metricRegistry.counter(MetricRegistry.name("ingest", "create", type, "count")).inc();
				log.info("Successfully ingested create request for type [{}]", type);
			} catch (Exception e) {
				String message = LogUtil.toLog(jsonObject,
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

}
