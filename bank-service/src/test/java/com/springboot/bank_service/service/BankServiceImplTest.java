package com.springboot.bank_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.springboot.bank_service.dto.AccountDTO;
import com.springboot.bank_service.model.Bank;
import com.springboot.bank_service.repository.BankRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;

import java.util.ArrayList;


public class BankServiceImplTest {

    @Mock
    private BankRepository bankRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BankServiceImpl bankServiceImpl;

    @Value("${account.service.url}")
    private String accountServiceUrl;

    private Bank bank;
    private AccountDTO accountDTO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        bank = new Bank();
        bank.setId(1L);
        bank.setName("Test Bank");
        bank.setLocation("Test Location");
        bank.setBranchCode("TST001");

        accountDTO = new AccountDTO();
        accountDTO.setId(1L);
        accountDTO.setAccountType("Test Account");
        accountDTO.setBankId(1L);
    }

    @Test
    public void testFindAll() {
        List<Bank> banks = new ArrayList<>();
        banks.add(bank);

        when(bankRepository.findAll()).thenReturn(banks);

        List<Bank> result = bankServiceImpl.findAll();

        assertEquals(1, result.size());
        verify(bankRepository).findAll();
    }

    @Test
    public void testFindById() {
        when(bankRepository.findById(1L)).thenReturn(Optional.of(bank));

        Optional<Bank> result = bankServiceImpl.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(bank, result.get());
        verify(bankRepository).findById(1L);
    }

    @Test
    public void testSave() {
        when(bankRepository.save(bank)).thenReturn(bank);

        Bank result = bankServiceImpl.save(bank);

        assertEquals(bank, result);
        verify(bankRepository).save(bank);
    }

    @Test
    public void testDeleteSuccess() {
        when(bankRepository.findById(1L)).thenReturn(Optional.of(bank));

        bankServiceImpl.delete(1L);

        verify(restTemplate).delete(accountServiceUrl + "/accounts/bank/1");
        verify(bankRepository).delete(bank);
    }

    @Test
    public void testDeleteNotFound() {
        when(bankRepository.findById(1L)).thenReturn(Optional.empty());

        boolean result = bankServiceImpl.delete(1L);

        assertFalse(result);
        verify(bankRepository).findById(1L);
        verify(bankRepository, never()).delete(any());
    }

    @Test
    public void testUpdateSuccess() {
        when(bankRepository.findById(1L)).thenReturn(Optional.of(bank));
        Bank updatedBank = new Bank();
        updatedBank.setName("Updated Bank");
        updatedBank.setLocation("Updated Location");
        updatedBank.setBranchCode("TST002");

        when(bankRepository.save(any())).thenReturn(updatedBank);

        Optional<Bank> result = bankServiceImpl.update(1L, updatedBank);

        assertTrue(result.isPresent());
        assertEquals(updatedBank.getName(), result.get().getName());
        verify(bankRepository).findById(1L);
        verify(bankRepository).save(any());
    }

    @Test
    public void testUpdateNotFound() {
        when(bankRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Bank> result = bankServiceImpl.update(1L, bank);

        assertFalse(result.isPresent());
        verify(bankRepository).findById(1L);
        verify(bankRepository, never()).save(any());
    }

    @Test
    public void testGetAccountsForBankSuccess() {
        when(bankRepository.findById(1L)).thenReturn(Optional.of(bank));
        List<AccountDTO> accountList = new ArrayList<>();
        accountList.add(accountDTO);

        ResponseEntity<List<AccountDTO>> responseEntity = mock(ResponseEntity.class);
        when(responseEntity.getBody()).thenReturn(accountList);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(new ParameterizedTypeReference<List<AccountDTO>>() {})))
                .thenReturn(responseEntity);

        List<AccountDTO> result = bankServiceImpl.getAccountsForBank(1L);

        assertEquals(1, result.size());
        verify(bankRepository).findById(1L);
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(new ParameterizedTypeReference<List<AccountDTO>>() {}));
    }

    @Test
    public void testGetAccountsForBankNotFound() {
        when(bankRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            bankServiceImpl.getAccountsForBank(1L);
        });

        assertEquals("Bank not found", exception.getMessage());
        verify(bankRepository).findById(1L);
    }

    @Test
    public void testCreateAccountForBankSuccess() {
        when(bankRepository.findById(1L)).thenReturn(Optional.of(bank));
        ResponseEntity<AccountDTO> responseEntity = mock(ResponseEntity.class);
        when(responseEntity.getBody()).thenReturn(accountDTO);
        when(restTemplate.postForEntity(anyString(), any(), eq(AccountDTO.class)))
                .thenReturn(responseEntity);

        AccountDTO result = bankServiceImpl.createAccountForBank(1L, accountDTO);

        assertEquals(accountDTO, result);
        verify(bankRepository).findById(1L);
        verify(restTemplate).postForEntity(anyString(), any(), eq(AccountDTO.class));
    }

    @Test
    public void testCreateAccountForBankNotFound() {
        when(bankRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            bankServiceImpl.createAccountForBank(1L, accountDTO);
        });

        assertEquals("Bank not found", exception.getMessage());
        verify(bankRepository).findById(1L);
    }

    @Test
    public void testUpdateAccountForBankSuccess() {
        when(bankRepository.findById(1L)).thenReturn(Optional.of(bank));
        ResponseEntity<AccountDTO> responseEntity = mock(ResponseEntity.class);
        when(responseEntity.getBody()).thenReturn(accountDTO);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(AccountDTO.class)))
                .thenReturn(responseEntity);

        AccountDTO result = bankServiceImpl.updateAccountForBank(1L, 1L, accountDTO);

        assertEquals(accountDTO, result);
        verify(bankRepository).findById(1L);
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(AccountDTO.class));
    }

    @Test
    public void testUpdateAccountForBankNotFound() {
        when(bankRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            bankServiceImpl.updateAccountForBank(1L, 1L, accountDTO);
        });

        assertEquals("Bank not found", exception.getMessage());
        verify(bankRepository).findById(1L);
    }

    @Test
    public void testDeleteAccountForBankSuccess() {
        when(bankRepository.findById(1L)).thenReturn(Optional.of(bank));

        bankServiceImpl.deleteAccountforBank(1L, 1L);

        verify(bankRepository).findById(1L);
        verify(restTemplate).delete(accountServiceUrl + "/accounts/1");
    }

    @Test
    public void testDeleteAccountForBankNotFound() {
        when(bankRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            bankServiceImpl.deleteAccountforBank(1L, 1L);
        });

        assertEquals("Bank not found", exception.getMessage());
        verify(bankRepository).findById(1L);
        verify(restTemplate, never()).delete(anyString());
    }
}
