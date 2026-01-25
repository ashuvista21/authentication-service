package com.user.auth.services.impl;

import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.user.auth.dtos.EmailEvent;
import com.user.auth.services.ProducerService;

@Service
public class ProducerServiceImpl implements ProducerService {
	
	private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic = "email-topics";

    public ProducerServiceImpl(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
	
	@Override
	public CompletableFuture<SendResult<String, Object>> send(EmailEvent emailEvent) {
		
		CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, 0, emailEvent.getEventId(), emailEvent);

        // Add callbacks using CompletableFuture API
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                RecordMetadata metadata = result.getRecordMetadata();
                System.out.printf("✅ Sent message to topic %s partition %d offset %d%n",
                        metadata.topic(), metadata.partition(), metadata.offset());
            } else {
                System.err.printf("❌ Failed to send message %s: %s%n", emailEvent.getEventId(), ex.getMessage());
            }
        });

        return future;
	}

}
