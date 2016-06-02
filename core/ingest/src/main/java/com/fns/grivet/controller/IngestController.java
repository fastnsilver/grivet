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

import com.codahale.metrics.MetricRegistry;
import com.fns.grivet.service.IngestService;

import org.json.JSONArray;
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
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * Provides end-points for type storage and retrieval
 * 
 * @author Chris Phillipson
 */
@RestController
@RequestMapping("/type/ingest")
@Api(value = "type/ingest", produces = "application/json")
public class IngestController {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    @Value("${grivet.ingest.batch-size:100}")
    int batchSize;
    
    private final IngestService ingestService;
    
    private final MetricRegistry metricRegistry;

    @Autowired
    public IngestController(IngestService entityService, MetricRegistry metricRegistry) {
        this.ingestService = entityService;
        this.metricRegistry = metricRegistry;
    }
    
    @PreAuthorize("hasRole(@roles.ADMIN) or hasRole(@roles.USER)")
    @RequestMapping(value = "/{type}", method = RequestMethod.POST, 
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", notes = "Store a type.", value = "/type/ingest/{type}")
    @ApiResponses({ @ApiResponse(code = 204, message = "Successfully ingest type."),
            @ApiResponse(code = 202, message = "Partial success. Error details for type(s) that could not be registered."),
            @ApiResponse(code = 400, message = "Bad request."),
            @ApiResponse(code = 500, message = "Internal server error.") })
    public ResponseEntity<?> createSingle(@PathVariable("type") String type, @RequestBody String payload)
            throws IOException {
        JSONObject json = new JSONObject(payload);
        ingestService.ingest(MessageBuilder.withPayload(json).setHeader("type", type).build());
        metricRegistry.counter(MetricRegistry.name("ingest", type, "count")).inc();
        log.info("Successfully ingested type [{}]", type);
        return ResponseEntity.accepted().build();
    }

    @PreAuthorize("hasRole(@roles.ADMIN) or hasRole(@roles.USER)")
    @RequestMapping(value = "/batch/{type}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", notes = "Store multiple types.", value = "/type/ingest/batch/{type}")
    @ApiResponses({ @ApiResponse(code = 204, message = "Successfully ingest types."),
            @ApiResponse(code = 202, message = "Partial success. Error details for type(s) that could not be registered."),
            @ApiResponse(code = 400, message = "Bad request."),
            @ApiResponse(code = 500, message = "Internal server error.") })
    public ResponseEntity<?> createMultiple(@PathVariable("type") String type, @RequestBody String payload)
            throws IOException {
        JSONArray json = new JSONArray(payload);
        int numberOfTypesToCreate = json.length();
        Assert.isTrue(numberOfTypesToCreate <= batchSize,
                String.format(
                        "The total number of entries in a request must not exceed %d! Your ingest request contained [%d] entries.",
                        batchSize, numberOfTypesToCreate));
        JSONObject jsonObject = null;
        HttpHeaders headers = new HttpHeaders();
        // allow for all JSONObjects within JSONArray to be processed; capture and report errors during processing
        for (int i = 0; i < numberOfTypesToCreate; i++) {
            jsonObject = json.getJSONObject(i);
            try {
                ingestService.ingest(MessageBuilder.withPayload(jsonObject).setHeader("type", type).build());
                metricRegistry.counter(MetricRegistry.name("ingest", type, "count")).inc();
                log.info("Successfully ingested type [{}]", type);
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
