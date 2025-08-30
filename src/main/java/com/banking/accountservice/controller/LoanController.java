package com.banking.accountservice.controller;

import com.banking.accountservice.model.Loan;
import com.banking.accountservice.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

@RestController
@RequestMapping("/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    // Apply for a new loan
    @PostMapping("/apply/{accountId}")
    public ResponseEntity<?> applyLoan(
            @PathVariable Long accountId,
            @RequestBody Loan loanRequest) {
        try {
            Loan loan = loanService.applyLoan(
                    accountId,
                    loanRequest.getLoanType(),
                    loanRequest.getAmount(),
                    loanRequest.getInterestRate(),
                    loanRequest.getPeriod() // make sure Loan has this field
            );
            return ResponseEntity.ok(loan);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    // Get all loans for a specific account
    @GetMapping("/account/{accountId}")
    public ResponseEntity<?> getLoansByAccount(@PathVariable Long accountId) {
        try {
            List<Loan> loans = loanService.getLoansByAccount(accountId);
            return ResponseEntity.ok(loans);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Get loans by type (HOME, CAR, PERSONAL)
    @GetMapping("/type/{loanType}")
    public ResponseEntity<?> getLoansByType(@PathVariable String loanType) {
        try {
            List<Loan> loans = loanService.getLoansByType(loanType);
            return ResponseEntity.ok(loans);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
