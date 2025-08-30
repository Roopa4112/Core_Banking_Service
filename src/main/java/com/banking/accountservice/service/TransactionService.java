package com.banking.accountservice.service;

import com.banking.accountservice.model.Account;
import com.banking.accountservice.model.Transaction;
import com.banking.accountservice.repository.AccountRepository;
import com.banking.accountservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RestTemplate restTemplate; // injected RestTemplate

    private final String USER_SERVICE_URL = "http://localhost:8081/users"; // UserService base URL

    // Deposit money into an account
    public Transaction deposit(Long accountId, Double amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));

        validateUser(account.getUserId());

        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setTransactionType("DEPOSIT");
        transaction.setAmount(amount);
        transaction.setDate(new Date()); // ✅ set current date

        return transactionRepository.save(transaction);
    }

    // Withdraw money from an account
    public Transaction withdraw(Long accountId, Double amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));

        validateUser(account.getUserId());

        if (account.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance!");
        }

        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setTransactionType("WITHDRAW");
        transaction.setAmount(amount);
        transaction.setDate(new Date()); // ✅ set current date

        return transactionRepository.save(transaction);
    }

    // Transfer money between accounts
    public Transaction transfer(Long fromAccountId, Long toAccountId, Double amount) {
        if (fromAccountId.equals(toAccountId)) {
            throw new RuntimeException("Cannot transfer to the same account!");
        }

        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new RuntimeException("Source account not found"));

        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new RuntimeException("Destination account not found"));

        // Validate users
        validateUser(fromAccount.getUserId());
        validateUser(toAccount.getUserId());

        if (fromAccount.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance in source account!");
        }

        // Deduct from source and add to destination
        fromAccount.setBalance(fromAccount.getBalance() - amount);
        accountRepository.save(fromAccount);

        toAccount.setBalance(toAccount.getBalance() + amount);
        accountRepository.save(toAccount);

        // Record transaction (source account)
        Transaction transaction = new Transaction();
        transaction.setAccount(fromAccount);
        transaction.setTransactionType("TRANSFER");
        transaction.setAmount(amount);
        transaction.setDate(new Date());

        return transactionRepository.save(transaction);
    }

    // Get all transactions of a specific account
    public List<Transaction> getTransactionsByAccount(Long accountId) {
        accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with id " + accountId));
        return transactionRepository.findByAccountId(accountId);
    }

    // Get all transactions
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // Validate user in UserService
    private void validateUser(Long userId) {
        String url = USER_SERVICE_URL + "/" + userId;
        Object userResponse = restTemplate.getForObject(url, Object.class);
        if (userResponse == null) {
            throw new RuntimeException("User not found in UserService with id: " + userId);
        }
    }
}
