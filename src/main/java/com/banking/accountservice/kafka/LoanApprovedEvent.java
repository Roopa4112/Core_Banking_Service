package com.banking.accountservice.kafka;

public class LoanApprovedEvent {
    private Long loanId;
    private Long userId;
    private String userEmail;
    private Double amount;
    private Long approvedByEmployeeId;

    public LoanApprovedEvent() {}

    public LoanApprovedEvent(Long loanId, Long userId, String userEmail, Double amount, Long approvedByEmployeeId) {
        this.loanId = loanId;
        this.userId = userId;
        this.userEmail = userEmail;
        this.amount = amount;
        this.approvedByEmployeeId = approvedByEmployeeId;
    }

    public Long getLoanId() { return loanId; }
    public void setLoanId(Long loanId) { this.loanId = loanId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public Long getApprovedByEmployeeId() { return approvedByEmployeeId; }
    public void setApprovedByEmployeeId(Long approvedByEmployeeId) { this.approvedByEmployeeId = approvedByEmployeeId; }
}
