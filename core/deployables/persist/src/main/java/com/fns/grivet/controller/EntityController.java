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
import java.net.URI;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fns.grivet.service.EntityService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * Provides end-points for type storage and retrieval
 *
 * @author Chris Phillipson
 */
@RestController
@RequestMapping("/store")
@Api(value = "store", produces = "application/json")
public class EntityController {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Value("${grivet.store.batch-size:100}")
	private int batchSize;

	private final EntityService entityService;

	private final MetricRegistry metricRegistry;

	@Autowired
	public EntityController(EntityService entityService, MetricRegistry metricRegistry) {
		this.entityService = entityService;
		this.metricRegistry = metricRegistry;
	}

	@Profile("!pipeline")
	@PreAuthorize("hasRole(@roles.ADMIN) or hasRole(@roles.USER)")
	@RequestMapping(value = "/{type}", method = RequestMethod.POST,
	consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", notes = "Store a type.", value = "/store/{type}")
	@ApiResponses({ @ApiResponse(code = 201, message = "Successfully store type."),
		@ApiResponse(code = 400, message = "Bad request."),
		@ApiResponse(code = 500, message = "Internal server error.") })
	public ResponseEntity<?> createSingle(@PathVariable("type") String type, @RequestBody JSONObject json)
			throws IOException {
		Long oid = entityService.create(type, json);
		URI location = UriComponentsBuilder.newInstance().path("/store").queryParam("oid", oid).build().toUri();
		metricRegistry.counter(MetricRegistry.name("store", "create", type, "count")).inc();
		log.info("Successfully created type [{}]", type);
		return ResponseEntity.created(location).build();
	}

	@Profile("!pipeline")
	@PreAuthorize("hasRole(@roles.ADMIN) or hasRole(@roles.USER)")
	@RequestMapping(value = "/{type}/batch", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", notes = "Store multiple types.", value = "/store/{type}/batch")
	@ApiResponses({ @ApiResponse(code = 201, message = "Successfully store types."),
		@ApiResponse(code = 202, message = "Partial success. Error details for type(s) that could not be registered."),
		@ApiResponse(code = 400, message = "Bad request."),
		@ApiResponse(code = 500, message = "Internal server error.") })
	public ResponseEntity<?> createMultiple(@PathVariable("type") String type, @RequestBody JSONArray json)
			throws IOException, JSONException {
		int numberOfTypesToCreate = json.length();
		Assert.isTrue(numberOfTypesToCreate <= batchSize,
				String.format(
						"The total number of entries in a request must not exceed %d! Your store request contained [%d] entries.",
						batchSize, numberOfTypesToCreate));
		int errorCount = 0;
		JSONObject jsonObject = null;
		HttpHeaders headers = new HttpHeaders();
		URI location = null;
		Long oid = null;
		// allow for all JSONObjects within JSONArray to be processed; capture and report errors during processing
		for (int i = 0; i < numberOfTypesToCreate; i++) {
			try {
				jsonObject = json.getJSONObject(i);
				oid = entityService.create(type, jsonObject);
				location = UriComponentsBuilder.newInstance().path("/store").queryParam("oid", oid).build().toUri();
				if (numberOfTypesToCreate == 1) {
					headers.setLocation(location);
				} else {
					headers.set(String.format("Location[%s]", String.valueOf(i + 1)), location.toASCIIString());
				}
				metricRegistry.counter(MetricRegistry.name("store", "create", type, "count")).inc();
				log.info("Successfully created type [{}]", type);
			} catch (Exception e) {
				String message = LogUtil.toLog(jsonObject, String.format("Problems storing type! Portion of payload @ index[%d]\n", i+1));
				log.error(message, e);
				if (numberOfTypesToCreate == 1) {
					throw e;
				}
				headers.set(String.format("Error[%s]", String.valueOf(i+1)), e.getMessage());
				errorCount++;
			}
		}
		return new ResponseEntity<>(headers, ((errorCount == 0) ? HttpStatus.CREATED : HttpStatus.ACCEPTED));
	}

	@Profile("!pipeline")
	@PreAuthorize("hasRole(@roles.ADMIN) or hasRole(@roles.USER)")
	@RequestMapping(value = "", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PATCH", notes = "Update an existing type.", value = "/store?oid={oid}")
	@ApiResponses({ @ApiResponse(code = 200, message = "Successfully updated type."),
		@ApiResponse(code = 400, message = "Bad request."),
		@ApiResponse(code = 500, message = "Internal server error.") })
	public ResponseEntity<?> update(
			@ApiParam(value = "Object identifier", required = true) @RequestParam(value = "oid", required = true) Long oid,
			@RequestBody JSONObject json)
					throws IOException {
		String type = entityService.update(oid, json);
		HttpHeaders headers = new HttpHeaders();
		URI location = UriComponentsBuilder.newInstance().path("/store").queryParam("oid", oid).build().toUri();
		headers.setLocation(location);
		metricRegistry.counter(MetricRegistry.name("store", "update", type, "count")).inc();
		log.info("Successfully updated type [{}]", type);
		return ResponseEntity.ok().headers(headers).build();
	}

	@PreAuthorize("hasRole(@roles.ADMIN) or hasRole(@roles.USER)")
	@RequestMapping(value = "/{type}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", notes = "Retrieve type matching criteria.", value = "/store/{type}")
	@ApiResponses({ @ApiResponse(code = 200, message = "Successfully retrieve type matching criteria."),
		@ApiResponse(code = 400, message = "Bad request."),
		@ApiResponse(code = 500, message = "Internal server error.") })
	public ResponseEntity<?> get(@PathVariable("type") String type,
			@RequestParam(value = "createdTimeStart", required = false) LocalDateTime createdTimeStart,
			@RequestParam(value = "createdTimeEnd", required = false) LocalDateTime createdTimeEnd,
			HttpServletRequest request) throws JsonProcessingException {
		LocalDateTime start = createdTimeStart == null ? LocalDateTime.now().minusDays(7) : createdTimeStart;
		LocalDateTime end = createdTimeEnd == null ? LocalDateTime.now() : createdTimeEnd;
		Assert.isTrue(ChronoUnit.SECONDS.between(start, end) >= 0, "Store request constraint createdTimeStart must be earlier or equal to createdTimeEnd!");
		return ResponseEntity.ok(entityService.findByCreatedTime(type, start, end, request.getParameterMap()));
	}

	@PreAuthorize("hasRole(@roles.ADMIN) or hasRole(@roles.USER)")
	@RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", notes = "Retrieve type by its object identifier.", value = "/store?oid={oid}")
	@ApiResponses({ @ApiResponse(code = 200, message = "Successfully retrieve a type by its object identifier."),
		@ApiResponse(code = 400, message = "Bad request."),
		@ApiResponse(code = 404, message = "Type not found."),
		@ApiResponse(code = 500, message = "Internal server error.") })
	public ResponseEntity<?> get(
			@ApiParam(value = "Object identifier", required = true)
			@RequestParam(value = "oid", required = true) Long oid) throws JsonProcessingException {
		return ResponseEntity.ok(entityService.findOne(oid));
	}

	@PreAuthorize("hasRole(@roles.ADMIN) or hasRole(@roles.USER)")
	@RequestMapping(value = "/{type}/noAudit", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", notes = "Retrieve all records for type. No audit trail, only most recent records.", value = "/store/{type}/noAudit")
	@ApiResponses({ @ApiResponse(code = 200, message = "Successfully retrieve type all records for type."),
		@ApiResponse(code = 400, message = "Bad request."),
		@ApiResponse(code = 500, message = "Internal server error.") })
	public ResponseEntity<?> get(@PathVariable("type") String type) throws JsonProcessingException {
		return ResponseEntity.ok(entityService.findAllByType(type));
	}

}
