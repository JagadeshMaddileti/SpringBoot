package com.springboot.account_service.service;

import com.springboot.account_service.model.Account;
import com.springboot.account_service.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private AccountRepository accountRepository;

    private Account account;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        account = new Account();
        account.setId(1L);
        account.setAccountNumber(893456789L);
        account.setAccountType("Checking");
        account.setBalance(1000);
        account.setBankId(1L);
    }

    @Test
    void testFindAll() {
        when(accountRepository.findAll()).thenReturn(List.of(account));

        List<Account> accounts = accountService.findAll();

        assertEquals(1, accounts.size());
        assertEquals(account, accounts.get(0));
        verify(accountRepository, times(1)).findAll();
    }

    @Test
    void testFindById_ExistingAccount() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        Optional<Account> foundAccount = accountService.findById(1L);

        assertTrue(foundAccount.isPresent());
        assertEquals(account, foundAccount.get());
        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_NonExistingAccount() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Account> foundAccount = accountService.findById(1L);

        assertFalse(foundAccount.isPresent());
        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByBankId_ExistingBank() {
        when(accountRepository.findByBankId(1L)).thenReturn(List.of(account));

        List<Account> accounts = accountService.findByBankId(1L);

        assertEquals(1, accounts.size());
        assertEquals(account, accounts.get(0));
        verify(accountRepository, times(1)).findByBankId(1L);
    }

    @Test
    void testFindByBankId_NonExistingBank() {
        when(accountRepository.findByBankId(1L)).thenReturn(Collections.emptyList());

        List<Account> accounts = accountService.findByBankId(1L);

        assertTrue(accounts.isEmpty());
        verify(accountRepository, times(1)).findByBankId(1L);
    }

    @Test
    void testSave() {
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account savedAccount = accountService.save(account);

        assertEquals(account, savedAccount);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void testUpdate_ExistingAccount() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account updatedAccount = new Account();
        updatedAccount.setAccountNumber(987654321L);
        updatedAccount.setAccountType("Savings");
        updatedAccount.setBalance(2000);
        updatedAccount.setBankId(1L);

        Optional<Account> result = accountService.update(1L, updatedAccount);

        assertTrue(result.isPresent());
        assertEquals(account, result.get());
        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void testUpdate_NonExistingAccount() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Account> result = accountService.update(1L, account);

        assertFalse(result.isPresent());
        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    void testDelete_ExistingAccount() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        boolean isDeleted = accountService.delete(1L);

        assertTrue(isDeleted);
        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).delete(account);
    }

    @Test
    void testDelete_NonExistingAccount() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        boolean isDeleted = accountService.delete(1L);

        assertFalse(isDeleted);
        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteByBankId() {
        doNothing().when(accountRepository).deleteByBankId(1L);

        accountService.deleteByBankId(1L);

        verify(accountRepository, times(1)).deleteByBankId(1L);
    }
}
