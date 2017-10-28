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

import java.net.URI;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Profile;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fns.grivet.service.EntityService;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;


/**
 * Provides end-points for type storage and retrieval
 *
 * @author Chris Phillipson
 */
@Slf4j
@RefreshScope
@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class EntityController {

	@Value("${grivet.store.batch-size:100}")
	private int batchSize;

	private final EntityService entityService;

	private final MeterRegistry meterRegistry;

	@Autowired
	public EntityController(EntityService entityService, MeterRegistry meterRegistry) {
		this.entityService = entityService;
		this.meterRegistry = meterRegistry;
	}

	@Profile("!pipeline")
	@PreAuthorize("hasAuthority('write:type')")
	@PostMapping("/api/v1/type")
	public ResponseEntity<?> createOne(@RequestHeader("Type") String type, @RequestBody JSONObject json) {
		UUID oid = entityService.create(type, json);
		URI location = UriComponentsBuilder.newInstance().path("/api/v1/type").queryParam("oid", oid).build().toUri();
		meterRegistry.counter(String.join("store", "create", type)).increment();
		log.info("Successfully created type [{}]", type);
		return ResponseEntity.created(location).build();
	}

	@Profile("!pipeline")
	@PreAuthorize("hasAuthority('write:type')")
	@PostMapping("/api/v1/types")
	public ResponseEntity<?> createMultiple(@RequestHeader("Type") String type, @RequestBody JSONArray json) {
		int numberOfTypesToCreate = json.length();
		Assert.isTrue(numberOfTypesToCreate <= batchSize,
				String.format(
						"The total number of entries in a request must not exceed %d! Your store request contained [%d] entries.",
						batchSize, numberOfTypesToCreate));
		int errorCount = 0;
		JSONObject jsonObject = null;
		HttpHeaders headers = new HttpHeaders();
		URI location = null;
		UUID oid = null;
		// allow for all JSONObjects within JSONArray to be processed; capture and report errors during processing
		for (int i = 0; i < numberOfTypesToCreate; i++) {
			try {
				jsonObject = json.getJSONObject(i);
				oid = entityService.create(type, jsonObject);
				location = UriComponentsBuilder.newInstance().path("/api/v1/type").queryParam("oid", oid).build().toUri();
				if (numberOfTypesToCreate == 1) {
					headers.setLocation(location);
				} else {
					headers.set(String.format("Location[%s]", String.valueOf(i + 1)), location.toASCIIString());
				}
				meterRegistry.counter(String.join("store", "create", type)).increment();
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
	@PreAuthorize("hasAuthority('write:type')")
	@PatchMapping("/api/v1/type")
	public ResponseEntity<?> updateOne(
			@RequestParam(value = "oid", required = true) UUID oid,
			@RequestBody JSONObject json) {
		String type = entityService.update(oid, json);
		HttpHeaders headers = new HttpHeaders();
		URI location = UriComponentsBuilder.newInstance().path("/api/v1/type").queryParam("oid", oid).build().toUri();
		headers.setLocation(location);
		meterRegistry.counter(String.join("store", "update", type)).increment();
		log.info("Successfully updated type [{}]", type);
		return ResponseEntity.ok().headers(headers).build();
	}

	@Profile("!pipeline")
	@PreAuthorize("hasAuthority('delete:type')")
	@DeleteMapping("/api/v1/type")
	public ResponseEntity<?> deleteOne(
			@RequestParam(value = "oid", required = true) UUID oid) {
		String type = entityService.delete(oid);
		meterRegistry.counter(String.join("store", "delete", type)).increment();
		log.info("Successfully delete type [{}]", type);
		return ResponseEntity.ok().build();
	}

	@PreAuthorize("hasAuthority('read:type')")
	@GetMapping("/api/v1/type/{type}")
	public ResponseEntity<?> fetch(@PathVariable("type") String type,
			@RequestParam(value = "createdTimeStart", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdTimeStart,
			@RequestParam(value = "createdTimeEnd", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdTimeEnd,
			@RequestParam(value = "noAudit", defaultValue = "false") boolean noAudit,
			HttpServletRequest request) throws JsonProcessingException {
		LocalDateTime start = createdTimeStart == null ? LocalDateTime.now().minusDays(7) : createdTimeStart;
		LocalDateTime end = createdTimeEnd == null ? LocalDateTime.now() : createdTimeEnd;
		Assert.isTrue(ChronoUnit.SECONDS.between(start, end) >= 0, "Store request constraint createdTimeStart must be earlier or equal to createdTimeEnd!");
		if (noAudit) {
		    return ResponseEntity.ok(entityService.findAllByType(type));
		} else {
		    return ResponseEntity.ok(entityService.findByCreatedTime(type, start, end, request.getParameterMap()));
		}
	}

	@PreAuthorize("hasAuthority('read:type')")
	@GetMapping("/api/v1/type")
	public ResponseEntity<?> fetchOne(
			@RequestParam(value = "oid", required = true) UUID oid) {
		return ResponseEntity.ok(entityService.findById(oid));
	}

}
