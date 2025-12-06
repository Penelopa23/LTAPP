package org.example.dto;

import java.time.Instant;

/**
 * Response DTO for Kafka message statistics.
 * Provides metrics for load testing validation.
 */
public class KafkaStatsResponse {
    private Long totalSent;
    private Long totalConsumed;
    private Integer currentQueueSize;
    private Instant lastMessageTimestamp;

    public KafkaStatsResponse() {
    }

    public KafkaStatsResponse(Long totalSent, Long totalConsumed, Integer currentQueueSize, Instant lastMessageTimestamp) {
        this.totalSent = totalSent;
        this.totalConsumed = totalConsumed;
        this.currentQueueSize = currentQueueSize;
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    // Getters and Setters
    public Long getTotalSent() {
        return totalSent;
    }

    public void setTotalSent(Long totalSent) {
        this.totalSent = totalSent;
    }

    public Long getTotalConsumed() {
        return totalConsumed;
    }

    public void setTotalConsumed(Long totalConsumed) {
        this.totalConsumed = totalConsumed;
    }

    public Integer getCurrentQueueSize() {
        return currentQueueSize;
    }

    public void setCurrentQueueSize(Integer currentQueueSize) {
        this.currentQueueSize = currentQueueSize;
    }

    public Instant getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(Instant lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }
}

