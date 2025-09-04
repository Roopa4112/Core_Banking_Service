package com.banking.accountservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AccountEventProducer {
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String ACCOUNT_CREATED_TOPIC = "account-created-topic";
    private static final String ACCOUNT_APPROVED_TOPIC = "account-approved-topic";

    public AccountEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAccountCreatedEvent(AccountCreatedEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(ACCOUNT_CREATED_TOPIC, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing AccountCreatedEvent", e);
        }
    }

    public void sendAccountApprovedEvent(AccountApprovedEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(ACCOUNT_APPROVED_TOPIC, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing AccountApprovedEvent", e);
        }
    }
}
