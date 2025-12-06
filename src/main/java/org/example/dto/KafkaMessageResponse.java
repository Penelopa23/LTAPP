package org.example.dto;

import java.time.Instant;

/**
 * Response DTO for Kafka message operations.
 * Provides rich data for validation in load tests.
 */
public class KafkaMessageResponse {
    private String messageId;
    private String payload;
    private Integer payloadLength;
    private String status;
    private String topic;
    private Instant sentAt;

    public KafkaMessageResponse() {
    }

    public KafkaMessageResponse(String messageId, String payload, Integer payloadLength,
                               String status, String topic, Instant sentAt) {
        this.messageId = messageId;
        this.payload = payload;
        this.payloadLength = payloadLength;
        this.status = status;
        this.topic = topic;
        this.sentAt = sentAt;
    }

    // Getters and Setters
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Integer getPayloadLength() {
        return payloadLength;
    }

    public void setPayloadLength(Integer payloadLength) {
        this.payloadLength = payloadLength;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }
}

