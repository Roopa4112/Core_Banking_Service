package com.banking.accountservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class LoanEventProducer {
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String LOAN_APPLIED_TOPIC = "loan-applied-topic";
    private static final String LOAN_APPROVED_TOPIC = "loan-approved-topic";

    public LoanEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendLoanAppliedEvent(LoanAppliedEvent event) {
        String json = null;
        try{
            json=objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        kafkaTemplate.send(LOAN_APPLIED_TOPIC, json);
    }

    public void sendLoanApprovedEvent(LoanApprovedEvent event) {
        String json = null;
        try{
            json=objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        kafkaTemplate.send(LOAN_APPROVED_TOPIC, json);
    }
}
