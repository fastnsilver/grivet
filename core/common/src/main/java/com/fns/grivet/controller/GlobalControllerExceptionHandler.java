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
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fns.grivet.service.SchemaValidationException;

@ControllerAdvice
class GlobalControllerExceptionHandler {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@ExceptionHandler({
		IOException.class, IllegalArgumentException.class, DataAccessException.class,
		DateTimeParseException.class, NumberFormatException.class, JSONException.class,
			SQLException.class
	})
	protected ResponseEntity<ErrorResponse> badRequest(Exception e, HttpServletRequest hsr) {
		ErrorResponse er = new ErrorResponse(hsr.getMethod(), hsr.getRequestURI(), hsr.getQueryString(),
				Arrays.asList(e.getMessage()));
		log.error(er.asLog());
		return ResponseEntity.badRequest().body(er);
	}

	@ExceptionHandler({ SchemaValidationException.class })
	protected ResponseEntity<ErrorResponse> invalid(SchemaValidationException e, HttpServletRequest hsr) {
		ErrorResponse er = new ErrorResponse(hsr.getMethod(), hsr.getRequestURI(), hsr.getQueryString(),
				e.getProcessingFailures());
		log.error(er.asLog());
		return ResponseEntity.badRequest().body(er);
	}

	@JsonPropertyOrder(value = { "errors", "method", "uri", "query" })
	private class ErrorResponse {
		private String method;
		private String uri;
		private String query;
		private List<Object> errors;

		@JsonCreator
		public ErrorResponse(@JsonProperty String method, @JsonProperty String uri, @JsonProperty String query,
				@JsonProperty List<Object> errors) {
			this.method = method;
			this.uri = uri;
			this.query = query;
			this.errors = errors;
		}

		public String getMethod() {
			return method;
		}

		public String getUri() {
			return uri;
		}

		public String getQuery() {
			return query;
		}

		public List<Object> getErrors() {
			return errors;
		}

		@JsonIgnore
		public String asLog() {
			return ToStringBuilder.reflectionToString(this);
		}

	}
}
