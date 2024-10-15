package com.springboot.account_service.service;

import com.springboot.account_service.model.Account;
import com.springboot.account_service.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService{

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    public List<Account> findByBankId(Long bankId) {
        return accountRepository.findByBankId(bankId);
    }

    @Override
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public Optional<Account> update(Long id, Account account) {
        return accountRepository.findById(id)
                .map(account1 ->{
                    account1.setAccountNumber(account.getAccountNumber());
                    account1.setAccountType(account.getAccountType());
                    account1.setBalance(account.getBalance());
                    account1.setBankId(account.getBankId());
                    return accountRepository.save(account);
                } );
    }

    @Override
    public boolean delete(Long id) {
        return accountRepository.findById(id)
                .map(account -> {
                    accountRepository.delete(account);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public void deleteByBankId(Long accountId) {
          accountRepository.deleteByBankId(accountId);
    }
}
