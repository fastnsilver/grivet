package com.fns.globaldb.controller;

import java.io.IOException;
import java.net.URI;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.fns.globaldb.service.ClassRegistryService;


@RestController
@RequestMapping("/register")
public class ClassRegistryController {

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
        return ResponseEntity.ok(String.format("JSON Schema for type [%s] linked!  Store requests for this type will be validated henceforth!", id));
    }
    
    private ResponseEntity<?> unlinkSchema(String type) {
        classRegistryService.unlinkSchema(type);
        return ResponseEntity.ok(String.format("JSON Schema for type [%s] unlinked!  Store requests for this type will no longer be validated!", type));
    }
    
    private ResponseEntity<?> registerSingleType(String json) {
        String type = classRegistryService.register(new JSONObject(json));
        UriComponentsBuilder ucb = UriComponentsBuilder.newInstance();
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
            } catch (Exception e) {
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
    
    @RequestMapping(value="/{type}", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> get(@PathVariable("type") String type) {
        JSONObject payload = classRegistryService.get(type);
        return ResponseEntity.ok(payload.toString());
    }
    
    @RequestMapping(value="/{type}", method=RequestMethod.PUT, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> unlinkSchema(@PathVariable("type") String type, HttpServletRequest request) {
        Assert.isTrue(request.getParameterMap().containsKey("unlinkSchema"), "Operation not supported!"); 
        return unlinkSchema(type);
    }
    
    @RequestMapping(value="/", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> all() {
        JSONArray payload = classRegistryService.all();
        return ResponseEntity.ok(payload.toString());
    }
        
}
