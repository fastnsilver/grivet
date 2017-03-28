package com.fns.grivet.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fns.grivet.model.ErrorResponse;
import com.fns.grivet.service.SchemaValidationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class SchemaValidationExceptionHandler {

    @ExceptionHandler({ SchemaValidationException.class })
    protected ResponseEntity<ErrorResponse> invalid(SchemaValidationException e, HttpServletRequest hsr) {
        ErrorResponse er = new ErrorResponse(hsr.getMethod(), hsr.getRequestURI(), hsr.getQueryString(),
                e.getProcessingFailures());
        log.error(er.toString());
        return ResponseEntity.unprocessableEntity().body(er);
    }
}
