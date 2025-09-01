package com.banking.accountservice.kafka;

public class LoanAppliedEvent {
    private Long loanId;
    private Long userId;
    private String userEmail;
    private Double amount;

    public LoanAppliedEvent() {}

    public LoanAppliedEvent(Long loanId, Long userId, String userEmail, Double amount) {
        this.loanId = loanId;
        this.userId = userId;
        this.userEmail = userEmail;
        this.amount = amount;
    }

    public Long getLoanId() { return loanId; }
    public void setLoanId(Long loanId) { this.loanId = loanId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}
