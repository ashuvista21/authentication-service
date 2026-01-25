package com.user.auth.dtos;

import java.time.Instant;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailEvent {
	String eventId ;                // Unique ID (UUID)
    String eventType ;              // e.g., USER_REGISTRATION, PASSWORD_RESET
    String recipient ;              // Target email address
    String template ;
    Map<String, Object> variables ; // Template placeholders (e.g., {"userName": "Ashutosh"})
    Instant timestamp ;             // When the event was created
}
      
