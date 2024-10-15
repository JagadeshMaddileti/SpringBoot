package com.springboot.account_service.exception;

public class BankNotFoundException extends RuntimeException {
    public BankNotFoundException(Long bankId) {
        super("Bank not found with ID: " + bankId);
    }
}

