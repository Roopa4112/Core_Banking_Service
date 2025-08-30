package com.banking.accountservice.repository;

import com.banking.accountservice.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Use the correct field name from Transaction entity
    List<Transaction> findByTransactionType(String transactionType);

    // Optional: get all transactions for a specific account
    List<Transaction> findByAccountId(Long accountId);
}
