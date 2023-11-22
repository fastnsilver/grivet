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
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.fns.grivet.model.ErrorResponse;

@ControllerAdvice
class GlobalControllerExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

	@ExceptionHandler({ IOException.class, IllegalArgumentException.class, DateTimeParseException.class,
			NumberFormatException.class, JSONException.class })
	protected ResponseEntity<ErrorResponse> badRequest(Exception e, HttpServletRequest hsr) {
		ErrorResponse er = new ErrorResponse(hsr.getMethod(), hsr.getRequestURI(), hsr.getQueryString(),
				Arrays.asList(e.getMessage()));
		log.error(er.toString());
		return ResponseEntity.badRequest().body(er);
	}

}
