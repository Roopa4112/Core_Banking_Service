package com.banking.accountservice.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransactionEventProducer {

    @Autowired
    private KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    private static final String TOPIC = "transaction-topic";

    public void sendTransactionEvent(TransactionEvent event) {
        kafkaTemplate.send(TOPIC, event);
    }
}
