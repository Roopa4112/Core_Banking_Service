package com.banking.accountservice.kafka;

public class TransactionEvent {
    private Long transactionId;
    private Long accountId;
    private Long userId;
    private String userEmail;
    private String type;        // DEPOSIT, WITHDRAW, TRANSFER
    private Double amount;
    private String description;

    // constructor
    public TransactionEvent(Long transactionId, Long accountId, Long userId,
                            String userEmail, String type, Double amount, String description) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.userId = userId;
        this.userEmail = userEmail;
        this.type = type;
        this.amount = amount;
        this.description = description;
    }

    // getters + setters ...


    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
