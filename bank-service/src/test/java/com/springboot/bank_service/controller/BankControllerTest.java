package com.springboot.bank_service.controller;

import com.springboot.bank_service.dto.AccountDTO;
import com.springboot.bank_service.exception.AccountNotFoundException;
import com.springboot.bank_service.exception.BankNotFoundException;
import com.springboot.bank_service.model.Bank;
import com.springboot.bank_service.service.BankService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


class BankControllerTest {

    @Mock
    private BankService bankService;

    @InjectMocks
    private BankController bankController;

    private Bank bank;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bank = new Bank(1L, "Test Bank", "Test Location", "1234");
    }

    @Test
    void getAllBank_returnsListOfBanks() {
        when(bankService.findAll()).thenReturn(List.of(bank));

        ResponseEntity<List<EntityModel<Bank>>> response = bankController.getAllBank();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get(0).getContent()).isEqualTo(bank);
        verify(bankService, times(1)).findAll();
    }



    @Test
    void getBankById_returnsBankIfExists() {
        when(bankService.findById(1L)).thenReturn(Optional.of(bank));

        ResponseEntity<EntityModel<Bank>> response = bankController.getBankById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getContent()).isEqualTo(bank);
        verify(bankService, times(1)).findById(1L);
    }

    @Test
    void getBankById_throwsBankNotFoundExceptionIfNotFound() {
        when(bankService.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BankNotFoundException.class, () -> bankController.getBankById(1L));
        verify(bankService, times(1)).findById(1L);
    }

    @Test
    void createBank_createsNewBank() {
        when(bankService.save(bank)).thenReturn(bank);

        ResponseEntity<EntityModel<Bank>> response = bankController.createBank(bank);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getContent()).isEqualTo(bank);
        verify(bankService, times(1)).save(bank);
    }

    @Test
    void updateBank_updatesBankIfExists() {
        when(bankService.update(1L, bank)).thenReturn(Optional.of(bank));

        ResponseEntity<EntityModel<Bank>> response = bankController.updateBank(1L, bank);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getContent()).isEqualTo(bank);
        verify(bankService, times(1)).update(1L, bank);
    }

    @Test
    void updateBank_throwsBankNotFoundExceptionIfNotFound() {
        when(bankService.update(1L, bank)).thenReturn(Optional.empty());

        assertThrows(BankNotFoundException.class, () -> bankController.updateBank(1L, bank));
        verify(bankService, times(1)).update(1L, bank);
    }

    @Test
    void deleteBank_deletesBankIfExists() {
        when(bankService.delete(1L)).thenReturn(true);

        ResponseEntity<String> response = bankController.deleteBank(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Bank and its accounts are deleted successfully");
        verify(bankService, times(1)).delete(1L);
    }

    @Test
    void deleteBank_throwsBankNotFoundExceptionIfNotFound() {
        when(bankService.delete(1L)).thenReturn(false);

        assertThrows(BankNotFoundException.class, () -> bankController.deleteBank(1L));
        verify(bankService, times(1)).delete(1L);
    }

    @Test
    void getAccountsForBank_returnsListOfAccounts() {
        AccountDTO accountDTO = new AccountDTO();
        when(bankService.getAccountsForBank(1L)).thenReturn(List.of(accountDTO));

        ResponseEntity<List<EntityModel<AccountDTO>>> response = bankController.getAccountsForBank(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get(0).getContent()).isEqualTo(accountDTO);
        verify(bankService, times(1)).getAccountsForBank(1L);
    }


    @Test
    void createAccountForBank_createsAccountForBank() {
        AccountDTO accountDTO = new AccountDTO();
        when(bankService.createAccountForBank(1L, accountDTO)).thenReturn(accountDTO);

        ResponseEntity<EntityModel<AccountDTO>> response = bankController.createAccountForBank(1L, accountDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getContent()).isEqualTo(accountDTO);
        verify(bankService, times(1)).createAccountForBank(1L, accountDTO);
    }

    @Test
    void updateAccountForBank_updatesAccountForBank() {
        AccountDTO accountDTO = new AccountDTO();
        when(bankService.updateAccountForBank(1L, 1L, accountDTO)).thenReturn(accountDTO);

        ResponseEntity<EntityModel<AccountDTO>> response = bankController.updateAccountforBank(1L, 1L, accountDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getContent()).isEqualTo(accountDTO);
        verify(bankService, times(1)).updateAccountForBank(1L, 1L, accountDTO);
    }

    @Test
    void deleteAccountForBank_deletesAccountForBank() {
        doNothing().when(bankService).deleteAccountforBank(1L, 1L);

        ResponseEntity<String> response = bankController.deleteAccountForBank(1L, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Account deleted successfully for bank_id: 1");
        verify(bankService, times(1)).deleteAccountforBank(1L, 1L);
    }

    @Test
    void deleteAccountForBank_throwsAccountNotFoundExceptionIfNotFound() {
        doThrow(new AccountNotFoundException(1L)).when(bankService).deleteAccountforBank(1L, 1L);

        assertThrows(AccountNotFoundException.class, () -> bankController.deleteAccountForBank(1L, 1L));
        verify(bankService, times(1)).deleteAccountforBank(1L, 1L);
    }
}


