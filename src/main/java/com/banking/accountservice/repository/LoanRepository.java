package com.banking.accountservice.repository;

import com.banking.accountservice.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {


    // Find all loans linked to a specific account
    List<Loan> findByAccountId(Long accountId);

    // Find loans by type (e.g., HOME, PERSONAL, CAR)
    List<Loan> findByLoanType(String loanType);
}
