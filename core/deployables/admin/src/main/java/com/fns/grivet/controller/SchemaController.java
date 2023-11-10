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

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fns.grivet.service.SchemaService;

import lombok.extern.slf4j.Slf4j;


/**
 * Provides end-points for linking and unlinking JSON Schema to pre-registered types
 * 
 * @author Chris Phillipson
 */
@Slf4j
@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class SchemaController {

    private final SchemaService schemaService;
    
    @Autowired
    public SchemaController(SchemaService schemaService) {
        this.schemaService = schemaService;
    }
    
    
    @PreAuthorize("hasAuthority('write:schema')")
    @PostMapping("/schema")
    public ResponseEntity<?> linkSchema(@RequestBody JSONObject payload) throws IOException {
        if (!schemaService.isJsonSchema(payload)) {
            return ResponseEntity.unprocessableEntity().build();
        }
        String id = schemaService.linkSchema(payload).getName();
        String message = 
                "JSON Schema for type [%s] linked!  Store requests for this type will be validated henceforth!".formatted(id);
        log.info(message);
        return ResponseEntity.ok(message);
    }
    
    @PreAuthorize("hasAuthority('delete:schema')")
    @DeleteMapping("/schema/{type}")
    public ResponseEntity<?> unlinkSchema(
            @PathVariable("type") String type) {
        schemaService.unlinkSchema(type);
        String message = "JSON Schema for type [%s] unlinked!  Store requests for this type will no longer be validated!".formatted(type);
        log.info(message);
        return ResponseEntity.ok(message);
    }
            
}
