package com.fns.grivet.model;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;

@Getter
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

    @JsonIgnore
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
