package com.banking.accountservice.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AccountEventProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private static final String ACCOUNT_CREATED_TOPIC = "account-created-topic";
    private static final String ACCOUNT_APPROVED_TOPIC = "account-approved-topic";

    public void sendAccountCreatedEvent(AccountCreatedEvent event) {
        kafkaTemplate.send(ACCOUNT_CREATED_TOPIC, event);
    }

    public void sendAccountApprovedEvent(AccountApprovedEvent event) {
        kafkaTemplate.send(ACCOUNT_APPROVED_TOPIC, event);
    }
}
