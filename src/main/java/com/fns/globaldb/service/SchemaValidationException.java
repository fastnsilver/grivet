package com.fns.globaldb.service;

public class SchemaValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public SchemaValidationException(String message, Throwable t) {
        super(message, t);
    }

}
