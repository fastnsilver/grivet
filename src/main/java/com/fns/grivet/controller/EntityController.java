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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fns.grivet.service.EntityService;

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
@RequestMapping("/store")
@Api(value = "store", produces = "application/json")
@Secured(value = { "ROLE_ADMIN", "ROLE_USER" })
public class EntityController {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private final EntityService entityService;
    private MetricRegistry metricRegistry;
    
    @Autowired
    public EntityController(EntityService entityService) {
        this.entityService = entityService;
    }
    
    @Autowired
    public void setMetricRegistry(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    @RequestMapping(value="/{type}", method=RequestMethod.POST, 
            consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", notes = "Store one or more type.", value = "/store/{type}")
    @ApiResponses(value = { 
            @ApiResponse(code = 204, message = "Successfully store type(s)."),
            @ApiResponse(code = 202, message = "Partial success. Error details for type(s) that could not be registered."),
            @ApiResponse(code = 400, message = "Bad request."),
            @ApiResponse(code = 500, message = "Internal server error.")
            })
    public ResponseEntity<?> create(@PathVariable("type") String type, HttpServletRequest request) throws IOException {
        String json = IOUtils.toString(request.getInputStream(), "UTF-8");
        Assert.isTrue(json.startsWith("{") || json.startsWith("["), "Store requests must be valid JSON starting with either a { or [!");
        ResponseEntity<?> result = ResponseEntity.unprocessableEntity().build();
        if (json.startsWith("{")) {
            result = createSingleType(type, json);
        }
        if (json.startsWith("[")) {
            result = createMultipleTypes(type, json);
        }
        return result;
    }
    
    private ResponseEntity<?> createSingleType(String type, String json) {
        entityService.create(type, new JSONObject(json));
        metricRegistry.counter(MetricRegistry.name("store", type, "count")).inc();
        log.info("Successfully stored type [{}]", type);
        return ResponseEntity.noContent().build();
    }
    
    private ResponseEntity<?> createMultipleTypes(String type, String json) {
        JSONArray jsonArray = new JSONArray(json);
        Assert.isTrue(jsonArray.length() <= 100, String.format("The total number of entries in a request must not exceed 100! The number of entries in your store request was [%d].", jsonArray.length()));
        int numberOfTypesToCreate = jsonArray.length();
        int errorCount = 0;
        JSONObject jsonObject = null;
        HttpHeaders headers = new HttpHeaders();
        // allow for all JSONObjects within JSONArray to be processed; capture and report errors during processing
        for (int i = 0; i < numberOfTypesToCreate; i++) {
            jsonObject = jsonArray.getJSONObject(i);
            try {
                entityService.create(type, jsonObject);
                metricRegistry.counter(MetricRegistry.name("store", type, "count")).inc();
                log.info("Successfully stored type [{}]", type);
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
        HttpStatus status = (errorCount == 0) ? HttpStatus.NO_CONTENT : HttpStatus.ACCEPTED;
        return new ResponseEntity<>(headers, status);
    }
    
    @RequestMapping(value="/{type}", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", notes = "Retrieve type matching criteria.", value = "/store/{type}")
    @ApiResponses(value = { 
            @ApiResponse(code = 200, message = "Successfully retrieve type matching criteria."),
            @ApiResponse(code = 400, message = "Bad request."),
            @ApiResponse(code = 500, message = "Internal server error.")
            })
    public ResponseEntity<?> get(@PathVariable("type") String type, @RequestParam(value="createdTimeStart", required=false) String createdTimeStart, @RequestParam(value="createdTimeEnd", required=false) String createdTimeEnd, HttpServletRequest request) throws JsonProcessingException {
        LocalDateTime start = createdTimeStart == null ? LocalDateTime.now().minusDays(7): LocalDateTime.parse(createdTimeStart);
        LocalDateTime end = createdTimeEnd == null ? LocalDateTime.now() : LocalDateTime.parse(createdTimeEnd);
        Assert.isTrue(ChronoUnit.SECONDS.between(start, end) >= 0, "Store request constraint createdTimeStart must be earlier or equal to createdTimeEnd!");
        String result = entityService.get(type, start, end, request.getParameterMap().entrySet());
        return ResponseEntity.ok(result);
    }
    
}
