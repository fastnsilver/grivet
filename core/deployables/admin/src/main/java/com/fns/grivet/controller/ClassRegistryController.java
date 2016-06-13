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

import com.fns.grivet.service.ClassRegistryService;

import org.json.JSONArray;
import org.json.JSONException;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * Provides end-points for type registration and verification
 * 
 * @author Chris Phillipson
 */
@RestController
@RequestMapping("/register")
@Api(value = "register", produces = "application/json")
public class ClassRegistryController {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    @Value("${grivet.register.batch-size:100}")
    int batchSize;
    
    private final ClassRegistryService classRegistryService;
    
    @Autowired
    public ClassRegistryController(ClassRegistryService classRegistryService) {
        this.classRegistryService = classRegistryService;
    }
    
    @PreAuthorize("hasRole(@roles.ADMIN)")
    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", notes = "Register a type", value = "/register")
    @ApiResponses({ @ApiResponse(code = 201, message = "Successfully registered type."),
            @ApiResponse(code = 202, message = "Partial success. Location info for registered type(s). Error details for type(s) that could not be registered."),
            @ApiResponse(code = 400, message = "Bad request."),
            @ApiResponse(code = 500, message = "Internal server error.") })
    public ResponseEntity<?> registerSingle(@RequestBody JSONObject json) throws IOException {
        String type = classRegistryService.register(json);
        UriComponentsBuilder ucb = UriComponentsBuilder.newInstance();
        log.info("Type [{}] successfully registered!", type);
        return ResponseEntity.created(ucb.path("/register/{type}").buildAndExpand(type).toUri()).build();
    }

    @PreAuthorize("hasRole(@roles.ADMIN)")
    @RequestMapping(value = "/types", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", notes = "Register multiple types", value = "/register/types")
    @ApiResponses({ @ApiResponse(code = 201, message = "Successfully registered types."),
            @ApiResponse(code = 202, message = "Partial success. Location info for registered type(s). Error details for type(s) that could not be registered."),
            @ApiResponse(code = 400, message = "Bad request."),
            @ApiResponse(code = 500, message = "Internal server error.") })
    public ResponseEntity<?> registerMultiple(@RequestBody JSONArray json) throws IOException, JSONException {
        int numberOfTypesToRegister = json.length();
        Assert.isTrue(numberOfTypesToRegister <= batchSize,
                String.format(
                        "The total number of entries in a type registration request must not exceed %d! Your registration request contained [%d] entries.",
                        batchSize, numberOfTypesToRegister));
        JSONObject jsonObject = null;
        String type = null;
        HttpHeaders headers = new HttpHeaders();
        URI location = null;
        int errorCount = 0;
        // allow for all JSONObjects within JSONArray to be processed; capture and report errors during processing
        for (int i = 0; i < numberOfTypesToRegister; i++) {
            try {
                jsonObject = json.getJSONObject(i);
                type = classRegistryService.register(jsonObject);
                location = UriComponentsBuilder.newInstance().path("/register/{type}").buildAndExpand(type).toUri();
                if (numberOfTypesToRegister == 1) {
                    headers.setLocation(location); 
                } else {
                    headers.set(String.format("Location[%s]",String.valueOf(i+1)), location.toASCIIString());
                }
                log.info("Type [{}] successfully registered!", type);
            } catch (Exception e) {
                String message = LogUtil.toLog(jsonObject, String.format("Problems registering type! Portion of payload @ index[%d]\n", i+1));
                log.error(message, e);
                if (numberOfTypesToRegister == 1) {
                    throw e;
                }
                headers.set(String.format("Error[%s]", String.valueOf(i+1)), e.getMessage());
                errorCount++;
            }
        }
        return new ResponseEntity<>(headers, ((errorCount == 0) ? HttpStatus.CREATED : HttpStatus.ACCEPTED));
    }

    @PreAuthorize("hasRole(@roles.ADMIN)")
    @RequestMapping(value = "/{type}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", notes = "Delete the registered type.", value = "/register/{type}")
    @ApiResponses({ @ApiResponse(code = 204, message = "Successfully deleted a registered type."),
            @ApiResponse(code = 400, message = "Bad request."),
            @ApiResponse(code = 500, message = "Internal server error.") })
    public ResponseEntity<?> delete(
            @ApiParam(value = "Type name", required = true)
            @PathVariable("type") String type) {
        classRegistryService.deregister(type);
        log.info("Type [{}] successfully deregistered!", type);
        return ResponseEntity.noContent().build();
    }
    
    @PreAuthorize("hasRole(@roles.ADMIN) or hasRole(@roles.USER)")
    @RequestMapping(value = "/{type}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", notes = "Retrieve the registered type.", value = "/register/{type}")
    @ApiResponses({ @ApiResponse(code = 200, message = "Successfully retrieved a registered type."),
            @ApiResponse(code = 400, message = "Bad request."),
            @ApiResponse(code = 500, message = "Internal server error.") })
    public ResponseEntity<?> get(
            @ApiParam(value = "Type name", required = true)
            @PathVariable("type") String type) {
        JSONObject payload = classRegistryService.get(type);
        String message = LogUtil.toLog(payload, String.format("Successfully retrieved type [%s]\n", type));
        log.info(message);
        return ResponseEntity.ok(payload.toString());
    }
    
    @PreAuthorize("hasRole(@roles.ADMIN) or hasRole(@roles.USER)")
    @RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", notes = "All registered types.", value = "/register?showAll")
    @ApiResponses({ @ApiResponse(code = 200, message = "List all registered types."),
            @ApiResponse(code = 400, message = "Bad request."),
            @ApiResponse(code = 500, message = "Internal server error.") })
    public ResponseEntity<?> all(
            @ApiParam(value = "Show all registered types?", required = true)
            @RequestParam(value = "showAll", required = true) String showAll) {
        return ResponseEntity.ok(classRegistryService.all().toString());
    }
        
}
