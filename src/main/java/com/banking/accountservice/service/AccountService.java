package com.banking.accountservice.service;

import com.banking.accountservice.kafka.AccountApprovedEvent;
import com.banking.accountservice.kafka.AccountCreatedEvent;
import com.banking.accountservice.kafka.AccountEventProducer;
import com.banking.accountservice.model.Account;
import com.banking.accountservice.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;


import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountEventProducer accountEventProducer;

    @Autowired
    private RestTemplate restTemplate; // <-- RestTemplate injected

    private final String USER_SERVICE_URL = "http://localhost:8081/users"; // UserService base URL


//    public Account createAccount(Account account) {
//        // Validate user exists in UserService
//        String url = USER_SERVICE_URL + "/" + account.getUserId();
//        Object userResponse = restTemplate.getForObject(url, Object.class);
//        if (userResponse == null) {
//            throw new RuntimeException("User not found in UserService with id " + account.getUserId());
//        }
//
//        // Set default balance if null
//        account.setBalance(account.getBalance() != null ? account.getBalance() : 0.0);
//
//        // Generate unique account number if not already set
//        if (account.getAccountNumber() == null) {
//            String generatedAccountNumber = String.valueOf(ThreadLocalRandom.current().nextLong(1000000000L, 9999999999L));
//            account.setAccountNumber(generatedAccountNumber);
//        }
//
//        return accountRepository.save(account);
//    }

    // Create Account
    public Account createAccount(Account account) {
        // Validate user exists
        String url = USER_SERVICE_URL + "/" + account.getUserId();
        Object userResponse = restTemplate.getForObject(url, Object.class);
        if (userResponse == null) {
            throw new RuntimeException("User not found in UserService with id " + account.getUserId());
        }

        // Extract email from userResponse
        @SuppressWarnings("unchecked")
        String userEmail = ((Map<String, Object>) userResponse).get("email").toString();

        System.out.println("userEmail: " + userEmail);
        account.setBalance(account.getBalance() != null ? account.getBalance() : 0.0);


        // Generate account number
        if (account.getAccountNumber() == null) {
            account.setAccountNumber(String.valueOf(ThreadLocalRandom.current().nextLong(1000000000L, 9999999999L)));
        }

        // Set default approval status
        account.setApproved(false);
        account.setStatus("PENDING");
        account.setCreatedAt(LocalDateTime.now());
        System.out.println("Created account with id " + account.getAccountNumber());
        Account savedaccount = accountRepository.save(account);
        System.out.println("Saved account with id " + savedaccount.getId());
        System.out.println("Saved account with id " + savedaccount);
        // Send Kafka event to employee
        AccountCreatedEvent event = new AccountCreatedEvent(
                savedaccount.getUserId(),
                userEmail,
                savedaccount.getAccountNumber(),
                savedaccount.getId()
        );
        accountEventProducer.sendAccountCreatedEvent(event);

        return account;
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


//    public Account approveAccount(Long accountId, Long employeeId) {
//        // 1. Fetch employee details from User Service
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
//        // 3. Fetch account and approve
//        Account account = getAccount(accountId);
//        account.setApprovedByEmployeeId(employeeId);  // Add this field in Account model
//        account.setApproved(true);                    // Optional: boolean flag for approved
//        return accountRepository.save(account);
//    }
//    public Account approveAccount(Long accountId, Long employeeId) {
//        // Fetch employee details from User Service
//        String url = USER_SERVICE_URL + "/" + employeeId;
//        @SuppressWarnings("unchecked")
//        Map<String, Object> employee = restTemplate.getForObject(url, Map.class);
//        if (employee == null) {
//            throw new RuntimeException("Employee not found with id: " + employeeId);
//        }
//
//        String role = (String) employee.get("role");
//        if (!"EMPLOYEE".equalsIgnoreCase(role)) {
//            throw new RuntimeException("User is not authorized to approve. Role must be EMPLOYEE.");
//        }
//
//        Account account = accountRepository.findById(accountId)
//                .orElseThrow(() -> new RuntimeException("Account not found"));
//
//        account.setApproved(true);
//        account.setApprovedByEmployeeId(employeeId);
//        account.setStatus("APPROVED");
//
//        account = accountRepository.save(account);
//
//        // Send Kafka event to user
//        String userEmail = restTemplate.getForObject(USER_SERVICE_URL + "/" + account.getUserId() + "/email", String.class);
//
//        AccountApprovedEvent event = new AccountApprovedEvent(
//                account.getId(),
//                account.getUserId(),
//                userEmail,
//                employeeId
//        );
//        accountEventProducer.sendAccountApprovedEvent(event);
//
//        return account;
//    }
public Account approveAccount(Long accountId, Long employeeId) {
    // Fetch employee details from User Service
    String urlEmployee = USER_SERVICE_URL + "/" + employeeId;
    Object employeeResponse = restTemplate.getForObject(urlEmployee, Object.class);
    if (employeeResponse == null) {
        throw new RuntimeException("Employee not found with id: " + employeeId);
    }

    @SuppressWarnings("unchecked")
    Map<String, Object> employeeMap = (Map<String, Object>) employeeResponse;
    String employeeRole = employeeMap.get("role").toString();
    String employeeEmail = employeeMap.get("email").toString();
    String employeeName = employeeMap.get("name").toString();

    if (!"EMPLOYEE".equalsIgnoreCase(employeeRole)) {
        throw new RuntimeException("User is not authorized to approve. Role must be EMPLOYEE.");
    }

    Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found"));

    account.setApproved(true);
    account.setApprovedByEmployeeId(employeeId);
    account.setStatus("APPROVED");
    account.setApprovedAt(LocalDateTime.now());

    Account savedaccount = accountRepository.save(account);

    // Fetch user email for notification
    String urlUser = USER_SERVICE_URL + "/" + account.getUserId();
    Object userResponse = restTemplate.getForObject(urlUser, Object.class);
    @SuppressWarnings("unchecked")
    String userEmail = ((Map<String, Object>) userResponse).get("email").toString();

    // Send Kafka event to notify user
    AccountApprovedEvent userEvent = new AccountApprovedEvent(
            savedaccount.getId(),
            savedaccount.getUserId(),
            userEmail,
            savedaccount.getAccountNumber(),
            "Account approved by employeeId: " + employeeId,
            employeeId.toString(),
            employeeName
    );
    accountEventProducer.sendAccountApprovedEvent(userEvent);

    // Send Kafka event to notify employee
    AccountApprovedEvent employeeEvent = new AccountApprovedEvent(
            savedaccount.getId(),
            savedaccount.getUserId(),
            employeeEmail,
            savedaccount.getAccountNumber(),
            "You approved account for userId: " + account.getUserId(),
            employeeId.toString(),
            employeeName
    );
    accountEventProducer.sendAccountApprovedEvent(employeeEvent);

    return account;
}


}
