package com.banking.accountservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "loans")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loanType;   // HOME, CAR, PERSONAL
    private Double amount;
    private Double interestRate;

    // Many Loans → One Account
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    @JsonIgnoreProperties({"loans", "transactions"}) // Ignore loans and transactions in Account to prevent recursion
    private Account account;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status = "PENDING";

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public Double getTotalAmountToPay() {
        return totalAmountToPay;
    }

    public void setTotalAmountToPay(Double totalAmountToPay) {
        this.totalAmountToPay = totalAmountToPay;
    }

    // Loan period in months
    @Column(name = "term_month")
    @JsonProperty("term_month")
    private Integer period;

    // Total amount to pay = amount + interest
    private Double totalAmountToPay;

    // ✅ dates
    @Column(name = "applied_date")
    private LocalDateTime appliedDate;

    @Column(name = "approved_date")
    private LocalDateTime approvedDate;

    // Getters and setters


    public LocalDateTime getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(LocalDateTime appliedDate) {
        this.appliedDate = appliedDate;
    }

    public LocalDateTime getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(LocalDateTime approvedDate) {
        this.approvedDate = approvedDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLoanType() { return loanType; }
    public void setLoanType(String loanType) { this.loanType = loanType; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public Double getInterestRate() { return interestRate; }
    public void setInterestRate(Double interestRate) { this.interestRate = interestRate; }

    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }

    // ===== Employee approval fields =====

    private Long approvedByEmployeeId;
    private boolean approved = false;

    // Getters and setters for approval
    public Long getApprovedByEmployeeId() { return approvedByEmployeeId; }
    public void setApprovedByEmployeeId(Long approvedByEmployeeId) { this.approvedByEmployeeId = approvedByEmployeeId; }

    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }

}
