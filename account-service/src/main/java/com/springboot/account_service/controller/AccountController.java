package com.springboot.account_service.controller;

import com.springboot.account_service.exception.AccountDeletionException;
import com.springboot.account_service.exception.AccountNotFoundException;
import com.springboot.account_service.exception.BankNotFoundException;
import com.springboot.account_service.model.Account;
import com.springboot.account_service.service.AccountService;
import com.springboot.account_service.util.Constants;
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
@RequestMapping(Constants.ACCOUNT_BASE_URL)
public class AccountController {

    private final AccountService accountService;
    public static final String ACCOUNTS_BY_BANK_ID = "accountsByBankId";
    public static final String ALL_ACCOUNTS = "allAccounts";

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<List<EntityModel<Account>>> getAllAccounts(){
        List<EntityModel<Account>> accountList= accountService.findAll().stream()
                .map(account -> EntityModel.of(account,
                        linkTo(methodOn(AccountController.class).getAccountById(account.getId())).withSelfRel(),
                        linkTo(methodOn(AccountController.class).getAllAccounts()).withRel(ALL_ACCOUNTS),
                        linkTo(methodOn(AccountController.class).getAccountsByBankId(account.getBankId())).withRel(ACCOUNTS_BY_BANK_ID)))
                .toList();
        return ResponseEntity.ok(accountList);
    }

    @GetMapping(Constants.ACCOUNT_ID)
    public ResponseEntity<EntityModel<Account>> getAccountById(@PathVariable Long id) {
        Account account = accountService.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
        return ResponseEntity.ok(EntityModel.of(account,
                linkTo(methodOn(AccountController.class).getAccountById(id)).withSelfRel(),
                linkTo(methodOn(AccountController.class).getAllAccounts()).withRel(ALL_ACCOUNTS),
                linkTo(methodOn(AccountController.class).getAccountsByBankId(account.getBankId())).withRel(ACCOUNTS_BY_BANK_ID)));
    }
    @GetMapping(Constants.BANK_BANK_ID)
    public ResponseEntity<List<EntityModel<Account>>> getAccountsByBankId(@PathVariable Long bankId){
        List<EntityModel<Account>> accounts=accountService.findByBankId(bankId).stream()
                .map(account -> EntityModel.of(account,
                        linkTo(methodOn(AccountController.class).getAccountById(account.getId())).withSelfRel(),
                        linkTo(methodOn(AccountController.class).getAccountsByBankId(bankId)).withRel(ACCOUNTS_BY_BANK_ID)))
                .toList();
        if (accounts.isEmpty()) {
            throw new BankNotFoundException(bankId);
        }
        return ResponseEntity.ok(accounts);
    }

    @PostMapping
    public ResponseEntity<EntityModel<Account>> createAccount(@RequestBody Account account){
        Account savedAccount= accountService.save(account);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(EntityModel.of(savedAccount,
                        linkTo(methodOn(AccountController.class).getAccountById(savedAccount.getId())).withSelfRel(),
                        linkTo(methodOn(AccountController.class).getAllAccounts()).withRel(ALL_ACCOUNTS),
                        linkTo(methodOn(AccountController.class).getAccountsByBankId(savedAccount.getBankId())).withRel(ACCOUNTS_BY_BANK_ID)));
    }

    @PutMapping(Constants.ACCOUNT_ID)
    public ResponseEntity<EntityModel<Account>> updateAccount(@PathVariable Long id, @RequestBody Account accountDetails) {
        Account updatedAccount = accountService.update(id, accountDetails)
                .orElseThrow(() -> new AccountNotFoundException(id));
        return ResponseEntity.ok(EntityModel.of(updatedAccount,
                linkTo(methodOn(AccountController.class).getAccountById(updatedAccount.getId())).withSelfRel(),
                linkTo(methodOn(AccountController.class).getAllAccounts()).withRel(ALL_ACCOUNTS),
                linkTo(methodOn(AccountController.class).getAccountsByBankId(updatedAccount.getBankId())).withRel(ACCOUNTS_BY_BANK_ID)));
    }

    @DeleteMapping(Constants.ACCOUNT_ID)
    public ResponseEntity<String> deleteAccount(@PathVariable Long id) {
        boolean isDeleted = accountService.delete(id);
        if (!isDeleted) {
            throw new AccountDeletionException(id);
        }
        return ResponseEntity.ok("Account deleted successfully");
    }

    @DeleteMapping(Constants.BANK_BANK_ID)
    public ResponseEntity<String> deleteAccountsByBankId(@PathVariable Long bankId) {
        try {
            accountService.deleteByBankId(bankId);
            return ResponseEntity.ok("All accounts for the bank are deleted successfully");
        } catch (Exception e) {
            throw new AccountDeletionException(bankId, e.getMessage());
        }
    }

}
