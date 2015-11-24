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
import java.sql.SQLException;
import java.time.format.DateTimeParseException;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fns.grivet.service.SchemaValidationException;

@ControllerAdvice
class GlobalControllerExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    @ExceptionHandler({ 
        IOException.class, IllegalArgumentException.class, DataAccessException.class, 
        DateTimeParseException.class, NumberFormatException.class, JSONException.class, 
        SQLException.class, SchemaValidationException.class 
    })
    protected ResponseEntity<?> badRequest(Exception e, HttpServletRequest hsr) {
        String queryString = hsr.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            log.error(String.format("Request\n-- Method: %s\n-- URI: %s\n-- Query string: %s\n-- Error: %s", 
                    hsr.getMethod(), hsr.getRequestURI(), hsr.getQueryString(), e.getMessage()));
        } else {
            log.error(String.format("Request\n-- Method: %s\n-- Request URI: %s\n-- Error: %s", 
                    hsr.getMethod(), hsr.getRequestURI(), e.getMessage()));
        }
        return ResponseEntity.badRequest().body(String.format("Error: %s", e.getMessage()));
    }
}
