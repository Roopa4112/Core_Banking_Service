package com.banking.accountservice.kafka;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransactionEvent {
    private Long transactionId;
    private Long accountId;
    private Long userId;
    private String userEmail;
    private String type;        // DEPOSIT, WITHDRAW, TRANSFER
    private Double amount;
    private String description;

}
