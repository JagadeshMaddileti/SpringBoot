package com.springboot.account_service.util;

public class Constants {
    private Constants(){
        throw new UnsupportedOperationException("This is a util class and cannot be instantiated");
    }
    public static final String ACCOUNT_BASE_URL="/accounts";
    public static final String ACCOUNT_ID="/{id}";
    public static final String BANK_BANK_ID="/bank/{bankId}";
}
