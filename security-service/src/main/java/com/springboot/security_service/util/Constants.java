package com.springboot.security_service.util;

public class Constants {
    private Constants() {
        throw new UnsupportedOperationException("This is a util class and cannot be instantiated");
    }
    public static final String AUTH_BASE_URL = "/auth";
    public static final String REGISTER = "/register";
    public static final String TOKEN = "/token";
    public static final String VALIDATE = "/validate";
}
