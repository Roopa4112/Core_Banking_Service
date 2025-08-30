package com.banking.accountservice.repository;


import com.banking.accountservice.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    // Find account by userId (fetch all accounts owned by a specific user)
    List<Account> findByUserId(Long userId);

    // Find account by account number
    Optional<Account> findByAccountNumber(String accountNumber);
}
