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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fns.grivet.service.EntityService;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

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
@RequestMapping("/type/store")
@Api(value = "type/store", produces = "application/json")
public class EntityController {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    @Value("${grivet.store.batch-size:100}")
    private int batchSize;
    
    private final EntityService entityService;
    
    @Autowired
    public EntityController(EntityService entityService) {
        this.entityService = entityService;
    }
    
    @PreAuthorize(value = "hasRole(@roles.ADMIN) or hasRole(@roles.USER)")
    @RequestMapping(value = "/{type}", method = RequestMethod.POST, 
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", notes = "Store a type.", value = "/type/store/{type}")
    @ApiResponses(value = { @ApiResponse(code = 204, message = "Successfully store type."),
            @ApiResponse(code = 202, message = "Partial success. Error details for type(s) that could not be registered."),
            @ApiResponse(code = 400, message = "Bad request."),
            @ApiResponse(code = 500, message = "Internal server error.") })
    public ResponseEntity<?> createSingle(@PathVariable("type") String type, @RequestBody String payload)
            throws IOException {
        JSONObject json = new JSONObject(payload);
        entityService.create(type, json);
        log.info("Successfully stored type [{}]", type);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize(value = "hasRole(@roles.ADMIN) or hasRole(@roles.USER)")
    @RequestMapping(value = "/batch/{type}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", notes = "Store multiple types.", value = "/type/store/batch/{type}")
    @ApiResponses(value = { 
            @ApiResponse(code = 204, message = "Successfully store types."),
            @ApiResponse(code = 202, message = "Partial success. Error details for type(s) that could not be registered."),
            @ApiResponse(code = 400, message = "Bad request."),
            @ApiResponse(code = 500, message = "Internal server error.")
            })
    public ResponseEntity<?> createMultiple(@PathVariable("type") String type, @RequestBody String payload)
            throws IOException {
        JSONArray json = new JSONArray(payload);
        int numberOfTypesToCreate = payload.length();
        Assert.isTrue(numberOfTypesToCreate <= batchSize,
                String.format(
                        "The total number of entries in a request must not exceed %d! Your store request contained [%d] entries.",
                        batchSize, numberOfTypesToCreate));
        int errorCount = 0;
        JSONObject jsonObject = null;
        HttpHeaders headers = new HttpHeaders();
        // allow for all JSONObjects within JSONArray to be processed; capture and report errors during processing
        for (int i = 0; i < numberOfTypesToCreate; i++) {
            jsonObject = json.getJSONObject(i);
            try {
                entityService.create(type, jsonObject);
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
    
    @PreAuthorize(value = "hasRole(@roles.ADMIN) or hasRole(@roles.USER)")
    @RequestMapping(value = "/{type}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", notes = "Retrieve type matching criteria.", value = "/type/store/{type}")
    @ApiResponses(value = { 
            @ApiResponse(code = 200, message = "Successfully retrieve type matching criteria."),
            @ApiResponse(code = 400, message = "Bad request."),
            @ApiResponse(code = 500, message = "Internal server error.")
            })
    public ResponseEntity<?> get(@PathVariable("type") String type,
            @RequestParam(value = "createdTimeStart", required = false) LocalDateTime createdTimeStart,
            @RequestParam(value = "createdTimeEnd", required = false) LocalDateTime createdTimeEnd,
            @RequestParam Map<String, String[]> parameters) throws JsonProcessingException {
        LocalDateTime start = createdTimeStart == null ? LocalDateTime.now().minusDays(7) : createdTimeStart;
        LocalDateTime end = createdTimeEnd == null ? LocalDateTime.now() : createdTimeEnd;
        Assert.isTrue(ChronoUnit.SECONDS.between(start, end) >= 0, "Store request constraint createdTimeStart must be earlier or equal to createdTimeEnd!");
        String result = entityService.findByCreatedTime(type, start, end, parameters);
        return ResponseEntity.ok(result);
    }
    
}
