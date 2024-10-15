package com.springboot.account_service.exception;

public class AccountDeletionException extends RuntimeException {
    public AccountDeletionException(Long id) {
        super("Failed to delete account with ID: " + id);
    }

    public AccountDeletionException(Long bankId, String message) {
        super("Failed to delete accounts for bank with ID: " + bankId + ". " + message);
    }
}

