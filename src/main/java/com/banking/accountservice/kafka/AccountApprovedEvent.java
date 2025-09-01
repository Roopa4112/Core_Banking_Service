package com.banking.accountservice.kafka;

public class AccountApprovedEvent
{
    private Long accountId;
    private String email; // email instead of Long
private String accountNumber;
private String message; // message instead of Long
 public AccountApprovedEvent() {}
    public AccountApprovedEvent(Long accountId, String email, String accountNumber, String message)
    { this.accountId = accountId;
        this.email = email;
        this.accountNumber = accountNumber;
        this.message = message;
    }
// getters and setters
public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}