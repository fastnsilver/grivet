package com.fns.grivet.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
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

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fns.grivet.service.EntityService;


@RestController
@RequestMapping("/store")
public class EntityController {

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
        return ResponseEntity.noContent().build();
    }
    
    private ResponseEntity<?> createMultipleTypes(String type, String json) {
        JSONArray jsonArray = new JSONArray(json);
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
            } catch (Exception e) {
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
    public ResponseEntity<?> get(@PathVariable("type") String type, @RequestParam(value="createdTimeStart", required=false) String createdTimeStart, @RequestParam(value="createdTimeEnd", required=false) String createdTimeEnd, HttpServletRequest request) throws JsonProcessingException {
        LocalDateTime start = createdTimeStart == null ? LocalDateTime.now().minusDays(7): LocalDateTime.parse(createdTimeStart);
        LocalDateTime end = createdTimeEnd == null ? LocalDateTime.now() : LocalDateTime.parse(createdTimeEnd);
        Assert.isTrue(ChronoUnit.SECONDS.between(start, end) >= 0, "Store request constraint createdTimeStart must be earlier or equal to createdTimeEnd!");
        return ResponseEntity.ok(entityService.get(type, start, end, request.getParameterMap().entrySet()));
    }
    
}
