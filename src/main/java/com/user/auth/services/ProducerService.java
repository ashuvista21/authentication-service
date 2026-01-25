package com.user.auth.services;

import java.util.concurrent.CompletableFuture;
import org.springframework.kafka.support.SendResult;

import com.user.auth.dtos.EmailEvent;

public interface ProducerService {

	CompletableFuture<SendResult<String, Object>> send(EmailEvent emailEvent) ;

}
