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

import com.fns.grivet.service.SchemaService;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * Provides end-points for linking and unlinking JSON Schema to pre-registered types
 * 
 * @author Chris Phillipson
 */
@RestController
@RequestMapping("/schema")
@Api(value = "schema", produces = "application/json")
public class SchemaController {

    private final Logger log = LoggerFactory.getLogger(getClass());
        
    private final SchemaService schemaService;
    
    @Autowired
    public SchemaController(SchemaService schemaService) {
        this.schemaService = schemaService;
    }
    
    
    @PreAuthorize("hasRole(@roles.ADMIN)")
    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", notes = "Link a JSON Schema to a pre-registered type.", value = "/schema")
    @ApiResponses({ @ApiResponse(code = 200, message = "Successfully link JSON Schema to registered type."),
            @ApiResponse(code = 400, message = "Bad request."),
            @ApiResponse(code = 422, message = "Unprocessable entity (e.g., invalid JSON schema)."),
            @ApiResponse(code = 500, message = "Internal server error.") })
    public ResponseEntity<?> linkSchema(@RequestBody JSONObject json) throws IOException {
        if (!schemaService.isJsonSchema(json)) {
            return ResponseEntity.unprocessableEntity().build();
        }
        String id = schemaService.linkSchema(json).getName();
        String message = String.format(
                "JSON Schema for type [%s] linked!  Store requests for this type will be validated henceforth!", id);
        log.info(message);
        return ResponseEntity.ok(message);
    }
    
    @PreAuthorize("hasRole(@roles.ADMIN)")
    @RequestMapping(value = "/{type}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", notes = "Unlink JSON Schema from a pre-registered type.", value = "/schema/{type}")
    @ApiResponses({ @ApiResponse(code = 200, message = "Unlinked JSON Schema from type."),
            @ApiResponse(code = 400, message = "Bad request."),
            @ApiResponse(code = 500, message = "Internal server error.") })
    public ResponseEntity<?> unlinkSchema(
            @ApiParam(value = "Type name", required = true)
            @PathVariable("type") String type) {
        schemaService.unlinkSchema(type);
        String message = String.format("JSON Schema for type [%s] unlinked!  Store requests for this type will no longer be validated!", type);
        log.info(message);
        return ResponseEntity.ok(message);
    }
            
}
