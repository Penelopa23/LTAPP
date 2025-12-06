package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for sending Kafka messages via POST with JSON payload.
 */
public class SendMessageRequest {
    @NotBlank(message = "Message payload is required")
    @Size(max = 10000, message = "Message payload must not exceed 10000 characters")
    private String payload;

    public SendMessageRequest() {
    }

    public SendMessageRequest(String payload) {
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}

