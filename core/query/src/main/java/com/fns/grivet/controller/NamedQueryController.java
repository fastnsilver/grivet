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

import com.fns.grivet.query.NamedQuery;
import com.fns.grivet.query.QueryType;
import com.fns.grivet.service.NamedQueryService;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * Provides end-points for registration, verification and execution of named queries
 * 
 * @author Chris Phillipson
 */
@RestController
@RequestMapping("/query")
@Api(value = "query", produces = "application/json")
public class NamedQueryController {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private final NamedQueryService namedQueryService;
    
    @Autowired
    public NamedQueryController(NamedQueryService namedQueryService) {
        this.namedQueryService = namedQueryService;
    }
    
    @PreAuthorize("hasRole(@roles.ADMIN)")
    @RequestMapping(method = RequestMethod.POST, 
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", notes = "Register a Named Query.", value = "/query")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully registered Named Query requiring no parameters."),
            @ApiResponse(code = 204, message = "Successfully registered Named Query that requires parameters."),
            @ApiResponse(code = 400, message = "Bad request."),
            @ApiResponse(code = 500, message = "Internal server error.") })
    public ResponseEntity<?> create(
            @ApiParam(value = "Named Query payload", required = true)
            @RequestBody NamedQuery query) {
        ResponseEntity<?> result = ResponseEntity.unprocessableEntity().build();
        Assert.isTrue(StringUtils.isNotBlank(query.getName()), "Query name must not be null, empty or blank.");
        Assert.notNull(query.getQuery(), "Query string must not be null!");
        Assert.isTrue(isSupportedQuery(query), "Query must start with either CALL or SELECT");
        // check for named parameters; but only if they exist
        if (!query.getParams().isEmpty()) {
            query.getParams().keySet()
                .forEach(k -> Assert.isTrue(query.getQuery().contains(String.format(":%s", k)), String.format("Query must contain named parameter [%s]", k)));
        }
        namedQueryService.create(query);
        log.info("Named Query \n\n{}\n\n successfully registered!", query);
        UriComponentsBuilder ucb = UriComponentsBuilder.newInstance();
        if (query.getParams().isEmpty()) {
            result = ResponseEntity.created(ucb.path("/query/{name}").buildAndExpand(query.getName()).toUri()).build();
        } else {
            result = ResponseEntity.noContent().build();
        }
        return result;
    }
    
    @PreAuthorize("hasRole(@roles.ADMIN) or hasRole(@roles.USER)")
    @RequestMapping(value = "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", notes = "Execute a Named Query.", value = "/query/{name}")
    @ApiResponses({ @ApiResponse(code = 200, message = "Successfully executed Named Query request."),
            @ApiResponse(code = 400, message = "Bad request."),
            @ApiResponse(code = 500, message = "Internal server error.") })
    public ResponseEntity<?> get(
            @ApiParam(value = "The name of the query to execute", required = true)
            @PathVariable("name") String name, 
            @ApiParam(value = "Named Query parameters (key-value pairs)", required = false)
            @RequestParam MultiValueMap<String, ?> parameters) {
        return ResponseEntity.ok(namedQueryService.get(name, parameters));
    }
    
    @PreAuthorize("hasRole(@roles.ADMIN) or hasRole(@roles.USER)")
    @RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", notes = "Available Named Queries.", value = "/query?showAll")
    @ApiResponses({ @ApiResponse(code = 200, message = "List available Named Queries."),
            @ApiResponse(code = 400, message = "Bad request."),
            @ApiResponse(code = 500, message = "Internal server error.") })
    public ResponseEntity<?> all(
            @ApiParam(value = "Show all registered queries?", required = true)
            @RequestParam(value = "showAll", required = true) String showAll) {
        JSONArray payload = namedQueryService.all();
        return ResponseEntity.ok(payload.toString());
    }
    
    @PreAuthorize("hasRole(@roles.ADMIN)")
    @RequestMapping(value = "/{name}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", notes = "Delete the named query.", value = "/query/{name}")
    @ApiResponses({ @ApiResponse(code = 204, message = "Successfully deleted a Named Query."),
            @ApiResponse(code = 400, message = "Bad request."),
            @ApiResponse(code = 500, message = "Internal server error.") })
    public ResponseEntity<?> delete(
            @ApiParam(value = "The name of the query to delete", required = true)
            @PathVariable("name") String name) {
        namedQueryService.delete(name);
        log.info("Query with name [{}] successfully deleted!", name);
        return ResponseEntity.noContent().build();
    }
    
    private boolean isSupportedQuery(NamedQuery query) {
        boolean result = false;
        if ((query.getQuery().toUpperCase().startsWith("SELECT") && query.getType().equals(QueryType.SELECT)) 
                || (query.getQuery().toUpperCase().startsWith("CALL") && query.getType().equals(QueryType.SPROC))) {
            result = true;
        }
        return result;
    }
}
