package org.example.dto;

import java.time.Instant;

/**
 * Lightweight DTO for Kafka message preview in datapools.
 */
public class KafkaMessagePreview {
    private String id;
    private String payloadPreview;
    private Instant createdAt;

    public KafkaMessagePreview() {
    }

    public KafkaMessagePreview(String id, String payloadPreview, Instant createdAt) {
        this.id = id;
        this.payloadPreview = payloadPreview;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPayloadPreview() {
        return payloadPreview;
    }

    public void setPayloadPreview(String payloadPreview) {
        this.payloadPreview = payloadPreview;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}

