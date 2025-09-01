package com.banking.accountservice.kafka;

// AccountCreatedEvent.java

public class AccountCreatedEvent {

    private Long userId;
    private String email;
    private String accountNumber;
    private Long employeeId; // if employee approved

    public AccountCreatedEvent() {
    }

    public AccountCreatedEvent(Long userId, String email, String accountNumber) {
        this.userId = userId;
        this.email = email;
        this.accountNumber = accountNumber;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

}
