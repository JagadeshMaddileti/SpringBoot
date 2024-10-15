package com.springboot.account_service.service;

import com.springboot.account_service.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    public List<Account> findAll();
    public Optional<Account> findById(Long id);
    public List<Account> findByBankId(Long bankId);
    public Account save(Account account);
    public Optional<Account> update(Long id, Account account);
    public boolean delete(Long id);
    public void deleteByBankId(Long accountId);
}
