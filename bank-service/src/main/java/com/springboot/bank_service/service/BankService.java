package com.springboot.bank_service.service;

import com.springboot.bank_service.dto.AccountDTO;
import com.springboot.bank_service.model.Bank;

import java.util.List;
import java.util.Optional;

public interface BankService {
    public List<Bank> findAll();
    public Optional<Bank> findById(Long id);
    public Bank save(Bank bank);
    public boolean delete(Long id);
    public Optional<Bank> update(Long id, Bank bankDetails);
    public List<AccountDTO> getAccountsForBank(Long bankId);
    public AccountDTO createAccountForBank(Long bankId,AccountDTO accountDTO);
    public AccountDTO updateAccountForBank(Long accountId,Long bankId,AccountDTO accountDTO);
    public void deleteAccountforBank(Long accountId, Long bankId);
}
