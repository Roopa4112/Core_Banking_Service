package com.banking.accountservice.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class LoanEventProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private static final String LOAN_APPLIED_TOPIC = "loan-applied-topic";
    private static final String LOAN_APPROVED_TOPIC = "loan-approved-topic";

    public void sendLoanAppliedEvent(LoanAppliedEvent event) {
        kafkaTemplate.send(LOAN_APPLIED_TOPIC, event);
    }

    public void sendLoanApprovedEvent(LoanApprovedEvent event) {
        kafkaTemplate.send(LOAN_APPROVED_TOPIC, event);
    }
}
