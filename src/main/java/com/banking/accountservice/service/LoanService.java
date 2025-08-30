package com.banking.accountservice.service;

import com.banking.accountservice.model.Account;
import com.banking.accountservice.model.Loan;
import com.banking.accountservice.repository.AccountRepository;
import com.banking.accountservice.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RestTemplate restTemplate; // <-- injected RestTemplate

    private final String USER_SERVICE_URL = "http://localhost:8081/users"; // UserService base URL

    // Apply for a new loan
    public Loan applyLoan(Long accountId, String loanType, Double amount, Double interestRate, Integer period) throws Exception {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new Exception("Account not found"));

        // Validate user exists in UserService
        validateUser(account.getUserId());

        Loan loan = new Loan();
        loan.setAccount(account);
        loan.setLoanType(loanType);
        loan.setAmount(amount);
        loan.setInterestRate(interestRate);
        loan.setPeriod(period);

        // Calculate total amount to pay
        double totalPay = amount + (amount * interestRate * period / 100);
        loan.setTotalAmountToPay(totalPay);

        return loanRepository.save(loan);
    }


    // Get all loans for a specific account
    public List<Loan> getLoansByAccount(Long accountId) {
        return loanRepository.findByAccountId(accountId);
    }

    // Get loans by type (HOME, CAR, PERSONAL)
    public List<Loan> getLoansByType(String loanType) {
        return loanRepository.findByLoanType(loanType);
    }

    // Helper method to validate user in UserService
    private void validateUser(Long userId) throws Exception {
        String url = USER_SERVICE_URL + "/" + userId;
        Object userResponse = restTemplate.getForObject(url, Object.class);
        if (userResponse == null) {
            throw new Exception("User not found in UserService with id: " + userId);
        }
    }
}
