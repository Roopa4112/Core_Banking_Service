package com.banking.accountservice.controller;

import com.banking.accountservice.model.Transaction;
import com.banking.accountservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // Deposit endpoint
    @PostMapping("/deposit/{accountId}")
    public ResponseEntity<?> deposit(@PathVariable Long accountId, @RequestParam double amount) {
        try {
            Transaction transaction = transactionService.deposit(accountId, amount);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Withdraw endpoint
    @PostMapping("/withdraw/{accountId}")
    public ResponseEntity<?> withdraw(@PathVariable Long accountId, @RequestParam double amount) {
        try {
            Transaction transaction = transactionService.withdraw(accountId, amount);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Transfer endpoint
    // Transfer money between accounts using path variables
//    @PostMapping("/transfer/{fromAccountId}/{toAccountId}")
//    public ResponseEntity<?> transfer(
//            @PathVariable Long fromAccountId,
//            @PathVariable Long toAccountId,
//            @RequestParam Double amount) {
//        try {
//            Transaction transaction = transactionService.transfer(fromAccountId, toAccountId, amount);
//            return ResponseEntity.ok(transaction);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }

    @PostMapping("/transfer/{fromAccountId}/{toAccountId}")
    public ResponseEntity<?> transfer(
            @PathVariable Long fromAccountId,
            @PathVariable Long toAccountId,
            @RequestParam Double amount) {
        try {
            List<Transaction> transactions = transactionService.transfer(fromAccountId, toAccountId, amount);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }



    // Get all transactions of an account
    @GetMapping("/{accountId}")
    public ResponseEntity<?> getTransactions(@PathVariable Long accountId) {
        try {
            List<Transaction> transactions = transactionService.getTransactionsByAccount(accountId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Get all transactions
    @GetMapping
    public ResponseEntity<?> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }
}
