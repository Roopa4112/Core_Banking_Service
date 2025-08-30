package com.banking.accountservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionType; // DEPOSIT, WITHDRAW
    private Double amount;
    private Date date;

    // Many Transactions → One Account
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }
}
