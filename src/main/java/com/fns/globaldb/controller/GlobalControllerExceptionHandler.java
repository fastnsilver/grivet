package com.fns.globaldb.controller;

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

import com.fns.globaldb.service.SchemaValidationException;

@ControllerAdvice
class GlobalControllerExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    @ExceptionHandler({ IOException.class, IllegalArgumentException.class, DataAccessException.class, DateTimeParseException.class, NumberFormatException.class, JSONException.class, SQLException.class, SchemaValidationException.class })
    protected ResponseEntity<?> badRequest(Exception e, HttpServletRequest hsr) {
        String queryString = hsr.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            log.error(String.format("Request Method: %s, Request URI: %s, Query string: %s, Error: %s", hsr.getMethod(), hsr.getRequestURI(), hsr.getQueryString(), e.getMessage()));
        } else {
            log.error(String.format("Request Method: %s, Request URI: %s, Error: %s", hsr.getMethod(), hsr.getRequestURI(), e.getMessage()));
        }
        return ResponseEntity.badRequest().body(String.format("Error: %s", e.getMessage()));
    }
}
