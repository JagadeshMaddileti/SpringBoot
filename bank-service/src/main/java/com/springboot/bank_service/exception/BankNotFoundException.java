package com.springboot.bank_service.exception;

public class BankNotFoundException extends RuntimeException {
    public BankNotFoundException(Long id) {
        super("Bank with id " + id + " not found");
    }
}