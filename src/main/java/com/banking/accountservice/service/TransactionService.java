package com.banking.accountservice.service;

import com.banking.accountservice.kafka.TransactionEvent;
import com.banking.accountservice.kafka.TransactionEventProducer;
import com.banking.accountservice.model.Account;
import com.banking.accountservice.model.Transaction;
import com.banking.accountservice.repository.AccountRepository;
import com.banking.accountservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.transaction.annotation.Transactional;


import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionEventProducer transactionEventProducer;

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
        transaction.setDate(new Date());
        transaction.setStatus("APPROVED");           // ✅ auto-approve
        transaction.setApproved(true);
        transaction.setApprovedByEmployeeId(7L);     // system / auto approval

        transaction = transactionRepository.save(transaction);

        // Send Kafka event
        sendTransactionEvent(transaction, "DEPOSIT", "Deposit successful");
        sendTransactionEvent(transaction, "DEPOSIT", "Deposit successful");

        return transaction;
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
        transaction.setDate(new Date());
        transaction.setStatus("APPROVED");
        transaction.setApproved(true);
        transaction.setApprovedByEmployeeId(7L);

        transaction = transactionRepository.save(transaction);

        // Send Kafka event
        sendTransactionEvent(transaction, "WITHDRAW", "Withdraw successful");
        sendTransactionEvent(transaction, "WITHDRAW", "Withdraw successful");

        return transaction;
    }

    // Transfer money between accounts
//    public Transaction transfer(Long fromAccountId, Long toAccountId, Double amount) {
//        if (fromAccountId.equals(toAccountId)) {
//            throw new RuntimeException("Cannot transfer to the same account!");
//        }
//
//        Account fromAccount = accountRepository.findById(fromAccountId)
//                .orElseThrow(() -> new RuntimeException("Source account not found"));
//
//        Account toAccount = accountRepository.findById(toAccountId)
//                .orElseThrow(() -> new RuntimeException("Destination account not found"));
//
//        // Validate users
//        validateUser(fromAccount.getUserId());
//        validateUser(toAccount.getUserId());
//
//        if (fromAccount.getBalance() < amount) {
//            throw new RuntimeException("Insufficient balance in source account!");
//        }
//
//        // Deduct from source and add to destination
//        fromAccount.setBalance(fromAccount.getBalance() - amount);
//        accountRepository.save(fromAccount);
//
//        toAccount.setBalance(toAccount.getBalance() + amount);
//        accountRepository.save(toAccount);
//
//        // Record transaction (source account)
//        Transaction transaction = new Transaction();
//        transaction.setAccount(fromAccount);
//        transaction.setTransactionType("TRANSFER");
//        transaction.setAmount(amount);
//        transaction.setDate(new Date());
//        transaction.setStatus("APPROVED");
//        transaction.setApproved(true);
//        transaction.setApprovedByEmployeeId(7L);
//
//        transaction = transactionRepository.save(transaction);
//
//        // Send Kafka events for both sides
//        sendTransactionEvent(transaction, "TRANSFER_OUT", "Money sent to account " + toAccount.getId());
//
//        Transaction toTransaction = new Transaction();
//        toTransaction.setAccount(toAccount);
//        toTransaction.setTransactionType("TRANSFER");
//        toTransaction.setAmount(amount);
//        toTransaction.setDate(new Date());
//        toTransaction.setStatus("APPROVED");
//        toTransaction.setApproved(true);
//        toTransaction.setApprovedByEmployeeId(7L);
//
//        toTransaction = transactionRepository.save(toTransaction);
//
//        sendTransactionEvent(toTransaction, "TRANSFER_IN", "Money received from account " + fromAccount.getId());
//
//        return transaction;
//    }

    @Transactional
    public List<Transaction> transfer(Long fromAccountId, Long toAccountId, Double amount) {
        if (fromAccountId.equals(toAccountId)) {
            throw new RuntimeException("Cannot transfer to the same account!");
        }

        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new RuntimeException("Source account not found"));

        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new RuntimeException("Destination account not found"));

        // Validate users (your existing method)
        validateUser(fromAccount.getUserId());
        validateUser(toAccount.getUserId());

        if (fromAccount.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance in source account!");
        }

        // Update balances
        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // Create Debit Transaction
        Transaction debitTx = new Transaction();
        debitTx.setAccount(fromAccount);
        debitTx.setTransactionType("TRANSFER_OUT");
        debitTx.setAmount(amount);
        debitTx.setDate(new Date());
        debitTx.setStatus("APPROVED");
        debitTx.setApproved(true);
        debitTx.setApprovedByEmployeeId(7L); // hardcoded example
        debitTx.setCounterpartyAccountId(toAccount.getId());

        transactionRepository.save(debitTx);

        // Create Credit Transaction
        Transaction creditTx = new Transaction();
        creditTx.setAccount(toAccount);
        creditTx.setTransactionType("TRANSFER_IN");
        creditTx.setAmount(amount);
        creditTx.setDate(new Date());
        creditTx.setStatus("APPROVED");
        creditTx.setApproved(true);
        creditTx.setApprovedByEmployeeId(7L);
        creditTx.setCounterpartyAccountId(fromAccount.getId());

        transactionRepository.save(creditTx);

        // Send Kafka events (helper method)
        sendTransactionEvent(debitTx, "TRANSFER_OUT", "Transfer to account " + toAccountId);
        sendTransactionEvent(creditTx, "TRANSFER_IN", "Transfer from account " + fromAccountId);

        sendTransactionEvent(debitTx, "TRANSFER_OUT", "Transfer to account " + toAccountId);
        sendTransactionEvent(creditTx, "TRANSFER_IN", "Transfer from account " + fromAccountId);

        // ✅ Return both transactions
        return List.of(debitTx, creditTx);
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

    // Helper: get user email
    private String getUserEmail(Long userId) {
        String url = USER_SERVICE_URL + "/" + userId;
        Map<String, Object> user = restTemplate.getForObject(url, Map.class);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        return (String) user.get("email");
    }

    // Helper: send Kafka event
    private void sendTransactionEvent(Transaction transaction, String type, String description) {
        Account account = transaction.getAccount();

        TransactionEvent event = new TransactionEvent(
                transaction.getId(),                // Transaction ID
                account.getId(),                    // Account ID
                account.getUserId(),                // User ID
                getUserEmail(account.getUserId()),  // User Email
                type,                               // Type (DEPOSIT/WITHDRAW/TRANSFER)
                transaction.getAmount(),            // Amount
                description                         // Description
        );

        transactionEventProducer.sendTransactionEvent(event);
    }
}
