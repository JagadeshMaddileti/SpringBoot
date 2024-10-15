package com.springboot.bank_service.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(Long accountId) {
        super("Account with id " + accountId + " not found");
    }
}