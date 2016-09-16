package com.fns.grivet.service;

import java.util.List;

public class SchemaValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private List<Object> processingFailures;

	public SchemaValidationException(String message, Throwable t) {
		super(message, t);
	}

	public SchemaValidationException(List<Object> processingFailures) {
		this.processingFailures = processingFailures;
	}

	public List<Object> getProcessingFailures() {
		return processingFailures;
	}

}
