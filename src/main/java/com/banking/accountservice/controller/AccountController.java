package com.banking.accountservice.controller;

import com.banking.accountservice.model.Account;
import com.banking.accountservice.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    // 1. Create account
    @PostMapping
    public Account createAccount(@RequestBody Account account) {
        return accountService.createAccount(account);
    }

    // 2. Get account by ID
    @GetMapping("/{id}")
    public Account getAccount(@PathVariable Long id) {
        return accountService.getAccount(id);
    }

    // 3. Get all accounts
    @GetMapping
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    // 4. Get accounts by userId
    @GetMapping("/user/{userId}")
    public List<Account> getAccountsByUser(@PathVariable Long userId) {
        return accountService.getAccountsByUser(userId);
    }

    // 5. Update account
    @PutMapping("/{id}")
    public Account updateAccount(@PathVariable Long id, @RequestBody Account account) {
        return accountService.updateAccount(id, account);
    }

    // 6. Delete account
    @DeleteMapping("/{id}")
    public String deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return "Account with ID " + id + " deleted successfully.";
    }

}
