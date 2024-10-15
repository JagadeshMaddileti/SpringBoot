package com.springboot.bank_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.springboot.bank_service.dto.AccountDTO;
import com.springboot.bank_service.model.Bank;
import com.springboot.bank_service.repository.BankRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class BankServiceImplTest {

    @Mock
    private BankRepository bankRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BankServiceImpl bankService;

    private String accountServiceUrl = "http://localhost:8080/accounts";

    @BeforeEach
    void setUp() {
        // Setting the accountServiceUrl directly for testing
        bankService = new BankServiceImpl(bankRepository, restTemplate, accountServiceUrl);
    }

    @Test
    void testFindAll() {
        when(bankRepository.findAll()).thenReturn(Collections.emptyList());

        assertTrue(bankService.findAll().isEmpty());
        verify(bankRepository, times(1)).findAll();
    }

    @Test
    void testFindById_ExistingBank() {
        Bank bank = new Bank();
        bank.setId(1L);
        when(bankRepository.findById(1L)).thenReturn(Optional.of(bank));

        Optional<Bank> result = bankService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(bank, result.get());
        verify(bankRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_NonExistingBank() {
        when(bankRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Bank> result = bankService.findById(1L);
        assertFalse(result.isPresent());
        verify(bankRepository, times(1)).findById(1L);
    }

    @Test
    void testSave() {
        Bank bank = new Bank();
        bank.setName("Test Bank");
        when(bankRepository.save(any(Bank.class))).thenReturn(bank);

        Bank result = bankService.save(bank);
        assertEquals(bank, result);
        verify(bankRepository, times(1)).save(bank);
    }

    @Test
    void testDelete_ExistingBank() {
        Bank bank = new Bank();
        bank.setId(1L);
        when(bankRepository.findById(1L)).thenReturn(Optional.of(bank));

        bankService.delete(1L);
        verify(restTemplate, times(1)).delete(accountServiceUrl + "/accounts/bank/" + 1L);
        verify(bankRepository, times(1)).delete(bank);
    }

    @Test
    void testDelete_NonExistingBank() {
        when(bankRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertFalse(bankService.delete(1L));
        verify(bankRepository, times(1)).findById(1L);
        verify(bankRepository, never()).delete(any(Bank.class));
    }

    @Test
    void testUpdate_ExistingBank() {
        Bank existingBank = new Bank();
        existingBank.setId(1L);
        existingBank.setName("Old Name");
        Bank updatedBank = new Bank();
        updatedBank.setName("New Name");
        when(bankRepository.findById(1L)).thenReturn(Optional.of(existingBank));
        when(bankRepository.save(any(Bank.class))).thenReturn(existingBank);

        Optional<Bank> result = bankService.update(1L, updatedBank);
        assertTrue(result.isPresent());
        assertEquals("New Name", result.get().getName());
        verify(bankRepository, times(1)).findById(1L);
        verify(bankRepository, times(1)).save(existingBank);
    }

    @Test
    void testUpdate_NonExistingBank() {
        Bank updatedBank = new Bank();
        updatedBank.setName("New Name");
        when(bankRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Bank> result = bankService.update(1L, updatedBank);
        assertFalse(result.isPresent());
        verify(bankRepository, times(1)).findById(1L);
        verify(bankRepository, never()).save(any(Bank.class));
    }

    @Test
    void testGetAccountsForBank_ExistingBank() {
        // Arrange
        Bank bank = new Bank();
        bank.setId(1L);
        when(bankRepository.findById(1L)).thenReturn(Optional.of(bank));

        List<AccountDTO> accountDTOList = Collections.singletonList(new AccountDTO());
        ResponseEntity<List<AccountDTO>> responseEntity = ResponseEntity.ok(accountDTOList);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class))
        ).thenReturn(responseEntity);
        List<AccountDTO> accounts = bankService.getAccountsForBank(1L);

        assertNotNull(accounts);
        assertEquals(1, accounts.size());
        verify(bankRepository).findById(1L);
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class));
    }


    @Test
    void testGetAccountsForBank_NonExistingBank() {
        when(bankRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bankService.getAccountsForBank(1L);
        });
        assertEquals("Bank not found", exception.getMessage());
        verify(bankRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateAccountForBank_ExistingBank() {
        Bank bank = new Bank();
        bank.setId(1L);
        when(bankRepository.findById(1L)).thenReturn(Optional.of(bank));

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setBankId(1L);
        ResponseEntity<AccountDTO> responseEntity = ResponseEntity.ok(accountDTO);
        when(restTemplate.postForEntity(anyString(), any(), eq(AccountDTO.class))).thenReturn(responseEntity);

        AccountDTO result = bankService.createAccountForBank(1L, accountDTO);
        assertNotNull(result);
        assertEquals(accountDTO, result);
        verify(bankRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateAccountForBank_NonExistingBank() {
        when(bankRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bankService.createAccountForBank(1L, new AccountDTO())
        );
        assertEquals("Bank not found", exception.getMessage());
        verify(bankRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateAccountForBank_ExistingBankAndAccount() {
        Bank bank = new Bank();
        bank.setId(1L);
        when(bankRepository.findById(1L)).thenReturn(Optional.of(bank));

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setBankId(1L);
        ResponseEntity<AccountDTO> responseEntity = ResponseEntity.ok(accountDTO);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(), eq(AccountDTO.class))).thenReturn(responseEntity);

        AccountDTO result = bankService.updateAccountForBank(1L, 1L, accountDTO);
        assertNotNull(result);
        assertEquals(accountDTO, result);
        verify(bankRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateAccountForBank_NonExistingBank() {
        when(bankRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bankService.updateAccountForBank(1L, 1L, new AccountDTO())
        );
        assertEquals("Bank not found", exception.getMessage());
        verify(bankRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteAccountForBank_ExistingBank() {
        Bank bank = new Bank();
        bank.setId(1L);
        when(bankRepository.findById(1L)).thenReturn(Optional.of(bank));

        bankService.deleteAccountforBank(1L, 1L);
        verify(restTemplate, times(1)).delete(accountServiceUrl + "/accounts/" + 1L);
        verify(bankRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteAccountForBank_NonExistingBank() {
        when(bankRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bankService.deleteAccountforBank(1L, 1L);
        });
        assertEquals("Bank not found", exception.getMessage());
        verify(bankRepository, times(1)).findById(1L);
    }
}
