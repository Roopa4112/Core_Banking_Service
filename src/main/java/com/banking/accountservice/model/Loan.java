package com.banking.accountservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

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
    private Integer period;

    // Total amount to pay = amount + interest
    private Double totalAmountToPay;

    // Getters and setters
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
}
