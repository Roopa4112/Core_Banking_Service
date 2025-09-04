package com.banking.accountservice.kafka;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoanApprovedEvent {
    private Long loanId;
    private Long userId;
    private String userEmail;
    private Double amount;
    private Long approvedByEmployeeId;
}
