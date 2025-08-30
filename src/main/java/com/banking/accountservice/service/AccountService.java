package com.banking.accountservice.service;

import com.banking.accountservice.model.Account;
import com.banking.accountservice.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.ThreadLocalRandom;


import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RestTemplate restTemplate; // <-- RestTemplate injected

    private final String USER_SERVICE_URL = "http://localhost:8081/users"; // UserService base URL

    // 1. Create new account with user validation
//    public Account createAccount(Account account) {
//        // 1a. Validate user exists in UserService
//        String url = USER_SERVICE_URL + "/" + account.getUserId(); // assuming Account has userId field
//        Object userResponse = restTemplate.getForObject(url, Object.class); // map to DTO if needed
//        if (userResponse == null) {
//            throw new RuntimeException("User not found in UserService with id " + account.getUserId());
//        }
//
//        // 1b. Set default balance if null
//        account.setBalance(account.getBalance() != null ? account.getBalance() : 0.0);
//
//        // 1c. Save account
//        return accountRepository.save(account);
//    }


    public Account createAccount(Account account) {
        // Validate user exists in UserService
        String url = USER_SERVICE_URL + "/" + account.getUserId();
        Object userResponse = restTemplate.getForObject(url, Object.class);
        if (userResponse == null) {
            throw new RuntimeException("User not found in UserService with id " + account.getUserId());
        }

        // Set default balance if null
        account.setBalance(account.getBalance() != null ? account.getBalance() : 0.0);

        // Generate unique account number if not already set
        if (account.getAccountNumber() == null) {
            String generatedAccountNumber = String.valueOf(ThreadLocalRandom.current().nextLong(1000000000L, 9999999999L));
            account.setAccountNumber(generatedAccountNumber);
        }

        return accountRepository.save(account);
    }

    // 2. Get account by ID
    public Account getAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with id " + accountId));
    }

    // 3. Get all accounts
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    // 4. Get accounts by userId
    public List<Account> getAccountsByUser(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    // 5. Update account details
    public Account updateAccount(Long accountId, Account updatedAccount) {
        Account existingAccount = getAccount(accountId);
        existingAccount.setAccountType(updatedAccount.getAccountType());
        return accountRepository.save(existingAccount);
    }

    // 6. Delete account
    public void deleteAccount(Long accountId) {
        accountRepository.deleteById(accountId);
    }


}
