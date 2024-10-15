package com.springboot.bank_service.controller;

import com.springboot.bank_service.dto.AccountDTO;
import com.springboot.bank_service.exception.AccountNotFoundException;
import com.springboot.bank_service.exception.BankNotFoundException;
import com.springboot.bank_service.model.Bank;
import com.springboot.bank_service.service.BankService;
import com.springboot.bank_service.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping(Constants.BANK_BASE_URL)
public class BankController {

    @Autowired
    private BankService bankService;

    @GetMapping
    public ResponseEntity<List<EntityModel<Bank>>> getAllBank(){
        List<EntityModel<Bank>> bankList=bankService.findAll().stream()
                .map(bank ->EntityModel.of(bank,
                        linkTo(methodOn(BankController.class).getBankById(bank.getId())).withSelfRel(),
                        linkTo(methodOn(BankController.class).getAccountsForBank(bank.getId())).withRel("accounts"),
                        linkTo(methodOn(BankController.class).getAllBank()).withRel("allBank")))
                .collect(Collectors.toList());
        return ResponseEntity.ok(bankList);
    }

    @GetMapping(Constants.BANK_ID)
    public ResponseEntity<EntityModel<Bank>> getBankById(@PathVariable Long id) {
        Bank bank = bankService.findById(id)
                .orElseThrow(() -> new BankNotFoundException(id));
        EntityModel<Bank> bankModel = EntityModel.of(bank,
                linkTo(methodOn(BankController.class).getBankById(id)).withSelfRel(),
                linkTo(methodOn(BankController.class).getAccountsForBank(bank.getId())).withRel("accounts"),
                linkTo(methodOn(BankController.class).getAllBank()).withRel("allBank"));
        return ResponseEntity.ok(bankModel);
    }

    @PostMapping
    public ResponseEntity<EntityModel<Bank>> createBank(@RequestBody Bank bank){
        Bank createBank=bankService.save(bank);

        EntityModel<Bank> bankModel=EntityModel.of(createBank,
                linkTo(methodOn(BankController.class).getBankById(createBank.getId())).withSelfRel(),
                linkTo(methodOn(BankController.class).getAllBank()).withRel("allBank"));
        return ResponseEntity.status(HttpStatus.CREATED).body(bankModel);
    }

    @PutMapping(Constants.BANK_ID)
    public ResponseEntity<EntityModel<Bank>> updateBank(@PathVariable Long id, @RequestBody Bank bankDetails) {
        Bank updatedBank = bankService.update(id, bankDetails)
                .orElseThrow(() -> new BankNotFoundException(id));
        EntityModel<Bank> bankModel = EntityModel.of(updatedBank,
                linkTo(methodOn(BankController.class).getBankById(id)).withSelfRel(),
                linkTo(methodOn(BankController.class).getAllBank()).withRel("allBank"));
        return ResponseEntity.ok(bankModel);
    }

    @DeleteMapping(Constants.BANK_ID)
    public ResponseEntity<String> deleteBank(@PathVariable Long id) {
        boolean isDeleted = bankService.delete(id);
        if (isDeleted) {
            return ResponseEntity.ok("Bank and its accounts are deleted successfully");
        } else {
            throw new BankNotFoundException(id);
        }
    }

    @GetMapping(Constants.BANK_ID_ACCOUNTS)
    public ResponseEntity<List<EntityModel<AccountDTO>>> getAccountsForBank(@PathVariable Long bankId){
        List<EntityModel<AccountDTO>> accounts=bankService.getAccountsForBank(bankId).stream()
                .map(account->EntityModel.of(account,
                        linkTo(methodOn(BankController.class).getAccountsForBank(bankId)).withSelfRel(),
                        linkTo(methodOn(BankController.class).getBankById(bankId)).withRel("bank")))
                .collect(Collectors.toList());
        return ResponseEntity.ok(accounts);
    }

    @PostMapping(Constants.BANK_ID_ACCOUNTS)
    public ResponseEntity<EntityModel<AccountDTO>> createAccountForBank(@PathVariable Long bankId,@RequestBody AccountDTO accountDTO){
        AccountDTO createdAccount=bankService.createAccountForBank(bankId,accountDTO);
        EntityModel<AccountDTO> accountModel=EntityModel.of(createdAccount,
                linkTo(methodOn(BankController.class).getAccountsForBank(bankId)).withRel("accounts"),
                linkTo(methodOn(BankController.class).getBankById(bankId)).withRel("bank"));
        return ResponseEntity.status(HttpStatus.CREATED).body(accountModel);
    }

    @PutMapping(Constants.BANK_ID_ACCOUNTS_ACCOUNT_ID)
    public ResponseEntity<EntityModel<AccountDTO>> updateAccountforBank(@PathVariable Long bankId,@PathVariable Long accountId,@RequestBody AccountDTO accountDTO){
        AccountDTO updatedAccount=bankService.updateAccountForBank(bankId,accountId,accountDTO);
        EntityModel<AccountDTO> accountModel=EntityModel.of(updatedAccount,
                linkTo(methodOn(BankController.class).getAccountsForBank(bankId)).withRel("accounts"),
                linkTo(methodOn(BankController.class).getBankById(bankId)).withRel("bank"));

        return ResponseEntity.ok(accountModel);
    }

    @DeleteMapping(Constants.BANK_ID_ACCOUNTS_ACCOUNT_ID)
    public ResponseEntity<String> deleteAccountForBank(@PathVariable Long bankId, @PathVariable Long accountId) {
        try {
            bankService.deleteAccountforBank(bankId, accountId);
            return ResponseEntity.ok("Account deleted successfully for bank_id: " + bankId);
        } catch (RuntimeException e) {
            throw new AccountNotFoundException(accountId);
        }
    }

}
