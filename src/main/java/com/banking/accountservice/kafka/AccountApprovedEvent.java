    package com.banking.accountservice.kafka;

    import lombok.*;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public class AccountApprovedEvent
    {
        private Long accountId;
        private Long userId;
        private String email;
        private String accountNumber;
        private String employeeId;
        private String accountType; // ✅ Add this
        private String message;

    }