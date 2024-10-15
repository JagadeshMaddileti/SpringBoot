package com.springboot.bank_service.service;

import com.springboot.bank_service.dto.AccountDTO;
import com.springboot.bank_service.model.Bank;
import com.springboot.bank_service.repository.BankRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class BankServiceImpl implements BankService{

    private BankRepository bankRepository;
    private RestTemplate restTemplate;
    private String accountServiceUrl;
    public static final String BANK_NOT_FOUND_MESSAGE = "Bank not found";

    public BankServiceImpl(BankRepository bankRepository, RestTemplate restTemplate,
                           @Value("${account.service.url}") String accountServiceUrl) {
        this.bankRepository = bankRepository;
        this.restTemplate = restTemplate;
        this.accountServiceUrl = accountServiceUrl;
    }

    @Override
    public List<Bank> findAll() {
         return bankRepository.findAll();
    }

    @Override
    public Optional<Bank> findById(Long id) {
        return bankRepository.findById(id);
    }

    @Override
    public Bank save(Bank bank) {
        return bankRepository.save(bank);
    }

    @Override
    public boolean delete(Long id) {
        return bankRepository.findById(id)
                .map(bank -> {
                    restTemplate.delete(accountServiceUrl+"/accounts/bank/"+id);
                    bankRepository.delete(bank);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public Optional<Bank> update(Long id, Bank bankDetails) {
        return bankRepository.findById(id)
                .map(bank -> {
                    bank.setName(bankDetails.getName());
                    bank.setLocation(bankDetails.getLocation());
                    bank.setBranchCode(bankDetails.getBranchCode());
                    return bankRepository.save(bank);
                });
    }

    @Override
    public List<AccountDTO> getAccountsForBank(Long bankId) {
        bankRepository.findById(bankId)
                .orElseThrow(()-> new RuntimeException(BANK_NOT_FOUND_MESSAGE));
        ResponseEntity<List<AccountDTO>> response=restTemplate.exchange(
                accountServiceUrl + "/accounts/bank/" + bankId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<AccountDTO>>() {
                }
        );
        return response.getBody();
    }

    @Override
    public AccountDTO createAccountForBank(Long bankId, AccountDTO accountDTO) {
        Bank bank=bankRepository.findById(bankId)
                .orElseThrow(()-> new RuntimeException(BANK_NOT_FOUND_MESSAGE));
        accountDTO.setBankId(bankId);
        ResponseEntity<AccountDTO> response=restTemplate.postForEntity(
                accountServiceUrl + "/accounts",
                accountDTO,
                AccountDTO.class
        );
        return response.getBody();
    }

    @Override
    public AccountDTO updateAccountForBank(Long accountId, Long bankId, AccountDTO accountDTO) {
        bankRepository.findById(bankId)
                .orElseThrow(()-> new RuntimeException(BANK_NOT_FOUND_MESSAGE));
        accountDTO.setBankId(bankId);
        ResponseEntity<AccountDTO> response=restTemplate.exchange(
                accountServiceUrl + "/accounts/"+accountId,
                HttpMethod.PUT,
                new HttpEntity<>(accountDTO),
                AccountDTO.class
        );
        return response.getBody();
    }

    @Override
    public void deleteAccountforBank(Long accountId, Long bankId) {
        bankRepository.findById(bankId)
                .orElseThrow(()-> new RuntimeException(BANK_NOT_FOUND_MESSAGE));
        restTemplate.delete(accountServiceUrl+"/accounts/"+accountId);
    }
}
