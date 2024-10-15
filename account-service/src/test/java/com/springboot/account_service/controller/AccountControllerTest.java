package com.springboot.account_service.controller;

import com.springboot.account_service.exception.AccountDeletionException;
import com.springboot.account_service.exception.AccountNotFoundException;
import com.springboot.account_service.exception.BankNotFoundException;
import com.springboot.account_service.model.Account;
import com.springboot.account_service.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.hateoas.EntityModel;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class AccountControllerTest {

    @InjectMocks
    private AccountController accountController;

    @Mock
    private AccountService accountService;

    private Account account;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        account = new Account();
        account.setId(1L);
        account.setBankId(1L);
    }

    @Test
    void testGetAllAccounts() {
        when(accountService.findAll()).thenReturn(List.of(account));

        ResponseEntity<List<EntityModel<Account>>> response = accountController.getAllAccounts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(accountService, times(1)).findAll();
    }

    @Test
    void testGetAccountById_ExistingAccount() {
        when(accountService.findById(1L)).thenReturn(Optional.of(account));

        ResponseEntity<EntityModel<Account>> response = accountController.getAccountById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getContent().getId());
        verify(accountService, times(1)).findById(1L);
    }

    @Test
    void testGetAccountById_NonExistingAccount() {
        when(accountService.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountController.getAccountById(1L));
    }

    @Test
    void testGetAccountsByBankId_ExistingBank() {
        when(accountService.findByBankId(1L)).thenReturn(List.of(account));

        ResponseEntity<List<EntityModel<Account>>> response = accountController.getAccountsByBankId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(accountService, times(1)).findByBankId(1L);
    }

    @Test
    void testGetAccountsByBankId_NonExistingBank() {
        when(accountService.findByBankId(1L)).thenReturn(Collections.emptyList());

        assertThrows(BankNotFoundException.class, () -> accountController.getAccountsByBankId(1L));
    }

    @Test
    void testCreateAccount() {
        when(accountService.save(any(Account.class))).thenReturn(account);

        ResponseEntity<EntityModel<Account>> response = accountController.createAccount(account);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getContent().getId());
        verify(accountService, times(1)).save(any(Account.class));
    }

    @Test
    void testUpdateAccount_ExistingAccount() {
        when(accountService.update(anyLong(), any(Account.class))).thenReturn(Optional.of(account));

        ResponseEntity<EntityModel<Account>> response = accountController.updateAccount(1L, account);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getContent().getId());
        verify(accountService, times(1)).update(anyLong(), any(Account.class));
    }

    @Test
    void testUpdateAccount_NonExistingAccount() {
        when(accountService.update(anyLong(), any(Account.class))).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountController.updateAccount(1L, account));
    }

    @Test
    void testDeleteAccount_ExistingAccount() {
        when(accountService.delete(1L)).thenReturn(true);

        ResponseEntity<String> response = accountController.deleteAccount(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Account deleted successfully", response.getBody());
        verify(accountService, times(1)).delete(1L);
    }

    @Test
    void testDeleteAccount_NonExistingAccount() {
        when(accountService.delete(1L)).thenReturn(false);

        assertThrows(AccountDeletionException.class, () -> accountController.deleteAccount(1L));
    }

    @Test
    void testDeleteAccountsByBankId() {
        doNothing().when(accountService).deleteByBankId(1L);

        ResponseEntity<String> response = accountController.deleteAccountsByBankId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("All accounts for the bank are deleted successfully", response.getBody());
        verify(accountService, times(1)).deleteByBankId(1L);
    }

    @Test
    void testDeleteAccountsByBankId_Exception() {
        doThrow(new RuntimeException("Deletion failed")).when(accountService).deleteByBankId(1L);

        assertThrows(AccountDeletionException.class, () -> accountController.deleteAccountsByBankId(1L));
    }
}

