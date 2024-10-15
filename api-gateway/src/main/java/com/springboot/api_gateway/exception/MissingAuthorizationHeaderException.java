package com.springboot.api_gateway.exception;

public class MissingAuthorizationHeaderException extends RuntimeException{
    public MissingAuthorizationHeaderException(String message) {
        super(message);
    }
}
