package com.fns.grivet.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = { "errors", "method", "uri", "query" })
public class ErrorResponse {

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
	public String toString() {
		return String.format(
				"ErrorResponse: { \"method\": \"%s\", \"uri\": \"%s\", \"query\": \"%s\", \"errors\": \"%s\" }", method,
				uri, query, errors);
	}

}
