package com.springboot.bank_service.util;

public class Constants {
    private Constants(){
        throw new UnsupportedOperationException("This is a util class and cannot be instantiated");
    }
    public static final String BANK_BASE_URL="/bank";
    public static final String BANK_ID="/{id}";
    public static final String BANK_ID_ACCOUNTS="/{bankId}/accounts";
    public static final String BANK_ID_ACCOUNTS_ACCOUNT_ID="/{bankId}/accounts/{accountId}";
}
