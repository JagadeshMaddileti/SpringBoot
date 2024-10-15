package com.springboot.account_service.exception;

public class AccountNotFoundException extends RuntimeException{
    public AccountNotFoundException(Long id) {
        super("Account not found with ID: " + id);
    }
}
