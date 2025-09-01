package com.banking.accountservice.service;

import com.banking.accountservice.kafka.LoanAppliedEvent;
import com.banking.accountservice.kafka.LoanApprovedEvent;
import com.banking.accountservice.kafka.LoanEventProducer;
import com.banking.accountservice.model.Account;
import com.banking.accountservice.model.Loan;
import com.banking.accountservice.repository.AccountRepository;
import com.banking.accountservice.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private LoanEventProducer loanEventProducer;

    @Autowired
    private RestTemplate restTemplate; // <-- injected RestTemplate

    private final String USER_SERVICE_URL = "http://localhost:8081/users"; // UserService base URL

    // Apply for a new loan
//    public Loan applyLoan(Long accountId, String loanType, Double amount, Double interestRate, Integer period) throws Exception {
//        Account account = accountRepository.findById(accountId)
//                .orElseThrow(() -> new Exception("Account not found"));
//
//        // Validate user exists in UserService
//        validateUser(account.getUserId());
//
//        Loan loan = new Loan();
//        loan.setAccount(account);
//        loan.setLoanType(loanType);
//        loan.setAmount(amount);
//        loan.setInterestRate(interestRate);
//        loan.setPeriod(period);
//
//        // Calculate total amount to pay
//        double totalPay = amount + (amount * interestRate * period / 100);
//        loan.setTotalAmountToPay(totalPay);
//
//        return loanRepository.save(loan);
//    }

    // Apply for a new loan
    public Loan applyLoan(Long accountId, String loanType, Double amount, Double interestRate, Integer period) throws Exception {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new Exception("Account not found"));

        validateUser(account.getUserId());

        Loan loan = new Loan();
        loan.setAccount(account);
        loan.setLoanType(loanType);
        loan.setAmount(amount);
        loan.setInterestRate(interestRate);
        loan.setPeriod(period);

        double totalPay = amount + (amount * interestRate * period / 100);
        loan.setTotalAmountToPay(totalPay);

        loan.setApproved(false);
        loan.setStatus("PENDING");
        loan.setAppliedDate(LocalDateTime.now());

        loan = loanRepository.save(loan);



// set applied date
        loan.setAppliedDate(LocalDateTime.now());

        // Send Kafka event to employee
        String userEmail = getUserEmail(account.getUserId());
        LoanAppliedEvent event = new LoanAppliedEvent(
                loan.getId(),
                account.getUserId(),
                userEmail,
                loan.getAmount()
        );
        loanEventProducer.sendLoanAppliedEvent(event);
        return loan;
    }

    // Helper method to fetch user email from UserService
    private String getUserEmail(Long userId) {
        Map<String, Object> user = restTemplate.getForObject(USER_SERVICE_URL + "/" + userId, Map.class);
        return user != null ? (String) user.get("email") : null;
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

    public Loan approveLoan(Long loanId, Long employeeId) {
        String url = USER_SERVICE_URL + "/" + employeeId;
        @SuppressWarnings("unchecked")
        Map<String, Object> employee = restTemplate.getForObject(url, Map.class);
        if (employee == null) {
            throw new RuntimeException("Employee not found with id: " + employeeId);
        }

        String role = (String) employee.get("role");
        if (!"EMPLOYEE".equalsIgnoreCase(role)) {
            throw new RuntimeException("User is not authorized to approve. Role must be EMPLOYEE.");
        }

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found with id: " + loanId));

        loan.setApproved(true);
        loan.setApprovedByEmployeeId(employeeId);
        loan.setApprovedDate(LocalDateTime.now());
        loan.setStatus("APPROVED");

        loan = loanRepository.save(loan);

        // Send Kafka event to user
        String userEmail = getUserEmail(loan.getAccount().getUserId());
        LoanApprovedEvent approvedEvent = new LoanApprovedEvent(
                loan.getId(),
                loan.getAccount().getUserId(),
                userEmail,
                loan.getAmount(),
                employeeId
        );
        loanEventProducer.sendLoanApprovedEvent(approvedEvent);

        return loan;
    }

//    public Loan approveLoan(Long loanId, Long employeeId) {
//        // 1. Fetch employee details
//        String url = USER_SERVICE_URL + "/" + employeeId;
//        @SuppressWarnings("unchecked")
//        Map<String, Object> employee = restTemplate.getForObject(url, Map.class);
//        if (employee == null) {
//            throw new RuntimeException("Employee not found with id: " + employeeId);
//        }
//
//        // 2. Check role
//        String role = (String) employee.get("role");
//        if (!"EMPLOYEE".equalsIgnoreCase(role)) {
//            throw new RuntimeException("User is not authorized to approve. Role must be EMPLOYEE.");
//        }
//
//        // 3. Fetch loan and approve
//        Loan loan = loanRepository.findById(loanId)
//                .orElseThrow(() -> new RuntimeException("Loan not found with id: " + loanId));
//        loan.setApprovedByEmployeeId(employeeId);  // Add this field in Loan model
//        loan.setApproved(true);
//        return loanRepository.save(loan);
//    }

}
