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
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.fns.grivet.service.ClassRegistryService;

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
    
    private final ClassRegistryService classRegistryService;
    
    @Autowired
    public ClassRegistryController(ClassRegistryService classRegistryService) {
        this.classRegistryService = classRegistryService;
    }
    
    @RequestMapping(method=RequestMethod.POST, 
            consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(HttpServletRequest request) throws IOException {
        String json = IOUtils.toString(request.getInputStream(), "UTF-8");
        Assert.isTrue(json.startsWith("{") || json.startsWith("["), "Registration requests must be valid JSON starting with either a { or [!");
        ResponseEntity<?> result = ResponseEntity.unprocessableEntity().build();
        if (json.startsWith("{")) {
            if (request.getParameterMap().containsKey("linkSchema") 
                    && classRegistryService.isJsonSchema(new JSONObject(json))) {
                result = linkSchema(json);
            } else {
                result = registerSingleType(json);
            }
        }
        if (json.startsWith("[")) {
            result = registerMultipleTypes(json);
        }
        return result;
    }
    
    private ResponseEntity<?> linkSchema(String json) {
        String id = classRegistryService.linkSchema(new JSONObject(json)).getName();
        String message = String.format("JSON Schema for type [%s] linked!  Store requests for this type will be validated henceforth!", id);
        log.info(message);
        return ResponseEntity.ok(message);
    }
    
    private ResponseEntity<?> unlinkSchema(String type) {
        classRegistryService.unlinkSchema(type);
        String message = String.format("JSON Schema for type [%s] unlinked!  Store requests for this type will no longer be validated!", type);
        log.info(message);
        return ResponseEntity.ok(message);
    }
    
    private ResponseEntity<?> registerSingleType(String json) {
        String type = classRegistryService.register(new JSONObject(json));
        UriComponentsBuilder ucb = UriComponentsBuilder.newInstance();
        log.info("Type [{}] successfully registered!", type);
        return ResponseEntity.created(ucb.path("/register/{type}").buildAndExpand(type).toUri()).build();
    }
    
    private ResponseEntity<?> registerMultipleTypes(String json) {
        JSONArray jsonArray = new JSONArray(json);
        JSONObject jsonObject = null;
        String type = null;
        HttpHeaders headers = new HttpHeaders();
        UriComponentsBuilder ucb = UriComponentsBuilder.newInstance();
        int numberOfTypesToRegister = jsonArray.length();
        URI location = null;
        int errorCount = 0;
        // allow for all JSONObjects within JSONArray to be processed; capture and report errors during processing
        for (int i = 0; i < numberOfTypesToRegister; i++) {
            jsonObject = jsonArray.getJSONObject(i);
            try {
                type = classRegistryService.register(jsonObject);
                location = ucb.path("/register/{type}").buildAndExpand(type).toUri();
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
        HttpStatus status = (errorCount == 0) ? HttpStatus.CREATED : HttpStatus.ACCEPTED;
        return new ResponseEntity<>(headers, status);
    }
    
    @RequestMapping(value="/{type}", method=RequestMethod.DELETE, produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", notes = "Delete the registered type.", value = "/register/{type}")
    public ResponseEntity<?> delete(@PathVariable("type") String type) {
        classRegistryService.deregister(type);
        log.info("Type [{}] successfully deregistered!", type);
        return ResponseEntity.noContent().build();
    }
    
    @RequestMapping(value="/{type}", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", notes = "Retrieve the registered type.", value = "/register/{type}")
    public ResponseEntity<?> get(@PathVariable("type") String type) {
        JSONObject payload = classRegistryService.get(type);
        String message = LogUtil.toLog(payload, String.format("Successfully retrieved type [%s]\n", type));
        log.info(message);
        return ResponseEntity.ok(payload.toString());
    }
    
    @RequestMapping(value="/{type}", method=RequestMethod.PUT, produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", notes = "Unlink JSON Schema from type.", value = "/register/{type}")
    public ResponseEntity<?> unlinkSchema(@PathVariable("type") String type, HttpServletRequest request) {
        Assert.isTrue(request.getParameterMap().containsKey("unlinkSchema"), "Operation not supported!"); 
        return unlinkSchema(type);
    }
    
    @RequestMapping(value="", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", notes = "All registered types.", value = "/register")
    @ApiResponses(value = { 
            @ApiResponse(code = 200, message = "List all registered types."),
            @ApiResponse(code = 400, message = "Bad request."),
            @ApiResponse(code = 500, message = "Internal server error.")
            })
    public ResponseEntity<?> all(
            @ApiParam(value = "Show all registered types?", required = true)
            @RequestParam(value = "showAll", required = true) String showAll) {
        JSONArray payload = classRegistryService.all();
        return ResponseEntity.ok(payload.toString());
    }
        
}
