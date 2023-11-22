package com.fns.grivet.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fns.grivet.model.ErrorResponse;
import com.fns.grivet.service.SchemaValidationException;

@ControllerAdvice
public class SchemaValidationExceptionHandler {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory
		.getLogger(SchemaValidationExceptionHandler.class);

	@ExceptionHandler({ SchemaValidationException.class })
	protected ResponseEntity<ErrorResponse> invalid(SchemaValidationException e, HttpServletRequest hsr) {
		var er = new ErrorResponse(hsr.getMethod(), hsr.getRequestURI(), hsr.getQueryString(),
				e.getProcessingFailures());
		log.error(er.toString());
		return ResponseEntity.unprocessableEntity().body(er);
	}

}
