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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import com.fns.grivet.service.ClassRegistryService;

/**
 * Provides end-points for type definition and verification
 *
 * @author Chris Phillipson
 */
@RefreshScope
@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class ClassRegistryController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ClassRegistryController.class);

    @Value("${grivet.register.batch-size:100}")
    private int batchSize;
    private final ClassRegistryService classRegistryService;

    @Autowired
    public ClassRegistryController(ClassRegistryService classRegistryService) {
        this.classRegistryService = classRegistryService;
    }

    @PreAuthorize("hasAuthority(\'write:typedef\')")
    @PostMapping("/definition")
    public ResponseEntity<?> defineType(@RequestBody JSONObject payload) throws IOException {
        String type = classRegistryService.register(payload);
        UriComponentsBuilder ucb = UriComponentsBuilder.newInstance();
        log.info("Type [{}] successfully registered!", type);
        return ResponseEntity.created(ucb.path("/definition/{type}").buildAndExpand(type).toUri()).build();
    }

    @PreAuthorize("hasAuthority(\'write:typedef\')")
    @PostMapping("/definitions")
    public ResponseEntity<?> defineTypes(@RequestBody JSONArray array) throws IOException, JSONException {
        int numberOfTypesToRegister = array.length();
        Assert.isTrue(numberOfTypesToRegister <= batchSize, "The total number of entries in a type registration request must not exceed %d! Your registration request contained [%d] entries.".formatted(batchSize, numberOfTypesToRegister));
        JSONObject payload = null;
        String type = null;
        HttpHeaders headers = new HttpHeaders();
        URI location = null;
        int errorCount = 0;
        // allow for all JSONObjects within JSONArray to be processed; capture and report errors during processing
        for (int i = 0; i < numberOfTypesToRegister; i++) {
            try {
                payload = array.getJSONObject(i);
                type = classRegistryService.register(payload);
                location = UriComponentsBuilder.newInstance().path("/definition/{type}").buildAndExpand(type).toUri();
                if (numberOfTypesToRegister == 1) {
                    headers.setLocation(location);
                } else {
                    headers.set("Location[%s]".formatted(String.valueOf(i + 1)), location.toASCIIString());
                }
                log.info("Type [{}] successfully registered!", type);
            } catch (Exception e) {
                String message = LogUtil.toLog(payload, "Problems registering type! Portion of payload @ index[%d]\n".formatted(i + 1));
                log.error(message, e);
                if (numberOfTypesToRegister == 1) {
                    throw e;
                }
                headers.set("Error[%s]".formatted(String.valueOf(i + 1)), e.getMessage());
                errorCount++;
            }
        }
        return new ResponseEntity<>(headers, ((errorCount == 0) ? HttpStatus.CREATED : HttpStatus.ACCEPTED));
    }

    @PreAuthorize("hasAuthority(\'delete:typedef\')")
    @DeleteMapping("/definition/{type}")
    public ResponseEntity<?> undefineType(@PathVariable("type") String type) {
        classRegistryService.deregister(type);
        log.info("Type [{}] successfully deregistered!", type);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority(\'read:typedef\')")
    @GetMapping("/definition/{type}")
    public ResponseEntity<?> getTypeDefinition(@PathVariable("type") String type) {
        JSONObject payload = classRegistryService.get(type);
        String message = LogUtil.toLog(payload, "Successfully retrieved type [%s]\n".formatted(type));
        log.info(message);
        return ResponseEntity.ok(payload.toString());
    }

    @PreAuthorize("hasAuthority(\'read:typedef\')")
    @GetMapping("/definitions")
    public ResponseEntity<?> getAllTypeDefinitions() {
        return ResponseEntity.ok(classRegistryService.all().toString());
    }
}
