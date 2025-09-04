package com.banking.accountservice.kafka;

// AccountCreatedEvent.java

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountCreatedEvent {

    private Long userId;
    private String email;
    private String accountNumber;
    private Long accountId;


}
